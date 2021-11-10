package org.jetbrains.kotlinx.dataframe.impl.columns.missing

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

internal class MissingFrameColumn<T> : MissingDataColumn<DataFrame<T>>(), FrameColumn<T> {

    override fun kind() = super.kind()

    override fun distinct() = throw UnsupportedOperationException()

    override val schema: Lazy<DataFrameSchema>
        get() = throw UnsupportedOperationException()
}
