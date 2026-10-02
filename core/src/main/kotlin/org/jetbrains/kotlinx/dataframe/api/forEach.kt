package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

// region docs

/**
 * This function returns [Unit], so it is only useful for its side effects, such as printing values or collecting them.
 * To get {@get [RESULT]} instead, use {@get [MAP]}.
 * @comment What all `forEach` overloads have in common. KDoc-snippet.
 *    Set [RESULT] to what the matching `map` function returns, and [MAP] to a link to it.
 *    The comment sits at the end: a leading `{@comment}` leaves blank lines where the snippet is included.
 */
@ExcludeFromSources
private interface CommonForEachSnippet {

    // the key for a @set that says what the matching `map` function returns
    @ExcludeFromSources
    typealias RESULT = Nothing

    // the key for a @set with a link to the matching `map` function
    @ExcludeFromSources
    typealias MAP = Nothing
}

// endregion

// region DataColumn

/**
 * Calls [action] for every value of this [DataColumn], from the first one to the last one.
 *
 * @include [CommonForEachSnippet] {@set [CommonForEachSnippet.RESULT] a new [DataColumn] of computed values} {@set [CommonForEachSnippet.MAP] [map][DataColumn.map]}
 *
 * For more information: {@include [DocumentationUrls.IterateOnColumn]}
 *
 * See also [forEachIndexed][DataColumn.forEachIndexed] — the same, and the position of every value is given as well.
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the ages, from the first value to the last one: 15, 45, 20, 40, 30, 20, 30
 * df.age.forEach { println(it) }
 * ```
 *
 * @param [action] A function that is called with every value of this column.
 */
public inline fun <T> DataColumn<T>.forEach(action: (T) -> Unit): Unit = values().forEach(action)

/**
 * Calls [action] for every value of this [DataColumn] and its position, from the first value to the last one.
 *
 * The position of the first value is `0`.
 *
 * @include [CommonForEachSnippet] {@set [CommonForEachSnippet.RESULT] a new [DataColumn] of computed values} {@set [CommonForEachSnippet.MAP] [mapIndexed][DataColumn.mapIndexed]}
 *
 * For more information: {@include [DocumentationUrls.IterateOnColumn]}
 *
 * See also [forEach][DataColumn.forEach] — the same, without the positions.
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the first names, numbered: "1. Alice", "2. Bob", ...
 * df.name.firstName.forEachIndexed { i, firstName -> println("\${i + 1}. \$firstName") }
 * ```
 *
 * @param [action] A function that is called with the position of every value of this column and that value.
 */
public inline fun <T> DataColumn<T>.forEachIndexed(action: (Int, T) -> Unit): Unit = values().forEachIndexed(action)

// endregion

// region DataFrame

/**
 * Calls [action] for every row of this [DataFrame], from the first one to the last one.
 *
 * [action] gets the row both as its receiver and as its argument,
 * so inside it `age` and `it.age` mean the same thing.
 *
 * @include [CommonForEachSnippet] {@set [CommonForEachSnippet.RESULT] a [List] of computed values} {@set [CommonForEachSnippet.MAP] [map][DataFrame.map]}
 *
 * For more information: {@include [DocumentationUrls.Iterate]} {@include [DocumentationUrls.DataRow.RowExpressions]}
 *
 * See also [forEach][DataColumn.forEach] — goes over the values of a single column.
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the age of every person, from the first row to the last one
 * df.forEach { println(it.age) }
 * ```
 *
 * @param [action] A [RowExpression] that is called with every row of this [DataFrame].
 */
public inline fun <T> DataFrame<T>.forEach(action: RowExpression<T, Unit>): Unit = rows().forEach { action(it, it) }

// endregion

// region GroupBy

/**
 * Calls [body] for every key–group pair of this [GroupBy], in the order in which the pairs appear in it.
 *
 * [body] gets each pair as a [GroupBy.Entry]: its [key][GroupBy.Entry.key] is the row of [keys][GroupBy.keys]
 * with the key values, and its [group][GroupBy.Entry.group] is a [DataFrame] with the rows of that group.
 * The entry can be destructured, as `(key, group)`.
 *
 * @include [CommonForEachSnippet] {@set [CommonForEachSnippet.RESULT] a [List] of computed values} {@set [CommonForEachSnippet.MAP] [map][GroupBy.map]}
 *
 * For more information: {@include [DocumentationUrls.IterateOnGroupBy]}
 *
 * See also [forEach][DataFrame.forEach] — goes over the rows of a [DataFrame].
 *
 * ### Example
 *
 * ```kotlin
 * // Prints the number of people per city: "London: 1", "Dubai: 1", "Moscow: 2", ...
 * df.groupBy { city }.forEach { (key, group) -> println("\${key.city}: \${group.rowsCount()}") }
 * ```
 *
 * @param [body] A function that is called with every key–group pair of this [GroupBy].
 */
public inline fun <T, G> GroupBy<T, G>.forEach(body: (GroupBy.Entry<T, G>) -> Unit): Unit =
    keys.forEach { key ->
        val group = groups[key.index()]
        body(GroupBy.Entry(key, group))
    }

// endregion
