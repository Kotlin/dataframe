package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.api.SortFlag
import org.jetbrains.kotlinx.dataframe.impl.api.addFlag
import org.jetbrains.kotlinx.dataframe.impl.api.sortByImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.index
import kotlin.reflect.KProperty

public interface SortDsl<out T> : ColumnsSelectionDsl<T> {

    public val <C> ColumnSet<C>.desc: ColumnSet<C> get() = addFlag(SortFlag.Reversed)
    public val String.desc: ColumnSet<Comparable<*>?> get() = cast<Comparable<*>>().desc
    public val <C> KProperty<C>.desc: ColumnSet<C> get() = toColumnAccessor().desc

    public fun <C> ColumnSet<C?>.nullsLast(flag: Boolean): ColumnSet<C?> =
        if (flag) addFlag(SortFlag.NullsLast) else this

    public val <C> ColumnSet<C?>.nullsLast: ColumnSet<C?> get() = addFlag(SortFlag.NullsLast)
    public val String.nullsLast: ColumnSet<Comparable<*>?> get() = cast<Comparable<*>>().nullsLast
    public val <C> KProperty<C?>.nullsLast: ColumnSet<C?> get() = toColumnAccessor().nullsLast
}

public typealias SortColumnsSelector<T, C> = Selector<SortDsl<T>, ColumnSet<C>>

public fun <T, C> DataFrame<T>.sortBy(columns: SortColumnsSelector<T, C>): DataFrame<T> = sortByImpl(
    UnresolvedColumnsPolicy.Fail, columns
)

public fun <T> DataFrame<T>.sortBy(cols: Iterable<ColumnReference<Comparable<*>?>>): DataFrame<T> =
    sortBy { cols.toColumnSet() }

public fun <T> DataFrame<T>.sortBy(vararg cols: ColumnReference<Comparable<*>?>): DataFrame<T> =
    sortBy { cols.toColumns() }

public fun <T> DataFrame<T>.sortBy(vararg cols: String): DataFrame<T> = sortBy { cols.toColumns() }
public fun <T> DataFrame<T>.sortBy(vararg cols: KProperty<Comparable<*>?>): DataFrame<T> = sortBy { cols.toColumns() }

public fun <T> DataFrame<T>.sortWith(comparator: Comparator<DataRow<T>>): DataFrame<T> {
    val permutation = rows().sortedWith(comparator).map { it.index }
    return this[permutation]
}

public fun <T> DataFrame<T>.sortWith(comparator: (DataRow<T>, DataRow<T>) -> Int): DataFrame<T> =
    sortWith(Comparator(comparator))

public fun <T, C> DataFrame<T>.sortByDesc(columns: SortColumnsSelector<T, C>): DataFrame<T> {
    val set = columns.toColumns()
    return sortByImpl { set.desc }
}

public fun <T, C> DataFrame<T>.sortByDesc(vararg columns: KProperty<Comparable<C>?>): DataFrame<T> =
    sortByDesc { columns.toColumns() }

public fun <T> DataFrame<T>.sortByDesc(vararg columns: String): DataFrame<T> = sortByDesc { columns.toColumns() }
public fun <T, C> DataFrame<T>.sortByDesc(vararg columns: ColumnReference<Comparable<C>?>): DataFrame<T> =
    sortByDesc { columns.toColumns() }

public fun <T, C> DataFrame<T>.sortByDesc(columns: Iterable<ColumnReference<Comparable<C>?>>): DataFrame<T> =
    sortByDesc { columns.toColumnSet() }
