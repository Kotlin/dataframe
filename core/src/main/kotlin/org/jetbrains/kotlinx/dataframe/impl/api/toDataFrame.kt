package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
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
import org.jetbrains.kotlinx.dataframe.codeGen.getFieldKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.impl.isArray
import org.jetbrains.kotlinx.dataframe.impl.isGetterLike
import org.jetbrains.kotlinx.dataframe.impl.isJavaRecord
import org.jetbrains.kotlinx.dataframe.impl.projectUpTo
import org.jetbrains.kotlinx.dataframe.impl.recordComponentNames
import org.jetbrains.kotlinx.dataframe.impl.schema.sortWithConstructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalAmount
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.valueParameters
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.typeOf
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

// non-standard value types (not supertypes, but exact types)
private val valueTypes = setOf(
    Any::class,
    Unit::class,
    Char::class,
    UByte::class,
    UShort::class,
    UInt::class,
    ULong::class,
    String::class,
    Boolean::class,
    kotlin.time.Duration::class,
    kotlinx.datetime.LocalDate::class,
    kotlinx.datetime.LocalDateTime::class,
    DeprecatedInstant::class,
    StdlibInstant::class,
    kotlinx.datetime.TimeZone::class,
    kotlinx.datetime.DateTimePeriod::class,
    kotlinx.datetime.DateTimeUnit::class,
    Map::class,
    MutableMap::class,
)

/**
 * Determines whether a class can be unfolded into its properties.
 *
 * A class is considered **unfoldable** if it has at least one public property
 * or getter-like function. This excludes:
 * - **Value types** such as primitives, enums, and date-time types.
 * - **Classes without properties**, including empty or marker classes.
 *
 * @return `true` if the class has unfoldable properties, `false` otherwise.
 */
@PublishedApi
internal val KClass<*>.canBeUnfolded: Boolean
    get() = (!this.isValueType) && this.hasProperties

/**
 * Checks if `KClass` is a value type (number, datetime, string, etc.)
 * Should be aligned with `ConeKotlinType.isValueType()` in
 * plugins/kotlin-dataframe/src/org/jetbrains/kotlinx/dataframe/plugin/impl/api/toDataFrame.kt
 */
@PublishedApi
internal val KClass<*>.isValueType: Boolean
    get() =
        this in valueTypes ||
            this.isSubclassOf(Number::class) ||
            this.isSubclassOf(Enum::class) ||
            // all java datetime types
            this.isSubclassOf(TemporalAccessor::class) ||
            this.isSubclassOf(TemporalAmount::class) ||
            this.isArray

/**
 * Checks if `KClass` has public properties / getter functions (for pojo-like classes).
 */
@PublishedApi
internal val KClass<*>.hasProperties: Boolean
    get() = properties().isNotEmpty()

internal class CreateDataFrameDslImpl<T>(
    override val source: Iterable<T>,
    private val type: KType,
    private val prefix: ColumnPath = emptyPath(),
    private val configuration: TraverseConfiguration = TraverseConfiguration(),
) : CreateDataFrameDsl<T>(),
    TraversePropertiesDsl by configuration {

    internal val columns = mutableListOf<Pair<ColumnPath, AnyBaseCol>>()

    override fun add(column: AnyBaseCol, path: ColumnPath?) {
        val col = if (path != null) column.rename(path.last()) else column
        val targetPath = if (path != null) prefix + path else prefix + column.name()
        columns.add(targetPath to col)
    }

    override operator fun String.invoke(builder: CreateDataFrameDsl<T>.() -> Unit) {
        val child = CreateDataFrameDslImpl(source, type, prefix + this)
        builder(child)
        columns.addAll(child.columns)
    }

    internal class TraverseConfiguration : TraversePropertiesDsl {

        val excludeProperties = mutableSetOf<KCallable<*>>()

        val excludeClasses = mutableSetOf<KClass<*>>()

        val preserveClasses = mutableSetOf<KClass<*>>()

        val preserveProperties = mutableSetOf<KCallable<*>>()

        fun clone(): TraverseConfiguration =
            TraverseConfiguration().also {
                it.excludeClasses.addAll(excludeClasses)
                it.excludeProperties.addAll(excludeProperties)
                it.preserveProperties.addAll(preserveProperties)
                it.preserveClasses.addAll(preserveClasses)
            }

        override fun exclude(vararg properties: KCallable<*>) {
            for (prop in properties) {
                require(prop.isGetterLike()) {
                    "${prop.name} is not a property or getter-like function. Only those are traversed and can be excluded."
                }
            }
            excludeProperties.addAll(properties)
        }

        override fun exclude(vararg classes: KClass<*>) {
            excludeClasses.addAll(classes)
        }

        override fun preserve(vararg classes: KClass<*>) {
            preserveClasses.addAll(classes)
        }

        override fun preserve(vararg properties: KCallable<*>) {
            for (prop in properties) {
                require(prop.isGetterLike()) {
                    "${prop.name} is not a property or getter-like function. Only those are traversed and can be preserved."
                }
            }
            preserveProperties.addAll(properties)
        }
    }

    override fun properties(vararg roots: KCallable<*>, maxDepth: Int, body: (TraversePropertiesDsl.() -> Unit)?) {
        for (prop in roots) {
            require(prop.isGetterLike()) {
                "${prop.name} is not a property or getter-like function. Only those are traversed and can be added as roots."
            }
        }

        val dsl = configuration.clone()
        if (body != null) {
            body(dsl)
        }
        val df = convertToDataFrame(
            data = source,
            type = type,
            roots = roots.toList(),
            excludes = dsl.excludeProperties,
            preserveClasses = dsl.preserveClasses,
            preserveProperties = dsl.preserveProperties,
            excludeClasses = dsl.excludeClasses,
            maxDepth = maxDepth,
        )
        df.columns().forEach {
            add(it)
        }
    }
}

@PublishedApi
internal fun <T> Iterable<T>.createDataFrameImpl(
    type: KType,
    body: CreateDataFrameDslImpl<T>.() -> Unit,
): DataFrame<T> {
    val builder = CreateDataFrameDslImpl(this, type)
    builder.body()
    return builder.columns.toDataFrameFromPairs()
}

@Deprecated("backward compatibility for Kandy", level = DeprecationLevel.ERROR)
internal fun <T> Iterable<T>.createDataFrameImpl(
    clazz: KClass<*>,
    body: CreateDataFrameDslImpl<T>.() -> Unit,
): DataFrame<T> {
    val builder = CreateDataFrameDslImpl(this, clazz.starProjectedType)
    builder.body()
    return builder.columns.toDataFrameFromPairs()
}

@PublishedApi
internal fun convertToDataFrame(
    data: Iterable<*>,
    type: KType,
    roots: List<KCallable<*>>,
    excludes: Set<KCallable<*>>,
    preserveClasses: Set<KClass<*>>,
    preserveProperties: Set<KCallable<*>>,
    excludeClasses: Set<KClass<*>>,
    maxDepth: Int,
): AnyFrame {
    val clazz = type.classifierOrAny()
    // this check relies on later recursive calls having roots = emptyList()
    if (roots.isEmpty() && !clazz.canBeUnfolded) {
        val column = DataColumn.createByType("value", data.toList(), type)
        return dataFrameOf(column)
    }

    val properties: List<KCallable<*>> = roots
        .ifEmpty {
            clazz.properties()
        }
        // sort properties by order in constructor
        .sortWithConstructor(clazz)

    val columns = properties.mapNotNull {
        val property = it
        if (excludes.contains(property)) return@mapNotNull null
        val klass = it.returnType.classifier as? KClass<*>
        if (excludeClasses.contains(klass)) return@mapNotNull null

        class ValueClassConverter(val unbox: Method, val box: Method)

        val valueClassConverter = klass?.let { kClass ->
            if (!kClass.isValue) return@let null

            val constructor = requireNotNull(kClass.primaryConstructor) {
                "value class $kClass is expected to have primary constructor, but couldn't obtain it"
            }
            val parameter = constructor.parameters.singleOrNull()
                ?: error(
                    "conversion of value class $kClass with multiple parameters in constructor is not yet supported",
                )
            // there's no need to unwrap if underlying field is nullable
            if (parameter.type.isMarkedNullable) return@let null
            // box and unbox impl methods are part of binary API of value classes
            // https://youtrack.jetbrains.com/issue/KT-50518/Boxing-Unboxing-methods-for-JvmInline-value-classes-should-be-public-accessible
            val unbox = kClass.java.getMethod("unbox-impl")
            val box = kClass.java.methods.single { it.name == "box-impl" }
            val valueClassConverter = ValueClassConverter(unbox, box)
            valueClassConverter
        }
        (property as? KProperty<*>)?.javaField?.isAccessible = true
        property.isAccessible = true

        var nullable = false
        var hasExceptions = false
        val values = data.map { obj ->
            if (obj == null) {
                nullable = true
                null
            } else {
                val value = try {
                    val value = it.call(obj)
                    /*
                     * here we do what compiler does
                     * @see org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.testKPropertyGetLibrary
                     */
                    if (valueClassConverter != null) {
                        val var1 = value?.let {
                            valueClassConverter.unbox.invoke(it)
                        }
                        var1?.let { valueClassConverter.box.invoke(null, var1) }
                    } else {
                        value
                    }
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

        val returnType = property.returnType.let { type ->
            if (type.classifier is KClass<*>) {
                type
            } else {
                typeOf<Any>()
            }
        }
        val kClass = returnType.classifier as KClass<*>
        val fieldKind = returnType.getFieldKind()

        val keepSubtree =
            maxDepth <= 0 && !fieldKind.shouldBeConvertedToFrameColumn && !fieldKind.shouldBeConvertedToColumnGroup
        val shouldCreateValueCol = keepSubtree ||
            kClass in preserveClasses ||
            property in preserveProperties ||
            (
                !kClass.canBeUnfolded &&
                    !fieldKind.shouldBeConvertedToFrameColumn &&
                    !fieldKind.shouldBeConvertedToColumnGroup
            )

        val shouldCreateFrameCol = kClass == DataFrame::class && !nullable
        val shouldCreateColumnGroup = kClass == DataRow::class

        when {
            hasExceptions -> DataColumn.createByInference(it.columnName, values, nullable = nullable)

            shouldCreateValueCol ->
                DataColumn.createValueColumn(
                    name = it.columnName,
                    values = values,
                    type = returnType.withNullability(nullable),
                )

            shouldCreateFrameCol ->
                DataColumn.createFrameColumn(
                    name = it.columnName,
                    groups = values as List<AnyFrame>,
                )

            shouldCreateColumnGroup ->
                DataColumn.createColumnGroup(
                    name = it.columnName,
                    df = (values as List<AnyRow>).concat(),
                )

            kClass.isSubclassOf(Iterable::class) ->
                when (val elementType = returnType.projectUpTo(Iterable::class).arguments.firstOrNull()?.type) {
                    null ->
                        DataColumn.createValueColumn(
                            name = it.columnName,
                            values = values,
                            type = returnType.withNullability(nullable),
                        )

                    else -> {
                        val elementClass = elementType.classifier as? KClass<*>
                        when {
                            elementClass == null -> {
                                val listValues = values.map {
                                    (it as? Iterable<*>)?.asList()
                                }

                                DataColumn.createByInference(it.columnName, listValues)
                            }

                            elementClass.isValueType -> {
                                val listType = getListType(elementType).withNullability(nullable)
                                val listValues = values.map {
                                    (it as? Iterable<*>)?.asList()
                                }
                                DataColumn.createValueColumn(it.columnName, listValues, listType)
                            }

                            else -> {
                                val frames = values.map {
                                    if (it == null) {
                                        DataFrame.empty()
                                    } else {
                                        require(it is Iterable<*>)
                                        convertToDataFrame(
                                            data = it,
                                            type = elementType,
                                            roots = emptyList(),
                                            excludes = excludes,
                                            preserveClasses = preserveClasses,
                                            preserveProperties = preserveProperties,
                                            excludeClasses = excludeClasses,
                                            maxDepth = maxDepth - 1,
                                        )
                                    }
                                }
                                DataColumn.createFrameColumn(it.columnName, frames)
                            }
                        }
                    }
                }

            else -> {
                val df = convertToDataFrame(
                    data = values,
                    type = returnType,
                    roots = emptyList(),
                    excludes = excludes,
                    preserveClasses = preserveClasses,
                    preserveProperties = preserveProperties,
                    excludeClasses = excludeClasses,
                    maxDepth = maxDepth - 1,
                )
                DataColumn.createColumnGroup(name = it.columnName, df = df)
            }
        }
    }
    return if (columns.isEmpty()) {
        DataFrame.empty(data.count())
    } else {
        dataFrameOf(columns)
    }
}

private fun KType.classifierOrAny(): KClass<*> = classifier as? KClass<*> ?: Any::class

private fun KClass<*>.properties(): List<KCallable<*>> {
    // fall back to getter functions for pojo-like classes if no member properties were found
    return memberProperties
        .filter { it.visibility == KVisibility.PUBLIC }
        .ifEmpty {
            if (this.isJavaRecord) {
                val componentNames = this.recordComponentNames
                memberFunctions.filter { it.name in componentNames }
            } else {
                memberFunctions
                    .filter { it.visibility == KVisibility.PUBLIC && it.isGetterLike() }
            }
        }
}
