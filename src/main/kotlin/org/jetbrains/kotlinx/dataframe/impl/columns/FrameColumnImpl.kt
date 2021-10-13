package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.createTypeWithArgument
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.schema.intersectSchemas
import org.jetbrains.kotlinx.dataframe.splitByIndices
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal open class FrameColumnImpl<T> constructor(
    name: String,
    values: List<DataFrame<T>?>,
    hasNulls: Boolean?,
    columnSchema: Lazy<DataFrameSchema>? = null,
    distinct: Lazy<Set<DataFrame<T>?>>? = null
) :
    DataColumnImpl<DataFrame<T>?>(
        values,
        name,
        createTypeWithArgument<AnyFrame>()
            .withNullability(hasNulls ?: values.any { it == null }),
        distinct
    ),
    FrameColumnInternal<T> {

    constructor(name: String, df: DataFrame<T>, startIndices: Sequence<Int>, emptyToNull: Boolean) : this(name, df.splitByIndices(startIndices, emptyToNull).toList(), hasNulls = if (emptyToNull) null else false)

    override fun rename(newName: String) = FrameColumnImpl(newName, values, hasNulls, schema, distinct)

    override fun defaultValue() = null

    override fun addParent(parent: ColumnGroup<*>) = FrameColumnWithParent(parent, this)

    override fun createWithValues(values: List<DataFrame<T>?>, hasNulls: Boolean?): DataColumn<DataFrame<T>?> {
        return DataColumn.create(name, values, hasNulls)
    }

    override fun changeType(type: KType) = throw UnsupportedOperationException()

    override fun distinct() = FrameColumnImpl(name, distinct.value.toList(), hasNulls, schema, distinct)

    override val schema = columnSchema ?: lazy {
        values.mapNotNull { it?.takeIf { it.nrow > 0 }?.extractSchema() }.intersectSchemas()
    }

    override fun forceResolve() = ResolvingFrameColumn(name, values, hasNulls, schema, distinct)
}

internal class ResolvingFrameColumn<T>(
    name: String,
    values: List<DataFrame<T>?>,
    hasNulls: Boolean,
    columnSchema: Lazy<DataFrameSchema>,
    distinct: Lazy<Set<DataFrame<T>?>>
) :
    FrameColumnImpl<T>(name, values, hasNulls, columnSchema, distinct) {

    override fun resolveSingle(context: ColumnResolutionContext) = context.df.getColumn<DataFrame<T>>(name, context.unresolvedColumnsPolicy)?.addPath(context.df)
}
