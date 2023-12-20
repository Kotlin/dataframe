package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.sql.Types
import java.util.Locale
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import kotlin.reflect.KType
import kotlin.reflect.full.createType
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

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        // TODO: could be a wrapper of convertSqlTypeToKType
        if (tableColumnMetadata.sqlTypeName == "money") // because of https://github.com/pgjdbc/pgjdbc/issues/425
        {
            val kType = String::class.createType(nullable = tableColumnMetadata.isNullable)
            return ColumnSchema.Value(kType)
        }
        return null
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return tableMetadata.name.lowercase(Locale.getDefault()).contains("pg_")
            || tableMetadata.schemaName?.lowercase(Locale.getDefault())?.contains("pg_catalog.") ?: false
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"))
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        if(tableColumnMetadata.sqlTypeName == "money") // because of https://github.com/pgjdbc/pgjdbc/issues/425
            return String::class.createType(nullable = tableColumnMetadata.isNullable)
        return null
    }
}
