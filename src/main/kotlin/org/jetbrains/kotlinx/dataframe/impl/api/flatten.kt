package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.isGroup

internal fun <T, C> DataFrame<T>.flattenImpl(
    separator: CharSequence,
    selector: ColumnsSelector<T, C>
): DataFrame<T> {
    val rootColumns = getColumnsWithPaths { selector.toColumns().filter { it.isGroup() }.top() }
    val rootPrefixes = rootColumns.map { it.path }.toSet()
    val nameGenerator = ColumnNameGenerator()

    fun getRootPrefix(path: ColumnPath) =
        (1 until path.size).asSequence().map { path.take(it) }.first { rootPrefixes.contains(it) }

    val result = move { rootColumns.toColumnSet().dfs { !it.isGroup() } }
        .into {
            val prefix = getRootPrefix(it.path).dropLast(1)
            val desiredName = it.path.drop(prefix.size).joinToString(separator)
            val name = nameGenerator.addUnique(desiredName)
            prefix + name
        }
    return result
}
