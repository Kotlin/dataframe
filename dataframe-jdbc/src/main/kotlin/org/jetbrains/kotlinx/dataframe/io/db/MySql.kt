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

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        if(tableColumnMetadata.jdbcType == Types.TINYINT) // because of https://github.com/pgjdbc/pgjdbc/blob/246b759cdc264c2732717dbd6ff9f8f472024196/pgjdbc/src/main/java/org/postgresql/jdbc/PgResultSet.java#L182
            return ColumnSchema.Value(typeOf<Int>())
        if(tableColumnMetadata.jdbcType == Types.SMALLINT) // because of https://github.com/pgjdbc/pgjdbc/blob/246b759cdc264c2732717dbd6ff9f8f472024196/pgjdbc/src/main/java/org/postgresql/jdbc/PgResultSet.java#L182
            return ColumnSchema.Value(typeOf<Int>())
        return null
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val locale = Locale.getDefault()

        fun String?.containsWithLowercase(substr: String) = this?.lowercase(locale)?.contains(substr) == true

        val schemaName = tableMetadata.schemaName
        val name = tableMetadata.name

        return schemaName.containsWithLowercase("information_schema")
            || tableMetadata.catalogue.containsWithLowercase("performance_schema")
            || tableMetadata.catalogue.containsWithLowercase("mysql")
            || schemaName?.contains("mysql.") == true
            || name.contains("mysql.")
            || name.contains("sys_config")
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"))
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        if(tableColumnMetadata.jdbcType == Types.TINYINT)
            return typeOf<Int>()
        if(tableColumnMetadata.jdbcType == Types.SMALLINT)
            return typeOf<Int>()
        return null
    }
}
