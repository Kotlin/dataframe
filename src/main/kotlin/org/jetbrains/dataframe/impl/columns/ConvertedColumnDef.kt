package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.SingleColumn
import kotlin.reflect.KType

internal class ConvertedColumnDef<C, R>(val source: ColumnDef<C>, val transform: (C) -> R, val type: KType?) : SingleColumn<R> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<R>? {
        return source.resolveSingle(context)?.let { it.data.map(type, transform).addPath(it.path) }
    }
}