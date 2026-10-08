package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

// region docs

// endregion

// region DataColumn

/**
 * Calls [<code>action</code>][action] for every value of this [<code>DataColumn</code>][DataColumn], from the first one to the last one.
 *
 * This function returns [<code>Unit</code>][Unit], so it is only useful for its side effects, such as printing values or collecting them.
 * To get a new [<code>DataColumn</code>][DataColumn] of computed values instead, use [<code>map</code>][DataColumn.map].
 *
 * For more information: [See `forEach` on a `DataColumn` on the documentation website.](https://kotlin.github.io/dataframe/iterate.html#foreach-on-datacolumn)
 *
 * See also [<code>forEachIndexed</code>][DataColumn.forEachIndexed] — the same, and the position of every value is given as well.
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the ages, from the first value to the last one:
 * // 15, 45, 20, 40, 30, 20, 30
 * df.age.forEach { println(it) }
 * ```
 *
 * @param [action] A function that is called with every value of this column.
 */
public inline fun <T> DataColumn<T>.forEach(action: (T) -> Unit): Unit = values().forEach(action)

/**
 * Calls [<code>action</code>][action] for every value of this [<code>DataColumn</code>][DataColumn] and its position, from the first value to the last one.
 *
 * The position of the first value is `0`.
 *
 * This function returns [<code>Unit</code>][Unit], so it is only useful for its side effects, such as printing values or collecting them.
 * To get a new [<code>DataColumn</code>][DataColumn] of computed values instead, use [<code>mapIndexed</code>][DataColumn.mapIndexed].
 *
 * For more information: [See `forEach` on a `DataColumn` on the documentation website.](https://kotlin.github.io/dataframe/iterate.html#foreach-on-datacolumn)
 *
 * See also [<code>forEach</code>][DataColumn.forEach] — the same, without the positions.
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the first names, numbered:
 * // "1. Alice", "2. Bob", ...
 * df.name.firstName.forEachIndexed { i, firstName ->
 *     println("${i + 1}. $firstName")
 * }
 * ```
 *
 * @param [action] A function that is called with the position of every value of this column and that value.
 */
public inline fun <T> DataColumn<T>.forEachIndexed(action: (Int, T) -> Unit): Unit = values().forEachIndexed(action)

// endregion

// region DataFrame

/**
 * Calls [<code>action</code>][action] for every row of this [<code>DataFrame</code>][DataFrame], from the first one to the last one.
 *
 * [<code>action</code>][action] gets the row both as its receiver and as its argument,
 * so inside it `age` and `it.age` mean the same thing.
 *
 * This function returns [<code>Unit</code>][Unit], so it is only useful for its side effects, such as printing values or collecting them.
 * To get a [<code>List</code>][List] of computed values instead, use [<code>map</code>][DataFrame.map].
 *
 * For more information: [See iterating on the documentation website.](https://kotlin.github.io/dataframe/iterate.html) [See Row Expressions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-expressions)
 *
 * See also:
 * - [<code>forEach</code>][DataColumn.forEach] — goes over the values of a single column.
 * - [<code>mapToColumn</code>][DataFrame.mapToColumn] — computes a value for every row
 *   and collects the results into a [<code>DataColumn</code>][DataColumn].
 * - [<code>mapToFrame</code>][DataFrame.mapToFrame] — computes several columns at once and returns them as a new [<code>DataFrame</code>][DataFrame].
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the age of every person, from the first row to the last one
 * df.forEach { println(it.age) }
 * ```
 *
 * @param [action] A [<code>RowExpression</code>][RowExpression] that is called with every row of this [<code>DataFrame</code>][DataFrame].
 */
public inline fun <T> DataFrame<T>.forEach(action: RowExpression<T, Unit>): Unit = rows().forEach { action(it, it) }

// endregion

// region GroupBy

/**
 * Calls [<code>body</code>][body] for every key–group pair of this [<code>GroupBy</code>][GroupBy], in the order in which the pairs appear in it.
 *
 * [<code>body</code>][body] gets each pair as a [<code>GroupBy.Entry</code>][GroupBy.Entry]: its [<code>key</code>][GroupBy.Entry.key] is the row of [<code>keys</code>][GroupBy.keys]
 * with the key values, and its [<code>group</code>][GroupBy.Entry.group] is a [<code>DataFrame</code>][DataFrame] with the rows of that group.
 * The entry can be destructured, as `(key, group)`.
 *
 * This function returns [<code>Unit</code>][Unit], so it is only useful for its side effects, such as printing values or collecting them.
 * To get a [<code>List</code>][List] of computed values instead, use [<code>map</code>][GroupBy.map].
 *
 * For more information: [See `forEach` on a `GroupBy` on the documentation website.](https://kotlin.github.io/dataframe/iterate.html#foreach-on-groupby)
 *
 * See also [<code>forEach</code>][DataFrame.forEach] — goes over the rows of a [<code>DataFrame</code>][DataFrame].
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the number of people per city:
 * // "London: 1", "Dubai: 1", "Moscow: 2", ...
 * df.groupBy { city }.forEach { (key, group) ->
 *     println("${key.city}: ${group.rowsCount()}")
 * }
 * ```
 *
 * @param [body] A function that is called with every key–group pair of this [<code>GroupBy</code>][GroupBy].
 */
public inline fun <T, G> GroupBy<T, G>.forEach(body: (GroupBy.Entry<T, G>) -> Unit): Unit =
    keys.forEach { key ->
        val group = groups[key.index()]
        body(GroupBy.Entry(key, group))
    }

// endregion
