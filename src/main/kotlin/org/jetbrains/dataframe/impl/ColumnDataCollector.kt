package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.commonParent
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.withNullability

interface DataCollector<T> {

    fun add(value: T)
    fun toColumn(name: String): ColumnData<T>
}

internal abstract class DataCollectorBase<T>(initCapacity: Int): DataCollector<T> {

    protected var hasNulls = false
        private set

    private val data = ArrayList<T?>(initCapacity)

    val values: List<T?>
        get() = data

    override fun add(value: T) {
        if (value == null) hasNulls = true
        data.add(value)
    }

    protected fun createColumn(name: String, type: KType): ColumnData<T> {
        if ((type.classifier as KClass<*>).isSubclassOf(DataFrame::class)) {
            return ColumnData.createTable(name, data as List<DataFrame<*>>) as ColumnData<T>
        }
        return column(name, data, type.withNullability(hasNulls)) as ColumnData<T>
    }
}

internal open class ColumnDataCollector(initCapacity: Int = 0, val getType: (KClass<*>)->KType): DataCollectorBase<Any?>(initCapacity) {

    private val classes = mutableSetOf<KClass<*>>()

    protected fun commonClass() = classes.commonParent()

    override fun add(value: Any?) {
        super.add(value)
        if (value != null) classes.add(value.javaClass.kotlin)
    }

    override fun toColumn(name: String) = createColumn(name, getType(commonClass()).withNullability(hasNulls))
}

internal class TypedColumnDataCollector<T>(initCapacity: Int = 0, val type: KType): DataCollectorBase<T?>(initCapacity) {

    override fun toColumn(name: String) = createColumn(name, type)
}

internal fun createDataCollector(initCapacity: Int = 0) = createDataCollector(initCapacity) { it.createStarProjectedType(false)}

internal fun createDataCollector(initCapacity: Int = 0, getType: (KClass<*>) -> KType) = ColumnDataCollector(initCapacity, getType)

internal fun <T> createDataCollector(initCapacity: Int = 0, type: KType) = TypedColumnDataCollector<T>(initCapacity, type)