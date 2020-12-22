package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.TableColumn

class MergeClause<T, C, R>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>, val transform: (Iterable<C>) -> R)

fun <T, C> DataFrame<T>.mergeCols(selector: ColumnsSelector<T, C>) = MergeClause(this, selector, { it })
inline fun <T, C, reified R> MergeClause<T, C, R>.into(columnName: String) = into(listOf(columnName))
inline fun <T, C, reified R> MergeClause<T, C, R>.into(columnPath: List<String>): DataFrame<T> {
    val grouped = df.move(selector).into(columnPath)
    val res = grouped.update { getGroup(columnPath) }.with {
        transform(it.values as List<C>)
    }
    return res
}

fun <T, C, R> MergeClause<T, C, R>.asStrings() = by(", ")
fun <T, C, R> MergeClause<T, C, R>.by(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...") =
        MergeClause<T, C, String>(df, selector) { it.joinToString(separator = separator, prefix = prefix, postfix = postfix, limit = limit, truncated = truncated) }

fun <T, C> MergeClause<T, C, *>.mergeRows(): DataFrame<T> {

    val removeResult = df.doRemove(selector)
    val removeRoot = removeResult.removeRoot ?: return df

    val grouped = df.groupBy { allExcept(selector) }

    val columnsToInsert = removeRoot.allWithColumns().map { node ->
        val column = node.data.column!!
        val newName = column.name
        val newColumn = when(column){
            is GroupedColumn<*> -> {
                val data = grouped.groups.asIterable().map { it.get(column).df }
                ColumnData.createTable(newName, data, column.df)
            }
            is TableColumn<*> -> {
                val data = grouped.groups.asIterable().map { it[column].toList().union() }
                ColumnData.createTable(newName, data, column.df)
            }
            else -> {
                val data = grouped.groups.asIterable().map { it[column].toList() }
                ColumnData.create(newName, data, List::class.createType(column.type))
            }
        }
        ColumnToInsert(node.pathFromRoot(), node, newColumn)
    }
    val result = insertColumns(grouped.keys, columnsToInsert)
    return result
}

inline fun <T, C, R, reified V> MergeClause<T, C, R>.by(crossinline transform: (R) -> V) = MergeClause(df, selector) { transform(this@by.transform(it)) }