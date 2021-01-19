package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.GroupedColumn
import org.jetbrains.dataframe.createType
import java.lang.UnsupportedOperationException
import kotlin.reflect.KType

internal class GroupedColumnImpl<T>(override val df: DataFrame<T>, val name: String) : GroupedColumn<T>, ColumnDataInternal<DataRow<T>>, DataFrame<T> by df {

    override val values: Iterable<DataRow<T>>
        get() = df.rows()

    override val ndistinct: Int
        get() = distinct.nrow()

    override val type by lazy { createType<DataRow<*>>() }

    private val distinct by lazy { df.distinct() }

    private val set by lazy { distinct.rows().toSet() }

    override fun toSet() = set

    override val size: Int
        get() = df.nrow()

    override fun get(index: Int) = df[index]

    override fun slice(range: IntRange) = GroupedColumnImpl(df[range], name)

    override fun rename(newName: String) = GroupedColumnImpl(df, newName)

    override fun defaultValue() = null

    override fun slice(indices: Iterable<Int>) = withDf(df[indices])

    override fun slice(mask: BooleanArray) = withDf(df.getRows(mask))

    override fun addParent(parent: GroupedColumn<*>) = GroupedColumnWithParent(parent, this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val g = other as? GroupedColumn<*> ?: return false
        return name == g.name() && df == other.df
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + df.hashCode()
    }

    override fun tryGetColumn(columnName: String) = df.tryGetColumn(columnName)

    override fun toString() = "$name: {${renderSchema(df)}}"

    override fun changeType(type: KType) = throw UnsupportedOperationException()

    override fun name() = name
}