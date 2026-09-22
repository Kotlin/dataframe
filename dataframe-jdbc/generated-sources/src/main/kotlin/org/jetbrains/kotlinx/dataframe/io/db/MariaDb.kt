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

    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        // Force BIGINT to always be Long, regardless of javaClassName
        // MariaDB JDBC driver may report Integer for small BIGINT values
        // TODO: investigate the corner case

        // if (tableColumnMetadata.jdbcType == java.sql.Types.BIGINT) {
        //    val kType = Long::class.createType(nullable = tableColumnMetadata.isNullable)
        //    return ColumnSchema.Value(kType)
        // }

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

        // A multi-bit BIT(M) column comes back as a `byte[]`, see [isMultiBit]
        if (tableColumnMetadata.isMultiBit) {
            return typeOf<ByteArray>().withNullability(tableColumnMetadata.isNullable)
        }

        // For TINYBLOB/BLOB/MEDIUMBLOB/LONGBLOB columns the MariaDB driver reports
        // `java.sql.Blob` from `ResultSetMetaData.getColumnClassName`, while `ResultSet.getObject`
        // actually returns a `byte[]`, so the column has to be typed as `ByteArray` (see #2087).
        // For those columns the driver reports VARBINARY/LONGVARBINARY as the JDBC type, never BLOB;
        // the `Types.BLOB` check keeps H2 in MariaDB mode working — it delegates here,
        // reports `Types.BLOB` and does return real `java.sql.Blob` values.
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
