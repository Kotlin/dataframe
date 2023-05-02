package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.DataColumnInternal
import org.jetbrains.kotlinx.dataframe.impl.headPlusIterable
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/**
 * Column with [type], [name]/[path] and [values]
 * Base interface for all three kinds of columns: [ValueColumn], [ColumnGroup] and [FrameColumn].
 * Column operations that doesn't clash by signature with [DataFrame] operations can be defined for [BaseColumn]
 *
 * @param T type of values contained in column.
 */
public interface BaseColumn<out T> : ColumnReference<T> {

    // region info

    public fun size(): Int
    public fun kind(): ColumnKind
    public fun type(): KType

    // TODO: remove
    public fun defaultValue(): T?

    // endregion

    // region get

    public operator fun get(index: Int): T
    public operator fun get(firstIndex: Int, vararg otherIndices: Int): BaseColumn<T> = get(
        headPlusIterable(
            firstIndex,
            otherIndices.asIterable()
        )
    )
    public operator fun get(row: AnyRow): T = get(row.index())

    public operator fun get(range: IntRange): BaseColumn<T>

    public operator fun get(indices: Iterable<Int>): BaseColumn<T>

    public operator fun get(columnName: String): AnyCol

    // endregion

    // region values

    public fun values(): Iterable<T>

    public fun toList(): List<T> = values().asList()
    public fun toSet(): Set<T>

    public fun distinct(): BaseColumn<T>
    public fun countDistinct(): Int

    public operator fun contains(value: @UnsafeVariance T): Boolean

    // endregion

    override fun rename(newName: String): BaseColumn<T>

    public override operator fun getValue(thisRef: Any?, property: KProperty<*>): BaseColumn<T> = (this as DataColumnInternal<*>).rename(property.columnName).forceResolve() as BaseColumn<T>
}

internal val <T> BaseColumn<T>.values: Iterable<T> get() = values()
internal val AnyBaseCol.size: Int get() = size()
