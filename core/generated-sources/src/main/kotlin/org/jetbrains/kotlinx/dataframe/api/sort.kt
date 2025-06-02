package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.SortFlag
import org.jetbrains.kotlinx.dataframe.impl.api.addFlag
import org.jetbrains.kotlinx.dataframe.impl.api.sortByImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumnWithActualType
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

public interface SortDsl<out T> : ColumnsSelectionDsl<T> {
    public fun <C> ColumnSet<C>.desc(): ColumnSet<C> = addFlag(SortFlag.Reversed)

    public fun <C> SingleColumn<C>.desc(): SingleColumn<C> = addFlag(SortFlag.Reversed).single()

    public fun String.desc(): SingleColumn<Comparable<*>?> = invoke<Comparable<*>>().desc()

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.desc(): SingleColumn<C> = toColumnAccessor().desc()

    public fun <C> ColumnSet<C?>.nullsLast(flag: Boolean = true): ColumnSet<C?> =
        if (flag) addFlag(SortFlag.NullsLast) else this

    public fun <C> SingleColumn<C?>.nullsLast(flag: Boolean = true): SingleColumn<C?> =
        if (flag) addFlag(SortFlag.NullsLast).single() else this

    public fun String.nullsLast(flag: Boolean = true): SingleColumn<Comparable<*>?> =
        invoke<Comparable<*>>().nullsLast(flag)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C?>.nullsLast(flag: Boolean = true): SingleColumn<C?> = toColumnAccessor().nullsLast(flag)
}

/**
 * [SortColumnsSelector] is used to express or select multiple columns to sort by, represented by [ColumnsResolver]`<C>`,
 * using the context of [SortDsl]`<T>` as `this` and `it`.
 *
 * So:
 * ```kotlin
 * SortDsl<T>.(it: SortDsl<T>) -> ColumnSet<C>
 * ```
 */
public typealias SortColumnsSelector<T, C> = Selector<SortDsl<T>, ColumnsResolver<C>>

// region DataColumn

public fun <T : Comparable<T>> DataColumn<T>.sort(): ValueColumn<T> =
    DataColumn.createValueColumn(name, values().sorted(), type, defaultValue = defaultValue())

public fun <T : Comparable<T>> DataColumn<T>.sortDesc(): ValueColumn<T> =
    DataColumn.createValueColumn(name, values().sortedDescending(), type, defaultValue = defaultValue())

/**
 * ## Sort [DataColumn] With
 *
 * This function returns the sorted version of the current [ValueColumn], [FrameColumn], or [ColumnGroup] based
 * on the given [Comparator]. The [comparator] can either be given as an instance of [Comparator], or directly
 * as a lambda.
 *
 * #### For example
 *
 * `df`[`[`][DataFrame.get]`"price"`[`]`][DataFrame.get]`.`[sortWith][sortWith]` { a, b -> a - b }`
 *
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * `df.`[select][DataFrame.select]` {`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`name.`[sortWith][sortWith]`(myComparator) `[and][ColumnsSelectionDsl.and]` `[allAfter][ColumnsSelectionDsl.allAfter]`(name)`
 *
 * `}`
 *
 * @receiver The [DataColumn] to sort. This can be either a [ValueColumn], [FrameColumn], or [ColumnGroup] and will
 *   dictate the return type of the function.
 * @param [comparator] The [Comparator] to use for sorting the [DataColumn]. This can either be a [Comparator]<[T]> or
 *   a lambda of type `(`[T][T]`, `[T][T]`) -> `[Int][Int].
 * @return The sorted [DataColumn] [this] of the same type as the receiver.
 */
private interface CommonDataColumnSortWithDocs

/** ## Sort [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] With
 *
 * This function returns the sorted version of the current [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] based
 * on the given [Comparator]. The [comparator] can either be given as an instance of [Comparator], or directly
 * as a lambda.
 *
 * #### For example
 *
 * `df`[`[`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`"price"`[`]`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]` { a, b -> a - b }`
 *
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`name.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]`(myComparator) `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(name)`
 *
 * `}`
 *
 * @receiver The [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] to sort. This can be either a [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and will
 *   dictate the return type of the function.
 * @param [comparator] The [Comparator] to use for sorting the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]. This can either be a [Comparator]<[T]> or
 *   a lambda of type `(`[T][T]`, `[T][T]`) -> `[Int][Int].
 * @return The sorted [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] [this] of the same type as the receiver. */
public fun <T, C : DataColumn<T>> C.sortWith(comparator: Comparator<T>): C =
    DataColumn.createByType(name, values().sortedWith(comparator), type) as C

/** ## Sort [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] With
 *
 * This function returns the sorted version of the current [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] based
 * on the given [Comparator]. The [comparator] can either be given as an instance of [Comparator], or directly
 * as a lambda.
 *
 * #### For example
 *
 * `df`[`[`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`"price"`[`]`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]` { a, b -> a - b }`
 *
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`name.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]`(myComparator) `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(name)`
 *
 * `}`
 *
 * @receiver The [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] to sort. This can be either a [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and will
 *   dictate the return type of the function.
 * @param [comparator] The [Comparator] to use for sorting the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]. This can either be a [Comparator]<[T]> or
 *   a lambda of type `(`[T][T]`, `[T][T]`) -> `[Int][Int].
 * @return The sorted [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] [this] of the same type as the receiver. */
public fun <T, C : DataColumn<T>> C.sortWith(comparator: (T, T) -> Int): C = sortWith(Comparator(comparator))

// endregion

// region DataFrame

public fun <T, C> DataFrame<T>.sortBy(columns: SortColumnsSelector<T, C>): DataFrame<T> =
    sortByImpl(UnresolvedColumnsPolicy.Fail, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sortBy(vararg cols: ColumnReference<*>): DataFrame<T> = sortBy { cols.toColumnSet() }

public fun <T> DataFrame<T>.sortBy(vararg cols: String): DataFrame<T> = sortBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sortBy(vararg cols: KProperty<Comparable<*>?>): DataFrame<T> = sortBy { cols.toColumnSet() }

public fun <T> DataFrame<T>.sortWith(comparator: Comparator<DataRow<T>>): DataFrame<T> {
    val permutation = rows().sortedWith(comparator).map { it.index }
    return this[permutation]
}

public fun <T> DataFrame<T>.sortWith(comparator: (DataRow<T>, DataRow<T>) -> Int): DataFrame<T> =
    sortWith(Comparator(comparator))

public fun <T, C> DataFrame<T>.sortByDesc(columns: SortColumnsSelector<T, C>): DataFrame<T> {
    val set = columns.toColumnSet()
    return sortByImpl { set.desc() }
}

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.sortByDesc(vararg columns: KProperty<Comparable<C>?>): DataFrame<T> =
    sortByDesc { columns.toColumnSet() }

public fun <T> DataFrame<T>.sortByDesc(vararg columns: String): DataFrame<T> = sortByDesc { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sortByDesc(vararg columns: ColumnReference<*>): DataFrame<T> =
    sortByDesc { columns.toColumnSet() }

// endregion

// region GroupBy

public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: String): GroupBy<T, G> = sortBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: ColumnReference<*>): GroupBy<T, G> = sortBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: KProperty<Comparable<*>?>): GroupBy<T, G> =
    sortBy { cols.toColumnSet() }

public fun <T, G, C> GroupBy<T, G>.sortBy(selector: SortColumnsSelector<G, C>): GroupBy<T, G> = sortByImpl(selector)

public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: String): GroupBy<T, G> = sortByDesc { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: ColumnReference<*>): GroupBy<T, G> =
    sortByDesc { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: KProperty<Comparable<*>?>): GroupBy<T, G> =
    sortByDesc { cols.toColumnSet() }

public fun <T, G, C> GroupBy<T, G>.sortByDesc(selector: SortColumnsSelector<G, C>): GroupBy<T, G> {
    val set = selector.toColumnSet()
    return sortByImpl { set.desc() }
}

private fun <T, G, C> GroupBy<T, G>.createColumnFromGroupExpression(
    receiver: ColumnsSelectionDsl<T>,
    expression: DataFrameExpression<G, C>,
): DataColumn<C?> =
    receiver.newColumnWithActualType("") { row ->
        val group = row[groups]
        expression(group, group)
    }

public fun <T, G, C> GroupBy<T, G>.sortByGroup(
    nullsLast: Boolean = false,
    expression: DataFrameExpression<G, C>,
): GroupBy<T, G> =
    toDataFrame().sortBy {
        createColumnFromGroupExpression(this, expression).nullsLast(nullsLast)
    }.asGroupBy(groups)

public fun <T, G, C> GroupBy<T, G>.sortByGroupDesc(
    nullsLast: Boolean = false,
    expression: DataFrameExpression<G, C>,
): GroupBy<T, G> =
    toDataFrame().sortBy {
        createColumnFromGroupExpression(this, expression).desc().nullsLast(nullsLast)
    }.asGroupBy(groups)

public fun <T, G> GroupBy<T, G>.sortByCountAsc(): GroupBy<T, G> = sortByGroup { nrow }

public fun <T, G> GroupBy<T, G>.sortByCount(): GroupBy<T, G> = sortByGroupDesc { nrow }

public fun <T, G> GroupBy<T, G>.sortByKeyDesc(nullsLast: Boolean = false): GroupBy<T, G> =
    toDataFrame()
        .sortBy { keys.columns().toColumnSet().desc().nullsLast(nullsLast) }
        .asGroupBy(groups)

public fun <T, G> GroupBy<T, G>.sortByKey(nullsLast: Boolean = false): GroupBy<T, G> =
    toDataFrame()
        .sortBy { keys.columns().toColumnSet().nullsLast(nullsLast) }
        .asGroupBy(groups)

// endregion
