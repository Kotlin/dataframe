package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.columns.DataColumn
import org.jetbrains.dataframe.columns.MapColumn
import org.jetbrains.dataframe.columns.ValueColumn
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class ValueImplColumn<T>(values: List<T>, name: String, type: KType, val defaultValue: T? = null, set: Set<T>? = null)
    : DataColumnImpl<T>(values, name, type, set), ValueColumn<T> {

    override fun distinct() = ValueImplColumn(toSet().toList(), name, type, defaultValue, valuesSet)

    override fun rename(newName: String) = ValueImplColumn(values, newName, type, defaultValue, valuesSet)

    override fun addParent(parent: MapColumn<*>): DataColumn<T> = ValueWithParentImplColumn(parent, this)

    override fun createWithValues(values: List<T>, hasNulls: Boolean?): DataColumn<T> {
        val nulls = hasNulls ?: values.any { it == null}
        return DataColumn.create(name, values, type.withNullability(nulls))
    }

    override fun defaultValue() = defaultValue

    override fun changeType(type: KType) = DataColumn.create(name, values, type, defaultValue)
}