package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.db.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.sqlite.SQLiteConfig
import java.math.BigInteger
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Represents the Sqlite database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for Sqlite,
 * and to generate the corresponding column schema.
 *
 * Use [customTypesMap] to register custom types and provide the corresponding [KType] for each one.
 * [KType] must correspond to JDBC actual type.
 * Even for default Sqlite types, you can override the actual [KType] after reading the column
 * by providing a custom type in [customTypesMap].
 */
public class Sqlite(public val customTypesMap: Map<String, KType> = mapOf()) : DbType("sqlite") {
    override val driverClassName: String
        get() = "org.sqlite.JDBC"

    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType =
        customTypesMap[tableColumnMetadata.sqlTypeName] ?: super.getExpectedJdbcType(tableColumnMetadata)

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean = tableMetadata.name.startsWith("sqlite_")

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("TABLE_NAME"),
            tables.getString("TABLE_SCHEM"),
            tables.getString("TABLE_CAT"),
        )

    override fun createConnection(dbConfig: DbConnectionConfig): Connection =
        if (dbConfig.readOnly) {
            val config = SQLiteConfig()
            config.setReadOnly(true)
            config.createConnection(dbConfig.url)
        } else {
            DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
        }

    public companion object {
        public val default: Sqlite = Sqlite()

        public fun withCustomTypes(customTypesMap: Map<String, KType>): Sqlite = Sqlite(customTypesMap)
    }
}
