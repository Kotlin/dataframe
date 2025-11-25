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
import org.jetbrains.kotlinx.dataframe.impl.aggregation.primitiveOrMixedNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toNumberColumns
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.SUM_NO_SKIPNAN
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
 * For mixed primitive number types, the aggregator unifies the numbers before calculating the sum.
 */

// region DataColumn

@JvmName("sumShort")
public fun DataColumn<Short?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

@JvmName("sumByte")
public fun DataColumn<Byte?>.sum(): Int = Aggregators.sum(false).aggregateSingleColumn(this) as Int

@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
public fun <T : Number?> DataColumn<T>.sum(skipNaN: Boolean = skipNaNDefault): T & Any =
    Aggregators.sum(skipNaN).aggregateSingleColumn(this) as (T & Any)

@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Short?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Byte?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@Suppress("UNCHECKED_CAST")
@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <C, reified V : Number?> DataColumn<C>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: (C) -> V,
): V & Any = Aggregators.sum(skipNaN).aggregateOf(this, expression) as (V & Any)

// endregion

// region DataRow

public fun AnyRow.rowSum(skipNaN: Boolean = skipNaNDefault): Number =
    Aggregators.sum(skipNaN).aggregateOfRow(this, primitiveOrMixedNumberColumns())

@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfShort")
public inline fun <reified T : Short> AnyRow.rowSumOf(_kClass: KClass<Short> = Short::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfByte")
public inline fun <reified T : Byte> AnyRow.rowSumOf(_kClass: KClass<Byte> = Byte::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfInt")
public inline fun <reified T : Int> AnyRow.rowSumOf(_kClass: KClass<Int> = Int::class): Int =
    rowSumOf(typeOf<T>(), false) as Int

@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfLong")
public inline fun <reified T : Long> AnyRow.rowSumOf(_kClass: KClass<Long> = Long::class): Long =
    rowSumOf(typeOf<T>(), false) as Long

@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfFloat")
public inline fun <reified T : Float> AnyRow.rowSumOf(
    skipNaN: Boolean = skipNaNDefault,
    _kClass: KClass<Float> = Float::class,
): Float = rowSumOf(typeOf<T>(), skipNaN) as Float

@Suppress("FINAL_UPPER_BOUND")
@JvmName("rowSumOfDouble")
public inline fun <reified T : Double> AnyRow.rowSumOf(
    skipNaN: Boolean = skipNaNDefault,
    _kClass: KClass<Double> = Double::class,
): Double = rowSumOf(typeOf<T>(), skipNaN) as Double

// unfortunately, we cannot make a `reified T : Number?` due to clashes
public fun AnyRow.rowSumOf(type: KType, skipNaN: Boolean = skipNaNDefault): Number {
    require(type.isPrimitiveOrMixedNumber()) {
        "Type $type is not a primitive number type. Mean only supports primitive number types."
    }
    return Aggregators.sum(skipNaN).aggregateOfRow(this) {
        colsOf(type.withNullability(true))
    }
}
// endregion

// region DataFrame
@Refine
@Interpretable("Sum0")
public fun <T> DataFrame<T>.sum(skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

@Refine
@Interpretable("Sum1")
public fun <T, C : Number?> DataFrame<T>.sumFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataRow<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

public fun <T> DataFrame<T>.sumFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(skipNaN) { columns.toColumnsSetOf() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sumFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sumFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(skipNaN) { columns.toColumnSet() }

@JvmName("sumShort")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Short?> DataFrame<T>.sum(columns: ColumnsSelector<T, C>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

@JvmName("sumByte")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Byte?> DataFrame<T>.sum(columns: ColumnsSelector<T, C>): Int =
    Aggregators.sum(false).aggregateAll(this, columns) as Int

@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
@OverloadResolutionByLambdaReturnType
public fun <T, C : Number?> DataFrame<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): C & Any = Aggregators.sum(skipNaN).aggregateAll(this, columns) as (C & Any)

@JvmName("sumShort")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Short?> DataFrame<T>.sum(vararg columns: ColumnReference<C>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Byte?> DataFrame<T>.sum(vararg columns: ColumnReference<C>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sum(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): C & Any = sum(skipNaN) { columns.toColumnSet() }

public fun <T> DataFrame<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaNDefault): Number =
    sum(skipNaN) { columns.toColumnsSetOf<Number?>() }

@JvmName("sumShort")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Short?>): Int = sum { columns.toColumnSet() }

@JvmName("sumByte")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sum(vararg columns: KProperty<Byte?>): Int = sum { columns.toColumnSet() }

@JvmName("sumNumber")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    vararg columns: KProperty<C>,
): C & Any = sum(skipNaN) { columns.toColumnSet() }

@JvmName("sumOfShort")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Short?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@JvmName("sumOfByte")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Byte?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): Int =
    Aggregators.sum(false).aggregateOf(this, expression) as Int

@Suppress("UNCHECKED_CAST")
@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, C>,
): C & Any = Aggregators.sum(skipNaN).aggregateOf(this, expression) as (C & Any)

// endregion

// region GroupBy
@Refine
@Interpretable("GroupBySum1")
public fun <T> Grouped<T>.sum(skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(skipNaN, primitiveOrMixedNumberColumns())

@Refine
@Interpretable("GroupBySum0")
public fun <T, C : Number?> Grouped<T>.sumFor(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, columns)

public fun <T> Grouped<T>.sumFor(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sumFor(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sumFor(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupBySum2")
public fun <T, C : Number?> Grouped<T>.sum(
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, name, columns)

public fun <T> Grouped<T>.sum(
    vararg columns: String,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(name, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sum(
    vararg columns: ColumnReference<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(name, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Grouped<T>.sum(
    vararg columns: KProperty<C>,
    name: String? = null,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(name, skipNaN) { columns.toColumnSet() }

@Refine
@Interpretable("GroupBySumOf")
public inline fun <T, reified R : Number?> Grouped<T>.sumOf(
    resultName: String? = null,
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateOf(this, resultName, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

public fun <T, R : Number?> Pivot<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = delegate { sumFor(separate, skipNaN, columns) }

public fun <T> Pivot<T>.sumFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sumFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Number?> Pivot<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataRow<T> = delegate { sum(skipNaN, columns) }

public fun <T> Pivot<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sum(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sum(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataRow<T> = sum(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> Pivot<T>.sum(vararg columns: KProperty<C>, skipNaN: Boolean = skipNaNDefault): DataRow<T> =
    sum(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Number?> Pivot<T>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataRow<T> = delegate { sumOf(skipNaN, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sumFor(separate, skipNaN, primitiveOrMixedNumberColumns())

public fun <T, R : Number?> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateFor(this, separate, columns)

public fun <T> PivotGroupBy<T>.sumFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sumFor(separate, skipNaN) { columns.toColumnSet() }

public fun <T, C : Number?> PivotGroupBy<T>.sum(
    skipNaN: Boolean = skipNaNDefault,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateAll(this, columns)

public fun <T> PivotGroupBy<T>.sum(vararg columns: String, skipNaN: Boolean = skipNaNDefault): DataFrame<T> =
    sum(skipNaN) { columns.toNumberColumns() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sum(
    vararg columns: ColumnReference<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(skipNaN) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> PivotGroupBy<T>.sum(
    vararg columns: KProperty<C>,
    skipNaN: Boolean = skipNaNDefault,
): DataFrame<T> = sum(skipNaN) { columns.toColumnSet() }

public inline fun <T, reified R : Number?> PivotGroupBy<T>.sumOf(
    skipNaN: Boolean = skipNaNDefault,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = Aggregators.sum(skipNaN).aggregateOf(this, expression)

// endregion

// region binary compatibility

@Suppress("UNCHECKED_CAST")
@JvmName("sumNumber")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T : Number?> DataColumn<T>.sum(): T = sum(skipNaN = skipNaNDefault)

@JvmName("sumOfNumber")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <C, reified V : Number?> DataColumn<C>.sumOf(crossinline expression: (C) -> V): V & Any =
    sumOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun AnyRow.rowSum(): Number = rowSum(skipNaN = skipNaNDefault)

@JvmName("rowSumOfFloat")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Float?> AnyRow.rowSumOf(_kClass: KClass<Float> = Float::class): Float =
    rowSumOf(typeOf<T>(), skipNaN = skipNaNDefault) as Float

@JvmName("rowSumOfDouble")
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <reified T : Double?> AnyRow.rowSumOf(_kClass: KClass<Double> = Double::class): Double =
    rowSumOf(typeOf<T>(), skipNaN = skipNaNDefault) as Double

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun AnyRow.rowSumOf(type: KType): Number = rowSumOf(type, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.sum(): DataRow<T> = sum(skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.sumFor(columns: ColumnsForAggregateSelector<T, C>): DataRow<T> =
    sumFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.sumFor(vararg columns: String): DataRow<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.sumFor(vararg columns: ColumnReference<C>): DataRow<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> DataFrame<T>.sumFor(vararg columns: KProperty<C>): DataRow<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@JvmName("sumNumber")
@OverloadResolutionByLambdaReturnType
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sum(noinline columns: ColumnsSelector<T, C>): C =
    sum(skipNaN = skipNaNDefault, columns = columns)

@JvmName("sumNumber")
@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sum(vararg columns: ColumnReference<C>): C & Any =
    sum(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.sum(vararg columns: String): Number = sum(columns = columns, skipNaN = skipNaNDefault)

@JvmName("sumNumber")
@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sum(vararg columns: KProperty<C>): C & Any =
    sum(skipNaN = skipNaNDefault, columns = columns)

@JvmName("sumOfNumber")
@OverloadResolutionByLambdaReturnType
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified C : Number?> DataFrame<T>.sumOf(crossinline expression: RowExpression<T, C>): C & Any =
    sumOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.sum(): DataFrame<T> = sum(skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sumFor(columns: ColumnsForAggregateSelector<T, C>): DataFrame<T> =
    sumFor(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.sumFor(vararg columns: String): DataFrame<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sumFor(vararg columns: ColumnReference<C>): DataFrame<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sumFor(vararg columns: KProperty<C>): DataFrame<T> =
    sumFor(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sum(name: String? = null, columns: ColumnsSelector<T, C>): DataFrame<T> =
    sum(name, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Grouped<T>.sum(vararg columns: String, name: String? = null): DataFrame<T> =
    sum(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sum(vararg columns: ColumnReference<C>, name: String? = null): DataFrame<T> =
    sum(columns = columns, name = name, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Grouped<T>.sum(vararg columns: KProperty<C>, name: String? = null): DataFrame<T> =
    sum(columns = columns, name = name, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Grouped<T>.sumOf(
    resultName: String? = null,
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = sumOf(resultName, skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.sum(separate: Boolean = false): DataRow<T> = sum(separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> Pivot<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataRow<T> = sumFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.sumFor(vararg columns: String, separate: Boolean = false): DataRow<T> =
    sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataRow<T> = sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sumFor(vararg columns: KProperty<C>, separate: Boolean = false): DataRow<T> =
    sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sum(columns: ColumnsSelector<T, C>): DataRow<T> =
    sum(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> Pivot<T>.sum(vararg columns: String): DataRow<T> = sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sum(vararg columns: ColumnReference<C>): DataRow<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> Pivot<T>.sum(vararg columns: KProperty<C>): DataRow<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> Pivot<T>.sumOf(crossinline expression: RowExpression<T, R>): DataRow<T> =
    sumOf(skipNaN = skipNaNDefault, expression = expression)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.sum(separate: Boolean = false): DataFrame<T> = sum(separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, R : Number?> PivotGroupBy<T>.sumFor(
    separate: Boolean = false,
    columns: ColumnsForAggregateSelector<T, R>,
): DataFrame<T> = sumFor(separate, skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.sumFor(vararg columns: String, separate: Boolean = false): DataFrame<T> =
    sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: ColumnReference<C>,
    separate: Boolean = false,
): DataFrame<T> = sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sumFor(
    vararg columns: KProperty<C>,
    separate: Boolean = false,
): DataFrame<T> = sumFor(columns = columns, separate = separate, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sum(columns: ColumnsSelector<T, C>): DataFrame<T> =
    sum(skipNaN = skipNaNDefault, columns = columns)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T> PivotGroupBy<T>.sum(vararg columns: String): DataFrame<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sum(vararg columns: ColumnReference<C>): DataFrame<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@AccessApiOverload
@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public fun <T, C : Number?> PivotGroupBy<T>.sum(vararg columns: KProperty<C>): DataFrame<T> =
    sum(columns = columns, skipNaN = skipNaNDefault)

@Deprecated(SUM_NO_SKIPNAN, level = DeprecationLevel.HIDDEN)
public inline fun <T, reified R : Number?> PivotGroupBy<T>.sumOf(
    crossinline expression: RowExpression<T, R>,
): DataFrame<T> = sumOf(skipNaN = skipNaNDefault, expression = expression)

// endregion
