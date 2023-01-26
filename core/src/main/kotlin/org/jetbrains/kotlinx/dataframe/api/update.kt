package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.impl.api.updateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWithValuePerColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.index
import kotlin.reflect.KProperty

/**
 * Returns [DataFrame] with changed values in some cells.
 *
 * Column types can not be changed.
 *
 * Usage:
 *
 * [update] { columns }
 *
 *   [.[where] { [rowCondition][UpdateOperation.Where.Predicate] } ]
 *
 *   [.[at] ([rowIndices][UpdateOperation.At.RowIndices]) ]
 *
 *   .[with][Update.with] { [rowExpression][UpdateOperation.With.Expression] } | .[notNull] { rowExpression } | .[perCol] { colExpression } | .[perRowCol] { rowColExpression } | .[withValue] (value) | .[withNull] () | .[withZero] () | .[asFrame] { frameExpression }
 *
 * @comment TODO
 * rowExpression: DataRow.(OldValue) -> NewValue
 * colExpression: DataColumn.(DataColumn) -> NewValue
 * rowColExpression: DataRow.(DataColumn) -> NewValue
 * frameExpression: DataFrame.(DataFrame) -> DataFrame
 *
 */
internal interface UpdateOperation {

    /** @param columns The [ColumnsSelector] used to select the columns of this [DataFrame] to update. */
    interface ColumnsSelectorParam

    /** @param columns An [Iterable] of [ColumnReference]s of this [DataFrame] to update. */
    interface ColumnReferenceIterableParam

    /** @param columns The [ColumnReference]s of this [DataFrame] to update. */
    interface ColumnReferencesParam

    /** @param columns The [KProperty] values corresponding to columns of this [DataFrame] to update. */
    interface KPropertyColumnsParam

    /** @param columns The column names belonging to this [DataFrame] to update. */
    interface StringColumnsParam

    /**
     * Only update the columns that pass a certain [predicate][UpdateOperation.Where.Predicate].
     *
     * For example:
     * ```kotlin
     * df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
     * ```
     */
    interface Where {

        /** The condition for rows to be included. A filter if you will.
         *
         * Can be seen as [DataRow].(oldValue: [C]) -> [Boolean] */
        interface Predicate
    }

    /**
     * Only update the columns at certain given [row indices][UpdateOperation.At.RowIndices]:
     *
     * Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
     *
     * For example:
     * ```kotlin
     * df.update { city }.at(5..10).withValue("Paris")
     * ```
     */
    interface At {

        /** The indices of the rows to update. */
        interface RowIndices
    }

    /**
     * Update the selected columns using the given [expression][With.Expression].
     *
     * For example:
     * ```kotlin
     * df.update { city }.with { name.firstName + " from " + it }
     * ```
     */
    interface With {

        /** The expression to update the selected columns with.
         *
         * Can be seen as [DataRow].(oldValue: [C]) -> newValue: [C]?
         * */
        interface Expression
    }
}

/**
 * @include [UpdateOperation]
 * @include [AccessApi.AnyApiLink]
 * @include [UpdateOperation.ColumnsSelectorParam]
 */
public fun <T, C> DataFrame<T>.update(columns: ColumnsSelector<T, C>): Update<T, C> =
    Update(this, null, columns)

/**
 * @include [UpdateOperation]
 * API:
 *  - {@include [AccessApi.StringApiLink]}
 *
 * @include [UpdateOperation.StringColumnsParam]
 */
public fun <T> DataFrame<T>.update(vararg columns: String): Update<T, Any?> = update { columns.toColumns() }

/**
 * @include [UpdateOperation]
 * API:
 *  - {@include [AccessApi.KPropertiesApiLink]}
 *
 * @include [UpdateOperation.KPropertyColumnsParam]
 */
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): Update<T, C> = update { columns.toColumns() }

/**
 * @include [UpdateOperation]
 * API:
 *  - {@include [AccessApi.ColumnAccessorsApiLink]}
 *
 * @include [UpdateOperation.ColumnReferencesParam]
 */
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): Update<T, C> =
    update { columns.toColumns() }

/**
 * @include [UpdateOperation]
 * API:
 *  - {@include [AccessApi.ColumnAccessorsApiLink]}
 *
 * @include [UpdateOperation.ColumnReferenceIterableParam]
 */
public fun <T, C> DataFrame<T>.update(columns: Iterable<ColumnReference<C>>): Update<T, C> =
    update { columns.toColumnSet() }

public data class Update<T, C>(
    val df: DataFrame<T>,
    val filter: RowValueFilter<T, C>?,
    val columns: ColumnsSelector<T, C>
) {
    public fun <R : C> cast(): Update<T, R> =
        Update(df, filter as RowValueFilter<T, R>?, columns as ColumnsSelector<T, R>)
}

/**
 * @include [UpdateOperation.Where]
 *
 * @param predicate {@include [UpdateOperation.Where.Predicate]}
 */
public fun <T, C> Update<T, C>.where(predicate: RowValueFilter<T, C>): Update<T, C> =
    copy(filter = filter and predicate)

/**
 * @include [UpdateOperation.At]
 *
 * @param rowIndices {@include [UpdateOperation.At.RowIndices]}
 */
public fun <T, C> Update<T, C>.at(rowIndices: Collection<Int>): Update<T, C> = where { index in rowIndices }

/**
 * @include [UpdateOperation.At]
 *
 * @param rowIndices {@include [UpdateOperation.At.RowIndices]}
 */
public fun <T, C> Update<T, C>.at(vararg rowIndices: Int): Update<T, C> = at(rowIndices.toSet())

/**
 * @include [UpdateOperation.At]
 *
 * @param rowRange {@include [UpdateOperation.At.RowIndices]}
 */
public fun <T, C> Update<T, C>.at(rowRange: IntRange): Update<T, C> = where { index in rowRange }

public infix fun <T, C> Update<T, C>.perRowCol(expression: RowColumnExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, column, _ -> expression(row, column) }

public typealias UpdateExpression<T, C, R> = AddDataRow<T>.(C) -> R

/**
 * @include [UpdateOperation.With]
 *
 * @param expression {@include [UpdateOperation.With.Expression]}
 */
public infix fun <T, C> Update<T, C>.with(expression: UpdateExpression<T, C, C?>): DataFrame<T> =
    updateImpl { row, _, value ->
        expression(row, value)
    }

public infix fun <T, C, R> Update<T, DataRow<C>>.asFrame(expression: DataFrameExpression<C, DataFrame<R>>): DataFrame<T> =
    df.replace(columns).with { it.asColumnGroup().let { expression(it, it) }.asColumnGroup(it.name()) }

public fun <T, C> Update<T, C>.asNullable(): Update<T, C?> = this as Update<T, C?>

public fun <T, C> Update<T, C>.perCol(values: Map<String, C>): DataFrame<T> = updateWithValuePerColumnImpl {
    values[it.name()] ?: throw IllegalArgumentException("Update value for column ${it.name()} is not defined")
}

public fun <T, C> Update<T, C>.perCol(values: AnyRow): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

public fun <T, C> Update<T, C>.perCol(valueSelector: Selector<DataColumn<C>, C>): DataFrame<T> =
    updateWithValuePerColumnImpl(valueSelector)

internal infix fun <T, C> RowValueFilter<T, C>?.and(other: RowValueFilter<T, C>): RowValueFilter<T, C> {
    if (this == null) return other
    val thisExp = this
    return { thisExp(this, it) && other(this, it) }
}

public fun <T, C> Update<T, C?>.notNull(): Update<T, C> =
    copy(filter = filter and { it != null }) as Update<T, C>

public fun <T, C> Update<T, C?>.notNull(expression: RowValueExpression<T, C, C>): DataFrame<T> =
    notNull().updateImpl { row, column, value ->
        expression(row, value)
    }

public fun <T, C> DataFrame<T>.update(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    expression: RowValueExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public fun <T, C> DataFrame<T>.update(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    expression: RowValueExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public fun <T> DataFrame<T>.update(
    firstCol: String,
    vararg cols: String,
    expression: RowValueExpression<T, Any?, Any?>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public fun <T, C> Update<T, C>.withNull(): DataFrame<T> = asNullable().withValue(null)

public fun <T, C> Update<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }

public infix fun <T, C> Update<T, C>.withValue(value: C): DataFrame<T> = with { value }
