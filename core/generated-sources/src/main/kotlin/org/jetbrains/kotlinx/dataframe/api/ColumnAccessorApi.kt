package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API

@Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
public inline fun <reified T> ColumnAccessor<T>.nullable(): ColumnAccessor<T?> = cast()
