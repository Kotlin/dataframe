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
import org.jetbrains.kotlinx.dataframe.math.mean
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty
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
public fun DataColumn<Number?>.mean(skipNA: Boolean = skipNA_default): Number? = meanOrNull(skipNA)

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

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfInt")
//@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Int?): Double =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Int?, Double>()
        .aggregateOf(this, expression)
        ?: Double.NaN

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Short?): Double =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Short?, Double>()
        .aggregateOf(this, expression)
        ?: Double.NaN

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Byte?): Double =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Byte?, Double>()
        .aggregateOf(this, expression)
        ?: Double.NaN

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfLong")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> Long?): Double =
    Aggregators.mean.toDouble(skipNA_default)
        .cast2<Long?, Double>()
        .aggregateOf(this, expression)
        ?: Double.NaN

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfDouble")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(skipNA: Boolean = skipNA_default, expression: (T) -> Double?): Double =
    Aggregators.mean.toDouble(skipNA)
        .cast2<Double?, Double>()
        .aggregateOf(this, expression)
        ?: Double.NaN

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfFloat")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(skipNA: Boolean = skipNA_default, expression: (T) -> Float?): Double =
    Aggregators.mean.toDouble(skipNA)
        .cast2<Float?, Double>()
        .aggregateOf(this, expression)
        ?: Double.NaN

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfBigInteger")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> BigInteger?): BigDecimal? =
    Aggregators.mean.toBigDecimal
        .cast2<BigInteger?, BigDecimal?>()
        .aggregateOf(this, expression)

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfBigDecimal")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(expression: (T) -> BigDecimal?): BigDecimal? =
    Aggregators.mean.toBigDecimal
        .cast2<BigDecimal?, BigDecimal?>()
        .aggregateOf(this, expression)

@OptIn(ExperimentalTypeInference::class)
@JvmName("meanOfNumber")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.meanOf(skipNA: Boolean = skipNA_default, expression: (T) -> Number?): Number? =
    Aggregators.mean.toNumber(skipNA)
        .cast2<Number?, Number?>()
        .aggregateOf(this, expression)

public fun main() {
    val data = (1..10).toList()
    val df = data.toDataFrame()

    val mean = df.value.meanOf { if (true) it.toLong() else it.toDouble() }
    val mean2 = df.value.meanOf { it.toBigInteger() }

    println(mean)
    println(mean!!::class)
}

// endregion

// endregion

// region DataRow
// todo
public fun AnyRow.rowMean(skipNA: Boolean = skipNA_default): Double =
    values().filterIsInstance<Number>().map { it.toDouble() }.mean(skipNA)

public inline fun <reified T : Number> AnyRow.rowMeanOf(): Double =
    values().filterIsInstance<T>().mean(typeOf<T>()) as Double

// endregion

// region DataFrame

public fun <T> DataFrame<T>.mean(skipNA: Boolean = skipNA_default): DataRow<T> = meanFor(skipNA, numberColumns())

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

// todo
public fun <T, C : Number> DataFrame<T>.mean(
    skipNA: Boolean = skipNA_default,
    columns: ColumnsSelector<T, C?>,
): Double = Aggregators.mean.toNumber(skipNA).aggregateAll(this, columns) as Double? ?: Double.NaN

public fun <T> DataFrame<T>.mean(vararg columns: String, skipNA: Boolean = skipNA_default): Double =
    mean(skipNA) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.mean(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
): Double = mean(skipNA) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.mean(vararg columns: KProperty<C?>, skipNA: Boolean = skipNA_default): Double =
    mean(skipNA) { columns.toColumnSet() }

public inline fun <T, reified D : Number> DataFrame<T>.meanOf(
    skipNA: Boolean = skipNA_default,
    noinline expression: RowExpression<T, D?>,
): Double = Aggregators.mean.toNumber(skipNA).of(this, expression) as Double? ?: Double.NaN

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
): DataFrame<T> = Aggregators.mean.toNumber(skipNA).aggregateOf(this, name, expression)

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
