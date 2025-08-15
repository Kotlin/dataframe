package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet

internal fun <T, C> DataFrame<T>.flattenImpl(
    columns: ColumnsSelector<T, C>,
    keepParentNameForColumns: Boolean = false,
    separator: String = "_",
): DataFrame<T> {
    val rootColumns = getColumnsWithPaths {
        columns.toColumnSet().filter { it.isColumnGroup() }.simplify()
    }
    val rootPrefixes = rootColumns.map { it.path }.toSet()
    val nameGenerators = rootPrefixes.map { it.dropLast() }.distinct().associateWith { path ->
        val usedNames = get(path).asColumnGroup()
            .columns()
            .filter { path + it.name() !in rootPrefixes }
            .map { it.name() }
        ColumnNameGenerator(usedNames)
    }

    fun getRootPrefix(path: ColumnPath) =
        (1 until path.size).asSequence().map { path.take(it) }.first { rootPrefixes.contains(it) }

    val result = move { rootPrefixes.toColumnSet().colsAtAnyDepth().filter { !it.isColumnGroup() } }
        .into {
            val targetPath = getRootPrefix(it.path).dropLast(1)
            val nameGen = nameGenerators[targetPath]!!
            val preferredName = if (keepParentNameForColumns) "${it.parentName}${separator}${it.name()}" else it.name()
            val name = nameGen.addUnique(preferredName)
            targetPath + name
        }
    return result
}
