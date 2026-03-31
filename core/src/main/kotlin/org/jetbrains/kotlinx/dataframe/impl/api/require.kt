package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.getColumnWithPath
import org.jetbrains.kotlinx.dataframe.api.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType

@PublishedApi
internal fun <T, C> DataFrame<T>.requireImpl(column: ColumnSelector<T, C>, type: KType): DataFrame<T> {
    val resolvedColumn = getColumnWithPath(column)
    val actualType = resolvedColumn.data.type
    require(resolvedColumn.data.isSubtypeOf(type)) {
        "Column '${resolvedColumn.path.joinToString()}' has type '$actualType', which is not subtype of required '$type' type."
    }
    return this
}
