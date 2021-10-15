package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import kotlin.reflect.KType

internal class TransformedColumnReference<C, R>(
    val source: ColumnReference<C>,
    val transform: (C) -> R,
    val type: KType?
) :
    ColumnReference<R> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<R>? {
        return source.resolveSingle(context)?.let { it.data.map(type, transform).addPath(it.path, context.df) }
    }

    override fun name() = source.name()

    override fun rename(newName: String) = TransformedColumnReference(source.rename(newName), transform, type)
}
