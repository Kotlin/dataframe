package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.DataColumnInternal
import org.jetbrains.kotlinx.dataframe.impl.headPlusIterable
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/**
 * Column with type, name/path and values
 * Base interface for all three kinds of columns: [ValueColumn], [ColumnGroup] and [FrameColumn]
 */
public interface BaseColumn<out T> : ColumnReference<T> {

    public fun size(): Int
    public fun ndistinct(): Int
    public fun kind(): ColumnKind
    public fun type(): KType

    public operator fun get(index: Int): T
    public operator fun get(firstIndex: Int, vararg otherIndices: Int): BaseColumn<T> = slice(
        headPlusIterable(
            firstIndex,
            otherIndices.asIterable()
        )
    )
    public operator fun get(row: AnyRow): T = get(row.index())

    public fun values(): Iterable<T>

    public fun toList(): List<T> = values().asList()

    public fun defaultValue(): T?

    public fun distinct(): BaseColumn<T>

    public fun slice(range: IntRange): BaseColumn<T>

    public fun slice(indices: Iterable<Int>): BaseColumn<T>

    public fun slice(mask: BooleanArray): BaseColumn<T>

    override fun rename(newName: String): BaseColumn<T>

    public operator fun get(columnName: String): AnyCol

    public fun toSet(): Set<T>

    public override operator fun getValue(thisRef: Any?, property: KProperty<*>): BaseColumn<T> = (this as DataColumnInternal<*>).rename(property.name).forceResolve() as BaseColumn<T>
}

internal val <T> BaseColumn<T>.values: Iterable<T> get() = values()
internal val AnyBaseColumn.ndistinct get() = ndistinct()
internal val AnyBaseColumn.size: Int get() = size()
