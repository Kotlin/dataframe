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
        /** It represents the mode value "MySQL" for the H2 database. */
        public const val MODE_MYSQL: String = "MySQL"

        /** It represents the mode value "PostgreSQL" for the H2 database. */
        public const val MODE_POSTGRESQL: String = "PostgreSQL"

        /** It represents the mode value "MSSQLServer" for the H2 database. */
        public const val MODE_MSSQLSERVER: String = "MSSQLServer"

        /** It represents the mode value "MariaDB" for the H2 database. */
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

    public override fun buildSqlQueryWithLimit(sqlQuery: String, limit: Int): String = dialect.buildSqlQueryWithLimit(sqlQuery, limit)
}
