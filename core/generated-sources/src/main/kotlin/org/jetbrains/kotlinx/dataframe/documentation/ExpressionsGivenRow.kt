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
 * - Providing a new value for every selected cell given the row of that cell ([Row Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpression.WithExample]),
 * for instance in [map][DataFrame.map], [add][DataFrame.add], and [insert][DataFrame.insert]
 * (using [RowExpression][DfRowExpression]).
 *
 * - Providing a new value for every selected cell given the row of that cell and its previous value ([Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]),
 * for instance in [update.with][Update.with], and [convert.notNull][Convert.notNull]
 * (using [RowValueExpression][DfRowValueExpression]).
 *
 * NOTE:
 *
 * [update with][org.jetbrains.kotlinx.dataframe.api.Update.with]-,
 * [convert with][org.jetbrains.kotlinx.dataframe.api.Convert.with]-
 * and [add][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [AddDataRow][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [RowValueExpression][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [RowExpression][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([AddDataRow.newValue][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 *
 * A [Row Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpression.WithExample] is similar to a [Row Condition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows] but that expects a [Boolean] as result.
 */
internal interface ExpressionsGivenRow {

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    typealias OPERATION = Nothing
    // Using <code>` notation to not create double `` when including

    typealias SetDefaultOperationArg = Nothing

    /**
     * [update with][org.jetbrains.kotlinx.dataframe.api.Update.with]-,
     * [convert with][org.jetbrains.kotlinx.dataframe.api.Convert.with]-
     * and [add][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [AddDataRow] instead of [DataRow] as the DSL's receiver type.
     * This is an extension to [RowValueExpression][DfRowValueExpression] and
     * [RowExpression][DfRowExpression] that provides access to
     * the modified/generated value of the preceding row ([AddDataRow.newValue]).
     */
    typealias AddDataRowNote = Nothing

    /** Provide a new value for every selected cell given its row using a [row expression][DfRowExpression]. */
    interface RowExpression {

        /**
         * Provide a new value for every selected cell given its row using a [row expression][org.jetbrains.kotlinx.dataframe.RowExpression].
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

    /** [Row Expression][RowExpression.WithExample] */
    typealias RowExpressionLink = Nothing

    /** Provide a new value for every selected cell given its row and its previous value using a
     * [row value expression][DfRowValueExpression].
     */
    interface RowValueExpression {

        /**
         * Provide a new value for every selected cell given its row and its previous value using a
         * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
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

    /** [Row Value Expression][RowValueExpression.WithExample] */
    typealias RowValueExpressionLink = Nothing
}

/** [Row Expression][ExpressionsGivenRow] */
internal typealias RowExpressionsLink = Nothing
