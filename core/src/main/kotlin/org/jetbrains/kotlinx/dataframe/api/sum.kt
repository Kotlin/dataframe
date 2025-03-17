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
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.primitiveNumberTypes
import org.jetbrains.kotlinx.dataframe.impl.zero
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region DataColumn

@JvmName("sumInt")
public fun DataColumn<Int?>.sum(): Int = Aggregators.sum.aggregate(this) as Int

@JvmName("sumShort")
public fun DataColumn<Short?>.sum(): Int = Aggregators.sum.aggregate(this) as Int

@JvmName("sumByte")
public fun DataColumn<Byte?>.sum(): Int = Aggregators.sum.aggregate(this) as Int

@JvmName("sumLong")
public fun DataColumn<Long?>.sum(): Long = Aggregators.sum.aggregate(this) as Long

@JvmName("sumFloat")
public fun DataColumn<Float?>.sum(): Float = Aggregators.sum.aggregate(this) as Float

@JvmName("sumDouble")
public fun DataColumn<Double?>.sum(): Double = Aggregators.sum.aggregate(this) as Double

@JvmName("sumNumber")
public fun DataColumn<Number?>.sum(): Number = Aggregators.sum.aggregate(this)

@JvmName("sumOfInt")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.sumOf(expression: (T) -> Int?): Int = Aggregators.sum.aggregateOf(this, expression) as Int

@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.sumOf(expression: (T) -> Short?): Int =
    Aggregators.sum.aggregateOf(this, expression) as Int

@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.sumOf(expression: (T) -> Byte?): Int = Aggregators.sum.aggregateOf(this, expression) as Int

@JvmName("sumOfLong")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.sumOf(expression: (T) -> Long?): Long =
    Aggregators.sum.aggregateOf(this, expression) as Long

@JvmName("sumOfFloat")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.sumOf(expression: (T) -> Float?): Float =
    Aggregators.sum.aggregateOf(this, expression) as Float

@JvmName("sumOfDouble")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.sumOf(expression: (T) -> Double?): Double =
    Aggregators.sum.aggregateOf(this, expression) as Double

@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public fun <T> DataColumn<T>.sumOf(expression: (T) -> Number?): Number = Aggregators.sum.aggregateOf(this, expression)

// endregion

// region DataRow

public fun AnyRow.rowSum(): Number =
    Aggregators.sum.aggregateOfRow(this) {
        colsOf<Number?> { it.isPrimitiveNumber() }
    }

public inline fun <reified T : Number> AnyRow.rowSumOf(): Number /*todo*/ {
    require(typeOf<T>() in primitiveNumberTypes) {
        "Type ${T::class.simpleName} is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.sum
        .aggregateOfRow(this) { colsOf<T>() }
}
// endregion

// region DataFrame

public fun <T> DataFrame<T>.sum(): DataRow<T> = sumFor(numberColumns())

public fun <T, C : Number> DataFrame<T>.sumFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> =
    Aggregators.sum.aggregateFor(this, columns)

public fun <T> DataFrame<T>.sumFor(vararg columns: String): DataRow<T> = sumFor { columns.toColumnsSetOf() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: ColumnReference<C?>): DataRow<T> =
    sumFor { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: KProperty<C?>): DataRow<T> =
    sumFor { columns.toColumnSet() }

public inline fun <T, reified C : Number> DataFrame<T>.sum(noinline columns: ColumnsSelector<T, C?>): C =
    (Aggregators.sum.aggregateAll(this, columns) as C?) ?: C::class.zero()

@AccessApiOverload
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: ColumnReference<C?>): C =
    sum { columns.toColumnSet() }

public fun <T> DataFrame<T>.sum(vararg columns: String): Number = sum { columns.toColumnsSetOf() }

@AccessApiOverload
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: KProperty<C?>): C =
    sum { columns.toColumnSet() }

public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): C =
    rows().sumOf(typeOf<C>()) { expression(it, it) }

// endregion

// region GroupBy
@Refine
@Interpretable("GroupBySum1")
public fun <T> Grouped<T>.sum(): DataFrame<T> = sumFor(numberColumns())

@Refine
@Interpretable("GroupBySum0")
public fun <T, C : Number> Grouped<T>.sumFor(columns: ColumnsForAggregateSelector<T, C?>): DataFrame<T> =
    Aggregators.sum.aggregateFor(this, columns)

public fun <T> Grouped<T>.sumFor(vararg columns: String): DataFrame<T> = sumFor { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sumFor(vararg columns: ColumnReference<C?>): DataFrame<T> =
    sumFor { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sumFor(vararg columns: KProperty<C?>): DataFrame<T> =
    sumFor { columns.toColumnSet() }

@Refine
@Interpretable("GroupBySum0")
public fun <T, C : Number> Grouped<T>.sum(name: String? = null, columns: ColumnsSelector<T, C?>): DataFrame<T> =
    Aggregators.sum.aggregateAll(this, name, columns)

public fun <T> Grouped<T>.sum(vararg columns: String, name: String? = null): DataFrame<T> =
    sum(name) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sum(vararg columns: ColumnReference<C?>, name: String? = null): DataFrame<T> =
    sum(name) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sum(vararg columns: KProperty<C?>, name: String? = null): DataFrame<T> =
    sum(name) { columns.toColumnSet() }

@Refine
@Interpretable("GroupBySumOf")
public inline fun <T, reified R : Number> Grouped<T>.sumOf(
    resultName: String? = null,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> = Aggregators.sum.aggregateOf(this, resultName, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.sum(separate: Boolean = false): DataRow<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> Pivot<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>,
): DataRow<T> = delegate { sumFor(separate, columns) }

public fun <T> Pivot<T>.sumFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    sumFor(separate) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
): DataRow<T> = sumFor(separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sumFor(vararg columns: KProperty<C?>, separate: Boolean = false): DataRow<T> =
    sumFor(separate) { columns.toColumnSet() }

public fun <T, C : Number> Pivot<T>.sum(columns: ColumnsSelector<T, C?>): DataRow<T> = delegate { sum(columns) }

public fun <T> Pivot<T>.sum(vararg columns: String): DataRow<T> = sum { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sum(vararg columns: ColumnReference<C?>): DataRow<T> = sum { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sum(vararg columns: KProperty<C?>): DataRow<T> = sum { columns.toColumnSet() }

public inline fun <T, reified R : Number> Pivot<T>.sumOf(crossinline expression: RowExpression<T, R>): DataRow<T> =
    delegate { sumOf(expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false): DataFrame<T> = sumFor(separate, numberColumns())

public fun <T, R : Number> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R?>,
): DataFrame<T> = Aggregators.sum.aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.sumFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    sumFor(separate) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
): DataFrame<T> = sumFor(separate) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sumFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
): DataFrame<T> = sumFor(separate) { columns.toColumnSet() }

public fun <T, C : Number> PivotGroupBy<T>.sum(columns: ColumnsSelector<T, C?>): DataFrame<T> =
    Aggregators.sum.aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.sum(vararg columns: String): DataFrame<T> = sum { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sum(vararg columns: ColumnReference<C?>): DataFrame<T> =
    sum { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sum(vararg columns: KProperty<C?>): DataFrame<T> =
    sum { columns.toColumnSet() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.sumOf(
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.sum.aggregateOf(this, expression)

// endregion
