package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.CreateDataFrameDsl
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

@PublishedApi
internal fun <T> DataColumn<T>.unfoldImpl(type: KType, body: CreateDataFrameDsl<T>.() -> Unit): AnyCol =
    when (kind()) {
        ColumnKind.Group, ColumnKind.Frame -> this

        else -> when (val traversed = traversedType(type)) {
            null -> this

            else -> values()
                .createDataFrameImpl(traversed) { (this as CreateDataFrameDsl<T>).body() }
                .asColumnGroup(name())
                .asDataColumn()
        }
    }

/**
 * [<code>type</code>][type] is the static type of the column at the call site. It decides which properties are read,
 * unless it cannot be unfolded itself (`Any?` of an untyped column): then the type of the column is used.
 * `null` when neither of them can be unfolded, so the column stays as it is.
 * Deciding and reading by one type unfolds a `DataColumn<Student>` even when the type of the column is `Any`.
 */
private fun AnyCol.traversedType(type: KType): KType? =
    when {
        (type.classifier as? KClass<*>)?.canBeUnfolded == true -> type
        typeClass.canBeUnfolded -> type()
        else -> null
    }
