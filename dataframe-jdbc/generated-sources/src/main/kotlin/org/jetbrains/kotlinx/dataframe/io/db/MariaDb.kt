package org.jetbrains.kotlinx.dataframe.io.db

import java.math.BigInteger
import java.sql.ResultSet
import java.sql.Types
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Represents the MariaDb database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MariaDb,
 * and to generate the corresponding column schema.
 */
public object MariaDb : DbType("mariadb") {
    override val driverClassName: String
        get() = "org.mariadb.jdbc.Driver"

    /**
     * MariaDB-specific deviations from the default type mapping:
     *
     * - `INT UNSIGNED` does not fit an [<code>Int</code>][Int] (`0 .. 2^32 - 1`), so it is read as [<code>Long</code>][Long], and
     *   `BIGINT UNSIGNED` does not fit a [<code>Long</code>][Long] (`0 .. 2^64 - 1`), so it is read as
     *   [<code>java.math.BigInteger</code>][java.math.BigInteger]. The driver returns exactly those types for them.
     * - `SMALLINT` is read as [<code>Short</code>][Short], which is what the driver reports and returns for it.
     * - A multi-bit `BIT(M)` column is read as [<code>ByteArray</code>][ByteArray], because that is what the driver returns
     *   for it while reporting `byte[]` — the source-code spelling, not the `"[B"` JVM binary name
     *   the default mapping looks for — as the column class. See [<code>isMultiBit</code>][isMultiBit] and #2087.
     * - `TINYBLOB`/`BLOB`/`MEDIUMBLOB`/`LONGBLOB` are read as [<code>ByteArray</code>][ByteArray]: the driver reports
     *   `java.sql.Blob` as their column class while [<code>ResultSet.getObject</code>][ResultSet.getObject] returns a `byte[]` (#2087).
     *   For those columns the driver reports `VARBINARY`/`LONGVARBINARY` as the JDBC type, never
     *   [<code>java.sql.Types.BLOB</code>][java.sql.Types.BLOB], and that is what the condition keys on — so H2 in MariaDB mode, which
     *   delegates its mapping here, reports [<code>java.sql.Types.BLOB</code>][java.sql.Types.BLOB] and does return real
     *   [<code>java.sql.Blob</code>][java.sql.Blob] values, keeps mapping to [<code>java.sql.Blob</code>][java.sql.Blob].
     *
     * Everything else falls through to [<code>DbType.getExpectedJdbcType</code>][DbType.getExpectedJdbcType].
     *
     * The driver is also known to report `java.lang.Integer` for a `BIGINT` column whose stored
     * values happen to fit in an [<code>Int</code>][Int]; that case is not handled here. The SQLite analogue of it is
     * (see `Sqlite.generateConverter`), so the fix pattern exists if this turns out to bite.
     */
    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        if (tableColumnMetadata.sqlTypeName == "INTEGER UNSIGNED" ||
            tableColumnMetadata.sqlTypeName == "INT UNSIGNED"
        ) {
            return typeOf<Long>().withNullability(tableColumnMetadata.isNullable)
        }

        if (tableColumnMetadata.sqlTypeName == "SMALLINT" && tableColumnMetadata.javaClassName == "java.lang.Short") {
            return typeOf<Short>().withNullability(tableColumnMetadata.isNullable)
        }
        if (tableColumnMetadata.sqlTypeName == "BIGINT UNSIGNED") {
            return typeOf<BigInteger>().withNullability(tableColumnMetadata.isNullable)
        }

        // a multi-bit BIT(M) column comes back as a byte[], see isMultiBit
        if (tableColumnMetadata.isMultiBit) {
            return typeOf<ByteArray>().withNullability(tableColumnMetadata.isNullable)
        }

        // a blob column comes back as a byte[] despite being reported as java.sql.Blob; the
        // jdbcType check keeps H2 in MariaDB mode on java.sql.Blob. See the KDoc above.
        if (tableColumnMetadata.javaClassName == "java.sql.Blob" &&
            tableColumnMetadata.jdbcType != Types.BLOB
        ) {
            return typeOf<ByteArray>().withNullability(tableColumnMetadata.isNullable)
        }

        return super.getExpectedJdbcType(tableColumnMetadata)
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean = MySql.isSystemTable(tableMetadata)

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"),
        )

    override fun quoteIdentifier(name: String): String {
        // schema.table -> `schema`.`table`
        return name.split(".").joinToString(".") { "`$it`" }
    }
}
