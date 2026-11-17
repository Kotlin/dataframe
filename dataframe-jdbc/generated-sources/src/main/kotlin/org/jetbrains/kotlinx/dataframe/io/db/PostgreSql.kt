package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
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
import java.util.UUID
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

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
     * Map of [<code>PostgreSQL object</code>][PGobject] types by name
     * containing both their [<code>Java class</code>][Class] and [<code>Kotlin type</code>][KType].
     *
     * These types need to be retrieved explicitly with [<code>Java class</code>][Class] in [<code>ResultSet.getObject</code>][ResultSet.getObject], else
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
     * The driver reports every `bit` column as [<code>java.sql.Types.BIT</code>][java.sql.Types.BIT] with `java.lang.Boolean` as its
     * column class, but only single-bit columns actually come back as a [<code>Boolean</code>][Boolean]; wider ones come
     * back as a [<code>PGobject</code>][PGobject] holding the bit string. The declared column width is the only thing in the
     * metadata that tells the two apart, see [<code>getExpectedJdbcType</code>][getExpectedJdbcType].
     *
     * That width is [<code>TableColumnMetadata.size</code>][TableColumnMetadata.size] — `0` when the driver reports no display size, in
     * which case the column maps to [<code>Boolean</code>][Boolean] as it did before. `bit varying`/`varbit` is excluded by
     * the [<code>TableColumnMetadata.sqlTypeName</code>][TableColumnMetadata.sqlTypeName] check: the driver reports it as
     * [<code>java.sql.Types.OTHER</code>][java.sql.Types.OTHER], and it is covered by [<code>isTextForm</code>][isTextForm] instead.
     *
     * Deliberately *not* named `isMultiBit` like the MySQL/MariaDB extension of the same shape in
     * `util.kt`: this one resolves to a [<code>String</code>][String] column and that one to a [<code>ByteArray</code>][ByteArray] one.
     */
    private val TableColumnMetadata.isWideBitString: Boolean
        get() = jdbcType == Types.BIT && sqlTypeName.lowercase() == "bit" && size > 1

    /**
     * PostgreSQL types the driver returns as a plain [<code>PGobject</code>][PGobject]: no class of their own, only the type
     * name and the text PostgreSQL prints for the value. That text is all the value holds, so these
     * columns are read as a [<code>String</code>][String], see [<code>isTextForm</code>][isTextForm].
     */
    private val textFormTypeNames = setOf(
        "json",
        "jsonb",
        "inet",
        "cidr",
        "macaddr",
        "macaddr8",
        "varbit",
        "tsvector",
        "tsquery",
        "int4range",
        "int8range",
        "numrange",
        "tsrange",
        "tstzrange",
        "daterange",
        "int4multirange",
        "int8multirange",
        "nummultirange",
        "tsmultirange",
        "tstzmultirange",
        "datemultirange",
    )

    /**
     * `true` for a column of one of the [<code>textFormTypeNames</code>][textFormTypeNames], read as its text form, e.g. `{"k": 1}`
     * or `[1,2)`.
     *
     * The PostgreSQL driver reports these as [<code>java.sql.Types.OTHER</code>][java.sql.Types.OTHER] with `org.postgresql.util.PGobject`
     * (`json`, `jsonb`) or `java.lang.String` (the rest) as their column class. The class check keeps
     * H2 in PostgreSQL mode out: H2 delegates [<code>getExpectedJdbcType</code>][getExpectedJdbcType] here but reads the value itself,
     * and its `JSON` column, also reported as [<code>java.sql.Types.OTHER</code>][java.sql.Types.OTHER], holds a byte array.
     */
    private val TableColumnMetadata.isTextForm: Boolean
        get() = jdbcType == Types.OTHER &&
            sqlTypeName.lowercase() in textFormTypeNames &&
            (javaClassName == "org.postgresql.util.PGobject" || javaClassName == "java.lang.String")

    /**
     * `true` for a `uuid` column, which the driver reports as [<code>java.sql.Types.OTHER</code>][java.sql.Types.OTHER] and returns as a
     * [<code>UUID</code>][UUID], converted to a [<code>Uuid</code>][Uuid] in [<code>preprocessValue</code>][preprocessValue]. Unlike [<code>isTextForm</code>][isTextForm], it keys on the column class
     * alone, as [<code>DbType</code>][DbType] does for its `UUID` branch. H2 in PostgreSQL mode reports its
     * `UUID` as [<code>java.sql.Types.BINARY</code>][java.sql.Types.BINARY], which [<code>DbType</code>][DbType] already maps to [<code>Uuid</code>][Uuid].
     */
    private val TableColumnMetadata.isUuid: Boolean
        get() = jdbcType == Types.OTHER && javaClassName == "java.util.UUID"

    // TODO: Composite types like tableColumnMetadata.sqlTypeName = ROW("a" INTEGER, "b" CHARACTER VARYING(10))

    /**
     * Returns the type of the values [<code>getValueFromResultSet</code>][getValueFromResultSet] returns for the given column. It is
     * also the column type, except for `uuid`, see the last item below.
     *
     * PostgreSQL-specific deviations from the default type mapping:
     *
     * - The geometric types, `money` and `interval` are read as the driver's classes for them:
     *   [<code>PGpoint</code>][PGpoint], [<code>PGbox</code>][PGbox], [<code>PGmoney</code>][PGmoney], [<code>PGInterval</code>][PGInterval] and so on.
     * - The types the driver returns as a plain [<code>PGobject</code>][PGobject], with no class of their own, are read as a
     *   [<code>String</code>][String] holding the text PostgreSQL prints for the value, for example `10.0.0.0/8` for a
     *   `cidr`. These are `json` and `jsonb`, `inet`, `cidr`, `macaddr` and `macaddr8`, `varbit`,
     *   `tsvector` and `tsquery`, and the range and multirange types.
     *   The text comes from the server, so it can differ from the text that was inserted: `jsonb` is
     *   normalized, a range of integers or dates is printed in its canonical form, and a `tstzrange`
     *   is printed in the session time zone.
     * - A `bit(n)` column with `n > 1` is read as a [<code>String</code>][String] of bits; `bit(1)` stays [<code>Boolean</code>][Boolean].
     * - `uuid` values are the driver's [<code>UUID</code>][UUID]; [<code>preprocessValue</code>][preprocessValue] converts them to [<code>Uuid</code>][Uuid], so the
     *   column is a [<code>Uuid</code>][Uuid] column, the same as a `UUID` column of H2 and DuckDB.
     *
     * Any other type the driver returns as a plain [<code>PGobject</code>][PGobject], a composite type for example, falls
     * through to the default mapping, which types it [<code>Any</code>][Any].
     *
     * For more information: [See PostgreSQL type mapping on the documentation website.](https://kotlin.github.io/dataframe/readsqltypemapping-postgresql.html)
     */
    override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType {
        val typeName = tableColumnMetadata.sqlTypeName.lowercase()
        return when {
            typeName in pgObjectTypes ->
                pgObjectTypes[typeName]!!.kType.withNullability(tableColumnMetadata.isNullable)

            // read as its text form, e.g. "101" or "[1,2)", see getValueFromResultSet
            tableColumnMetadata.isWideBitString || tableColumnMetadata.isTextForm ->
                typeOf<String>().withNullability(tableColumnMetadata.isNullable)

            // converted to a Uuid, see preprocessValue
            tableColumnMetadata.isUuid ->
                typeOf<UUID>().withNullability(tableColumnMetadata.isNullable)

            else -> super.getExpectedJdbcType(tableColumnMetadata)
        }
    }

    /**
     * Reads the values [<code>getExpectedJdbcType</code>][getExpectedJdbcType] declares. The geometric types, `money` and `interval`
     * are retrieved explicitly with their [<code>Java class</code>][Class], else the class [<code>ResultSet.getObject</code>][ResultSet.getObject]
     * returns for them is unpredictable. The [<code>PGobject</code>][PGobject] types with no class of their own, and
     * `bit(n)` with `n > 1`, are retrieved as their text.
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

            // the value is a PGobject holding the text form, see getExpectedJdbcType
            tableColumnMetadata.isWideBitString || tableColumnMetadata.isTextForm ->
                rs.getString(columnIndex + 1) as J

            else -> super.getValueFromResultSet(rs, columnIndex, tableColumnMetadata, expectedJdbcType)
        }
    }

    /**
     * Returns [<code>Uuid</code>][Uuid] for a `uuid` column, see [<code>preprocessValue</code>][preprocessValue], and the type [<code>DbType</code>][DbType] returns
     * for every other column.
     */
    override fun getPreprocessedValueType(tableColumnMetadata: TableColumnMetadata, expectedJdbcType: KType): KType =
        if (tableColumnMetadata.isUuid) {
            typeOf<Uuid>().withNullability(tableColumnMetadata.isNullable)
        } else {
            super.getPreprocessedValueType(tableColumnMetadata, expectedJdbcType)
        }

    /**
     * Converts the driver's [<code>UUID</code>][UUID] of a `uuid` column to a [<code>Uuid</code>][Uuid], as [<code>DbType</code>][DbType] does for the `UUID`
     * columns of H2. Every other value is preprocessed as [<code>DbType</code>][DbType] does it.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <J, D> preprocessValue(
        value: J,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
        expectedPreprocessedValueType: KType,
    ): D =
        if (tableColumnMetadata.isUuid) {
            (value as UUID?)?.toKotlinUuid() as D
        } else {
            super.preprocessValue(value, tableColumnMetadata, expectedJdbcType, expectedPreprocessedValueType)
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
