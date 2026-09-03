package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/**
 * Column that stores values.
 *
 * Can be instantiated by [<code>DataColumn.createValueColumn</code>][DataColumn.createValueColumn].
 *
 * @param T - type of values
 */
public interface ValueColumn<out T> : DataColumn<T> {

    override fun kind(): ColumnKind = ColumnKind.Value

    override fun distinct(): ValueColumn<T>

    override fun get(indices: Iterable<Int>): DataColumn<T>

    override fun rename(newName: String): ValueColumn<T>

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): ValueColumn<T> =
        super.getValue(thisRef, property) as ValueColumn<T>

    public override operator fun get(range: IntRange): DataColumn<T>

    /**
     * Changes column [<code>type</code>][BaseColumn.type].
     * Doesn't change column [<code>values</code>][BaseColumn.values].
     *
     * @param type New column [<code>type</code>][KType].
     */
    public fun changeType(type: KType): ValueColumn<T>
}
