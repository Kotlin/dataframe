package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet

/**
 * The `DbType` class represents a database type used for reading dataframe from the database.
 *
 * @property [dbTypeInJdbcUrl] The name of the database as specified in the JDBC URL.
 */
public sealed class DbType(public val dbTypeInJdbcUrl: String) {
    /**
     * Converts the data from the given [ResultSet] into the specified [TableColumnMetadata] type.
     *
     * @param rs The [ResultSet] containing the data to be converted.
     * @param tableColumn The [TableColumnMetadata] representing the target type of the conversion.
     * @return The converted data as an instance of [Any].
     */
    public abstract fun convertDataFromResultSet(rs: ResultSet, tableColumn: TableColumnMetadata): Any?
    public abstract fun toColumnSchema(tableColumn: TableColumnMetadata): ColumnSchema
}
