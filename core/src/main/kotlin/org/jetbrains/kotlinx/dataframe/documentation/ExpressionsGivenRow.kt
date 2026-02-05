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

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    typealias OPERATION = Nothing
    // Using <code>` notation to not create double `` when including

    /** {@set [OPERATION] <code>`operation`</code>} */
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
         * {@include [RowExpression]}
         *
         * For example:
         *
         * `df.`{@get [OPERATION]}` { name.firstName + " " + name.lastName }`
         *
         * `df.`{@get [OPERATION]}` { 2021 - age }`
         * @include [SetDefaultOperationArg]
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
         * {@include [RowValueExpression]}
         *
         * For example:
         *
         * `df.`{@get [OPERATION]}` { name.firstName + " from " + it }`
         *
         * `df.`{@get [OPERATION]}` { it.uppercase() }`
         * {@include [SetDefaultOperationArg]}
         */
        typealias WithExample = Nothing
    }

    /** [Row Value Expression][RowValueExpression.WithExample] */
    typealias RowValueExpressionLink = Nothing
}

/** [Row Expression][ExpressionsGivenRow] */
internal typealias RowExpressionsLink = Nothing
