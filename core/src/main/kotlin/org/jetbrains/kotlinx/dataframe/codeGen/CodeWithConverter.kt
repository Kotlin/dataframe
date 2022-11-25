package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.jupyter.api.Code
import org.jetbrains.kotlinx.jupyter.api.VariableName

/**
 * Class representing generated code declarations for a [Marker].
 *
 * @param declarations The generated code.
 * @param converter Optional converter for the [Marker], such as a [org.jetbrains.kotlinx.dataframe.api.cast], often used for Jupyter.
 */
public data class CodeWithConverter(val declarations: Code, val converter: (VariableName) -> Code = EmptyConverter) {

    public companion object {
        public const val EmptyDeclarations: Code = ""
        public val EmptyConverter: (VariableName) -> Code = { it }
        public val Empty: CodeWithConverter = CodeWithConverter(EmptyDeclarations, EmptyConverter)
    }

    val hasDeclarations: Boolean get() = declarations.isNotBlank()

    val hasConverter: Boolean get() = converter("it").trim() != "it"

    public fun with(name: VariableName): Code = when {
        !hasConverter -> declarations
        !hasDeclarations -> converter(name)
        else -> declarations + "\n" + converter(name)
    }
}
