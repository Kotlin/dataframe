package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpressionLink
import org.jetbrains.kotlinx.dataframe.RowColumnExpression as DfRowColumnExpression

/**
 * ## Expressions Given Row and Column
 * Expressing values using a "Row-Column Expression" can occur exclusively in a
 * {@include [RowColumnExpressionLink]}.
 */
internal interface ExpressionsGivenRowAndColumn {

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    /** {@arg [OperationArg] operation} */
    interface SetDefaultOperationArg

    /** Provide a new value for every selected cell given both its row and column using a [row-column expression][DfRowColumnExpression]. */
    interface RowColumnExpression {

        /**
         * @include [RowColumnExpression]
         *
         * For example:
         *
         * `df.`{@includeArg [OperationArg]}` { row, col ->`
         *
         * `row.age / col.`[mean][DataColumn.mean]`(skipNA = true)`
         *
         * `}`
         *
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Row Column Expression][RowColumnExpression] */
    interface RowColumnExpressionLink
}
