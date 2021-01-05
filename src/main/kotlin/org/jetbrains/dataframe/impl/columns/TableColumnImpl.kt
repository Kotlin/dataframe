package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.api.columns.TableColumn
import org.jetbrains.dataframe.createType
import java.lang.UnsupportedOperationException
import kotlin.reflect.KType

internal class TableColumnImpl<T> constructor(override val df: DataFrame<T>, name: String, values: List<DataFrame<T>>)
    : ColumnDataImpl<DataFrame<T>>(values, name, createType<DataFrame<*>>()), TableColumn<T> {

    constructor(name: String, df: DataFrame<T>, startIndices: List<Int>) : this(df, name, df.splitByIndices(startIndices))

    override fun rename(newName: String) = TableColumnImpl(df, newName, values)

    override fun defaultValue() = null

    override fun addParent(parent: GroupedColumn<*>) = TableColumnWithParent(parent, this)

    override fun createWithValues(values: List<DataFrame<T>>, hasNulls: Boolean?): ColumnData<DataFrame<T>> {
        return ColumnData.createTable(name, values, values.getBaseSchema())
    }

    override fun changeType(type: KType) = throw UnsupportedOperationException()
}