package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import kotlin.reflect.KProperty

public interface ColumnAccessor<T> : ColumnReference<T> {

    public override operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = rename(property.name)

    public operator fun <C> get(column: ColumnReference<C>): ColumnAccessor<C>

    override fun rename(newName: String): ColumnAccessor<T>

    public fun <C> typed(): ColumnAccessor<C> = this as ColumnAccessor<C>
}

public fun <T> ColumnReference<T>.toAccessor(): ColumnAccessor<T> = when (this) {
    is ColumnAccessor<T> -> this
    else -> ColumnAccessorImpl(path())
}
