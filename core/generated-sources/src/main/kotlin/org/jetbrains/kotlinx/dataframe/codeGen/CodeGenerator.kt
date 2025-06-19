package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGeneratorImpl
import org.jetbrains.kotlinx.dataframe.impl.codeGen.FullyQualifiedNames
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ShortNames
import org.jetbrains.kotlinx.dataframe.impl.codeGen.id
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass

public enum class InterfaceGenerationMode {
    NoFields,
    WithFields,
    Enum,
    TypeAlias,
    None,
}

public data class CodeGenResult(val code: CodeWithTypeCastGenerator, val newMarkers: List<Marker>)

public interface CodeGenerator : ExtensionsCodeGenerator {

    public fun generate(
        schema: DataFrameSchema,
        name: String,
        fields: Boolean,
        extensionProperties: Boolean,
        isOpen: Boolean,
        visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
        knownMarkers: Iterable<Marker> = emptyList(),
        readDfMethod: DefaultReadDfMethod? = null,
        fieldNameNormalizer: NameNormalizer = NameNormalizer.id(),
        asDataClass: Boolean = false,
    ): CodeGenResult

    public fun generate(
        marker: Marker,
        interfaceMode: InterfaceGenerationMode,
        extensionProperties: Boolean,
        readDfMethod: DefaultReadDfMethod? = null,
    ): CodeWithTypeCastGenerator

    public companion object {
        public fun create(useFqNames: Boolean = true): CodeGenerator =
            if (useFqNames) {
                CodeGeneratorImpl(FullyQualifiedNames)
            } else {
                CodeGeneratorImpl(ShortNames)
            }
    }
}

@PublishedApi
internal fun CodeGenerator.generate(
    markerClass: KClass<*>,
    interfaceMode: InterfaceGenerationMode,
    extensionProperties: Boolean,
): CodeWithTypeCastGenerator =
    generate(
        MarkersExtractor.get(markerClass),
        interfaceMode,
        extensionProperties,
    )

public inline fun <reified T> CodeGenerator.generate(
    interfaceMode: InterfaceGenerationMode,
    extensionProperties: Boolean,
): CodeWithTypeCastGenerator = generate(T::class, interfaceMode, extensionProperties)
