package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.corrImpl
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

internal fun AnyCol.isSuitableForCorr() = isSubtypeOf<Number>() || type() == typeOf<Boolean>()

// region DataFrame

public data class Corr<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
)

public fun <T> DataFrame<T>.corr(): DataFrame<T> = corr { cols { it.isSuitableForCorr() }.recursively() }.withItself()

public fun <T, C> DataFrame<T>.corr(columns: ColumnsSelector<T, C>): Corr<T, C> = Corr(this, columns)
public fun <T> DataFrame<T>.corr(vararg columns: String): Corr<T, Any?> = corr { columns.toColumnSet() }
public fun <T, C> DataFrame<T>.corr(vararg columns: KProperty<C>): Corr<T, C> = corr { columns.toColumnSet() }
public fun <T, C> DataFrame<T>.corr(vararg columns: ColumnReference<C>): Corr<T, C> = corr { columns.toColumnSet() }

public fun <T, C, R> Corr<T, C>.with(otherColumns: ColumnsSelector<T, R>): DataFrame<T> = corrImpl(otherColumns)
public fun <T, C> Corr<T, C>.with(vararg otherColumns: String): DataFrame<T> = with { otherColumns.toColumnSet() }
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: KProperty<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: ColumnReference<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

public fun <T, C> Corr<T, C>.withItself(): DataFrame<T> = with(columns)

// endregion
