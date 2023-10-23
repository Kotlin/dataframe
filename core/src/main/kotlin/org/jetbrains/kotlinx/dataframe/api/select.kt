package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.util.ITERABLE_COLUMNS_DEPRECATION_MESSAGE
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.select(columns: ColumnsSelector<T, *>): DataFrame<T> =
    get(columns).toDataFrame().cast()

public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select { columns.toColumnSet() }

public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select { columns.toColumnSet() }

public fun <T> DataFrame<T>.select(vararg columns: AnyColumnReference): DataFrame<T> = select { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "select { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
@JvmName("selectStringIterable")
public fun <T> DataFrame<T>.select(columns: Iterable<String>): DataFrame<T> =
    select { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "select { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
@JvmName("selectAnyColumnReferenceIterable")
public fun <T> DataFrame<T>.select(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    select { columns.toColumnSet() }

// endregion
