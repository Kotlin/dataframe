package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataFrameRow

internal class DataFrameRowImpl<T>(override var index: Int, override val owner: DataFrame<T>) : DataFrameRow<T> {

    override operator fun get(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
        return owner[name][index]
    }

    override val prev: DataFrameRow<T>?
        get() = if (index > 0) owner[index - 1] else null
    override val next: DataFrameRow<T>?
        get() = if (index < owner.nrow - 1) owner[index + 1] else null

    override fun getRow(index: Int): DataFrameRow<T>? = if (index >= 0 && index < owner.nrow) DataFrameRowImpl(index, owner) else null

    override val values by lazy { owner.columns.map { it[index] } }

    override fun get(columnIndex: Int): Any? {
        val column = owner.columns[columnIndex]
        ColumnAccessTracker.registerColumnAccess(column.name)
        return column[index]
    }

    override fun toString(): String {
        return "{ " + owner.columns.map { "${it.name}:${it[index]}" }.joinToString() + " }"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? DataFrameRow<T>
        if(o == null) return false
        return values.equals(o.values)
    }

    override fun hashCode() = values.hashCode()
}