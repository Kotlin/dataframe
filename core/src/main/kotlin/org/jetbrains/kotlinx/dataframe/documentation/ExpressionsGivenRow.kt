package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.*
import org.jetbrains.kotlinx.dataframe.RowExpression as DfRowExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression as DfRowValueExpression

/**
 * ## Expressions Given Row
 * Expressing values using a "Row Expression" ({@include [DocumentationUrls.DataRow.RowExpressions]})
 * can occur in the following two types of operations:
 *
 * - Providing a new value for every selected cell given the row of that cell ({@include [RowExpressionLink]}),
 * for instance in [map][DataFrame.map], [add][DataFrame.add], and [insert][DataFrame.insert]
 * (using [RowExpression][DfRowExpression]).
 *
 * - Providing a new value for every selected cell given the row of that cell and its previous value ({@include [RowValueExpressionLink]}),
 * for instance in [update.with][Update.with], and [convert.notNull][Convert.notNull]
 * (using [RowValueExpression][DfRowValueExpression]).
 *
 * NOTE:
 *
 * @include [AddDataRowNote]
 *
 * A {@include [RowExpressionLink]} is similar to a {@include [RowConditionLink]} but that expects a [Boolean] as result.
 */
internal interface ExpressionsGivenRow {

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    /** {@arg [OperationArg] operation} */
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
         * {@include [RowExpression]}
         *
         * For example:
         *
         * `df.`{@includeArg [OperationArg]}` { name.firstName + " " + name.lastName }`
         *
         * `df.`{@includeArg [OperationArg]}` { 2021 - age }`
         * @include [SetDefaultOperationArg]
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
         * {@include [RowValueExpression]}
         *
         * For example:
         *
         * `df.`{@includeArg [OperationArg]}` { name.firstName + " from " + it }`
         *
         * `df.`{@includeArg [OperationArg]}` { it.uppercase() }`
         * {@include [SetDefaultOperationArg]}
         */
        interface WithExample
    }

    /** [Row Value Expression][RowValueExpression.WithExample] */
    interface RowValueExpressionLink
}

/** [Row Expression][ExpressionsGivenRow] */
internal interface RowExpressionsLink
