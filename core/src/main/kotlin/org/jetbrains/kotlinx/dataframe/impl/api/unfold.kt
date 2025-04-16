package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.CreateDataFrameDsl
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.typeClass

@PublishedApi
internal fun <T> DataColumn<T>.unfoldImpl(body: CreateDataFrameDsl<T>.() -> Unit): AnyCol =
    when (kind()) {
        ColumnKind.Group, ColumnKind.Frame -> this

        else -> when {
            !typeClass.canBeUnfolded -> this

            else -> values()
                .createDataFrameImpl(typeClass) { (this as CreateDataFrameDsl<T>).body() }
                .asColumnGroup(name())
                .asDataColumn()
        }
    }
