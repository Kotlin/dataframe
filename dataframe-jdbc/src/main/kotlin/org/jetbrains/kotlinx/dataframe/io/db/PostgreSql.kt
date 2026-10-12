package org.jetbrains.kotlinx.dataframe.io.db

import org.postgresql.geometric.PGbox
import org.postgresql.geometric.PGcircle
import org.postgresql.geometric.PGline
import org.postgresql.geometric.PGlseg
import org.postgresql.geometric.PGpath
import org.postgresql.geometric.PGpoint
import org.postgresql.geometric.PGpolygon
import org.postgresql.util.PGInterval
import org.postgresql.util.PGmoney
import org.postgresql.util.PGobject
import java.sql.ResultSet
import java.sql.Types
import java.util.Locale
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
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

    /**
     * Map of [PostgreSQL object][PGobject] types by name
     * containing both their [Java class][Class] and [Kotlin type][KType].
     *
     * These types need to be retrieved explicitly with [Java class][Class] in [ResultSet.getObject], else
     * their return type is unpredictable.
     */
    private val pgObjectTypes by lazy {
        listOf(
            PGbox(),
            PGcircle(),
            PGline(),
            PGlseg(),
            PGpath(),
            PGpoint(),
            PGpolygon(),
            PGmoney(),
            PGInterval(),
        ).map(::PgObjectType)
            .associateBy { it.typeName }
    }

    /**
     * `true` for a multi-bit `bit(n)` column, `n > 1`.
     *
     * The driver reports every `bit` column as [java.sql.Types.BIT] with `java.lang.Boolean` as its
     * column class, but only single-bit columns actually come back as a [Boolean]; wider ones come
     * back as a [PGobject] holding the bit string. The declared column width is the only thing in the
     * metadata that tells the two apart, see [getExpectedJdbcType].
     *
     * That width is [TableColumnMetadata.size] — `0` when the driver reports no display size, in
     * which case the column maps to [Boolean] as it did before. `bit varying`/`varbit` is excluded by
     * the [TableColumnMetadata.sqlTypeName] check: the driver reports it as
     * [java.sql.Types.OTHER] and it is read as an opaque [PGobject].
     *
     * Deliberately *not* named `isMultiBit` like the MySQL/MariaDB extension of the same shape in
     * `util.kt`: this one resolves to a [String] column and that one to a [ByteArray] one.
     */
    private val TableColumnMetadata.isWideBitString: Boolean
        get() = jdbcType == Types.BIT && sqlTypeName.lowercase() == "bit" && size > 1

    // TODO: Composite types like tableColumnMetadata.sqlTypeName = ROW("a" INTEGER, "b" CHARACTER VARYING(10))
    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        val typeName = tableColumnMetadata.sqlTypeName.lowercase()
        return when {
            typeName in pgObjectTypes ->
                pgObjectTypes[typeName]!!.kType.withNullability(tableColumnMetadata.isNullable)

            // read as its bit-string form, e.g. "101", see getValueFromResultSet
            tableColumnMetadata.isWideBitString ->
                typeOf<String>().withNullability(tableColumnMetadata.isNullable)

            else -> super.getExpectedJdbcType(tableColumnMetadata)
        }
    }

    /**
     * Overridden so [PGobject] types are retrieved explicitly with [Java class][Class],
     * else their return type is unpredictable.
     */
    override fun <J> getValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): J {
        val typeName = tableColumnMetadata.sqlTypeName.lowercase()
        return when {
            typeName in pgObjectTypes -> rs.getObject(columnIndex + 1, pgObjectTypes[typeName]!!.javaClass) as J

            // the value is a PGobject holding the bit string, see getExpectedJdbcType
            tableColumnMetadata.isWideBitString -> rs.getString(columnIndex + 1) as J

            else -> super.getValueFromResultSet(rs, columnIndex, tableColumnMetadata, expectedJdbcType)
        }
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean =
        tableMetadata.name.lowercase(Locale.getDefault()).contains("pg_") ||
            tableMetadata.schemaName?.lowercase(Locale.getDefault())?.contains("pg_catalog.") ?: false

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"),
        )

    override fun quoteIdentifier(name: String): String {
        // schema.table -> "schema"."table"
        return name.split(".").joinToString(".") { "\"$it\"" }
    }

    private data class PgObjectType(val typeName: String, val kType: KType, val javaClass: Class<*>) {
        constructor(pgObject: PGobject) : this(
            typeName = pgObject.type,
            kType = pgObject::class.starProjectedType,
            javaClass = pgObject::class.java,
        )
    }
}
