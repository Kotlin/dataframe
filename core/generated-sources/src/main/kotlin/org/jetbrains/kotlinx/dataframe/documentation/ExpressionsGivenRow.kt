package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.AddDataRow
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.Update
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.RowExpression as DfRowExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression as DfRowValueExpression

/**
 * ## Expressions Given Row
 * Expressing values using a "Row Expression" ([See Row Expressions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-expressions))
 * can occur in the following two types of operations:
 *
 * - Providing a new value for every selected cell given the row of that cell ([<code>Row Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpression.WithExample]),
 * for instance in [<code>map</code>][DataFrame.map], [<code>add</code>][DataFrame.add], and [<code>insert</code>][DataFrame.insert]
 * (using [<code>RowExpression</code>][DfRowExpression]).
 *
 * - Providing a new value for every selected cell given the row of that cell and its previous value ([<code>Row Value Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]),
 * for instance in [<code>update.with</code>][Update.with], and [<code>convert.notNull</code>][Convert.notNull]
 * (using [<code>RowValueExpression</code>][DfRowValueExpression]).
 *
 * NOTE:
 *
 * [<code>update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]-,
 * [<code>convert with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with]-
 * and [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [<code>AddDataRow</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [<code>RowValueExpression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([<code>AddDataRow.newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 *
 * A [<code>Row Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpression.WithExample] is similar to a [<code>Row Condition</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows] but that expects a [<code>Boolean</code>][Boolean] as result.
 */
internal interface ExpressionsGivenRow {

    // Using <code>` notation to not create double `` when including

    /**
     * Provide a new value for every selected cell given its row using a [<code>row expression</code>][DfRowExpression].
     *
     * Fore more information, [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
     */
    interface RowExpression {

        /**
         * Provide a new value for every selected cell given its row using a [<code>row expression</code>][org.jetbrains.kotlinx.dataframe.RowExpression].
         *
         * Fore more information, [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
         *
         * For example:
         *
         * `df.`<code>`operation`</code>` { name.firstName + " " + name.lastName }`
         *
         * `df.`<code>`operation`</code>` { 2021 - age }`
         *
         */
        typealias WithExample = Nothing
    }

    /** Provide a new value for every selected cell given its row and its previous value using a
     * [<code>row value expression</code>][DfRowValueExpression].
     *
     * Fore more information, [See RowValueExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowvalueexpression)
     */
    interface RowValueExpression {

        /**
         * Provide a new value for every selected cell given its row and its previous value using a
         * [<code>row value expression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression].
         *
         * Fore more information, [See RowValueExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowvalueexpression)
         *
         * For example:
         *
         * `df.`<code>`operation`</code>` { name.firstName + " from " + it }`
         *
         * `df.`<code>`operation`</code>` { it.uppercase() }`
         *
         */
        typealias WithExample = Nothing
    }
}
