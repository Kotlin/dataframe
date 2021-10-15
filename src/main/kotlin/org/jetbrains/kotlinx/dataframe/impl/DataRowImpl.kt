package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.NamedValue
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.shortPath
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.io.renderToString

internal open class DataRowImpl<T>(private val index: Int, val owner: DataFrame<T>) : DataRow<T> {

    override fun df() = owner

    override operator fun get(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
        return owner[name][index]
    }

    override operator fun <R> get(column: ColumnReference<R>): R {
        ColumnAccessTracker.registerColumnAccess(column.name())
        return owner[column][index]
    }

    override fun index() = index

    override fun getRow(index: Int): DataRow<T>? =
        if (index >= 0 && index < owner.nrow()) DataRowImpl(index, owner) else null

    val values by lazy { owner.columns().map { it[index] } }

    override fun values() = values

    override fun get(columnIndex: Int): Any? {
        val column = owner.col(columnIndex)
        ColumnAccessTracker.registerColumnAccess(column.name())
        return column[index]
    }

    override fun toString() = renderToString()

    override fun equals(other: Any?): Boolean {
        val o = other as? DataRowImpl<T>
        if (o == null) return false
        return values.equals(o.values)
    }

    override fun hashCode() = values.hashCode()

    override fun tryGet(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
        return owner.tryGetColumn(name)?.get(index)
    }

    override fun prev(): DataRow<T>? {
        return if (index > 0) owner[index - 1] else null
    }

    override fun next(): DataRow<T>? {
        return if (index < owner.nrow() - 1) owner[index + 1] else null
    }
}

internal val <T> DataRow<T>.owner: DataFrame<T> get() = df()
internal fun AnyRow.namedValues(): Sequence<NamedValue> = owner.columns().asSequence().map {
    NamedValue.create(it.shortPath(), it[index], it.type(), it.defaultValue(), guessType = false)
}
