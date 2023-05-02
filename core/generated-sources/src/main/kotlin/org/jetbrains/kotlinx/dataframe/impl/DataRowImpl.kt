package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.NamedValue
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.shortPath
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.io.renderToString

internal open class DataRowImpl<T>(private val index: Int, private val df: DataFrame<T>) : DataRow<T> {

    override fun df() = df

    override operator fun get(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
        return df[name][index]
    }

    override operator fun <R> get(column: ColumnReference<R>): R {
        ColumnAccessTracker.registerColumnAccess(column.name())
        return column.getValue(this)
    }

    override fun <R> getValueOrNull(column: ColumnReference<R>): R? {
        ColumnAccessTracker.registerColumnAccess(column.name())
        return column.getValueOrNull(this)
    }

    override fun index() = index

    private val values by lazy { df.columns().map { it[index] } }

    override fun values() = values

    override fun get(columnIndex: Int): Any? {
        val column = df.getColumn(columnIndex)
        ColumnAccessTracker.registerColumnAccess(column.name())
        return column[index]
    }

    override fun toString() = renderToString()

    override fun equals(other: Any?): Boolean {
        val o = other as? DataRowImpl<*> ?: return false
        return values.equals(o.values)
    }

    override fun hashCode() = values.hashCode()

    override fun getOrNull(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
        return df.getColumnOrNull(name)?.get(index)
    }
}

internal val <T> DataRow<T>.owner: DataFrame<T> get() = df()
internal fun AnyRow.namedValues(): Sequence<NamedValue> = owner.columns().asSequence().map {
    NamedValue.create(it.shortPath(), it[index], it.type(), it.defaultValue(), guessType = false)
}
