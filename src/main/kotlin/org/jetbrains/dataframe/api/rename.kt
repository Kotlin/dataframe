package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.impl.columns.ColumnDataInternal
import org.jetbrains.dataframe.impl.columns.RenamedColumnDef
import kotlin.reflect.KProperty

fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>) = rename { mappings.map { it.first.toColumnDef() }.toColumnSet() }
        .into(*mappings.map { it.second }.toTypedArray())

fun <T, C> DataFrame<T>.rename(selector: ColumnsSelector<T, C>) = RenameClause(this, selector)
fun <T, C> DataFrame<T>.rename(vararg cols: ColumnDef<C>) = rename { cols.toColumns() }
fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>) = rename { cols.toColumns() }
fun <T> DataFrame<T>.rename(vararg cols: String) = rename { cols.toColumns() }
fun <T, C> DataFrame<T>.rename(cols: Iterable<ColumnDef<C>>) = rename { cols.toColumnSet() }

data class RenameClause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>)

fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnDef<*>) = into(*newColumns.map { it.name() }.toTypedArray())
fun <T, C> RenameClause<T, C>.into(vararg newNames: String) = df.move(selector).intoIndexed { col, index ->
    col.path.drop(1) + newNames[index]
}

fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String) = df.move(selector).into {
    it.path.drop(1) + transform(it)
}

fun <C> ColumnDef<C>.rename(newName: String) = if (newName == name()) this else RenamedColumnDef(this, newName)

fun <C> ColumnData<C>.rename(newName: String) = if (newName == name()) this else (this as ColumnDataInternal<C>).rename(newName)