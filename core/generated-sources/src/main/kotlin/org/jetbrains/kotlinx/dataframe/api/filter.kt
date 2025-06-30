package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.getTrueIndices
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/**
 * Returns a new [DataColumn] containing only the elements that match the given [predicate].
 *
 * @param predicate the condition used to filter the elements in the DataColumn.
 * @return a new DataColumn containing elements that satisfy the predicate.
 */
public inline fun <T> DataColumn<T>.filter(predicate: Predicate<T>): DataColumn<T> =
    indices
        .filter { predicate(get(it)) }
        .let { get(it) }

// endregion

// region DataFrame

/**
 * Filters the rows of this [DataFrame] based on the provided [RowFilter].
 * Returns a new [DataFrame] containing only the rows that satisfy the given [predicate].
 *
 * A [RowFilter] provides each row as a lambda argument, allowing you to define filtering logic
 * using a [Boolean] condition.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `filter` on the documentation website.](https://kotlin.github.io/dataframe/filter.html)
 *
 * See also:
 *  - [filterBy], which filters rows based on the values in a given [Boolean] column.
 *  - [drop][DataFrame.drop], which drops rows based on values within the row.
 *
 * ### Example
 * ```kotlin
 * // Select rows where the value in the "age" column is greater than 18
 * // and the "name/firstName" column starts with 'A'
 * df.filter { age > 18 && name.firstName.startsWith("A") }
 * ```
 *
 * @param predicate A lambda that takes a row (twice for compatibility) and returns `true`
 * if the row should be included in the result.
 * @return A new [DataFrame] containing only the rows that satisfy the predicate.
 */
public inline fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
    indices().filter {
        val row = get(it)
        predicate(row, row)
    }.let { get(it) }

/**
 * Filters the rows of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] based on the [Boolean] values in the specified [column][org.jetbrains.kotlinx.dataframe.api.column].
 *
 * Returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] containing only the rows where the value in the given [column][org.jetbrains.kotlinx.dataframe.api.column] is `true`.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `filter` on the documentation website.](https://kotlin.github.io/dataframe/filter.html)
 *
 * See also: [filter][org.jetbrains.kotlinx.dataframe.api.filter], which allows filtering rows based on values within the row.
 *
 * ### This Gather Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
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
 * ### Examples
 * ```kotlin
 * // Filter rows by the "isHappy" column
 * df.filterBy { isHappy }
 *
 * // Filter rows by a single `Boolean` column
 * df.filterBy { colsOf<Boolean>().single() }
 * ```
 *
 * @param column A [ColumnSelector] that selects the Boolean column to use for filtering.
 *               Only rows where the value in this column is `true` will be included.
 * @return A new [DataFrame] containing only the rows where the selected column is `true`.
 */
public fun <T> DataFrame<T>.filterBy(column: ColumnSelector<T, Boolean>): DataFrame<T> =
    getRows(getColumn(column).toList().getTrueIndices())

/**
 * Filters the rows of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] based on the [Boolean] values in the specified [column][org.jetbrains.kotlinx.dataframe.api.column].
 *
 * Returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] containing only the rows where the value in the given [column][org.jetbrains.kotlinx.dataframe.api.column] is `true`.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `filter` on the documentation website.](https://kotlin.github.io/dataframe/filter.html)
 *
 * See also: [filter][org.jetbrains.kotlinx.dataframe.api.filter], which allows filtering rows based on values within the row.
 *
 * ### This Gather Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // Filter rows by the "isHappy" column
 * df.filterBy("isHappy")
 * ```
 *
 * @param column The name of the `Boolean` column to use for filtering.
 *               Only rows where the value in this column is `true` will be included.
 * @return A new [DataFrame] containing only the rows where the specified column is `true`.
 */
public fun <T> DataFrame<T>.filterBy(column: String): DataFrame<T> = filterBy { column.toColumnOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.filterBy(column: ColumnReference<Boolean>): DataFrame<T> = filterBy { column }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.filterBy(column: KProperty<Boolean>): DataFrame<T> = filterBy { column.toColumnAccessor() }

// endregion

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> =
    { this@filter(it).asColumnSet().filter(predicate) }

// region ColumnsSelectionDsl

/**
 * ## Filter [ColumnSet] [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FilterColumnsSelectionDsl {

    /**
     * ## Filter [ColumnSet] Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[`ColumnFilter`][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`filter`**][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]**` { `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** __`.`__[**`filter`**][ColumnsSelectionDsl.filter] */
        public interface ColumnSetName
    }

    /**
     * ## Filter [ColumnSet]
     *
     * Creates a subset of columns ([ColumnSet]) from the current [ColumnSet] that
     * adhere to the given [predicate].
     *
     * Aside from calling [filter][ColumnSet.filter] directly, you can also use the [get][ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][ColumnsSelectionDsl.cols] but operates identically.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[`remove`][DataFrame.remove]`  {  `[`all`][ColumnsSelectionDsl.all]`().`[`filter`][ColumnSet.filter]` { it.`[`hasNulls`][DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[`colsOf`][colsOf]` call:`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][colsOf]`<`[`String`][String]`>().`[`filter`][ColumnSet.filter]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][SingleColumn.colsOf]`<`[`String`][String]`>()`[`[`][ColumnsSelectionDsl.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * @param [predicate] A [ColumnFilter function][ColumnFilter] that takes a [ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.cols]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.filter(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>) as TransformableColumnSet<C>
}

// endregion
