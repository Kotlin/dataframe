package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.ColumnDivNumberDocs.COLUMN_DIV_NUMBER_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.ColumnDivNumberDocs.COLUMN_DIV_NUMBER_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.ColumnDivNumberDocs.COLUMN_DIV_NUMBER_INT_NOTE
import org.jetbrains.kotlinx.dataframe.api.ColumnDivNumberDocs.COLUMN_DIV_NUMBER_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.ColumnDivNumberDocs.COLUMN_DIV_NUMBER_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.ColumnDivNumberDocs.COLUMN_DIV_NUMBER_ZERO_ERROR
import org.jetbrains.kotlinx.dataframe.api.ColumnMinusNumberDocs.COLUMN_MINUS_NUMBER_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.ColumnMinusNumberDocs.COLUMN_MINUS_NUMBER_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.ColumnMinusNumberDocs.COLUMN_MINUS_NUMBER_EXAMPLE_NUMBER
import org.jetbrains.kotlinx.dataframe.api.ColumnMinusNumberDocs.COLUMN_MINUS_NUMBER_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.ColumnMinusNumberDocs.COLUMN_MINUS_NUMBER_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.ColumnPlusNumberDocs.COLUMN_PLUS_NUMBER_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.ColumnPlusNumberDocs.COLUMN_PLUS_NUMBER_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.ColumnPlusNumberDocs.COLUMN_PLUS_NUMBER_EXAMPLE_NUMBER
import org.jetbrains.kotlinx.dataframe.api.ColumnPlusNumberDocs.COLUMN_PLUS_NUMBER_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.ColumnPlusNumberDocs.COLUMN_PLUS_NUMBER_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.ColumnPlusStringDocs.COLUMN_PLUS_STRING_RECEIVER
import org.jetbrains.kotlinx.dataframe.api.ColumnPlusStringDocs.COLUMN_PLUS_STRING_RETURN_TYPE
import org.jetbrains.kotlinx.dataframe.api.CompareDocs.COMPARE_DESCRIPTION
import org.jetbrains.kotlinx.dataframe.api.CompareDocs.COMPARE_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.CompareDocs.COMPARE_OPERATION
import org.jetbrains.kotlinx.dataframe.api.CompareDocs.COMPARE_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.NotDocs.NOT_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.NotDocs.NOT_DESCRIPTION
import org.jetbrains.kotlinx.dataframe.api.NotDocs.NOT_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.NotDocs.NOT_RETURN
import org.jetbrains.kotlinx.dataframe.api.NumberDivColumnDocs.NUMBER_DIV_COLUMN_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.NumberDivColumnDocs.NUMBER_DIV_COLUMN_DIVIDEND_TYPE
import org.jetbrains.kotlinx.dataframe.api.NumberDivColumnDocs.NUMBER_DIV_COLUMN_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.NumberDivColumnDocs.NUMBER_DIV_COLUMN_INT_NOTE
import org.jetbrains.kotlinx.dataframe.api.NumberDivColumnDocs.NUMBER_DIV_COLUMN_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.NumberDivColumnDocs.NUMBER_DIV_COLUMN_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.NumberDivColumnDocs.NUMBER_DIV_COLUMN_ZERO_ERROR
import org.jetbrains.kotlinx.dataframe.api.NumberMinusColumnDocs.NUMBER_MINUS_COLUMN_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.NumberMinusColumnDocs.NUMBER_MINUS_COLUMN_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.NumberMinusColumnDocs.NUMBER_MINUS_COLUMN_EXAMPLE_NUMBER
import org.jetbrains.kotlinx.dataframe.api.NumberMinusColumnDocs.NUMBER_MINUS_COLUMN_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.NumberMinusColumnDocs.NUMBER_MINUS_COLUMN_NUMBER_TYPE
import org.jetbrains.kotlinx.dataframe.api.NumberMinusColumnDocs.NUMBER_MINUS_COLUMN_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.NumberPlusColumnDocs.NUMBER_PLUS_COLUMN_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.NumberPlusColumnDocs.NUMBER_PLUS_COLUMN_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.NumberPlusColumnDocs.NUMBER_PLUS_COLUMN_EXAMPLE_NUMBER
import org.jetbrains.kotlinx.dataframe.api.NumberPlusColumnDocs.NUMBER_PLUS_COLUMN_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.NumberPlusColumnDocs.NUMBER_PLUS_COLUMN_NUMBER_TYPE
import org.jetbrains.kotlinx.dataframe.api.NumberPlusColumnDocs.NUMBER_PLUS_COLUMN_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.TimesDocs.TIMES_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.TimesDocs.TIMES_EXAMPLE
import org.jetbrains.kotlinx.dataframe.api.TimesDocs.TIMES_EXAMPLE_NUMBER
import org.jetbrains.kotlinx.dataframe.api.TimesDocs.TIMES_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.api.TimesDocs.TIMES_SEE_ALSO
import org.jetbrains.kotlinx.dataframe.api.UnaryMinusDocs.UNARY_MINUS_COLUMN_TYPE
import org.jetbrains.kotlinx.dataframe.api.UnaryMinusDocs.UNARY_MINUS_NULL_NOTE
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import java.math.BigDecimal
import java.math.BigInteger

// region Not

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the inverse [Boolean] values
 * of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * Each element in this [column][DataColumn] is transformed
 * using the logical `not` operation: `true` becomes `false`, and `false` becomes `true`.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of financial transactions,
 * // find which transactions failed
 * df.select { !isSuccessful }
 * // or
 * !df.isSuccessful
 * ```
 *
 * @return A [DataColumn] containing the negated [Boolean] values of this [column][DataColumn].
 */
public operator fun DataColumn<Boolean>.not(): DataColumn<Boolean> = map { !it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the inverse [Boolean] values
 * of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]. Each `null` value in the original [DataColumn] is preserved.
 *
 * Non-null values are transformed using the logical `not` operation:
 * `true` becomes `false`, and `false` becomes `true`. `null` values remain `null`.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of financial transactions,
 * // find which transactions failed
 * df.select { !isSuccessful }
 * // or
 * !df.isSuccessful
 * ```
 *
 * @return A [DataColumn] containing the negated [Boolean] values of this [column][DataColumn],
 * while preserving `null` values.
 */
@JvmName("notBooleanNullable")
public operator fun DataColumn<Boolean?>.not(): DataColumn<Boolean?> = map { it?.not() }

/**
 * Returns a [ColumnReference] containing the inverse [Boolean] values
 * of this [ColumnReference].
 *
 * Each value in this [reference][ColumnReference] is transformed
 * using the logical `not` operation: `true` becomes `false`, and `false` becomes `true`.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of financial transactions,
 * // find which transactions failed
 * df.select { !isSuccessful }
 * // or
 * !df.isSuccessful
 * ```
 *
 * @return A [ColumnReference] containing the negated [Boolean] values of this [reference][ColumnReference].
 */
public operator fun ColumnReference<Boolean>.not(): ColumnReference<Boolean> = map { !it }

/**
 * Returns a [ColumnReference] containing the inverse [Boolean] values
 * of this [ColumnReference]. Each `null` value in the original [ColumnReference] is preserved.
 *
 * Non-null values are transformed using the logical `not` operation:
 * `true` becomes `false`, and `false` becomes `true`. `null` values remain `null`.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of financial transactions,
 * // find which transactions failed
 * df.select { !isSuccessful }
 * // or
 * !df.isSuccessful
 * ```
 *
 * @return A [ColumnReference] containing the negated [Boolean] values
 * of the original [reference][ColumnReference], while preserving `null` values.
 */
@JvmName("notBooleanNullable")
public operator fun ColumnReference<Boolean?>.not(): ColumnReference<Boolean?> = map { it?.not() }

// endregion

// region ColumnPlusNumber

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Int>.plus(value: Int): DataColumn<Int> = map { it + value }

/**
 * Returns a [ColumnReference] where each element is the result of adding [value]
 * to the corresponding element of this [ColumnReference].
 *
 * That is, [value] is added to each element of this [ColumnReference].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * See also [minus][ColumnReference.minus],
 * [times][ColumnReference.times], [div][ColumnReference.div].
 *
 * @param [value] The value to add to each element of this [ColumnReference].
 *
 * @return A [ColumnReference] containing the results of adding [value]
 * to each element of this [ColumnReference].
 */
public operator fun ColumnReference<Int>.plus(value: Int): ColumnReference<Int> = map { it + value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * `null` values are not changed by this operation.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("plusIntNullable")
public operator fun DataColumn<Int?>.plus(value: Int): DataColumn<Int?> = map { it?.plus(value) }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10.0
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("plusInt")
public operator fun DataColumn<Int>.plus(value: Double): DataColumn<Double> = map { it + value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("plusDouble")
public operator fun DataColumn<Double>.plus(value: Int): DataColumn<Double> = map { it + value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10L
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Long>.plus(value: Long): DataColumn<Long> = map { it + value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10.0
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Double>.plus(value: Double): DataColumn<Double> = map { it + value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + BigDecimal("12.03")
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigDecimal>.plus(value: BigDecimal): DataColumn<BigDecimal> = map { it + value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [value]
 * to the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is added to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of current disks usage in bits,
 * // compute the total disks usage if the size of a file is added
 * val totalDisksUsage = df.diskUsage + BigInteger("12345678900")
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [value] The value to add to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [value]
 * to each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigInteger>.plus(value: BigInteger): DataColumn<BigInteger> = map { it + value }

// endregion

// region NumberPlusColumn

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [Int].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [Int].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [Int].
 */
public operator fun Int.plus(column: DataColumn<Int>): DataColumn<Int> = column.map { this + it }

/**
 * Returns a [ColumnReference] where each element is the result of adding
 * the corresponding element of [column] to this [Int].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * See also [minus][ColumnReference.minus],
 * [times][ColumnReference.times], [div][ColumnReference.div].
 *
 * @param [column] A [ColumnReference] containing the elements to add
 * to this [Int].
 *
 * @return A [ColumnReference] containing the results of adding
 * the corresponding element of [column] to this [Int].
 */
public operator fun Int.plus(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this + it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [Int].
 *
 * `null` values from the original [column]
 * remain `null` values in the resulting [DataColumn].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [Int].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [Int].
 */
@JvmName("plusNullable")
public operator fun Int.plus(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.plus(this) }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [Double].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10.0 + df.distanceFee
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [Double].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [Double].
 */
@JvmName("doublePlus")
public operator fun Double.plus(column: DataColumn<Int>): DataColumn<Double> = column.map { this + it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [Int].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [Int].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [Int].
 */
@JvmName("intPlus")
public operator fun Int.plus(column: DataColumn<Double>): DataColumn<Double> = column.map { this + it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [Double].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10.0 + df.distanceFee
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [Double].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [Double].
 */
public operator fun Double.plus(column: DataColumn<Double>): DataColumn<Double> = column.map { this + it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [Long].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10L + df.distanceFee
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [Long].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [Long].
 */
public operator fun Long.plus(column: DataColumn<Long>): DataColumn<Long> = column.map { this + it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [BigDecimal].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * BigDecimal("12.03") + df.distanceFee
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [BigDecimal].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [BigDecimal].
 */
public operator fun BigDecimal.plus(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this + it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [column] to this [BigInteger].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given the current disk usage in bits,
 * // and a DataFrame of file sizes in bits, compute the total disk usage if each file is added
 * val diskUsage = BigInteger("12345678900") + df.fileSize
 * ```
 *
 * See also [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [BigInteger].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [column] to this [BigInteger].
 */
public operator fun BigInteger.plus(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this + it }

// endregion

// region ColumnPlusString

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] of [String] values
 * obtained by converting each element of this [AnyCol][org.jetbrains.kotlinx.dataframe.AnyCol]
 * to a [String] and concatenating it with [str].
 *
 * `null` values are converted to the string `"null"`.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of temperature measurements, display temperatures with units
 * df.select { temperature + " °C" }
 * // or
 * df.temperature + " °C"
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus].
 *
 * @param [str] The [String] to append to each element of this [AnyCol][org.jetbrains.kotlinx.dataframe.AnyCol].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] of [String] values where each element is the result
 * of concatenating the corresponding element of this [AnyCol][org.jetbrains.kotlinx.dataframe.AnyCol] with [str].
 */
public operator fun AnyCol.plus(str: String): DataColumn<String> = map { it.toString() + str }

/**
 * Returns a [ColumnReference] of [String] values
 * obtained by converting each element of this [ColumnReference]
 * to a [String] and concatenating it with [str].
 *
 * `null` values are converted to the string `"null"`.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of temperature measurements, display temperatures with units
 * df.select { temperature + " °C" }
 * // or
 * df.temperature + " °C"
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus].
 *
 * @param [str] The [String] to append to each element of this [ColumnReference].
 *
 * @return A [ColumnReference] of [String] values where each element is the result
 * of concatenating the corresponding element of this [ColumnReference] with [str].
 */
public operator fun ColumnReference<Any?>.plus(str: String): ColumnReference<String> = map { it.toString() + str }

// endregion

// region ColumnMinusNumber

/**
 * Returns a [DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * See also [plus][DataColumn.plus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @return A [DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn].
 */
private interface ColumnMinusNumberDocs {
    // the type of the column accepted and returned by the function
    typealias COLUMN_MINUS_NUMBER_COLUMN_TYPE = Nothing

    // the example used in the documentation
    typealias COLUMN_MINUS_NUMBER_EXAMPLE = Nothing

    // adjustment of the example to use different types of numbers
    typealias COLUMN_MINUS_NUMBER_EXAMPLE_NUMBER = Nothing

    // `See also` section in the documentation
    typealias COLUMN_MINUS_NUMBER_SEE_ALSO = Nothing

    // info about handling `null` values
    typealias COLUMN_MINUS_NUMBER_NULL_NOTE = Nothing
}

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Int>.minus(value: Int): DataColumn<Int> = map { it - value }

/**
 * Returns a [ColumnReference] where each element is the result of subtracting [value]
 * from the corresponding element of this [ColumnReference].
 *
 * That is, [value] is subtracted from each element of the [ColumnReference].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * See also [plus][ColumnReference.plus],
 * [times][ColumnReference.times], [div][ColumnReference.div].
 *
 * @return A [ColumnReference] containing the results of subtracting [value]
 * from each element of this [ColumnReference].
 */
public operator fun ColumnReference<Int>.minus(value: Int): ColumnReference<Int> = map { it - value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * `null` values are not changed by this operation.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("minusIntNullable")
public operator fun DataColumn<Int?>.minus(value: Int): DataColumn<Int?> = map { it?.minus(value) }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10.0
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("minusInt")
public operator fun DataColumn<Int>.minus(value: Double): DataColumn<Double> = map { it - value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("minusDouble")
public operator fun DataColumn<Double>.minus(value: Int): DataColumn<Double> = map { it - value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10.0
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Double>.minus(value: Double): DataColumn<Double> = map { it - value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10L
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Long>.minus(value: Long): DataColumn<Long> = map { it - value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - BigDecimal("12.03")
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigDecimal>.minus(value: BigDecimal): DataColumn<BigDecimal> = map { it - value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [value]
 * from the corresponding element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [value] is subtracted from each element of the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of current disks usage in bits,
 * // compute the total disks usage if a file is deleted
 * val totalDisksUsage = df.diskUsage - BigInteger("12345678900")
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [value]
 * from each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigInteger>.minus(value: BigInteger): DataColumn<BigInteger> = map { it - value }

// endregion

// region NumberMinusColumn

/**
 * Returns a [DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [Int].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * See also [plus][DataColumn.plus], [times][DataColumn.times], [div][DataColumn.div].
 *
 * @param [column] A [DataColumn] containing the elements to subtract
 * from this [Int].
 *
 * @return A [DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [Int].
 */
private interface NumberMinusColumnDocs {
    // the type of the column passed to the function and returned by the function
    typealias NUMBER_MINUS_COLUMN_COLUMN_TYPE = Nothing

    // the type of the number on which the function is applied
    typealias NUMBER_MINUS_COLUMN_NUMBER_TYPE = Nothing

    // the example used in the documentation
    typealias NUMBER_MINUS_COLUMN_EXAMPLE = Nothing

    // adjustment of the example to use different types of numbers
    typealias NUMBER_MINUS_COLUMN_EXAMPLE_NUMBER = Nothing

    // `See also` section in the documentation
    typealias NUMBER_MINUS_COLUMN_SEE_ALSO = Nothing

    // info about handling `null` values
    typealias NUMBER_MINUS_COLUMN_NULL_NOTE = Nothing
}

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [Int].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [Int].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [Int].
 */
public operator fun Int.minus(column: DataColumn<Int>): DataColumn<Int> = column.map { this - it }

/**
 * Returns a [ColumnReference] where each element is the result of subtracting
 * the corresponding element of [column] from this [Int].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * See also [plus][ColumnReference.plus], [times][ColumnReference.times], [div][ColumnReference.div].
 *
 * @param [column] A [ColumnReference] containing the elements to subtract
 * from this [Int].
 *
 * @return A [ColumnReference] containing the results
 * of subtracting the corresponding element of [column] from this [Int].
 */
public operator fun Int.minus(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this - it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [Int].
 *
 * `null` values from the original [column] remain `null` values in the resulting [DataColumn].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [Int].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [Int].
 */
@JvmName("minusNullable")
public operator fun Int.minus(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.let { this - it } }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [Double].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000.0 - df.expenses
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [Double].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [Double].
 */
@JvmName("doubleMinus")
public operator fun Double.minus(column: DataColumn<Int>): DataColumn<Double> = column.map { this - it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [Int].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [Int].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [Int].
 */
@JvmName("intMinus")
public operator fun Int.minus(column: DataColumn<Double>): DataColumn<Double> = column.map { this - it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [Double].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000.0 - df.expenses
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [Double].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [Double].
 */
public operator fun Double.minus(column: DataColumn<Double>): DataColumn<Double> = column.map { this - it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [Long].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000L - df.expenses
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [Long].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [Long].
 */
public operator fun Long.minus(column: DataColumn<Long>): DataColumn<Long> = column.map { this - it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [BigDecimal].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = BigDecimal("1000.00") - df.expenses
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [BigDecimal].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [BigDecimal].
 */
public operator fun BigDecimal.minus(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this - it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [column] from this [BigInteger].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given the current disk usage in bits,
 * // and a DataFrame of file sizes in bits, compute the total disk usage if any file is deleted
 * val diskUsage = BigInteger("12345678900") - df.fileSize
 * ```
 *
 * See also [plus][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [times][org.jetbrains.kotlinx.dataframe.DataColumn.times], [div][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [BigInteger].
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [column] from this [BigInteger].
 */
public operator fun BigInteger.minus(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this - it }

// endregion

// region UnaryMinus

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Int>.unaryMinus(): DataColumn<Int> = map { -it }

/**
 * Returns a [ColumnReference] containing negatives
 * of the corresponding elements of this [ColumnReference].
 *
 * The sign of each element in this [ColumnReference] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [ColumnReference] containing negatives
 * of the corresponding elements of this [ColumnReference].
 */
public operator fun ColumnReference<Int>.unaryMinus(): ColumnReference<Int> = map { -it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 * `null` values are not changed by this operation.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusIntNullable")
public operator fun DataColumn<Int?>.unaryMinus(): DataColumn<Int?> = map { it?.unaryMinus() }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusDouble")
public operator fun DataColumn<Double>.unaryMinus(): DataColumn<Double> = map { -it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusLong")
public operator fun DataColumn<Long>.unaryMinus(): DataColumn<Long> = map { -it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusBigDecimal")
public operator fun DataColumn<BigDecimal>.unaryMinus(): DataColumn<BigDecimal> = map { -it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusBigInteger")
public operator fun DataColumn<BigInteger>.unaryMinus(): DataColumn<BigInteger> = map { -it }

// endregion

// region Times

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<Int>.times(value: Int): DataColumn<Int> = map { it * value }

/**
 * Returns a [ColumnReference] containing the results
 * of multiplying each element of this [ColumnReference] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * See also [div][ColumnReference.div], [plus][ColumnReference.plus], [minus][ColumnReference.minus].
 *
 * @param [value] The value to multiply each element of this [ColumnReference] by.
 *
 * @return A [ColumnReference] containing the results
 * of multiplying each element of this [ColumnReference] by [value].
 */
public operator fun ColumnReference<Int>.times(value: Int): ColumnReference<Int> = map { it * value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 * `null` values are not changed by this operation.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
@JvmName("timesIntNullable")
public operator fun DataColumn<Int?>.times(value: Int): DataColumn<Int?> = map { it?.times(value) }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000.0
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
@JvmName("timesInt")
public operator fun DataColumn<Int>.times(value: Double): DataColumn<Double> = map { it * value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
@JvmName("timesDouble")
public operator fun DataColumn<Double>.times(value: Int): DataColumn<Double> = map { it * value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000.0
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<Double>.times(value: Double): DataColumn<Double> = map { it * value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000L
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<Long>.times(value: Long): DataColumn<Long> = map { it * value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of product prices, compute the price including a 20% tax
 * val priceWithTax = df.price * BigDecimal("1.20")
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<BigDecimal>.times(value: BigDecimal): DataColumn<BigDecimal> = map { it * value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of file sizes in bits, compute the total size of multiple copies of each file
 * val totalSize = df.fileSize * BigInteger("12345")
 * ```
 *
 * See also [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to multiply each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<BigInteger>.times(value: BigInteger): DataColumn<BigInteger> = map { it * value }

// endregion

// region ColumnDivNumber

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [value] is equal to zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<Int>.div(value: Int): DataColumn<Int> = map { it / value }

/**
 * Returns a [ColumnReference] containing the results of dividing
 * each element of this [ColumnReference] by [value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10
 * ```
 *
 * See also [times][ColumnReference.times],
 * [plus][ColumnReference.plus], [minus][ColumnReference.minus].
 *
 * @param [value] The value to divide each element of this [ColumnReference] by.
 *
 * @throws [ArithmeticException] if [value] is equal to zero.
 *
 * @return A [ColumnReference] containing the results of dividing
 * each element of this [ColumnReference] by [value].
 */
public operator fun ColumnReference<Int>.div(value: Int): ColumnReference<Int> = map { it / value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 * `null` values are not changed by this operation.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [value] is equal to zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
@JvmName("divIntNullable")
public operator fun DataColumn<Int?>.div(value: Int): DataColumn<Int?> = map { it?.div(value) }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of distances in meters, convert them to kilometers
 * val distanceKm = df.distanceMeters / 1000.0
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
@JvmName("divInt")
public operator fun DataColumn<Int>.div(value: Double): DataColumn<Double> = map { it / value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of hourly travel distances, compute the average distance traveled per minute
 * val distancePerMinute = df.distancePerHour / 60
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
@JvmName("divDouble")
public operator fun DataColumn<Double>.div(value: Int): DataColumn<Double> = map { it / value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of distances in meters, convert them to kilometers
 * val distanceKm = df.distanceMeters / 1000.0
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<Double>.div(value: Double): DataColumn<Double> = map { it / value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10L
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [value] is equal to zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<Long>.div(value: Long): DataColumn<Long> = map { it / value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of distances in miles, convert them to kilometers
 * val distanceKm = df.distanceMiles / BigDecimal("0.62137")
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [value] is equal to zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<BigDecimal>.div(value: BigDecimal): DataColumn<BigDecimal> = map { it / value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts stored as BigInteger values,
 * // compute how many full batches of 1,000 items can be formed
 * val batches = df.itemCount / BigInteger("1000")
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [value] The value to divide each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [value] is equal to zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] by [value].
 */
public operator fun DataColumn<BigInteger>.div(value: BigInteger): DataColumn<BigInteger> = map { it / value }

// endregion

// region NumberDivColumn

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [Int] by each element of [column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of tasks with estimated hours,
 * // compute how many tasks can fit into a fixed 40-hour work week
 * val tasksPerWeek = 40 / df.estimatedHours
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [Int] by.
 *
 * @throws [ArithmeticException] if [column] contains zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [Int] by each element of [column].
 */
public operator fun Int.div(column: DataColumn<Int>): DataColumn<Int> = column.map { this / it }

/**
 * Returns a [ColumnReference] containing the results of dividing
 * this [Int] by each element of [column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of tasks with estimated hours,
 * // compute how many tasks can fit into a fixed 40-hour work week
 * val tasksPerWeek = 40 / df.estimatedHours
 * ```
 *
 * See also [times][ColumnReference.times],
 * [plus][ColumnReference.plus], [minus][ColumnReference.minus].
 *
 * @param [column] A [ColumnReference] containing the elements
 * to divide this [Int] by.
 *
 * @throws [ArithmeticException] if [column] contains zero.
 *
 * @return A [ColumnReference] containing the results
 * of dividing this [Int] by each element of [column].
 */
public operator fun Int.div(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this / it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [Int] by each element of [column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 * If an element of [column] is `null`,
 * the corresponding value in the resulting [DataColumn] is also `null`.
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of tasks with estimated hours,
 * // compute how many tasks can fit into a fixed 40-hour work week
 * val tasksPerWeek = 40 / df.estimatedHours
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [Int] by.
 *
 * @throws [ArithmeticException] if [column] contains zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [Int] by each element of [column].
 */
@JvmName("divNullable")
public operator fun Int.div(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.let { this / it } }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [Double] by each element of [column].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // Given a marketing budget of 1,000 euros,
 * // compute the cost per acquired customer for each campaign
 * val costPerCustomer = 1000.0 / df.acquiredCustomers
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [Double] by.
 *
 * @throws
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [Double] by each element of [column].
 */
@JvmName("doubleDiv")
public operator fun Double.div(column: DataColumn<Int>): DataColumn<Double> = column.map { this / it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [Int] by each element of [column].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of prices of a square meter in different places,
 * // compute how many square meters can be bought with a budget of 500 thousand euros
 * val squareMeters = 500_000 / df.pricePerSquareMeter
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [Int] by.
 *
 * @throws
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [Int] by each element of [column].
 */
@JvmName("intDiv")
public operator fun Int.div(column: DataColumn<Double>): DataColumn<Double> = column.map { this / it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [Double] by each element of [column].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of prices of a square meter in different places,
 * // compute how many square meters can be bought with a budget of 500 thousand euros
 * val squareMeters = 500_000.0 / df.pricePerSquareMeter
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [Double] by.
 *
 * @throws
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [Double] by each element of [column].
 */
public operator fun Double.div(column: DataColumn<Double>): DataColumn<Double> = column.map { this / it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [Long] by each element of [column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of file sizes in bits,
 * // compute how many copies of each file can fit into the given storage capacity
 * val fileCopies = 10_000_000_000L / df.fileSize
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [Long] by.
 *
 * @throws [ArithmeticException] if [column] contains zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [Long] by each element of [column].
 */
public operator fun Long.div(column: DataColumn<Long>): DataColumn<Long> = column.map { this / it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [BigDecimal] by each element of [column].
 *
 *
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of prices of a product per gram,
 * // compute the amount of product that can be bought with a budget of 3,451.76 euros
 * val productAmount = BigDecimal("3451.76") / df.pricePerGram
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [BigDecimal] by.
 *
 * @throws [ArithmeticException] if [column] contains zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [BigDecimal] by each element of [column].
 */
public operator fun BigDecimal.div(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this / it }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [BigInteger] by each element of [column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of file sizes in bits,
 * // compute how many copies of each file can fit into the given storage capacity
 * val fileCopies = BigInteger("10000000000") / df.fileSize
 * ```
 *
 * See also [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus].
 *
 * @param [column] A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [BigInteger] by.
 *
 * @throws [ArithmeticException] if [column] contains zero.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [BigInteger] by each element of [column].
 */
public operator fun BigInteger.div(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this / it }

// endregion

// region Compare

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [results][Boolean] of comparing each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [value] for equality using the `==` operator.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders with statuses represented as strings,
 * // create a column that indicates whether each order is canceled
 * val isCanceled = df.status eq "canceled"
 * ```
 *
 * See also [neq][DataColumn.neq], [gt][DataColumn.gt], [lt][DataColumn.lt].
 *
 * @param [value] The value to compare each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements equal to [value], and `false` otherwise.
 */
public infix fun <T> DataColumn<T>.eq(value: T): DataColumn<Boolean> = map { it == value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [results][Boolean] of comparing each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [value] for inequality using the `!=` operator.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders with statuses represented as strings,
 * // create a column that indicates which orders are not completed
 * val isNotCompleted = df.status neq "completed"
 * ```
 *
 * See also [eq][DataColumn.eq], [gt][DataColumn.gt], [lt][DataColumn.lt].
 *
 * @param [value] The value to compare each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements not equal to [value], and `false` otherwise.
 */
public infix fun <T> DataColumn<T>.neq(value: T): DataColumn<Boolean> = map { it != value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [results][Boolean] of comparing each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [value] using the `>` operator.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders,
 * // create a column that indicates which orders cost more than 1,000 euros
 * val isExpensive = df.orderCost gt 1000
 * ```
 *
 * See also [eq][DataColumn.eq], [neq][DataColumn.neq], [lt][DataColumn.lt].
 *
 * @param [value] The value to compare each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements greater than [value], and `false` otherwise.
 */
public infix fun <T : Comparable<T>> DataColumn<T>.gt(value: T): DataColumn<Boolean> = map { it > value }

/**
 * Returns a [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [results][Boolean] of comparing each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [value] using the `<` operator.
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders,
 * // create a column that indicates which orders cost less than 20 euros
 * val isCheap = df.orderCost lt 20
 * ```
 *
 * See also [eq][DataColumn.eq], [neq][DataColumn.neq], [gt][DataColumn.gt].
 *
 * @param [value] The value to compare each element of this [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements less than [value], and `false` otherwise.
 */
public infix fun <T : Comparable<T>> DataColumn<T>.lt(value: T): DataColumn<Boolean> = map { it < value }
// endregion
