package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.implodeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public fun <T, C> DataFrame<T>.implode(dropNulls: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> =
    implodeImpl(dropNulls, columns)

public fun <T> DataFrame<T>.implode(vararg columns: String, dropNulls: Boolean = false): DataFrame<T> =
    implode(dropNulls) { columns.toColumns() }

public fun <T, C> DataFrame<T>.implode(vararg columns: ColumnReference<C>, dropNulls: Boolean = false): DataFrame<T> =
    implode(dropNulls) { columns.toColumns() }

public fun <T, C> DataFrame<T>.implode(vararg columns: KProperty<C>, dropNulls: Boolean = false): DataFrame<T> =
    implode(dropNulls) { columns.toColumns() }
