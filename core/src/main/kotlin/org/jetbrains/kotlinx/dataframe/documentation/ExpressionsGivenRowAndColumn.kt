package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.RowColumnExpression as DfRowColumnExpression

/**
 * ## Expressions Given Row and Column
 * Expressing values using a "Row-Column Expression" can occur exclusively in a
 * {@include [RowColumnExpressionLink]}.
 */
internal interface ExpressionsGivenRowAndColumn {

    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    typealias OPERATION = Nothing
    // Using <code>` notation to not create double `` when including

    /** {@set [OPERATION] <code>`operation`</code>} */
    typealias SetDefaultOperationArg = Nothing

    /** Provide a new value for every selected cell given both its row and column using a [row-column expression][DfRowColumnExpression]. */
    interface RowColumnExpression {

        /**
         * @include [RowColumnExpression]
         *
         * For example:
         *
         * `df.`{@get [OPERATION]}` { row, col ->`
         *
         * {@include [Indent]}`row.age / col.`[mean][DataColumn.mean]`(skipNA = true)`
         *
         * `}`
         *
         * @include [SetDefaultOperationArg]
         */
        typealias WithExample = Nothing
    }

    /** [Row Column Expression][RowColumnExpression] */
    typealias RowColumnExpressionLink = Nothing
}
