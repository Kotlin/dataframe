package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
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
import org.jetbrains.kotlinx.dataframe.util.NAME_VALUE_PAIR
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty
import kotlin.reflect.KType

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(IS_EMPTY_REPLACE), DeprecationLevel.WARNING)
public fun AnyRow.isEmpty(): Boolean = owner.columns().all { it[index] == null }

@Suppress("DEPRECATION_ERROR")
@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(IS_NOT_EMPTY_REPLACE), DeprecationLevel.WARNING)
public fun AnyRow.isNotEmpty(): Boolean = !isEmpty()

public inline fun <reified R> AnyRow.valuesOf(): List<R> = values().filterIsInstance<R>()

// region DataSchema

/**
 * Instantiatable [KeyValueProperty] representing a key-value pair [DataSchema] for a [DataFrame].
 *
 * [NameValuePair] may be deprecated in favor of an instantiatable [KeyValueProperty] class in the future.
 *
 * @param V type of the value
 * @param key the name of the key column (previously called `name`)
 * @param value the name of the value column
 */
@DataSchema
@RequiredByIntellijPlugin
public data class NameValuePair<V>(override val key: String, override val value: V) : KeyValueProperty<V> {
    public companion object {
        @Deprecated(NAME_VALUE_PAIR, level = DeprecationLevel.WARNING)
        public operator fun <V> invoke(name: String, value: V): NameValuePair<V> = NameValuePair(name, value)
    }
}

@Deprecated(NAME_VALUE_PAIR, ReplaceWith("key"), level = DeprecationLevel.WARNING)
public val NameValuePair<*>.name: String
    get() = key

@Deprecated(NAME_VALUE_PAIR, ReplaceWith("this.copy(name, value)"), level = DeprecationLevel.WARNING)
public fun <V> NameValuePair<V>.copy(name: String = this.key, value: V = this.value): NameValuePair<V> =
    NameValuePair(key = name, value = value)

// Without these overloads row.transpose().name or row.map { name } won't resolve

@Deprecated(NAME_VALUE_PAIR, ReplaceWith("this.key"), level = DeprecationLevel.WARNING)
public val ColumnsContainer<NameValuePair<*>>.name: DataColumn<String>
    @JvmName("NameValuePairAny_name")
    get() = this["key"] as DataColumn<String>

@Deprecated(NAME_VALUE_PAIR, ReplaceWith("this.key"), level = DeprecationLevel.WARNING)
public val DataRow<NameValuePair<*>>.name: String
    @JvmName("NameValuePairAny_name")
    get() = this["key"] as String

public val ColumnsContainer<NameValuePair<*>>.key: DataColumn<String>
    @JvmName("NameValuePairAny_key")
    get() = this["key"] as DataColumn<String>

public val DataRow<NameValuePair<*>>.key: String
    @JvmName("NameValuePairAny_key")
    get() = this["key"] as String

public val ColumnsContainer<NameValuePair<*>>.value: DataColumn<*>
    @JvmName("NameValuePairAny_value")
    get() = this["value"]

public val DataRow<NameValuePair<*>>.value: Any?
    @JvmName("NameValuePairAny_value")
    get() = this["value"]

// endregion

public inline fun <reified R> AnyRow.namedValuesOf(): List<NameValuePair<R>> =
    values().zip(columnNames()).filter { it.first is R }.map { NameValuePair(it.second, it.first as R) }

@RequiredByIntellijPlugin
public fun AnyRow.namedValues(): List<NameValuePair<Any?>> =
    values().zip(columnNames()) { value, name -> NameValuePair(name, value) }

// region getValue

public fun <T> AnyRow.getValue(columnName: String): T = get(columnName) as T

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AnyRow.getValue(column: ColumnReference<T>): T = get(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AnyRow.getValue(column: KProperty<T>): T = get(column)

public fun <T> AnyRow.getValueOrNull(columnName: String): T? = getOrNull(columnName) as T?

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AnyRow.getValueOrNull(column: KProperty<T>): T? = getValueOrNull<T>(column.columnName)

// endregion

// region contains

public fun AnyRow.containsKey(columnName: String): Boolean = owner.containsColumn(columnName)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun AnyRow.containsKey(column: AnyColumnReference): Boolean = owner.containsColumn(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun AnyRow.containsKey(column: KProperty<*>): Boolean = owner.containsColumn(column)

public operator fun AnyRow.contains(column: AnyColumnReference): Boolean = containsKey(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public operator fun AnyRow.contains(column: KProperty<*>): Boolean = containsKey(column)

// endregion

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
internal interface DiffDocs

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
internal interface DiffOrNullDocs

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T> DataRow<T>.diff(firstRowResult: Double, expression: RowExpression<T, Double>): Double =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
// required to resolve `diff(0) { intValue }`
public inline fun <T> DataRow<T>.diff(firstRowResult: Int, expression: RowExpression<T, Int>): Int =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diff(firstRowResult: Long, expression: RowExpression<T, Long>): Long =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diff(firstRowResult: Float, expression: RowExpression<T, Float>): Float =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Double>): Double? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Int>): Int? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Long>): Long? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Calculates the difference between the results of a row expression computed on the current and previous DataRow.
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Float>): Float? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

@RequiredByIntellijPlugin
public fun AnyRow.columnsCount(): Int = df().ncol

public fun AnyRow.columnNames(): List<String> = df().columnNames()

public fun AnyRow.columnTypes(): List<KType> = df().columnTypes()

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
 * @return The previous [DataRow] if it exists, or `null` if the current row is the last in the [DataFrame].
 */
public fun <T> DataRow<T>.next(): DataRow<T>? {
    val index = index()
    val df = df()
    return if (index < df.nrow - 1) df[index + 1] else null
}

@Suppress("DEPRECATION_ERROR")
public fun <T> DataRow<T>.relative(relativeIndices: Iterable<Int>): DataFrame<T> =
    getRows(relativeIndices.mapNotNull { (index + it).let { if (it >= 0 && it < df().rowsCount()) it else null } })

@Suppress("DEPRECATION_ERROR")
public fun <T> DataRow<T>.relative(relativeIndices: IntRange): DataFrame<T> =
    getRows(
        (relativeIndices.first + index).coerceIn(df().indices)..(relativeIndices.last + index).coerceIn(df().indices),
    )

public inline fun <T> DataRow<T>.movingAverage(k: Int, expression: RowExpression<T, Number>): Double {
    var count = 0
    return backwardIterable().take(k).sumOf {
        count++
        expression(it).toDouble()
    } / count
}
