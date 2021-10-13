package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

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

public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> = filter { it != null } as DataColumn<T>
