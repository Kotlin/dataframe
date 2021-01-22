package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.impl.columns.MapColumnImpl
import org.jetbrains.dataframe.impl.columns.FrameColumnImpl
import org.jetbrains.dataframe.impl.columns.ValueImplColumn
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

interface DataColumn<out T> : ColumnReference<T> {

    companion object {

        fun <T> create(name: String, values: List<T>, type: KType, defaultValue: T? = null): ValueColumn<T> = ValueImplColumn(values, name, type, defaultValue)

        fun <T> createGroup(name: String, df: DataFrame<T>): MapColumn<T> = MapColumnImpl(df, name)

        fun <T> createTable(name: String, df: DataFrame<T>, startIndices: List<Int>): FrameColumn<T> = FrameColumnImpl(name, df, startIndices)

        fun <T> createTable(name: String, groups: List<DataFrame<T>>, df: DataFrame<T>? = null): FrameColumn<T> = FrameColumnImpl(df
                ?: groups.getBaseSchema(), name, groups)

        fun <T> createGuess(name: String, values: List<T>, type:KType, defaultValue: T? = null): DataColumn<T> {
            val kClass = type.classifier!! as KClass<*>
            if(kClass.isSubclassOf(DataRow::class)){
                val df = values.map { (it as DataRow<*>).toDataFrame() }.union()
                return createGroup(name, df) as DataColumn<T>
            }
            if(kClass.isSubclassOf(DataFrame::class)){
                // TODO: support nulls in values
                return createTable(name, values as List<DataFrame<T>>) as DataColumn<T>
            }
            return create(name, values, type, defaultValue)
        }

        fun empty() = create("", emptyList<Unit>(), getType<Unit>()) as AnyCol
    }

    val values: Iterable<T>
    val ndistinct: Int
    val type: KType
    val hasNulls: Boolean get() = type.isMarkedNullable
    val size: Int

    fun kind(): ColumnKind

    operator fun get(index: Int): T

    operator fun get(row: DataRow<*>) = get(row.index)

    fun values() = values

    fun toList() = values.asList()

    fun defaultValue(): T?

    fun slice(range: IntRange): DataColumn<T>

    operator fun get(columnName: String): AnyCol

    fun slice(indices: Iterable<Int>): DataColumn<T>

    fun slice(mask: BooleanArray): DataColumn<T>

    fun toSet(): Set<T>

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? = this.addPath()
}