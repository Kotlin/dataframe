package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import org.jetbrains.kotlinx.dataframe.io.TableMetadata

/**
 * The `DbType` class represents a database type used for reading dataframe from the database.
 *
 * @property [dbTypeInJdbcUrl] The name of the database as specified in the JDBC URL.
 */
public abstract class DbType(public val dbTypeInJdbcUrl: String) {
    /**
     * Converts the data from the given [ResultSet] into the specified [TableColumnMetadata] type.
     *
     * @param rs The [ResultSet] containing the data to be converted.
     * @param tableColumnMetadata The [TableColumnMetadata] representing the target type of the conversion.
     * @return The converted data as an instance of [Any].
     */
    public abstract fun convertDataFromResultSet(rs: ResultSet, tableColumnMetadata: TableColumnMetadata): Any?

    /**
     * Returns a [ColumnSchema] produced from [tableColumnMetadata].
     */
    public abstract fun toColumnSchema(tableColumnMetadata: TableColumnMetadata): ColumnSchema

    /**
     * Checks if the given table name is a system table for the specified database type.
     *
     * @param [tableMetadata] the table object representing the table from the database.
     * @param [dbType] the database type to check against.
     * @return True if the table is a system table for the specified database type, false otherwise.
     */
    public abstract fun isSystemTable(tableMetadata: TableMetadata): Boolean
}
