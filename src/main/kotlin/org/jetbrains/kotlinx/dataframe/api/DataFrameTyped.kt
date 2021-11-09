package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.api.toListImpl
import org.jetbrains.kotlinx.dataframe.impl.api.typedImpl
import org.jetbrains.kotlinx.dataframe.impl.getType

// region cast

public fun <T> AnyFrame.cast(): DataFrame<T> = this as DataFrame<T>

public fun <T> AnyRow.cast(): DataRow<T> = this as DataRow<T>

public fun <T> AnyCol.cast(): DataColumn<T> = this as DataColumn<T>

public fun <T> ValueColumn<*>.cast(): ValueColumn<T> = this as ValueColumn<T>

public fun <T> FrameColumn<*>.castFrameColumn(): FrameColumn<T> = this as FrameColumn<T>

public fun <T> ColumnGroup<*>.cast(): ColumnGroup<T> = this as ColumnGroup<T>

public fun <T> ColumnWithPath<*>.cast(): ColumnWithPath<T> = this as ColumnWithPath<T>

// endregion

// region typed

public enum class ExtraColumnsBehavior { Remove, Keep, Fail }

public inline fun <reified T> AnyFrame.typed(
    allowConversion: Boolean = true,
    extraColumns: ExtraColumnsBehavior = ExtraColumnsBehavior.Remove
): DataFrame<T> = typedImpl(getType<T>(), allowConversion, extraColumns)

// endregion

public inline fun <reified T> DataFrame<T>.toList(): List<T> = toListImpl(getType<T>()) as List<T>

public inline fun <reified T> AnyFrame.toListOf(): List<T> = toListImpl(getType<T>()) as List<T>
