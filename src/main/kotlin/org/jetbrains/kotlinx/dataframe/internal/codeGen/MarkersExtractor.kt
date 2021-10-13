package org.jetbrains.kotlinx.dataframe.internal.codeGen

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.internal.schema.ColumnSchema
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmErasure

internal object MarkersExtractor {

    private val cache = mutableMapOf<KClass<*>, Marker>()

    inline fun <reified T> get() = get(T::class)

    operator fun get(markerClass: KClass<*>): Marker =
        cache.getOrPut(markerClass) {
            val fields = getFields(markerClass)
            val isOpen = markerClass.findAnnotation<DataSchema>()?.isOpen ?: false
            val baseSchemas = markerClass.superclasses.filter { it != Any::class }.map { get(it) }
            Marker(markerClass.qualifiedName!!, isOpen, fields, baseSchemas, MarkerVisibility.IMPLICIT_PUBLIC)
        }

    private fun getFields(markerClass: KClass<*>): List<GeneratedField> =
        markerClass.declaredMemberProperties.mapIndexed { index, it ->
            val fieldName = ValidFieldName.of(it.name)
            val columnName = it.findAnnotation<ColumnName>()?.name ?: fieldName.unquoted
            val type = it.returnType
            var marker: Marker? = null
            val columnSchema = when (type.jvmErasure) {
                DataRow::class -> {
                    val typeArgument = type.arguments[0].type!!
                    marker = get(typeArgument.jvmErasure)
                    ColumnSchema.Map(marker.schema)
                }
                DataFrame::class -> {
                    val typeArgument = type.arguments[0].type!!
                    marker = get(typeArgument.jvmErasure)
                    ColumnSchema.Frame(marker.schema, type.isMarkedNullable)
                }
                else -> ColumnSchema.Value(type)
            }
            GeneratedField(fieldName, columnName, false, columnSchema, marker?.name)
        }
}
