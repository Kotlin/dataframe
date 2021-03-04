package org.jetbrains.dataframe.columns

import org.jetbrains.dataframe.DataFrame

interface NestedColumn<out T> {
    val df: DataFrame<T>
}