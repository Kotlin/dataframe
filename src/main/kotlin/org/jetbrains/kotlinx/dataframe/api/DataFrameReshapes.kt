package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.MapColumnReference
import org.jetbrains.kotlinx.dataframe.PivotedDataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateReceiver
import org.jetbrains.kotlinx.dataframe.aggregation.GroupByReceiver
import org.jetbrains.kotlinx.dataframe.aggregation.PivotReceiver
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.getType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.DataFramePivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ValueWithDefault
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.toColumnPath
import org.jetbrains.kotlinx.dataframe.pathOf
import kotlin.reflect.KType

// region pivot

public fun <T> DataFrame<T>.pivot(columns: ColumnsSelector<T, *>): PivotedDataFrame<T> = DataFramePivotImpl(this, columns)
public fun <T> DataFrame<T>.pivot(vararg columns: String): PivotedDataFrame<T> = pivot { columns.toColumns() }
public fun <T> DataFrame<T>.pivot(vararg columns: Column): PivotedDataFrame<T> = pivot { columns.toColumns() }

public fun <T, P : GroupedPivot<T>> P.withGrouping(group: MapColumnReference): P = withGrouping(group.path()) as P
public fun <T, P : GroupedPivot<T>> P.withGrouping(groupName: String): P = withGrouping(pathOf(groupName)) as P

public typealias AggregateBody<T, R> = AggregateReceiver<T>.(AggregateReceiver<T>) -> R

public typealias PivotAggregateBody<T, R> = PivotReceiver<T>.(PivotReceiver<T>) -> R

public data class ValueWithName(val value: Any?, val name: String)

@Suppress("DataClassPrivateConstructor")
public data class NamedValue private constructor(
    val path: ColumnPath,
    val value: Any?,
    val type: KType?,
    var default: Any?,
    val guessType: Boolean = false
) {
    public companion object {
        public fun create(path: ColumnPath, value: Any?, type: KType?, defaultValue: Any?, guessType: Boolean = false): NamedValue = when (value) {
            is ValueWithDefault<*> -> create(path, value.value, type, value.default, guessType)
            is ValueWithName -> create(path.replaceLast(value.name).toColumnPath(), value.value, type, defaultValue, guessType)
            else -> NamedValue(path, value, type, defaultValue, guessType)
        }
        public fun aggregator(builder: GroupByReceiver<*>): NamedValue = NamedValue(emptyPath(), builder, null, null, false)
    }

    val name: String get() = path.last()
}

// endregion

// region gather

public data class GatherClause<T, C, K, R>(
    val df: DataFrame<T>,
    val selector: ColumnsSelector<T, C>,
    val filter: ((C) -> Boolean)? = null,
    val dropNulls: Boolean = true,
    val nameTransform: ((String) -> K),
    val valueTransform: ((C) -> R)? = null
)

public fun <T, C> DataFrame<T>.gather(dropNulls: Boolean = true, selector: ColumnsSelector<T, C?>): GatherClause<T, C, String, C> = GatherClause<T, C, String, C>(this, selector as ColumnsSelector<T, C>, null, dropNulls, { it }, null)

public fun <T, C, K, R> GatherClause<T, C, K, R>.where(filter: Predicate<C>): GatherClause<T, C, K, R> = copy(filter = filter)

public fun <T, C, K, R> GatherClause<T, C, *, R>.mapNames(transform: (String) -> K): GatherClause<T, C, K, R> = GatherClause(df, selector, filter, dropNulls, transform, valueTransform)
public fun <T, C, K, R> GatherClause<T, C, K, *>.map(transform: (C) -> R): GatherClause<T, C, K, R> = GatherClause(df, selector, filter, dropNulls, nameTransform, transform)

public inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: ColumnReference<String>): DataFrame<T> = into(keyColumn.name())
public inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String): DataFrame<T> = gatherImpl(this, keyColumn, null, getType<K>(), getType<R>())
public inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String, valueColumn: String): DataFrame<T> = gatherImpl(this, keyColumn, valueColumn, getType<K>(), getType<R>())

// endregion
