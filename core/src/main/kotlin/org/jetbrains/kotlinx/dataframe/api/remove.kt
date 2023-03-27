package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.util.ITERABLE_COLUMNS_DEPRECATION_MESSAGE
import kotlin.reflect.KProperty

// region DataFrame

// region remove

public fun <T> DataFrame<T>.remove(columns: ColumnsSelector<T, *>): DataFrame<T> =
    removeImpl(allowMissingColumns = true, columns = columns).df

public fun <T> DataFrame<T>.remove(vararg columns: KProperty<*>): DataFrame<T> = remove { columns.toColumnSet() }

public fun <T> DataFrame<T>.remove(vararg columns: String): DataFrame<T> = remove { columns.toColumnSet() }

public fun <T> DataFrame<T>.remove(vararg columns: AnyColumnReference): DataFrame<T> = remove { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "remove { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet"
    ),
    level = DeprecationLevel.ERROR
)
public fun <T> DataFrame<T>.remove(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    remove { columns.toColumnSet() }

// endregion

// region minus

public infix operator fun <T> DataFrame<T>.minus(columns: ColumnsSelector<T, *>): DataFrame<T> = remove(columns)

public infix operator fun <T> DataFrame<T>.minus(columns: KProperty<*>): DataFrame<T> = remove(columns)

public infix operator fun <T> DataFrame<T>.minus(column: String): DataFrame<T> = remove(column)

public infix operator fun <T> DataFrame<T>.minus(column: AnyColumnReference): DataFrame<T> = remove(column)

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "minus { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet"
    ),
    level = DeprecationLevel.ERROR
)
public infix operator fun <T> DataFrame<T>.minus(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    minus { columns.toColumnSet() }

// endregion

// endregion
