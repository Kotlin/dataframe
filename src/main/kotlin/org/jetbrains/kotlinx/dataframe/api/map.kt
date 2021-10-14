package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.getType
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformedColumnReference
import kotlin.reflect.KType

public inline fun <C, reified R> ColumnReference<C>.map(noinline transform: (C) -> R): ColumnReference<R> =
    map(getType<R>(), transform)

public fun <C, R> ColumnReference<C>.map(targetType: KType?, transform: (C) -> R): ColumnReference<R> =
    TransformedColumnReference(this, transform, targetType)
