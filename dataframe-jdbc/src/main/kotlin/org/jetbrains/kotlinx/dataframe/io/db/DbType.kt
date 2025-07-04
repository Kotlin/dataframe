package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import org.jetbrains.kotlinx.dataframe.io.getSchemaForAllSqlTables
import org.jetbrains.kotlinx.dataframe.io.readAllSqlTables
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import kotlin.reflect.KType

/**
 * The `DbType` class represents a database type used for reading dataframe from the database.
 *
 * @property [dbTypeInJdbcUrl] The name of the database as specified in the JDBC URL.
 */
public abstract class DbType(public val dbTypeInJdbcUrl: String) {
    /**
     * Represents the JDBC driver class name for a given database type.
     *
     * NOTE: It's important for usage in dataframe-gradle-plugin for force class loading.
     *
     * @return The JDBC driver class name as a [String].
     */
    public abstract val driverClassName: String

    /**
     * The table type(s) (`TABLE_TYPE`) of ordinary tables in the SQL database, used by
     * [getSchemaForAllSqlTables], and [readAllSqlTables] as a filter when querying the database
     * for all the tables it has using [DatabaseMetaData.getTables].
     *
     * This is usually "TABLE" or "BASE TABLE", which is what [tableTypes] is set to by default,
     * but it can be overridden to any custom list of table types, or `null` to let the JDBC integration
     * return all types of tables.
     *
     * See [DatabaseMetaData.getTableTypes] for all supported table types of your specific database.
     */
    public open val tableTypes: List<String>? = listOf("TABLE", "BASE TABLE")

    /**
     * Returns a [ColumnSchema] produced from [tableColumnMetadata].
     */
    public abstract fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema?

    /**
     * Checks if the given table name is a system table for the specified database type.
     *
     * @param [tableMetadata] the table object representing the table from the database.
     * @param [dbType] the database type to check against.
     * @return True if the table is a system table for the specified database type, false otherwise.
     */
    public abstract fun isSystemTable(tableMetadata: TableMetadata): Boolean

    /**
     * Builds the table metadata based on the database type and the ResultSet from the query.
     *
     * @param [tables] the ResultSet containing the table's meta-information.
     * @return the TableMetadata object representing the table metadata.
     */
    public abstract fun buildTableMetadata(tables: ResultSet): TableMetadata

    /**
     * Converts SQL data type to a Kotlin data type.
     *
     * @param [tableColumnMetadata] The metadata of the table column.
     * @return The corresponding Kotlin data type, or null if no mapping is found.
     */
    public abstract fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType?

    /**
     * Constructs a SQL query with a limit clause.
     *
     * @param sqlQuery The original SQL query.
     * @param limit The maximum number of rows to retrieve from the query. Default is 1.
     * @return A new SQL query with the limit clause added.
     */
    public open fun sqlQueryLimit(sqlQuery: String, limit: Int = 1): String = "$sqlQuery LIMIT $limit"
}
