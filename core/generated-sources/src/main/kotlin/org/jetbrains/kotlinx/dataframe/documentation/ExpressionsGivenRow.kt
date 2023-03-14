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
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.AddDataRowNote
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpressionLink
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpressionLink
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
 * Note:
 *
 * [update with][org.jetbrains.kotlinx.dataframe.api.Update.with]- and [add][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [AddDataRow][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [RowValueExpression][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [RowExpression][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([AddDataRow.newValue][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 * A Row Expression is similar to a [Row Condition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows] but that expects a [Boolean][Boolean] as result.
 */
internal interface ExpressionsGivenRow {

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    interface SetDefaultOperationArg

    /**
     * [update with][org.jetbrains.kotlinx.dataframe.api.Update.with]- and [add][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [AddDataRow] instead of [DataRow] as the DSL's receiver type.
     * This is an extension to [RowValueExpression][DfRowValueExpression] and
     * [RowExpression][DfRowExpression] that provides access to
     * the modified/generated value of the preceding row ([AddDataRow.newValue]).
     */
    interface AddDataRowNote

    /** Provide a new value for every selected cell given its row using a [row expression][DfRowExpression]. */
    interface RowExpression {

        /**
         * Provide a new value for every selected cell given its row using a [row expression][org.jetbrains.kotlinx.dataframe.RowExpression].
         *
         * For example:
         *
         * `df.`operation` { name.firstName + " " + name.lastName }`
         *
         * `df.`operation` { 2021 - age }`
         *
         */
        interface WithExample
    }

    /** [Row Expression][RowExpression.WithExample] */
    interface RowExpressionLink

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
         * `df.`operation` { name.firstName + " from " + it }`
         *
         * `df.`operation` { it.uppercase() }`
         *
         */
        interface WithExample
    }

    /** [Row Value Expression][RowValueExpression.WithExample] */
    interface RowValueExpressionLink
}

/** [Row Expression][ExpressionsGivenRow] */
internal interface RowExpressionsLink
