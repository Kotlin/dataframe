package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.typeOf

// region Pivot

public inline fun <T, reified V> Pivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> = delegate { with(expression) }

// endregion

// region ReducedPivot

public inline fun <T, reified V> ReducedPivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> = pivot.delegate { reduce(reducer).with(expression) }

// endregion

// region PivotGroupBy

public inline fun <T, reified V> PivotGroupBy<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = typeOf<V>()
    return aggregate { internal().withExpr(type, emptyPath(), expression) }
}

// endregion

// region ReducedPivotGroupBy

public inline fun <T, reified V> ReducedPivotGroupBy<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = typeOf<V>()
    return pivot.aggregate {
        val value = reducer(this)?.let {
            val value = expression(it, it)
            if (value is AnyColumnReference) it[value]
            else value
        }
        internal().yield(emptyPath(), value, type)
    }
}

// endregion
