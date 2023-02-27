package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpressionLink
import org.jetbrains.kotlinx.dataframe.DataFrameExpression as DfDataFrameExpression

/**
 * ## Expressions Given DataFrame
 * Expressing values using a "Data Frame Expression" can occur exclusively in a
 * [Data Frame Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression].
 */
internal interface ExpressionsGivenDataFrame {

    interface OperationArg

    /** Provide a new value for every selected data frame using a [dataframe expression][DfDataFrameExpression]. */
    interface DataFrameExpression {

        /**
         * Provide a new value for every selected data frame using a [dataframe expression][org.jetbrains.kotlinx.dataframe.DataFrameExpression].
         * 
         * For example:
         * 
         * {@includeArg [OperationArg]}` { `[select][DataFrame.select]` { lastName } }`
         */
        interface WithExample
    }

    /** [Data Frame Expression][DataFrameExpression] */
    interface DataFrameExpressionLink
}
