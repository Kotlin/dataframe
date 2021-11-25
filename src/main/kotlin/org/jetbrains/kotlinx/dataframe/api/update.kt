package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.updateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWithValuePerColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.index
import kotlin.reflect.KProperty

public fun <T, C> DataFrame<T>.update(columns: ColumnsSelector<T, C>): UpdateClause<T, C> =
    UpdateClause(this, null, columns)

public fun <T, C> DataFrame<T>.update(columns: Iterable<ColumnReference<C>>): UpdateClause<T, C> =
    update { columns.toColumnSet() }

public fun <T> DataFrame<T>.update(vararg columns: String): UpdateClause<T, Any?> = update { columns.toColumns() }
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): UpdateClause<T, C> = update { columns.toColumns() }
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): UpdateClause<T, C> =
    update { columns.toColumns() }

public data class UpdateClause<T, C>(
    val df: DataFrame<T>,
    val filter: RowValueFilter<T, C>?,
    val columns: ColumnsSelector<T, C>
) {
    public fun <R : C> cast(): UpdateClause<T, R> =
        UpdateClause(df, filter as RowValueFilter<T, R>?, columns as ColumnsSelector<T, R>)
}

public fun <T, C> UpdateClause<T, C>.where(predicate: RowValueFilter<T, C>): UpdateClause<T, C> =
    copy(filter = filter and predicate)

public fun <T, C> UpdateClause<T, C>.at(rowIndices: Collection<Int>): UpdateClause<T, C> = where { index in rowIndices }
public fun <T, C> UpdateClause<T, C>.at(vararg rowIndices: Int): UpdateClause<T, C> = at(rowIndices.toSet())
public fun <T, C> UpdateClause<T, C>.at(rowRange: IntRange): UpdateClause<T, C> = where { index in rowRange }

public infix fun <T, C> UpdateClause<T, C>.perRowCol(expression: RowColumnExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, column, _ -> expression(row, column) }

public infix fun <T, C> UpdateClause<T, C>.with(expression: RowValueExpression<T, C, C>): DataFrame<T> =
    withExpression(expression)

public fun <T, C> UpdateClause<T, C>.asNullable(): UpdateClause<T, C?> = this as UpdateClause<T, C?>

public fun <T, C> UpdateClause<T, C>.perCol(values: Map<String, C>): DataFrame<T> = updateWithValuePerColumnImpl {
    values[it.name()] ?: throw IllegalArgumentException("Update value for column ${it.name()} is not defined")
}

public fun <T, C> UpdateClause<T, C>.perCol(values: AnyRow): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

public fun <T, C> UpdateClause<T, C>.perCol(valueSelector: Selector<DataColumn<C>, C>): DataFrame<T> =
    updateWithValuePerColumnImpl(valueSelector)

public fun <T, C> UpdateClause<T, C>.withExpression(expression: RowValueExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, _, value ->
        expression(row, value)
    }

internal infix fun <T, C> RowValueFilter<T, C>?.and(other: RowValueFilter<T, C>): RowValueFilter<T, C> {
    if (this == null) return other
    val thisExp = this
    return { thisExp(this, it) && other(this, it) }
}

public fun <T, C> UpdateClause<T, C?>.notNull(): UpdateClause<T, C> =
    copy(filter = filter and { it != null }) as UpdateClause<T, C>

public fun <T, C> UpdateClause<T, C?>.notNull(expression: RowValueExpression<T, C, C>): DataFrame<T> =
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
    update(*headPlusArray(firstCol, cols)).withExpression(expression)

public fun <T, C> UpdateClause<T, C>.withNull(): DataFrame<T> = asNullable().withValue(null)

public fun <T, C> UpdateClause<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }

public infix fun <T, C> UpdateClause<T, C>.withValue(value: C): DataFrame<T> = withExpression { value }
