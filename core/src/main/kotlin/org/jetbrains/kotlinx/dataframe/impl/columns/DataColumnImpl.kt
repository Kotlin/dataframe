package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.BuildConfig
import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.isArray
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import org.jetbrains.kotlinx.dataframe.kind
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

internal abstract class DataColumnImpl<T>(
    internal val values: ColumnDataHolder<T>,
    val name: String,
    val type: KType,
) : DataColumn<T>,
    DataColumnInternal<T> {

    private infix fun <T> T?.matches(type: KType) =
        when {
            this == null -> type.isMarkedNullable

            this.isPrimitiveArray ->
                type.isPrimitiveArray &&
                    this!!::class.qualifiedName == type.classifier?.let { (it as KClass<*>).qualifiedName }

            this.isArray -> type.isArray

            // cannot check the precise type of array
            else -> this!!::class.isSubclassOf(type.classifier as KClass<*>)
        }

    init {
        // Check for [Issue #713](https://github.com/Kotlin/dataframe/issues/713).
        // This only runs with `kotlin.dataframe.debug=true` in gradle.properties.
        if (BuildConfig.DEBUG) {
            require(values.all { it matches type }) {
                val types = values.map { if (it == null) "Nothing?" else it!!::class.simpleName }.distinct()
                "Values of $kind '$name' have types '$types' which are not compatible given with column type '$type'"
            }
        }
    }

    protected val distinct
        get() = values.distinct

    override fun name() = name

    override fun values(): List<T> = values.toList() // todo is heavy but tests break without it

    override fun type() = type

    override fun toSet() = distinct.value

    override fun contains(value: T): Boolean =
        if (distinct.isInitialized()) distinct.value.contains(value) else values.contains(value)

    override fun toString() = dataFrameOf(this).toString() // "${name()}: $type"

    override fun countDistinct() = toSet().size

    override fun get(index: Int) = values[index]

    override fun size() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    private val hashCode by lazy { getHashCode() }

    override fun hashCode() = hashCode

    override operator fun get(range: IntRange) = createWithValues(values[range])

    protected abstract fun createWithValues(values: List<T>, hasNulls: Boolean? = null): DataColumn<T>
}
