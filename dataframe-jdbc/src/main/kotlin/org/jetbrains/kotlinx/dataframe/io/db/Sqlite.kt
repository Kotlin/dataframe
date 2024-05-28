package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.KType

/**
 * Represents the Sqlite database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for Sqlite,
 * and to generate the corresponding column schema.
 */
public object Sqlite : DbType("sqlite") {
    override val driverClassName: String
        get() = "org.sqlite.JDBC"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? = null

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean = tableMetadata.name.startsWith("sqlite_")

    override fun buildTableMetadata(tables: ResultSet): TableMetadata = TableMetadata(
        tables.getString("TABLE_NAME"),
        tables.getString("TABLE_SCHEM"),
        tables.getString("TABLE_CAT"),
    )

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? = null
}
