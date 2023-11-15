package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import kotlin.reflect.KType
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
    override val driverClassName: String
        get() = "org.h2.Driver"

    override fun toColumnSchema(tableColumnMetadata: TableColumnMetadata): ColumnSchema {
        return when (tableColumnMetadata.sqlTypeName) {
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
            "JSON" -> ColumnSchema.Value(typeOf<String>()) // TODO: https://github.com/Kotlin/dataframe/issues/462
            "UUID" -> ColumnSchema.Value(typeOf<String>())
            else -> throw IllegalArgumentException("Unsupported H2 type: ${tableColumnMetadata.sqlTypeName} for column ${tableColumnMetadata.name}")
        }
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return tableMetadata.name.lowercase(Locale.getDefault()).contains("sys_")
            || tableMetadata.schemaName?.lowercase(Locale.getDefault())?.contains("information_schema") ?: false
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"))
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        return null
    }
}
