package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.JdbcColumn
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.typeOf

public object Sqlite : DbType("sqlite") {
    override fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any? {
        val name = jdbcColumn.name
        return when (jdbcColumn.sqlType) {
            "INTEGER", "INTEGER AUTO_INCREMENT" -> rs.getInt(name)
            "TEXT" -> rs.getString(name)
            "REAL" -> rs.getDouble(name)
            "NUMERIC" -> rs.getDouble(name)
            "BLOB" -> rs.getBytes(name)
            else -> throw IllegalArgumentException("Unsupported SQLite type: ${jdbcColumn.sqlType}")
        }
    }

    override fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema {
        return when (jdbcColumn.sqlType) {
            "INTEGER", "INTEGER AUTO_INCREMENT" -> ColumnSchema.Value(typeOf<Int>())
            "TEXT" -> ColumnSchema.Value(typeOf<String>())
            "REAL" -> ColumnSchema.Value(typeOf<Double>())
            "NUMERIC" -> ColumnSchema.Value(typeOf<Double>())
            "BLOB" -> ColumnSchema.Value(typeOf<ByteArray>())
            else -> throw IllegalArgumentException("Unsupported SQLite type: ${jdbcColumn.sqlType}")
        }
    }
}
