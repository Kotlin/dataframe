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
 * Returns a {@get [NOT_COLUMN_TYPE] [DataColumn]} containing the inverse [Boolean] values
 * of this {@get [NOT_COLUMN_TYPE] [DataColumn]}. {@get [NOT_NULL_NOTE]}
 *
 * {@get [NOT_DESCRIPTION]}
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
 * @return {@get [NOT_RETURN]}
 */
@ExcludeFromSources
private interface NotDocs {
    // type of the column accepted and returned by the function
    typealias NOT_COLUMN_TYPE = Nothing

    // info about handling `null` values
    typealias NOT_NULL_NOTE = Nothing

    // description of the function
    typealias NOT_DESCRIPTION = Nothing

    // info about what the function returns
    typealias NOT_RETURN = Nothing
}

/**
 * @include [NotDocs]
 * @set [NOT_DESCRIPTION] Each element in this [column][DataColumn] is transformed
 * using the logical `not` operation: `true` becomes `false`, and `false` becomes `true`.
 * @set [NOT_RETURN] A [DataColumn] containing the negated [Boolean] values of this [column][DataColumn].
 */
public operator fun DataColumn<Boolean>.not(): DataColumn<Boolean> = map { !it }

/**
 * @include [NotDocs]
 * @set [NOT_NULL_NOTE] Each `null` value in the original [DataColumn] is preserved.
 * @set [NOT_DESCRIPTION] Non-null values are transformed using the logical `not` operation:
 * `true` becomes `false`, and `false` becomes `true`. `null` values remain `null`.
 * @set [NOT_RETURN] A [DataColumn] containing the negated [Boolean] values of this [column][DataColumn],
 * while preserving `null` values.
 */
@JvmName("notBooleanNullable")
public operator fun DataColumn<Boolean?>.not(): DataColumn<Boolean?> = map { it?.not() }

/**
 * @include [NotDocs]
 * @set [NOT_COLUMN_TYPE] [ColumnReference]
 * @set [NOT_DESCRIPTION] Each value in this [reference][ColumnReference] is transformed
 * using the logical `not` operation: `true` becomes `false`, and `false` becomes `true`.
 * @set [NOT_RETURN] A [ColumnReference] containing the negated [Boolean] values of this [reference][ColumnReference].
 */
public operator fun ColumnReference<Boolean>.not(): ColumnReference<Boolean> = map { !it }

/**
 * @include [NotDocs]
 * @set [NOT_COLUMN_TYPE] [ColumnReference]
 * @set [NOT_NULL_NOTE] Each `null` value in the original [ColumnReference] is preserved.
 * @set [NOT_DESCRIPTION] Non-null values are transformed using the logical `not` operation:
 * `true` becomes `false`, and `false` becomes `true`. `null` values remain `null`.
 * @set [NOT_RETURN] A [ColumnReference] containing the negated [Boolean] values
 * of the original [reference][ColumnReference], while preserving `null` values.
 */
@JvmName("notBooleanNullable")
public operator fun ColumnReference<Boolean?>.not(): ColumnReference<Boolean?> = map { it?.not() }

// endregion

// region ColumnPlusNumber

/**
 * Returns a {@get [COLUMN_PLUS_NUMBER_COLUMN_TYPE] [DataColumn]} where each element is the result of adding [\value]
 * to the corresponding element of this {@get [COLUMN_PLUS_NUMBER_COLUMN_TYPE] [DataColumn]}.
 *
 * That is, [\value] is added to each element of this {@get [COLUMN_PLUS_NUMBER_COLUMN_TYPE] [DataColumn]}.
 *
 * {@get [COLUMN_PLUS_NUMBER_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * {@get [COLUMN_PLUS_NUMBER_EXAMPLE] // In a DataFrame of financial transactions,
 * // add a fixed fee to each transaction amount
 * df.amount + {@get [COLUMN_PLUS_NUMBER_EXAMPLE_NUMBER] 10}}
 * ```
 *
 * See also {@get [COLUMN_PLUS_NUMBER_SEE_ALSO]}.
 *
 * @param [\value] The value to add to each element of this {@get [COLUMN_PLUS_NUMBER_COLUMN_TYPE] [DataColumn]}.
 *
 * @return A {@get [COLUMN_PLUS_NUMBER_COLUMN_TYPE] [DataColumn]} containing the results of adding [\value]
 * to each element of this {@get [COLUMN_PLUS_NUMBER_COLUMN_TYPE] [DataColumn]}.
 */
@ExcludeFromSources
private interface ColumnPlusNumberDocs {
    // the type of the column accepted and returned by the function
    typealias COLUMN_PLUS_NUMBER_COLUMN_TYPE = Nothing

    // info about handling `null` values
    typealias COLUMN_PLUS_NUMBER_NULL_NOTE = Nothing

    // the example used in the documentation
    typealias COLUMN_PLUS_NUMBER_EXAMPLE = Nothing

    // adjustment of the example to use different types of numbers
    typealias COLUMN_PLUS_NUMBER_EXAMPLE_NUMBER = Nothing

    // `See also` section in the documentation
    typealias COLUMN_PLUS_NUMBER_SEE_ALSO = Nothing
}

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun DataColumn<Int>.plus(value: Int): DataColumn<Int> = map { it + value }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_COLUMN_TYPE] [ColumnReference]
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][ColumnReference.minus],
 * [times][ColumnReference.times], [div][ColumnReference.div]
 */
public operator fun ColumnReference<Int>.plus(value: Int): ColumnReference<Int> = map { it + value }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_NULL_NOTE] `null` values are not changed by this operation.
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
@JvmName("plusIntNullable")
public operator fun DataColumn<Int?>.plus(value: Int): DataColumn<Int?> = map { it?.plus(value) }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_EXAMPLE_NUMBER] 10.0
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
@JvmName("plusInt")
public operator fun DataColumn<Int>.plus(value: Double): DataColumn<Double> = map { it + value }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
@JvmName("plusDouble")
public operator fun DataColumn<Double>.plus(value: Int): DataColumn<Double> = map { it + value }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_EXAMPLE_NUMBER] 10L
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun DataColumn<Long>.plus(value: Long): DataColumn<Long> = map { it + value }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_EXAMPLE_NUMBER] 10.0
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun DataColumn<Double>.plus(value: Double): DataColumn<Double> = map { it + value }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_EXAMPLE_NUMBER] BigDecimal("12.03")
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun DataColumn<BigDecimal>.plus(value: BigDecimal): DataColumn<BigDecimal> = map { it + value }

/**
 * @include [ColumnPlusNumberDocs]
 * @set [COLUMN_PLUS_NUMBER_EXAMPLE] // Given a DataFrame of current disks usage in bits,
 * // compute the total disks usage if the size of a file is added
 * val totalDisksUsage = df.diskUsage + BigInteger("12345678900")
 * @set [COLUMN_PLUS_NUMBER_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun DataColumn<BigInteger>.plus(value: BigInteger): DataColumn<BigInteger> = map { it + value }

// endregion

// region NumberPlusColumn

/**
 * Returns a {@get [NUMBER_PLUS_COLUMN_COLUMN_TYPE] [DataColumn]} where each element is the result of adding
 * the corresponding element of [\column] to this {@get [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [Int]}.
 *
 * {@get [NUMBER_PLUS_COLUMN_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * {@get [NUMBER_PLUS_COLUMN_EXAMPLE] // In a DataFrame of orders to deliver,
 * // compute the total delivery cost of each order from a fixed base fee and a variable distance fee.
 * {@get [NUMBER_PLUS_COLUMN_EXAMPLE_NUMBER] 10} + df.distanceFee}
 * ```
 *
 * See also {@get [NUMBER_PLUS_COLUMN_SEE_ALSO]}.
 *
 * @param [\column] A {@get [NUMBER_PLUS_COLUMN_COLUMN_TYPE] [DataColumn]} containing the elements to add
 * to this {@get [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [Int]}.
 *
 * @return A {@get [NUMBER_PLUS_COLUMN_COLUMN_TYPE] [DataColumn]} containing the results of adding
 * the corresponding element of [\column] to this {@get [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [Int]}.
 */
@ExcludeFromSources
private interface NumberPlusColumnDocs {
    // the type of the column passed to the function and returned by the function
    typealias NUMBER_PLUS_COLUMN_COLUMN_TYPE = Nothing

    // the type of the number on which the function is applied
    typealias NUMBER_PLUS_COLUMN_NUMBER_TYPE = Nothing

    // the example used in the documentation
    typealias NUMBER_PLUS_COLUMN_EXAMPLE = Nothing

    // adjustment of the example to use different types of numbers
    typealias NUMBER_PLUS_COLUMN_EXAMPLE_NUMBER = Nothing

    // `See also` section in the documentation
    typealias NUMBER_PLUS_COLUMN_SEE_ALSO = Nothing

    // info about handling `null` values
    typealias NUMBER_PLUS_COLUMN_NULL_NOTE = Nothing
}

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun Int.plus(column: DataColumn<Int>): DataColumn<Int> = column.map { this + it }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_COLUMN_TYPE] [ColumnReference]
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][ColumnReference.minus],
 * [times][ColumnReference.times], [div][ColumnReference.div]
 */
public operator fun Int.plus(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this + it }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_NULL_NOTE] `null` values from the original [column]
 * remain `null` values in the resulting [DataColumn].
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
@JvmName("plusNullable")
public operator fun Int.plus(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.plus(this) }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [Double]
 * @set [NUMBER_PLUS_COLUMN_EXAMPLE_NUMBER] 10.0
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
@JvmName("doublePlus")
public operator fun Double.plus(column: DataColumn<Int>): DataColumn<Double> = column.map { this + it }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
@JvmName("intPlus")
public operator fun Int.plus(column: DataColumn<Double>): DataColumn<Double> = column.map { this + it }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [Double]
 * @set [NUMBER_PLUS_COLUMN_EXAMPLE_NUMBER] 10.0
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun Double.plus(column: DataColumn<Double>): DataColumn<Double> = column.map { this + it }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [Long]
 * @set [NUMBER_PLUS_COLUMN_EXAMPLE_NUMBER] 10L
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun Long.plus(column: DataColumn<Long>): DataColumn<Long> = column.map { this + it }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [BigDecimal]
 * @set [NUMBER_PLUS_COLUMN_EXAMPLE_NUMBER] BigDecimal("12.03")
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun BigDecimal.plus(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this + it }

/**
 * @include [NumberPlusColumnDocs]
 * @set [NUMBER_PLUS_COLUMN_NUMBER_TYPE] [BigInteger]
 * @set [NUMBER_PLUS_COLUMN_EXAMPLE] // Given the current disk usage in bits,
 * // and a DataFrame of file sizes in bits, compute the total disk usage if each file is added
 * val diskUsage = BigInteger("12345678900") + df.fileSize
 * @set [NUMBER_PLUS_COLUMN_SEE_ALSO] [minus][DataColumn.minus], [times][DataColumn.times], [div][DataColumn.div]
 */
public operator fun BigInteger.plus(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this + it }

// endregion

// region ColumnPlusString

/**
 * Returns a {@get [COLUMN_PLUS_STRING_RETURN_TYPE] [DataColumn]} of [String] values
 * obtained by converting each element of this {@get [COLUMN_PLUS_STRING_RECEIVER] [AnyCol]}
 * to a [String] and concatenating it with [\str].
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
 * See also {@get [COLUMN_PLUS_STRING_SEE_ALSO] [plus][DataColumn.plus]}.
 *
 * @param [\str] The [String] to append to each element of this {@get [COLUMN_PLUS_STRING_RECEIVER] [AnyCol]}.
 *
 * @return A {@get [COLUMN_PLUS_STRING_RETURN_TYPE] [DataColumn]} of [String] values where each element is the result
 * of concatenating the corresponding element of this {@get [COLUMN_PLUS_STRING_RECEIVER] [AnyCol]} with [\str].
 */
@ExcludeFromSources
private interface ColumnPlusStringDocs {
    // type of the column on which the function is applied
    typealias COLUMN_PLUS_STRING_RECEIVER = Nothing

    // the type of the column returned by the function
    typealias COLUMN_PLUS_STRING_RETURN_TYPE = Nothing

    // `See also` section in the documentation
    typealias COLUMN_PLUS_STRING_SEE_ALSO = Nothing
}

/**
 * @include [ColumnPlusStringDocs]
 */
public operator fun AnyCol.plus(str: String): DataColumn<String> = map { it.toString() + str }

/**
 * @include [ColumnPlusStringDocs]
 * @set [COLUMN_PLUS_STRING_RECEIVER] [ColumnReference]
 * @set [COLUMN_PLUS_STRING_RETURN_TYPE] [ColumnReference]
 */
public operator fun ColumnReference<Any?>.plus(str: String): ColumnReference<String> = map { it.toString() + str }

// endregion

// region ColumnMinusNumber

/**
 * Returns a {@get [COLUMN_MINUS_NUMBER_COLUMN_TYPE] [DataColumn]} where each element is the result of subtracting [\value]
 * from the corresponding element of this {@get [COLUMN_MINUS_NUMBER_COLUMN_TYPE] [DataColumn]}.
 *
 * That is, [\value] is subtracted from each element of the {@get [COLUMN_MINUS_NUMBER_COLUMN_TYPE] [DataColumn]}.
 *
 * {@get [COLUMN_MINUS_NUMBER_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * {@get [COLUMN_MINUS_NUMBER_EXAMPLE] // In a DataFrame of financial transactions,
 * // subtract a fixed fee from each transaction amount
 * df.amount - {@get [COLUMN_MINUS_NUMBER_EXAMPLE_NUMBER] 10}}
 * ```
 *
 * See also {@get [COLUMN_MINUS_NUMBER_SEE_ALSO] [plus][DataColumn.plus], [times][DataColumn.times], [div][DataColumn.div]}.
 *
 * @return A {@get [COLUMN_MINUS_NUMBER_COLUMN_TYPE] [DataColumn]} containing the results of subtracting [\value]
 * from each element of this {@get [COLUMN_MINUS_NUMBER_COLUMN_TYPE] [DataColumn]}.
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
 * @include [ColumnMinusNumberDocs]
 */
public operator fun DataColumn<Int>.minus(value: Int): DataColumn<Int> = map { it - value }

/**
 * @include [ColumnMinusNumberDocs]
 * @set [COLUMN_MINUS_NUMBER_COLUMN_TYPE] [ColumnReference]
 * @set [COLUMN_MINUS_NUMBER_SEE_ALSO] [plus][ColumnReference.plus],
 * [times][ColumnReference.times], [div][ColumnReference.div]
 */
public operator fun ColumnReference<Int>.minus(value: Int): ColumnReference<Int> = map { it - value }

/**
 * @include [ColumnMinusNumberDocs]
 * @set [COLUMN_MINUS_NUMBER_NULL_NOTE] `null` values are not changed by this operation.
 */
@JvmName("minusIntNullable")
public operator fun DataColumn<Int?>.minus(value: Int): DataColumn<Int?> = map { it?.minus(value) }

/**
 * @include [ColumnMinusNumberDocs]
 * @set [COLUMN_MINUS_NUMBER_EXAMPLE_NUMBER] 10.0
 */
@JvmName("minusInt")
public operator fun DataColumn<Int>.minus(value: Double): DataColumn<Double> = map { it - value }

/**
 * @include [ColumnMinusNumberDocs]
 */
@JvmName("minusDouble")
public operator fun DataColumn<Double>.minus(value: Int): DataColumn<Double> = map { it - value }

/**
 * @include [ColumnMinusNumberDocs]
 * @set [COLUMN_MINUS_NUMBER_EXAMPLE_NUMBER] 10.0
 */
public operator fun DataColumn<Double>.minus(value: Double): DataColumn<Double> = map { it - value }

/**
 * @include [ColumnMinusNumberDocs]
 * @set [COLUMN_MINUS_NUMBER_EXAMPLE_NUMBER] 10L
 */
public operator fun DataColumn<Long>.minus(value: Long): DataColumn<Long> = map { it - value }

/**
 * @include [ColumnMinusNumberDocs]
 * @set [COLUMN_MINUS_NUMBER_EXAMPLE_NUMBER] BigDecimal("12.03")
 */
public operator fun DataColumn<BigDecimal>.minus(value: BigDecimal): DataColumn<BigDecimal> = map { it - value }

/**
 * @include [ColumnMinusNumberDocs]
 * @set [COLUMN_MINUS_NUMBER_EXAMPLE] // Given a DataFrame of current disks usage in bits,
 * // compute the total disks usage if a file is deleted
 * val totalDisksUsage = df.diskUsage - BigInteger("12345678900")
 */
public operator fun DataColumn<BigInteger>.minus(value: BigInteger): DataColumn<BigInteger> = map { it - value }

// endregion

// region NumberMinusColumn

/**
 * Returns a {@get [NUMBER_MINUS_COLUMN_COLUMN_TYPE] [DataColumn]} where each element is the result of subtracting
 * the corresponding element of [\column] from this {@get [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [Int]}.
 *
 * {@get [NUMBER_MINUS_COLUMN_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * {@get [NUMBER_MINUS_COLUMN_EXAMPLE] // Given a budget of 1000 euros per employee,
 * // compute the remaining budget for each employee after expenses
 * val remainingBudget = {@get [NUMBER_MINUS_COLUMN_EXAMPLE_NUMBER] 1000} - df.expenses}
 * ```
 *
 * See also {@get [NUMBER_MINUS_COLUMN_SEE_ALSO] [plus][DataColumn.plus], [times][DataColumn.times], [div][DataColumn.div]}.
 *
 * @param [\column] A {@get [NUMBER_MINUS_COLUMN_COLUMN_TYPE] [DataColumn]} containing the elements to subtract
 * from this {@get [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [Int]}.
 *
 * @return A {@get [NUMBER_MINUS_COLUMN_COLUMN_TYPE] [DataColumn]} containing the results
 * of subtracting the corresponding element of [\column] from this {@get [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [Int]}.
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
 * @include [NumberMinusColumnDocs]
 */
public operator fun Int.minus(column: DataColumn<Int>): DataColumn<Int> = column.map { this - it }

/**
 * @include [NumberMinusColumnDocs]
 * @set [NUMBER_MINUS_COLUMN_COLUMN_TYPE] [ColumnReference]
 * @set [NUMBER_MINUS_COLUMN_SEE_ALSO] [plus][ColumnReference.plus], [times][ColumnReference.times], [div][ColumnReference.div]
 */
public operator fun Int.minus(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this - it }

/**
 * @include [NumberMinusColumnDocs]
 * @set [NUMBER_MINUS_COLUMN_NULL_NOTE] `null` values from the original [column] remain `null` values in the resulting [DataColumn].
 */
@JvmName("minusNullable")
public operator fun Int.minus(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.let { this - it } }

/**
 * @include [NumberMinusColumnDocs]
 * @set [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [Double]
 * @set [NUMBER_MINUS_COLUMN_EXAMPLE_NUMBER] 1000.0
 */
@JvmName("doubleMinus")
public operator fun Double.minus(column: DataColumn<Int>): DataColumn<Double> = column.map { this - it }

/**
 * @include [NumberMinusColumnDocs]
 */
@JvmName("intMinus")
public operator fun Int.minus(column: DataColumn<Double>): DataColumn<Double> = column.map { this - it }

/**
 * @include [NumberMinusColumnDocs]
 * @set [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [Double]
 * @set [NUMBER_MINUS_COLUMN_EXAMPLE_NUMBER] 1000.0
 */
public operator fun Double.minus(column: DataColumn<Double>): DataColumn<Double> = column.map { this - it }

/**
 * @include [NumberMinusColumnDocs]
 * @set [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [Long]
 * @set [NUMBER_MINUS_COLUMN_EXAMPLE_NUMBER] 1000L
 */
public operator fun Long.minus(column: DataColumn<Long>): DataColumn<Long> = column.map { this - it }

/**
 * @include [NumberMinusColumnDocs]
 * @set [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [BigDecimal]
 * @set [NUMBER_MINUS_COLUMN_EXAMPLE_NUMBER] BigDecimal("1000.00")
 */
public operator fun BigDecimal.minus(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this - it }

/**
 * @include [NumberMinusColumnDocs]
 * @set [NUMBER_MINUS_COLUMN_NUMBER_TYPE] [BigInteger]
 * @set [NUMBER_MINUS_COLUMN_EXAMPLE] // Given the current disk usage in bits,
 * // and a DataFrame of file sizes in bits, compute the total disk usage if any file is deleted
 * val diskUsage = BigInteger("12345678900") - df.fileSize
 */
public operator fun BigInteger.minus(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this - it }

// endregion

// region UnaryMinus

/**
 * Returns a {@get [UNARY_MINUS_COLUMN_TYPE] [DataColumn]} containing negatives
 * of the corresponding elements of this {@get [UNARY_MINUS_COLUMN_TYPE] [DataColumn]}.
 *
 * The sign of each element in this {@get [UNARY_MINUS_COLUMN_TYPE] [DataColumn]} is flipped:
 * positive values become negative, and negative values become positive.
 *
 * {@get [UNARY_MINUS_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // represent expenses as negative values
 * val expenses = -df.expenses
 * ```
 *
 * @return A {@get [UNARY_MINUS_COLUMN_TYPE] [DataColumn]} containing negatives
 * of the corresponding elements of this {@get [UNARY_MINUS_COLUMN_TYPE] [DataColumn]}.
 */
@ExcludeFromSources
private interface UnaryMinusDocs {
    // the type of the column accepted and returned by the function
    typealias UNARY_MINUS_COLUMN_TYPE = Nothing

    // info about handling `null` values
    typealias UNARY_MINUS_NULL_NOTE = Nothing
}

/**
 * @include [UnaryMinusDocs]
 */
public operator fun DataColumn<Int>.unaryMinus(): DataColumn<Int> = map { -it }

/**
 * @include [UnaryMinusDocs]
 * @set [UNARY_MINUS_COLUMN_TYPE] [ColumnReference]
 */
public operator fun ColumnReference<Int>.unaryMinus(): ColumnReference<Int> = map { -it }

/**
 * @include [UnaryMinusDocs]
 * @set [UNARY_MINUS_NULL_NOTE] `null` values are not changed by this operation.
 */
@JvmName("unaryMinusIntNullable")
public operator fun DataColumn<Int?>.unaryMinus(): DataColumn<Int?> = map { it?.unaryMinus() }

/**
 * @include [UnaryMinusDocs]
 */
@JvmName("unaryMinusDouble")
public operator fun DataColumn<Double>.unaryMinus(): DataColumn<Double> = map { -it }

/**
 * @include [UnaryMinusDocs]
 */
@JvmName("unaryMinusLong")
public operator fun DataColumn<Long>.unaryMinus(): DataColumn<Long> = map { -it }

/**
 * @include [UnaryMinusDocs]
 */
@JvmName("unaryMinusBigDecimal")
public operator fun DataColumn<BigDecimal>.unaryMinus(): DataColumn<BigDecimal> = map { -it }

/**
 * @include [UnaryMinusDocs]
 */
@JvmName("unaryMinusBigInteger")
public operator fun DataColumn<BigInteger>.unaryMinus(): DataColumn<BigInteger> = map { -it }

// endregion

// region Times

/**
 * Returns a {@get [TIMES_COLUMN_TYPE] [DataColumn]} containing the results
 * of multiplying each element of this {@get [TIMES_COLUMN_TYPE] [DataColumn]} by [\value].
 *
 * {@get [TIMES_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * {@get [TIMES_EXAMPLE] // In a DataFrame with distances in kilometers,
 * // convert them to meters
 * val distanceMeters = df.distanceKm * {@get [TIMES_EXAMPLE_NUMBER] 1000}}
 * ```
 *
 * See also {@get [TIMES_SEE_ALSO]}.
 *
 * @param [\value] The value to multiply each element of this {@get [TIMES_COLUMN_TYPE] [DataColumn]} by.
 *
 * @return A {@get [TIMES_COLUMN_TYPE] [DataColumn]} containing the results
 * of multiplying each element of this {@get [TIMES_COLUMN_TYPE] [DataColumn]} by [\value].
 */
@ExcludeFromSources
private interface TimesDocs {
    // the type of the column accepted and returned by the function
    typealias TIMES_COLUMN_TYPE = Nothing

    // the example used in the documentation
    typealias TIMES_EXAMPLE = Nothing

    // adjustment of the example to use different types of numbers
    typealias TIMES_EXAMPLE_NUMBER = Nothing

    // `See also` section in the documentation
    typealias TIMES_SEE_ALSO = Nothing

    // info about handling `null` values
    typealias TIMES_NULL_NOTE = Nothing
}

/**
 * @include [TimesDocs]
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<Int>.times(value: Int): DataColumn<Int> = map { it * value }

/**
 * @include [TimesDocs]
 * @set [TIMES_COLUMN_TYPE] [ColumnReference]
 * @set [TIMES_SEE_ALSO] [div][ColumnReference.div], [plus][ColumnReference.plus], [minus][ColumnReference.minus]
 */
public operator fun ColumnReference<Int>.times(value: Int): ColumnReference<Int> = map { it * value }

/**
 * @include [TimesDocs]
 * @set [TIMES_NULL_NOTE] `null` values are not changed by this operation.
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
@JvmName("timesIntNullable")
public operator fun DataColumn<Int?>.times(value: Int): DataColumn<Int?> = map { it?.times(value) }

/**
 * @include [TimesDocs]
 * @set [TIMES_EXAMPLE_NUMBER] 1000.0
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
@JvmName("timesInt")
public operator fun DataColumn<Int>.times(value: Double): DataColumn<Double> = map { it * value }

/**
 * @include [TimesDocs]
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
@JvmName("timesDouble")
public operator fun DataColumn<Double>.times(value: Int): DataColumn<Double> = map { it * value }

/**
 * @include [TimesDocs]
 * @set [TIMES_EXAMPLE_NUMBER] 1000.0
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<Double>.times(value: Double): DataColumn<Double> = map { it * value }

/**
 * @include [TimesDocs]
 * @set [TIMES_EXAMPLE_NUMBER] 1000L
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<Long>.times(value: Long): DataColumn<Long> = map { it * value }

/**
 * @include [TimesDocs]
 * @set [TIMES_EXAMPLE] // In a DataFrame of product prices, compute the price including a 20% tax
 * val priceWithTax = df.price * BigDecimal("1.20")
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<BigDecimal>.times(value: BigDecimal): DataColumn<BigDecimal> = map { it * value }

/**
 * @include [TimesDocs]
 * @set [TIMES_EXAMPLE] // In a DataFrame of file sizes in bits, compute the total size of multiple copies of each file
 * val totalSize = df.fileSize * BigInteger("12345")
 * @set [TIMES_SEE_ALSO] [div][DataColumn.div], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<BigInteger>.times(value: BigInteger): DataColumn<BigInteger> = map { it * value }

// endregion

// region ColumnDivNumber

/**
 * Returns a {@get [COLUMN_DIV_NUMBER_COLUMN_TYPE] [DataColumn]} containing the results of dividing
 * each element of this {@get [COLUMN_DIV_NUMBER_COLUMN_TYPE] [DataColumn]} by [\value].
 *
 * {@get [COLUMN_DIV_NUMBER_INT_NOTE] The result of each division is truncated to an integer that is closer to zero.}
 *
 * {@get [COLUMN_DIV_NUMBER_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * {@get [COLUMN_DIV_NUMBER_EXAMPLE] // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10}
 * ```
 *
 * See also {@get [COLUMN_DIV_NUMBER_SEE_ALSO]}.
 *
 * @param [\value] The value to divide each element of this {@get [COLUMN_DIV_NUMBER_COLUMN_TYPE] [DataColumn]} by.
 *
 * @throws {@get [COLUMN_DIV_NUMBER_ZERO_ERROR] [\ArithmeticException] if [\value] is equal to zero.}
 *
 * @return A {@get [COLUMN_DIV_NUMBER_COLUMN_TYPE] [DataColumn]} containing the results of dividing
 * each element of this {@get [COLUMN_DIV_NUMBER_COLUMN_TYPE] [DataColumn]} by [\value].
 */
@ExcludeFromSources
private interface ColumnDivNumberDocs {
    // the type of the column accepted and returned by the function
    typealias COLUMN_DIV_NUMBER_COLUMN_TYPE = Nothing

    // `See also` section in the documentation
    typealias COLUMN_DIV_NUMBER_SEE_ALSO = Nothing

    // info about handling `null` values
    typealias COLUMN_DIV_NUMBER_NULL_NOTE = Nothing

    // the example used in the documentation
    typealias COLUMN_DIV_NUMBER_EXAMPLE = Nothing

    // info about truncating the result to an integer that is closer to zero
    typealias COLUMN_DIV_NUMBER_INT_NOTE = Nothing

    // the exception thrown when dividing by zero
    typealias COLUMN_DIV_NUMBER_ZERO_ERROR = Nothing
}

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<Int>.div(value: Int): DataColumn<Int> = map { it / value }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_COLUMN_TYPE] [ColumnReference]
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][ColumnReference.times],
 * [plus][ColumnReference.plus], [minus][ColumnReference.minus]
 */
public operator fun ColumnReference<Int>.div(value: Int): ColumnReference<Int> = map { it / value }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_NULL_NOTE] `null` values are not changed by this operation.
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
@JvmName("divIntNullable")
public operator fun DataColumn<Int?>.div(value: Int): DataColumn<Int?> = map { it?.div(value) }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_INT_NOTE]
 * @set [COLUMN_DIV_NUMBER_EXAMPLE] // In a DataFrame of distances in meters, convert them to kilometers
 * val distanceKm = df.distanceMeters / 1000.0
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 * @set [COLUMN_DIV_NUMBER_ZERO_ERROR]
 */
@JvmName("divInt")
public operator fun DataColumn<Int>.div(value: Double): DataColumn<Double> = map { it / value }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_INT_NOTE]
 * @set [COLUMN_DIV_NUMBER_EXAMPLE]
 * // In a DataFrame of hourly travel distances, compute the average distance traveled per minute
 * val distancePerMinute = df.distancePerHour / 60
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 * @set [COLUMN_DIV_NUMBER_ZERO_ERROR]
 */
@JvmName("divDouble")
public operator fun DataColumn<Double>.div(value: Int): DataColumn<Double> = map { it / value }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_INT_NOTE]
 * @set [COLUMN_DIV_NUMBER_EXAMPLE] // In a DataFrame of distances in meters, convert them to kilometers
 * val distanceKm = df.distanceMeters / 1000.0
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 * @set [COLUMN_DIV_NUMBER_ZERO_ERROR]
 */
public operator fun DataColumn<Double>.div(value: Double): DataColumn<Double> = map { it / value }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_EXAMPLE] // In a DataFrame of item counts,
 * // compute how many full boxes of 10 items can be formed
 * val fullBoxes = df.itemCount / 10L
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<Long>.div(value: Long): DataColumn<Long> = map { it / value }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_INT_NOTE]
 * @set [COLUMN_DIV_NUMBER_EXAMPLE] // In a DataFrame of distances in miles, convert them to kilometers
 * val distanceKm = df.distanceMiles / BigDecimal("0.62137")
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<BigDecimal>.div(value: BigDecimal): DataColumn<BigDecimal> = map { it / value }

/**
 * @include [ColumnDivNumberDocs]
 * @set [COLUMN_DIV_NUMBER_EXAMPLE] // In a DataFrame of item counts stored as BigInteger values,
 * // compute how many full batches of 1,000 items can be formed
 * val batches = df.itemCount / BigInteger("1000")
 * @set [COLUMN_DIV_NUMBER_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun DataColumn<BigInteger>.div(value: BigInteger): DataColumn<BigInteger> = map { it / value }

// endregion

// region NumberDivColumn

/**
 * Returns a {@get [NUMBER_DIV_COLUMN_COLUMN_TYPE] [DataColumn]} containing the results of dividing
 * this {@get [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [Int]} by each element of [\column].
 *
 * {@get [NUMBER_DIV_COLUMN_INT_NOTE] The result of each division is truncated to an integer that is closer to zero.}
 *
 * {@get [NUMBER_DIV_COLUMN_NULL_NOTE]}
 *
 * ### Example
 * ```kotlin
 * {@get [NUMBER_DIV_COLUMN_EXAMPLE] // In a DataFrame of tasks with estimated hours,
 * // compute how many tasks can fit into a fixed 40-hour work week
 * val tasksPerWeek = 40 / df.estimatedHours}
 * ```
 *
 * See also {@get [NUMBER_DIV_COLUMN_SEE_ALSO]}.
 *
 * @param [\column] A {@get [NUMBER_DIV_COLUMN_COLUMN_TYPE] [DataColumn]} containing the elements
 * to divide this {@get [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [Int]} by.
 *
 * @throws {@get [NUMBER_DIV_COLUMN_ZERO_ERROR] [\ArithmeticException] if [\column] contains zero.}
 *
 * @return A {@get [NUMBER_DIV_COLUMN_COLUMN_TYPE] [DataColumn]} containing the results
 * of dividing this {@get [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [Int]} by each element of [\column].
 */
@ExcludeFromSources
private interface NumberDivColumnDocs {
    // the type of the column passed to the function and returned by the function
    typealias NUMBER_DIV_COLUMN_COLUMN_TYPE = Nothing

    // `See also` section in the documentation
    typealias NUMBER_DIV_COLUMN_SEE_ALSO = Nothing

    // info about truncating the result to an integer that is closer to zero
    typealias NUMBER_DIV_COLUMN_INT_NOTE = Nothing

    // info about handling `null` values
    typealias NUMBER_DIV_COLUMN_NULL_NOTE = Nothing

    // the type of the number on which the function is applied
    typealias NUMBER_DIV_COLUMN_DIVIDEND_TYPE = Nothing

    // the example used in the documentation
    typealias NUMBER_DIV_COLUMN_EXAMPLE = Nothing

    // the exception thrown when dividing by zero
    typealias NUMBER_DIV_COLUMN_ZERO_ERROR = Nothing
}

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun Int.div(column: DataColumn<Int>): DataColumn<Int> = column.map { this / it }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_COLUMN_TYPE] [ColumnReference]
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][ColumnReference.times],
 * [plus][ColumnReference.plus], [minus][ColumnReference.minus]
 */
public operator fun Int.div(column: ColumnReference<Int>): ColumnReference<Int> = column.map { this / it }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_NULL_NOTE] If an element of [column] is `null`,
 * the corresponding value in the resulting [DataColumn] is also `null`.
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
@JvmName("divNullable")
public operator fun Int.div(column: DataColumn<Int?>): DataColumn<Int?> = column.map { it?.let { this / it } }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [Double]
 * @set [NUMBER_DIV_COLUMN_INT_NOTE]
 * @set [NUMBER_DIV_COLUMN_EXAMPLE] // Given a marketing budget of 1,000 euros,
 * // compute the cost per acquired customer for each campaign
 * val costPerCustomer = 1000.0 / df.acquiredCustomers
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 * @set [NUMBER_DIV_COLUMN_ZERO_ERROR]
 */
@JvmName("doubleDiv")
public operator fun Double.div(column: DataColumn<Int>): DataColumn<Double> = column.map { this / it }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_INT_NOTE]
 * @set [NUMBER_DIV_COLUMN_EXAMPLE] // In a DataFrame of prices of a square meter in different places,
 * // compute how many square meters can be bought with a budget of 500 thousand euros
 * val squareMeters = 500_000 / df.pricePerSquareMeter
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 * @set [NUMBER_DIV_COLUMN_ZERO_ERROR]
 */
@JvmName("intDiv")
public operator fun Int.div(column: DataColumn<Double>): DataColumn<Double> = column.map { this / it }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [Double]
 * @set [NUMBER_DIV_COLUMN_INT_NOTE]
 * @set [NUMBER_DIV_COLUMN_EXAMPLE] // In a DataFrame of prices of a square meter in different places,
 * // compute how many square meters can be bought with a budget of 500 thousand euros
 * val squareMeters = 500_000.0 / df.pricePerSquareMeter
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 * @set [NUMBER_DIV_COLUMN_ZERO_ERROR]
 */
public operator fun Double.div(column: DataColumn<Double>): DataColumn<Double> = column.map { this / it }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [Long]
 * @set [NUMBER_DIV_COLUMN_EXAMPLE] // In a DataFrame of file sizes in bits,
 * // compute how many copies of each file can fit into the given storage capacity
 * val fileCopies = 10_000_000_000L / df.fileSize
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun Long.div(column: DataColumn<Long>): DataColumn<Long> = column.map { this / it }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [BigDecimal]
 * @set [NUMBER_DIV_COLUMN_INT_NOTE]
 * @set [NUMBER_DIV_COLUMN_EXAMPLE] // In a DataFrame of prices of a product per gram,
 * // compute the amount of product that can be bought with a budget of 3,451.76 euros
 * val productAmount = BigDecimal("3451.76") / df.pricePerGram
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun BigDecimal.div(column: DataColumn<BigDecimal>): DataColumn<BigDecimal> = column.map { this / it }

/**
 * @include [NumberDivColumnDocs]
 * @set [NUMBER_DIV_COLUMN_DIVIDEND_TYPE] [BigInteger]
 * @set [NUMBER_DIV_COLUMN_EXAMPLE] // In a DataFrame of file sizes in bits,
 * // compute how many copies of each file can fit into the given storage capacity
 * val fileCopies = BigInteger("10000000000") / df.fileSize
 * @set [NUMBER_DIV_COLUMN_SEE_ALSO] [times][DataColumn.times], [plus][DataColumn.plus], [minus][DataColumn.minus]
 */
public operator fun BigInteger.div(column: DataColumn<BigInteger>): DataColumn<BigInteger> = column.map { this / it }

// endregion

// region Compare

/**
 * Returns a [DataColumn] containing the [results][Boolean] of comparing each element of this [DataColumn]
 * with [\value] {@get [COMPARE_DESCRIPTION]}.
 *
 * ### Example
 * ```kotlin
 * {@get [COMPARE_EXAMPLE]}
 * ```
 *
 * See also {@get [COMPARE_SEE_ALSO]}.
 *
 * @param [\value] The value to compare each element of this [DataColumn] with.
 *
 * @return A [DataColumn] containing `true` for elements {@get [COMPARE_OPERATION]} [\value], and `false` otherwise.
 */
@ExcludeFromSources
private interface CompareDocs {
    // the description of the operation
    typealias COMPARE_DESCRIPTION = Nothing

    // the example used in the documentation
    typealias COMPARE_EXAMPLE = Nothing

    // `See also` section in the documentation
    typealias COMPARE_SEE_ALSO = Nothing

    // the meaning of the operation to adjust in the section about the return value
    typealias COMPARE_OPERATION = Nothing
}

/**
 * @include [CompareDocs]
 * @set [COMPARE_DESCRIPTION] for equality using the `==` operator
 * @set [COMPARE_EXAMPLE] // Given a DataFrame of orders with statuses represented as strings,
 * // create a column that indicates whether each order is canceled
 * val isCanceled = df.status eq "canceled"
 * @set [COMPARE_SEE_ALSO] [neq][DataColumn.neq], [gt][DataColumn.gt], [lt][DataColumn.lt]
 * @set [COMPARE_OPERATION] equal to
 */
public infix fun <T> DataColumn<T>.eq(value: T): DataColumn<Boolean> = map { it == value }

/**
 * @include [CompareDocs]
 * @set [COMPARE_DESCRIPTION] for inequality using the `!=` operator
 * @set [COMPARE_EXAMPLE] // Given a DataFrame of orders with statuses represented as strings,
 * // create a column that indicates which orders are not completed
 * val isNotCompleted = df.status neq "completed"
 * @set [COMPARE_SEE_ALSO] [eq][DataColumn.eq], [gt][DataColumn.gt], [lt][DataColumn.lt]
 * @set [COMPARE_OPERATION] not equal to
 */
public infix fun <T> DataColumn<T>.neq(value: T): DataColumn<Boolean> = map { it != value }

/**
 * @include [CompareDocs]
 * @set [COMPARE_DESCRIPTION] using the `>` operator
 * @set [COMPARE_EXAMPLE] // Given a DataFrame of orders,
 * // create a column that indicates which orders cost more than 1,000 euros
 * val isExpensive = df.orderCost gt 1000
 * @set [COMPARE_SEE_ALSO] [eq][DataColumn.eq], [neq][DataColumn.neq], [lt][DataColumn.lt]
 * @set [COMPARE_OPERATION] greater than
 */
public infix fun <T : Comparable<T>> DataColumn<T>.gt(value: T): DataColumn<Boolean> = map { it > value }

/**
 * @include [CompareDocs]
 * @set [COMPARE_DESCRIPTION] using the `<` operator
 * @set [COMPARE_EXAMPLE] // Given a DataFrame of orders,
 * // create a column that indicates which orders cost less than 20 euros
 * val isCheap = df.orderCost lt 20
 * @set [COMPARE_SEE_ALSO] [eq][DataColumn.eq], [neq][DataColumn.neq], [gt][DataColumn.gt]
 * @set [COMPARE_OPERATION] less than
 */
public infix fun <T : Comparable<T>> DataColumn<T>.lt(value: T): DataColumn<Boolean> = map { it < value }
// endregion
