package org.jetbrains.dataframe.api.columns

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.impl.columns.GroupedColImpl
import org.jetbrains.dataframe.impl.columns.TableColImpl
import org.jetbrains.dataframe.impl.columns.ValueImplCol
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

interface DataCol<out T> : ColumnReference<T> {

    companion object {

        fun <T> create(name: String, values: List<T>, type: KType, defaultValue: T? = null): ValueCol<T> = ValueImplCol(values, name, type, defaultValue)

        fun <T> createGroup(name: String, df: DataFrame<T>): GroupedCol<T> = GroupedColImpl(df, name)

        fun <T> createTable(name: String, df: DataFrame<T>, startIndices: List<Int>): TableCol<T> = TableColImpl(name, df, startIndices)

        fun <T> createTable(name: String, groups: List<DataFrame<T>>, df: DataFrame<T>? = null): TableCol<T> = TableColImpl(df
                ?: groups.getBaseSchema(), name, groups)

        fun <T> createGuess(name: String, values: List<T>, type:KType, defaultValue: T? = null): DataCol<T> {
            val kClass = type.classifier!! as KClass<*>
            if(kClass.isSubclassOf(DataRow::class)){
                val df = values.map { (it as DataRow<*>).toDataFrame() }.union()
                return createGroup(name, df) as DataCol<T>
            }
            if(kClass.isSubclassOf(DataFrame::class)){
                // TODO: support nulls in values
                return createTable(name, values as List<DataFrame<T>>) as DataCol<T>
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

    fun slice(range: IntRange): DataCol<T>

    operator fun get(columnName: String): AnyCol

    fun slice(indices: Iterable<Int>): DataCol<T>

    fun slice(mask: BooleanArray): DataCol<T>

    fun toSet(): Set<T>

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T>? = this.addPath()
}