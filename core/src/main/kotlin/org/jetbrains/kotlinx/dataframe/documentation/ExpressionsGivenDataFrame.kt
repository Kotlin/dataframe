@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.DataFrameExpression as DfDataFrameExpression

/**
 * ## Expressions Given DataFrame
 * Expressing values using a "Data Frame Expression" can occur exclusively in a
 * {@include [DataFrameExpressionLink]}.
 */
internal interface ExpressionsGivenDataFrame {

    interface OPERATION

    /** Provide a new value for every selected data frame using a [dataframe expression][DfDataFrameExpression]. */
    interface DataFrameExpression {

        /**
         * @include [DataFrameExpression]
         *
         * For example:
         *
         * {@get [OPERATION]}` { `[select][DataFrame.select]` { lastName } }`
         */
        interface WithExample
    }

    /** [Data Frame Expression][DataFrameExpression] */
    interface DataFrameExpressionLink
}
