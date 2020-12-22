package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.impl.columns.GroupedColumnImpl
import org.jetbrains.dataframe.impl.columns.TableColumnImpl
import org.jetbrains.dataframe.impl.columns.ValueColumnImpl
import kotlin.reflect.KType

interface ColumnData<out T> : ColumnDef<T> {

    companion object {

        fun <T> create(name: String, values: List<T>, type: KType, defaultValue: T? = null): ColumnData<T> = ValueColumnImpl(values, name, type, defaultValue)

        fun <T> createGroup(name: String, df: DataFrame<T>): GroupedColumn<T> = GroupedColumnImpl(df, name)

        fun <T> createTable(name: String, df: DataFrame<T>, startIndices: List<Int>): TableColumn<T> = TableColumnImpl(name, df, startIndices)

        fun <T> createTable(name: String, groups: List<DataFrame<T>>, df: DataFrame<T>? = null): TableColumn<T> = TableColumnImpl(df
                ?: groups.getBaseSchema(), name, groups)

        fun empty() = create("", emptyList<Unit>(), getType<Unit>()) as DataCol
    }

    val values: Iterable<T>
    val ndistinct: Int
    val type: KType
    val hasNulls: Boolean get() = type.isMarkedNullable
    val size: Int

    fun kind(): ColumnKind

    operator fun get(index: Int): T

    operator fun get(row: DataFrameRow<*>) = get(row.index)

    fun toList() = values.asList()

    fun asIterable() = values

    fun defaultValue(): T?

    fun slice(range: IntRange): ColumnData<T>

    operator fun get(columnName: String): DataCol

    fun slice(indices: Iterable<Int>): ColumnData<T>

    fun slice(mask: BooleanArray): ColumnData<T>

    fun toSet(): Set<T>

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? = this.addPath()
}