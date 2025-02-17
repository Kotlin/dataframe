package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.VariableName

/**
 * Class representing generated code declarations for a [Marker].
 *
 * @param declarations The generated code.
 * @param converter Optional converter for the [Marker], such as a [org.jetbrains.kotlinx.dataframe.api.cast], often used for Jupyter.
 */
public data class CodeWithConverter(val declarations: Code, val converter: (VariableName) -> Code = EMPTY_CONVERTER) {

    public companion object {
        public const val EMPTY_DECLARATIONS: Code = ""
        public val EMPTY_CONVERTER: (VariableName) -> Code = { it }
        public val EMPTY: CodeWithConverter = CodeWithConverter(EMPTY_DECLARATIONS, EMPTY_CONVERTER)
    }

    val hasDeclarations: Boolean get() = declarations.isNotBlank()

    val hasConverter: Boolean get() = converter("it").trim() != "it"

    public fun with(name: VariableName): Code =
        when {
            !hasConverter -> declarations
            !hasDeclarations -> converter(name)
            else -> declarations + "\n" + converter(name)
        }
}
