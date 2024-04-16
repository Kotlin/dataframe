package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpressionLink
import org.jetbrains.kotlinx.dataframe.RowColumnExpression as DfRowColumnExpression

/**
 * ## Expressions Given Row and Column
 * Expressing values using a "Row-Column Expression" can occur exclusively in a
 * [Row Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression].
 */
internal interface ExpressionsGivenRowAndColumn {

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    // Using <code>` notation to not create double `` when including
    interface SetDefaultOperationArg

    /** Provide a new value for every selected cell given both its row and column using a [row-column expression][DfRowColumnExpression]. */
    interface RowColumnExpression {

        /**
         * Provide a new value for every selected cell given both its row and column using a [row-column expression][org.jetbrains.kotlinx.dataframe.RowColumnExpression].
         *
         * For example:
         *
         * `df.`<code>`operation`</code>` { row, col ->`
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;`row.age / col.`[mean][DataColumn.mean]`(skipNA = true)`
         *
         * `}`
         *
         *
         */
        interface WithExample
    }

    /** [Row Column Expression][RowColumnExpression] */
    interface RowColumnExpressionLink
}
