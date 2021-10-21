package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ValueWithDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.internal
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.pathOf

public abstract class AggregateDsl<out T> : DataFrame<T> {

    public inline infix fun <reified R> R.into(name: String): NamedValue = internal().yield(pathOf(name), this, getType<R>())

    public infix fun <R> R.default(defaultValue: R): Any = when (this) {
        is NamedValue -> this.also { it.default = defaultValue }
        else -> ValueWithDefault(this, defaultValue)
    }
}
