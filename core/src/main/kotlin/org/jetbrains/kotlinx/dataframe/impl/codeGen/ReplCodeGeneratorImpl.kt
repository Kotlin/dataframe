package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithTypeCastGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.InterfaceGenerationMode
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.codeGen.TypeCastGenerator
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf
import kotlin.time.Instant

internal class ReplCodeGeneratorImpl : ReplCodeGenerator {

    companion object {
        internal val markerInterfacePrefix = "_DataFrameType"
    }

    private val registeredProperties = mutableSetOf<KProperty<*>>()

    private val registeredMarkers = mutableMapOf<KClass<*>, Marker>()

    private val generatedMarkers = mutableMapOf<String, Marker>()

    private val generator: CodeGenerator = CodeGenerator.create(useFqNames = false)

    private fun getMarkerClass(type: KType): KClass<*>? =
        when (type.classifier) {
            DataFrame::class -> type.arguments[0].type?.jvmErasure
            DataRow::class -> type.arguments[0].type?.jvmErasure
            else -> null
        }

    override fun process(row: AnyRow, property: KProperty<*>?): CodeWithTypeCastGenerator = process(row.df(), property)

    override fun process(df: AnyFrame, property: KProperty<*>?): CodeWithTypeCastGenerator {
        var targetSchema = df.schema()

        if (property != null) {
            val wasProcessedBefore = property in registeredProperties
            registeredProperties.add(property)

            // maybe property is already properly typed, let's do some checks
            val currentMarker = getMarkerClass(property.returnType)
                ?.takeIf { it.findAnnotation<DataSchema>() != null }
                ?.let { registeredMarkers[it] ?: MarkersExtractor.get(it) }
            if (currentMarker != null) {
                // we need to make sure that the property's marker type is open in order to let derived dataframes be assignable to it
                if (currentMarker.isOpen) {
                    val columnSchema = currentMarker.schema
                    // for mutable properties we do strong typing only at the first processing, after that we allow its type to be more general than actual dataframe type
                    if (wasProcessedBefore || columnSchema.compare(targetSchema).matches()) {
                        // property scheme is valid for current dataframe, but we should also check that all compatible open markers are implemented by it
                        val requiredBaseMarkers = registeredMarkers.values.filterRequiredForSchema(columnSchema)
                        if (requiredBaseMarkers.any() && requiredBaseMarkers.all { currentMarker.implements(it) }) {
                            return CodeWithTypeCastGenerator.EMPTY
                        }
                        // use current marker scheme as a target for generation of new marker interface, so that available properties won't change
                        targetSchema = columnSchema
                    }
                }
            }
        }

        return generate(schema = targetSchema, name = markerInterfacePrefix, isOpen = true)
    }

    override fun process(groupBy: GroupBy<*, *>): CodeWithTypeCastGenerator {
        val key = generate(
            schema = groupBy.keys.schema(),
            name = markerInterfacePrefix + "Keys",
            isOpen = false,
        )
        val group = generate(
            schema = groupBy.groups.schema.value,
            name = markerInterfacePrefix + "Groups",
            isOpen = false,
        )

        val keyTypeName = (key.typeCastGenerator as TypeCastGenerator.DataFrameApi).types.single()
        val groupTypeName = (group.typeCastGenerator as TypeCastGenerator.DataFrameApi).types.single()

        return CodeWithTypeCastGenerator(
            declarations = key.declarations + "\n" + group.declarations,
            typeCastGenerator = TypeCastGenerator.DataFrameApi(keyTypeName, groupTypeName),
        )
    }

    fun generate(schema: DataFrameSchema, name: String, isOpen: Boolean): CodeWithTypeCastGenerator {
        val result = generator.generate(
            schema = schema,
            name = name,
            fields = true,
            extensionProperties = true,
            isOpen = isOpen,
            visibility = MarkerVisibility.IMPLICIT_PUBLIC,
            knownMarkers = registeredMarkers
                .filterKeys { it.visibility != KVisibility.PRIVATE }
                .values,
        )

        result.newMarkers.forEach {
            generatedMarkers[it.name] = it
        }
        return if (schema.hasExperimentalInstant()) {
            result.code.copy(
                declarations = "@file:OptIn(kotlin.time.ExperimentalTime::class)\n" + result.code.declarations,
            )
        } else {
            result.code
        }
    }

    private fun DataFrameSchema.hasExperimentalInstant(): Boolean =
        columns.values.any { column ->
            when (column) {
                is ColumnSchema.Frame -> column.schema.hasExperimentalInstant()
                is ColumnSchema.Group -> column.schema.hasExperimentalInstant()
                is ColumnSchema.Value -> column.type.isSubtypeOf(typeOf<Instant?>())
            }
        }

    override fun process(markerClass: KClass<*>): Code {
        val newMarkers = mutableListOf<Marker>()

        fun resolve(clazz: KClass<*>): Marker {
            val processed = registeredMarkers[clazz]
            if (processed != null) return processed
            val temp = generatedMarkers[clazz.simpleName!!]
            if (temp != null) {
                val baseClasses = clazz.superclasses.filter { it != Any::class }

                val baseClassNames = baseClasses
                    .map { it.simpleName!! }
                    .sorted()

                val tempBaseClassNames = temp.superMarkers.map { it.value.shortName }.sorted()

                if (baseClassNames == tempBaseClassNames) {
                    val newBaseMarkers = baseClasses.map { resolve(it) }
                    val newMarker = Marker(
                        name = clazz.qualifiedName!!,
                        isOpen = temp.isOpen,
                        fields = temp.fields,
                        superMarkers = newBaseMarkers,
                        visibility = MarkerVisibility.IMPLICIT_PUBLIC,
                        klass = clazz,
                    )
                    registeredMarkers[markerClass] = newMarker
                    generatedMarkers.remove(temp.name)
                    return newMarker
                }
            }
            val marker = MarkersExtractor.get(markerClass)
            registeredMarkers[markerClass] = marker
            newMarkers.add(marker)
            return marker
        }

        val marker = resolve(markerClass)
        return newMarkers.map { generator.generate(marker, InterfaceGenerationMode.None, true).declarations }.join()
    }
}
