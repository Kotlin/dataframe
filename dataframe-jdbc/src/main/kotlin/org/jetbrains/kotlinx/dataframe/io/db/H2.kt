package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import kotlin.reflect.KType

/**
 * Represents the H2 database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for H2
 * and to generate the corresponding column schema.
 *
 * NOTE: All date and timestamp-related types are converted to String to avoid java.sql.* types.
 */
public open class H2(public val dialect: DbType = MySql) : DbType("h2") {
    init {
        require(dialect::class != H2::class) { "H2 database could not be specified with H2 dialect!" }
    }


    /**
     * It contains constants related to different database modes.
     *
     * The mode value is used in the [extractDBTypeFromConnection] function to determine the corresponding `DbType` for the H2 database connection URL.
     * For example, if the URL contains the mode value "MySQL", the H2 instance with the MySQL database type is returned.
     * Otherwise, the `DbType` is determined based on the URL without the mode value.
     *
     * @see [extractDBTypeFromConnection]
     * @see [createH2Instance]
     */
    public companion object {
        /**
         * Represents the compatibility modes supported by an H2 database.
         *
         * @property value The string value used in H2 JDBC URL and settings.
         */
        public enum class Mode(public val value: String) {
            MySql("MySQL"),
            PostgreSql("PostgreSQL"),
            MsSqlServer("MSSQLServer"),
            MariaDb("MariaDB");

            public companion object {
                /**
                 * Finds a Mode by its string value (case-insensitive).
                 *
                 * @param value The string value to search for.
                 * @return The matching Mode, or null if not found.
                 */
                public fun fromValue(value: String): Mode? =
                    entries.find { it.value.equals(value, ignoreCase = true) }
            }
        }

        @Deprecated("Use Mode.MySql.value instead", ReplaceWith("Mode.MySql.value"))
        public const val MODE_MYSQL: String = "MySQL"
        @Deprecated("Use Mode.PostgreSql.value instead", ReplaceWith("Mode.PostgreSql.value"))
        public const val MODE_POSTGRESQL: String = "PostgreSQL"
        @Deprecated("Use Mode.MsSqlServer.value instead", ReplaceWith("Mode.MsSqlServer.value"))
        public const val MODE_MSSQLSERVER: String = "MSSQLServer"
        @Deprecated("Use Mode.MariaDb.value instead", ReplaceWith("Mode.MariaDb.value"))
        public const val MODE_MARIADB: String = "MariaDB"
    }

    override val driverClassName: String
        get() = "org.h2.Driver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? =
        dialect.convertSqlTypeToColumnSchemaValue(tableColumnMetadata)

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val locale = Locale.getDefault()

        fun String?.containsWithLowercase(substr: String) = this?.lowercase(locale)?.contains(substr) == true
        val schemaName = tableMetadata.schemaName

        // could be extended for other symptoms of the system tables for H2
        val isH2SystemTable = schemaName.containsWithLowercase("information_schema")

        return isH2SystemTable || dialect.isSystemTable(tableMetadata)
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata = dialect.buildTableMetadata(tables)

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? =
        dialect.convertSqlTypeToKType(tableColumnMetadata)

    public override fun buildSqlQueryWithLimit(sqlQuery: String, limit: Int): String =
        dialect.buildSqlQueryWithLimit(sqlQuery, limit)
}
