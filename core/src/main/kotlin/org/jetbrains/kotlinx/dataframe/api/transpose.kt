package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.impl.api.convertTo
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.values
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region DataRow

/**
 * Converts this [DataRow] into a [DataFrame] with one row per column of the original row.
 *
 * The result is a [DataFrame] of [NameValuePair] with two columns:
 * - `name` — the original column names, as [String];
 * - `value` — the corresponding cell values, kept as-is (so the resulting schema is `NameValuePair<*>`).
 *
 * See also [transposeTo], which does the same but converts every value to a given type `T`,
 * and [namedValues][DataRow.namedValues].
 *
 * ### Example
 * ```kotlin
 * // Convert the first row into a two-column "name" / "value" DataFrame
 * df.first().transpose()
 * ```
 *
 * @return A [DataFrame] of [NameValuePair] with `name` and `value` columns,
 * containing one row for each column of this [DataRow].
 */
public fun DataRow<*>.transpose(): DataFrame<NameValuePair<*>> {
    val valueColumn = DataColumn.createByInference(NameValuePair<*>::value.columnName, values)
    val nameColumn = owner.columnNames().toValueColumn(NameValuePair<*>::name.name)
    return dataFrameOf(nameColumn, valueColumn).cast()
}

/**
 * Converts this [DataRow] into a [DataFrame] with one row per column of the original row,
 * converting every value to the given type [T].
 *
 * The result is a [DataFrame] of [NameValuePair] with two columns:
 * - `name` — the original column names, as [String];
 * - `value` — the corresponding cell values, each converted to [T].
 *
 * This is the same as [transpose], but instead of keeping the values as-is,
 * each value is converted to [T].
 *
 * ### Example
 * ```kotlin
 * // Convert the first row into a two-column "name" / "value" DataFrame,
 * // converting all values to String
 * df.first().transposeTo<String>()
 * ```
 *
 * @param T the type every value is converted to.
 * @return A [DataFrame] of [NameValuePair] with `name` and `value` columns,
 * containing one row for each column of this [DataRow], with each value converted to [T].
 */
public inline fun <reified T> DataRow<*>.transposeTo(): DataFrame<NameValuePair<T>> = transposeTo(typeOf<T>())

/**
 * Implementation of [transposeTo] that takes the target [type] explicitly
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
