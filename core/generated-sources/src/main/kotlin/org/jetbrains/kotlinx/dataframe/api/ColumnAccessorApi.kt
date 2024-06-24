package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor

public inline fun <reified T> ColumnAccessor<T>.nullable(): ColumnAccessor<T?> = cast()
