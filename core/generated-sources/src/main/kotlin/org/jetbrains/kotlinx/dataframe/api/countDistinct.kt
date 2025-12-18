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
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 * @return The number of distinct rows in this [DataFrame].
 */
public fun AnyFrame.countDistinct(): Int = countDistinct { all() }

/**
 * Returns number of distinct combinations of values in selected [columns] in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Compares values in the selected columns and returns
 * the number of unique values combinations.
 *
 * See also:
 * - [distinct][org.jetbrains.kotlinx.dataframe.DataFrame.distinct], which removes duplicate rows and returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [count][org.jetbrains.kotlinx.dataframe.DataFrame.count], which counts the number of rows satisfying a given predicate.
 *
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 * ### This [countDistinct][org.jetbrains.kotlinx.dataframe.api.countDistinct] overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
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
 * @return The number of distinct rows in this [DataFrame].
 */
public fun <T, C> DataFrame<T>.countDistinct(columns: ColumnsSelector<T, C>): Int {
    val cols = get(columns)
    return indices.distinctBy { i -> cols.map { it[i] } }.size
}

/**
 * Returns number of distinct combinations of values in selected [columns] in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Compares values in the selected columns and returns
 * the number of unique values combinations.
 *
 * See also:
 * - [distinct][org.jetbrains.kotlinx.dataframe.DataFrame.distinct], which removes duplicate rows and returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [count][org.jetbrains.kotlinx.dataframe.DataFrame.count], which counts the number of rows satisfying a given predicate.
 *
 * For more information: [See `countDistinct` on the documentation website.](https://kotlin.github.io/dataframe/countdistinct.html)
 *
 * ### This [countDistinct][org.jetbrains.kotlinx.dataframe.api.countDistinct] overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
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

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> DataFrame<T>.countDistinct(vararg columns: KProperty<C>): Int =
    countDistinct { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.countDistinct(vararg columns: AnyColumnReference): Int =
    countDistinct { columns.toColumnSet() }

// endregion
