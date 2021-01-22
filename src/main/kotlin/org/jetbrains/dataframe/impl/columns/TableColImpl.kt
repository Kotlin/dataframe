package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.api.columns.GroupedCol
import org.jetbrains.dataframe.api.columns.TableCol
import org.jetbrains.dataframe.createType
import java.lang.Exception
import java.lang.UnsupportedOperationException
import kotlin.reflect.KType

internal class TableColImpl<T> constructor(override val df: DataFrame<T>, name: String, values: List<DataFrame<T>>)
    : DataColImpl<DataFrame<T>>(values, name, createType<DataFrame<*>>()), TableCol<T> {

    constructor(name: String, df: DataFrame<T>, startIndices: List<Int>) : this(df, name, df.splitByIndices(startIndices))

    init {
        if(values.any{it == null})
            throw Exception()
    }

    override fun rename(newName: String) = TableColImpl(df, newName, values)

    override fun defaultValue() = null

    override fun addParent(parent: GroupedCol<*>) = TableWithParentCol(parent, this)

    override fun createWithValues(values: List<DataFrame<T>>, hasNulls: Boolean?): DataCol<DataFrame<T>> {
        return DataCol.createTable(name, values, values.getBaseSchema())
    }

    override fun changeType(type: KType) = throw UnsupportedOperationException()
}