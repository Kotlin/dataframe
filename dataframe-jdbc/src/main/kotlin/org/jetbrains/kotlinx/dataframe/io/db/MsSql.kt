package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Represents the MariaDb database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MariaDb,
 * and to generate the corresponding column schema.
 */
public object MsSql : DbType("sqlserver") {
    override val driverClassName: String
        get() = "com.microsoft.sqlserver.jdbc.SQLServerDriver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        return null
    }

    // TODO: need to find solution to filter system tables
    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return MySql.isSystemTable(tableMetadata)
    }

    // TODO: need to check
    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat")
        )
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        return null
    }

    public override fun sqlQueryLimit(sqlQuery: String, limit: Int): String {
        sqlQuery.replace("SELECT", "SELECT TOP $limit", ignoreCase = true)
        return sqlQuery
    }
}
