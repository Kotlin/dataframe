package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.dataframe.NamedValue
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.getType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ValueWithDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.internal
import org.jetbrains.kotlinx.dataframe.pathOf

public interface AggregateReceiverWithDefault<out T> : DataFrame<T> {

    public infix fun <R> R.default(defaultValue: R): Any = when (this) {
        is NamedValue -> this.also { it.default = defaultValue }
        else -> ValueWithDefault(this, defaultValue)
    }
}

public abstract class AggregateReceiver<out T> : AggregateReceiverWithDefault<T> {

    public inline infix fun <reified R> R.into(name: String): NamedValue = internal().yield(pathOf(name), this, getType<R>())
}
