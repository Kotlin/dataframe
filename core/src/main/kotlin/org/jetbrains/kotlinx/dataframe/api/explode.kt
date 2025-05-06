package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.explodeImpl
import kotlin.reflect.KProperty

private val defaultExplodeColumns: ColumnsSelector<*, *> = {
    colsAtAnyDepth().filter { it.isList() || it.isFrameColumn() }
}

// region explode DataFrame
@Refine
@Interpretable("Explode0")
public fun <T> DataFrame<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = defaultExplodeColumns,
): DataFrame<T> = explodeImpl(dropEmpty, selector)

public fun <T> DataFrame<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataFrame<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataFrame<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

// endregion

// region explode DataRow

public fun <T> DataRow<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = defaultExplodeColumns,
): DataFrame<T> = toDataFrame().explode(dropEmpty, selector)

public fun <T> DataRow<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataRow<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataRow<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumnSet() }

// endregion

// region explode DataColumn

@JvmName("explodeList")
public fun <T> DataColumn<Collection<T>>.explode(): DataColumn<T> = explodeImpl() as DataColumn<T>

@JvmName("explodeFrames")
public fun <T> DataColumn<DataFrame<T>>.explode(): ColumnGroup<T> = concat().asColumnGroup(name())

// endregion
