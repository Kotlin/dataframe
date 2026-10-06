package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnExpression
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.reorderImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Reorders the selected columns of the [<code>DataFrame</code>][DataFrame].
 *
 * This function does not reorder the columns immediately. It returns a [<code>Reorder</code>][Reorder],
 * which serves as an intermediate step. Finish it with one of these methods to get a new [<code>DataFrame</code>][DataFrame]:
 * - [<code>by</code>][Reorder.by] — puts the selected columns in ascending order of the value of a [<code>column expression</code>][ColumnExpression];
 * - [<code>byDesc</code>][Reorder.byDesc] — puts them in descending order of that value;
 * - [<code>byName</code>][Reorder.byName] — puts them in order of their names.
 *
 * Before that, [<code>cast</code>][Reorder.cast] can change the type of the selected columns for the column expression.
 *
 * The selected columns change places only with each other: they take the positions of the selected columns,
 * in the new order. Other columns stay where they are. The values in the columns do not change.
 *
 * If the selected columns belong to different [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], they are reordered within
 * their groups, so no column moves to another group.
 *
 * If exactly one [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is selected, its nested columns are reordered instead.
 * A column group selected together with other columns moves as a whole, and its nested columns keep their order.
 *
 * Columns that get equal values keep their original order.
 *
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][ReorderSelectingOptions].
 *
 * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
 *
 * See also [<code>reorderColumnsBy</code>][reorderColumnsBy] and [<code>reorderColumnsByName</code>][reorderColumnsByName], which reorder all columns of the [<code>DataFrame</code>][DataFrame],
 * and [<code>move</code>][move], which moves columns to a new place.
 *
 * Don't confuse it with [<code>sortBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy], which changes the order of rows, not columns.
 */
internal interface ReorderDocs {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
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
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>reorder</code>][org.jetbrains.kotlinx.dataframe.api.reorder]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>reorder</code>][org.jetbrains.kotlinx.dataframe.api.reorder]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>reorder</code>][org.jetbrains.kotlinx.dataframe.api.reorder]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>reorder</code>][org.jetbrains.kotlinx.dataframe.api.reorder]`("length", "age")`
     *
     *
     *
     */
    typealias ReorderSelectingOptions = Nothing

    /**
     * ## Reorder Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * **[<code>`reorder`</code>][reorder]****`  {  `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`cast`**</code>][Reorder.cast]**`<`**`ColumnType`**`>()`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`by`**</code>][Reorder.by]**`  {  `**`expression: `[<code>`ColumnExpression`</code>][ColumnExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`byDesc`**</code>][Reorder.byDesc]**`  {  `**`expression: `[<code>`ColumnExpression`</code>][ColumnExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`byName`**</code>][Reorder.byName]**`(`**`desc: `[<code>`Boolean`</code>][Boolean]` = false`**`)`**
     */
    typealias Grammar = Nothing
}

/**
 * An intermediate step of the [<code>reorder</code>][reorder] operation.
 *
 * It holds the selected columns and does not reorder anything by itself.
 * Finish it with [<code>by</code>][Reorder.by], [<code>byDesc</code>][Reorder.byDesc] or [<code>byName</code>][Reorder.byName]
 * to get a new [<code>DataFrame</code>][DataFrame] with the selected columns reordered.
 * Before that, [<code>cast</code>][Reorder.cast] can change the type of the selected columns.
 *
 * See [<code>Grammar</code>][ReorderDocs.Grammar] for more details.
 *
 * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
 *
 * @param [T] The schema marker of the [<code>DataFrame</code>][DataFrame].
 * @param [C] The type of the selected columns.
 */
public data class Reorder<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
    internal val inFrameColumns: Boolean,
) {
    /**
     * Changes the type of the selected columns to [<code>R</code>][R] for the next step of the [<code>reorder</code>][reorder] operation,
     * so the column expression in [<code>by</code>][Reorder.by] or [<code>byDesc</code>][Reorder.byDesc] can use functions of this type,
     * for example, `sum()` for [<code>Int</code>][Int] columns.
     *
     * Nothing is converted: the columns and their values stay the same.
     * The type has to fit the values of the selected columns.
     *
     * See [<code>Grammar</code>][ReorderDocs.Grammar] for more details.
     *
     * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
     *
     * ### Example:
     * ```kotlin
     * val df = dataFrameOf("c", "d", "a", "b")(
     *     3, 4, 1, 2,
     *     1, 1, 1, 1,
     * )
     * // "b" has a smaller sum than "d", so they change places
     * df.reorder("d", "b").cast<Int>().by { sum() } // [c, b, a, d]
     * ```
     * @param [R] The new type of the selected columns.
     */
    public fun <R> cast(): Reorder<T, R> = this as Reorder<T, R>
}

/**
 * Reorders the selected columns of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not reorder the columns immediately. It returns a [<code>Reorder</code>][org.jetbrains.kotlinx.dataframe.api.Reorder],
 * which serves as an intermediate step. Finish it with one of these methods to get a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * - [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.by] — puts the selected columns in ascending order of the value of a [<code>column expression</code>][org.jetbrains.kotlinx.dataframe.ColumnExpression];
 * - [<code>byDesc</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byDesc] — puts them in descending order of that value;
 * - [<code>byName</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byName] — puts them in order of their names.
 *
 * Before that, [<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.cast] can change the type of the selected columns for the column expression.
 *
 * The selected columns change places only with each other: they take the positions of the selected columns,
 * in the new order. Other columns stay where they are. The values in the columns do not change.
 *
 * If the selected columns belong to different [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], they are reordered within
 * their groups, so no column moves to another group.
 *
 * If exactly one [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is selected, its nested columns are reordered instead.
 * A column group selected together with other columns moves as a whole, and its nested columns keep their order.
 *
 * Columns that get equal values keep their original order.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReorderDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.ReorderDocs.ReorderSelectingOptions].
 *
 * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
 *
 * See also [<code>reorderColumnsBy</code>][org.jetbrains.kotlinx.dataframe.api.reorderColumnsBy] and [<code>reorderColumnsByName</code>][org.jetbrains.kotlinx.dataframe.api.reorderColumnsByName], which reorder all columns of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move], which moves columns to a new place.
 *
 * Don't confuse it with [<code>sortBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy], which changes the order of rows, not columns.
 * ### This Reorder Overload
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
 * ### Examples:
 * ```kotlin
 * // df: [name[firstName, lastName], age, city, weight, isHappy]
 *
 * // Put the columns from "age" to "isHappy" in order of their names
 * df.reorder { age..isHappy }.byName() // [name, age, city, isHappy, weight]
 *
 * // Put the nested columns of the "name" column group in descending order of their names
 * df.reorder { name }.byName(desc = true) // [name[lastName, firstName], age, city, weight, isHappy]
 *
 * // Columns from different groups are reordered within their own groups
 * df.reorder { age and city and name.firstName and name.lastName }.byName(desc = true)
 * // [name[lastName, firstName], city, age, weight, isHappy]
 * ```
 * @param [selector] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to reorder.
 * @return A [<code>Reorder</code>][org.jetbrains.kotlinx.dataframe.api.Reorder] to finish with [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.by], [<code>byDesc</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byDesc] or [<code>byName</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byName].
 */
@Interpretable("Reorder")
public fun <T, C> DataFrame<T>.reorder(selector: ColumnsSelector<T, C>): Reorder<T, C> = Reorder(this, selector, false)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.reorder(vararg columns: ColumnReference<C>): Reorder<T, C> =
    reorder { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.reorder(vararg columns: KProperty<C>): Reorder<T, C> = reorder { columns.toColumnSet() }

/**
 * Reorders the selected columns of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not reorder the columns immediately. It returns a [<code>Reorder</code>][org.jetbrains.kotlinx.dataframe.api.Reorder],
 * which serves as an intermediate step. Finish it with one of these methods to get a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * - [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.by] — puts the selected columns in ascending order of the value of a [<code>column expression</code>][org.jetbrains.kotlinx.dataframe.ColumnExpression];
 * - [<code>byDesc</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byDesc] — puts them in descending order of that value;
 * - [<code>byName</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byName] — puts them in order of their names.
 *
 * Before that, [<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.cast] can change the type of the selected columns for the column expression.
 *
 * The selected columns change places only with each other: they take the positions of the selected columns,
 * in the new order. Other columns stay where they are. The values in the columns do not change.
 *
 * If the selected columns belong to different [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], they are reordered within
 * their groups, so no column moves to another group.
 *
 * If exactly one [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is selected, its nested columns are reordered instead.
 * A column group selected together with other columns moves as a whole, and its nested columns keep their order.
 *
 * Columns that get equal values keep their original order.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReorderDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.ReorderDocs.ReorderSelectingOptions].
 *
 * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
 *
 * See also [<code>reorderColumnsBy</code>][org.jetbrains.kotlinx.dataframe.api.reorderColumnsBy] and [<code>reorderColumnsByName</code>][org.jetbrains.kotlinx.dataframe.api.reorderColumnsByName], which reorder all columns of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move], which moves columns to a new place.
 *
 * Don't confuse it with [<code>sortBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy], which changes the order of rows, not columns.
 * ### This Reorder Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Examples:
 * ```kotlin
 * // df: [name[firstName, lastName], age, city, weight, isHappy]
 *
 * // Put "age", "name" and "city" in descending order of the length of their names
 * df.reorder("age", "name", "city").byDesc { it.name().length } // [name, city, age, weight, isHappy]
 *
 * // Put the nested columns of the "name" column group in descending order of their names
 * df.reorder("name").byName(desc = true) // [name[lastName, firstName], age, city, weight, isHappy]
 * ```
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to reorder.
 * @return A [<code>Reorder</code>][org.jetbrains.kotlinx.dataframe.api.Reorder] to finish with [<code>by</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.by], [<code>byDesc</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byDesc] or [<code>byName</code>][org.jetbrains.kotlinx.dataframe.api.Reorder.byName].
 */
@StringApiInterpretable(interpreter = "Reorder", stringArgument = "columns", targetArgument = "selector")
public fun <T> DataFrame<T>.reorder(vararg columns: String): Reorder<T, *> = reorder { columns.toColumnSet() }

/**
 * Puts the columns selected by [<code>reorder</code>][org.jetbrains.kotlinx.dataframe.api.reorder] in ascending order of the value of the [expression]
 * and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * The [expression] is computed for every selected column.
 * The column is passed to it both as the receiver (`this`) and as the argument (`it`).
 * The result has to be [<code>Comparable</code>][Comparable].
 *
 * The selected columns change places only with each other: they take the positions of the selected columns,
 * in the new order. Other columns stay where they are. The values in the columns do not change.
 *
 * If the selected columns belong to different [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], they are reordered within
 * their groups, so no column moves to another group.
 *
 * If exactly one [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is selected, its nested columns are reordered instead.
 * A column group selected together with other columns moves as a whole, and its nested columns keep their order.
 *
 * Columns that get equal values keep their original order.
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReorderDocs.Grammar] for more details.
 *
 * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
 *
 * See also [<code>byDesc</code>][Reorder.byDesc] and [<code>byName</code>][Reorder.byName].
 *
 * ### Example:
 * ```kotlin
 * val df = dataFrameOf("c", "d", "a", "b")(
 *     3, 4, 1, 2,
 *     1, 1, 1, 1,
 * )
 * // "b" has a smaller sum than "d", so they change places
 * df.reorder("d", "b").cast<Int>().by { sum() } // [c, b, a, d]
 * ```
 * @param [expression] The [<code>ColumnExpression</code>][ColumnExpression] that gives the value to sort the selected columns by.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected columns reordered.
 */
public fun <T, C, V : Comparable<V>> Reorder<T, C>.by(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(false, expression)

/**
 * Puts the columns selected by [<code>reorder</code>][reorder] in order of their names and returns a new [<code>DataFrame</code>][DataFrame].
 *
 * It is a shortcut for `by { it.name() }`, or for `byDesc { it.name() }` when [desc] is `true`.
 *
 * The selected columns change places only with each other: they take the positions of the selected columns,
 * in the new order. Other columns stay where they are. The values in the columns do not change.
 *
 * If the selected columns belong to different [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], they are reordered within
 * their groups, so no column moves to another group.
 *
 * If exactly one [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is selected, its nested columns are reordered instead.
 * A column group selected together with other columns moves as a whole, and its nested columns keep their order.
 *
 * See [<code>Grammar</code>][ReorderDocs.Grammar] for more details.
 *
 * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
 *
 * See also [<code>by</code>][Reorder.by] and [<code>byDesc</code>][Reorder.byDesc].
 *
 * ### Examples:
 * ```kotlin
 * // df: [name[firstName, lastName], age, city, weight, isHappy]
 *
 * // Put the columns from "age" to "isHappy" in order of their names
 * df.reorder { age..isHappy }.byName() // [name, age, city, isHappy, weight]
 *
 * // Put the nested columns of the "name" column group in descending order of their names
 * df.reorder { name }.byName(desc = true) // [name[lastName, firstName], age, city, weight, isHappy]
 * ```
 * @param [desc] If `true`, the columns are put in descending order. `false` by default.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected columns reordered.
 */
@Refine
@Interpretable("ByName")
public fun <T, C> Reorder<T, C>.byName(desc: Boolean = false): DataFrame<T> =
    if (desc) byDesc { it.name } else by { it.name }

/**
 * Puts the columns selected by [<code>reorder</code>][org.jetbrains.kotlinx.dataframe.api.reorder] in descending order of the value of the [expression]
 * and returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * The [expression] is computed for every selected column.
 * The column is passed to it both as the receiver (`this`) and as the argument (`it`).
 * The result has to be [<code>Comparable</code>][Comparable].
 *
 * The selected columns change places only with each other: they take the positions of the selected columns,
 * in the new order. Other columns stay where they are. The values in the columns do not change.
 *
 * If the selected columns belong to different [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], they are reordered within
 * their groups, so no column moves to another group.
 *
 * If exactly one [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is selected, its nested columns are reordered instead.
 * A column group selected together with other columns moves as a whole, and its nested columns keep their order.
 *
 * Columns that get equal values keep their original order.
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReorderDocs.Grammar] for more details.
 *
 * For more information: [See `reorder` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html)
 *
 * See also [<code>by</code>][Reorder.by] and [<code>byName</code>][Reorder.byName].
 *
 * ### Examples:
 * ```kotlin
 * val df = dataFrameOf("c", "d", "a", "b")(
 *     3, 4, 1, 2,
 *     1, 1, 1, 1,
 * )
 * // "b" has a larger sum than "a", so they change places
 * df.reorder("a", "b").cast<Int>().byDesc { sum() } // [c, d, b, a]
 * ```
 * ```kotlin
 * // df: [name[firstName, lastName], age, city, weight, isHappy]
 *
 * // "name" and "city" have names of the same length, so "name" stays before "city"
 * df.reorder { age and name and city }.byDesc { it.name().length } // [name, city, age, weight, isHappy]
 * ```
 * @param [expression] The [<code>ColumnExpression</code>][ColumnExpression] that gives the value to sort the selected columns by.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the selected columns reordered.
 */
public fun <T, C, V : Comparable<V>> Reorder<T, C>.byDesc(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(true, expression)

/**
 * Puts all columns of the [<code>DataFrame</code>][DataFrame] in order of the value of the [expression]
 * and returns a new [<code>DataFrame</code>][DataFrame].
 *
 * The [expression] is computed for every column.
 * The column is passed to it both as the receiver (`this`) and as the argument (`it`).
 * The result has to be [<code>Comparable</code>][Comparable].
 * Columns that get equal values keep their original order.
 *
 * With [atAnyDepth] set to `true` (the default), the columns inside every [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * and inside every [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] of a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] are reordered too,
 * each within its own group or dataframe.
 * With [atAnyDepth] set to `false`, only the top-level columns are reordered, and the nested columns keep their order.
 *
 * The values in the columns do not change.
 *
 * To reorder only some of the columns, use [<code>reorder</code>][reorder].
 *
 * For more information: [See `reorderColumnsBy` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html#reordercolumnsby)
 *
 * See also [<code>reorderColumnsByName</code>][reorderColumnsByName].
 *
 * Don't confuse it with [<code>sortBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy], which changes the order of rows, not columns.
 *
 * ### Examples:
 * ```kotlin
 * // df: [name[firstName, lastName], age, city, weight, isHappy]
 *
 * // Put the columns at any depth in order of the length of their names
 * df.reorderColumnsBy { name().length } // [age, name[lastName, firstName], city, weight, isHappy]
 *
 * // Put only the top-level columns in order of the length of their names
 * df.reorderColumnsBy(atAnyDepth = false) { name().length } // [age, name[firstName, lastName], city, weight, isHappy]
 * ```
 * @param [atAnyDepth] If `true`, the nested columns are reordered too. `true` by default.
 * @param [desc] If `true`, the columns are put in descending order. `false` by default.
 * @param [expression] The expression that gives the value to sort the columns by.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with all columns reordered.
 */
public fun <T, V : Comparable<V>> DataFrame<T>.reorderColumnsBy(
    atAnyDepth: Boolean = true,
    desc: Boolean = false,
    expression: Selector<AnyCol, V>,
): DataFrame<T> =
    Reorder(
        df = this,
        columns = { if (atAnyDepth) colsAtAnyDepth() else all() },
        inFrameColumns = atAnyDepth,
    ).reorderImpl(desc, expression, reorderNestedColumnsOfSingleGroup = atAnyDepth)

/**
 * Puts all columns of the [<code>DataFrame</code>][DataFrame] in order of their names and returns a new [<code>DataFrame</code>][DataFrame].
 *
 * It is a shortcut for [<code>reorderColumnsBy</code>][reorderColumnsBy] with `{ name() }`.
 *
 * With [atAnyDepth] set to `true` (the default), the columns inside every [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 * and inside every [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] of a [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] are reordered too,
 * each within its own group or dataframe.
 * With [atAnyDepth] set to `false`, only the top-level columns are reordered, and the nested columns keep their order.
 *
 * The values in the columns do not change.
 *
 * To reorder only some of the columns, use [<code>reorder</code>][reorder] with [<code>byName</code>][Reorder.byName].
 *
 * For more information: [See `reorderColumnsByName` on the documentation website.](https://kotlin.github.io/dataframe/reorder.html#reordercolumnsbyname)
 *
 * ### Examples:
 * ```kotlin
 * // df: [name[firstName, lastName], age, city, weight, isHappy]
 *
 * // The nested columns of "name" are reordered too
 * df.reorderColumnsByName(desc = true) // [weight, name[lastName, firstName], isHappy, city, age]
 *
 * // The nested columns of "name" keep their order
 * df.reorderColumnsByName(atAnyDepth = false, desc = true) // [weight, name[firstName, lastName], isHappy, city, age]
 * ```
 * @param [atAnyDepth] If `true`, the nested columns are reordered too. `true` by default.
 * @param [desc] If `true`, the columns are put in descending order. `false` by default.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with all columns reordered.
 */
@Refine
@Interpretable("ReorderColumnsByName")
public fun <T> DataFrame<T>.reorderColumnsByName(atAnyDepth: Boolean = true, desc: Boolean = false): DataFrame<T> =
    reorderColumnsBy(atAnyDepth, desc) { name() }

// endregion
