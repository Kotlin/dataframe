package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import kotlin.reflect.KType
import org.jetbrains.kotlinx.dataframe.io.db.MariaDb as MariaDbType
import org.jetbrains.kotlinx.dataframe.io.db.MsSql as MsSqlType
import org.jetbrains.kotlinx.dataframe.io.db.MySql as MySqlType
import org.jetbrains.kotlinx.dataframe.io.db.PostgreSql as PostgreSqlType

/**
 * Represents the H2 database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for H2
 * and to generate the corresponding column schema.
 *
 * NOTE: All date and timestamp-related types are converted to String to avoid java.sql.* types.
 */

public open class H2(public val mode: Mode = Mode.Regular) : DbType("h2") {
    @Deprecated("Use H2(mode = Mode.XXX) instead", ReplaceWith("H2(H2.Mode.MySql)"))
    public constructor(dialect: DbType) : this(
        Mode.fromDbType(dialect)
            ?: throw IllegalArgumentException("H2 database could not be specified with H2 dialect!"),
    )

    private val delegate: DbType? = mode.toDbType()

    /**
     * Represents the compatibility modes supported by an H2 database.
     *
     * @property value The string value used in H2 JDBC URL and settings.
     */
    public enum class Mode(public val value: String) {
        /** Native H2 mode (no compatibility), our synthetic marker. */
        Regular("H2-Regular"),
        MySql("MySQL"),
        PostgreSql("PostgreSQL"),
        MsSqlServer("MSSQLServer"),
        MariaDb("MariaDB"), ;

        /**
         * Converts this Mode to the corresponding DbType delegate.
         *
         * @return The DbType for this mode, or null for Regular mode.
         */
        public fun toDbType(): DbType? =
            when (this) {
                Regular -> null
                MySql -> MySqlType
                PostgreSql -> PostgreSqlType
                MsSqlServer -> MsSqlType
                MariaDb -> MariaDbType
            }

        public companion object {
            /**
             * Creates a Mode from the given DbType.
             *
             * @param dialect The DbType to convert.
             * @return The corresponding Mode, or null if the dialect is H2.
             */
            public fun fromDbType(dialect: DbType): Mode? =
                when (dialect) {
                    is H2 -> null
                    MySqlType -> MySql
                    PostgreSqlType -> PostgreSql
                    MsSqlType -> MsSqlServer
                    MariaDbType -> MariaDb
                    else -> Regular
                }

            /**
             * Finds a Mode by its string value (case-insensitive).
             * Handles both URL values (MySQL, PostgreSQL, etc.) and
             * INFORMATION_SCHEMA values (Regular).
             *
             * @param value The string value to search for.
             * @return The matching Mode, or null if not found.
             */
            public fun fromValue(value: String): Mode? {
                // "Regular" from INFORMATION_SCHEMA or "H2-Regular" from URL
                if (value.equals("regular", ignoreCase = true) ||
                    value.equals("h2-regular", ignoreCase = true)
                ) {
                    return Regular
                }
                return entries.find { it.value.equals(value, ignoreCase = true) }
            }
        }
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
        delegate?.convertSqlTypeToColumnSchemaValue(tableColumnMetadata)

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val locale = Locale.getDefault()

        fun String?.containsWithLowercase(substr: String) = this?.lowercase(locale)?.contains(substr) == true
        val schemaName = tableMetadata.schemaName

        // could be extended for other symptoms of the system tables for H2
        val isH2SystemTable = schemaName.containsWithLowercase("information_schema")

        return if (delegate == null) {
            isH2SystemTable
        } else {
            isH2SystemTable || delegate.isSystemTable(tableMetadata)
        }
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        delegate?.buildTableMetadata(tables)
            ?: TableMetadata(
                tables.getString("table_name"),
                tables.getString("table_schem"),
                tables.getString("table_cat"),
            )

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? =
        delegate?.convertSqlTypeToKType(tableColumnMetadata)

    public override fun buildSqlQueryWithLimit(sqlQuery: String, limit: Int): String =
        delegate?.buildSqlQueryWithLimit(sqlQuery, limit) ?: super.buildSqlQueryWithLimit(sqlQuery, limit)
}
