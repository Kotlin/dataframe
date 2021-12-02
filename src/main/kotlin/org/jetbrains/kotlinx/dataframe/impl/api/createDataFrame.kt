package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.CreateDataFrameDsl
import org.jetbrains.kotlinx.dataframe.api.TraversePropertiesDsl
import org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.impl.projectUpTo
import java.lang.reflect.InvocationTargetException
import java.time.temporal.Temporal
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
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

    internal val columns = mutableListOf<Pair<ColumnPath, AnyBaseColumn>>()

    override fun add(column: AnyBaseColumn, path: ColumnPath?) {
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
internal fun <T> Iterable<T>.createDataFrameImpl(clazz: KClass<*>, body: CreateDataFrameDslImpl<T>.() -> Unit): DataFrame<T> {
    val builder = CreateDataFrameDslImpl(this, clazz)
    builder.body()
    return builder.columns.toDataFrameFromPairs()
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
            .filter { it.visibility == KVisibility.PUBLIC && it.parameters.toList().size == 1 }
    }

    val columns = properties.mapNotNull {
        val property = it
        if (excludes.contains(property)) return@mapNotNull null

        property.javaField?.isAccessible = true

        var nullable = false
        var hasExceptions = false
        val values = data.map { obj ->
            if (obj == null) {
                nullable = true
                null
            } else {
                val value = try {
                    it.call(obj)
                } catch (e: InvocationTargetException) {
                    hasExceptions = true
                    e.targetException
                } catch (e: Throwable) {
                    hasExceptions = true
                    e
                }
                if (value == null) nullable = true
                value
            }
        }

        val type = property.returnType
        val kclass = (type.classifier as KClass<*>)
        when {
            hasExceptions -> DataColumn.createWithTypeInference(it.columnName, values, nullable)
            depth == 1 || kclass.isValueType || preserves.contains(kclass) -> DataColumn.createValueColumn(
                it.columnName,
                values,
                property.returnType.withNullability(nullable)
            )
            kclass.isSubclassOf(Iterable::class) -> {
                val elementType = type.projectUpTo(Iterable::class).arguments.firstOrNull()?.type
                if (elementType == null) DataColumn.createValueColumn(
                    it.columnName,
                    values,
                    property.returnType.withNullability(nullable)
                )
                else {
                    val elementClass = (elementType.classifier as KClass<*>)
                    if (elementClass.isValueType) {
                        val listType = getListType(elementType).withNullability(nullable)
                        val listValues = values.map {
                            (it as? Iterable<*>)?.asList()
                        }
                        DataColumn.createValueColumn(it.columnName, listValues, listType)
                    } else {
                        val frames = values.map {
                            if (it == null) DataFrame.empty()
                            else {
                                require(it is Iterable<*>)
                                convertToDataFrame(it, elementClass, emptyList(), excludes, preserves, depth - 1)
                            }
                        }
                        DataColumn.createFrameColumn(it.columnName, frames)
                    }
                }
            }
            else -> {
                val df = convertToDataFrame(values, kclass, emptyList(), excludes, preserves, depth - 1)
                DataColumn.createColumnGroup(it.columnName, df)
            }
        }
    }
    return dataFrameOf(columns)
}
