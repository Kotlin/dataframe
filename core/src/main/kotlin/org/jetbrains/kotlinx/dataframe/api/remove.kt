package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region DataFrame

// region remove

public fun <T> DataFrame<T>.remove(columns: ColumnsSelector<T, *>): DataFrame<T> =
    removeImpl(allowMissingColumns = true, columns = columns).df

public fun <T> DataFrame<T>.remove(vararg columns: KProperty<*>): DataFrame<T> = remove { columns.toColumns() }

public fun <T> DataFrame<T>.remove(vararg columns: String): DataFrame<T> = remove { columns.toColumns() }

public fun <T> DataFrame<T>.remove(vararg columns: AnyColumnReference): DataFrame<T> = remove { columns.toColumns() }

@Deprecated(
    message = "It will be removed in the 0.10.0 release",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("remove(columns)")
)
public fun <T> DataFrame<T>.remove(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    remove { columns.toColumnSet() }

// endregion

// region infix minus

public infix operator fun <T> DataFrame<T>.minus(columns: ColumnsSelector<T, *>): DataFrame<T> = remove(columns)

public infix operator fun <T> DataFrame<T>.minus(column: String): DataFrame<T> = remove(column)

public infix operator fun <T> DataFrame<T>.minus(column: KProperty<*>): DataFrame<T> = remove(column)

public infix operator fun <T> DataFrame<T>.minus(column: AnyColumnReference): DataFrame<T> = remove(column)

@Deprecated(
    message = "It will be removed in the 0.10.0 release",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("remove(columns)")
)
public infix operator fun <T> DataFrame<T>.minus(columns: Iterable<AnyColumnReference>): DataFrame<T> = remove(columns)

// endregion

// endregion
