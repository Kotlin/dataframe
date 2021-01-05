package org.jetbrains.dataframe

import org.jetbrains.dataframe.impl.columns.ColumnDataInternal
import kotlin.reflect.KProperty
import kotlin.reflect.KType

fun <T> DataFrame<T>.cast(selector: ColumnsSelector<T, *>) = CastClause(this, selector)
fun <T> DataFrame<T>.cast(vararg columns: KProperty<*>) = cast { columns.toColumns() }
fun <T> DataFrame<T>.cast(vararg columns: String) = cast { columns.toColumns() }
fun <T> DataFrame<T>.cast(vararg columns: Column) = cast { columns.toColumns() }

data class CastClause<T>(val df: DataFrame<T>, val selector: ColumnsSelector<T, *>) {
    inline fun <reified C> to() = df.update(selector).with { it as C? }
}

fun <T> CastClause<T>.to(type: KType) = df.replace(selector).with { (it as ColumnDataInternal<T>).changeType(type) }

