package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.CreateDataFrameDsl
import org.jetbrains.kotlinx.dataframe.api.TraversePropertiesDsl
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs
import org.jetbrains.kotlinx.dataframe.codeGen.shouldBeConvertedToColumnGroup
import org.jetbrains.kotlinx.dataframe.codeGen.shouldBeConvertedToFrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.impl.projectUpTo
import org.jetbrains.kotlinx.dataframe.impl.schema.getPropertiesOrder
import java.lang.reflect.InvocationTargetException
import java.time.temporal.Temporal
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaField

internal val valueTypes = setOf(
    String::class,
    Boolean::class,
    kotlin.time.Duration::class,
    kotlinx.datetime.LocalDate::class,
    kotlinx.datetime.LocalDateTime::class,
    kotlinx.datetime.Instant::class,
)

internal val KClass<*>.isValueType: Boolean get() =
    this in valueTypes ||
        this.isSubclassOf(Number::class) ||
        this.isSubclassOf(Enum::class) ||
        this.isSubclassOf(Temporal::class)

internal class CreateDataFrameDslImpl<T>(
    override val source: Iterable<T>,
    private val clazz: KClass<*>,
    private val prefix: ColumnPath = emptyPath(),
    private val configuration: TraverseConfiguration = TraverseConfiguration()
) : CreateDataFrameDsl<T>(), TraversePropertiesDsl by configuration {

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

    internal class TraverseConfiguration : TraversePropertiesDsl {

        val excludeProperties = mutableSetOf<KProperty<*>>()

        val excludeClasses = mutableSetOf<KClass<*>>()

        val preserveClasses = mutableSetOf<KClass<*>>()

        val preserveProperties = mutableSetOf<KProperty<*>>()

        fun clone(): TraverseConfiguration = TraverseConfiguration().also {
            it.excludeClasses.addAll(excludeClasses)
            it.excludeProperties.addAll(excludeProperties)
            it.preserveProperties.addAll(preserveProperties)
            it.preserveClasses.addAll(preserveClasses)
        }

        override fun exclude(vararg properties: KProperty<*>) {
            excludeProperties.addAll(properties)
        }

        override fun exclude(vararg classes: KClass<*>) {
            excludeClasses.addAll(classes)
        }

        override fun preserve(vararg classes: KClass<*>) {
            preserveClasses.addAll(classes)
        }

        override fun preserve(vararg properties: KProperty<*>) {
            preserveProperties.addAll(properties)
        }
    }

    override fun properties(vararg roots: KProperty<*>, depth: Int, body: (TraversePropertiesDsl.() -> Unit)?) {
        val dsl = configuration.clone()
        if (body != null) {
            body(dsl)
        }
        val df = convertToDataFrame(source, clazz, roots.toList(), dsl.excludeProperties, dsl.preserveClasses, dsl.preserveProperties, depth)
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
    preserveClasses: Set<KClass<*>>,
    preserveProperties: Set<KProperty<*>>,
    depth: Int
): AnyFrame {
    val order = getPropertiesOrder(clazz)

    val properties = roots.ifEmpty {
        clazz.memberProperties
            .filter { it.visibility == KVisibility.PUBLIC && it.parameters.toList().size == 1 }
    }.sortedBy { order[it.name] ?: Int.MAX_VALUE }

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
            preserveClasses.contains(kclass) || preserveProperties.contains(property) || (depth == 1 && !type.shouldBeConvertedToFrameColumn() && !type.shouldBeConvertedToColumnGroup()) || kclass.isValueType ->
                DataColumn.createValueColumn(it.columnName, values, property.returnType.withNullability(nullable))
            kclass == DataFrame::class && !nullable -> DataColumn.createFrameColumn(it.columnName, values as List<AnyFrame>)
            kclass == DataRow::class -> DataColumn.createColumnGroup(it.columnName, (values as List<AnyRow>).concat())
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
                                convertToDataFrame(it, elementClass, emptyList(), excludes, preserveClasses, preserveProperties, depth - 1)
                            }
                        }
                        DataColumn.createFrameColumn(it.columnName, frames)
                    }
                }
            }
            else -> {
                val df = convertToDataFrame(values, kclass, emptyList(), excludes, preserveClasses, preserveProperties, depth - 1)
                DataColumn.createColumnGroup(it.columnName, df)
            }
        }
    }
    return if (columns.isEmpty()) {
        DataFrame.empty(data.count())
    } else dataFrameOf(columns)
}
