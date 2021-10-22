package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.api.CreateDataFrameDsl
import org.jetbrains.kotlinx.dataframe.api.TraversePropertiesDsl
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toMany
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.createTypeWithArgument
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.projectUpTo
import java.time.temporal.Temporal
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaField

internal val KClass<*>.isValueType: Boolean get() {
    return this == String::class || this.isSubclassOf(Number::class) || this.isSubclassOf(Temporal::class)
}

internal class CreateDataFrameDslImpl<T>(
    source: Iterable<T>,
    private val clazz: KClass<*>,
    private val prefix: ColumnPath = emptyPath()
) : CreateDataFrameDsl<T>(source) {

    internal val columns = mutableListOf<Pair<ColumnPath, AnyColumn>>()

    override fun add(column: AnyColumn, path: ColumnPath?) {
        val col = if (path != null) column.rename(path.last()) else column
        val targetPath = if (path != null) prefix + path else prefix + column.name()
        columns.add(targetPath to col)
    }

    override operator fun String.invoke(builder: CreateDataFrameDsl<T>.() -> Unit) {
        val child = CreateDataFrameDslImpl(source, clazz, prefix + this)
        builder(child)
        columns.addAll(child.columns)
    }

    private class TraversePropertiesDslImpl : TraversePropertiesDsl {

        val excludes = mutableSetOf<KProperty<*>>()

        val preserves: MutableSet<KClass<*>> = mutableSetOf()

        override fun exclude(vararg properties: KProperty<*>) {
            excludes.addAll(properties)
        }

        override fun preserve(vararg classes: KClass<*>) {
            preserves.addAll(classes)
        }
    }

    override fun properties(vararg roots: KProperty<*>, depth: Int, body: (TraversePropertiesDsl.() -> Unit)?) {
        val dsl = TraversePropertiesDslImpl()
        if (body != null) {
            body(dsl)
        }
        val df = convertToDataFrame(source, clazz, roots.toList(), dsl.excludes, dsl.preserves, depth)
        df.columns().forEach {
            add(it)
        }
    }
}

@PublishedApi
internal fun <T> Iterable<T>.createDataFrameImpl(clazz: KClass<*>, body: CreateDataFrameDslImpl<T>.() -> Unit): AnyFrame {
    val builder = CreateDataFrameDslImpl(this, clazz)
    builder.body()
    return builder.columns.toDataFrame<Unit>()
}

@PublishedApi
internal fun convertToDataFrame(
    data: Iterable<*>,
    clazz: KClass<*>,
    roots: List<KProperty<*>>,
    excludes: Set<KProperty<*>>,
    preserves: Set<KClass<*>>,
    depth: Int
): AnyFrame {
    val properties = roots.ifEmpty {
        clazz.memberProperties
            .filter { it.parameters.toList().size == 1 }
    }

    val columns = properties.mapNotNull {
        val property = it
        if (excludes.contains(property)) return@mapNotNull null

        property.javaField?.isAccessible = true

        var nullable = false
        val values = data.map { obj ->
            if (obj == null) {
                nullable = true
                null
            } else {
                val value = it.call(obj)
                if (value == null) nullable = true
                value
            }
        }

        val type = property.returnType
        val kclass = (type.classifier as KClass<*>)
        when {
            depth == 1 || kclass.isValueType || preserves.contains(kclass) -> DataColumn.createValueColumn(
                it.name,
                values,
                property.returnType.withNullability(nullable)
            )
            kclass.isSubclassOf(Iterable::class) -> {
                val elementType = type.projectUpTo(Iterable::class).arguments.firstOrNull()?.type
                if (elementType == null) DataColumn.createValueColumn(
                    it.name,
                    values,
                    property.returnType.withNullability(nullable)
                )
                else {
                    val elementClass = (elementType.classifier as KClass<*>)
                    if (elementClass.isValueType) {
                        val manyType = Many::class.createTypeWithArgument(elementType).withNullability(nullable)
                        val manyValues = values.map {
                            (it as? Iterable<*>)?.toMany()
                        }
                        DataColumn.createValueColumn(it.name, manyValues, manyType)
                    } else {
                        val frames = values.map {
                            if (it is Iterable<*>) {
                                convertToDataFrame(it, elementClass, emptyList(), excludes, preserves, depth - 1)
                            } else null
                        }
                        DataColumn.createFrameColumn(it.name, frames, nullable)
                    }
                }
            }
            else -> {
                val df = convertToDataFrame(values, kclass, emptyList(), excludes, preserves, depth - 1)
                DataColumn.createColumnGroup(it.name, df)
            }
        }
    }
    return dataFrameOf(columns)
}
