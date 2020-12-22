package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnSet
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

fun <T> DataFrame<T>.nullToZero(selector: ColumnsSelector<T, Number?>): DataFrame<T> {
    val cols = getColumnsWithPaths(selector).groupBy { it.type }

    return cols.asIterable().fold(this) { df, group ->
        df.nullColumnToZero(group.key, group.value)
    }
}

fun <T> DataFrame<T>.nullToZero(vararg cols: String) = nullToZero { cols.toColumns() as ColumnSet<Number?> }
fun <T> DataFrame<T>.nullToZero(vararg cols: ColumnDef<Number?>) = nullToZero { cols.toColumns() }
fun <T> DataFrame<T>.nullToZero(cols: Iterable<ColumnDef<Number?>>) = nullToZero { cols.toColumnSet() }

internal fun <T> DataFrame<T>.nullColumnToZero(type: KType, cols: Iterable<ColumnDef<Number?>>) =
        when (type.jvmErasure) {
            Double::class -> update(cols).with { it as Double? ?: .0 }
            Int::class -> update(cols).with { it as Int? ?: 0 }
            Long::class -> update(cols).with { it as Long? ?: 0 }
            else -> throw IllegalArgumentException()
        }
