package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl

// region DataColumn

/**
 * Returns `true` if none of the [<code>values</code>][values] match the given [<code>predicate</code>][predicate].
 *
 * For more information: [See `none` on the documentation website.](https://kotlin.github.io/dataframe/none.html)
 */
public fun <T> DataColumn<T>.none(predicate: Predicate<T>): Boolean = values.none(predicate)

// endregion

// region DataFrame

/**
 * Returns `true` if none of the rows in this [<code>DataFrame</code>][DataFrame] satisfies the given [<code>predicate</code>][predicate].
 *
 * For more information: [See `none` on the documentation website.](https://kotlin.github.io/dataframe/none.html)
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 * ### Example
 * ```kotlin
 * // Check if there is not any row where "age" is greater than 18
 * val hasNoAdults = df.none { age > 18 }
 * ```
 *
 * @param predicate A [<code>RowFilter</code>][RowFilter] lambda that takes a [<code>DataRow</code>][DataRow] (as both `this` and `it`)
 * and returns `true` if none of the rows should be considered a match.
 * @return `true` if none of the rows satisfies the [<code>predicate</code>][predicate], `false` otherwise.
 * @see [DataFrame.any]
 */
public inline fun <T> DataFrame<T>.none(predicate: RowFilter<T>): Boolean = rows().none { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## None [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface NoneColumnsSelectionDsl {

    /**
     * ## None Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
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
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>**`none`**</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none]**`()`**
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

        /** [<code>**`none`**</code>][ColumnsSelectionDsl.none] */
        public typealias PlainDslName = Nothing
    }

    /**
     * ## None
     *
     * Creates an empty [<code>ColumnsResolver</code>][ColumnsResolver] / [<code>ColumnSet</code>][ColumnSet], essentially selecting no columns at all.
     *
     * This is the opposite of [<code>all</code>][ColumnsSelectionDsl.all].
     *
     * For more information: [See `none` in the Columns Selection DSL on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#none)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>groupBy</code>][DataFrame.groupBy]`  {  `[<code>`none`</code>][none]`() }`
     *
     * @return An empty [<code>ColumnsResolver</code>][ColumnsResolver].
     */
    public fun none(): ColumnsResolver<*> = ColumnListImpl<Any?>(emptyList())
}

// endregion
