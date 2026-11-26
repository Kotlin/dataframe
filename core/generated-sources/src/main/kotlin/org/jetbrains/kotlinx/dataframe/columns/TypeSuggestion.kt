package org.jetbrains.kotlinx.dataframe.columns

import kotlin.reflect.KType

/**
 * The suggestion of how to find a column type.
 *
 * The suggestion can either be:
 *
 * - [<code>Infer</code>][Infer] - The library will try to infer the type by checking all the values.
 * - [<code>InferWithUpperbound</code>][InferWithUpperbound] - The library will infer the type by checking all the values taking a given upper bound into account.
 * - [<code>Use</code>][Use] - The library will use the specified type without inference.
 *
 * It can be either an [<code>exact type</code>][Use] or an [<code>upper bound</code>][InferWithUpperbound] of possible types
 * after which the library will infer the exact type.
 */
public sealed interface TypeSuggestion {

    public companion object {

        /** Creates a new [<code>TypeSuggestion</code>][TypeSuggestion] instance based on the given parameters. */
        public fun create(suggestedType: KType?, guessType: Boolean): TypeSuggestion =
            when {
                suggestedType != null && guessType -> InferWithUpperbound(suggestedType)
                suggestedType != null && !guessType -> Use(suggestedType)
                else -> Infer // no type was suggested, so we need to guess, no matter what guessType is
            }
    }

    /** The library will try to infer the type by checking all the values. */
    public data object Infer : TypeSuggestion

    /** The library will infer the type by checking all the values taking a given upper bound into account. */
    public data class InferWithUpperbound(val upperbound: KType) : TypeSuggestion

    /** The library will use the specified type without inference. */
    public data class Use(val type: KType) : TypeSuggestion
}
