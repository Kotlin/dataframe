package org.jetbrains.dataframe

import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

class UpdateClause<T, C>(val df: DataFrame<T>, val filter: UpdateExpression<T, C, Boolean>?, val selector: ColumnsSelector<T, C>)

fun <T, C> UpdateClause<T, C>.where(predicate: UpdateExpression<T, C, Boolean>) = UpdateClause(df, predicate, selector)typealias UpdateExpression<T, C, R> = DataFrameRow<T>.(C) -> R
typealias UpdateByColumnExpression<T, C, R> = (DataFrameRow<T>, ColumnData<C>) -> R

fun <T, C, R> doUpdate(clause: UpdateClause<T, C>, type: KType, expression: (DataFrameRow<T>, ColumnData<C>) -> R): DataFrame<T> {

    val removeResult = clause.df.doRemove(clause.selector)

    val removeRoot = removeResult.removeRoot ?: return clause.df

    val toInsert = removeRoot.dfs().mapNotNull {
        val srcColumn = it.data.column as? ColumnData<C>
        if (srcColumn != null) {

            var nullable = false
            val values = (0 until clause.df.nrow).map {
                clause.df[it].let { row ->
                    val currentValue = srcColumn[row.index]
                    if (clause.filter?.invoke(row, currentValue) == false)
                        currentValue as R
                    else expression(row, srcColumn)
                }.also { if (it == null) nullable = true }
            }
            val typeWithNullability = type.withNullability(nullable)

            val newColumn: ColumnData<*> = if (type.classifier == DataFrame::class) {
                val firstFrame = values.firstOrNull { it != null } as? DataFrame<T>
                if (firstFrame != null) {
                    // TODO :compute most general scheme for all data frames
                    ColumnData.createTable(srcColumn.name, values as List<DataFrame<T>>, firstFrame)
                } else column(srcColumn.name, values, typeWithNullability)
            } else column(srcColumn.name, values, typeWithNullability)

            ColumnToInsert(it.pathFromRoot(), it, newColumn)
        } else null
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

fun <T, C> DataFrame<T>.update(selector: ColumnsSelector<T, C>) = UpdateClause(this, null, selector)
fun <T, C> DataFrame<T>.update(cols: Iterable<ColumnDef<C>>) = update { cols.toColumns() }
fun <T> DataFrame<T>.update(vararg cols: String) = update { cols.toColumns() }
fun <T, C> DataFrame<T>.update(vararg cols: KProperty<C>) = update { cols.toColumns() }
fun <T, C> DataFrame<T>.update(vararg cols: ColumnDef<C>) = update { cols.toColumns() }

fun <T, C> UpdateClause<T, C>.withNull() = with { null as Any? }
inline infix fun <T, C, reified R> UpdateClause<T, C>.with(value: R) = with { value }
