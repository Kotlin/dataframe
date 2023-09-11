package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.typeOf

/**
 * Represents the H2 database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for H2,
 * and to generate the corresponding column schema.
 *
 * NOTE: All date and timestamp related types are converted to String to avoid java.sql.* types.
 */
public object H2 : DbType("h2") {
    override fun convertDataFromResultSet(rs: ResultSet, tableColumn: TableColumnMetadata): Any? {
        val name = tableColumn.name
        return when (tableColumn.sqlType) {
            "CHARACTER", "CHAR" -> rs.getString(name)
            "CHARACTER VARYING", "CHAR VARYING",  "VARCHAR" -> rs.getString(name)
            "CHARACTER LARGE OBJECT", "CHAR LARGE OBJECT", "CLOB" -> rs.getString(name)
            "MEDIUMTEXT" -> rs.getString(name)
            "VARCHAR_IGNORECASE" -> rs.getString(name)
            "BINARY" -> rs.getBytes(name)
            "BINARY VARYING", "VARBINARY" -> rs.getBytes(name)
            "BINARY LARGE OBJECT", "BLOB" -> rs.getBytes(name)
            "BOOLEAN" -> rs.getBoolean(name)
            "TINYINT" -> rs.getByte(name)
            "SMALLINT" -> rs.getShort(name)
            "INTEGER", "INT" -> rs.getInt(name)
            "BIGINT" -> rs.getLong(name)
            "NUMERIC", "DECIMAL", "DEC" -> rs.getFloat(name) // not a BigDecimal
            "REAL", "FLOAT" -> rs.getFloat(name)
            "DOUBLE PRECISION" -> rs.getDouble(name)
            "DECFLOAT" -> rs.getDouble(name)
            "DATE" -> rs.getDate(name).toString()
            "TIME" -> rs.getTime(name).toString()
            "TIME WITH TIME ZONE" -> rs.getTime(name).toString()
            "TIMESTAMP" -> rs.getTimestamp(name).toString()
            "TIMESTAMP WITH TIME ZONE" -> rs.getTimestamp(name).toString()
            "INTERVAL" -> rs.getObject(name).toString()
            "JAVA_OBJECT" -> rs.getObject(name)
            "ENUM" -> rs.getString(name)
            "JSON" -> rs.getString(name)
            "UUID" -> rs.getString(name)
            else -> throw IllegalArgumentException("Unsupported H2 type: ${tableColumn.sqlType}")
        }
    }

    override fun toColumnSchema(tableColumnMetadata: TableColumnMetadata): ColumnSchema {
        return when (tableColumnMetadata.sqlType) {
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
            else -> throw IllegalArgumentException("Unsupported H2 type: ${tableColumnMetadata.sqlType}")
        }
    }
}
