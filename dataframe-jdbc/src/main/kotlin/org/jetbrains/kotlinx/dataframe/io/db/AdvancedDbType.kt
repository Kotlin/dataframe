package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KType

/**
 * Alternative version of [DbType] that allows to customize type mapping
 * by initializing a [JdbcTypeMapping] instance for each JDBC type.
 *
 * This can be helpful for JDBC databases that support structured data, like [DuckDb]
 * or that need to a lot of type mapping.
 */
public abstract class AdvancedDbType(dbTypeInJdbcUrl: String) : DbType(dbTypeInJdbcUrl) {

    protected abstract fun generateTypeMapping(tableColumnMetadata: TableColumnMetadata): AnyJdbcTypeMapping

    private val typeMappingCache = mutableMapOf<TableColumnMetadata, AnyJdbcTypeMapping>()

    protected fun getTypeMapping(tableColumnMetadata: TableColumnMetadata): AnyJdbcTypeMapping =
        typeMappingCache.getOrPut(tableColumnMetadata) {
            generateTypeMapping(tableColumnMetadata)
        }

    final override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType =
        getTypeMapping(tableColumnMetadata).expectedJdbcType

    final override fun getPreprocessedValueType(
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): KType = getTypeMapping(tableColumnMetadata).preprocessedValueType

    final override fun getTargetColumnSchema(
        tableColumnMetadata: TableColumnMetadata,
        expectedValueType: KType,
    ): ColumnSchema = getTypeMapping(tableColumnMetadata).targetSchema

    final override fun <J : Any, D : Any> preprocessValue(
        value: J?,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
        expectedPreprocessedValueType: KType,
    ): D? = getTypeMapping(tableColumnMetadata).cast<J, D, Any>().preprocessOrCast(value)

    final override fun <D : Any, P : Any> buildDataColumn(
        name: String,
        values: List<D?>,
        tableColumnMetadata: TableColumnMetadata,
        targetColumnSchema: ColumnSchema,
        inferNullability: Boolean,
    ): DataColumn<P?> =
        getTypeMapping(tableColumnMetadata).cast<Any, D, P>()
            .buildDataColumnOrNull(name, values, inferNullability)
            ?: values.toDataColumn(
                name = name,
                targetColumnSchema = targetColumnSchema,
                inferNullability = inferNullability,
            )
}
