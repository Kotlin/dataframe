package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KProperty

/**
 * Column that stores values.
 *
 * @param T - type of values
 */
public interface ValueColumn<out T> : DataColumn<T> {

    override fun kind(): ColumnKind = ColumnKind.Value

    override fun distinct(): ValueColumn<T>

    override fun get(indices: Iterable<Int>): ValueColumn<T>

    override fun rename(newName: String): ValueColumn<T>

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): ValueColumn<T> = super.getValue(thisRef, property) as ValueColumn<T>

    public override operator fun get(range: IntRange): ValueColumn<T>
}
