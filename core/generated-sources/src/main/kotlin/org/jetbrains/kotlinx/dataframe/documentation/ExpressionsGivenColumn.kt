package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.ColumnExpression as DfColumnExpression

/**
 * ## Expressions Given Column
 * Expressing values using a "Column Expression" can occur exclusively in a
 * [<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression].
 */
internal interface ExpressionsGivenColumn {

    /** Provide a new value for every selected cell given its column using a [<code>column expression</code>][DfColumnExpression]. */
    interface ColumnExpression {

        /**
         * Provide a new value for every selected cell given its column using a [<code>column expression</code>][org.jetbrains.kotlinx.dataframe.ColumnExpression].
         *
         * For example:
         *
         * `df.`operation` { `[<code>mean</code>][DataColumn.mean]`(skipNA = true) }`
         *
         * `df.`operation` { `[<code>count</code>][DataColumn.count]` { it > 10 } }`
         *
         */
        typealias WithExample = Nothing
    }
}
