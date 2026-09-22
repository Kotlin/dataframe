package org.jetbrains.kotlinx.dataframe.io.db

import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.Locale
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Represents the MSSQL database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MSSQL,
 * and to generate the corresponding column schema.
 */
public object MsSql : DbType("sqlserver") {
    override val driverClassName: String
        get() = "com.microsoft.sqlserver.jdbc.SQLServerDriver"

    /**
     * The class name the MSSQL driver reports for `DATETIMEOFFSET` columns,
     * referenced by name so that this module does not need the driver on its compile classpath.
     */
    private const val DATE_TIME_OFFSET_CLASS_NAME = "microsoft.sql.DateTimeOffset"

    /** The driver-specific `microsoft.sql.Types.SQL_VARIANT`, referenced by value for the same reason. */
    private const val SQL_VARIANT_JDBC_TYPE = -156

    /**
     * MSSQL-specific deviations from the default type mapping, all of them cases where the default
     * mapping and the values [ResultSet.getObject] returns disagreed (see #2087):
     *
     * - `DATETIMEOFFSET` is reported with the driver-specific JDBC type `-155` and
     *   [microsoft.sql.DateTimeOffset][DATE_TIME_OFFSET_CLASS_NAME] as its column class, which the
     *   default mapping has no entry for, so it fell back to [String] while the driver returned a
     *   `DateTimeOffset`. It is read as [OffsetDateTime]: a JDK type that keeps the UTC offset of the
     *   value, and the one the default mapping already uses for
     *   [java.sql.Types.TIMESTAMP_WITH_TIMEZONE]. The driver produces it directly,
     *   see [getValueFromResultSet].
     * - `SMALLINT` and `TINYINT` are reported with `java.lang.Short` as their column class and the
     *   driver does return [Short] values, while the default mapping declared them [Int].
     *   [Short] is also the smallest Kotlin type that fits MSSQL's `TINYINT`, which — unlike in most
     *   databases — is *unsigned* (`0..255`) and so does not fit a [Byte].
     * - `SQL_VARIANT` holds a value of any base type, and the class of that value differs from row to
     *   row (an `Int` for `CAST(1 AS SQL_VARIANT)`, a `String` for a string), so the column can only
     *   honestly be typed [Any]. The default mapping declared it [String].
     */
    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        val kType = when {
            tableColumnMetadata.javaClassName == DATE_TIME_OFFSET_CLASS_NAME -> typeOf<OffsetDateTime>()
            tableColumnMetadata.javaClassName == "java.lang.Short" -> typeOf<Short>()
            tableColumnMetadata.jdbcType == SQL_VARIANT_JDBC_TYPE -> typeOf<Any>()
            else -> return super.getExpectedJdbcType(tableColumnMetadata)
        }
        return kType.withNullability(tableColumnMetadata.isNullable)
    }

    /**
     * Reads `DATETIMEOFFSET` columns as [OffsetDateTime] directly from the driver,
     * see [getExpectedJdbcType]. Plain [ResultSet.getObject] would return a
     * driver-specific `microsoft.sql.DateTimeOffset` instead.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <J> getValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): J =
        if (tableColumnMetadata.javaClassName == DATE_TIME_OFFSET_CLASS_NAME) {
            rs.getObject(columnIndex + 1, OffsetDateTime::class.java) as J
        } else {
            super.getValueFromResultSet(rs, columnIndex, tableColumnMetadata, expectedJdbcType)
        }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        val locale = Locale.getDefault()

        fun String?.containsWithLowercase(substr: String) = this?.lowercase(locale)?.contains(substr) == true

        val schemaName = tableMetadata.schemaName
        val tableName = tableMetadata.name
        val catalogName = tableMetadata.catalogue

        return schemaName.containsWithLowercase("sys") ||
            schemaName.containsWithLowercase("information_schema") ||
            tableName.startsWith("sys") ||
            tableName.startsWith("dt") ||
            tableName.containsWithLowercase("sys_config") ||
            catalogName.containsWithLowercase("system") ||
            catalogName.containsWithLowercase("master") ||
            catalogName.containsWithLowercase("model") ||
            catalogName.containsWithLowercase("msdb") ||
            catalogName.containsWithLowercase("tempdb")
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"),
        )

    public override fun buildSqlQueryWithLimit(sqlQuery: String, limit: Int): String =
        sqlQuery.replace("SELECT", "SELECT TOP $limit", ignoreCase = true)

    override fun quoteIdentifier(name: String): String {
        // schema.table -> [schema].[table]
        return name.split(".").joinToString(".") { "[$it]" }
    }
}
