package org.jetbrains.dataframe.columns

import kotlin.reflect.KType

public interface TypedColumn<out T> : ColumnReference<T> {

    public fun type(): KType
}
