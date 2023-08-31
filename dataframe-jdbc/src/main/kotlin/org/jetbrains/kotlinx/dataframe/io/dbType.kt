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


/**
 * NOTE: all date and timestamp related types converted to String to avoid java.sql.* types
 */
public object PostgreSql : DbType("postgresql") {
    override fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any? {
        return when (jdbcColumn.type) {
            "serial" -> rs.getInt(jdbcColumn.name)
            "int8", "bigint", "bigserial" -> rs.getLong(jdbcColumn.name)
            "bool" -> rs.getBoolean(jdbcColumn.name)
            "box" -> rs.getString(jdbcColumn.name)
            "bytea" -> rs.getBytes(jdbcColumn.name)
            "character", "bpchar" -> rs.getString(jdbcColumn.name)
            "circle" -> rs.getString(jdbcColumn.name)
            "date" -> rs.getDate(jdbcColumn.name).toString()
            "float8", "double precision" -> rs.getDouble(jdbcColumn.name)
            "int4", "integer" -> rs.getInt(jdbcColumn.name)
            "interval" -> rs.getString(jdbcColumn.name)
            "json", "jsonb" -> rs.getString(jdbcColumn.name)
            "line" -> rs.getString(jdbcColumn.name)
            "lseg" -> rs.getString(jdbcColumn.name)
            "macaddr" -> rs.getString(jdbcColumn.name)
            "money" -> rs.getString(jdbcColumn.name)
            "numeric" -> rs.getString(jdbcColumn.name)
            "path" -> rs.getString(jdbcColumn.name)
            "point" -> rs.getString(jdbcColumn.name)
            "polygon" -> rs.getString(jdbcColumn.name)
            "float4", "real" -> rs.getFloat(jdbcColumn.name)
            "int2", "smallint" -> rs.getShort(jdbcColumn.name)
            "smallserial" -> rs.getInt(jdbcColumn.name)
            "text" -> rs.getString(jdbcColumn.name)
            "time" -> rs.getString(jdbcColumn.name)
            "timetz", "time with time zone" -> rs.getString(jdbcColumn.name)
            "timestamp" -> rs.getString(jdbcColumn.name)
            "timestamptz", "timestamp with time zone" -> rs.getString(jdbcColumn.name)
            "uuid" -> rs.getString(jdbcColumn.name)
            "xml" -> rs.getString(jdbcColumn.name)
            else -> throw IllegalArgumentException("Unsupported PostgreSQL type: ${jdbcColumn.type}")
        }
    }

    override fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema {
        return when (jdbcColumn.type) {
            "serial" -> ColumnSchema.Value(typeOf<Int>())
            "int8", "bigint", "bigserial" -> ColumnSchema.Value(typeOf<Long>())
            "bool" -> ColumnSchema.Value(typeOf<Boolean>())
            "box" -> ColumnSchema.Value(typeOf<String>())
            "bytea" -> ColumnSchema.Value(typeOf<ByteArray>())
            "character", "bpchar" -> ColumnSchema.Value(typeOf<String>())
            "circle" -> ColumnSchema.Value(typeOf<String>())
            "date" -> ColumnSchema.Value(typeOf<String>())
            "float8", "double precision" -> ColumnSchema.Value(typeOf<Double>())
            "int4", "integer" -> ColumnSchema.Value(typeOf<Int>())
            "interval" -> ColumnSchema.Value(typeOf<String>())
            "json", "jsonb" -> ColumnSchema.Value(typeOf<String>())
            "line" -> ColumnSchema.Value(typeOf<String>())
            "lseg" -> ColumnSchema.Value(typeOf<String>())
            "macaddr" -> ColumnSchema.Value(typeOf<String>())
            "money" -> ColumnSchema.Value(typeOf<String>())
            "numeric" -> ColumnSchema.Value(typeOf<String>())
            "path" -> ColumnSchema.Value(typeOf<String>())
            "point" -> ColumnSchema.Value(typeOf<String>())
            "polygon" -> ColumnSchema.Value(typeOf<String>())
            "float4", "real" -> ColumnSchema.Value(typeOf<Float>())
            "int2", "smallint" -> ColumnSchema.Value(typeOf<Float>())
            "smallserial" -> ColumnSchema.Value(typeOf<Int>())
            "text" -> ColumnSchema.Value(typeOf<String>())
            "time" -> ColumnSchema.Value(typeOf<String>())
            "timetz", "time with time zone" -> ColumnSchema.Value(typeOf<String>())
            "timestamp" -> ColumnSchema.Value(typeOf<String>())
            "timestamptz", "timestamp with time zone" -> ColumnSchema.Value(typeOf<String>())
            "uuid" -> ColumnSchema.Value(typeOf<String>())
            "xml" -> ColumnSchema.Value(typeOf<String>())
            else -> throw IllegalArgumentException("Unsupported PostgreSQL type: ${jdbcColumn.type}")
        }
    }
}

/**
 * NOTE: all date and timestamp related types converted to String to avoid java.sql.* types
 */
public object H2 : DbType("h2") {
    override fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any? {
        return when (jdbcColumn.type) {
            "CHARACTER", "CHAR" -> rs.getString(jdbcColumn.name)
            "CHARACTER VARYING", "CHAR VARYING",  "VARCHAR" -> rs.getString(jdbcColumn.name)
            "CHARACTER LARGE OBJECT", "CHAR LARGE OBJECT", "CLOB" -> rs.getString(jdbcColumn.name)
            "MEDIUMTEXT" -> rs.getString(jdbcColumn.name)
            "VARCHAR_IGNORECASE" -> rs.getString(jdbcColumn.name)
            "BINARY" -> rs.getBytes(jdbcColumn.name)
            "BINARY VARYING", "VARBINARY" -> rs.getBytes(jdbcColumn.name)
            "BINARY LARGE OBJECT", "BLOB" -> rs.getBytes(jdbcColumn.name)
            "BOOLEAN" -> rs.getBoolean(jdbcColumn.name)
            "TINYINT" -> rs.getByte(jdbcColumn.name)
            "SMALLINT" -> rs.getShort(jdbcColumn.name)
            "INTEGER", "INT" -> rs.getInt(jdbcColumn.name)
            "BIGINT" -> rs.getLong(jdbcColumn.name)
            "NUMERIC", "DECIMAL", "DEC" -> rs.getFloat(jdbcColumn.name) // not a BigDecimal
            "REAL", "FLOAT" -> rs.getFloat(jdbcColumn.name)
            "DOUBLE PRECISION" -> rs.getDouble(jdbcColumn.name)
            "DECFLOAT" -> rs.getDouble(jdbcColumn.name)
            "DATE" -> rs.getDate(jdbcColumn.name).toString()
            "TIME" -> rs.getTime(jdbcColumn.name).toString()
            "TIME WITH TIME ZONE" -> rs.getTime(jdbcColumn.name).toString()
            "TIMESTAMP" -> rs.getTimestamp(jdbcColumn.name).toString()
            "TIMESTAMP WITH TIME ZONE" -> rs.getTimestamp(jdbcColumn.name).toString()
            "INTERVAL" -> rs.getObject(jdbcColumn.name).toString()
            "JAVA_OBJECT" -> rs.getObject(jdbcColumn.name)
            "ENUM" -> rs.getString(jdbcColumn.name)
            "JSON" -> rs.getString(jdbcColumn.name)
            "UUID" -> rs.getString(jdbcColumn.name)
            //"ARRAY" -> rs.getArray(jdbcColumn.name)
            else -> throw IllegalArgumentException("Unsupported H2 type: ${jdbcColumn.type}")
        }
    }

    override fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema {
        return when (jdbcColumn.type) {
            "CHARACTER", "CHAR" -> ColumnSchema.Value(typeOf<String>())
            "CHARACTER VARYING", "CHAR VARYING",  "VARCHAR" -> ColumnSchema.Value(typeOf<String>())
            "CHARACTER LARGE OBJECT", "CHAR LARGE OBJECT", "CLOB" -> ColumnSchema.Value(typeOf<String>())
            "MEDIUMTEXT" -> ColumnSchema.Value(typeOf<String>())
            "VARCHAR_IGNORECASE" -> ColumnSchema.Value(typeOf<String>())
            "BINARY" -> ColumnSchema.Value(typeOf<ByteArray>())
            "BINARY VARYING", "VARBINARY" -> ColumnSchema.Value(typeOf<ByteArray>())
            "BINARY LARGE OBJECT", "BLOB" -> ColumnSchema.Value(typeOf<ByteArray>())
            "BOOLEAN" -> ColumnSchema.Value(typeOf<Boolean>())
            "TINYINT" -> ColumnSchema.Value(typeOf<Byte>())
            "SMALLINT" -> ColumnSchema.Value(typeOf<Short>())
            "INTEGER", "INT" -> ColumnSchema.Value(typeOf<Int>())
            "BIGINT" -> ColumnSchema.Value(typeOf<Long>())
            "NUMERIC", "DECIMAL", "DEC" -> ColumnSchema.Value(typeOf<Float>())
            "REAL", "FLOAT" -> ColumnSchema.Value(typeOf<Float>())
            "DOUBLE PRECISION" -> ColumnSchema.Value(typeOf<Double>())
            "DECFLOAT" -> ColumnSchema.Value(typeOf<Double>())
            "DATE" -> ColumnSchema.Value(typeOf<String>())
            "TIME" -> ColumnSchema.Value(typeOf<String>())
            "TIME WITH TIME ZONE" -> ColumnSchema.Value(typeOf<String>())
            "TIMESTAMP" -> ColumnSchema.Value(typeOf<String>())
            "TIMESTAMP WITH TIME ZONE" -> ColumnSchema.Value(typeOf<String>())
            "INTERVAL" -> ColumnSchema.Value(typeOf<String>())
            "JAVA_OBJECT" -> ColumnSchema.Value(typeOf<Any>())
            "ENUM" -> ColumnSchema.Value(typeOf<String>())
            "JSON" -> ColumnSchema.Value(typeOf<String>())
            "UUID" -> ColumnSchema.Value(typeOf<String>())
            //"ARRAY" -> rs.getArray(jdbcColumn.name)
            else -> throw IllegalArgumentException("Unsupported H2 type: ${jdbcColumn.type}")
        }
    }
}

public object Sqlite : DbType("sqlite") {
    override fun convertDataFromResultSet(rs: ResultSet, jdbcColumn: JdbcColumn): Any? {
        return when (jdbcColumn.type) {
            "INTEGER" -> rs.getInt(jdbcColumn.name)
            "TEXT" -> rs.getString(jdbcColumn.name)
            "REAL" -> rs.getDouble(jdbcColumn.name)
            "NUMERIC" -> rs.getDouble(jdbcColumn.name)
            "BLOB" -> rs.getBytes(jdbcColumn.name)
            else -> throw IllegalArgumentException("Unsupported SQLite type: ${jdbcColumn.type}")
        }
    }

    override fun toColumnSchema(jdbcColumn: JdbcColumn): ColumnSchema {
        return when (jdbcColumn.type) {
            "INTEGER" -> ColumnSchema.Value(typeOf<Int>())
            "TEXT" -> ColumnSchema.Value(typeOf<String>())
            "REAL" -> ColumnSchema.Value(typeOf<Double>())
            "NUMERIC" -> ColumnSchema.Value(typeOf<Double>())
            "BLOB" -> ColumnSchema.Value(typeOf<ByteArray>())
            else -> throw IllegalArgumentException("Unsupported SQLite type: ${jdbcColumn.type}")
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
            Sqlite.jdbcName in url -> Sqlite
            PostgreSql.jdbcName in url -> PostgreSql
            else -> MariaDb // probably better to add default SQL databases without vendor name
        }
    } else {
        throw RuntimeException("Database URL could not be null. The existing value is $url")
    }
}
