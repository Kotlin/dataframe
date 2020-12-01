package org.jetbrains.dataframe

data class CastClause<T>(val df: DataFrame<T>, val selector: ColumnsSelector<T, *>) {
    inline fun <reified C> to() = df.update(selector).with { it as C? }
}

fun <T> DataFrame<T>.cast(selector: ColumnsSelector<T, *>) = CastClause(this, selector)