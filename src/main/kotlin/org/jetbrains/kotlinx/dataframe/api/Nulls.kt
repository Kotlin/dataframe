package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.type
import java.math.BigDecimal
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

// region fillNulls

public fun <T, C> DataFrame<T>.fillNulls(cols: ColumnsSelector<T, C>): UpdateClause<T, C> = update(cols).where { it == null }
public fun <T> DataFrame<T>.fillNulls(vararg cols: String): UpdateClause<T, Any?> = fillNulls { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNulls(vararg cols: KProperty<C>): UpdateClause<T, C> = fillNulls { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNulls(vararg cols: ColumnReference<C>): UpdateClause<T, C> = fillNulls { cols.toColumns() }
public fun <T, C> DataFrame<T>.fillNulls(cols: Iterable<ColumnReference<C>>): UpdateClause<T, C> = fillNulls { cols.toColumnSet() }

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

public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> = filter { it != null } as DataColumn<T>

// endregion

// region dropNa

public fun <T> DataFrame<T>.dropNa(whereAllNa: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[selector]

    fun DataRow<T>.checkNa(col: AnyCol): Boolean {
        val value = col[this]
        return value == null || (value is Double && value.isNaN())
    }

    return if (whereAllNa) drop { cols.all { checkNa(it) } }
    else drop { cols.any { checkNa(it) } }
}

public fun <T> DataFrame<T>.dropNa(vararg cols: KProperty<*>, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNa(vararg cols: String, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNa(vararg cols: Column, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumns() }
public fun <T> DataFrame<T>.dropNa(cols: Iterable<Column>, whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { cols.toColumnSet() }

public fun <T> DataFrame<T>.dropNa(whereAllNa: Boolean = false): DataFrame<T> = dropNa(whereAllNa) { all() }

// endregion

//region nullToZero

public fun <T> DataFrame<T>.nullToZero(selector: ColumnsSelector<T, Number?>): DataFrame<T> {
    val cols = getColumnsWithPaths(selector).groupBy { it.type }

    return cols.asIterable().fold(this) { df, group ->
        df.nullColumnToZero(group.key, group.value)
    }
}

public fun <T> DataFrame<T>.nullToZero(vararg cols: String): DataFrame<T> = nullToZero { cols.toColumns() as Columns<Number?> }
public fun <T> DataFrame<T>.nullToZero(vararg cols: ColumnReference<Number?>): DataFrame<T> = nullToZero { cols.toColumns() }
public fun <T> DataFrame<T>.nullToZero(cols: Iterable<ColumnReference<Number?>>): DataFrame<T> = nullToZero { cols.toColumnSet() }

internal fun <T> DataFrame<T>.nullColumnToZero(type: KType, cols: Iterable<ColumnReference<Number?>>) =
    when (type.jvmErasure) {
        Double::class -> fillNulls(cols).with { .0 }
        Int::class -> fillNulls(cols).with { 0 }
        Long::class -> fillNulls(cols).with { 0L }
        BigDecimal::class -> fillNulls(cols).with { BigDecimal.ZERO }
        else -> throw IllegalArgumentException()
    }

// endregion
