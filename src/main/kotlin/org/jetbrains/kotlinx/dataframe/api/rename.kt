package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.toColumnAccessor
import kotlin.reflect.KProperty

public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> = rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
    .into(*mappings.map { it.second }.toTypedArray())

public fun <T, C> DataFrame<T>.rename(selector: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, selector)
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(cols: Iterable<ColumnReference<C>>): RenameClause<T, C> = rename { cols.toColumnSet() }

public data class RenameClause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>)

public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())
public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> = df.move(selector).intoIndexed { col, index ->
    col.path.drop(1) + newNames[index]
}

public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> = df.move(selector).into {
    it.path.dropLast(1) + transform(it)
}
