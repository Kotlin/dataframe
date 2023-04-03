package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.explodeImpl
import kotlin.reflect.KProperty

private val defaultExplodeColumns: ColumnsSelector<*, *> = { cols { it.isList() || it.isFrameColumn() }.recursively() }

// region explode DataFrame

public fun <T> DataFrame<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = defaultExplodeColumns
): DataFrame<T> = explodeImpl(dropEmpty, selector)

public fun <T> DataFrame<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

// endregion

// region explode DataRow

public fun <T> DataRow<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = defaultExplodeColumns
): DataFrame<T> = toDataFrame().explode(dropEmpty, selector)

public fun <T> DataRow<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

public fun <T, C> DataRow<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

public fun <T, C> DataRow<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

// endregion

// region explode DataColumn

@JvmName("explodeList")
public fun <T> DataColumn<Collection<T>>.explode(): DataColumn<T> = explodeImpl() as DataColumn<T>

@JvmName("explodeFrames")
public fun <T> DataColumn<DataFrame<T>>.explode(): ColumnGroup<T> = concat().asColumnGroup(name())

// endregion
