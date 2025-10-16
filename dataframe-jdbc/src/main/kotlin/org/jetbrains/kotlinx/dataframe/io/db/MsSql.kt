package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.db.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import kotlin.reflect.KType

/**
 * Represents the MSSQL database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MSSQL,
 * and to generate the corresponding column schema.
 */
public object MsSql : DbType("sqlserver") {
    override val driverClassName: String
        get() = "com.microsoft.sqlserver.jdbc.SQLServerDriver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? = null

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val locale = Locale.getDefault()

        fun String?.containsWithLowercase(substr: String) = this?.lowercase(locale)?.contains(substr) == true

        val schemaName = tableMetadata.schemaName
        val tableName = tableMetadata.name
        val catalogName = tableMetadata.catalogue

        return schemaName.containsWithLowercase("sys") ||
            schemaName.containsWithLowercase("information_schema") ||
            tableName.startsWith("sys") ||
            tableName.startsWith("dt") ||
            tableName.containsWithLowercase("sys_config") ||
            catalogName.containsWithLowercase("system") ||
            catalogName.containsWithLowercase("master") ||
            catalogName.containsWithLowercase("model") ||
            catalogName.containsWithLowercase("msdb") ||
            catalogName.containsWithLowercase("tempdb")
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"),
        )

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? = null

    public override fun buildSqlQueryWithLimit(sqlQuery: String, limit: Int): String =
        sqlQuery.replace("SELECT", "SELECT TOP $limit", ignoreCase = true)

    override fun quoteIdentifier(name: String): String {
        // schema.table -> [schema].[table]
        return name.split(".").joinToString(".") { "[$it]" }
    }
}
