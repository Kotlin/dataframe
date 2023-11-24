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
 * Represents the PostgreSql database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for PostgreSql,
 * and to generate the corresponding column schema.
 */
public object PostgreSql : DbType("postgresql") {
    override val driverClassName: String
        get() = "org.postgresql.Driver"

    override fun convertDataFromResultSet(rs: ResultSet, tableColumnMetadata: TableColumnMetadata): Any? {
        val name = tableColumnMetadata.name
        return when (tableColumnMetadata.sqlTypeName) {
            "serial" -> rs.getInt(name)
            "int8", "bigint", "bigserial" -> rs.getLong(name)
            "bool" -> rs.getBoolean(name)
            "box" -> rs.getString(name)
            "bytea" -> rs.getBytes(name)
            "character", "bpchar" -> rs.getString(name)
            "circle" -> rs.getString(name)
            "date" -> rs.getDate(name).toString()
            "float8", "double precision" -> rs.getDouble(name)
            "int4", "integer" -> rs.getInt(name)
            "interval" -> rs.getString(name)
            "json", "jsonb" -> rs.getString(name) // TODO: https://github.com/Kotlin/dataframe/issues/462
            "line" -> rs.getString(name)
            "lseg" -> rs.getString(name)
            "macaddr" -> rs.getString(name)
            "money" -> rs.getString(name)
            "numeric" -> rs.getString(name)
            "path" -> rs.getString(name)
            "point" -> rs.getString(name)
            "polygon" -> rs.getString(name)
            "float4", "real" -> rs.getFloat(name)
            "int2", "smallint" -> rs.getShort(name)
            "smallserial" -> rs.getInt(name)
            "text" -> rs.getString(name)
            "time" -> rs.getString(name)
            "timetz", "time with time zone" -> rs.getString(name)
            "timestamp" -> rs.getString(name)
            "timestamptz", "timestamp with time zone" -> rs.getString(name)
            "uuid" -> rs.getString(name)
            "varchar" -> rs.getString(name)
            "xml" -> rs.getString(name)
            else -> throw IllegalArgumentException("Unsupported PostgreSQL type: ${tableColumnMetadata.sqlTypeName}")
        }
    }

    override fun toColumnSchema(tableColumnMetadata: TableColumnMetadata): ColumnSchema {
        return when (tableColumnMetadata.sqlTypeName) {
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
            "json", "jsonb" -> ColumnSchema.Value(typeOf<ColumnGroup<DataRow<String>>>()) // TODO: https://github.com/Kotlin/dataframe/issues/462
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
            "varchar" -> ColumnSchema.Value(typeOf<String>())
            "xml" -> ColumnSchema.Value(typeOf<String>())
            else -> throw IllegalArgumentException("Unsupported PostgreSQL type: ${tableColumnMetadata.sqlTypeName} for column ${tableColumnMetadata.name}")
        }
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return tableMetadata.name.lowercase(Locale.getDefault()).contains("pg_")
            || tableMetadata.schemaName?.lowercase(Locale.getDefault())?.contains("pg_catalog.") ?: false
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"))
    }
}
