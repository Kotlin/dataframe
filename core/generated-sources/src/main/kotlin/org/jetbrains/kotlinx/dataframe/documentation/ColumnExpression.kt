package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.api.expr

/**
 *
 *
 * ## Column Expression
 * In many DSLs, the lambda [<code>`expr {}`</code>][expr] can be used to
 * create a new column by defining an expression to fill up each row.
 *
 * [<code>`expr {}`</code>][expr] behaves like a mapping statement, iterating over the object it's called on.
 */
internal interface ColumnExpression {

    /**
     * Creates a temporary new column by defining an expression to fill up each row.
     *
     * See [<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] for more information.
     */
    typealias CommonDocs = Nothing
}
