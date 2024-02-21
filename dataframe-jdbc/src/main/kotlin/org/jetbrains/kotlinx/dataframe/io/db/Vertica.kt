package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Represents the MySql database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MySql,
 * and to generate the corresponding column schema.
 */
public object Vertica : DbType("vertica") {
    override val driverClassName: String
        get() = "com.vertica.jdbc.Driver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
//        when(tableColumnMetadata.sqlTypeName) {
//            "FLOAT8" -> Double::class.createType(nullable = tableColumnMetadata.isNullable)
//        }
        return null
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val schemaName = tableMetadata.schemaName

        return schemaName?.startsWith("v_", true) ?: false
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"))
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
//        if(tableColumnMetadata.sqlTypeName == "INT UNSIGNED")
//            return Long::class.createType(nullable = tableColumnMetadata.isNullable)
        return null
    }
}
