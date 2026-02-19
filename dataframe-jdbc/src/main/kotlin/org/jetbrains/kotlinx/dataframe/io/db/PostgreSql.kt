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
import java.util.Locale
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

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
    private val pgObjectTypes =
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

    // TODO: Composite types like tableColumnMetadata.sqlTypeName = ROW("a" INTEGER, "b" CHARACTER VARYING(10))
    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        val typeName = tableColumnMetadata.sqlTypeName.lowercase()
        return if (typeName in pgObjectTypes) {
            pgObjectTypes[typeName]!!.kType.withNullability(tableColumnMetadata.isNullable)
        } else {
            super.getExpectedJdbcType(tableColumnMetadata)
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
        return if (typeName in pgObjectTypes) {
            rs.getObject(columnIndex + 1, pgObjectTypes[typeName]!!.javaClass) as J
        } else {
            super.getValueFromResultSet(rs, columnIndex, tableColumnMetadata, expectedJdbcType)
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
