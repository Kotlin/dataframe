package org.jetbrains.kotlinx.dataframe.io.db

import java.math.BigInteger
import java.sql.ResultSet
import java.sql.Types
import java.util.Locale
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Represents the MySql database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MySql,
 * and to generate the corresponding column schema.
 */
public object MySql : DbType("mysql") {
    override val driverClassName: String
        get() = "com.mysql.jdbc.Driver"

    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        if (tableColumnMetadata.sqlTypeName == "INT UNSIGNED") {
            return typeOf<Long>().withNullability(tableColumnMetadata.isNullable)
        }
        if (tableColumnMetadata.sqlTypeName == "BIGINT UNSIGNED") {
            return typeOf<BigInteger>().withNullability(tableColumnMetadata.isNullable)
        }
        if (tableColumnMetadata.isMultiBit) {
            return typeOf<ByteArray>().withNullability(tableColumnMetadata.isNullable)
        }
        return super.getExpectedJdbcType(tableColumnMetadata)
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val locale = Locale.getDefault()

        fun String?.containsWithLowercase(substr: String) = this?.lowercase(locale)?.contains(substr) == true

        val schemaName = tableMetadata.schemaName
        val name = tableMetadata.name

        return schemaName.containsWithLowercase("information_schema") ||
            tableMetadata.catalogue.containsWithLowercase("performance_schema") ||
            tableMetadata.catalogue.containsWithLowercase("mysql") ||
            schemaName?.contains("mysql.") == true ||
            name.contains("mysql.") ||
            name.contains("sys_config")
    }

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

/**
 * `true` for a multi-bit `BIT(M)` column, `M > 1`, in MySQL and MariaDB.
 *
 * Both drivers return a `byte[]` for such a column, but neither reports a column class the default
 * mapping recognises as one: MySQL reports `java.lang.Boolean`, and MariaDB reports `byte[]` — the
 * source-code spelling, not the `"[B"` JVM binary name the default mapping looks for. So both used to
 * fall through to the [java.sql.Types.BIT] default, [Boolean], which does not match the values
 * (see #2087). A single-bit column really is a [Boolean] and is left alone; the declared column
 * width is the only thing in the metadata that tells the two apart.
 */
internal val TableColumnMetadata.isMultiBit: Boolean
    get() = jdbcType == Types.BIT && size > 1
