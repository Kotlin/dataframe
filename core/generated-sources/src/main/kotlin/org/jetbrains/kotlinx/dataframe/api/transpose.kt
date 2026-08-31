package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.jetbrains.kotlinx.dataframe.impl.api.convertTo
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.values
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region DataRow

/**
 * Converts this [<code>DataRow</code>][DataRow] into a [<code>DataFrame</code>][DataFrame] with one row per column of the original row.
 *
 * The result is a [<code>DataFrame</code>][DataFrame] of [<code>NameValuePair</code>][NameValuePair] with two columns:
 * - `name` — the original column names, as [<code>String</code>][String];
 * - `value` — the cell values of this row, kept as they are.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>transposeTo</code>][DataRow.transposeTo], which converts every value to a given type,
 * and [<code>namedValues</code>][DataRow.namedValues], which gives the same name-value pairs as a [<code>List</code>][List].
 *
 * ### Example
 * ```kotlin
 * // Read one wide row vertically: one line per column
 * df.first().transpose()
 * ```
 *
 * @return A [<code>DataFrame</code>][DataFrame] of [<code>NameValuePair</code>][NameValuePair] with `name` and `value` columns,
 * containing one row for each column of this [<code>DataRow</code>][DataRow].
 */
public fun DataRow<*>.transpose(): DataFrame<NameValuePair<*>> {
    val valueColumn = DataColumn.createByInference(NameValuePair<*>::value.columnName, values)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<*>::name.name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

/**
 * Converts this [<code>DataRow</code>][DataRow] into a [<code>DataFrame</code>][DataFrame] with one row per column of the original row.
 *
 * This is the same as [<code>transpose</code>][DataRow.transpose], but instead of keeping the values as they are,
 * each value is converted to [<code>T</code>][T] — unless it is already a [<code>T</code>][T], which is taken as it is.
 * `null` stays `null`.
 *
 * The result is a [<code>DataFrame</code>][DataFrame] of [<code>NameValuePair</code>][NameValuePair] with two columns:
 * - `name` — the original column names, as [<code>String</code>][String];
 * - `value` — the cell values of this row, as [<code>T</code>][T].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>namedValuesOf</code>][DataRow.namedValuesOf], which gives a [<code>List</code>][List] and skips
 * the values of other types instead of converting them.
 *
 * ### Example
 * ```kotlin
 * // Show every cell of a row as text, whatever its original type is
 * df.first().transposeTo<String>()
 * ```
 *
 * @param [T] the type every value is converted to.
 * @throws [TypeConverterNotFoundException] if there is no converter from the type of some value to [<code>T</code>][T].
 * @return A [<code>DataFrame</code>][DataFrame] of [<code>NameValuePair</code>][NameValuePair] with `name` and `value` columns,
 * containing one row for each column of this [<code>DataRow</code>][DataRow].
 */
public inline fun <reified T> DataRow<*>.transposeTo(): DataFrame<NameValuePair<T>> = transposeTo(typeOf<T>())

/**
 * Implementation of the public `transposeTo` that takes the target [<code>type</code>][type] explicitly
 * instead of as a reified type parameter.
 */
@PublishedApi
internal fun <T> AnyRow.transposeTo(type: KType): DataFrame<NameValuePair<T>> {
    val convertedValues = values.map { it?.convertTo(type) as T? }
    val valueColumn = DataColumn.createByInference(NameValuePair<T>::value.columnName, convertedValues)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<T>::name.name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

// endregion
