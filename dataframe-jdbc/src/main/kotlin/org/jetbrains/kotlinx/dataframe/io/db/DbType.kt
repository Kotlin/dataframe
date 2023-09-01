package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.JdbcColumn
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet

// TODO: lools like we need here more then enum, but hierarchy of sealed classes with some fields
// Basic Type: supported database with mapping of types and jdbcProtocol names
public sealed class DbType(public val jdbcUrlDatabaseName: String) {
    public abstract fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any?
    public abstract fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema
}
