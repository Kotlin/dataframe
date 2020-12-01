package org.jetbrains.dataframe.impl

import org.jetbrains.dataframe.column
import org.jetbrains.dataframe.commonParent
import org.jetbrains.dataframe.createStarProjectedType
import org.jetbrains.dataframe.getType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class ColumnDataCollector(initCapacity: Int = 0) {
    private val classes = mutableSetOf<KClass<*>>()
    private var hasNulls = false
    private val data = ArrayList<Any?>(initCapacity)

    fun add(value: Any?) {
        if (value == null) hasNulls = true
        else classes.add(value.javaClass.kotlin)
        data.add(value)
    }

    val values: List<*>
        get() = data

    fun toColumn(name: String) = column(name, data, classes.commonParent().createStarProjectedType(hasNulls))

    fun toColumn(name: String, clazz: KClass<*>) = column(name, data, clazz.createStarProjectedType(hasNulls))
}

internal class TypedColumnDataCollector<T>(initCapacity: Int = 0, val type: KType) {
    private var hasNulls = false
    private val data = ArrayList<T?>(initCapacity)

    fun add(value: T?) {
        if (value == null) hasNulls = true
        data.add(value)
    }

    val values: List<T?>
        get() = data

    fun toColumn(name: String) = column(name, data, type.withNullability(hasNulls))
}

internal inline fun <reified T> createDataCollector(initCapacity: Int = 0) = TypedColumnDataCollector<T>(initCapacity, getType<T>())
internal fun <T> createDataCollector(type: KType, initCapacity: Int = 0) = TypedColumnDataCollector<T>(initCapacity, type)