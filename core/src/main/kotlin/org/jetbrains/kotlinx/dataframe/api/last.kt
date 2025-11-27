package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.RowFilterDescription
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/**
 * Returns the last value in this [DataColumn].
 *
 * @see [lastOrNull]
 * @see [first]
 * @see [take]
 * @see [takeLast]
 *
 * @return The last value in this [DataColumn].
 *
 * @throws [IndexOutOfBoundsException] if the [DataColumn] is empty.
 */
public fun <T> DataColumn<T>.last(): T = get(size - 1)

/**
 * Returns the last value in this [DataColumn]. If the [DataColumn] is empty, returns `null`.
 *
 * @see [last]
 * @see [first]
 * @see [take]
 * @see [takeLast]
 *
 * @return The last value in this [DataColumn], or `null` if the [DataColumn] is empty.
 */
public fun <T> DataColumn<T>.lastOrNull(): T? = if (size > 0) last() else null

/**
 * Returns the last value in this [DataColumn] that matches the given [predicate].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the amount of the most recent financial transaction over 100 euros
 * df.amount.last { it > 100 }
 * ```
 *
 * @see [lastOrNull]
 * @see [first]
 * @see [take]
 * @see [takeLast]
 *
 * @param [predicate] A lambda expression used to get the last value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return The last value in this [DataColumn] that matches the given [predicate].
 *
 * @throws [NoSuchElementException] if the [DataColumn] contains no element matching the [predicate]
 * (including the case when the [DataColumn] is empty).
 */
public inline fun <T> DataColumn<T>.last(predicate: (T) -> Boolean): T = values.last(predicate)

/**
 * Returns the last value in this [DataColumn] that matches the given [predicate].
 * Returns `null` if the [DataColumn] contains no elements matching the [predicate]
 * (including the case when the [DataColumn] is empty).
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the amount of the most recent financial transaction over 100 euros,
 * // or 'null' if there is no such transaction
 * df.amount.lastOrNull { it > 100 }
 * ```
 *
 * @see [last]
 * @see [first]
 * @see [take]
 * @see [takeLast]
 *
 * @param [predicate] A lambda expression used to get the last value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return The last value in this [DataColumn] that matches the given [predicate],
 * or `null` if the [DataColumn] contains no elements matching the [predicate].
 */
public inline fun <T> DataColumn<T>.lastOrNull(predicate: (T) -> Boolean): T? = values.lastOrNull(predicate)

// endregion

// region DataFrame

/**
 * Returns the last [row][DataRow] in this [DataFrame] that satisfies the given [predicate].
 * Returns `null` if the [DataFrame] contains no rows matching the [predicate]
 * (including the case when the [DataFrame] is empty).
 *
 * {@include [RowFilterDescription]}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // obtain the most recent financial transaction with amount over 100 euros,
 * // or 'null' if there is no such transaction
 * df.lastOrNull { amount > 100 }
 * ```
 *
 * @see [DataFrame.last]
 * @see [DataFrame.first]
 * @see [DataFrame.take]
 * @see [DataFrame.takeLast]
 * @see [takeWhile]
 *
 * @param [predicate] A [row filter][RowFilter] used to get the last value
 * that satisfies a condition specified in this filter.
 *
 * @return A [DataRow] containing the last row that matches the given [predicate],
 * or `null` if the [DataFrame] contains no rows matching the [predicate].
 */
public inline fun <T> DataFrame<T>.lastOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rowsReversed().firstOrNull { predicate(it, it) }

/**
 * Returns the last [row][DataRow] in this [DataFrame] that satisfies the given [predicate].
 *
 * {@include [RowFilterDescription]}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the most recent financial transaction with amount over 100 euros
 * df.last { amount > 100 }
 * ```
 *
 * @see [DataFrame.lastOrNull]
 * @see [DataFrame.first]
 * @see [DataFrame.take]
 * @see [DataFrame.takeLast]
 * @see [DataFrame.takeWhile]
 *
 * @param [predicate] A [row filter][RowFilter] used to get the last value
 * that satisfies a condition specified in this filter.
 *
 * @return A [DataRow] containing the last row that matches the given [predicate].
 *
 * @throws [NoSuchElementException] if the [DataFrame] contains no rows matching the [predicate].
 */
public inline fun <T> DataFrame<T>.last(predicate: RowFilter<T>): DataRow<T> =
    rowsReversed().first {
        predicate(it, it)
    }

/**
 * Returns the last [row][DataRow] in this [DataFrame]. If the [DataFrame] does not contain any rows, returns `null`.
 *
 * @see [DataFrame.last]
 * @see [DataFrame.first]
 * @see [DataFrame.take]
 * @see [DataFrame.takeLast]
 *
 * @return A [DataRow] containing the last row in this [DataFrame], or `null` if the [DataFrame] is empty.
 */
public fun <T> DataFrame<T>.lastOrNull(): DataRow<T>? = if (nrow > 0) get(nrow - 1) else null

/**
 * Returns the last [row][DataRow] in this [DataFrame].
 *
 * @see [DataFrame.lastOrNull]
 * @see [DataFrame.first]
 * @see [DataFrame.take]
 * @see [DataFrame.takeLast]
 *
 * @return A [DataRow] containing the last row in this [DataFrame].
 *
 * @throws NoSuchElementException if the [DataFrame] contains no rows.
 */
public fun <T> DataFrame<T>.last(): DataRow<T> {
    if (nrow == 0) {
        throw NoSuchElementException("DataFrame has no rows. Use `lastOrNull`.")
    }
    return get(nrow - 1)
}

// endregion

// region GroupBy

/**
 * [Reduces][GroupByDocs.Reducing] the groups of this [GroupBy]
 * by taking the last [row][DataRow] from each group,
 * and returns a [ReducedGroupBy] containing these rows
 * (one [row][DataRow] per group, each [row][DataRow] is the last [row][DataRow] in its group).
 *
 * If a group in this [GroupBy] is empty,
 * the corresponding [row][DataRow] in the resulting [ReducedGroupBy] will contain `null` values
 * for all columns in the group, except the grouping key.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of order status logs sorted by time,
 * // find the most recent status for each order
 * df.groupBy { orderId }.last().concat()
 * ```
 *
 * @see [GroupBy.first]
 *
 * @return A [ReducedGroupBy] containing the last [row][DataRow]
 * (or a [row][DataRow] with `null` values, except the grouping key) from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.last(): ReducedGroupBy<T, G> = reduce { lastOrNull() }

/**
 * [Reduces][GroupByDocs.Reducing] the groups of this [GroupBy]
 * by taking from each group the last [row][DataRow] satisfying the given [predicate],
 * and returns a [ReducedGroupBy] containing these rows (one [row][DataRow] per group,
 * each [row][DataRow] is the last [row][DataRow] in its group that satisfies the [predicate]).
 *
 * If the group in [GroupBy] contains no matching rows,
 * the corresponding row in [ReducedGroupBy] will contain `null` values for all columns in the group,
 * except the grouping key.
 *
 * {@include [RowFilterDescription]}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of order status logs sorted by time,
 * // find the most recent status shown to the customer for each order
 * df.groupBy { orderId }.last { !isInternal }.concat()
 * ```
 *
 * @see [GroupBy.first]
 *
 * @param [predicate] A [row filter][RowFilter] used to get the last value
 * that satisfies a condition specified in this filter.
 *
 * @return A [ReducedGroupBy] containing the last [row][DataRow] matching the [predicate]
 * (or a [row][DataRow] with `null` values, except the grouping key) from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.last(predicate: RowFilter<G>): ReducedGroupBy<T, G> = reduce { lastOrNull(predicate) }

// endregion

// region Pivot

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking the last [row][DataRow] from each group,
 * and returns a [ReducedPivot] that contains the last [row][DataRow] from the corresponding group in each column.
 *
 * @see [pivot]
 * @see [Pivot.reduce]
 * @see [Pivot.first]
 *
 * For more information about [Pivot] with examples: {@include [DocumentationUrls.Pivot]}
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings, find the most recent (if sorted by date and time)
 * // or the most expensive (if sorted by price) listing for each type of property (house, apartment, etc.)
 * df.pivot { type }.last().values()
 * ```
 *
 * @return A [ReducedPivot] containing in each column the last [row][DataRow] from the corresponding group.
 */
public fun <T> Pivot<T>.last(): ReducedPivot<T> = reduce { lastOrNull() }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the last [row][DataRow]
 * satisfying the given [predicate], and returns a [ReducedPivot] that contains the last [row][DataRow],
 * matching the [predicate], from the corresponding group in each column.
 *
 * @see [pivot]
 * @see [Pivot.reduce]
 * @see [Pivot.first]
 *
 * For more information about [Pivot] with examples: {@include [DocumentationUrls.Pivot]}
 *
 * {@include [RowFilterDescription]}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by date and time,
 * // find the most recent listing for each type of property (house, apartment, etc.)
 * // with the price less than 500,000 euros
 * df.pivot { type }.last { price < 500_000 }.values()
 * ```
 *
 * @param [predicate] A [row filter][RowFilter] used to get the last value
 * that satisfies a condition specified in this filter.
 *
 * @return A [ReducedPivot] containing in each column the last [row][DataRow]
 * that satisfies the [predicate], from the corresponding group (or a [row][DataRow] with `null` values)
 */
public fun <T> Pivot<T>.last(predicate: RowFilter<T>): ReducedPivot<T> = reduce { lastOrNull(predicate) }

// endregion

// region PivotGroupBy

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking the last [row][DataRow]
 * from each combined [pivot] + [groupBy] group, and returns a [ReducedPivotGroupBy]
 * that contains the last row from each corresponding group.
 * If any combined [pivot] + [groupBy] group in [PivotGroupBy] is empty, in the resulting [ReducedPivotGroupBy]
 * it will be represented by a [row][DataRow] with `null` values (except the grouping key).
 *
 * @see [pivot]
 * @see [Pivot.groupBy]
 * @see [GroupBy.pivot]
 * @see [PivotGroupBy.reduce]
 * @see [PivotGroupBy.first]
 *
 * For more information about [PivotGroupBy] with examples: {@include [DocumentationUrls.PivotGroupBy]}
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by date and time,
 * // find the most recent listing for each combination of type of property (house, apartment, etc.)
 * // and the city it is located in
 * df.pivot { type }.groupBy { city }.last().values()
 * ```
 *
 * @return A [ReducedPivotGroupBy] containing in each combination of a [groupBy] key and a [pivot] key either
 * the last [row][DataRow] of the corresponding [DataFrame] formed by this pivot–group pair,
 * or a [row][DataRow] with `null` values (except the grouping key) if this [DataFrame] is empty.
 */
public fun <T> PivotGroupBy<T>.last(): ReducedPivotGroupBy<T> = reduce { lastOrNull() }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy]
 * by taking from each combined [pivot] + [groupBy] group the last [row][DataRow] satisfying the given [predicate].
 * Returns a [ReducedPivotGroupBy] that contains the last [row][DataRow], matching the [predicate],
 * from each corresponding group.
 * If any combined [pivot] + [groupBy] group in [PivotGroupBy] does not contain any rows matching the [predicate],
 * in the resulting [ReducedPivotGroupBy] it will be represented by a [row][DataRow] with `null` values
 * (except the grouping key).
 *
 * @see [pivot]
 * @see [Pivot.groupBy]
 * @see [GroupBy.pivot]
 * @see [PivotGroupBy.reduce]
 * @see [PivotGroupBy.first]
 *
 * {@include [DocumentationUrls.PivotGroupBy]}
 *
 * {@include [DocumentationUrls.Pivot]}
 *
 * {@include [DocumentationUrls.GroupBy]}
 *
 * {@include [RowFilterDescription]}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by date and time,
 * // for each combination of type of property (house, apartment, etc.)
 * // and the city it is located in,
 * // find the most recent listing with the price less than 500,000 euros
 * df.pivot { type }.groupBy { city }.last { price < 500_000 }.values()
 * ```
 *
 * @param [predicate] A [row filter][RowFilter] used to get the last value
 * that satisfies a condition specified in this filter.
 *
 * @return A [ReducedPivotGroupBy] containing in each combination of a [groupBy] key and a [pivot] key either
 * the last matching the [predicate] [row][DataRow] of the corresponding [DataFrame] formed by this pivot–group pair,
 * or a [row][DataRow] with `null` values if this [DataFrame] does not contain any rows matching the [predicate].
 */
public fun <T> PivotGroupBy<T>.last(predicate: RowFilter<T>): ReducedPivotGroupBy<T> = reduce { lastOrNull(predicate) }

// endregion

// region ColumnsSelectionDsl

/**
 * # Last (Col) {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface LastColumnsSelectionDsl {

    /**
     * ## Last (Col) Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     */
    public interface Grammar {

        /** [**`last`**][ColumnsSelectionDsl.last] */
        public interface PlainDslName

        /** __`.`__[**`last`**][ColumnsSelectionDsl.last] */
        public interface ColumnSetName

        /** __`.`__[**`lastCol`**][ColumnsSelectionDsl.lastCol] */
        public interface ColumnGroupName
    }

    /**
     * ## Last (Col)
     * Returns the last column from [this\] that adheres to the optional given [condition\].
     * If no column adheres to the given [condition\], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `last` is named `lastCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]`  {  `[last][ColumnsSelectionDsl.last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[lastCol][String.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * #### Examples for this overload:
     *
     * {@get [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the last column
     *   that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @see [ColumnsSelectionDsl.first\]
     */
    private interface CommonLastDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[last][ColumnSet.last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[last][ColumnSet.last]`() }`
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("Last0")
    public fun <C> ColumnSet<C>.last(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.last(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     *
     * `df.`[select][DataFrame.select]`  {  `[last][ColumnsSelectionDsl.last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    @Interpretable("Last1")
    public fun ColumnsSelectionDsl<*>.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        asSingleColumn().lastCol(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[lastCol][SingleColumn.lastCol]`() }`
     */
    @Interpretable("Last2")
    public fun SingleColumn<DataRow<*>>.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().last(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[lastCol][String.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun String.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).lastCol(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[lastCol][SingleColumn.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[lastCol][KProperty.lastCol]`() }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).lastCol(condition)

    /**
     * @include [CommonLastDocs]
     * @set [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[lastCol][ColumnPath.lastCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.lastCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).lastCol(condition)
}

// endregion
