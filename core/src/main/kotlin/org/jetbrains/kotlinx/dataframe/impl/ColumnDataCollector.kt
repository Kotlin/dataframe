package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

public interface DataCollector<T> {

    public val data: List<T?>
    public val hasNulls: Boolean

    public fun add(value: T)

    public fun toColumn(name: String): DataColumn<T>
}

internal abstract class DataCollectorBase<T>(initCapacity: Int) : DataCollector<T> {

    override var hasNulls = false

    override val data = ArrayList<T?>(initCapacity)

    val values: List<T?>
        get() = data

    override fun add(value: T) {
        if (value == null) hasNulls = true
        data.add(value)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun createColumn(name: String, type: KType): DataColumn<T> =
        when {
            type == nothingType -> {
                require(values.isEmpty()) { "Cannot create non-empty DataColumn of type Nothing" }
                DataColumn.empty(name)
            }

            type == nullableNothingType -> {
                require(values.all { it == null }) { "Cannot create DataColumn of type Nothing? with non-null values" }
                DataColumn.createValueColumn(name, values, nullableNothingType)
            }

            type.isSubtypeOf(typeOf<AnyFrame?>()) && !hasNulls ->
                DataColumn.createFrameColumn(name, data as List<AnyFrame>)

            type.isSubtypeOf(typeOf<AnyRow?>()) && !hasNulls -> {
                val mergedDf = (data as List<AnyRow>).map { it.toDataFrame() }.concat()
                DataColumn.createColumnGroup(name, mergedDf).asDataColumn()
            }

            else -> DataColumn.createValueColumn(name, data, type.withNullability(hasNulls))
        }.cast()
}

internal open class ColumnDataCollector(initCapacity: Int = 0, val typeOf: (KClass<*>) -> KType) :
    DataCollectorBase<Any?>(initCapacity) {

    override fun toColumn(name: String) = createColumnGuessingType(name, values)
}

internal class TypedColumnDataCollector<T>(initCapacity: Int = 0, val type: KType, val checkTypes: Boolean = true) :
    DataCollectorBase<T?>(initCapacity) {

    internal val kclass = type.jvmErasure

    override fun add(value: T?) {
        if (checkTypes && value != null && !value.javaClass.kotlin.isSubclassOf(kclass)) {
            throw IllegalArgumentException(
                "Cannot add a value of class ${value.javaClass.kotlin.qualifiedName} to a column of type $type. Value: '$value'.",
            )
        }
        super.add(value)
    }

    override fun toColumn(name: String) = createColumn(name, type)
}

internal fun createDataCollector(initCapacity: Int = 0) =
    createDataCollector(initCapacity) {
        it.createStarProjectedType(false)
    }

internal fun createDataCollector(initCapacity: Int = 0, typeOf: (KClass<*>) -> KType) =
    ColumnDataCollector(initCapacity, typeOf)

internal fun <T> createDataCollector(initCapacity: Int = 0, type: KType) =
    TypedColumnDataCollector<T>(initCapacity, type)
