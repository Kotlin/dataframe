@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.mean
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
    typealias OPERATION = Nothing

    /** {@set [OPERATION] operation} */
    typealias SetDefaultOperationArg = Nothing

    /** Provide a new value for every selected cell given its column using a [column expression][DfColumnExpression]. */
    interface ColumnExpression {

        /**
         * {@include [ColumnExpression]}
         *
         * For example:
         *
         * `df.`{@get [OPERATION]}` { `[mean][DataColumn.mean]`(skipNA = true) }`
         *
         * `df.`{@get [OPERATION]}` { `[count][DataColumn.count]` { it > 10 } }`
         * @include [SetDefaultOperationArg]
         */
        typealias WithExample = Nothing
    }

    /** [Column Expression][ColumnExpression] */
    typealias ColumnExpressionLink = Nothing
}
