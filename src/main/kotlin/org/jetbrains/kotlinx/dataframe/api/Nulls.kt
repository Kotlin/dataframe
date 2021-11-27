package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region fillNulls

public fun <T, C> DataFrame<T>.fillNulls(cols: ColumnsSelector<T, C>): UpdateClause<T, C> = update(cols).where { it == null }
public fun <T> DataFrame<T>.fillNulls(vararg cols: String): UpdateClause<T, Any?> = fillNulls { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNulls(vararg cols: KProperty<C>): UpdateClause<T, C> = fillNulls { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNulls(vararg cols: ColumnReference<C>): UpdateClause<T, C> = fillNulls { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNulls(cols: Iterable<ColumnReference<C>>): UpdateClause<T, C> = fillNulls { cols.toColumnSet() }

// endregion

internal inline val Any?.isNaN: Boolean get() = (this is Double && isNaN()) || (this is Float && isNaN())

internal inline val Any?.isNA: Boolean get() = this == null || isNaN

internal inline val Double?.isNA: Boolean get() = this == null || this.isNaN()

internal inline val Float?.isNA: Boolean get() = this == null || this.isNaN()

// region fillNaNs

public fun <T, C> DataFrame<T>.fillNaNs(cols: ColumnsSelector<T, C>): UpdateClause<T, C> = update(cols).where { it.isNaN }
public fun <T> DataFrame<T>.fillNaNs(vararg cols: String): UpdateClause<T, Any?> = fillNaNs { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNaNs(vararg cols: KProperty<C>): UpdateClause<T, C> = fillNaNs { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNaNs(vararg cols: ColumnReference<C>): UpdateClause<T, C> = fillNaNs { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNaNs(cols: Iterable<ColumnReference<C>>): UpdateClause<T, C> = fillNaNs { cols.toColumnSet() }

// endregion

// region fillNA

public fun <T, C> DataFrame<T>.fillNA(cols: ColumnsSelector<T, C>): UpdateClause<T, C> = update(cols).where { it.isNA }
public fun <T> DataFrame<T>.fillNA(vararg cols: String): UpdateClause<T, Any?> = fillNA { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNA(vararg cols: KProperty<C>): UpdateClause<T, C> = fillNA { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNA(vararg cols: ColumnReference<C>): UpdateClause<T, C> = fillNA { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNA(cols: Iterable<ColumnReference<C>>): UpdateClause<T, C> = fillNA { cols.toColumnSet() }

// endregion

// region dropNulls

public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[selector]
    return if (whereAllNull) drop { row -> cols.all { col -> col[row] == null } }
    else drop { row -> cols.any { col -> col[row] == null } }
}

public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false): DataFrame<T> = dropNulls(whereAllNull) { all() }
public fun <T> DataFrame<T>.dropNulls(vararg cols: KProperty<*>, whereAllNull: Boolean = false): DataFrame<T> = dropNulls(whereAllNull) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNulls(vararg cols: String, whereAllNull: Boolean = false): DataFrame<T> = dropNulls(whereAllNull) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNulls(vararg cols: Column, whereAllNull: Boolean = false): DataFrame<T> = dropNulls(whereAllNull) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNulls(cols: Iterable<Column>, whereAllNull: Boolean = false): DataFrame<T> = dropNulls(whereAllNull) { cols.toColumnSet() }

public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> = (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>

// endregion

// region dropNA

public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[selector]

    fun DataRow<T>.checkNA(col: AnyCol) = col[this].isNA

    return if (whereAllNA) drop { cols.all { checkNA(it) } }
    else drop { cols.any { checkNA(it) } }
}

public fun <T> DataFrame<T>.dropNA(vararg cols: KProperty<*>, whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNA(vararg cols: String, whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNA(vararg cols: Column, whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNA(cols: Iterable<Column>, whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { cols.toColumnSet() }

public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { all() }

// endregion
