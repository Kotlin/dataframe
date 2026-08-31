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
 * Converts this [DataRow] into a [DataFrame] with one row per column of the original row.
 *
 * The result is a [DataFrame] of [NameValuePair] with two columns:
 * - `name` — the original column names, as [String];
 * - `value` — the cell values of this row, kept as they are.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [transposeTo][DataRow.transposeTo], which converts every value to a given type,
 * and [namedValues][DataRow.namedValues], which gives the same name-value pairs as a [List].
 *
 * ### Example
 * ```kotlin
 * // Read one wide row vertically: one line per column
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
 * Converts this [DataRow] into a [DataFrame] with one row per column of the original row.
 *
 * This is the same as [transpose][DataRow.transpose], but instead of keeping the values as they are,
 * each value is converted to [T] — unless it is already a [T], which is taken as it is.
 * `null` stays `null`.
 *
 * The result is a [DataFrame] of [NameValuePair] with two columns:
 * - `name` — the original column names, as [String];
 * - `value` — the cell values of this row, as [T].
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [namedValuesOf][DataRow.namedValuesOf], which gives a [List] and skips
 * the values of other types instead of converting them.
 *
 * ### Example
 * ```kotlin
 * // Show every cell of a row as text, whatever its original type is
 * df.first().transposeTo<String>()
 * ```
 *
 * @param [T] the type every value is converted to.
 * @throws [TypeConverterNotFoundException] if there is no converter from the type of some value to [T].
 * @return A [DataFrame] of [NameValuePair] with `name` and `value` columns,
 * containing one row for each column of this [DataRow].
 */
public inline fun <reified T> DataRow<*>.transposeTo(): DataFrame<NameValuePair<T>> = transposeTo(typeOf<T>())

/**
 * Implementation of the public `transposeTo` that takes the target [type] explicitly
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
