package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.getColumnWithPath
import org.jetbrains.kotlinx.dataframe.api.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.exceptions.ColumnNotFoundException
import org.jetbrains.kotlinx.dataframe.impl.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType

@PublishedApi
internal fun <T, C> DataFrame<T>.requireImpl(column: ColumnSelector<T, C>, type: KType): DataFrame<T> {
    val resolvedColumn = try {
        getColumnWithPath(column)
    } catch (e: IllegalStateException) {
        // Distinguish a missing column from an IllegalStateException thrown by the selector code itself:
        // the Create policy never throws for a missing column, so a failed re-resolution, or a column
        // that does exist, means the original exception is not a resolution failure and is rethrown as is.
        val requestedPath = runCatching {
            getColumnsWithPaths(UnresolvedColumnsPolicy.Create, column).single().path
        }.getOrElse { throw e }
        if (getColumnOrNull(requestedPath) != null) throw e
        throw ColumnNotFoundException(
            columnName = requestedPath.joinToString(),
            message = e.message ?: "Column '${requestedPath.joinToString()}' not found",
            cause = e,
        )
    }
    val actualType = resolvedColumn.data.type
    require(resolvedColumn.data.isSubtypeOf(type)) {
        "Column '${resolvedColumn.path.joinToString()}' has type '$actualType', which is not subtype of required '$type' type."
    }
    return this
}
