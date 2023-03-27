package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpressionLink
import org.jetbrains.kotlinx.dataframe.ColumnExpression as DfColumnExpression

/**
 * ## Expressions Given Column
 * Expressing values using a "Column Expression" can occur exclusively in a
 * [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression].
 */
internal interface ExpressionsGivenColumn {

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    interface SetDefaultOperationArg

    /** Provide a new value for every selected cell given its column using a [column expression][DfColumnExpression]. */
    interface ColumnExpression {

        /**
         * Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
         *
         * For example:
         *
         * `df.`operation` { `[mean][DataColumn.mean]`(skipNA = true) }`
         *
         * `df.`operation` { `[count][DataColumn.count]` { it > 10 } }`
         *
         */
        interface WithExample
    }

    /** [Column Expression][ColumnExpression] */
    interface ColumnExpressionLink
}
