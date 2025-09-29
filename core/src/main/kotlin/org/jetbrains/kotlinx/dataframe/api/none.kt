package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl

// region DataFrame

/**
 * Returns `true` if none of the rows in this [DataFrame] satisfies the given [predicate].
 *
 * {@include [org.jetbrains.kotlinx.dataframe.documentation.RowFilterDescription]}
 *
 * ### Example
 * ```kotlin
 * // Check if there is not any row where "age" is greater than 18
 * val hasNoAdults = df.none { age > 18 }
 * ```
 *
 * @param predicate A [RowFilter] lambda that takes a [DataRow] (as both `this` and `it`)
 * and returns `true` if none of the rows should be considered a match.
 * @return `true` if none of the rows satisfies the [predicate], `false` otherwise.
 * @see [DataFrame.any]
 */
public inline fun <T> DataFrame<T>.none(predicate: RowFilter<T>): Boolean = rows().none { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## None {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface NoneColumnsSelectionDsl {

    /**
     * ## None Grammar
     *
     * @include [DslGrammarTemplate]
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}**`()`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_PART]}
     * {@set [DslGrammarTemplate.COLUMN_GROUP_PART]}
     */
    public interface Grammar {

        /** [**`none`**][ColumnsSelectionDsl.none] */
        public interface PlainDslName
    }

    /**
     * ## None
     *
     * Creates an empty [ColumnsResolver] / [ColumnSet], essentially selecting no columns at all.
     *
     * This is the opposite of [all][ColumnsSelectionDsl.all].
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[groupBy][DataFrame.groupBy]`  {  `[`none`][none]`() }`
     *
     * @return An empty [ColumnsResolver].
     */
    public fun none(): ColumnsResolver<*> = ColumnListImpl<Any?>(emptyList())
}

// endregion
