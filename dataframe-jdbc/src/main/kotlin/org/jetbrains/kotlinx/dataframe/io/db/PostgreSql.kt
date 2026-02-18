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
import java.sql.ResultSet
import java.util.Locale
import kotlin.reflect.KType
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

    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType =
        when (tableColumnMetadata.sqlTypeName.lowercase()) {
            "box" -> typeOf<PGbox>()

            "circle" -> typeOf<PGcircle>()

            "line" -> typeOf<PGline>()

            "lseg" -> typeOf<PGlseg>()

            "path" -> typeOf<PGpath>()

            "point" -> typeOf<PGpoint>()

            "polygon" -> typeOf<PGpolygon>()

            "money" -> typeOf<PGmoney>()

            "interval" -> typeOf<PGInterval>()

            // TODO: Composite types like tableColumnMetadata.sqlTypeName = ROW("a" INTEGER, "b" CHARACTER VARYING(10))
            else -> null
        }?.withNullability(tableColumnMetadata.isNullable)
            ?: super.getExpectedJdbcType(tableColumnMetadata)

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
}
