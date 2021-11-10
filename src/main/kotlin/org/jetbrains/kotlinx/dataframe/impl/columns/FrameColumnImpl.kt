package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.impl.schema.intersectSchemas
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KType

internal open class FrameColumnImpl<T> constructor(
    name: String,
    values: List<DataFrame<T>>,
    columnSchema: Lazy<DataFrameSchema>? = null,
    distinct: Lazy<Set<DataFrame<T>>>? = null
) :
    DataColumnImpl<DataFrame<T>>(
        values,
        name,
        DataFrame::class.createStarProjectedType(false),
        distinct
    ),
    FrameColumn<T> {

    override fun rename(newName: String) = FrameColumnImpl(newName, values, schema, distinct)

    override fun defaultValue() = null

    override fun addParent(parent: ColumnGroup<*>) = FrameColumnWithParent(parent, this)

    override fun createWithValues(values: List<DataFrame<T>>, hasNulls: Boolean?) = DataColumn.createFrameColumn(name, values)

    override fun changeType(type: KType) = throw UnsupportedOperationException()

    override fun distinct() = FrameColumnImpl(name, distinct.value.toList(), schema, distinct)

    override val schema: Lazy<DataFrameSchema> = columnSchema ?: lazy {
        values.mapNotNull { it.takeIf { it.nrow > 0 }?.schema() }.intersectSchemas()
    }

    override fun forceResolve() = ResolvingFrameColumn(name, values, schema, distinct)

    override fun get(indices: Iterable<Int>): FrameColumn<T> = DataColumn.createFrameColumn(name, indices.map { values[it] })
}

internal class ResolvingFrameColumn<T>(
    name: String,
    values: List<DataFrame<T>>,
    columnSchema: Lazy<DataFrameSchema>,
    distinct: Lazy<Set<DataFrame<T>>>
) :
    FrameColumnImpl<T>(name, values, columnSchema, distinct) {

    override fun resolveSingle(context: ColumnResolutionContext) = context.df.getColumn<DataFrame<T>>(name, context.unresolvedColumnsPolicy)?.addPath(context.df)
}
