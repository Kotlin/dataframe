package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.and
import org.jetbrains.kotlinx.dataframe.impl.api.PivotChainColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.getType
import kotlin.reflect.KProperty
import kotlin.reflect.KType

// region pivot

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

public typealias PivotColumnsSelector<T, C> = Selector<PivotDsl<T>, ColumnSet<C>>

public fun <T> DataFrame<T>.pivot(inward: Boolean? = null, columns: PivotColumnsSelector<T, *>): Pivot<T> = PivotImpl(this, columns, inward)
public fun <T> DataFrame<T>.pivot(vararg columns: String, inward: Boolean? = null): Pivot<T> = pivot(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: Column, inward: Boolean? = null): Pivot<T> = pivot(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: KProperty<*>, inward: Boolean? = null): Pivot<T> = pivot(inward) { columns.toColumns() }

public fun <T> DataFrame<T>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).groupByOther().matches()
public fun <T> DataFrame<T>.pivotMatches(vararg columns: String, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotMatches(vararg columns: Column, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> = pivotMatches(inward) { columns.toColumns() }

public fun <T> DataFrame<T>.pivotCount(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> = pivot(inward, columns).groupByOther().count()
public fun <T> DataFrame<T>.pivotCount(vararg columns: String, inward: Boolean = true): DataFrame<T> = pivotCount(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotCount(vararg columns: Column, inward: Boolean = true): DataFrame<T> = pivotCount(inward) { columns.toColumns() }
public fun <T> DataFrame<T>.pivotCount(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> = pivotCount(inward) { columns.toColumns() }

// endregion

// region gather

public fun <T, C> DataFrame<T>.gather(selector: ColumnsSelector<T, C>): Gather<T, C, String, C> = Gather(
    this, selector, null, getType<String>(),
    { it }, null
)
public fun <T> DataFrame<T>.gather(vararg columns: String): Gather<T, Any?, String, Any?> = gather { columns.toColumns() }
public fun <T, C> DataFrame<T>.gather(vararg columns: ColumnReference<C>): Gather<T, C, String, C> = gather { columns.toColumns() }
public fun <T, C> DataFrame<T>.gather(vararg columns: KProperty<C>): Gather<T, C, String, C> = gather { columns.toColumns() }

public fun <T, C, K, R> Gather<T, C, K, R>.where(filter: Predicate<C>): Gather<T, C, K, R> = copy(filter = this.filter and filter)
public fun <T, C, K, R> Gather<T, C?, K, R>.notNull(): Gather<T, C, K, R> = where { it != null } as Gather<T, C, K, R>

public fun <T, C, K, R> Gather<T, C, K, R>.explodeLists(): Gather<T, C, K, R> = copy(explode = true)

public inline fun <T, C, reified K, R> Gather<T, C, *, R>.mapKeys(noinline transform: (String) -> K): Gather<T, C, K, R> =
    copy(keyTransform = transform as ((String) -> Nothing), keyType = getType<K>()) as Gather<T, C, K, R>

public fun <T, C, K, R> Gather<T, C, K, *>.mapValues(transform: (C) -> R): Gather<T, C, K, R> =
    copy(valueTransform = transform as ((C) -> Nothing)) as Gather<T, C, K, R>

public data class Gather<T, C, K, R>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
    internal val filter: ((C) -> Boolean)? = null,
    internal val keyType: KType? = null,
    internal val keyTransform: ((String) -> K),
    internal val valueTransform: ((C) -> R)? = null,
    internal val explode: Boolean = false
) {
    public fun <P> cast(): Gather<T, P, K, P> {
        // TODO: introduce GatherWithTransform to avoid this error
        require(valueTransform == null) { "Cast is not allowed to be called after `mapValues`" }
        return this as Gather<T, P, K, P>
    }
}

public fun <T, C, K, R> Gather<T, C, K, R>.into(
    keyColumn: String,
    valueColumn: String
): DataFrame<T> = gatherImpl(keyColumn, valueColumn)

public fun <T, C, K, R> Gather<T, C, K, R>.into(
    keyColumn: ColumnAccessor<K>,
    valueColumn: ColumnAccessor<R>
): DataFrame<T> = into(keyColumn.name(), valueColumn.name)

public fun <T, C, K, R> Gather<T, C, K, R>.into(
    keyColumn: KProperty<K>,
    valueColumn: KProperty<R>
): DataFrame<T> = into(keyColumn.columnName, valueColumn.columnName)

public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(
    keyColumn: String
): DataFrame<T> = gatherImpl(keyColumn, null)

public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(
    keyColumn: ColumnAccessor<K>
): DataFrame<T> = keysInto(keyColumn.name())

public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(
    keyColumn: KProperty<K>
): DataFrame<T> = keysInto(keyColumn.columnName)

public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(
    valueColumn: String
): DataFrame<T> = gatherImpl(null, valueColumn)

public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(
    valueColumn: ColumnAccessor<K>
): DataFrame<T> = valuesInto(valueColumn.name())

public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(
    valueColumn: KProperty<K>
): DataFrame<T> = valuesInto(valueColumn.columnName)

// endregion
