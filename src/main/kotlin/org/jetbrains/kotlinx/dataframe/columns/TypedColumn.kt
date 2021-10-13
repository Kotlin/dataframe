package org.jetbrains.kotlinx.dataframe.columns

import kotlin.reflect.KType

public interface TypedColumn<out T> : ColumnReference<T> {

    public fun type(): KType
}
