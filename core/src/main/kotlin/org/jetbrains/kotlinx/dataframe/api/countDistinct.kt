package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.CountDistinctOnGroupByDocs.COLUMNS_PARAM
import org.jetbrains.kotlinx.dataframe.api.CountDistinctOnGroupByDocs.COLUMN_SELECTION_DSL
import org.jetbrains.kotlinx.dataframe.api.CountDistinctOnGroupByDocs.COMPARISON_OBJECT
import org.jetbrains.kotlinx.dataframe.api.CountDistinctOnGroupByDocs.EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.CountDistinctOnGroupByDocs.SCOPE
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateValue
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
public fun DataFrame<*>.countDistinct(): Int = countDistinct { all() }

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
internal typealias CountDistinctDocs = Nothing

/**
 * {@include [CountDistinctDocs]}
 * {@include [SelectingColumns.ColumnsSelectionDsl]}
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
 * {@include [SelectingColumns.ColumnNamesApi]}
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

// region GroupBy

/**
 * Aggregates this [GroupBy] by counting the number of distinct {@get [COMPARISON_OBJECT] rows} in each group.
 *
 * Compares rows in each group based on the values in {@get [SCOPE] all} columns.
 * Returns a new [DataFrame] where each row corresponds to a group.
 * The resulting [DataFrame] contains:
 * - the original group key columns,
 * - a new column (named [resultName\], default is `"countDistinct"`)
 * that contains the number of distinct {@get [COMPARISON_OBJECT] rows} in each group.
 *
 * See also:
 * - [aggregate][Grouped.aggregate], which aggregates a [GroupBy] using the provided statistics.
 * - [count][Grouped.count], which counts the number of rows in each group.
 * - [distinct][DataFrame.distinct], which removes duplicate rows and returns a new [DataFrame].
 * - [groupBy][DataFrame.groupBy], which groups the rows of a [DataFrame]
 * based on the values in one or more specified cols.
 *
 * For more information: {@include [DocumentationUrls.CountDistinct]}
 *
 * {@get [COLUMN_SELECTION_DSL]}
 *
 * ### Example
 * ```kotlin
 * {@get [EXAMPLE]}
 * ```
 *
 * @param [resultName\] The name of the result column that will store the number
 * of distinct {@get [COMPARISON_OBJECT] rows} in each group. Defaults to `"countDistinct"`.
 * @get [COLUMNS_PARAM]
 * @return A new [DataFrame] with group keys and corresponding numbers of distinct {@get [COMPARISON_OBJECT] rows}.
 */
@ExcludeFromSources
private interface CountDistinctOnGroupByDocs {
    typealias COMPARISON_OBJECT = Nothing
    typealias SCOPE = Nothing
    typealias EXAMPLE = Nothing
    typealias COLUMN_SELECTION_DSL = Nothing
    typealias COLUMNS_PARAM = Nothing
}

/**
 * @include [CountDistinctOnGroupByDocs]
 * @set [EXAMPLE]
 * // Counts the number of distinct rows for each city, returning
 * // a new DataFrame with columns "city" and "countDistinct"
 * df.groupBy { city }.countDistinct()
 */
@Refine
@Interpretable("GroupByCountDistinct0")
public fun <T> Grouped<T>.countDistinct(resultName: String = "countDistinct"): DataFrame<T> =
    countDistinct(resultName) { all() }

/**
 * @include [CountDistinctOnGroupByDocs]
 * @set [COMPARISON_OBJECT] combinations of values in the selected [columns]
 * @set [SCOPE] the selected
 * @set [COLUMN_SELECTION_DSL] {@include [SelectingColumns.ColumnsSelectionDsl]}
 * @set [EXAMPLE]
 * // Counts unique combinations of values in the "year" and "title" columns
 * // for each city, returning a new DataFrame with columns "city" and "countDistinct"
 * df.groupBy { city }.countDistinct { year and title }
 * @set [COLUMNS_PARAM] @param [columns\] The [ColumnsSelector] used to select columns
 * that will be considered for evaluating whether the rows are distinct.
 */
@Refine
@Interpretable("GroupByCountDistinct0")
public fun <T, C> Grouped<T>.countDistinct(
    resultName: String = "countDistinct",
    columns: ColumnsSelector<T, C>,
): DataFrame<T> =
    aggregateValue(resultName) {
        countDistinct(columns) default 0
    }

// endregion
