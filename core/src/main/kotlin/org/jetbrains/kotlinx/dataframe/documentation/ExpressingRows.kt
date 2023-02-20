package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.ExpressingRows.AddDataRowNote
import org.jetbrains.kotlinx.dataframe.documentation.ExpressingRows.RowExpressionLink
import org.jetbrains.kotlinx.dataframe.documentation.ExpressingRows.RowValueExpressionLink
import org.jetbrains.kotlinx.dataframe.RowExpression as DfRowExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression as DfRowValueExpression

/**
 * ## Expressing Rows
 * Expressing values using a "Row Expression" ({@include [DocumentationUrls.DataRow.RowExpressions]})
 * can occur in the following two types of operations:
 *
 * - Providing a new value for every selected row given the row ({@include [RowExpressionLink]}),
 * for instance in [map][DataFrame.map], [add][DataFrame.add], and [insert][DataFrame.insert]
 * (using [RowExpression][DfRowExpression]).
 *
 * - Providing a new value for every selected row given the row and the previous value ({@include [RowValueExpressionLink]}),
 * for instance in [update.with][Update.with], and [convert.notNull][Convert.notNull]
 * (using [RowValueExpression][DfRowValueExpression]).
 *
 * Note:
 * @include [AddDataRowNote]
 *
 * A Row Expression is similar to a {@include [RowConditionLink]} but that expects a [Boolean] as result.
 */
internal interface ExpressingRows {

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

    /** Provide a new value for every selected row given the row using a [row expression][DfRowExpression]. */
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

    /** Provide a new value for every selected row given the row and the previous value using a
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

    /** @include [ExpressingColumns.RowColumnExpression] */
    interface RowColumnExpression
}

/** [Row Expressions][ExpressingRows] */
internal interface RowExpressionsLink
