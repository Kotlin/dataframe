package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.api.createDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KProperty

public inline fun <reified T> DataColumn<T>.unfold(): AnyCol =
    when (kind()) {
        ColumnKind.Group, ColumnKind.Frame -> this
        else -> when {
            isPrimitive() -> this
            else -> values().createDataFrameImpl(typeClass) {
                (this as CreateDataFrameDsl<T>).properties()
            }.asColumnGroup(name()).asDataColumn()
        }
    }

public fun <T> DataFrame<T>.unfold(columns: ColumnsSelector<T, *>): DataFrame<T> = replace(columns).with { it.unfold() }
public fun <T> DataFrame<T>.unfold(vararg columns: String): DataFrame<T> = unfold { columns.toColumnSet() }
public fun <T> DataFrame<T>.unfold(vararg columns: KProperty<*>): DataFrame<T> = unfold { columns.toColumnSet() }
public fun <T> DataFrame<T>.unfold(vararg columns: AnyColumnReference): DataFrame<T> = unfold { columns.toColumnSet() }
