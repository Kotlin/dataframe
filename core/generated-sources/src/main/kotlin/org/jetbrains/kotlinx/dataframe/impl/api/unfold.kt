package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.CreateDataFrameDsl
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KType

@PublishedApi
internal fun <T> DataColumn<T>.unfoldImpl(type: KType, body: CreateDataFrameDsl<T>.() -> Unit): AnyCol =
    when (kind()) {
        ColumnKind.Group, ColumnKind.Frame -> this

        else -> when {
            !typeClass.canBeUnfolded -> this

            else -> values()
                .createDataFrameImpl(type) { (this as CreateDataFrameDsl<T>).body() }
                .asColumnGroup(name())
                .asDataColumn()
        }
    }
