package org.jetbrains.kotlinx.dataframe.codeGen

/**
 * Class representing generated code for a [Marker]. The generated code can potentially span multiple snippets.
 * In that case, any [converter] is always added to the last snippet in the chain.
 *
 * @param snippets The list of generated snippets. They will be executed in order
 * @param converter Optional converter for the [Marker], such as a [org.jetbrains.kotlinx.dataframe.api.cast], often used for Jupyter.
 */
public data class CodeWithConverter(val snippets: List<Code>, val converter: (VariableName) -> Code = EMPTY_CONVERTER) {
    public constructor(snippet: String, converter: (VariableName) -> Code = EMPTY_CONVERTER) : this(
        listOf(snippet),
        converter,
    )

    public companion object {
        public val EMPTY_DECLARATIONS: List<Code> = emptyList<Code>()
        public val EMPTY_CONVERTER: (VariableName) -> Code = { it }
        public val EMPTY: CodeWithConverter = CodeWithConverter(EMPTY_DECLARATIONS, EMPTY_CONVERTER)
    }

    val hasDeclarations: Boolean get() = snippets.any { it.isNotEmpty() }

    val hasConverter: Boolean get() = converter("it").trim() != "it"

    public fun with(name: VariableName): List<Code> =
        when {
            !hasConverter -> snippets
            !hasDeclarations -> listOf(converter(name))
            else -> snippets + converter(name)
        }
}

public typealias Code = String
public typealias VariableName = String
