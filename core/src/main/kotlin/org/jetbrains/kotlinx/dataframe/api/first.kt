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
 * Returns the first value in this [DataColumn].
 *
 * @see [firstOrNull]
 * @see [last]
 * @see [take]
 * @see [takeLast]
 *
 * @return The first value in this [DataColumn].
 *
 * @throws [IndexOutOfBoundsException] if the [DataColumn] is empty.
 */
public fun <T> DataColumn<T>.first(): T = get(0)

/**
 * Returns the first value in this [DataColumn]. If the [DataColumn] is empty, returns `null`.
 *
 * @see [first]
 * @see [last]
 * @see [take]
 * @see [takeLast]
 *
 * @return The first value in this [DataColumn], or `null` if the [DataColumn] is empty.
 */
public fun <T> DataColumn<T>.firstOrNull(): T? = if (size > 0) first() else null

/**
 * Returns the first value in this [DataColumn] that matches the given [predicate].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the amount of the first transaction over 100 euros
 * df.amount.first { it > 100 }
 * ```
 *
 * @see [firstOrNull]
 * @see [last]
 * @see [take]
 * @see [takeLast]
 *
 * @param [predicate] A lambda expression used to select the first value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return The first value in this [DataColumn] that matches the given [predicate].
 *
 * @throws [NoSuchElementException] if the [DataColumn] contains no elements matching the [predicate]
 * (including the case when the [DataColumn] is empty).
 */
public fun <T> DataColumn<T>.first(predicate: (T) -> Boolean): T = values.first(predicate)

/**
 * Returns the first value in this [DataColumn] that matches the given [predicate].
 * Returns `null` if the [DataColumn] contains no elements matching the [predicate]
 * (including the case when the [DataColumn] is empty).
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the amount of the first transaction over 100 euros,
 * // or 'null' if there is no such transaction
 * df.amount.firstOrNull { it > 100 }
 * ```
 *
 * @see [first]
 * @see [last]
 * @see [take]
 * @see [takeLast]
 *
 * @param [predicate] A lambda expression used to select the first value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return The first value in this [DataColumn] that matches the given [predicate],
 * or `null` if the [DataColumn] contains no elements matching the [predicate].
 */
public fun <T> DataColumn<T>.firstOrNull(predicate: (T) -> Boolean): T? = values.firstOrNull(predicate)

// endregion

// region DataFrame

/**
 * Returns the first [row][DataRow] in this [DataFrame].
 *
 * @see [firstOrNull]
 * @see [last]
 * @see [take]
 * @see [takeWhile]
 * @see [takeLast]
 *
 * @return A [DataRow] containing the first row in this [DataFrame].
 *
 * @throws NoSuchElementException if the [DataFrame] contains no rows.
 */
public fun <T> DataFrame<T>.first(): DataRow<T> {
    if (nrow == 0) {
        throw NoSuchElementException("DataFrame has no rows. Use `firstOrNull`.")
    }
    return get(0)
}

/**
 * Returns the first [row][DataRow] in this [DataFrame]. If the [DataFrame] does not contain any rows, returns `null`.
 *
 * @see [first]
 * @see [last]
 * @see [take]
 * @see [takeWhile]
 * @see [takeLast]
 *
 * @return A [DataRow] containing the first row in this [DataFrame], or `null` if the [DataFrame] is empty.
 */
public fun <T> DataFrame<T>.firstOrNull(): DataRow<T>? = if (nrow > 0) first() else null

/**
 * Returns the first [row][DataRow] in this [DataFrame] that satisfies the given [predicate].
 *
 * {@include [RowFilterDescription]}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the first transaction with amount over 100 euros
 * df.first { amount > 100 }
 * ```
 *
 * @see [firstOrNull]
 * @see [last]
 * @see [take]
 * @see [takeWhile]
 * @see [takeLast]
 *
 * @param predicate A [row filter][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [DataRow] containing the first row that matches the given [predicate].
 *
 * @throws [NoSuchElementException] if the [DataFrame] contains no rows matching the [predicate].
 */
public inline fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> =
    rows().first {
        predicate(it, it)
    }

/**
 * Returns the first [row][DataRow] in this [DataFrame] that satisfies the given [predicate].
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
 * // find the first transaction with amount over 100 euros,
 * // or 'null' if there is no such transaction
 * df.firstOrNull { amount > 100 }
 * ```
 *
 * @see [first]
 * @see [last]
 * @see [take]
 * @see [takeWhile]
 * @see [takeLast]
 *
 * @param predicate A [row filter][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [DataRow] containing the first row that matches the given [predicate],
 * or `null` if the [DataFrame] contains no rows matching the [predicate].
 */
public inline fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rows().firstOrNull {
        predicate(it, it)
    }

// endregion

// region GroupBy

/**
 * [Reduces][GroupByDocs.Reducing] the groups of this [GroupBy]
 * by taking the first [row][DataRow] from each group,
 * and returns a [ReducedGroupBy] containing these rows
 * (one [row][DataRow] per group, each [row][DataRow] is the first [row][DataRow] in its group).
 *
 * If a group in this [GroupBy] is empty,
 * the corresponding [row][DataRow] in the resulting [ReducedGroupBy] will contain `null` values
 * for all columns in the group, except the grouping key.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders sorted by date and time,
 * // find the first order placed by each customer
 * df.groupBy { customerId }.first().concat()
 * ```
 *
 * @see [last]
 *
 * @return A [ReducedGroupBy] containing the first [row][DataRow]
 * (or a [row][DataRow] with `null` values, except the grouping key) from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(): ReducedGroupBy<T, G> = reduce { firstOrNull() }

/**
 * [Reduces][GroupByDocs.Reducing] the groups of this [GroupBy]
 * by taking from each group the first [row][DataRow] satisfying the given [predicate],
 * and returns a [ReducedGroupBy] containing these rows (one [row][DataRow] per group,
 * each [row][DataRow] is the first [row][DataRow] in its group that satisfies the [predicate]).
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
 * // In a DataFrame of orders sorted by date and time,
 * // find the first order over 100 euros placed by each customer
 * df.groupBy { customerId }.first { total > 100 }.concat()
 * ```
 *
 * @see [last]
 *
 * @param predicate A [row filter][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [ReducedGroupBy] containing the first [row][DataRow] matching the [predicate]
 * (or a [row][DataRow] with `null` values, except the grouping key) from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(predicate: RowFilter<G>): ReducedGroupBy<T, G> = reduce { firstOrNull(predicate) }

// endregion

// region Pivot

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking the first [row][DataRow] from each group,
 * and returns a [ReducedPivot] that contains the first [row][DataRow] from the corresponding group in each column.
 *
 * @see [pivot]
 * @see [Pivot.reduce]
 * @see [Pivot.last]
 *
 * For more information about [Pivot] with examples: {@include [DocumentationUrls.Pivot]}
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by price,
 * // find the cheapest listing for each type of property (house, apartment, etc.)
 * df.pivot { type }.first().values()
 * ```
 *
 * @return A [ReducedPivot] containing in each column the first [row][DataRow] from the corresponding group.
 */
public fun <T> Pivot<T>.first(): ReducedPivot<T> = reduce { firstOrNull() }

/**
 * [Reduces][PivotDocs.Reducing] this [Pivot] by taking from each group the first [row][DataRow]
 * satisfying the given [predicate], and returns a [ReducedPivot] that contains the first row, matching the [predicate],
 * from the corresponding group in each column.
 *
 * @see [pivot]
 * @see [Pivot.reduce]
 * @see [Pivot.last]
 *
 * For more information about [Pivot] with examples: {@include [DocumentationUrls.Pivot]}
 *
 * {@include [RowFilterDescription]}
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by price,
 * // find the cheapest listing for each type of property (house, apartment, etc.)
 * // with is not yet sold out.
 * df.pivot { type }.first { !soldOut }.values()
 * ```
 *
 * @param predicate A [row filter][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [ReducedPivot] containing in each column the first [row][DataRow]
 * that satisfies the [predicate], from the corresponding group (or a [row][DataRow] with `null` values).
 */
public fun <T> Pivot<T>.first(predicate: RowFilter<T>): ReducedPivot<T> = reduce { firstOrNull(predicate) }

// endregion

// region PivotGroupBy

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy] by taking the first [row][DataRow]
 * from each combined [pivot] + [groupBy] group, and returns a [ReducedPivotGroupBy]
 * that contains the first row from each corresponding group.
 * If any combined [pivot] + [groupBy] group in [PivotGroupBy] is empty, in the resulting [ReducedPivotGroupBy]
 * it will be represented by a [row][DataRow] with `null` values (except the grouping key).
 *
 * @see [pivot]
 * @see [Pivot.groupBy]
 * @see [GroupBy.pivot]
 * @see [PivotGroupBy.reduce]
 * @see [PivotGroupBy.last]
 *
 * For more information about [PivotGroupBy] with examples: {@include [DocumentationUrls.PivotGroupBy]}
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by price,
 * // find the cheapest listing for each combination of type of property (house, apartment, etc.)
 * // and the city it is located in
 * df.pivot { type }.groupBy { city }.first().values()
 * ```
 *
 * @return A [ReducedPivotGroupBy] containing in each combination of a [groupBy] key and a [pivot] key either
 * the first [row][DataRow] of the corresponding [DataFrame] formed by this pivot–group pair,
 * or a [row][DataRow] with `null` values (except the grouping key) if this [DataFrame] is empty.
 */
public fun <T> PivotGroupBy<T>.first(): ReducedPivotGroupBy<T> = reduce { firstOrNull() }

/**
 * [Reduces][PivotGroupByDocs.Reducing] this [PivotGroupBy]
 * by taking from each combined [pivot] + [groupBy] group the first [row][DataRow] satisfying the given [predicate].
 * Returns a [ReducedPivotGroupBy] that contains the first row, matching the [predicate], from each corresponding group.
 * If any combined [pivot] + [groupBy] group in [PivotGroupBy] does not contain any rows matching the [predicate],
 * in the resulting [ReducedPivotGroupBy] it will be represented by a [row][DataRow] with `null` values
 * (except the grouping key).
 *
 * @see [pivot]
 * @see [Pivot.groupBy]
 * @see [GroupBy.pivot]
 * @see [PivotGroupBy.reduce]
 * @see [PivotGroupBy.last]
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
 * // In a DataFrame of real estate listings sorted by price,
 * // for each combination of type of property (house, apartment, etc.)
 * // and the city it is located in,
 * // find the cheapest listing that is not yet sold out
 * df.pivot { type }.groupBy { city }.first { !soldOut }.values()
 * ```
 *
 * @param predicate A [row filter][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [ReducedPivotGroupBy] containing in each combination of a [groupBy] key and a [pivot] key either
 * the first matching the [predicate] [row][DataRow] of the corresponding [DataFrame] formed by this pivot–group pair,
 * or a [row][DataRow] with `null` values if this [DataFrame] does not contain any rows matching the [predicate].
 */
public fun <T> PivotGroupBy<T>.first(predicate: RowFilter<T>): ReducedPivotGroupBy<T> =
    reduce { firstOrNull(predicate) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## First (Col) {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FirstColumnsSelectionDsl {

    /**
     * ## First (Col) Grammar
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

        /** [**`first`**][ColumnsSelectionDsl.first] */
        public interface PlainDslName

        /** __`.`__[**`first`**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** __`.`__[**`firstCol`**][ColumnsSelectionDsl.firstCol] */
        public interface ColumnGroupName
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this\] that adheres to the optional given [condition\].
     * If no column adheres to the given [condition\], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]`  {  `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * {@get [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the first column
     *   that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @see [ColumnsSelectionDsl.last\]
     */
    private interface CommonFirstDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * @include [CommonFirstDocs]
     * @set [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[first][ColumnSet.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[first][ColumnSet.first]`() }`
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("First0")
    public fun <C> ColumnSet<C>.first(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.first(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * @include [CommonFirstDocs]
     * @set [CommonFirstDocs.Examples]
     *
     * `df.`[select][DataFrame.select]`  {  `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    @Interpretable("First1")
    public fun ColumnsSelectionDsl<*>.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        asSingleColumn().firstCol(condition)

    /**
     * @include [CommonFirstDocs]
     * @set [CommonFirstDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[firstCol][SingleColumn.firstCol]`() }`
     */
    @Interpretable("First2")
    public fun SingleColumn<DataRow<*>>.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().first(condition)

    /**
     * @include [CommonFirstDocs]
     * @set [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun String.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * @include [CommonFirstDocs]
     * @set [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[firstCol][SingleColumn.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[firstCol][KProperty.firstCol]`() }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * @include [CommonFirstDocs]
     * @set [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[firstCol][ColumnPath.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)
}

// endregion
