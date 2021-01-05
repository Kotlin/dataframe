package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.ValueColumn
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class ValueColumnImpl<T>(values: List<T>, name: String, type: KType, val defaultValue: T? = null, set: Set<T>? = null)
    : ColumnDataImpl<T>(values, name, type, set), ValueColumn<T> {

    override fun distinct() = ValueColumnImpl(toSet().toList(), name, type, defaultValue, valuesSet)

    override fun rename(newName: String) = ValueColumnImpl(values, newName, type, defaultValue, valuesSet)

    override fun addParent(parent: GroupedColumn<*>): ColumnData<T> = ValueColumnWithParentImpl(parent, this)

    override fun createWithValues(values: List<T>, hasNulls: Boolean?): ColumnData<T> {
        val nulls = hasNulls ?: values.any { it == null}
        return ColumnData.create(name, values, type.withNullability(nulls))
    }

    override fun defaultValue() = defaultValue

    override fun changeType(type: KType) = ColumnData.create(name, values, type, defaultValue)
}