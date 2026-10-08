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
import org.jetbrains.kotlinx.dataframe.util.REORDER_COLUMNS_WITHOUT_IN_FRAME_COLUMNS
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Reorders the selected columns of the [DataFrame].
 *
 * This function does not reorder the columns immediately. It returns a [Reorder],
 * which serves as an intermediate step.
 * First, [cast][Reorder.cast] can optionally change the type of the selected columns for the column expression.
 * Then finish it with one of these methods to get a new [DataFrame]:
 * - [by][Reorder.by] — puts the selected columns in ascending order of the value of a [column expression][ColumnExpression];
 * - [byDesc][Reorder.byDesc] — puts them in descending order of that value;
 * - [byName][Reorder.byName] — puts them in lexicographic order of their names.
 *
 * @include [ReorderRulesSnippet]
 *
 * @include [ReorderEqualValuesSnippet]
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See [Selecting Columns][ReorderSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Reorder]}
 *
 * See also [reorderColumnsBy] and [reorderColumnsByName], which reorder all columns of the [DataFrame],
 * and [move], which moves columns to a new place.
 *
 * @include [DontConfuseWithSortBySnippet]
 */
internal interface ReorderDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetReorderOperationArg]}
     */
    typealias ReorderSelectingOptions = Nothing

    /**
     * ## Reorder Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * **[`reorder`][reorder]****`  {  `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`cast`**][Reorder.cast]**`<`**`ColumnType`**`>()`**` ]`
     *
     * {@include [Indent]}
     * __`.`__[**`by`**][Reorder.by]**`  {  `**`expression: `[`ColumnExpression`][ColumnExpression]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`byDesc`**][Reorder.byDesc]**`  {  `**`expression: `[`ColumnExpression`][ColumnExpression]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`byName`**][Reorder.byName]**`(`**`desc: `[`Boolean`][Boolean]` = false`**`)`**
     */
    typealias Grammar = Nothing
}

/**
 * The selected columns change places only with each other: they take the positions of the selected columns,
 * in the new order. Other columns stay where they are. The values in the columns do not change.
 *
 * If the selected columns belong to different [column groups][ColumnGroup], they are reordered within
 * their groups, so no column moves to another group.
 *
 * If exactly one [column group][ColumnGroup] is selected, its nested columns are reordered instead.
 * A column group selected together with other columns moves as a whole, and its nested columns keep their order.
 */
@ExcludeFromSources
private typealias ReorderRulesSnippet = Nothing

/** Columns for which the expression gives equal results keep their original order. */
@ExcludeFromSources
private typealias ReorderEqualValuesSnippet = Nothing

/** Don't confuse it with [sortBy][DataFrame.sortBy], which changes the order of rows, not columns. */
@ExcludeFromSources
private typealias DontConfuseWithSortBySnippet = Nothing

/** @return A [Reorder] to finish with [by][Reorder.by], [byDesc][Reorder.byDesc] or [byName][Reorder.byName]. */
@ExcludeFromSources
private typealias ReorderClauseReturnSnippet = Nothing

/** @return A new [DataFrame] with the selected columns reordered. */
@ExcludeFromSources
private typealias ReorderReturnSnippet = Nothing

/** @return A new [DataFrame] with all columns reordered. */
@ExcludeFromSources
private typealias ReorderColumnsReturnSnippet = Nothing

/** {@set [SelectingColumns.OPERATION] [reorder][reorder]} */
@ExcludeFromSources
private typealias SetReorderOperationArg = Nothing

/**
 * {@include [ReorderDocs]}
 * ### This Reorder Overload
 */
@ExcludeFromSources
private typealias CommonReorderDocs = Nothing

/**
 * An intermediate step of the [reorder] operation.
 *
 * It holds the selected columns and does not reorder anything by itself.
 * First, [cast][Reorder.cast] can optionally change the type of the selected columns.
 * Then finish it with [by][Reorder.by], [byDesc][Reorder.byDesc] or [byName][Reorder.byName]
 * to get a new [DataFrame] with the selected columns reordered.
 *
 * See [Grammar][ReorderDocs.Grammar] for more details.
 *
 * For more information: {@include [DocumentationUrls.Reorder]}
 *
 * @param [T] The schema marker of the [DataFrame].
 * @param [C] The type of the selected columns.
 */
public data class Reorder<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
    internal val inFrameColumns: Boolean,
) {
    /**
     * Changes the type of the selected columns to [R] for the next step of the [reorder] operation,
     * so the column expression in [by][Reorder.by] or [byDesc][Reorder.byDesc] can use functions of this type,
     * for example, `sum()` for [Int] columns.
     *
     * Nothing is converted: the columns and their values stay the same.
     * The type has to fit the values of the selected columns.
     *
     * See [Grammar][ReorderDocs.Grammar] for more details.
     *
     * For more information: {@include [DocumentationUrls.Reorder]}
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
 * @include [CommonReorderDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl] {@include [SetReorderOperationArg]}
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
 * @param [selector] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to reorder.
 * @include [ReorderClauseReturnSnippet]
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
 * @include [CommonReorderDocs]
 * @include [SelectingColumns.ColumnNamesApi] {@include [SetReorderOperationArg]}
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
 * @param [columns\] The [Column Names][String] used to select the columns of this [DataFrame] to reorder.
 * @include [ReorderClauseReturnSnippet]
 */
@StringApiInterpretable(interpreter = "Reorder", stringArgument = "columns", targetArgument = "selector")
public fun <T> DataFrame<T>.reorder(vararg columns: String): Reorder<T, *> = reorder { columns.toColumnSet() }

/**
 * Puts the columns selected by [reorder] in {@get [ReorderByDocs.ORDER]} order of the value of the [expression\]
 * and returns a new [DataFrame].
 *
 * The [expression\] is computed for every selected column.
 * The column is passed to it both as the receiver (`this`) and as the argument (`it`).
 * The result has to be [Comparable].
 *
 * @include [ReorderRulesSnippet]
 *
 * @include [ReorderEqualValuesSnippet]
 *
 * See [Grammar][ReorderDocs.Grammar] for more details.
 *
 * For more information: {@include [DocumentationUrls.Reorder]}
 */
@ExcludeFromSources
private interface ReorderByDocs {

    // "ascending" or "descending"
    @ExcludeFromSources
    typealias ORDER = Nothing
}

/**
 * @include [ReorderByDocs]
 *
 * See also [byDesc][Reorder.byDesc] and [byName][Reorder.byName].
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
 * @param [expression\] The [ColumnExpression] that gives the value to sort the selected columns by.
 * @include [ReorderReturnSnippet]
 * @set [ReorderByDocs.ORDER] ascending
 */
public fun <T, C, V : Comparable<V>> Reorder<T, C>.by(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(false, expression)

/**
 * Puts the columns selected by [reorder] in lexicographic order of their names and returns a new [DataFrame].
 *
 * It is a shortcut for `by { it.name() }`, or for `byDesc { it.name() }` when [desc\] is `true`.
 *
 * @include [ReorderRulesSnippet]
 *
 * See [Grammar][ReorderDocs.Grammar] for more details.
 *
 * For more information: {@include [DocumentationUrls.Reorder]}
 *
 * See also [by][Reorder.by] and [byDesc][Reorder.byDesc].
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
 * @param [desc\] If `true`, the columns are put in descending order. `false` by default.
 * @include [ReorderReturnSnippet]
 */
@Refine
@Interpretable("ByName")
public fun <T, C> Reorder<T, C>.byName(desc: Boolean = false): DataFrame<T> =
    if (desc) byDesc { it.name } else by { it.name }

/**
 * @include [ReorderByDocs]
 *
 * See also [by][Reorder.by] and [byName][Reorder.byName].
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
 * @param [expression\] The [ColumnExpression] that gives the value to sort the selected columns by.
 * @include [ReorderReturnSnippet]
 * @set [ReorderByDocs.ORDER] descending
 */
public fun <T, C, V : Comparable<V>> Reorder<T, C>.byDesc(expression: ColumnExpression<C, V>): DataFrame<T> =
    reorderImpl(true, expression)

/**
 * With [atAnyDepth\] set to `true` (the default), the columns inside every [column group][ColumnGroup]
 * are reordered too, each within its own group.
 * With [atAnyDepth\] set to `false`, only the top-level columns are reordered, and the nested columns keep their order.
 *
 * With [inFrameColumns\] set to `true`, the columns inside every [DataFrame] of a [frame column][FrameColumn]
 * are reordered too, each dataframe on its own and with the same [atAnyDepth\].
 * With [inFrameColumns\] set to `false` (the default), they keep their order:
 * like [colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth], [atAnyDepth\] does not look inside frame columns.
 *
 * The values in the columns do not change.
 */
@ExcludeFromSources
private typealias ReorderColumnsAtAnyDepthSnippet = Nothing

/**
 * @param [atAnyDepth\] If `true`, the columns inside column groups are reordered too. `true` by default.
 * @param [desc\] If `true`, the columns are put in descending order. `false` by default.
 * @param [inFrameColumns\] If `true`, the columns inside frame columns are reordered too. `false` by default.
 */
@ExcludeFromSources
private typealias ReorderColumnsParamsSnippet = Nothing

/**
 * Puts all columns of the [DataFrame] in order of the value of the [expression\]
 * and returns a new [DataFrame].
 *
 * The [expression\] is computed for every column.
 * The column is passed to it both as the receiver (`this`) and as the argument (`it`).
 * The result has to be [Comparable].
 * @include [ReorderEqualValuesSnippet]
 *
 * @include [ReorderColumnsAtAnyDepthSnippet]
 *
 * To reorder only some of the columns, use [reorder].
 *
 * For more information: {@include [DocumentationUrls.ReorderColumnsBy]}
 *
 * See also [reorderColumnsByName].
 *
 * @include [DontConfuseWithSortBySnippet]
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
 * @include [ReorderColumnsParamsSnippet]
 * @param [expression\] The expression that gives the value to sort the columns by.
 * @include [ReorderColumnsReturnSnippet]
 */
public fun <T, V : Comparable<V>> DataFrame<T>.reorderColumnsBy(
    atAnyDepth: Boolean = true,
    desc: Boolean = false,
    inFrameColumns: Boolean = false,
    expression: Selector<AnyCol, V>,
): DataFrame<T> =
    Reorder(
        df = this,
        columns = { if (atAnyDepth) colsAtAnyDepth() else all() },
        inFrameColumns = inFrameColumns,
    ).reorderImpl(desc, expression, unwrapSingleColumnGroup = atAnyDepth)

/**
 * Puts all columns of the [DataFrame] in lexicographic order of their names and returns a new [DataFrame].
 *
 * It is a shortcut for [reorderColumnsBy] with `{ name() }`.
 *
 * @include [ReorderColumnsAtAnyDepthSnippet]
 *
 * To reorder only some of the columns, use [reorder] with [byName][Reorder.byName].
 *
 * For more information: {@include [DocumentationUrls.ReorderColumnsByName]}
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
 * @include [ReorderColumnsParamsSnippet]
 * @include [ReorderColumnsReturnSnippet]
 */
@Refine
@Interpretable("ReorderColumnsByName")
public fun <T> DataFrame<T>.reorderColumnsByName(
    atAnyDepth: Boolean = true,
    desc: Boolean = false,
    inFrameColumns: Boolean = false,
): DataFrame<T> = reorderColumnsBy(atAnyDepth, desc, inFrameColumns) { name() }

// endregion

// region binary compatibility

@Deprecated(REORDER_COLUMNS_WITHOUT_IN_FRAME_COLUMNS, level = DeprecationLevel.HIDDEN)
public fun <T, V : Comparable<V>> DataFrame<T>.reorderColumnsBy(
    atAnyDepth: Boolean = true,
    desc: Boolean = false,
    expression: Selector<AnyCol, V>,
): DataFrame<T> = reorderColumnsBy(atAnyDepth, desc, inFrameColumns = false, expression)

@Deprecated(REORDER_COLUMNS_WITHOUT_IN_FRAME_COLUMNS, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.reorderColumnsByName(atAnyDepth: Boolean = true, desc: Boolean = false): DataFrame<T> =
    reorderColumnsByName(atAnyDepth, desc, inFrameColumns = false)

// endregion
