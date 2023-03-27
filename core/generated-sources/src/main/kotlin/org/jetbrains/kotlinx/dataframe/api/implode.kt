package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.implodeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import kotlin.reflect.KProperty

public fun <T> DataFrame<T>.implode(dropNA: Boolean = false): DataRow<T> =
    implode(dropNA) { all() }[0]

public fun <T, C> DataFrame<T>.implode(dropNA: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> =
    implodeImpl(dropNA, columns)

public fun <T> DataFrame<T>.implode(vararg columns: String, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.implode(vararg columns: ColumnReference<C>, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.implode(vararg columns: KProperty<C>, dropNA: Boolean = false): DataFrame<T> =
    implode(dropNA) { columns.toColumnSet() }
