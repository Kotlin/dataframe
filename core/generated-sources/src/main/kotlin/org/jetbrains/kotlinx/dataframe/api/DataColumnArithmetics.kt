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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import java.math.BigDecimal
import java.math.BigInteger

// region Not

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the inverse [<code>Boolean</code>][Boolean] values
 * of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * Each element in this [<code>column</code>][DataColumn] is transformed
 * using the logical `not` operation: `true` becomes `false`, and `false` becomes `true`.
 *
 * For more information: [See `not` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#not)
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
 * @return A [<code>DataColumn</code>][DataColumn] containing the negated [<code>Boolean</code>][Boolean] values of this [<code>column</code>][DataColumn].
 */
public operator fun DataColumn<Boolean>.not(): DataColumn<Boolean> = map { !it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the inverse [<code>Boolean</code>][Boolean] values
 * of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]. Each `null` value in the original [<code>DataColumn</code>][DataColumn] is preserved.
 *
 * Non-null values are transformed using the logical `not` operation:
 * `true` becomes `false`, and `false` becomes `true`. `null` values remain `null`.
 *
 * For more information: [See `not` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#not)
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
 * @return A [<code>DataColumn</code>][DataColumn] containing the negated [<code>Boolean</code>][Boolean] values of this [<code>column</code>][DataColumn],
 * while preserving `null` values.
 */
@JvmName("notBooleanNullable")
public operator fun DataColumn<Boolean?>.not(): DataColumn<Boolean?> = map { it?.not() }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] containing the inverse [<code>Boolean</code>][Boolean] values
 * of this [<code>ColumnReference</code>][ColumnReference].
 *
 * Each value in this [<code>reference</code>][ColumnReference] is transformed
 * using the logical `not` operation: `true` becomes `false`, and `false` becomes `true`.
 *
 * For more information: [See `not` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#not)
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
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the negated [<code>Boolean</code>][Boolean] values of this [<code>reference</code>][ColumnReference].
 */
public operator fun ColumnReference<Boolean>.not(): ColumnReference<Boolean> = map { !it }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] containing the inverse [<code>Boolean</code>][Boolean] values
 * of this [<code>ColumnReference</code>][ColumnReference]. Each `null` value in the original [<code>ColumnReference</code>][ColumnReference] is preserved.
 *
 * Non-null values are transformed using the logical `not` operation:
 * `true` becomes `false`, and `false` becomes `true`. `null` values remain `null`.
 *
 * For more information: [See `not` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#not)
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
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the negated [<code>Boolean</code>][Boolean] values
 * of the original [<code>reference</code>][ColumnReference], while preserving `null` values.
 */
@JvmName("notBooleanNullable")
public operator fun ColumnReference<Boolean?>.not(): ColumnReference<Boolean?> = map { it?.not() }

// endregion

// region ColumnPlusNumber

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Int>.plus(value: Int): DataColumn<Int> = map { it + value }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>ColumnReference</code>][ColumnReference].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>ColumnReference</code>][ColumnReference].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][ColumnReference.minus],
 * [<code>times</code>][ColumnReference.times], [<code>div</code>][ColumnReference.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * @param [value] The value to add to each element of this [<code>ColumnReference</code>][ColumnReference].
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>ColumnReference</code>][ColumnReference].
 */
public operator fun ColumnReference<Int>.plus(value: Int): ColumnReference<Int> = map { it + value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * `null` values are not changed by this operation.
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("plusIntNullable")
public operator fun DataColumn<Int?>.plus(value: Int): DataColumn<Int?> = map { it?.plus(value) }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10.0
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("plusInt")
public operator fun DataColumn<Int>.plus(value: Double): DataColumn<Double> = map { it + value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("plusDouble")
public operator fun DataColumn<Double>.plus(value: Int): DataColumn<Double> = map { it + value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10L
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Long>.plus(value: Long): DataColumn<Long> = map { it + value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + 10.0
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Double>.plus(value: Double): DataColumn<Double> = map { it + value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + BigDecimal("12.03")
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigDecimal>.plus(value: BigDecimal): DataColumn<BigDecimal> = map { it + value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding [<code>value</code>][value]
 * to the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is added to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of current disks usage in bits,
 * // compute the total disks usage if the size of a file is added
 * val totalDisksUsage = df.diskUsage + BigInteger("12345678900")
 * ```
 *
 * @param [value] The value to add to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding [<code>value</code>][value]
 * to each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigInteger>.plus(value: BigInteger): DataColumn<BigInteger> = map { it + value }

// endregion

// region NumberPlusColumn

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>Int</code>][Int].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 */
public operator fun Int.plus(column: DataColumn<Int>): DataColumn<Int> = column.map { this + it }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][ColumnReference.minus],
 * [<code>times</code>][ColumnReference.times], [<code>div</code>][ColumnReference.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * @param [column] A [<code>ColumnReference</code>][ColumnReference] containing the elements to add
 * to this [<code>Int</code>][Int].
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 */
public operator fun Int.plus(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this + it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 *
 * `null` values from the original [<code>column</code>][column]
 * remain `null` values in the resulting [<code>DataColumn</code>][DataColumn].
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>Int</code>][Int].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 */
@JvmName("plusNullable")
public operator fun Int.plus(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.plus(this) }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Double</code>][Double].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10.0 + df.distanceFee
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>Double</code>][Double].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Double</code>][Double].
 */
@JvmName("doublePlus")
public operator fun Double.plus(column: DataColumn<Int>): DataColumn<Double> = column.map { this + it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10 + df.distanceFee
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>Int</code>][Int].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Int</code>][Int].
 */
@JvmName("intPlus")
public operator fun Int.plus(column: DataColumn<Double>): DataColumn<Double> = column.map { this + it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Double</code>][Double].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10.0 + df.distanceFee
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>Double</code>][Double].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Double</code>][Double].
 */
public operator fun Double.plus(column: DataColumn<Double>): DataColumn<Double> = column.map { this + it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Long</code>][Long].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * 10L + df.distanceFee
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>Long</code>][Long].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>Long</code>][Long].
 */
public operator fun Long.plus(column: DataColumn<Long>): DataColumn<Long> = column.map { this + it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>BigDecimal</code>][BigDecimal].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * BigDecimal("12.03") + df.distanceFee
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>BigDecimal</code>][BigDecimal].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>BigDecimal</code>][BigDecimal].
 */
public operator fun BigDecimal.plus(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this + it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>BigInteger</code>][BigInteger].
 *
 *
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>minus</code>][DataColumn.minus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given the current disk usage in bits,
 * // and a DataFrame of file sizes in bits, compute the total disk usage if each file is added
 * val diskUsage = BigInteger("12345678900") + df.fileSize
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to add
 * to this [<code>BigInteger</code>][BigInteger].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of adding
 * the corresponding element of [<code>column</code>][column] to this [<code>BigInteger</code>][BigInteger].
 */
public operator fun BigInteger.plus(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this + it }

// endregion

// region ColumnPlusString

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] of [<code>String</code>][String] values
 * obtained by converting each element of this [<code>AnyCol</code>][org.jetbrains.kotlinx.dataframe.AnyCol]
 * to a [<code>String</code>][String] and concatenating it with [<code>str</code>][str].
 *
 * `null` values are converted to the string `"null"`.
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of temperature measurements, display temperatures with units
 * df.select { temperature + " °C" }
 * // or
 * df.temperature + " °C"
 * ```
 *
 * @param [str] The [<code>String</code>][String] to append to each element of this [<code>AnyCol</code>][org.jetbrains.kotlinx.dataframe.AnyCol].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] of [<code>String</code>][String] values where each element is the result
 * of concatenating the corresponding element of this [<code>AnyCol</code>][org.jetbrains.kotlinx.dataframe.AnyCol] with [<code>str</code>][str].
 */
public operator fun AnyCol.plus(str: String): DataColumn<String> = map { it.toString() + str }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] of [<code>String</code>][String] values
 * obtained by converting each element of this [<code>ColumnReference</code>][ColumnReference]
 * to a [<code>String</code>][String] and concatenating it with [<code>str</code>][str].
 *
 * `null` values are converted to the string `"null"`.
 *
 * For more information: [See `plus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#plus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of temperature measurements, display temperatures with units
 * df.select { temperature + " °C" }
 * // or
 * df.temperature + " °C"
 * ```
 *
 * @param [str] The [<code>String</code>][String] to append to each element of this [<code>ColumnReference</code>][ColumnReference].
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] of [<code>String</code>][String] values where each element is the result
 * of concatenating the corresponding element of this [<code>ColumnReference</code>][ColumnReference] with [<code>str</code>][str].
 */
public operator fun ColumnReference<Any?>.plus(str: String): ColumnReference<String> = map { it.toString() + str }

// endregion

// region ColumnMinusNumber

/**
 * Returns a [<code>DataColumn</code>][DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][DataColumn.plus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * @return A [<code>DataColumn</code>][DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][DataColumn].
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
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Int>.minus(value: Int): DataColumn<Int> = map { it - value }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>ColumnReference</code>][ColumnReference].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>ColumnReference</code>][ColumnReference].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][ColumnReference.plus],
 * [<code>times</code>][ColumnReference.times], [<code>div</code>][ColumnReference.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>ColumnReference</code>][ColumnReference].
 */
public operator fun ColumnReference<Int>.minus(value: Int): ColumnReference<Int> = map { it - value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * `null` values are not changed by this operation.
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("minusIntNullable")
public operator fun DataColumn<Int?>.minus(value: Int): DataColumn<Int?> = map { it?.minus(value) }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10.0
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("minusInt")
public operator fun DataColumn<Int>.minus(value: Double): DataColumn<Double> = map { it - value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("minusDouble")
public operator fun DataColumn<Double>.minus(value: Int): DataColumn<Double> = map { it - value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10.0
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Double>.minus(value: Double): DataColumn<Double> = map { it - value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - 10L
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Long>.minus(value: Long): DataColumn<Long> = map { it - value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - BigDecimal("12.03")
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigDecimal>.minus(value: BigDecimal): DataColumn<BigDecimal> = map { it - value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting [<code>value</code>][value]
 * from the corresponding element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * That is, [<code>value</code>][value] is subtracted from each element of the [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of current disks usage in bits,
 * // compute the total disks usage if a file is deleted
 * val totalDisksUsage = df.diskUsage - BigInteger("12345678900")
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of subtracting [<code>value</code>][value]
 * from each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<BigInteger>.minus(value: BigInteger): DataColumn<BigInteger> = map { it - value }

// endregion

// region NumberMinusColumn

/**
 * Returns a [<code>DataColumn</code>][DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][DataColumn.plus], [<code>times</code>][DataColumn.times], [<code>div</code>][DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][DataColumn] containing the elements to subtract
 * from this [<code>Int</code>][Int].
 *
 * @return A [<code>DataColumn</code>][DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
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
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>Int</code>][Int].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 */
public operator fun Int.minus(column: DataColumn<Int>): DataColumn<Int> = column.map { this - it }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][ColumnReference.plus], [<code>times</code>][ColumnReference.times], [<code>div</code>][ColumnReference.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * @param [column] A [<code>ColumnReference</code>][ColumnReference] containing the elements to subtract
 * from this [<code>Int</code>][Int].
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 */
public operator fun Int.minus(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this - it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 *
 * `null` values from the original [<code>column</code>][column] remain `null` values in the resulting [<code>DataColumn</code>][DataColumn].
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>Int</code>][Int].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 */
@JvmName("minusNullable")
public operator fun Int.minus(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.let { this - it } }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Double</code>][Double].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000.0 - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>Double</code>][Double].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Double</code>][Double].
 */
@JvmName("doubleMinus")
public operator fun Double.minus(column: DataColumn<Int>): DataColumn<Double> = column.map { this - it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000 - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>Int</code>][Int].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Int</code>][Int].
 */
@JvmName("intMinus")
public operator fun Int.minus(column: DataColumn<Double>): DataColumn<Double> = column.map { this - it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Double</code>][Double].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000.0 - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>Double</code>][Double].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Double</code>][Double].
 */
public operator fun Double.minus(column: DataColumn<Double>): DataColumn<Double> = column.map { this - it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>Long</code>][Long].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = 1000L - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>Long</code>][Long].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>Long</code>][Long].
 */
public operator fun Long.minus(column: DataColumn<Long>): DataColumn<Long> = column.map { this - it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>BigDecimal</code>][BigDecimal].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = BigDecimal("1000.00") - df.expenses
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>BigDecimal</code>][BigDecimal].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>BigDecimal</code>][BigDecimal].
 */
public operator fun BigDecimal.minus(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this - it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] where each element is the result of subtracting
 * the corresponding element of [<code>column</code>][column] from this [<code>BigInteger</code>][BigInteger].
 *
 *
 *
 * For more information: [See `minus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#minus)
 *
 * See also [<code>plus</code>][org.jetbrains.kotlinx.dataframe.DataColumn.plus], [<code>times</code>][org.jetbrains.kotlinx.dataframe.DataColumn.times], [<code>div</code>][org.jetbrains.kotlinx.dataframe.DataColumn.div].
 *
 * ### Example
 * ```kotlin
 * // Given the current disk usage in bits,
 * // and a DataFrame of file sizes in bits, compute the total disk usage if any file is deleted
 * val diskUsage = BigInteger("12345678900") - df.fileSize
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements to subtract
 * from this [<code>BigInteger</code>][BigInteger].
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of subtracting the corresponding element of [<code>column</code>][column] from this [<code>BigInteger</code>][BigInteger].
 */
public operator fun BigInteger.minus(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this - it }

// endregion

// region UnaryMinus

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * For more information: [See `unaryMinus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#unary-minus)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
public operator fun DataColumn<Int>.unaryMinus(): DataColumn<Int> = map { -it }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] containing negatives
 * of the corresponding elements of this [<code>ColumnReference</code>][ColumnReference].
 *
 * The sign of each element in this [<code>ColumnReference</code>][ColumnReference] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * For more information: [See `unaryMinus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#unary-minus)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing negatives
 * of the corresponding elements of this [<code>ColumnReference</code>][ColumnReference].
 */
public operator fun ColumnReference<Int>.unaryMinus(): ColumnReference<Int> = map { -it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 * `null` values are not changed by this operation.
 *
 * For more information: [See `unaryMinus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#unary-minus)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusIntNullable")
public operator fun DataColumn<Int?>.unaryMinus(): DataColumn<Int?> = map { it?.unaryMinus() }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * For more information: [See `unaryMinus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#unary-minus)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusDouble")
public operator fun DataColumn<Double>.unaryMinus(): DataColumn<Double> = map { -it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * For more information: [See `unaryMinus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#unary-minus)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusLong")
public operator fun DataColumn<Long>.unaryMinus(): DataColumn<Long> = map { -it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * For more information: [See `unaryMinus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#unary-minus)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusBigDecimal")
public operator fun DataColumn<BigDecimal>.unaryMinus(): DataColumn<BigDecimal> = map { -it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 *
 * The sign of each element in this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] is flipped:
 * positive values become negative, and negative values become positive.
 *
 *
 *
 * For more information: [See `unaryMinus` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#unary-minus)
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing negatives
 * of the corresponding elements of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 */
@JvmName("unaryMinusBigInteger")
public operator fun DataColumn<BigInteger>.unaryMinus(): DataColumn<BigInteger> = map { -it }

// endregion

// region Times

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<Int>.times(value: Int): DataColumn<Int> = map { it * value }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] containing the results
 * of multiplying each element of this [<code>ColumnReference</code>][ColumnReference] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][ColumnReference.div], [<code>plus</code>][ColumnReference.plus], [<code>minus</code>][ColumnReference.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>ColumnReference</code>][ColumnReference] by.
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the results
 * of multiplying each element of this [<code>ColumnReference</code>][ColumnReference] by [<code>value</code>][value].
 */
public operator fun ColumnReference<Int>.times(value: Int): ColumnReference<Int> = map { it * value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 * `null` values are not changed by this operation.
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
@JvmName("timesIntNullable")
public operator fun DataColumn<Int?>.times(value: Int): DataColumn<Int?> = map { it?.times(value) }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000.0
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
@JvmName("timesInt")
public operator fun DataColumn<Int>.times(value: Double): DataColumn<Double> = map { it * value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
@JvmName("timesDouble")
public operator fun DataColumn<Double>.times(value: Int): DataColumn<Double> = map { it * value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000.0
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<Double>.times(value: Double): DataColumn<Double> = map { it * value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * 1000L
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<Long>.times(value: Long): DataColumn<Long> = map { it * value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of product prices, compute the price including a 20% tax
 * val priceWithTax = df.price * BigDecimal("1.20")
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<BigDecimal>.times(value: BigDecimal): DataColumn<BigDecimal> = map { it * value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 * For more information: [See `times` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#times)
 *
 * See also [<code>div</code>][DataColumn.div], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of file sizes in bits, compute the total size of multiple copies of each file
 * val totalSize = df.fileSize * BigInteger("12345")
 * ```
 *
 * @param [value] The value to multiply each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of multiplying each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<BigInteger>.times(value: BigInteger): DataColumn<BigInteger> = map { it * value }

// endregion

// region ColumnDivNumber

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [<code>value</code>][value] is equal to zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<Int>.div(value: Int): DataColumn<Int> = map { it / value }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] containing the results of dividing
 * each element of this [<code>ColumnReference</code>][ColumnReference] by [<code>value</code>][value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][ColumnReference.times],
 * [<code>plus</code>][ColumnReference.plus], [<code>minus</code>][ColumnReference.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10
 * ```
 *
 * @param [value] The value to divide each element of this [<code>ColumnReference</code>][ColumnReference] by.
 *
 * @throws [ArithmeticException] if [<code>value</code>][value] is equal to zero.
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the results of dividing
 * each element of this [<code>ColumnReference</code>][ColumnReference] by [<code>value</code>][value].
 */
public operator fun ColumnReference<Int>.div(value: Int): ColumnReference<Int> = map { it / value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 * `null` values are not changed by this operation.
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [<code>value</code>][value] is equal to zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
@JvmName("divIntNullable")
public operator fun DataColumn<Int?>.div(value: Int): DataColumn<Int?> = map { it?.div(value) }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of distances in meters, convert them to kilometers
 * val distanceKm = df.distanceMeters / 1000.0
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
@JvmName("divInt")
public operator fun DataColumn<Int>.div(value: Double): DataColumn<Double> = map { it / value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of hourly travel distances, compute the average distance traveled per minute
 * val distancePerMinute = df.distancePerHour / 60
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
@JvmName("divDouble")
public operator fun DataColumn<Double>.div(value: Int): DataColumn<Double> = map { it / value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of distances in meters, convert them to kilometers
 * val distanceKm = df.distanceMeters / 1000.0
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<Double>.div(value: Double): DataColumn<Double> = map { it / value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10L
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [<code>value</code>][value] is equal to zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<Long>.div(value: Long): DataColumn<Long> = map { it / value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of distances in miles, convert them to kilometers
 * val distanceKm = df.distanceMiles / BigDecimal("0.62137")
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [<code>value</code>][value] is equal to zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<BigDecimal>.div(value: BigDecimal): DataColumn<BigDecimal> = map { it / value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of item counts stored as BigInteger values,
 * // compute how many full batches of 1,000 items can be formed
 * val batches = df.itemCount / BigInteger("1000")
 * ```
 *
 * @param [value] The value to divide each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by.
 *
 * @throws [ArithmeticException] if [<code>value</code>][value] is equal to zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] by [<code>value</code>][value].
 */
public operator fun DataColumn<BigInteger>.div(value: BigInteger): DataColumn<BigInteger> = map { it / value }

// endregion

// region NumberDivColumn

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of tasks with estimated hours,
 * // compute how many tasks can fit into a fixed 40-hour work week
 * val tasksPerWeek = 40 / df.estimatedHours
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>Int</code>][Int] by.
 *
 * @throws [ArithmeticException] if [<code>column</code>][column] contains zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 */
public operator fun Int.div(column: DataColumn<Int>): DataColumn<Int> = column.map { this / it }

/**
 * Returns a [<code>ColumnReference</code>][ColumnReference] containing the results of dividing
 * this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][ColumnReference.times],
 * [<code>plus</code>][ColumnReference.plus], [<code>minus</code>][ColumnReference.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of tasks with estimated hours,
 * // compute how many tasks can fit into a fixed 40-hour work week
 * val tasksPerWeek = 40 / df.estimatedHours
 * ```
 *
 * @param [column] A [<code>ColumnReference</code>][ColumnReference] containing the elements
 * to divide this [<code>Int</code>][Int] by.
 *
 * @throws [ArithmeticException] if [<code>column</code>][column] contains zero.
 *
 * @return A [<code>ColumnReference</code>][ColumnReference] containing the results
 * of dividing this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 */
public operator fun Int.div(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this / it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 * If an element of [<code>column</code>][column] is `null`,
 * the corresponding value in the resulting [<code>DataColumn</code>][DataColumn] is also `null`.
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of tasks with estimated hours,
 * // compute how many tasks can fit into a fixed 40-hour work week
 * val tasksPerWeek = 40 / df.estimatedHours
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>Int</code>][Int] by.
 *
 * @throws [ArithmeticException] if [<code>column</code>][column] contains zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 */
@JvmName("divNullable")
public operator fun Int.div(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.let { this / it } }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>Double</code>][Double] by each element of [<code>column</code>][column].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // Given a marketing budget of 1,000 euros,
 * // compute the cost per acquired customer for each campaign
 * val costPerCustomer = 1000.0 / df.acquiredCustomers
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>Double</code>][Double] by.
 *
 * @throws
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>Double</code>][Double] by each element of [<code>column</code>][column].
 */
@JvmName("doubleDiv")
public operator fun Double.div(column: DataColumn<Int>): DataColumn<Double> = column.map { this / it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of prices of a square meter in different places,
 * // compute how many square meters can be bought with a budget of 500 thousand euros
 * val squareMeters = 500_000 / df.pricePerSquareMeter
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>Int</code>][Int] by.
 *
 * @throws
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>Int</code>][Int] by each element of [<code>column</code>][column].
 */
@JvmName("intDiv")
public operator fun Int.div(column: DataColumn<Double>): DataColumn<Double> = column.map { this / it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>Double</code>][Double] by each element of [<code>column</code>][column].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of prices of a square meter in different places,
 * // compute how many square meters can be bought with a budget of 500 thousand euros
 * val squareMeters = 500_000.0 / df.pricePerSquareMeter
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>Double</code>][Double] by.
 *
 * @throws
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>Double</code>][Double] by each element of [<code>column</code>][column].
 */
public operator fun Double.div(column: DataColumn<Double>): DataColumn<Double> = column.map { this / it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>Long</code>][Long] by each element of [<code>column</code>][column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of file sizes in bits,
 * // compute how many copies of each file can fit into the given storage capacity
 * val fileCopies = 10_000_000_000L / df.fileSize
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>Long</code>][Long] by.
 *
 * @throws [ArithmeticException] if [<code>column</code>][column] contains zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>Long</code>][Long] by each element of [<code>column</code>][column].
 */
public operator fun Long.div(column: DataColumn<Long>): DataColumn<Long> = column.map { this / it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>BigDecimal</code>][BigDecimal] by each element of [<code>column</code>][column].
 *
 *
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of prices of a product per gram,
 * // compute the amount of product that can be bought with a budget of 3,451.76 euros
 * val productAmount = BigDecimal("3451.76") / df.pricePerGram
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>BigDecimal</code>][BigDecimal] by.
 *
 * @throws [ArithmeticException] if [<code>column</code>][column] contains zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>BigDecimal</code>][BigDecimal] by each element of [<code>column</code>][column].
 */
public operator fun BigDecimal.div(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this / it }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results of dividing
 * this [<code>BigInteger</code>][BigInteger] by each element of [<code>column</code>][column].
 *
 * The result of each division is truncated to an integer that is closer to zero.
 *
 *
 *
 * For more information: [See `div` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#div)
 *
 * See also [<code>times</code>][DataColumn.times], [<code>plus</code>][DataColumn.plus], [<code>minus</code>][DataColumn.minus].
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of file sizes in bits,
 * // compute how many copies of each file can fit into the given storage capacity
 * val fileCopies = BigInteger("10000000000") / df.fileSize
 * ```
 *
 * @param [column] A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the elements
 * to divide this [<code>BigInteger</code>][BigInteger] by.
 *
 * @throws [ArithmeticException] if [<code>column</code>][column] contains zero.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the results
 * of dividing this [<code>BigInteger</code>][BigInteger] by each element of [<code>column</code>][column].
 */
public operator fun BigInteger.div(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this / it }

// endregion

// region Compare

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [<code>results</code>][Boolean] of comparing each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [<code>value</code>][value] for equality using the `==` operator.
 *
 * For more information: [See `Compare` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#compare)
 *
 * See also [<code>neq</code>][DataColumn.neq], [<code>gt</code>][DataColumn.gt], [<code>lt</code>][DataColumn.lt].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders with statuses represented as strings,
 * // create a column that indicates whether each order is canceled
 * val isCanceled = df.status eq "canceled"
 * ```
 *
 * @param [value] The value to compare each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements equal to [<code>value</code>][value], and `false` otherwise.
 */
public infix fun <T> DataColumn<T>.eq(value: T): DataColumn<Boolean> = map { it == value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [<code>results</code>][Boolean] of comparing each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [<code>value</code>][value] for inequality using the `!=` operator.
 *
 * For more information: [See `Compare` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#compare)
 *
 * See also [<code>eq</code>][DataColumn.eq], [<code>gt</code>][DataColumn.gt], [<code>lt</code>][DataColumn.lt].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders with statuses represented as strings,
 * // create a column that indicates which orders are not completed
 * val isNotCompleted = df.status neq "completed"
 * ```
 *
 * @param [value] The value to compare each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements not equal to [<code>value</code>][value], and `false` otherwise.
 */
public infix fun <T> DataColumn<T>.neq(value: T): DataColumn<Boolean> = map { it != value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [<code>results</code>][Boolean] of comparing each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [<code>value</code>][value] using the `>` operator.
 *
 * For more information: [See `Compare` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#compare)
 *
 * See also [<code>eq</code>][DataColumn.eq], [<code>neq</code>][DataColumn.neq], [<code>lt</code>][DataColumn.lt].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders,
 * // create a column that indicates which orders cost more than 1,000 euros
 * val isExpensive = df.orderCost gt 1000
 * ```
 *
 * @param [value] The value to compare each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements greater than [<code>value</code>][value], and `false` otherwise.
 */
public infix fun <T : Comparable<T>> DataColumn<T>.gt(value: T): DataColumn<Boolean> = map { it > value }

/**
 * Returns a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing the [<code>results</code>][Boolean] of comparing each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]
 * with [<code>value</code>][value] using the `<` operator.
 *
 * For more information: [See `Compare` on the documentation website.](https://kotlin.github.io/dataframe/columnarithmetics.html#compare)
 *
 * See also [<code>eq</code>][DataColumn.eq], [<code>neq</code>][DataColumn.neq], [<code>gt</code>][DataColumn.gt].
 *
 * ### Example
 * ```kotlin
 * // Given a DataFrame of orders,
 * // create a column that indicates which orders cost less than 20 euros
 * val isCheap = df.orderCost lt 20
 * ```
 *
 * @param [value] The value to compare each element of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with.
 *
 * @return A [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] containing `true` for elements less than [<code>value</code>][value], and `false` otherwise.
 */
public infix fun <T : Comparable<T>> DataColumn<T>.lt(value: T): DataColumn<Boolean> = map { it < value }
// endregion
