package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotGroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotInAggregateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.PivotChainColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public interface PivotDsl<out T> : ColumnsSelectionDsl<T> {

    public infix fun <C> ColumnSet<C>.then(other: ColumnSet<C>): ColumnSet<C> = PivotChainColumnSet(this, other)

    public infix fun <C> String.then(other: ColumnSet<C>): ColumnSet<C> = toColumnOf<C>() then other

    public infix fun <C> ColumnSet<C>.then(other: String): ColumnSet<C> = this then other.toColumnOf()

    public infix fun String.then(other: String): ColumnSet<Any?> = toColumnAccessor() then other.toColumnAccessor()

    public infix fun <C> KProperty<C>.then(other: ColumnSet<C>): ColumnSet<C> = toColumnAccessor() then other

    public infix fun <C> ColumnSet<C>.then(other: KProperty<C>): ColumnSet<C> = this then other.toColumnAccessor()

    public infix fun <C> KProperty<C>.then(other: KProperty<C>): ColumnSet<C> = toColumnAccessor() then other.toColumnAccessor()

    public infix fun <C> KProperty<C>.then(other: String): ColumnSet<C> = toColumnAccessor() then other.toColumnOf()

    public infix fun <C> String.then(other: KProperty<C>): ColumnSet<C> = toColumnOf<C>() then other.toColumnAccessor()
}

public interface Pivot<T> : Aggregatable<T>

public typealias PivotColumnsSelector<T, C> = Selector<PivotDsl<T>, ColumnSet<C>>

// region df.pivot { }

public fun <T> DataFrame<T>.pivot(inward: Boolean? = null, columns: PivotColumnsSelector<T, *>): Pivot<T> = PivotImpl(this, columns, inward)
public fun <T> DataFrame<T>.pivot(vararg columns: String, inward: Boolean? = null): Pivot<T> = pivot(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: Column, inward: Boolean? = null): Pivot<T> = pivot(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: KProperty<*>, inward: Boolean? = null): Pivot<T> = pivot(inward) { columns.toColumns() }

public fun <T> DataFrame<T>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).groupByOther().matches()
public fun <T> DataFrame<T>.pivotMatches(vararg columns: String, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotMatches(vararg columns: Column, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }

public fun <T> DataFrame<T>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).groupByOther().count()
public fun <T> DataFrame<T>.pivotCounts(vararg columns: String, inward: Boolean = true): DataFrame<T> = pivotCounts(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotCounts(vararg columns: Column, inward: Boolean = true): DataFrame<T> = pivotCounts(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotCounts(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> = pivotCounts(inward) { columns.toColumns() }

// endregion

// region groupBy { }.pivot { }

public fun <G> GroupBy<*, G>.pivot(inward: Boolean = true, columns: ColumnsSelector<G, *>): PivotGroupBy<G> = PivotGroupByImpl(this, columns, inward)
public fun <G> GroupBy<*, G>.pivot(vararg columns: Column, inward: Boolean = true): PivotGroupBy<G> = pivot(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivot(vararg columns: String, inward: Boolean = true): PivotGroupBy<G> = pivot(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivot(vararg columns: KProperty<*>, inward: Boolean = true): PivotGroupBy<G> = pivot(inward) { columns.toColumns() }

public fun <G> GroupBy<*, G>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> = pivot(inward, columns).matches()
public fun <G> GroupBy<*, G>.pivotMatches(vararg columns: String, inward: Boolean = true): DataFrame<G> = pivotMatches(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivotMatches(vararg columns: Column, inward: Boolean = true): DataFrame<G> = pivotMatches(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<G> = pivotMatches(inward) { columns.toColumns() }

public fun <G> GroupBy<*, G>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> = pivot(inward, columns).count()
public fun <G> GroupBy<*, G>.pivotCounts(vararg columns: String, inward: Boolean = true): DataFrame<G> = pivotCounts(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivotCounts(vararg columns: Column, inward: Boolean = true): DataFrame<G> = pivotCounts(inward) { columns.toColumns() }
public fun <G> GroupBy<*, G>.pivotCounts(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<G> = pivotCounts(inward) { columns.toColumns() }

// endregion

// region groupBy.aggregate { pivot { } }

public fun <T> AggregateGroupedDsl<T>.pivot(inward: Boolean = true, columns: ColumnsSelector<T, *>): PivotGroupBy<T> =
    PivotInAggregateImpl(this, columns, inward)
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: String, inward: Boolean = true): PivotGroupBy<T> = pivot(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: Column, inward: Boolean = true): PivotGroupBy<T> = pivot(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: KProperty<*>, inward: Boolean = true): PivotGroupBy<T> = pivot(inward) { columns.toColumns() }

public fun <T> AggregateGroupedDsl<T>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).matches()
public fun <T> AggregateGroupedDsl<T>.pivotMatches(vararg columns: String, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivotMatches(vararg columns: Column, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }

public fun <T> AggregateGroupedDsl<T>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).matches()
public fun <T> AggregateGroupedDsl<T>.pivotCounts(vararg columns: String, inward: Boolean = true): DataFrame<T> = pivotCounts(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivotCounts(vararg columns: Column, inward: Boolean = true): DataFrame<T> = pivotCounts(inward) { columns.toColumns() }
public fun <T> AggregateGroupedDsl<T>.pivotCounts(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> = pivotCounts(inward) { columns.toColumns() }

// endregion
