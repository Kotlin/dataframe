package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.createComputedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumn
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.UNIFIED_SIMILAR_CS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region docs

/**
 *
 *
 * ## The Map Operation
 *
 * Computes a new value for every value, row, or key–group pair of the receiver, and collects the results.
 *
 * The functions of this family share the name but differ in what they go over and what they give back:
 *
 * - [<code>map</code>][DataColumn.map] and [<code>mapIndexed</code>][DataColumn.mapIndexed] — go over the values of a [<code>DataColumn</code>][DataColumn]
 *   and return a [<code>DataColumn</code>][DataColumn] of the same size.
 * - [<code>map</code>][DataFrame.map] — goes over the rows of a [<code>DataFrame</code>][DataFrame] and returns a [<code>List</code>][List].
 * - [<code>mapToColumn</code>][DataFrame.mapToColumn] — goes over the rows of a [<code>DataFrame</code>][DataFrame] and returns a single
 *   [<code>DataColumn</code>][DataColumn] that is not part of that [<code>DataFrame</code>][DataFrame].
 * - [<code>mapToFrame</code>][DataFrame.mapToFrame] — returns a new [<code>DataFrame</code>][DataFrame] made of the columns described in an [<code>AddDsl</code>][AddDsl].
 * - [<code>map</code>][GroupBy.map] — goes over the key–group pairs of a [<code>GroupBy</code>][GroupBy] and returns a [<code>List</code>][List].
 * - [<code>mapToRows</code>][GroupBy.mapToRows] — goes over the key–group pairs of a [<code>GroupBy</code>][GroupBy] and returns a [<code>DataFrame</code>][DataFrame].
 * - [<code>mapToFrames</code>][GroupBy.mapToFrames] — goes over the key–group pairs of a [<code>GroupBy</code>][GroupBy] and returns a [<code>FrameColumn</code>][FrameColumn].
 *
 * Each result keeps the order of the values, rows, or key–group pairs it was computed from.
 *
 * See also:
 * - [<code>add</code>][DataFrame.add] — computes a new column in the same way, and returns the [<code>DataFrame</code>][DataFrame] with that column in it.
 * - [<code>expr</code>][ColumnsSelectionDsl.expr] — the same as [<code>mapToColumn</code>][DataFrame.mapToColumn],
 *   for use inside the Columns Selection DSL.
 * - [<code>convert</code>][DataFrame.convert] — computes new values for the selected columns and replaces the old ones.
 *
 * For more information: [See `map` on the documentation website.](https://kotlin.github.io/dataframe/map.html)
 */
internal interface MapDocs

// endregion

// region ColumnReference

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
internal inline fun <C, reified R> ColumnReference<C>.map(
    infer: Infer = Infer.Nulls,
    noinline transform: (C) -> R,
): ColumnReference<R> = createComputedColumnReference(name(), typeOf<R>(), infer) { transform(this@map()) }

// endregion

// region DataColumn

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] with the values that [<code>transform</code>][transform] computes from the values of this column.
 *
 *
 *
 * [transform] is called once for every value of this column, from the first one to the last one,
 * so the new column has as many values as this one, in the same order.
 * The new column has the same name as this one.
 *
 * Which kind of column you get follows the reified type argument `R`, and not the computed values:
 * a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type gives a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] type gives a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * and any other type gives a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
 * A nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type belongs to the last group, because a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] cannot hold `null`.
 *
 * [infer] only concerns a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn]: it decides whether the [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of that column
 * is the reified type argument `R` as it is, or the type of the computed values.
 * For a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], [infer] changes nothing.
 *
 * The computed values have to fit the reified type argument `R`.
 * A [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] can never have a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type, so a call that would give it one —
 * computing dataframes with [<code>Infer.Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type], or under a nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type —
 * fails with an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>mapIndexed</code>][mapIndexed] — the same, and the position of every value is given as well.
 * - the overload with an explicit `type` argument — when the type of the new column
 *   is only known at runtime.
 * - [<code>convert</code>][DataFrame.convert] — computes new values for the selected columns of a [<code>DataFrame</code>][DataFrame]
 *   and replaces the old ones, instead of returning a single new column.
 *
 * ### Example
 *
 * ```kotlin
 * // The lengths of the last names. The new column has the name of the original one,
 * // so it is usually renamed on the spot.
 * df.name.lastName.map { it.length }.rename("lastNameLength")
 * ```
 *
 * For more information: [See `map` on a `DataColumn` on the documentation website.](https://kotlin.github.io/dataframe/map.html#map-on-datacolumn)
 *
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column from a value of this column.
 * @return A new [<code>DataColumn</code>][DataColumn] with the computed values.
 */
@Interpretable("DataColumnMap")
public inline fun <T, reified R> DataColumn<T>.map(infer: Infer = Infer.Nulls, transform: (T) -> R): DataColumn<R> {
    val newValues = Array(size()) { transform(get(it)) }.asList()
    return DataColumn.createByType(name(), newValues, typeOf<R>(), infer)
}

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] of the given [<code>type</code>][type] with the values
 * that [<code>transform</code>][transform] computes from the values of this column.
 *
 *
 *
 * [transform] is called once for every value of this column, from the first one to the last one,
 * so the new column has as many values as this one, in the same order.
 * The new column has the same name as this one.
 *
 * Which kind of column you get follows the [<code>type</code>][type] argument, and not the computed values:
 * a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type gives a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] type gives a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * and any other type gives a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
 * A nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type belongs to the last group, because a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] cannot hold `null`.
 *
 * [infer] only concerns a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn]: it decides whether the [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of that column
 * is the [<code>type</code>][type] argument as it is, or the type of the computed values.
 * For a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], [infer] changes nothing.
 *
 * The computed values have to fit the [<code>type</code>][type] argument.
 * A [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] can never have a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type, so a call that would give it one —
 * computing dataframes with [<code>Infer.Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type], or under a nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type —
 * fails with an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>mapIndexed</code>][mapIndexed] — the same, and the position of every value is given as well.
 * - the overload without a `type` argument — when the type of the new column is known at compile time.
 *
 * ### Example
 *
 * ```kotlin
 * // A column of ages typed `Number` on purpose, even though every value is an `Int`
 * df.age.map(typeOf<Number>()) { it }
 * ```
 *
 * For more information: [See `map` on a `DataColumn` on the documentation website.](https://kotlin.github.io/dataframe/map.html#map-on-datacolumn)
 *
 * @param [type] The type to give to the new column.
 * The computed values are put into the column as they are, without any conversion,
 * so [type] has to fit them.
 * With [<code>Infer.Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type] it is only an upper bound for a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * whose own type is then the type of the computed values.
 * Note that [type] and the type argument `R` are independent: the result is a `DataColumn<R>` for the
 * compiler, while its [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] at runtime is [type].
 * Keep the two in agreement unless that difference is exactly what you are after.
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column from a value of this column.
 * @return A new [<code>DataColumn</code>][DataColumn] with the computed values.
 */
@Interpretable("DataColumnMapKType")
public inline fun <T, R> DataColumn<T>.map(
    type: KType,
    infer: Infer = Infer.Nulls,
    transform: (T) -> R,
): DataColumn<R> {
    val values = Array<Any?>(size()) { transform(get(it)) }.asList()
    return DataColumn.createByType(name(), values, type, infer).cast()
}

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] with the values that [<code>transform</code>][transform] computes
 * from the values of this column and their positions.
 *
 *
 *
 * [transform] is called once for every value of this column, from the first one to the last one,
 * so the new column has as many values as this one, in the same order.
 * The new column has the same name as this one.
 *
 * Which kind of column you get follows the reified type argument `R`, and not the computed values:
 * a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type gives a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] type gives a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * and any other type gives a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
 * A nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type belongs to the last group, because a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] cannot hold `null`.
 *
 * [infer] only concerns a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn]: it decides whether the [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of that column
 * is the reified type argument `R` as it is, or the type of the computed values.
 * For a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], [infer] changes nothing.
 *
 * The computed values have to fit the reified type argument `R`.
 * A [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] can never have a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type, so a call that would give it one —
 * computing dataframes with [<code>Infer.Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type], or under a nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type —
 * fails with an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * The position of the first value is `0`.
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>map</code>][DataColumn.map] — the same, without the positions.
 * - the overload with an explicit `type` argument — when the type of the new column
 *   is only known at runtime.
 *
 * ### Example
 *
 * ```kotlin
 * // The first names, numbered: "1. Alice", "2. Bob", ...
 * df.name.firstName.mapIndexed { i, firstName -> "${i + 1}. $firstName" }
 * ```
 *
 * For more information: [See `map` on a `DataColumn` on the documentation website.](https://kotlin.github.io/dataframe/map.html#map-on-datacolumn)
 *
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column
 * from the position of a value of this column and that value.
 * @return A new [<code>DataColumn</code>][DataColumn] with the computed values.
 */
@Interpretable("DataColumnMapIndexed")
public inline fun <T, reified R> DataColumn<T>.mapIndexed(
    infer: Infer = Infer.Nulls,
    transform: (Int, T) -> R,
): DataColumn<R> {
    val newValues = Array(size()) { transform(it, get(it)) }.asList()
    return DataColumn.createByType(name(), newValues, typeOf<R>(), infer)
}

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] of the given [<code>type</code>][type] with the values that [<code>transform</code>][transform] computes
 * from the values of this column and their positions.
 *
 *
 *
 * [transform] is called once for every value of this column, from the first one to the last one,
 * so the new column has as many values as this one, in the same order.
 * The new column has the same name as this one.
 *
 * Which kind of column you get follows the [<code>type</code>][type] argument, and not the computed values:
 * a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type gives a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] type gives a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * and any other type gives a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
 * A nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type belongs to the last group, because a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] cannot hold `null`.
 *
 * [infer] only concerns a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn]: it decides whether the [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of that column
 * is the [<code>type</code>][type] argument as it is, or the type of the computed values.
 * For a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], [infer] changes nothing.
 *
 * The computed values have to fit the [<code>type</code>][type] argument.
 * A [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] can never have a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type, so a call that would give it one —
 * computing dataframes with [<code>Infer.Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type], or under a nullable [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] type —
 * fails with an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * The position of the first value is `0`.
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>map</code>][DataColumn.map] — the same, without the positions.
 * - the overload without a `type` argument — when the type of the new column is known at compile time.
 *
 * ### Example
 *
 * ```kotlin
 * // The positions of the rows, in a column typed `Number` on purpose
 * df.age.mapIndexed(typeOf<Number>()) { i, _ -> i }
 * ```
 *
 * For more information: [See `map` on a `DataColumn` on the documentation website.](https://kotlin.github.io/dataframe/map.html#map-on-datacolumn)
 *
 * @param [type] The type to give to the new column.
 * The computed values are put into the column as they are, without any conversion,
 * so [type] has to fit them.
 * With [<code>Infer.Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type] it is only an upper bound for a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
 * whose own type is then the type of the computed values.
 * Note that [type] and the type argument `R` are independent: the result is a `DataColumn<R>` for the
 * compiler, while its [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] at runtime is [type].
 * Keep the two in agreement unless that difference is exactly what you are after.
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column
 * from the position of a value of this column and that value.
 * @return A new [<code>DataColumn</code>][DataColumn] with the computed values.
 */
@Interpretable("DataColumnMapIndexedKType")
public inline fun <T, R> DataColumn<T>.mapIndexed(
    type: KType,
    infer: Infer = Infer.Nulls,
    transform: (Int, T) -> R,
): DataColumn<R> {
    val values = Array<Any?>(size()) { transform(it, get(it)) }.asList()
    return DataColumn.createByType(name(), values, type, infer).cast()
}

// endregion

// region DataFrame

/**
 * Returns a [<code>List</code>][List] with the values that [<code>transform</code>][transform] computes from the rows of this [<code>DataFrame</code>][DataFrame].
 *
 * [<code>transform</code>][transform] is called once for every row, from the first one to the last one,
 * so the list has one element per row, in row order.
 *
 * [<code>transform</code>][transform] gets the row both as its receiver and as its argument,
 * so inside it `age` and `it.age` mean the same thing.
 * For more information: [See Row Expressions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-expressions)
 *
 * The result is an ordinary [<code>List</code>][List], and not a [<code>DataFrame</code>][DataFrame] or a [<code>DataColumn</code>][DataColumn].
 *
 * A [<code>ColumnGroup</code>][ColumnGroup] is also a [<code>DataFrame</code>][DataFrame], so `map` on a column group is this function,
 * and its result is a [<code>List</code>][List] with one element per row of the group.
 * To get a [<code>DataColumn</code>][DataColumn] of the same size instead — a column of the [<code>DataRow</code>][DataRow]s of the group —
 * call [<code>asDataColumn</code>][ColumnGroup.asDataColumn] first, and then [<code>map</code>][DataColumn.map].
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>mapToColumn</code>][mapToColumn] — the same, but the results are collected into a [<code>DataColumn</code>][DataColumn].
 * - [<code>mapToFrame</code>][mapToFrame] — a new [<code>DataFrame</code>][DataFrame] of several computed columns at once.
 * - [<code>rows</code>][DataFrame.rows] — the rows themselves, when nothing has to be computed.
 *
 * ### Example
 *
 * ```kotlin
 * // The list of birth years, one per row
 * df.map { 2021 - age }
 * ```
 *
 * For more information: [See `map` over rows on the documentation website.](https://kotlin.github.io/dataframe/map.html#map)
 *
 * @param [transform] A [<code>RowExpression</code>][RowExpression] that computes an element of the list from a row of this [<code>DataFrame</code>][DataFrame].
 * @return A [<code>List</code>][List] with one computed element per row of this [<code>DataFrame</code>][DataFrame].
 */
public inline fun <T, R> DataFrame<T>.map(transform: RowExpression<T, R>): List<R> = rows().map { transform(it, it) }

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] with the given [<code>name</code>][name] and the values
 * that [<code>body</code>][body] computes from the rows of this [<code>DataFrame</code>][DataFrame].
 *
 * [<code>body</code>][body] is called once for every row, from the first one to the last one,
 * so the new column has one value per row, in row order.
 * The new column is standalone: this [<code>DataFrame</code>][DataFrame] is not changed and does not contain it.
 *
 * With an [<code>AddExpression</code>][org.jetbrains.kotlinx.dataframe.api.AddExpression], you define the value that each row in the new column should have.
 * This can be based on values from the same row in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * You can also use functions like [<code>prev</code>][org.jetbrains.kotlinx.dataframe.api.prev] and [<code>next</code>][org.jetbrains.kotlinx.dataframe.api.next] to access other rows, and combine them with
 * [<code>newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue] to reference values already computed in the new column.
 * For example, use `prev().newValue()` to access the new column value from the previous row.
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>add</code>][DataFrame.add] — the same, but the result is this [<code>DataFrame</code>][DataFrame] with the new column in it.
 * - [<code>map</code>][DataFrame.map] — the same, but the results are collected into a [<code>List</code>][List].
 * - [<code>expr</code>][ColumnsSelectionDsl.expr] — the same inside the Columns Selection DSL.
 *
 * ### Example
 *
 * ```kotlin
 * // A standalone column of birth years; `df` itself is left as it was
 * val yearOfBirth = df.mapToColumn("year of birth") { 2021 - age }
 * ```
 *
 * For more information: [See `mapToColumn` on the documentation website.](https://kotlin.github.io/dataframe/map.html#maptocolumn)
 *
 * @param [name] The name to give to the new column.
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [body] An [<code>AddExpression</code>][AddExpression] that computes a value of the new column from a row of this [<code>DataFrame</code>][DataFrame].
 * @return A new [<code>DataColumn</code>][DataColumn] with the computed values.
 */
public inline fun <T, reified R> DataFrame<T>.mapToColumn(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, typeOf<R>(), infer, body)

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] with the given [<code>name</code>][name] and the values
 * that [<code>body</code>][body] computes from the rows of this [<code>ColumnsContainer</code>][ColumnsContainer].
 *
 * Use [<code>expr</code>][ColumnsSelectionDsl.expr] instead — it does the same and works in every Columns Selection DSL.
 */
@Deprecated(
    UNIFIED_SIMILAR_CS_API,
    replaceWith = ReplaceWith("expr(name, infer, body)", "org.jetbrains.kotlinx.dataframe.api.Infer"),
)
public inline fun <T, reified R> ColumnsContainer<T>.mapToColumn(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, typeOf<R>(), infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified R> ColumnsContainer<T>.mapToColumn(
    column: ColumnReference<R>,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column, typeOf<R>(), infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified R> ColumnsContainer<T>.mapToColumn(
    column: KProperty<R>,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column, typeOf<R>(), infer, body)

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.mapToColumn(
    name: String,
    type: KType,
    infer: Infer = Infer.Nulls,
    body: AddExpression<T, R>,
): DataColumn<R> = newColumn(type, name, infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.mapToColumn(
    column: ColumnReference<R>,
    type: KType,
    infer: Infer = Infer.Nulls,
    body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column.name(), type, infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.mapToColumn(
    column: KProperty<R>,
    type: KType,
    infer: Infer = Infer.Nulls,
    body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column.columnName, type, infer, body)

/**
 * Returns a new [<code>DataFrame</code>][DataFrame] made of the columns described in [<code>body</code>][body].
 *
 * [<code>body</code>][body] describes the new columns with the [<code>AddDsl</code>][AddDsl] operations —
 * `from`, `into`, `expr`, `group`,
 * and the unary `+` that takes a column of this [<code>DataFrame</code>][DataFrame] as it is.
 * The result holds only those columns, in the order in which they are described, and nothing else.
 * This is what makes it different from [<code>add</code>][DataFrame.add],
 * where the columns of this [<code>DataFrame</code>][DataFrame] are also part of the result.
 *
 * Every column computed from a row expression has one value per row of this [<code>DataFrame</code>][DataFrame], in row order.
 * An empty [<code>body</code>][body] describes no columns, so it gives a [<code>DataFrame</code>][DataFrame] with no columns and no rows.
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>add</code>][DataFrame.add] — the same [<code>AddDsl</code>][AddDsl], but the columns of this [<code>DataFrame</code>][DataFrame] are kept as well.
 * - [<code>mapToColumn</code>][mapToColumn] — a single new column instead of a whole [<code>DataFrame</code>][DataFrame].
 * - [<code>select</code>][DataFrame.select] — a [<code>DataFrame</code>][DataFrame] of chosen columns, when nothing has to be computed.
 *
 * ### Example
 *
 * ```kotlin
 * // A frame of four new columns and a copy of "city";
 * // "name" and "age" of `df` are not in the result
 * df.mapToFrame {
 *     "year of birth" from { 2021 - age }
 *     expr { age > 18 } into "is adult"
 *     name.lastName.map { it.length } into "last name length"
 *     "full name" from { name.firstName + " " + name.lastName }
 *     +city
 * }
 * ```
 *
 * For more information: [See `mapToFrame` on the documentation website.](https://kotlin.github.io/dataframe/map.html#maptoframe)
 *
 * @param [body] An [<code>AddDsl</code>][AddDsl] expression that describes the columns of the new [<code>DataFrame</code>][DataFrame].
 * @return A new [<code>DataFrame</code>][DataFrame] with the described columns.
 */
@Refine
@Interpretable("MapToFrame")
public inline fun <T> DataFrame<T>.mapToFrame(body: AddDsl<T>.() -> Unit): DataFrame<*> {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(dsl.columns)
}

// endregion

// region GroupBy

/**
 * Returns a [<code>List</code>][List] with the values that [<code>body</code>][body] computes from the key–group pairs of this [<code>GroupBy</code>][GroupBy].
 *
 *
 *
 * [body] is called once for every key–group pair of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * in the order in which the pairs appear in it.
 * Each pair is given to [body] as a [<code>GroupWithKey</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey], both as the receiver and as the argument,
 * so the key values are available as [<code>key</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey.key] and the rows of the group
 * as [<code>group</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey.group].
 *
 * A pair for which [<code>body</code>][body] returns `null` gives no element,
 * so the list can be shorter than the number of key–group pairs.
 * Apart from that, the list has one element per pair, in the same order.
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>mapToRows</code>][GroupBy.mapToRows] — the same, but every result is a row, collected into a [<code>DataFrame</code>][DataFrame].
 * - [<code>mapToFrames</code>][GroupBy.mapToFrames] — the same, but every result is a [<code>DataFrame</code>][DataFrame],
 *   collected into a [<code>FrameColumn</code>][FrameColumn].
 * - [<code>aggregate</code>][Grouped.aggregate] — computes named values per group and returns them as a [<code>DataFrame</code>][DataFrame]
 *   next to the key columns.
 *
 * ### Example
 *
 * ```kotlin
 * // The number of people per city, as a list, in the order of the groups: [1, 1, 2, 1, 1, 1]
 * df.groupBy { city }.map { group.rowsCount() }
 * ```
 *
 * For more information: [See `map` on a `GroupBy` on the documentation website.](https://kotlin.github.io/dataframe/map.html#map-on-groupby)
 *
 * @param [body] A [<code>Selector</code>][Selector] that computes an element of the list from a key–group pair.
 * @return A [<code>List</code>][List] with one computed element per key–group pair, without the `null` results.
 */
public inline fun <T, G, R> GroupBy<T, G>.map(body: Selector<GroupWithKey<T, G>, R>): List<R> =
    keys.rows().mapIndexedNotNull { index, row ->
        val group = groups[index]
        val g = GroupWithKey(row, group)
        body(g, g)
    }

/**
 * Returns a [<code>DataFrame</code>][DataFrame] with the rows that [<code>body</code>][body] computes from the key–group pairs of this [<code>GroupBy</code>][GroupBy].
 *
 *
 *
 * [body] is called once for every key–group pair of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * in the order in which the pairs appear in it.
 * Each pair is given to [body] as a [<code>GroupWithKey</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey], both as the receiver and as the argument,
 * so the key values are available as [<code>key</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey.key] and the rows of the group
 * as [<code>group</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey.group].
 *
 * A pair for which [<code>body</code>][body] returns `null` gives no row,
 * so the result can have fewer rows than the number of key–group pairs.
 * Apart from that, the result has one row per pair, in the same order.
 *
 * The columns of the result are the columns of the returned rows.
 * If [<code>body</code>][body] returns `null` for every pair, the result has no rows and no columns at all,
 * even though its type still says `DataFrame<G>` — so a result with no rows can carry no schema.
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>map</code>][GroupBy.map] — the same, but every result is kept, of any type, in a [<code>List</code>][List].
 * - [<code>concat</code>][GroupBy.concat] — all rows of all groups, without computing anything.
 *
 * ### Example
 *
 * ```kotlin
 * // The oldest person of every city, one row per city
 * df.groupBy { city }.mapToRows { group.sortByDesc { age }.firstOrNull() }
 * ```
 *
 * For more information: [See `map` on a `GroupBy` on the documentation website.](https://kotlin.github.io/dataframe/map.html#map-on-groupby)
 *
 * @param [body] A [<code>Selector</code>][Selector] that computes a row of the result from a key–group pair, or `null` for no row.
 * @return A [<code>DataFrame</code>][DataFrame] with one computed row per key–group pair, without the `null` results.
 */
public fun <T, G> GroupBy<T, G>.mapToRows(body: Selector<GroupWithKey<T, G>, DataRow<G>?>): DataFrame<G> =
    map(body).concat()

/**
 * Returns a [<code>FrameColumn</code>][FrameColumn] with the dataframes that [<code>body</code>][body] computes from the key–group pairs of this [<code>GroupBy</code>][GroupBy].
 *
 *
 *
 * [body] is called once for every key–group pair of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * in the order in which the pairs appear in it.
 * Each pair is given to [body] as a [<code>GroupWithKey</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey], both as the receiver and as the argument,
 * so the key values are available as [<code>key</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey.key] and the rows of the group
 * as [<code>group</code>][org.jetbrains.kotlinx.dataframe.api.GroupWithKey.group].
 *
 * The new column has one [<code>DataFrame</code>][DataFrame] per key–group pair, in the same order,
 * and the same name as [<code>groups</code>][GroupBy.groups].
 *
 * Call `concat()` on the result to get all of those dataframes back as one [<code>DataFrame</code>][DataFrame].
 *
 * See also:
 * - [<code>The Map Operation</code>][org.jetbrains.kotlinx.dataframe.api.MapDocs] — an overview of the whole `map` family.
 * - [<code>mapToRows</code>][GroupBy.mapToRows] — the same, but every result is a single row instead of a whole [<code>DataFrame</code>][DataFrame].
 * - [<code>updateGroups</code>][GroupBy.updateGroups] — the same computation over the groups,
 *   but the result is a [<code>GroupBy</code>][GroupBy] with its key columns, and not a bare [<code>FrameColumn</code>][FrameColumn].
 *
 * ### Example
 *
 * ```kotlin
 * // The two oldest people with each first name, as a frame column:
 * // only the group of "Charlie" has a third person to leave out
 * df.groupBy { name.firstName }.mapToFrames { group.sortByDesc { age }.take(2) }
 * ```
 *
 * For more information: [See `map` on a `GroupBy` on the documentation website.](https://kotlin.github.io/dataframe/map.html#map-on-groupby)
 *
 * @param [body] A [<code>Selector</code>][Selector] that computes a [<code>DataFrame</code>][DataFrame] from a key–group pair.
 * @return A [<code>FrameColumn</code>][FrameColumn] with one computed [<code>DataFrame</code>][DataFrame] per key–group pair.
 */
public fun <T, G> GroupBy<T, G>.mapToFrames(body: Selector<GroupWithKey<T, G>, DataFrame<G>>): FrameColumn<G> =
    DataColumn.createFrameColumn(groups.name, map(body))

// endregion
