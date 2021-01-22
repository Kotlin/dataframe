package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.api.columns.GroupedCol
import org.jetbrains.dataframe.api.columns.ValueCol
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class ValueImplCol<T>(values: List<T>, name: String, type: KType, val defaultValue: T? = null, set: Set<T>? = null)
    : DataColImpl<T>(values, name, type, set), ValueCol<T> {

    override fun distinct() = ValueImplCol(toSet().toList(), name, type, defaultValue, valuesSet)

    override fun rename(newName: String) = ValueImplCol(values, newName, type, defaultValue, valuesSet)

    override fun addParent(parent: GroupedCol<*>): DataCol<T> = ValueWithParentImplCol(parent, this)

    override fun createWithValues(values: List<T>, hasNulls: Boolean?): DataCol<T> {
        val nulls = hasNulls ?: values.any { it == null}
        return DataCol.create(name, values, type.withNullability(nulls))
    }

    override fun defaultValue() = defaultValue

    override fun changeType(type: KType) = DataCol.create(name, values, type, defaultValue)
}