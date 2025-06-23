package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.api.cast

/**
 * Class representing generated code declarations for a [Marker].
 *
 * @param declarations The generated code.
 * @param typeCastGenerator Optional [TypeCastGenerator] for the [Marker], see [TypeCastGenerator] for
 *   more information.
 */
public data class CodeWithTypeCastGenerator(
    val declarations: Code,
    val typeCastGenerator: TypeCastGenerator = TypeCastGenerator.Empty,
) {

    public companion object {
        public const val EMPTY_DECLARATIONS: Code = ""

        @Deprecated("", ReplaceWith("TypeCastGenerator.Empty"))
        public val EMPTY_CONVERTER: (Expression) -> Code = { it }
        public val EMPTY: CodeWithTypeCastGenerator = CodeWithTypeCastGenerator(EMPTY_DECLARATIONS)
    }

    val hasDeclarations: Boolean get() = declarations.isNotBlank()

    val hasCaster: Boolean
        get() = typeCastGenerator !is TypeCastGenerator.Empty &&
            typeCastGenerator("it").trim() != "it"

    public fun declarationsWithCastExpression(expression: Expression): Code =
        when {
            !hasCaster -> declarations
            !hasDeclarations -> typeCastGenerator(expression)
            else -> declarations + "\n" + typeCastGenerator(expression)
        }
}

public typealias Code = String
public typealias Expression = String

/**
 * A [TypeCastGenerator] can generate [Code] given an [Expression] that casts or converts
 * it to a predefined target type.
 *
 * To create a [TypeCastGenerator] that, for instance, casts everything you pass to [Any?][Any]:
 * ```kt
 * val myCastGenerator = TypeCastGenerator { expression -> "($expression as Any?)" }
 *
 * myCastGenerator.addCastTo("myVariable") == "(myVariable as Any?)"
 * ```
 *
 * @see TypeCastGenerator.Empty
 * @see TypeCastGenerator.DataFrameApi
 */
public fun interface TypeCastGenerator {
    public fun addCastTo(expression: Expression): Code

    public operator fun invoke(expression: Expression): Code = addCastTo(expression)

    public object Empty : TypeCastGenerator by (TypeCastGenerator { it })

    /**
     * [TypeCastGenerator] that uses the [cast] functions of the DataFrame API.
     *
     * NOTE: This generator assumes there's a `.cast<>()` function available that can be called on your
     * specific [Expression]. It will cause runtime errors when there isn't one.
     */
    public class DataFrameApi private constructor(public val types: Array<out String>) : TypeCastGenerator {
        override fun addCastTo(expression: Expression): Code =
            if (types.isEmpty()) {
                "$expression.cast()"
            } else {
                "$expression.cast<${types.joinToString()}>()"
            }

        public companion object {
            public operator fun invoke(vararg types: String): TypeCastGenerator = DataFrameApi(types)
        }
    }
}
