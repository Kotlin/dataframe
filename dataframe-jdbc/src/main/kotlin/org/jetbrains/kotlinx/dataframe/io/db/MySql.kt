package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
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

    override fun convertDataFromResultSet(rs: ResultSet, tableColumnMetadata: TableColumnMetadata): Any? {
        val name = tableColumnMetadata.name
        return when (tableColumnMetadata.sqlTypeName) {
            "BIT" -> rs.getBytes(name)
            "TINYINT" -> rs.getInt(name)
            "SMALLINT" -> rs.getInt(name)
            "MEDIUMINT"-> rs.getInt(name)
            "MEDIUMINT UNSIGNED" -> rs.getLong(name)
            "INTEGER", "INT" -> rs.getInt(name)
            "INTEGER UNSIGNED", "INT UNSIGNED" -> rs.getLong(name)
            "BIGINT" -> rs.getLong(name)
            "FLOAT" -> rs.getFloat(name)
            "DOUBLE" -> rs.getDouble(name)
            "DECIMAL" -> rs.getBigDecimal(name)
            "DATE" -> rs.getDate(name).toString()
            "DATETIME" -> rs.getTimestamp(name).toString()
            "TIMESTAMP" -> rs.getTimestamp(name).toString()
            "TIME"-> rs.getTime(name).toString()
            "YEAR" -> rs.getDate(name).toString()
            "VARCHAR", "CHAR" -> rs.getString(name)
            "BINARY" -> rs.getBytes(name)
            "VARBINARY" -> rs.getBytes(name)
            "TINYBLOB"-> rs.getBytes(name)
            "BLOB"-> rs.getBytes(name)
            "MEDIUMBLOB" -> rs.getBytes(name)
            "LONGBLOB" -> rs.getBytes(name)
            "TEXT" -> rs.getString(name)
            "MEDIUMTEXT" -> rs.getString(name)
            "LONGTEXT" -> rs.getString(name)
            "ENUM" -> rs.getString(name)
            "SET" -> rs.getString(name)
            // special mysql types
            "JSON" -> rs.getString(name) // TODO: https://github.com/Kotlin/dataframe/issues/462
            "GEOMETRY" -> rs.getBytes(name)
            else -> throw IllegalArgumentException("Unsupported MySQL type: ${tableColumnMetadata.sqlTypeName}")
        }
    }

    override fun toColumnSchema(tableColumnMetadata: TableColumnMetadata): ColumnSchema {
        return when (tableColumnMetadata.sqlTypeName) {
            "BIT" -> ColumnSchema.Value(typeOf<ByteArray>())
            "TINYINT" -> ColumnSchema.Value(typeOf<Int>())
            "SMALLINT" -> ColumnSchema.Value(typeOf<Int>())
            "MEDIUMINT"-> ColumnSchema.Value(typeOf<Int>())
            "MEDIUMINT UNSIGNED" -> ColumnSchema.Value(typeOf<Long>())
            "INTEGER", "INT" -> ColumnSchema.Value(typeOf<Int>())
            "INTEGER UNSIGNED", "INT UNSIGNED" -> ColumnSchema.Value(typeOf<Long>())
            "BIGINT" -> ColumnSchema.Value(typeOf<Long>())
            "FLOAT" -> ColumnSchema.Value(typeOf<Float>())
            "DOUBLE" -> ColumnSchema.Value(typeOf<Double>())
            "DECIMAL" -> ColumnSchema.Value(typeOf<Double>())
            "DATE" -> ColumnSchema.Value(typeOf<String>())
            "DATETIME" -> ColumnSchema.Value(typeOf<String>())
            "TIMESTAMP" -> ColumnSchema.Value(typeOf<String>())
            "TIME"-> ColumnSchema.Value(typeOf<String>())
            "YEAR" -> ColumnSchema.Value(typeOf<String>())
            "VARCHAR", "CHAR" -> ColumnSchema.Value(typeOf<String>())
            "BINARY" -> ColumnSchema.Value(typeOf<ByteArray>())
            "VARBINARY" -> ColumnSchema.Value(typeOf<ByteArray>())
            "TINYBLOB"-> ColumnSchema.Value(typeOf<ByteArray>())
            "BLOB"-> ColumnSchema.Value(typeOf<ByteArray>())
            "MEDIUMBLOB" -> ColumnSchema.Value(typeOf<ByteArray>())
            "LONGBLOB" -> ColumnSchema.Value(typeOf<ByteArray>())
            "TEXT" -> ColumnSchema.Value(typeOf<String>())
            "MEDIUMTEXT" -> ColumnSchema.Value(typeOf<String>())
            "LONGTEXT" -> ColumnSchema.Value(typeOf<String>())
            "ENUM" -> ColumnSchema.Value(typeOf<String>())
            "SET" -> ColumnSchema.Value(typeOf<String>())
            // special mysql types
            "JSON" -> ColumnSchema.Value(typeOf<ColumnGroup<DataRow<String>>>()) // TODO: https://github.com/Kotlin/dataframe/issues/462
            "GEOMETRY" -> ColumnSchema.Value(typeOf<ByteArray>())
            else -> throw IllegalArgumentException("Unsupported MySQL type: ${tableColumnMetadata.sqlTypeName} for column ${tableColumnMetadata.name}")
        }
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
}
