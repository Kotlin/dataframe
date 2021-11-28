package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.explodeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region explode DataFrame

public fun <T> DataFrame<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = { all() }
): DataFrame<T> = explodeImpl(dropEmpty, selector)

public fun <T> DataFrame<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

public fun <T, C> DataFrame<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

public fun <T, C> DataFrame<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

// endregion

// region explode DataRow

public fun <T> DataRow<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = { all() }
): DataFrame<T> = toDataFrame().explode(dropEmpty, selector)

public fun <T> DataRow<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

public fun <T, C> DataRow<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

public fun <T, C> DataRow<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

// endregion

// region explode DataColumn

@JvmName("explodeList")
public fun <T> DataColumn<Collection<T>>.explode(): DataColumn<T> = explodeImpl() as DataColumn<T>

@JvmName("explodeFrames")
public fun <T> DataColumn<DataFrame<T>>.explode(): ColumnGroup<T> = concat().toColumnGroup(name())

// endregion
