package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.RowColumnExpression as DfRowColumnExpression

/**
 * ## Expressions Given Row and Column
 * Expressing values using a "Row-Column Expression" can occur exclusively in a
 * [<code>Row Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression].
 */
internal interface ExpressionsGivenRowAndColumn {

    // Using <code>` notation to not create double `` when including

    /** Provide a new value for every selected cell given both its row and column using a [<code>row-column expression</code>][DfRowColumnExpression]. */
    interface RowColumnExpression {

        /**
         * Provide a new value for every selected cell given both its row and column using a [<code>row-column expression</code>][org.jetbrains.kotlinx.dataframe.RowColumnExpression].
         *
         * For example:
         *
         * `df.`<code>`operation`</code>` { row, col ->`
         *
         * &nbsp;&nbsp;&nbsp;&nbsp;`row.age / col.`[<code>mean</code>][DataColumn.mean]`(skipNA = true)`
         *
         * `}`
         *
         *
         */
        typealias WithExample = Nothing
    }
}
