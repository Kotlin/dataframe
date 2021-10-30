package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import kotlin.reflect.KType

internal class ComputedColumnReference<R>(
    val name: String,
    val type: KType?,
    val compute: RowExpression<Any?, R>
) :
    ColumnReference<R> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<R> {
        return context.df.newColumn(type, name) { compute(it, it) }.addPath(context.df)
    }

    override fun name() = name

    override fun rename(newName: String) = ComputedColumnReference(newName, type, compute)

    override fun getValue(row: AnyRow) = compute(row, row)
}

@PublishedApi
internal fun <R> createComputedColumnReference(name: String, type: KType?, compute: RowExpression<Any?, R>): ColumnReference<R> = ComputedColumnReference(name, type, compute)
