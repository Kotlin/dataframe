package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.api.expr

/**
 * {@comment
 *    Column Expression KDoc-topic.
 *    Link to it with `@include [ColumnExpressionLink]`.
 * }
 *
 * ## Column Expression
 * In many DSLs, the lambda [`expr {}`][expr] can be used to
 * create a new column by defining an expression to fill up each row.
 *
 * [`expr {}`][expr] behaves like a mapping statement, iterating over the object it's called on.
 */
internal interface ColumnExpression {

    /**
     * Creates a temporary new column by defining an expression to fill up each row.
     *
     * See {@include [ColumnExpressionLink]} for more information.
     */
    typealias CommonDocs = Nothing
}

/** [Column Expression][ColumnExpression] */
@ExcludeFromSources
internal typealias ColumnExpressionLink = Nothing
