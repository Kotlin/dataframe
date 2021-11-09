package org.jetbrains.kotlinx.dataframe.columns

import kotlin.reflect.KProperty

public interface ColumnAccessor<T> : ColumnReference<T> {

    public override operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = rename(property.name)

    public operator fun <C> get(column: ColumnReference<C>): ColumnAccessor<C>

    override fun rename(newName: String): ColumnAccessor<T>
}
