package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl

// region DataColumn

/** Returns `true` if none of the [values] match the given [predicate] */
public fun <T> DataColumn<T>.none(predicate: Predicate<T>): Boolean = values.none(predicate)

// endregion

// region DataFrame

/**
 * Returns `true` if none of the rows in this [DataFrame] satisfies the given [predicate].
 *
 * The [predicate] is a [RowFilter][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs] for convenient and type-safe access.
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
 * ## None [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface NoneColumnsSelectionDsl {

    /**
     * ## None Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**`none`**][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none]**`()`**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** [**`none`**][ColumnsSelectionDsl.none] */
        public typealias PlainDslName = Nothing
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
