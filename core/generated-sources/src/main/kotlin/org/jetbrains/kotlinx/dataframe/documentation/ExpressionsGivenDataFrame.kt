package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.DataFrameExpression as DfDataFrameExpression

/**
 * ## Expressions Given DataFrame
 * Expressing values using a "DataFrame Expression" can occur exclusively in a
 * [DataFrame Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression].
 */
internal interface ExpressionsGivenDataFrame {

    /** Provide a new value for every selected dataframe using a [dataframe expression][DfDataFrameExpression]. */
    interface DataFrameExpression {

        /**
         * Provide a new value for every selected dataframe using a [dataframe expression][org.jetbrains.kotlinx.dataframe.DataFrameExpression].
         *
         * For example:
         *
         * ` { `[select][DataFrame.select]` { lastName } }`
         */
        typealias WithExample = Nothing
    }
}
