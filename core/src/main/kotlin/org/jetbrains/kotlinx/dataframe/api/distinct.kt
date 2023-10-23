package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.ITERABLE_COLUMNS_DEPRECATION_MESSAGE
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.distinct(): DataFrame<T> = distinctBy { all() }

public fun <T, C> DataFrame<T>.distinct(columns: ColumnsSelector<T, C>): DataFrame<T> = select(columns).distinct()

public fun <T> DataFrame<T>.distinct(vararg columns: KProperty<*>): DataFrame<T> = distinct {
    val set = columns.toColumnSet()
    set
}

public fun <T> DataFrame<T>.distinct(vararg columns: String): DataFrame<T> = distinct { columns.toColumnSet() }

public fun <T> DataFrame<T>.distinct(vararg columns: AnyColumnReference): DataFrame<T> =
    distinct { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "distinct { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
@JvmName("distinctT")
public fun <T> DataFrame<T>.distinct(columns: Iterable<String>): DataFrame<T> = distinct { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "distinct { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.distinct(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    distinct { columns.toColumnSet() }

public fun <T> DataFrame<T>.distinctBy(vararg columns: KProperty<*>): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

public fun <T> DataFrame<T>.distinctBy(vararg columns: String): DataFrame<T> = distinctBy { columns.toColumnSet() }

public fun <T> DataFrame<T>.distinctBy(vararg columns: AnyColumnReference): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "distinctBy { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
@JvmName("distinctByT")
public fun <T> DataFrame<T>.distinctBy(columns: Iterable<String>): DataFrame<T> = distinctBy { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "distinctBy { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.distinctBy(columns: Iterable<AnyColumnReference>): DataFrame<T> =
    distinctBy { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.distinctBy(columns: ColumnsSelector<T, C>): DataFrame<T> {
    val cols = get(columns)
    val distinctIndices = indices.distinctBy { i -> cols.map { it[i] } }
    return this[distinctIndices]
}

// endregion
