package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Represents the PostgreSql database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for PostgreSql,
 * and to generate the corresponding column schema.
 */
public object PostgreSql : DbType("postgresql") {
    override val driverClassName: String
        get() = "org.postgresql.Driver"

    override fun generateTypeInformation(tableColumnMetadata: TableColumnMetadata): AnyDbColumnTypeInformation {
        // because of https://github.com/pgjdbc/pgjdbc/issues/425
        if (tableColumnMetadata.sqlTypeName == "money") {
            val kType = typeOf<String>().withNullability(tableColumnMetadata.isNullable)
            return dbColumnTypeInformation<String?>(targetSchema = ColumnSchema.Value(kType))
        }
        return super.generateTypeInformation(tableColumnMetadata)
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean =
        tableMetadata.name.lowercase(Locale.getDefault()).contains("pg_") ||
            tableMetadata.schemaName?.lowercase(Locale.getDefault())?.contains("pg_catalog.") ?: false

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"),
        )

    override fun quoteIdentifier(name: String): String {
        // schema.table -> "schema"."table"
        return name.split(".").joinToString(".") { "\"$it\"" }
    }
}
