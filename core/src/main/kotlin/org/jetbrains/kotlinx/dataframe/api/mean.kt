@file:OptIn(ExperimentalTypeInference::class)

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast2
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.of
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.math.meanOrNull
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

// region DataColumn

// region mean

@JvmName("meanInt")
public fun DataColumn<Int?>.mean(): Double = meanOrNull().suggestIfNull("mean")

@JvmName("meanShort")
public fun DataColumn<Short?>.mean(): Double = meanOrNull().suggestIfNull("mean")

@JvmName("meanByte")
public fun DataColumn<Byte?>.mean(): Double = meanOrNull().suggestIfNull("mean")

@JvmName("meanLong")
public fun DataColumn<Long?>.mean(): Double = meanOrNull().suggestIfNull("mean")

@JvmName("meanDouble")
public fun DataColumn<Double?>.mean(skipNA: Boolean = skipNA_default): Double = meanOrNull(skipNA).suggestIfNull("mean")

@JvmName("meanFloat")
public fun DataColumn<Float?>.mean(skipNA: Boolean = skipNA_default): Double = meanOrNull(skipNA).suggestIfNull("mean")

@JvmName("meanBigInteger")
public fun DataColumn<BigInteger?>.mean(): BigDecimal = meanOrNull().suggestIfNull("mean")

@JvmName("meanBigDecimal")
public fun DataColumn<BigDecimal?>.mean(): BigDecimal = meanOrNull().suggestIfNull("mean")

@JvmName("meanNumber")
public fun DataColumn<Number?>.mean(skipNA: Boolean = skipNA_default): Number = meanOrNull(skipNA).suggestIfNull("mean")

// endregion

// region meanOrNull

@JvmName("meanOrNullInt")
public fun DataColumn<Int?>.meanOrNull(): Double? = Aggregators.mean.toDouble(skipNA_default).aggregate(this)

@JvmName("meanOrNullShort")
public fun DataColumn<Short?>.meanOrNull(): Double? = Aggregators.mean.toDouble(skipNA_default).aggregate(this)

@JvmName("meanOrNullByte")
public fun DataColumn<Byte?>.meanOrNull(): Double? = Aggregators.mean.toDouble(skipNA_default).aggregate(this)

@JvmName("meanOrNullLong")
public fun DataColumn<Long?>.meanOrNull(): Double? = Aggregators.mean.toDouble(skipNA_default).aggregate(this)

@JvmName("meanOrNullDouble")
public fun DataColumn<Double?>.meanOrNull(skipNA: Boolean = skipNA_default): Double? =
    Aggregators.mean.toDouble(skipNA).aggregate(this)

@JvmName("meanOrNullFloat")
public fun DataColumn<Float?>.meanOrNull(skipNA: Boolean = skipNA_default): Double? =
    Aggregators.mean.toDouble(skipNA).aggregate(this)

@JvmName("meanOrNullBigInteger")
public fun DataColumn<BigInteger?>.meanOrNull(): BigDecimal? = Aggregators.mean.toBigDecimal.aggregate(this)

@JvmName("meanOrNullBigDecimal")
public fun DataColumn<BigDecimal?>.meanOrNull(): BigDecimal? = Aggregators.mean.toBigDecimal.aggregate(this)

@JvmName("meanOrNullNumber")
public fun DataColumn<Number?>.meanOrNull(skipNA: Boolean = skipNA_default): Number? =
    Aggregators.mean.toNumber(skipNA).aggregate(this)

// endregion

// region meanOf

@JvmName("meanOfInt")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Int?): Double = meanOfOrNull(expression).suggestIfNull("meanOf")

@JvmName("meanOfShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Short?): Double =
    meanOfOrNull(expression).suggestIfNull("meanOf")

@JvmName("meanOfByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Byte?): Double = meanOfOrNull(expression).suggestIfNull("meanOf")

@JvmName("meanOfLong")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Long?): Double = meanOfOrNull(expression).suggestIfNull("meanOf")

@JvmName("meanOfDouble")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(skipNA: Boolean = skipNA_default, expression: (T) -> Double?): Double =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

@JvmName("meanOfFloat")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(skipNA: Boolean = skipNA_default, expression: (T) -> Float?): Double =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

@JvmName("meanOfBigInteger")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> BigInteger?): BigDecimal =
    meanOfOrNull(expression).suggestIfNull("meanOf")

@JvmName("meanOfBigDecimal")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> BigDecimal?): BigDecimal =
    meanOfOrNull(expression).suggestIfNull("meanOf")

@JvmName("meanOfNumber")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(skipNA: Boolean = skipNA_default, expression: (T) -> Number?): Number =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

// endregion

// region meanOfOrNull

@JvmName("meanOfOrNullInt")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(expression: (T) -> Int?): Double? =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Int?, Double>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(expression: (T) -> Short?): Double? =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Short?, Double>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(expression: (T) -> Byte?): Double? =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Byte?, Double>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullLong")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(expression: (T) -> Long?): Double? =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Long?, Double>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullDouble")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(skipNA: Boolean = skipNA_default, expression: (T) -> Double?): Double? =
    Aggregators.mean.toDouble(skipNA)
        .cast2<Double?, Double>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullFloat")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(skipNA: Boolean = skipNA_default, expression: (T) -> Float?): Double? =
    Aggregators.mean.toDouble(skipNA)
        .cast2<Float?, Double>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullBigInteger")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(expression: (T) -> BigInteger?): BigDecimal? =
    Aggregators.mean.toBigDecimal
        .cast2<BigInteger?, BigDecimal?>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullBigDecimal")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(expression: (T) -> BigDecimal?): BigDecimal? =
    Aggregators.mean.toBigDecimal
        .cast2<BigDecimal?, BigDecimal?>()
        .aggregateOf(this, expression)

@JvmName("meanOfOrNullNumber")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOfOrNull(skipNA: Boolean = skipNA_default, expression: (T) -> Number?): Number? =
    Aggregators.mean.toNumber(skipNA)
        .cast2<Number?, Number?>()
        .aggregateOf(this, expression)

// endregion

// endregion

// region DataRow - rowMean

public fun AnyRow.rowMean(skipNA: Boolean = skipNA_default): Number = rowMeanOrNull(skipNA).suggestIfNull("rowMean")

public fun AnyRow.rowMeanOrNull(skipNA: Boolean = skipNA_default): Number? =
    Aggregators.mean.toNumber(skipNA).aggregateCalculatingType(
        values().filterIsInstance<Number>(),
        columnTypes().filter { it.isSubtypeOf(typeOf<Number?>()) }.toSet(),
    )

public inline fun <reified T : Number> AnyRow.rowMeanOf(): Number = rowMeanOfOrNull<T>().suggestIfNull("rowMeanOf")

public inline fun <reified T : Number> AnyRow.rowMeanOfOrNull(): Number? =
    values().filterIsInstance<T>().meanOrNull(typeOf<T>())

// endregion

// region DataFrame

// region meanFor

public fun <T, C : Number> DataFrame<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataRow<T> = Aggregators.mean.toNumber(skipNA).aggregateFor(this, columns)

public fun <T> DataFrame<T>.meanFor(vararg columns: String, skipNA: Boolean = skipNA_default): DataRow<T> =
    meanFor(skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): DataRow<T> = meanFor(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
): DataRow<T> = meanFor(skipNA) { columns.toColumnSet() }

// endregion

// region mean

public fun <T> DataFrame<T>.mean(skipNA: Boolean = skipNA_default): DataRow<T> = meanFor(skipNA, numberColumns())

@OverloadResolutionByLambdaReturnType
@JvmName("meanInt")
public fun <T> DataFrame<T>.mean(columns: ColumnsSelector<T, Int?>): Double = meanOrNull(columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanShort")
public fun <T> DataFrame<T>.mean(columns: ColumnsSelector<T, Short?>): Double =
    meanOrNull(columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanByte")
public fun <T> DataFrame<T>.mean(columns: ColumnsSelector<T, Byte?>): Double = meanOrNull(columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanLong")
public fun <T> DataFrame<T>.mean(columns: ColumnsSelector<T, Long?>): Double = meanOrNull(columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanDouble")
public fun <T> DataFrame<T>.mean(skipNA: Boolean = skipNA_default, columns: ColumnsSelector<T, Double?>): Double =
    meanOrNull(skipNA, columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanFloat")
public fun <T> DataFrame<T>.mean(skipNA: Boolean = skipNA_default, columns: ColumnsSelector<T, Float?>): Double =
    meanOrNull(skipNA, columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanBigInteger")
public fun <T> DataFrame<T>.mean(columns: ColumnsSelector<T, BigInteger?>): BigDecimal =
    meanOrNull(columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanBigDecimal")
public fun <T> DataFrame<T>.mean(columns: ColumnsSelector<T, BigDecimal?>): BigDecimal =
    meanOrNull(columns).suggestIfNull("mean")

@OverloadResolutionByLambdaReturnType
@JvmName("meanNumber")
public fun <T, C : Number> DataFrame<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, C?>,
): Number = meanOrNull(skipNA, columns).suggestIfNull("mean")

public fun <T> DataFrame<T>.mean(vararg columns: String, skipNA: Boolean = skipNA_default): Number =
    meanOrNull(columns = columns, skipNA = skipNA).suggestIfNull("mean")

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.mean(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): Number = meanOrNull(columns = columns, skipNA = skipNA).suggestIfNull("mean")

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.mean(vararg columns: KProperty<C?>, skipNA: Boolean = skipNA_default): Number =
    meanOrNull(columns = columns, skipNA = skipNA).suggestIfNull("mean")

// endregion

// region meanOrNull
@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullInt")
public fun <T> DataFrame<T>.meanOrNull(columns: ColumnsSelector<T, Int?>): Double? =
    Aggregators.mean.toDouble(skipNA_default).aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullShort")
public fun <T> DataFrame<T>.meanOrNull(columns: ColumnsSelector<T, Short?>): Double? =
    Aggregators.mean.toDouble(skipNA_default).aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullByte")
public fun <T> DataFrame<T>.meanOrNull(columns: ColumnsSelector<T, Byte?>): Double? =
    Aggregators.mean.toDouble(skipNA_default).aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullLong")
public fun <T> DataFrame<T>.meanOrNull(columns: ColumnsSelector<T, Long?>): Double? =
    Aggregators.mean.toDouble(skipNA_default).aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullDouble")
public fun <T> DataFrame<T>.meanOrNull(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, Double?>,
): Double? = Aggregators.mean.toDouble(skipNA).aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullFloat")
public fun <T> DataFrame<T>.meanOrNull(skipNA: Boolean = skipNA_default, columns: ColumnsSelector<T, Float?>): Double? =
    Aggregators.mean.toDouble(skipNA).aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullBigInteger")
public fun <T> DataFrame<T>.meanOrNull(columns: ColumnsSelector<T, BigInteger?>): BigDecimal? =
    Aggregators.mean.toBigDecimal.aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullBigDecimal")
public fun <T> DataFrame<T>.meanOrNull(columns: ColumnsSelector<T, BigDecimal?>): BigDecimal? =
    Aggregators.mean.toBigDecimal.aggregateAll(this, columns)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullNumber")
public fun <T, C : Number> DataFrame<T>.meanOrNull(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, C?>,
): Number? = Aggregators.mean.toNumber(skipNA).aggregateAll(this, columns)

public fun <T> DataFrame<T>.meanOrNull(vararg columns: String, skipNA: Boolean = skipNA_default): Number? =
    meanOrNull(skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.meanOrNull(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): Number? = meanOrNull(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.meanOrNull(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
): Number? = meanOrNull(skipNA) { columns.toColumnSet() }

// endregion

// region meanOf

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfInt")
public fun <T> DataFrame<T>.meanOf(expression: RowExpression<T, Int?>): Double =
    meanOfOrNull(expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfShort")
public fun <T> DataFrame<T>.meanOf(skipNA: Boolean = skipNA_default, expression: RowExpression<T, Short?>): Double =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfByte")
public fun <T> DataFrame<T>.meanOf(skipNA: Boolean = skipNA_default, expression: RowExpression<T, Byte?>): Double =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfLong")
public fun <T> DataFrame<T>.meanOf(skipNA: Boolean = skipNA_default, expression: RowExpression<T, Long?>): Double =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfDouble")
public fun <T> DataFrame<T>.meanOf(skipNA: Boolean = skipNA_default, expression: RowExpression<T, Double?>): Double =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfFloat")
public fun <T> DataFrame<T>.meanOf(skipNA: Boolean = skipNA_default, expression: RowExpression<T, Float?>): Double =
    meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfBigInteger")
public fun <T> DataFrame<T>.meanOf(expression: RowExpression<T, BigInteger?>): BigDecimal =
    meanOfOrNull(expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfBigDecimal")
public fun <T> DataFrame<T>.meanOf(expression: RowExpression<T, BigDecimal?>): BigDecimal =
    meanOfOrNull(expression).suggestIfNull("meanOf")

@OverloadResolutionByLambdaReturnType
@JvmName("meanOfNumber")
public inline fun <T, reified D : Number> DataFrame<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    noinline expression: RowExpression<T, D?>,
): Number = meanOfOrNull(skipNA, expression).suggestIfNull("meanOf")

// endregion

// region meanOfOrNull

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfInt")
public fun <T> DataFrame<T>.meanOfOrNull(expression: RowExpression<T, Int?>): Double? =
    Aggregators.mean.toDouble(skipNA_default).of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfShort")
public fun <T> DataFrame<T>.meanOfOrNull(
    skipNA: Boolean = skipNA_default,
    expression: RowExpression<T, Short?>,
): Double? = Aggregators.mean.toDouble(skipNA).of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfByte")
public fun <T> DataFrame<T>.meanOfOrNull(
    skipNA: Boolean = skipNA_default,
    expression: RowExpression<T, Byte?>,
): Double? = Aggregators.mean.toDouble(skipNA).of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfLong")
public fun <T> DataFrame<T>.meanOfOrNull(
    skipNA: Boolean = skipNA_default,
    expression: RowExpression<T, Long?>,
): Double? = Aggregators.mean.toDouble(skipNA).of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfDouble")
public fun <T> DataFrame<T>.meanOfOrNull(
    skipNA: Boolean = skipNA_default,
    expression: RowExpression<T, Double?>,
): Double? = Aggregators.mean.toDouble(skipNA).of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfFloat")
public fun <T> DataFrame<T>.meanOfOrNull(
    skipNA: Boolean = skipNA_default,
    expression: RowExpression<T, Float?>,
): Double? = Aggregators.mean.toDouble(skipNA).of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfBigInteger")
public fun <T> DataFrame<T>.meanOfOrNull(expression: RowExpression<T, BigInteger?>): BigDecimal? =
    Aggregators.mean.toBigDecimal.of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfBigDecimal")
public fun <T> DataFrame<T>.meanOfOrNull(expression: RowExpression<T, BigDecimal?>): BigDecimal? =
    Aggregators.mean.toBigDecimal.of(this, expression)

@OverloadResolutionByLambdaReturnType
@JvmName("meanOrNullOfNumber")
public inline fun <T, reified D : Number> DataFrame<T>.meanOfOrNull(
    skipNA: Boolean = skipNA_default,
    noinline expression: RowExpression<T, D?>,
): Number? = Aggregators.mean.toNumber(skipNA).of(this, expression)

// endregion

// endregion

// region GroupBy

public fun <T> Grouped<T>.mean(skipNA: Boolean = skipNA_default): DataFrame<T> = meanFor(skipNA, numberColumns())

public fun <T, C : Number> Grouped<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataFrame<T> = Aggregators.mean.toNumber(skipNA).aggregateFor(this, columns)

public fun <T> Grouped<T>.meanFor(vararg columns: String, skipNA: Boolean = skipNA_default): DataFrame<T> =
    meanFor(skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA) { columns.toColumnSet() }

public fun <T, C : Number> Grouped<T>.mean(
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.mean.toNumber(skipNA).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.mean(
    vararg columns: String,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: ColumnReference<C?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.mean(
    vararg columns: KProperty<C?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(name, skipNA) { columns.toColumnSet() }

public inline fun <T, reified R : Number> Grouped<T>.meanOf(
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> =
    Aggregators.mean.toNumber(skipNA)
        .aggregateOf(this, name, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.mean(skipNA: Boolean = skipNA_default, separate: Boolean = false): DataRow<T> =
    meanFor(skipNA, separate, numberColumns())

public fun <T, C : Number> Pivot<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataRow<T> = delegate { meanFor(skipNA, separate, columns) }

public fun <T> Pivot<T>.meanFor(
    vararg columns: String,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.meanFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
): DataRow<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, R : Number> Pivot<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, R?>,
): DataRow<T> = delegate { mean(skipNA, columns) }

public inline fun <T, reified R : Number> Pivot<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>,
): DataRow<T> = delegate { meanOf(skipNA, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.mean(separate: Boolean = false, skipNA: Boolean = skipNA_default): DataFrame<T> =
    meanFor(skipNA, separate, numberColumns())

public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    skipNA: Boolean = skipNA_default,
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataFrame<T> = Aggregators.mean.toNumber(skipNA).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.meanFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.meanFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = meanFor(skipNA, separate) { columns.toColumnSet() }

public fun <T, R : Number> PivotGroupBy<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, R?>,
): DataFrame<T> = Aggregators.mean.toNumber(skipNA).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.mean(vararg columns: String, skipNA: Boolean = skipNA_default): DataFrame<T> =
    mean(skipNA) { columns.toColumnsSetOf() }

@AccessApiOverload
public fun <T, R : Number> PivotGroupBy<T>.mean(
    vararg columns: ColumnReference<R?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, R : Number> PivotGroupBy<T>.mean(
    vararg columns: KProperty<R?>,
    skipNA: Boolean = skipNA_default,
): DataFrame<T> = mean(skipNA) { columns.toColumnSet() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> = Aggregators.mean.toNumber(skipNA).aggregateOf(this, expression)

// endregion
