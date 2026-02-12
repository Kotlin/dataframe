package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.KType

/**
 * Alternative version of [DbType] that allows to customize type mapping
 * by initializing a [JdbcToDataFrameConverter] instance for each JDBC type.
 *
 * This can be helpful for JDBC databases that support structured data, like [DuckDb]
 * or that need to a lot of type mapping.
 *
 * This API is experimental and subject to change.
 */
public abstract class AdvancedDbType(dbTypeInJdbcUrl: String) : DbType(dbTypeInJdbcUrl) {

    protected abstract fun generateConverter(tableColumnMetadata: TableColumnMetadata): AnyJdbcToDataFrameConverter

    private val converterCache = mutableMapOf<TableColumnMetadata, AnyJdbcToDataFrameConverter>()

    protected fun getConverter(tableColumnMetadata: TableColumnMetadata): AnyJdbcToDataFrameConverter =
        converterCache.getOrPut(tableColumnMetadata) {
            generateConverter(tableColumnMetadata)
        }

    final override fun getExpectedJdbcType(tableColumnMetadata: TableColumnMetadata): KType =
        getConverter(tableColumnMetadata).expectedJdbcType

    final override fun getPreprocessedValueType(
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): KType = getConverter(tableColumnMetadata).preprocessedValueType

    final override fun getTargetColumnSchema(
        tableColumnMetadata: TableColumnMetadata,
        expectedValueType: KType,
    ): ColumnSchema? = getConverter(tableColumnMetadata).targetSchema

    @Suppress("UNCHECKED_CAST")
    final override fun <J> getValueFromResultSet(
        rs: ResultSet,
        columnIndex: Int,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
    ): J =
        getConverter(tableColumnMetadata)
            .cast<J, Any?, Any?>()
            .getValueFromResultSetOrElse(rs, columnIndex) {
                try {
                    rs.getObject(columnIndex + 1)
                } catch (_: Throwable) {
                    // TODO?
                    rs.getString(columnIndex + 1)
                } as J
            }

    final override fun <J, D> preprocessValue(
        value: J,
        tableColumnMetadata: TableColumnMetadata,
        expectedJdbcType: KType,
        expectedPreprocessedValueType: KType,
    ): D =
        getConverter(tableColumnMetadata)
            .cast<J, D, Any?>()
            .preprocessOrCast(value)

    final override fun <D, P> buildDataColumn(
        name: String,
        values: List<D>,
        tableColumnMetadata: TableColumnMetadata,
        targetColumnSchema: ColumnSchema?,
        inferNullability: Boolean,
    ): DataColumn<P> =
        getConverter(tableColumnMetadata)
            .cast<Any?, D, P>()
            .buildDataColumnOrNull(name, values, inferNullability)
            ?: values.toDataColumn(
                name = name,
                targetColumnSchema = targetColumnSchema,
                inferNullability = inferNullability,
            )
}
