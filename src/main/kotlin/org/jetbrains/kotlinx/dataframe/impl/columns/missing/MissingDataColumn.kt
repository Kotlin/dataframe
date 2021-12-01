package org.jetbrains.kotlinx.dataframe.impl.columns.missing

import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.DataColumnInternal
import kotlin.reflect.KType

internal abstract class MissingDataColumn<T> : DataColumnInternal<T> {

    override fun values() = throw UnsupportedOperationException()

    override fun type() = throw UnsupportedOperationException()

    override fun countDistinct() = throw UnsupportedOperationException()

    override fun size() = throw UnsupportedOperationException()

    override fun get(index: Int) = throw UnsupportedOperationException()

    override fun defaultValue() = throw UnsupportedOperationException()

    override fun get(columnName: String) = throw UnsupportedOperationException()

    override fun get(indices: Iterable<Int>) = throw UnsupportedOperationException()

    override fun toSet() = throw UnsupportedOperationException()

    override fun changeType(type: KType) = throw UnsupportedOperationException()

    override fun rename(newName: String) = throw UnsupportedOperationException()

    override fun addParent(parent: ColumnGroup<*>) = throw UnsupportedOperationException()

    override fun forceResolve() = throw UnsupportedOperationException()

    override fun get(range: IntRange) = throw UnsupportedOperationException()
}
