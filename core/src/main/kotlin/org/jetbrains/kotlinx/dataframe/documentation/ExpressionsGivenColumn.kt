package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpressionLink
import org.jetbrains.kotlinx.dataframe.ColumnExpression as DfColumnExpression

/**
 * ## Expressions Given Column
 * Expressing values using a "Column Expression" can occur exclusively in a
 * {@include [ColumnExpressionLink]}.
 */
internal interface ExpressionsGivenColumn {

    /**
     * The key for an @arg that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    /** {@arg [OperationArg] operation} */
    interface SetDefaultOperationArg

    /** Provide a new value for every selected cell given its column using a [column expression][DfColumnExpression]. */
    interface ColumnExpression {

        /**
         * {@include [ColumnExpression]}
         *
         * For example:
         *
         * `df.`{@includeArg [OperationArg]}` { `[mean][DataColumn.mean]`(skipNA = true) }`
         *
         * `df.`{@includeArg [OperationArg]}` { `[count][DataColumn.count]` { it > 10 } }`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column Expression][ColumnExpression] */
    interface ColumnExpressionLink
}
