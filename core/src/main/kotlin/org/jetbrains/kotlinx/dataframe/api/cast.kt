package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.api.ExtraColumns
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import kotlin.reflect.typeOf

public fun <T> AnyFrame.cast(): DataFrame<T> = this as DataFrame<T>

public inline fun <reified T> AnyFrame.cast(verify: Boolean = true): DataFrame<T> = if (verify) convertToImpl(typeOf<T>(), allowConversion = false, org.jetbrains.kotlinx.dataframe.impl.api.ExtraColumns.Keep)
else cast()

public fun <T> AnyRow.cast(): DataRow<T> = this as DataRow<T>

public inline fun <reified T> AnyRow.cast(verify: Boolean = true): DataRow<T> = df().cast<T>(verify)[0]

public fun <T> AnyCol.cast(): DataColumn<T> = this as DataColumn<T>

public fun <T> ValueColumn<*>.cast(): ValueColumn<T> = this as ValueColumn<T>

public fun <T> FrameColumn<*>.castFrameColumn(): FrameColumn<T> = this as FrameColumn<T>

public fun <T> ColumnGroup<*>.cast(): ColumnGroup<T> = this as ColumnGroup<T>

public fun <T> ColumnWithPath<*>.cast(): ColumnWithPath<T> = this as ColumnWithPath<T>

public fun <T> ColumnAccessor<*>.cast(): ColumnAccessor<T> = this as ColumnAccessor<T>

public fun <C> ColumnSet<*>.cast(): ColumnSet<C> = this as ColumnSet<C>

public fun <C> ColumnReference<*>.cast(): ColumnReference<C> = this as ColumnReference<C>

public fun <C> SingleColumn<*>.cast(): SingleColumn<C> = this as SingleColumn<C>
