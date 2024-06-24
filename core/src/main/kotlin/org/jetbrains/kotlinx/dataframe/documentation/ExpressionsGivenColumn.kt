@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpressionLink
import org.jetbrains.kotlinx.dataframe.ColumnExpression as DfColumnExpression

/**
 * ## Expressions Given Column
 * Expressing values using a "Column Expression" can occur exclusively in a
 * {@include [ColumnExpressionLink]}.
 */
internal interface ExpressionsGivenColumn {
    /*
     * The key for a @set that will define the operation name for the examples below.
     * Make sure to [alias][your examples].
     */
    interface OperationArg

    /** {@set [OperationArg] operation} */
    interface SetDefaultOperationArg

    /** Provide a new value for every selected cell given its column using a [column expression][DfColumnExpression]. */
    interface ColumnExpression {
        /**
         * {@include [ColumnExpression]}
         *
         * For example:
         *
         * `df.`{@get [OperationArg]}` { `[mean][DataColumn.mean]`(skipNA = true) }`
         *
         * `df.`{@get [OperationArg]}` { `[count][DataColumn.count]` { it > 10 } }`
         * @include [SetDefaultOperationArg]
         */
        interface WithExample
    }

    /** [Column Expression][ColumnExpression] */
    interface ColumnExpressionLink
}
