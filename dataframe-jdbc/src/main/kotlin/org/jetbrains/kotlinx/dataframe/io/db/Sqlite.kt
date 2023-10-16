package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import kotlin.reflect.typeOf

/**
 * Represents the Sqlite database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for Sqlite,
 * and to generate the corresponding column schema.
 */
public object Sqlite : DbType("sqlite") {
    override val driverClassName: String
        get() = "org.sqlite.JDBC"

    override fun convertDataFromResultSet(rs: ResultSet, tableColumnMetadata: TableColumnMetadata): Any? {
        val name = tableColumnMetadata.name
        return when (tableColumnMetadata.sqlTypeName) {
            "INTEGER", "INTEGER AUTO_INCREMENT" -> rs.getInt(name)
            "TEXT" -> rs.getString(name)
            "REAL" -> rs.getDouble(name)
            "NUMERIC" -> rs.getDouble(name)
            "BLOB" -> rs.getBytes(name)
            else -> throw IllegalArgumentException("Unsupported SQLite type: ${tableColumnMetadata.sqlTypeName}")
        }
    }

    override fun toColumnSchema(tableColumnMetadata: TableColumnMetadata): ColumnSchema {
        return when (tableColumnMetadata.sqlTypeName) {
            "INTEGER", "INTEGER AUTO_INCREMENT" -> ColumnSchema.Value(typeOf<Int>())
            "TEXT" -> ColumnSchema.Value(typeOf<String>())
            "REAL" -> ColumnSchema.Value(typeOf<Double>())
            "NUMERIC" -> ColumnSchema.Value(typeOf<Double>())
            "BLOB" -> ColumnSchema.Value(typeOf<ByteArray>())
            else -> throw IllegalArgumentException("Unsupported SQLite type: ${tableColumnMetadata.sqlTypeName} for column ${tableColumnMetadata.name}")
        }
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return tableMetadata.name.startsWith("sqlite_")
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"))
    }
}
