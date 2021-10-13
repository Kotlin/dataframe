package org.jetbrains.kotlinx.dataframe.impl.columns.missing

import org.jetbrains.kotlinx.dataframe.columns.ValueColumn

internal class MissingValueColumn<T> : MissingDataColumn<T>(), ValueColumn<T> {

    override fun distinct() = throw UnsupportedOperationException()
}
