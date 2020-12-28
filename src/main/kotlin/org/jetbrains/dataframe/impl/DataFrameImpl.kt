package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

internal open class DataFrameImpl<T>(override val columns: List<DataCol>) : DataFrame<T> {

    override val nrow: Int

    init {

        nrow = columns.firstOrNull()?.size ?: 0

        val invalidSizeColumns = columns.filter { it.size != nrow }
        if (invalidSizeColumns.size > 0)
            throw Exception("Invalid column sizes: ${invalidSizeColumns}") // TODO

        val columnNames = columns.groupBy { it.name }.filter { it.value.size > 1 }.map { it.key }
        if (columnNames.size > 0)
            throw Exception("Duplicate column names: ${columnNames}. All column names: ${columnNames()}")
    }


    private val columnsMap by lazy { columns.withIndex().associateBy({ it.value.name }, { it.index }) }

    override val rows = object : Iterable<DataRow<T>> {
        override fun iterator() =

                object : Iterator<DataRow<T>> {
                    var curRow = 0

                    override fun hasNext(): Boolean = curRow < nrow

                    override fun next() = get(curRow++)!!
                }
    }


    override fun getColumnIndex(columnName: String) = columnsMap[columnName] ?: -1

    override fun equals(other: Any?): Boolean {
        val df = other as? DataFrame<*> ?: return false
        return columns == df.columns
    }

    override fun hashCode() = columns.hashCode()

    override fun toString() = renderToString()

    override fun addRow(vararg values: Any?): DataFrame<T> {
        assert(values.size == ncol) { "Invalid number of arguments. Expected: $ncol, actual: ${values.size}" }
        return values.mapIndexed { i, v ->
            val col = columns[i]
            if (v != null)
            // Note: type arguments for a new value are not validated here because they are erased
                assert(v.javaClass.kotlin.isSubclassOf(col.type.jvmErasure))
            col.withValues(col.values + listOf(v), col.hasNulls || v == null)
        }.asDataFrame<T>()
    }

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<DataRow<T>>? {
        return ColumnData.createGroup("", this).addPath(emptyList())
    }
}