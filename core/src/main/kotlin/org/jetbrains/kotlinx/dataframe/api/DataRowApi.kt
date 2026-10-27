package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.GET_ROWS_ITERABLE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.GET_ROWS_RANGE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.GET_ROW_OR_NULL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.GET_ROW_REPLACE
import org.jetbrains.kotlinx.dataframe.util.IS_EMPTY_REPLACE
import org.jetbrains.kotlinx.dataframe.util.IS_NOT_EMPTY_REPLACE
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty
import kotlin.reflect.KType

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(IS_EMPTY_REPLACE), DeprecationLevel.WARNING)
public fun DataRow<*>.isEmpty(): Boolean = owner.columns().all { it[index] == null }

@Suppress("DEPRECATION_ERROR")
@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(IS_NOT_EMPTY_REPLACE), DeprecationLevel.WARNING)
public fun DataRow<*>.isNotEmpty(): Boolean = !isEmpty()

// region docs

/**
 * There are three functions listing the values of a [DataRow]; they differ in what is filtered and what is returned:
 * - [valuesOf][DataRow.valuesOf]`<R>()` — only the values of type `R`, without their column names;
 * - [namedValues][DataRow.namedValues]`()` — all values, each paired with its column name in a [NameValuePair];
 * - [namedValuesOf][DataRow.namedValuesOf]`<R>()` — only the [NameValuePair]s whose value is of type `R`.
 *
 * All of them go over the top-level columns of the row, in column order:
 * the value of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a [DataRow]
 * and is not split into the values of its nested columns.
 * Use [values][DataRow.values] to get all values without filtering.
 */
@ExcludeFromSources
private typealias RowValuesListingSnippet = Nothing

/**
 * The type check is done with `is R` on each value, so a nullable `R` (like `Int?`) also keeps the `null` values.
 */
@ExcludeFromSources
private typealias RowValuesTypeFilterSnippet = Nothing

/**
 * This accessor is declared explicitly for the star-projected schema `NameValuePair<*>`,
 * which is what [transpose][DataRow.transpose] returns.
 * The generated extension properties of [NameValuePair] require a concrete value type,
 * so without this overload `row.transpose().name`, `row.transpose().value`,
 * or `row.transpose().map { name }` would not resolve.
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.nameValuePairAccessors]
 */
@ExcludeFromSources
private typealias NameValuePairAccessorSnippet = Nothing

/**
 * As seen from a row, this describes the columns of the [DataFrame] the row belongs to (see [df][DataRow.df]).
 * For a row of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * these are the columns of that group, not of the top-level [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [columnsCount][DataRow.columnsCount], [columnNames][DataRow.columnNames],
 * and [columnTypes][DataRow.columnTypes].
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.columnsMetadata]
 */
@ExcludeFromSources
private typealias RowColumnsMetadataSnippet = Nothing

/**
 * {@get [GetValueDocs.FIRST_LINE]}
 *
 * The cast to [T\] is unchecked: because of type erasure, a wrong [T\]
 * (like `getValue<Int>("city")`) does not fail here, but later with a [ClassCastException]
 * where the value is used as [T\].
 * Use a nullable [T\] for columns that can contain `null`.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * {@get [GetValueDocs.SEE_ALSO]}
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.getValueAndGetValueOrNull]
 */
@ExcludeFromSources
internal interface GetValueDocs {

    // The first line of the KDoc, including the behavior for a missing column
    @ExcludeFromSources
    typealias FIRST_LINE = Nothing

    // The "See also" paragraph
    @ExcludeFromSources
    typealias SEE_ALSO = Nothing
}

/**
 * Returns a [DataFrame] with the rows of the [DataFrame] this row belongs to (see [df][DataRow.df]),
 * at the given offsets relative to the [index][DataRow.index] of this row:
 * offset `0` is this row, `-1` is the [previous][DataRow.prev] row, `1` is the [next][DataRow.next] row.
 *
 * {@get [RelativeDocs.BOUNDS]}
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [prev][DataRow.prev], [next][DataRow.next], and [movingAverage][DataRow.movingAverage],
 * which aggregates a window of the previous rows instead of returning it.
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.relativeRows]
 */
@ExcludeFromSources
internal interface RelativeDocs {

    // How offsets outside the DataFrame are handled by this overload
    @ExcludeFromSources
    typealias BOUNDS = Nothing
}

// endregion

/**
 * Returns the values of this [DataRow] that are of type [R], without their column names.
 *
 * @include [RowValuesTypeFilterSnippet]
 *
 * @include [RowValuesListingSnippet]
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.valuesOfAndNamedValues]
 *
 * @param [R] the type of values to keep.
 * @return A [List] of the values of type [R], in column order.
 */
public inline fun <reified R> DataRow<*>.valuesOf(): List<R> = values().filterIsInstance<R>()

// region DataSchema

/**
 * A [column name][name] paired with a [value]: the [DataSchema] of a single cell of a [DataRow] seen as a map entry.
 *
 * It is the element type of [namedValues][DataRow.namedValues] and [namedValuesOf][DataRow.namedValuesOf],
 * and the schema of the [DataFrame] returned by [transpose][DataRow.transpose] and
 * [transposeTo][DataRow.transposeTo], which has a `name` and a `value` column.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * @param [V] the type of [value].
 * @property [name] the name of the column the [value] comes from.
 * @property [value] the cell value.
 */
@DataSchema
@RequiredByIntellijPlugin
public data class NameValuePair<V>(override val name: String, override val value: V) : NameValueProperty<V>

// Without these overloads row.transpose().name or row.map { name } won't resolve

/**
 * Returns the `name` column of this [NameValuePair] [DataFrame]: the column names of the transposed [DataRow].
 *
 * @include [NameValuePairAccessorSnippet]
 */
public val ColumnsContainer<NameValuePair<*>>.name: DataColumn<String>
    @JvmName("NameValuePairAny_name")
    get() = this["name"] as DataColumn<String>

/**
 * Returns the `name` of this [NameValuePair] row: the name of the column the [value][DataRow.value] comes from.
 *
 * @include [NameValuePairAccessorSnippet]
 */
public val DataRow<NameValuePair<*>>.name: String
    @JvmName("NameValuePairAny_name")
    get() = this["name"] as String

/**
 * Returns the `value` column of this [NameValuePair] [DataFrame]: the cell values of the transposed [DataRow].
 * Its type is the common type of all these values.
 *
 * @include [NameValuePairAccessorSnippet]
 */
public val ColumnsContainer<NameValuePair<*>>.value: DataColumn<*>
    @JvmName("NameValuePairAny_value")
    get() = this["value"]

/**
 * Returns the `value` of this [NameValuePair] row: the cell value of the column named [name][DataRow.name].
 *
 * @include [NameValuePairAccessorSnippet]
 */
public val DataRow<NameValuePair<*>>.value: Any?
    @JvmName("NameValuePairAny_value")
    get() = this["value"]

// endregion

/**
 * Returns the values of this [DataRow] that are of type [R], each paired with its column name in a [NameValuePair].
 *
 * @include [RowValuesTypeFilterSnippet]
 *
 * @include [RowValuesListingSnippet]
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [transposeTo][DataRow.transposeTo], which converts all values to [R] instead of skipping
 * the values of other types, and returns a [DataFrame].
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.valuesOfAndNamedValues]
 *
 * @param [R] the type of values to keep.
 * @return A [List] of [NameValuePair]s with the values of type [R], in column order.
 */
public inline fun <reified R> DataRow<*>.namedValuesOf(): List<NameValuePair<R>> =
    values().zip(columnNames()).filter { it.first is R }.map { NameValuePair(it.second, it.first as R) }

/**
 * Returns all values of this [DataRow], each paired with its column name in a [NameValuePair].
 *
 * @include [RowValuesListingSnippet]
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [transpose][DataRow.transpose], which returns the same name-value pairs as a [DataFrame].
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.valuesOfAndNamedValues]
 *
 * @return A [List] of [NameValuePair]s, one for each column, in column order.
 */
@RequiredByIntellijPlugin
public fun DataRow<*>.namedValues(): List<NameValuePair<Any?>> =
    values().zip(columnNames()) { value, name -> NameValuePair(name, value) }

// region getValue

/**
 * @include [GetValueDocs]
 * {@set [GetValueDocs.FIRST_LINE] Returns the value of the column with the given [columnName\] in this [DataRow],
 * cast to [T\]. Throws an [IllegalArgumentException] if there is no such column.}
 * {@set [GetValueDocs.SEE_ALSO] See also [getValueOrNull][DataRow.getValueOrNull],
 * which returns `null` instead of throwing if there is no such column.}
 *
 * @param [T] the type to cast the value to.
 * @param [columnName] the name of a top-level column of the [DataFrame] this row belongs to.
 * @return The value of the column [columnName], cast to [T].
 * @throws [IllegalArgumentException] if there is no column with the given [columnName].
 */
public fun <T> DataRow<*>.getValue(columnName: String): T = get(columnName) as T

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataRow<*>.getValue(column: ColumnReference<T>): T = get(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataRow<*>.getValue(column: KProperty<T>): T = get(column)

/**
 * @include [GetValueDocs]
 * {@set [GetValueDocs.FIRST_LINE] Returns the value of the column with the given [columnName\] in this [DataRow],
 * cast to [T\], or `null` if there is no such column.}
 * {@set [GetValueDocs.SEE_ALSO] See also [getValue][DataRow.getValue], which throws if there is no such column,
 * and [containsKey][DataRow.containsKey], which checks if there is such a column.
 *
 * Note that `null` is also returned if the column exists and its value is `null`.}
 *
 * @param [T] the type to cast the value to.
 * @param [columnName] the name of a top-level column of the [DataFrame] this row belongs to.
 * @return The value of the column [columnName], cast to [T], or `null` if there is no such column.
 */
public fun <T> DataRow<*>.getValueOrNull(columnName: String): T? = getOrNull(columnName) as T?

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataRow<*>.getValueOrNull(column: KProperty<T>): T? = getValueOrNull<T>(column.columnName)

// endregion

// region contains

/**
 * Returns `true` if the [DataFrame] this row belongs to has a top-level column with the given [columnName].
 *
 * Nested columns of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are not checked;
 * use [contains][DataRow.contains] with a [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
 * (like `pathOf("name", "firstName") in row`) for them.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [getValue][DataRow.getValue], which throws if there is no such column,
 * and [getValueOrNull][DataRow.getValueOrNull], which returns `null` instead.
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.containsKeyAndContains]
 *
 * @param [columnName] the name of the column to look for.
 * @return `true` if there is a top-level column with the given [columnName], `false` otherwise.
 */
public fun DataRow<*>.containsKey(columnName: String): Boolean = owner.containsColumn(columnName)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun DataRow<*>.containsKey(column: ColumnReference<*>): Boolean = owner.containsColumn(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun DataRow<*>.containsKey(column: KProperty<*>): Boolean = owner.containsColumn(column)

/**
 * Returns `true` if the [DataFrame] this row belongs to has the given [column].
 * Allows writing `column in row`.
 *
 * Unlike [containsKey][DataRow.containsKey] with a column name, the [column] is resolved by its path,
 * so a [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
 * (like `pathOf("name", "firstName")`) can point to a nested column of a column group.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.containsKeyAndContains]
 *
 * @param [column] the reference to the column to look for.
 * @return `true` if the [column] is found, `false` otherwise.
 */
public operator fun DataRow<*>.contains(column: ColumnReference<*>): Boolean = containsKey(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public operator fun DataRow<*>.contains(column: KProperty<*>): Boolean = containsKey(column)

// endregion

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [DataRow].
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
internal typealias DiffDocs = Nothing

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [DataRow].
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
internal typealias DiffOrNullDocs = Nothing

/**
 * @include [DiffDocs]
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T> DataRow<T>.diff(firstRowResult: Double, expression: RowExpression<T, Double>): Double =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * @include [DiffDocs]
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
// required to resolve `diff(0) { intValue }`
public inline fun <T> DataRow<T>.diff(firstRowResult: Int, expression: RowExpression<T, Int>): Int =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * @include [DiffDocs]
 */
public inline fun <T> DataRow<T>.diff(firstRowResult: Long, expression: RowExpression<T, Long>): Long =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * @include [DiffDocs]
 */
public inline fun <T> DataRow<T>.diff(firstRowResult: Float, expression: RowExpression<T, Float>): Float =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * @include [DiffOrNullDocs]
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Double>): Double? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * @include [DiffOrNullDocs]
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Int>): Int? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * @include [DiffOrNullDocs]
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Long>): Long? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * @include [DiffOrNullDocs]
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Float>): Float? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Returns the number of columns of this [DataRow], which is the number of its values.
 *
 * @include [RowColumnsMetadataSnippet]
 *
 * @return The number of top-level columns of the [DataFrame] this row belongs to.
 */
@RequiredByIntellijPlugin
public fun DataRow<*>.columnsCount(): Int = df().ncol

/**
 * Returns the names of the columns of this [DataRow], in column order.
 *
 * @include [RowColumnsMetadataSnippet]
 *
 * @return A [List] of the names of the top-level columns of the [DataFrame] this row belongs to.
 */
public fun DataRow<*>.columnNames(): List<String> = df().columnNames()

/**
 * Returns the [types][KType] of the columns of this [DataRow], in column order.
 *
 * These are the types of the columns, not of the values in this row:
 * for a column of type `Int?`, it is `Int?` even if the value in this row is not `null`.
 *
 * @include [RowColumnsMetadataSnippet]
 *
 * @return A [List] of the [KType]s of the top-level columns of the [DataFrame] this row belongs to.
 */
public fun DataRow<*>.columnTypes(): List<KType> = df().columnTypes()

@Suppress("DEPRECATION_ERROR")
@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROW_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRow(index: Int): DataRow<T> = getRowOrNull(index)!!

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROWS_ITERABLE_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRows(indices: Iterable<Int>): DataFrame<T> = df().getRows(indices)

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROWS_RANGE_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRows(indices: IntRange): DataFrame<T> = df().getRows(indices)

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROW_OR_NULL_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRowOrNull(index: Int): DataRow<T>? {
    val df = df()
    return if (index >= 0 && index < df.nrow) df[index] else null
}

/**
 * Returns the previous [row][DataRow] in the [DataFrame] relative to the current row.
 * If the current row is the first row in the [DataFrame], it returns `null`.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [next][DataRow.next] and [relative][DataRow.relative].
 *
 * @return The previous [DataRow] if it exists, or `null` if the current row is the first in the [DataFrame].
 */
public fun <T> DataRow<T>.prev(): DataRow<T>? {
    val index = index()
    return if (index > 0) df()[index - 1] else null
}

/**
 * Returns the next [row][DataRow] in the [DataFrame] relative to the current row.
 * If the current row is the last row in the [DataFrame], it returns `null`.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [prev][DataRow.prev] and [relative][DataRow.relative].
 *
 * @return The next [DataRow] if it exists, or `null` if the current row is the last in the [DataFrame].
 */
public fun <T> DataRow<T>.next(): DataRow<T>? {
    val index = index()
    val df = df()
    return if (index < df.nrow - 1) df[index + 1] else null
}

/**
 * @include [RelativeDocs]
 * {@set [RelativeDocs.BOUNDS] The rows are returned in the order of [relativeIndices\]. Offsets that point outside
 * the [DataFrame] are dropped, so the result can have fewer rows than [relativeIndices\] or no rows at all.}
 *
 * @param [relativeIndices] the offsets of the rows to return, relative to the index of this row.
 * @return A [DataFrame] with the rows at the given offsets that exist.
 */
@Suppress("DEPRECATION_ERROR")
public fun <T> DataRow<T>.relative(relativeIndices: Iterable<Int>): DataFrame<T> =
    getRows(relativeIndices.mapNotNull { (index + it).let { if (it >= 0 && it < df().rowsCount()) it else null } })

/**
 * @include [RelativeDocs]
 * {@set [RelativeDocs.BOUNDS] Unlike the overload with an [Iterable] of offsets, the range is not filtered
 * offset by offset: both of its bounds, shifted by the index of this row, are coerced into the valid row indices
 * of the [DataFrame]. So a range that partly sticks out of the [DataFrame], like `-5..0` for the third row,
 * is cut to the rows that exist.}
 *
 * @param [relativeIndices] the range of offsets of the rows to return, relative to the index of this row.
 * @return A [DataFrame] with the rows in the given range of offsets, cut to the bounds of the [DataFrame].
 */
@Suppress("DEPRECATION_ERROR")
public fun <T> DataRow<T>.relative(relativeIndices: IntRange): DataFrame<T> =
    getRows(
        (relativeIndices.first + index).coerceIn(df().indices)..(relativeIndices.last + index).coerceIn(df().indices),
    )

/**
 * Returns the average of the [expression] computed on this row and the `k - 1` rows before it.
 *
 * Near the start of the [DataFrame], fewer than [k] rows are available, so the sum is divided
 * by the actual number of rows: for the first row, the result is the value of [expression] for that row.
 * If [k] is `0`, no rows are taken and the result is [Double.NaN].
 *
 * The [expression] is called for each averaged row with that row as `it`, while `this` stays
 * the current row, so access the averaged values through `it`, like `movingAverage(3) { it.age }`.
 *
 * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
 *
 * See also [relative][DataRow.relative], which returns a window of rows around this row as a [DataFrame],
 * and [prev][DataRow.prev].
 *
 * ### Example
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.movingAverageOfAge]
 *
 * @param [k] the size of the window: the number of rows to average over, including this row.
 * @throws [IllegalArgumentException] if [k] is negative.
 * @param [expression] the [row expression][RowExpression] computing the [Number] to average for each row.
 * @return The average of [expression] over the current row and up to `k - 1` previous rows,
 * as a [Double].
 */
public inline fun <T> DataRow<T>.movingAverage(k: Int, expression: RowExpression<T, Number>): Double {
    var count = 0
    return backwardIterable().take(k).sumOf {
        count++
        expression(it).toDouble()
    } / count
}
