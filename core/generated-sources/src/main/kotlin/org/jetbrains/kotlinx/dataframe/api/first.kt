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
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/**
 * Returns the first value in this [<code>DataColumn</code>][DataColumn].
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/firstoncolumn.html)
 *
 * See also [<code>firstOrNull</code>][firstOrNull], [<code>last</code>][last], [<code>take</code>][take], [<code>takeLast</code>][takeLast].
 *
 * @return The first value in this [<code>DataColumn</code>][DataColumn].
 *
 * @throws [IndexOutOfBoundsException] if the [<code>DataColumn</code>][DataColumn] is empty.
 */
public fun <T> DataColumn<T>.first(): T = get(0)

/**
 * Returns the first value in this [<code>DataColumn</code>][DataColumn]. If the [<code>DataColumn</code>][DataColumn] is empty, returns `null`.
 *
 * For more information: [See `firstOrNull` on the documentation website.](https://kotlin.github.io/dataframe/firstoncolumn.html#firstornull)
 *
 * See also [<code>first</code>][first], [<code>last</code>][last], [<code>take</code>][take], [<code>takeLast</code>][takeLast].
 *
 * @return The first value in this [<code>DataColumn</code>][DataColumn], or `null` if the [<code>DataColumn</code>][DataColumn] is empty.
 */
public fun <T> DataColumn<T>.firstOrNull(): T? = if (size > 0) first() else null

/**
 * Returns the first value in this [<code>DataColumn</code>][DataColumn] that matches the given [<code>predicate</code>][predicate].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the amount of the first transaction over 100 euros
 * df.amount.first { it > 100 }
 * ```
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/firstoncolumn.html)
 *
 * See also [<code>firstOrNull</code>][firstOrNull], [<code>last</code>][last], [<code>take</code>][take], [<code>takeLast</code>][takeLast].
 *
 * @param [predicate] A lambda expression used to get the first value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [<code>DataColumn</code>][DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return The first value in this [<code>DataColumn</code>][DataColumn] that matches the given [<code>predicate</code>][predicate].
 *
 * @throws [NoSuchElementException] if the [<code>DataColumn</code>][DataColumn] contains no elements matching the [<code>predicate</code>][predicate]
 * (including the case when the [<code>DataColumn</code>][DataColumn] is empty).
 */
public fun <T> DataColumn<T>.first(predicate: (T) -> Boolean): T = values.first(predicate)

/**
 * Returns the first value in this [<code>DataColumn</code>][DataColumn] that matches the given [<code>predicate</code>][predicate].
 * Returns `null` if the [<code>DataColumn</code>][DataColumn] contains no elements matching the [<code>predicate</code>][predicate]
 * (including the case when the [<code>DataColumn</code>][DataColumn] is empty).
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the amount of the first transaction over 100 euros,
 * // or 'null' if there is no such transaction
 * df.amount.firstOrNull { it > 100 }
 * ```
 *
 * For more information: [See `firstOrNull` on the documentation website.](https://kotlin.github.io/dataframe/firstoncolumn.html#firstornull)
 *
 * See also [<code>first</code>][first], [<code>last</code>][last], [<code>take</code>][take], [<code>takeLast</code>][takeLast].
 *
 * @param [predicate] A lambda expression used to get the first value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [<code>DataColumn</code>][DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return The first value in this [<code>DataColumn</code>][DataColumn] that matches the given [<code>predicate</code>][predicate],
 * or `null` if the [<code>DataColumn</code>][DataColumn] contains no elements matching the [<code>predicate</code>][predicate].
 */
public fun <T> DataColumn<T>.firstOrNull(predicate: (T) -> Boolean): T? = values.firstOrNull(predicate)

// endregion

// region DataFrame

/**
 * Returns the first [<code>row</code>][DataRow] in this [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * See also [<code>firstOrNull</code>][DataFrame.firstOrNull],
 * [<code>last</code>][DataFrame.last],
 * [<code>take</code>][DataFrame.take],
 * [<code>takeWhile</code>][DataFrame.takeWhile],
 * [<code>takeLast</code>][DataFrame.takeLast].
 *
 * @return A [<code>DataRow</code>][DataRow] containing the first row in this [<code>DataFrame</code>][DataFrame].
 *
 * @throws NoSuchElementException if the [<code>DataFrame</code>][DataFrame] contains no rows.
 */
public fun <T> DataFrame<T>.first(): DataRow<T> {
    if (nrow == 0) {
        throw NoSuchElementException("DataFrame has no rows. Use `firstOrNull`.")
    }
    return get(0)
}

/**
 * Returns the first [<code>row</code>][DataRow] in this [<code>DataFrame</code>][DataFrame]. If the [<code>DataFrame</code>][DataFrame] does not contain any rows, returns `null`.
 *
 * For more information: [See `firstOrNull` on the documentation website.](https://kotlin.github.io/dataframe/first.html#firstornull)
 *
 * See also [<code>first</code>][DataFrame.first],
 * [<code>last</code>][DataFrame.last],
 * [<code>take</code>][DataFrame.take],
 * [<code>takeWhile</code>][DataFrame.takeWhile],
 * [<code>takeLast</code>][DataFrame.takeLast].
 *
 * @return A [<code>DataRow</code>][DataRow] containing the first row in this [<code>DataFrame</code>][DataFrame], or `null` if the [<code>DataFrame</code>][DataFrame] is empty.
 */
public fun <T> DataFrame<T>.firstOrNull(): DataRow<T>? = if (nrow > 0) first() else null

/**
 * Returns the first [<code>row</code>][DataRow] in this [<code>DataFrame</code>][DataFrame] that satisfies the given [<code>predicate</code>][predicate].
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the first transaction with amount over 100 euros
 * df.first { amount > 100 }
 * ```
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * See also [<code>firstOrNull</code>][DataFrame.firstOrNull],
 * [<code>last</code>][DataFrame.last],
 * [<code>take</code>][DataFrame.take],
 * [<code>takeWhile</code>][DataFrame.takeWhile],
 * [<code>takeLast</code>][DataFrame.takeLast].
 *
 * @param [predicate] A [<code>row filter</code>][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [<code>DataRow</code>][DataRow] containing the first row that matches the given [<code>predicate</code>][predicate].
 *
 * @throws [NoSuchElementException] if the [<code>DataFrame</code>][DataFrame] contains no rows matching the [<code>predicate</code>][predicate].
 */
public inline fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> =
    rows().first {
        predicate(it, it)
    }

/**
 * Returns the first [<code>row</code>][DataRow] in this [<code>DataFrame</code>][DataFrame] that satisfies the given [<code>predicate</code>][predicate].
 * Returns `null` if the [<code>DataFrame</code>][DataFrame] contains no rows matching the [<code>predicate</code>][predicate]
 * (including the case when the [<code>DataFrame</code>][DataFrame] is empty).
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions sorted by time,
 * // find the first transaction with amount over 100 euros,
 * // or 'null' if there is no such transaction
 * df.firstOrNull { amount > 100 }
 * ```
 *
 * For more information: [See `firstOrNull` on the documentation website.](https://kotlin.github.io/dataframe/first.html#firstornull)
 *
 * See also [<code>first</code>][DataFrame.first],
 * [<code>last</code>][DataFrame.last],
 * [<code>take</code>][DataFrame.take],
 * [<code>takeWhile</code>][DataFrame.takeWhile],
 * [<code>takeLast</code>][DataFrame.takeLast].
 *
 * @param [predicate] A [<code>row filter</code>][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [<code>DataRow</code>][DataRow] containing the first row that matches the given [<code>predicate</code>][predicate],
 * or `null` if the [<code>DataFrame</code>][DataFrame] contains no rows matching the [<code>predicate</code>][predicate].
 */
public inline fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rows().firstOrNull {
        predicate(it, it)
    }

// endregion

// region GroupBy

/**
 * [<code>Reduces</code>][GroupByDocs.Reducing] the groups of this [<code>GroupBy</code>][GroupBy]
 * by taking the first [<code>row</code>][DataRow] from each group,
 * and returns a [<code>ReducedGroupBy</code>][ReducedGroupBy] containing these rows
 * (one [<code>row</code>][DataRow] per group, each [<code>row</code>][DataRow] is the first [<code>row</code>][DataRow] in its group).
 *
 * If a group in this [<code>GroupBy</code>][GroupBy] is empty,
 * the corresponding [<code>row</code>][DataRow] in the resulting [<code>ReducedGroupBy</code>][ReducedGroupBy] will contain `null` values
 * for all columns in the group, except the grouping key.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders sorted by date and time,
 * // find the first order placed by each customer
 * df.groupBy { customerId }.first().concat()
 * ```
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * For more information about [<code>GroupBy</code>][GroupBy] and [<code>first</code>][first] with examples: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * See also [<code>last</code>][GroupBy.last].
 *
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] containing the first [<code>row</code>][DataRow]
 * (or a [<code>row</code>][DataRow] with `null` values, except the grouping key) from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(): ReducedGroupBy<T, G> = reduce { firstOrNull() }

/**
 * [<code>Reduces</code>][GroupByDocs.Reducing] the groups of this [<code>GroupBy</code>][GroupBy]
 * by taking from each group the first [<code>row</code>][DataRow] satisfying the given [<code>predicate</code>][predicate],
 * and returns a [<code>ReducedGroupBy</code>][ReducedGroupBy] containing these rows (one [<code>row</code>][DataRow] per group,
 * each [<code>row</code>][DataRow] is the first [<code>row</code>][DataRow] in its group that satisfies the [<code>predicate</code>][predicate]).
 *
 * If the group in [<code>GroupBy</code>][GroupBy] contains no matching rows,
 * the corresponding row in [<code>ReducedGroupBy</code>][ReducedGroupBy] will contain `null` values for all columns in the group,
 * except the grouping key.
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders sorted by date and time,
 * // find the first order over 100 euros placed by each customer
 * df.groupBy { customerId }.first { total > 100 }.concat()
 * ```
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * For more information about [<code>GroupBy</code>][GroupBy] and [<code>first</code>][first] with examples: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * See also [<code>last</code>][GroupBy.last].
 *
 * @param [predicate] A [<code>row filter</code>][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [<code>ReducedGroupBy</code>][ReducedGroupBy] containing the first [<code>row</code>][DataRow] matching the [<code>predicate</code>][predicate]
 * (or a [<code>row</code>][DataRow] with `null` values, except the grouping key) from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(predicate: RowFilter<G>): ReducedGroupBy<T, G> = reduce { firstOrNull(predicate) }

// endregion

// region Pivot

/**
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking the first [<code>row</code>][DataRow] from each group,
 * and returns a [<code>ReducedPivot</code>][ReducedPivot] that contains the first [<code>row</code>][DataRow] from the corresponding group in each column.
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * For more information about [<code>Pivot</code>][Pivot] and [<code>first</code>][first] with examples: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by price,
 * // find the cheapest listing for each type of property (house, apartment, etc.)
 * df.pivot { type }.first().values()
 * ```
 *
 * See also [<code>pivot</code>][pivot], [<code>reduce</code>][Pivot.reduce], [<code>last</code>][Pivot.last].
 *
 * @return A [<code>ReducedPivot</code>][ReducedPivot] containing in each column the first [<code>row</code>][DataRow] from the corresponding group.
 */
public fun <T> Pivot<T>.first(): ReducedPivot<T> = reduce { firstOrNull() }

/**
 * [<code>Reduces</code>][PivotDocs.Reducing] this [<code>Pivot</code>][Pivot] by taking from each group the first [<code>row</code>][DataRow]
 * satisfying the given [<code>predicate</code>][predicate], and returns a [<code>ReducedPivot</code>][ReducedPivot] that contains the first row, matching the [<code>predicate</code>][predicate],
 * from the corresponding group in each column.
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * For more information about [<code>Pivot</code>][Pivot] and [<code>first</code>][first] with examples: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by price,
 * // find the cheapest listing for each type of property (house, apartment, etc.)
 * // with is not yet sold out.
 * df.pivot { type }.first { !soldOut }.values()
 * ```
 *
 * See also [<code>pivot</code>][pivot], [<code>reduce</code>][Pivot.reduce], [<code>last</code>][Pivot.last].
 *
 * @param [predicate] A [<code>row filter</code>][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [<code>ReducedPivot</code>][ReducedPivot] containing in each column the first [<code>row</code>][DataRow]
 * that satisfies the [<code>predicate</code>][predicate], from the corresponding group (or a [<code>row</code>][DataRow] with `null` values).
 */
public fun <T> Pivot<T>.first(predicate: RowFilter<T>): ReducedPivot<T> = reduce { firstOrNull(predicate) }

// endregion

// region PivotGroupBy

/**
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy] by taking the first [<code>row</code>][DataRow]
 * from each combined [<code>pivot</code>][pivot] + [<code>groupBy</code>][groupBy] group, and returns a [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy]
 * that contains the first row from each corresponding group.
 * If any combined [<code>pivot</code>][pivot] + [<code>groupBy</code>][groupBy] group in [<code>PivotGroupBy</code>][PivotGroupBy] is empty, in the resulting [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy]
 * it will be represented by a [<code>row</code>][DataRow] with `null` values (except the grouping key).
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * For more information about [<code>PivotGroupBy</code>][PivotGroupBy] with examples: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of real estate listings sorted by price,
 * // find the cheapest listing for each combination of type of property (house, apartment, etc.)
 * // and the city it is located in
 * df.pivot { type }.groupBy { city }.first().values()
 * ```
 *
 * See also [<code>groupBy</code>][Pivot.groupBy],
 * [<code>pivot</code>][GroupBy.pivot],
 * [<code>reduce</code>][PivotGroupBy.reduce],
 * [<code>last</code>][PivotGroupBy.last].
 *
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] containing in each combination of a [<code>groupBy</code>][groupBy] key and a [<code>pivot</code>][pivot] key either
 * the first [<code>row</code>][DataRow] of the corresponding [<code>DataFrame</code>][DataFrame] formed by this pivot–group pair,
 * or a [<code>row</code>][DataRow] with `null` values (except the grouping key) if this [<code>DataFrame</code>][DataFrame] is empty.
 */
public fun <T> PivotGroupBy<T>.first(): ReducedPivotGroupBy<T> = reduce { firstOrNull() }

/**
 * [<code>Reduces</code>][PivotGroupByDocs.Reducing] this [<code>PivotGroupBy</code>][PivotGroupBy]
 * by taking from each combined [<code>pivot</code>][pivot] + [<code>groupBy</code>][groupBy] group the first [<code>row</code>][DataRow] satisfying the given [<code>predicate</code>][predicate].
 * Returns a [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] that contains the first row, matching the [<code>predicate</code>][predicate], from each corresponding group.
 * If any combined [<code>pivot</code>][pivot] + [<code>groupBy</code>][groupBy] group in [<code>PivotGroupBy</code>][PivotGroupBy] does not contain any rows matching the [<code>predicate</code>][predicate],
 * in the resulting [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] it will be represented by a [<code>row</code>][DataRow] with `null` values
 * (except the grouping key).
 *
 * For more information: [See `first` on the documentation website.](https://kotlin.github.io/dataframe/first.html)
 *
 * [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 *
 * [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
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
 * See also [<code>groupBy</code>][Pivot.groupBy],
 * [<code>pivot</code>][GroupBy.pivot],
 * [<code>reduce</code>][PivotGroupBy.reduce],
 * [<code>last</code>][PivotGroupBy.last].
 *
 * @param [predicate] A [<code>row filter</code>][RowFilter] used to get the first value
 * that satisfies a condition specified in this filter.
 *
 * @return A [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] containing in each combination of a [<code>groupBy</code>][groupBy] key and a [<code>pivot</code>][pivot] key either
 * the first matching the [<code>predicate</code>][predicate] [<code>row</code>][DataRow] of the corresponding [<code>DataFrame</code>][DataFrame] formed by this pivot–group pair,
 * or a [<code>row</code>][DataRow] with `null` values if this [<code>DataFrame</code>][DataFrame] does not contain any rows matching the [<code>predicate</code>][predicate].
 */
public fun <T> PivotGroupBy<T>.first(predicate: RowFilter<T>): ReducedPivotGroupBy<T> =
    reduce { firstOrNull(predicate) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## First (Col) [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface FirstColumnsSelectionDsl {

    /**
     * ## First (Col) Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>**`first`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`first`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`firstCol`**</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [<code>**`first`**</code>][ColumnsSelectionDsl.first] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`first`**</code>][ColumnsSelectionDsl.first] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`firstCol`**</code>][ColumnsSelectionDsl.firstCol] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>first</code>][ColumnsSelectionDsl.first]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("order") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][String.firstCol]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [condition] The optional [<code>ColumnFilter</code>][ColumnFilter] condition that the column must adhere to.
     * @return A [<code>SingleColumn</code>][SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    private interface CommonFirstDocs {

        /** Examples key */
        typealias Examples = Nothing
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("order") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][kotlin.String.firstCol]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>first</code>][ColumnSet.first]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>first</code>][ColumnSet.first]`() }`
     *
     * @param [condition] The optional [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("First0")
    public fun <C> ColumnSet<C>.first(condition: (ColumnWithPath<C>) -> Boolean = { true }): SingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.first(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("order") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][kotlin.String.firstCol]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>first</code>][ColumnsSelectionDsl.first]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Interpretable("First1")
    public fun ColumnsSelectionDsl<*>.first(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        asSingleColumn().firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("order") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][kotlin.String.firstCol]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>firstCol</code>][SingleColumn.firstCol]`() }`
     *
     * @param [condition] The optional [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Interpretable("First2")
    public fun SingleColumn<DataRow<*>>.firstCol(
        condition: (ColumnWithPath<*>) -> Boolean = { true },
    ): SingleColumn<*> = this.ensureIsColumnGroup().asColumnSet().first(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("order") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][kotlin.String.firstCol]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][String.firstCol]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    public fun String.firstCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("order") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][kotlin.String.firstCol]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>firstCol</code>][SingleColumn.firstCol]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>firstCol</code>][KProperty.firstCol]`() }`
     *
     * @param [condition] The optional [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.firstCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("order") } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>firstCol</code>][kotlin.String.firstCol]` { it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>firstCol</code>][ColumnPath.firstCol]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    public fun ColumnPath.firstCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)
}

// endregion
