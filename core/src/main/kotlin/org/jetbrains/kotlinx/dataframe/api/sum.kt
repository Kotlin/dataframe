@file:OptIn(ExperimentalTypeInference::class)
@file:Suppress("LocalVariableName")

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
import org.jetbrains.kotlinx.dataframe.impl.aggregation.primitiveNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.primitiveNumberTypes
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/* TODO KDocs
 * Calculating the sum is supported for all primitive number types.
 * Nulls are filtered out.
 * The return type is always the same as the input type (never null), except for `Byte` and `Short`,
 * which are converted to `Int`.
 * Empty input will result in 0 in the supplied number type.
 * For mixed primitive number types, [TwoStepNumbersAggregator] unifies the numbers before calculating the sum.
 */

// region DataColumn

@JvmName("sumShort")
public fun DataColumn<Short?>.sum(): Int = Aggregators.sum.aggregate(this) as Int

@JvmName("sumByte")
public fun DataColumn<Byte?>.sum(): Int = Aggregators.sum.aggregate(this) as Int

@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
public fun <T : Number> DataColumn<T?>.sum(): T = Aggregators.sum.aggregate(this) as T

@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public fun <C> DataColumn<C>.sumOf(expression: (C) -> Short?): Int =
    Aggregators.sum.aggregateOf(this, expression) as Int

@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public fun <C> DataColumn<C>.sumOf(expression: (C) -> Byte?): Int = Aggregators.sum.aggregateOf(this, expression) as Int

@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Number> DataColumn<C>.sumOf(crossinline expression: (C) -> V?): V =
    Aggregators.sum.aggregateOf(this, expression) as V

// endregion

// region DataRow

public fun AnyRow.rowSum(): Number = Aggregators.sum.aggregateOfRow(this, primitiveNumberColumns())

@JvmName("rowSumOfShort")
public inline fun <reified T : Short?> AnyRow.rowSumOf(_kClass: KClass<Short> = Short::class): Int =
    rowSumOf(typeOf<T>()) as Int

@JvmName("rowSumOfByte")
public inline fun <reified T : Byte?> AnyRow.rowSumOf(_kClass: KClass<Byte> = Byte::class): Int =
    rowSumOf(typeOf<T>()) as Int

@JvmName("rowSumOfInt")
public inline fun <reified T : Int?> AnyRow.rowSumOf(_kClass: KClass<Int> = Int::class): Int =
    rowSumOf(typeOf<T>()) as Int

@JvmName("rowSumOfLong")
public inline fun <reified T : Long?> AnyRow.rowSumOf(_kClass: KClass<Long> = Long::class): Long =
    rowSumOf(typeOf<T>()) as Long

@JvmName("rowSumOfFloat")
public inline fun <reified T : Float?> AnyRow.rowSumOf(_kClass: KClass<Float> = Float::class): Float =
    rowSumOf(typeOf<T>()) as Float

@JvmName("rowSumOfDouble")
public inline fun <reified T : Double?> AnyRow.rowSumOf(_kClass: KClass<Double> = Double::class): Double =
    rowSumOf(typeOf<T>()) as Double

// unfortunately, we cannot make a `reified T : Number?` due to clashes
public fun AnyRow.rowSumOf(type: KType): Number {
    require(type.withNullability(false) in primitiveNumberTypes) {
        "Type $type is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.sum.aggregateOfRow(this) { colsOf(type) }
}
// endregion

// region DataFrame

public fun <T> DataFrame<T>.sum(): DataRow<T> = sumFor(primitiveNumberColumns())

public fun <T, C : Number> DataFrame<T>.sumFor(columns: ColumnsForAggregateSelector<T, C?>): DataRow<T> =
    Aggregators.sum.aggregateFor(this, columns)

public fun <T> DataFrame<T>.sumFor(vararg columns: String): DataRow<T> = sumFor { columns.toColumnsSetOf() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: ColumnReference<C?>): DataRow<T> =
    sumFor { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.sumFor(vararg columns: KProperty<C?>): DataRow<T> =
    sumFor { columns.toColumnSet() }

@JvmName("sumShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sum(columns: ColumnsSelector<T, Short?>): Int =
    Aggregators.sum.aggregateAll(this, columns) as Int

@JvmName("sumByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sum(columns: ColumnsSelector<T, Byte?>): Int =
    Aggregators.sum.aggregateAll(this, columns) as Int

@JvmName("sumNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Number> DataFrame<T>.sum(noinline columns: ColumnsSelector<T, C?>): C =
    Aggregators.sum.aggregateAll(this, columns) as C

@JvmName("sumShort")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: ColumnReference<Short?>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: ColumnReference<Byte?>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@AccessApiOverload
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: ColumnReference<C?>): C =
    sum { columns.toColumnSet() }

public fun <T> DataFrame<T>.sum(vararg columns: String): Number = sum { columns.toColumnsSetOf<Number?>() }

@JvmName("sumShort")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Short?>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Byte?>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@AccessApiOverload
public inline fun <T, reified C : Number> DataFrame<T>.sum(vararg columns: KProperty<C?>): C =
    sum { columns.toColumnSet() }

@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sumOf(expression: RowExpression<T, Short?>): Int =
    Aggregators.sum.aggregateOf(this, expression) as Int

@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sumOf(expression: RowExpression<T, Byte?>): Int =
    Aggregators.sum.aggregateOf(this, expression) as Int

@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Number> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C?>): C =
    Aggregators.sum.aggregateOf(this, expression) as C

// endregion

// region GroupBy
@Refine
@Interpretable("GroupBySum1")
public fun <T> Grouped<T>.sum(): DataFrame<T> = sumFor(primitiveNumberColumns())

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

public fun <T> Pivot<T>.sum(separate: Boolean = false): DataRow<T> = sumFor(separate, primitiveNumberColumns())

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

public inline fun <T, reified R : Number> Pivot<T>.sumOf(crossinline expression: RowExpression<T, R?>): DataRow<T> =
    delegate { sumOf(expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false): DataFrame<T> = sumFor(separate, primitiveNumberColumns())

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
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> = Aggregators.sum.aggregateOf(this, expression)

// endregion
