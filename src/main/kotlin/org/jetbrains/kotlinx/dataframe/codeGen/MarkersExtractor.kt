package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.schema.getPropertiesOrder
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmErasure

internal fun KType.shouldBeConvertedToFrameColumn(): Boolean = when (jvmErasure) {
    DataFrame::class -> true
    List::class -> arguments[0].type?.jvmErasure?.hasAnnotation<DataSchema>() == true
    else -> false
}

internal fun KType.shouldBeConvertedToColumnGroup(): Boolean = jvmErasure.let {
    it == DataRow::class || it.hasAnnotation<DataSchema>()
}

internal object MarkersExtractor {

    private val cache = mutableMapOf<KClass<*>, Marker>()

    inline fun <reified T> get() = get(T::class)

    operator fun get(markerClass: KClass<*>): Marker =
        cache.getOrPut(markerClass) {
            val fields = getFields(markerClass)
            val isOpen = markerClass.findAnnotation<DataSchema>()?.isOpen ?: false
            val baseSchemas = markerClass.superclasses.filter { it != Any::class }.map { get(it) }
            Marker(
                markerClass.qualifiedName ?: markerClass.simpleName!!,
                isOpen,
                fields,
                baseSchemas,
                MarkerVisibility.IMPLICIT_PUBLIC,
                markerClass
            )
        }

    private fun getFields(markerClass: KClass<*>): List<GeneratedField> {
        val order = getPropertiesOrder(markerClass)
        return markerClass.declaredMemberProperties.sortedBy { order[it.name] ?: Int.MAX_VALUE }.mapIndexed { index, it ->
            val fieldName = ValidFieldName.of(it.name)
            val columnName = it.findAnnotation<ColumnName>()?.name ?: fieldName.unquoted
            val type = it.returnType
            val fieldType: FieldType
            val clazz = type.jvmErasure
            val columnSchema = when {
                type.shouldBeConvertedToColumnGroup() -> {
                    val nestedType = if (clazz == DataRow::class) type.arguments[0].type!! else type
                    val marker = get(nestedType.jvmErasure)
                    fieldType = FieldType.GroupFieldType(marker.name)
                    ColumnSchema.Group(marker.schema)
                }
                type.shouldBeConvertedToFrameColumn() -> {
                    val frameType = type.arguments[0].type!!
                    val marker = get(frameType.jvmErasure)
                    fieldType = FieldType.FrameFieldType(marker.name, type.isMarkedNullable)
                    ColumnSchema.Frame(marker.schema, type.isMarkedNullable)
                }
                else -> {
                    fieldType = FieldType.ValueFieldType(type.toString())
                    ColumnSchema.Value(type)
                }
            }

            GeneratedField(fieldName, columnName, false, columnSchema, fieldType)
        }
    }
}
