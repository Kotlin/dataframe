package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region gather

@Interpretable("Gather0")
public fun <T, C> DataFrame<T>.gather(selector: ColumnsSelector<T, C>): Gather<T, C, String, C> =
    Gather(
        df = this,
        columns = selector,
        filter = null,
        keyType = typeOf<String>(),
        keyTransform = { it },
        valueTransform = null,
    )

public fun <T> DataFrame<T>.gather(vararg columns: String): Gather<T, Any?, String, Any?> =
    gather { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.gather(vararg columns: ColumnReference<C>): Gather<T, C, String, C> =
    gather { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.gather(vararg columns: KProperty<C>): Gather<T, C, String, C> =
    gather { columns.toColumnSet() }

// endregion

@Interpretable("GatherWhere")
public fun <T, C, K, R> Gather<T, C, K, R>.where(filter: RowValueFilter<T, C>): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = this.filter and filter,
        keyType = keyType,
        keyTransform = keyTransform,
        valueTransform = valueTransform,
        explode = explode,
    )

@Interpretable("GatherChangeType")
public fun <T, C, K, R> Gather<T, C?, K, R>.notNull(): Gather<T, C, K, R> = where { it != null } as Gather<T, C, K, R>

@Interpretable("GatherExplodeLists")
public fun <T, C, K, R> Gather<T, C, K, R>.explodeLists(): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = filter,
        keyType = keyType,
        keyTransform = keyTransform,
        valueTransform = valueTransform,
        explode = true,
    )

@Interpretable("GatherMap")
public inline fun <T, C, reified K, R> Gather<T, C, *, R>.mapKeys(
    noinline transform: (String) -> K,
): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = filter,
        keyType = typeOf<K>(),
        keyTransform = transform,
        valueTransform = valueTransform,
        explode = explode,
    )

@Interpretable("GatherMap")
public fun <T, C, K, R> Gather<T, C, K, *>.mapValues(transform: (C) -> R): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = filter,
        keyType = keyType,
        keyTransform = keyTransform,
        valueTransform = transform,
        explode = explode,
    )

public class Gather<T, C, K, R>(
    @PublishedApi
    internal val df: DataFrame<T>,
    @PublishedApi
    internal val columns: ColumnsSelector<T, C>,
    @PublishedApi
    internal val filter: RowValueFilter<T, C>? = null,
    @PublishedApi
    internal val keyType: KType? = null,
    @PublishedApi
    internal val keyTransform: ((String) -> K),
    @PublishedApi
    internal val valueTransform: ((C) -> R)? = null,
    @PublishedApi
    internal val explode: Boolean = false,
) {
    @Interpretable("GatherChangeType")
    public fun <P> cast(): Gather<T, P, K, P> {
        // TODO: introduce GatherWithTransform to avoid this error
        require(valueTransform == null) { "Cast is not allowed to be called after `mapValues`" }
        return this as Gather<T, P, K, P>
    }
}

// region into

@Refine
@Interpretable("GatherInto")
public fun <T, C, K, R> Gather<T, C, K, R>.into(keyColumn: String, valueColumn: String): DataFrame<T> =
    gatherImpl(keyColumn, valueColumn)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.into(
    keyColumn: ColumnAccessor<K>,
    valueColumn: ColumnAccessor<R>,
): DataFrame<T> = into(keyColumn.name(), valueColumn.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.into(keyColumn: KProperty<K>, valueColumn: KProperty<R>): DataFrame<T> =
    into(keyColumn.columnName, valueColumn.columnName)

// endregion

// region keysInto

@Refine
@Interpretable("GatherKeysInto")
public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(keyColumn: String): DataFrame<T> = gatherImpl(keyColumn, null)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(keyColumn: ColumnAccessor<K>): DataFrame<T> =
    keysInto(keyColumn.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(keyColumn: KProperty<K>): DataFrame<T> =
    keysInto(keyColumn.columnName)

// endregion

// region valuesInto

@Refine
@Interpretable("GatherValuesInto")
public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(valueColumn: String): DataFrame<T> = gatherImpl(null, valueColumn)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(valueColumn: ColumnAccessor<K>): DataFrame<T> =
    valuesInto(valueColumn.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(valueColumn: KProperty<K>): DataFrame<T> =
    valuesInto(valueColumn.columnName)

// endregion
