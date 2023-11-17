package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Represents the H2 database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for H2,
 * and to generate the corresponding column schema.
 *
 * NOTE: All date and timestamp related types are converted to String to avoid java.sql.* types.
 */
public object H2 : DbType("h2") {
    override val driverClassName: String
        get() = "org.h2.Driver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        return null
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return tableMetadata.name.lowercase(Locale.getDefault()).contains("sys_")
            || tableMetadata.schemaName?.lowercase(Locale.getDefault())?.contains("information_schema") ?: false
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"))
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        return null
    }
}
