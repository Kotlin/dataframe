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
 * Returns the number of distinct rows in this [<code>DataFrame</code>][DataFrame].
 *
 * Compares rows based on the values in all columns and returns
 * the number of unique row combinations.
 *
 * See also:
 * - [<code>distinct</code>][DataFrame.distinct], which removes duplicate rows and returns a new [<code>DataFrame</code>][DataFrame].
 * - [<code>count</code>][DataFrame.count], which counts the number of rows satisfying a given predicate.
 *
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 * @return The number of distinct rows in this [<code>DataFrame</code>][DataFrame].
 */
public fun DataFrame<*>.countDistinct(): Int = countDistinct { all() }

/**
 * Returns the number of distinct combinations of values in selected [columns] in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Compares values in the selected columns and returns
 * the number of unique values combinations.
 *
 * See also:
 * - [<code>distinct</code>][org.jetbrains.kotlinx.dataframe.DataFrame.distinct], which removes duplicate rows and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>count</code>][org.jetbrains.kotlinx.dataframe.DataFrame.count], which counts the number of rows satisfying a given predicate.
 *
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 * ### This [<code>countDistinct</code>][org.jetbrains.kotlinx.dataframe.api.countDistinct] overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### Example
 *
 * ```kotlin
 * // Counts unique combinations of values in the "year" and "title" columns
 * // across all rows in the DataFrame
 * df.countDistinct { year and title }
 * ```
 *
 * @return The number of distinct rows in this [<code>DataFrame</code>][DataFrame].
 */
public fun <T, C> DataFrame<T>.countDistinct(columns: ColumnsSelector<T, C>): Int {
    val cols = get(columns)
    return indices.distinctBy { i -> cols.map { it[i] } }.size
}

/**
 * Returns the number of distinct combinations of values in selected [columns] in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Compares values in the selected columns and returns
 * the number of unique values combinations.
 *
 * See also:
 * - [<code>distinct</code>][org.jetbrains.kotlinx.dataframe.DataFrame.distinct], which removes duplicate rows and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>count</code>][org.jetbrains.kotlinx.dataframe.DataFrame.count], which counts the number of rows satisfying a given predicate.
 *
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 * ### This [<code>countDistinct</code>][org.jetbrains.kotlinx.dataframe.api.countDistinct] overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### Example
 *
 * ```kotlin
 * // Counts unique combinations of values in the "year" and "title" columns
 * // across all rows in the DataFrame
 * df.countDistinct("year", "title")
 * ```
 *
 * @return The number of distinct rows in this [<code>DataFrame</code>][DataFrame].
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
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by counting the number of distinct rows in each group.
 *
 * Compares rows in each group based on the values in all columns.
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each row corresponds to a group.
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] contains:
 * - the original group key columns,
 * - a new column (named [resultName], default is `"countDistinct"`)
 * that contains the number of distinct rows in each group.
 *
 * See also:
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate], which aggregates a [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] using the provided statistics.
 * - [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.count], which counts the number of rows in each group.
 * - [<code>distinct</code>][org.jetbrains.kotlinx.dataframe.DataFrame.distinct], which removes duplicate rows and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy], which groups the rows of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * based on the values in one or more specified cols.
 *
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Counts the number of distinct rows for each city, returning
 * // a new DataFrame with columns "city" and "countDistinct"
 * df.groupBy { city }.countDistinct()
 * ```
 *
 * @param [resultName] The name of the result column that will store the number
 * of distinct rows in each group. Defaults to `"countDistinct"`.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with group keys and corresponding numbers of distinct rows.
 */
@Refine
@Interpretable("GroupByCountDistinct0")
public fun <T> Grouped<T>.countDistinct(resultName: String = "countDistinct"): DataFrame<T> =
    countDistinct(resultName) { all() }

/**
 * Aggregates this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] by counting the number of distinct combinations of values in the selected [<code>columns</code>][columns] in each group.
 *
 * Compares rows in each group based on the values in the selected columns.
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where each row corresponds to a group.
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] contains:
 * - the original group key columns,
 * - a new column (named [resultName], default is `"countDistinct"`)
 * that contains the number of distinct combinations of values in the selected [<code>columns</code>][columns] in each group.
 *
 * See also:
 * - [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate], which aggregates a [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] using the provided statistics.
 * - [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.count], which counts the number of rows in each group.
 * - [<code>distinct</code>][org.jetbrains.kotlinx.dataframe.DataFrame.distinct], which removes duplicate rows and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy], which groups the rows of a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * based on the values in one or more specified cols.
 *
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Example
 * ```kotlin
 * // Counts unique combinations of values in the "year" and "title" columns
 * // for each city, returning a new DataFrame with columns "city" and "countDistinct"
 * df.groupBy { city }.countDistinct { year and title }
 * ```
 *
 * @param [resultName] The name of the result column that will store the number
 * of distinct combinations of values in the selected [<code>columns</code>][columns] in each group. Defaults to `"countDistinct"`.
 * @param [columns] The [<code>ColumnsSelector</code>][ColumnsSelector] used to select columns
 * that will be considered for evaluating whether the rows are distinct.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with group keys and corresponding numbers of distinct combinations of values in the selected [<code>columns</code>][columns].
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
