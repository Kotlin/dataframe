package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.*
import kotlin.reflect.KType

/**
 * Represents the H2 database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for H2
 * and to generate the corresponding column schema.
 *
 * NOTE: All date and timestamp-related types are converted to String to avoid java.sql.* types.
 */
public class H2 (public val dialect: DbType = MySql) : DbType("h2") {
    init {
        require(dialect.javaClass.simpleName != "H2kt") { "H2 database could not be specified with H2 dialect!"}
    }

    public companion object {
        public const val MODE_MYSQL: String = "MySQL"
        public const val MODE_POSTGRESQL: String = "PostgreSQL"
        public const val MODE_MSSQLSERVER: String = "MSSQLServer"
        public const val MODE_MARIADB: String = "MariaDB"
    }

    override val driverClassName: String
        get() = "org.h2.Driver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        return dialect.convertSqlTypeToColumnSchemaValue(tableColumnMetadata)
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val locale = Locale.getDefault()
        fun String?.containsWithLowercase(substr: String) = this?.lowercase(locale)?.contains(substr) == true
        val schemaName = tableMetadata.schemaName

        // could be extended for other symptoms of the system tables for H2
        val isH2SystemTable = schemaName.containsWithLowercase("information_schema")

        return isH2SystemTable || dialect.isSystemTable(tableMetadata)
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return dialect.buildTableMetadata(tables)
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        return dialect.convertSqlTypeToKType(tableColumnMetadata)
    }

    public override fun sqlQueryLimit(sqlQuery: String, limit: Int): String {
        return dialect.sqlQueryLimit(sqlQuery, limit)
    }
}
