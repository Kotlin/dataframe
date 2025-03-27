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
import org.jetbrains.kotlinx.dataframe.api.skipNaN_default
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOfRow
import org.jetbrains.kotlinx.dataframe.impl.aggregation.primitiveOrMixedNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
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
public fun DataColumn<Short?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

@JvmName("sumByte")
public fun DataColumn<Byte?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
public fun <T : Number> DataColumn<T?>.sum(skipNaN: Boolean = skipNaN_default): T =
    Aggregators.sum(skipNaN).aggregateSingleColumn(this) as T

@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public fun <C> DataColumn<C>.sumOf(expression: (C) -> Short?): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public fun <C> DataColumn<C>.sumOf(expression: (C) -> Byte?): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Number> DataColumn<C>.sumOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: (C) -> V?,
): V = Aggregators.sum(skipNaN).aggregateOf(this, expression) as V

// endregion

// region DataRow

public fun AnyRow.rowSum(skipNaN: Boolean = skipNaN_default): Number =
    Aggregators.sum(skipNaN).aggregateOfRow(this, primitiveOrMixedNumberColumns())

@JvmName("rowSumOfShort")
public inline fun <reified T : Short?> AnyRow.rowSumOf(_kClass: KClass<Short> = Short::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

@JvmName("rowSumOfByte")
public inline fun <reified T : Byte?> AnyRow.rowSumOf(_kClass: KClass<Byte> = Byte::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

@JvmName("rowSumOfInt")
public inline fun <reified T : Int?> AnyRow.rowSumOf(_kClass: KClass<Int> = Int::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

@JvmName("rowSumOfLong")
public inline fun <reified T : Long?> AnyRow.rowSumOf(_kClass: KClass<Long> = Long::class): Long =
    rowSumOf(typeOf<T>(), false) as Long

@JvmName("rowSumOfFloat")
public inline fun <reified T : Float?> AnyRow.rowSumOf(
    skipNaN: Boolean = skipNaN_default,
    _kClass: KClass<Float> = Float::class,
): Float = rowSumOf(typeOf<T>(), skipNaN) as Float

@JvmName("rowSumOfDouble")
public inline fun <reified T : Double?> AnyRow.rowSumOf(
    skipNaN: Boolean = skipNaN_default,
    _kClass: KClass<Double> = Double::class,
): Double = rowSumOf(typeOf<T>(), skipNaN) as Double

// unfortunately, we cannot make a `reified T : Number?` due to clashes
public fun AnyRow.rowSumOf(type: KType, skipNaN: Boolean = skipNaN_default): Number {
    require(type.isPrimitiveOrMixedNumber()) {
        "Type $type is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.sum(skipNaN).aggregateOfRow(this) { colsOf(type) }
}
// endregion

// region DataFrame

public fun <T> DataFrame<T>.sum(skipNaN: Boolean = skipNaN_default): DataRow<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

public fun <T, C : Number> DataFrame<T>.sumFor(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataRow<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.sumFor(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    sumFor(skipNaN) { columns.toColumnsSetOf() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = sumFor(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> DataFrame<T>.sumFor(
    vararg columns: KProperty<C?>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = sumFor(skipNaN) { columns.toColumnSet() }

@JvmName("sumShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sum(columns: ColumnsSelector<T, Short?>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

@JvmName("sumByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sum(columns: ColumnsSelector<T, Byte?>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

@JvmName("sumNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Number> DataFrame<T>.sum(
    skipNaN: Boolean = skipNaN_default,
    noinline columns: ColumnsSelector<T, C?>,
): C = Aggregators.sum(skipNaN).aggregateAll(this, columns) as C

@JvmName("sumShort")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: ColumnReference<Short?>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: ColumnReference<Byte?>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@AccessApiOverload
public inline fun <T, reified C : Number> DataFrame<T>.sum(
    vararg columns: ColumnReference<C?>,
    skipNaN: Boolean = skipNaN_default,
): C = sum(skipNaN) { columns.toColumnSet() }

public fun <T> DataFrame<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaN_default): Number =
    sum(skipNaN) { columns.toColumnsSetOf<Number?>() }

@JvmName("sumShort")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Short?>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Byte?>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@AccessApiOverload
public inline fun <T, reified C : Number> DataFrame<T>.sum(
    skipNaN: Boolean = skipNaN_default,
    vararg columns: KProperty<C?>,
): C = sum(skipNaN) { columns.toColumnSet() }

@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sumOf(expression: RowExpression<T, Short?>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public fun <T> DataFrame<T>.sumOf(expression: RowExpression<T, Byte?>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Number> DataFrame<T>.sumOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, C?>,
): C = Aggregators.sum(skipNaN).aggregateOf(this, expression) as C

// endregion

// region GroupBy
@Refine
@Interpretable("GroupBySum1")
public fun <T> Grouped<T>.sum(skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

@Refine
@Interpretable("GroupBySum0")
public fun <T, C : Number> Grouped<T>.sumFor(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, C?>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.sumFor(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    sumFor(skipNaN) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sumFor(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sumFor(
    vararg columns: KProperty<C?>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sumFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupBySum0")
public fun <T, C : Number> Grouped<T>.sum(
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.sum(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sum(name, skipNaN) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sum(
    vararg columns: ColumnReference<C?>,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sum(name, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Grouped<T>.sum(
    vararg columns: KProperty<C?>,
    name: String? = null,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sum(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupBySumOf")
public inline fun <T, reified R : Number> Grouped<T>.sumOf(
    resultName: String? = null,
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateOf(this, resultName, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

public fun <T, R : Number> Pivot<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, R?>,
): DataRow<T> = delegate { sumFor(separate, skipNaN, columns) }

public fun <T> Pivot<T>.sumFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sumFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Number> Pivot<T>.sum(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C?>,
): DataRow<T> = delegate { sum(skipNaN, columns) }

public fun <T> Pivot<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    sum(skipNaN) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sum(
    vararg columns: ColumnReference<C?>,
    skipNaN: Boolean = skipNaN_default,
): DataRow<T> = sum(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> Pivot<T>.sum(vararg columns: KProperty<C?>, skipNaN: Boolean = skipNaN_default): DataRow<T> =
    sum(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Number> Pivot<T>.sumOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, R?>,
): DataRow<T> = delegate { sumOf(skipNaN, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

public fun <T, R : Number> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsForAggregateSelector<T, R?>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.sumFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sumFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Number> PivotGroupBy<T>.sum(
    skipNaN: Boolean = skipNaN_default,
    columns: ColumnsSelector<T, C?>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaN_default): DataFrame<T> =
    sum(skipNaN) { columns.toNumberColumns() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sum(
    vararg columns: ColumnReference<C?>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sum(skipNaN) { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C : Number> PivotGroupBy<T>.sum(
    vararg columns: KProperty<C?>,
    skipNaN: Boolean = skipNaN_default,
): DataFrame<T> = sum(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.sumOf(
    skipNaN: Boolean = skipNaN_default,
    crossinline expression: RowExpression<T, R?>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateOf(this, expression)

// endregion
