package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Returns the number of distinct rows in this [DataFrame].
 *
 * Compares rows based on the values in all columns and returns
 * the number of unique row combinations.
 *
 * See also:
 * - [distinct][DataFrame.distinct], which removes duplicate rows and returns a new [DataFrame].
 * - [count][DataFrame.count], which counts the number of rows satisfying a given predicate.
 *
 * For more information: {@include [DocumentationUrls.CountDistinct]}
 *
 * @return The number of distinct rows in this [DataFrame].
 */
public fun AnyFrame.countDistinct(): Int = countDistinct { all() }

/**
 * Returns number of distinct combinations of values in selected [columns\] in this [DataFrame].
 *
 * Compares values in the selected columns and returns
 * the number of unique values combinations.
 *
 * See also:
 * - [distinct][DataFrame.distinct], which removes duplicate rows and returns a new [DataFrame].
 * - [count][DataFrame.count], which counts the number of rows satisfying a given predicate.
 *
 * For more information: {@include [DocumentationUrls.CountDistinct]}
 *
 * ### This [countDistinct] overload
 */
@ExcludeFromSources
internal interface CountDistinctDocs

/**
 * {@include [CountDistinctDocs]}
 * {@include [SelectingColumns.Dsl]}
 *
 * #### Example
 *
 * ```kotlin
 * // Counts unique combinations of values in the "year" and "title" columns
 * // across all rows in the DataFrame
 * df.countDistinct { year and title }
 * ```
 *
 * @return The number of distinct rows in this [DataFrame].
 */
public fun <T, C> DataFrame<T>.countDistinct(columns: ColumnsSelector<T, C>): Int {
    val cols = get(columns)
    return indices.distinctBy { i -> cols.map { it[i] } }.size
}

/**
 * {@include [CountDistinctDocs]}
 * {@include [SelectingColumns.ColumnNames]}
 *
 * #### Example
 *
 * ```kotlin
 * // Counts unique combinations of values in the "year" and "title" columns
 * // across all rows in the DataFrame
 * df.countDistinct("year", "title")
 * ```
 *
 * @return The number of distinct rows in this [DataFrame].
 */
public fun <T> DataFrame<T>.countDistinct(vararg columns: String): Int = countDistinct { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.countDistinct(vararg columns: KProperty<C>): Int =
    countDistinct { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.countDistinct(vararg columns: AnyColumnReference): Int =
    countDistinct { columns.toColumnSet() }

// endregion
