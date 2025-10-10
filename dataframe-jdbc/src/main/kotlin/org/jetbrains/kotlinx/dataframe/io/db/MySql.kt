package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.db.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Represents the MySql database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MySql,
 * and to generate the corresponding column schema.
 */
public object MySql : DbType("mysql") {
    override val driverClassName: String
        get() = "com.mysql.jdbc.Driver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        if (tableColumnMetadata.sqlTypeName == "INT UNSIGNED") {
            val kType = Long::class.createType(nullable = tableColumnMetadata.isNullable)
            return ColumnSchema.Value(kType)
        }
        return null
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

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        if (tableColumnMetadata.sqlTypeName == "INT UNSIGNED") {
            return Long::class.createType(nullable = tableColumnMetadata.isNullable)
        }
        return null
    }

    override fun quoteIdentifier(name: String): String {
        // schema.table -> `schema`.`table`
        return name.split(".").joinToString(".") { "`$it`" }
    }
}
