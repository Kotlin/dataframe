package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.impl.createDataCollector
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

fun <T, C> DataFrame<T>.update(selector: ColumnsSelector<T, C>) = UpdateClause(this, null, selector, null)
fun <T, C> DataFrame<T>.update(cols: Iterable<ColumnDef<C>>) = update { cols.toColumnSet() }
fun <T> DataFrame<T>.update(vararg cols: String) = update { cols.toColumns() }
fun <T, C> DataFrame<T>.update(vararg cols: KProperty<C>) = update { cols.toColumns() }
fun <T, C> DataFrame<T>.update(vararg cols: ColumnDef<C>) = update { cols.toColumns() }

class UpdateClause<T, C>(val df: DataFrame<T>, val filter: UpdateExpression<T, C, Boolean>?, val selector: ColumnsSelector<T, C>, val typeSuggestions: ((KClass<*>) -> KType)?){
    fun <R> cast() = UpdateClause(df, filter as UpdateExpression<T, R, Boolean>?, selector as ColumnsSelector<T, R>, typeSuggestions)
}

fun <T, C> UpdateClause<T, C>.where(predicate: UpdateExpression<T, C, Boolean>) = UpdateClause(df, predicate, selector, typeSuggestions)

fun <T, C> UpdateClause<T, C>.guessTypes() = UpdateClause(df, filter, selector) { it.createStarProjectedType(false) }

fun <T, C> UpdateClause<T, C>.suggestTypes(vararg suggestions: Pair<KClass<*>, KType>): UpdateClause<T, C> {
    val map = suggestions.toMap()
    return UpdateClause(df, filter, selector) { map[it] ?: it.createStarProjectedType(false) }
}

typealias UpdateExpression<T, C, R> = DataFrameRow<T>.(C) -> R
typealias UpdateByColumnExpression<T, C, R> = (DataFrameRow<T>, ColumnData<C>) -> R

fun <T, C, R> doUpdate(clause: UpdateClause<T, C>, type: KType, expression: (DataFrameRow<T>, ColumnData<C>) -> R): DataFrame<T> {

    val removeResult = clause.df.doRemove(clause.selector)

    val toInsert = removeResult.removedColumns.map {
        val srcColumn = it.data.column as ColumnData<C>
        val collector = if (clause.typeSuggestions != null) createDataCollector(clause.df.nrow, clause.typeSuggestions) else createDataCollector(clause.df.nrow, type)
        clause.df.forEach { row ->
            val currentValue = srcColumn[row.index]
            val skip = clause.filter?.invoke(row, currentValue) == false
            val newValue = if (skip) currentValue as R else expression(row, srcColumn)
            collector.add(newValue)
        }

        val newColumn = collector.toColumn(srcColumn.name)

        ColumnToInsert(it.pathFromRoot(), it, newColumn)
    }
    return removeResult.df.doInsert(toInsert)
}

// TODO: rename
inline infix fun <T, C, reified R> UpdateClause<T, C>.with2(noinline expression: UpdateByColumnExpression<T, C, R>) = doUpdate(this, getType<R>(), expression)

inline infix fun <T, C, reified R> UpdateClause<T, C>.with(noinline expression: UpdateExpression<T, C, R>) = doUpdate(this, getType<R>()) { row, column ->
    val currentValue = column[row.index]
    if (filter?.invoke(row, currentValue) == false)
        currentValue as R
    else expression(row, currentValue)
}

inline fun <T, C, reified R> DataFrame<T>.update(firstCol: ColumnDef<C>, vararg cols: ColumnDef<C>, noinline expression: UpdateExpression<T, C, R>) =
        update(*headPlusArray(firstCol, cols)).with(expression)

inline fun <T, C, reified R> DataFrame<T>.update(firstCol: KProperty<C>, vararg cols: KProperty<C>, noinline expression: UpdateExpression<T, C, R>) =
        update(*headPlusArray(firstCol, cols)).with(expression)

inline fun <T, reified R> DataFrame<T>.update(firstCol: String, vararg cols: String, noinline expression: UpdateExpression<T, Any?, R>) =
        update(*headPlusArray(firstCol, cols)).with(expression)

fun <T, C> UpdateClause<T, C>.withNull() = with { null as Any? }
inline infix fun <T, C, reified R> UpdateClause<T, C>.with(value: R) = with { value }
