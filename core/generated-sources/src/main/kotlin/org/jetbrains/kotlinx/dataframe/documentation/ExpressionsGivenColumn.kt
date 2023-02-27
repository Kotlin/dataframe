package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.ColumnExpression as DfColumnExpression
import org.jetbrains.kotlinx.dataframe.RowColumnExpression as DfRowColumnExpression

/**
 * ## Expressions Given Column
 *
 * TODO
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
         */
        interface WithExample
    }

    /** [Column Expression][ColumnExpression] */
    interface ColumnExpressionLink

    /** Provide a new value for every selected cell given both its row and column using a [row-column expression][DfRowColumnExpression]. */
    interface RowColumnExpression {

        /**
         * Provide a new value for every selected cell given both its row and column using a [row-column expression][org.jetbrains.kotlinx.dataframe.RowColumnExpression].
         * 
         * For example:
         * 
         * `df.`operation` { row, col ->`
         * 
         * `row.age / col.`[mean][DataColumn.mean]`(skipNA = true)`
         * 
         * `}`
         */
        interface WithExample
    }

    /** [Row Column Expression][RowColumnExpression] */
    interface RowColumnExpressionLink
}
