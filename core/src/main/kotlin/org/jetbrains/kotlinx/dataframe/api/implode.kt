package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.implodeImpl
import kotlin.reflect.KProperty

// region DataFrame

@Refine
@Interpretable("ImplodeDefault")
public fun <T> DataFrame<T>.implode(dropNA: Boolean = false): DataRow<T> = implode(dropNA) { all() }[0]

@Refine
@Interpretable("Implode")
public fun <T, C> DataFrame<T>.implode(dropNA: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> =
    implodeImpl(dropNA, columns)

public fun <T> DataFrame<T>.implode(vararg columns: String, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataFrame<T>.implode(vararg columns: ColumnReference<C>, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataFrame<T>.implode(vararg columns: KProperty<C>, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

// endregion
