package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.DataFrameExpression as DfDataFrameExpression

/**
 * ## Expressions Given DataFrame
 * Expressing values using a "DataFrame Expression" can occur exclusively in a
 * [<code>DataFrame Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression].
 */
internal interface ExpressionsGivenDataFrame {

    /** Provide a new value for every selected dataframe using a [<code>dataframe expression</code>][DfDataFrameExpression]. */
    interface DataFrameExpression {

        /**
         * Provide a new value for every selected dataframe using a [<code>dataframe expression</code>][org.jetbrains.kotlinx.dataframe.DataFrameExpression].
         *
         * For example:
         *
         * ` { `[<code>select</code>][DataFrame.select]` { lastName } }`
         */
        typealias WithExample = Nothing
    }
}
