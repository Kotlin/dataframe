package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.Connection
import java.sql.DriverManager
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

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"),
        )

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? = null

    override fun createConnection(dbConfig: DbConnectionConfig): Connection {
        return if (dbConfig.readOnly) {
            try {
                // Use SQLiteConfig to set read-only mode before a connection establishment
                val configClass = Class.forName("org.sqlite.SQLiteConfig")
                val config = configClass.getDeclaredConstructor().newInstance()
                val setReadOnlyMethod = configClass.getMethod("setReadOnly", Boolean::class.javaPrimitiveType)
                setReadOnlyMethod.invoke(config, true)
                val createConnectionMethod = configClass.getMethod("createConnection", String::class.java)
                createConnectionMethod.invoke(config, dbConfig.url) as Connection
            } catch (e: Exception) {
                // Fallback to regular connection if SQLiteConfig is not available
                DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
            }
        } else {
            DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
        }
    }

}
