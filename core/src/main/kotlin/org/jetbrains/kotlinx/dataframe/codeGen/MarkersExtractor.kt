package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.ScopeProperty
import org.jetbrains.kotlinx.dataframe.impl.schema.getPropertyOrderFromPrimaryConstructor
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

internal fun KType.getFieldKind(): FieldKind = when {
    jvmErasure == DataFrame::class -> Frame
    jvmErasure == List::class && (arguments[0].type?.jvmErasure?.hasAnnotation<DataSchema>() == true) -> ListToFrame
    jvmErasure == DataRow::class -> Group
    jvmErasure.hasAnnotation<DataSchema>() -> ObjectToGroup
    else -> Default
}

internal sealed interface FieldKind {
    val shouldBeConvertedToColumnGroup: Boolean get() = false
    val shouldBeConvertedToFrameColumn: Boolean get() = false
}
internal data object Frame : FieldKind {
    override val shouldBeConvertedToFrameColumn: Boolean = true
}
internal data object ListToFrame : FieldKind {
    override val shouldBeConvertedToFrameColumn: Boolean = true
}

internal data object Default : FieldKind

internal data object Group : FieldKind {
    override val shouldBeConvertedToColumnGroup: Boolean = true
}

internal data object ObjectToGroup : FieldKind {
    override val shouldBeConvertedToColumnGroup: Boolean = true
}

private fun String.toNullable(): String = if (endsWith("?")) this else "$this?"

internal object MarkersExtractor {

    private val cache = mutableMapOf<Pair<KClass<*>, Boolean>, Marker>()

    inline fun <reified T> get() = get(T::class)

    fun get(markerClass: KClass<*>, nullableProperties: Boolean = false): Marker =
        cache.getOrPut(Pair(markerClass, nullableProperties)) {
            val fields = getFields(markerClass, nullableProperties)
            val isOpen = !markerClass.isSealed &&
                markerClass.java.isInterface &&
                markerClass.findAnnotation<DataSchema>()?.isOpen == true

            val baseSchemas = markerClass.superclasses.filter { it != Any::class }.map { get(it, nullableProperties) }
            Marker(
                name = markerClass.qualifiedName ?: markerClass.simpleName!!,
                isOpen = isOpen,
                fields = fields,
                superMarkers = baseSchemas,
                visibility = MarkerVisibility.IMPLICIT_PUBLIC,
                klass = markerClass,
            )
        }

    private fun getFields(markerClass: KClass<*>, nullableProperties: Boolean): List<GeneratedField> {
        val order = getPropertyOrderFromPrimaryConstructor(markerClass) ?: emptyMap()
        val structuralProperties = markerClass.memberProperties.filter { !it.hasAnnotation<ScopeProperty>() }
        return structuralProperties.sortedBy { order[it.name] ?: Int.MAX_VALUE }.map {
            val fieldName = ValidFieldName.of(it.name)
            val columnName = it.findAnnotation<ColumnName>()?.name ?: fieldName.unquoted
            val type = it.returnType
            val fieldType: FieldType
            val clazz = type.jvmErasure
            val fieldKind = type.getFieldKind()
            val columnSchema = when {
                fieldKind.shouldBeConvertedToColumnGroup -> {
                    val nestedType = if (clazz == DataRow::class) type.arguments[0].type ?: typeOf<Any?>() else type
                    val marker = get(nestedType.jvmErasure, nullableProperties || type.isMarkedNullable)
                    fieldType = FieldType.GroupFieldType(
                        marker.name,
                        renderAsObject = fieldKind is ObjectToGroup
                    )
                    ColumnSchema.Group(marker.schema, nestedType)
                }

                fieldKind.shouldBeConvertedToFrameColumn -> {
                    val frameType = type.arguments[0].type ?: typeOf<Any?>()
                    val marker = get(frameType.jvmErasure, nullableProperties || type.isMarkedNullable)
                    fieldType = FieldType.FrameFieldType(
                        marker.name,
                        type.isMarkedNullable || nullableProperties,
                        renderAsList = fieldKind is ListToFrame
                    )
                    ColumnSchema.Frame(marker.schema, type.isMarkedNullable, frameType)
                }

                else -> {
                    fieldType = FieldType.ValueFieldType(
                        if (nullableProperties) type.toString().toNullable() else type.toString()
                    )
                    ColumnSchema.Value(
                        if (nullableProperties) type.withNullability(true) else type
                    )
                }
            }

            GeneratedField(fieldName, columnName, false, columnSchema, fieldType)
        }
    }
}
