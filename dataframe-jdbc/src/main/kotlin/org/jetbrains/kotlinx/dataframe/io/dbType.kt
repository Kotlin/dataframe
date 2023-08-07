package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.typeOf

// TODO: lools like we need here more then enum, but hierarchy of sealed classes with some fields
// Basic Type: supported database with mapping of types and jdbcProtocol names
public sealed class DbType(public val jdbcName: String) {
    public abstract fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any?
    public abstract fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema
}

public object H2 : DbType("h2") {
    override fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any? {
        return when (jdbcColumn.type) {
            "INT" -> rs.getInt(jdbcColumn.name)
            "CHARACTER VARYING" -> rs.getString(jdbcColumn.name)
            "REAL", "NUMERIC" -> rs.getFloat(jdbcColumn.name)
            "MEDIUMTEXT" -> rs.getString(jdbcColumn.name)
            else -> null
        }
    }

    override fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema {
        return when (jdbcColumn.type) {
            "INT" -> ColumnSchema.Value(typeOf<Int>())
            "CHARACTER VARYING" -> ColumnSchema.Value(typeOf<String>())
            "REAL", "NUMERIC" -> ColumnSchema.Value(typeOf<Float>())
            "MEDIUMTEXT" -> ColumnSchema.Value(typeOf<String>())
            else -> ColumnSchema.Value(typeOf<Any>())
        }
    }
}

public object MariaDb : DbType("mariadb") {
    override fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any? {
        return when (jdbcColumn.type) {
            "INTEGER", "INT" -> rs.getInt(jdbcColumn.name)
            "VARCHAR", "CHAR" -> rs.getString(jdbcColumn.name)
            "FLOAT" -> rs.getFloat(jdbcColumn.name)
            "MEDIUMTEXT" -> rs.getString(jdbcColumn.name)
            else -> null
        }
    }

    override fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema {
        return when (jdbcColumn.type) {
            "INTEGER", "INT" -> ColumnSchema.Value(typeOf<Int>())
            "VARCHAR", "CHAR" -> ColumnSchema.Value(typeOf<String>())
            "FLOAT" -> ColumnSchema.Value(typeOf<Float>())
            "MEDIUMTEXT" -> ColumnSchema.Value(typeOf<String>())
            else -> ColumnSchema.Value(typeOf<Any>())
        }
    }
}

public object MySql : DbType("mysql") {
    override fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any? {
        return when (jdbcColumn.type) {
            "INTEGER" -> rs.getInt(jdbcColumn.name)
            "VARCHAR" -> rs.getString(jdbcColumn.name)
            "FLOAT" -> rs.getFloat(jdbcColumn.name)
            "MEDIUMTEXT" -> rs.getString(jdbcColumn.name)
            else -> null
        }
    }

    override fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema {
        return when (jdbcColumn.type) {
            "INTEGER" -> ColumnSchema.Value(typeOf<Int>())
            "VARCHAR" -> ColumnSchema.Value(typeOf<String>())
            "FLOAT" -> ColumnSchema.Value(typeOf<Float>())
            "MEDIUMTEXT" -> ColumnSchema.Value(typeOf<String>())
            else -> ColumnSchema.Value(typeOf<Any>())
        }
    }
}

public fun extractDBTypeFromURL(url: String?): DbType {
    if (url != null) {
        return when {
            H2.jdbcName in url -> H2
            MariaDb.jdbcName in url -> MariaDb
            MySql.jdbcName in url -> MySql
            else -> MariaDb // probably better to add default SQL databases without vendor name
        }
    } else {
        throw RuntimeException("Database URL could not be null. The existing value is $url")
    }
}
