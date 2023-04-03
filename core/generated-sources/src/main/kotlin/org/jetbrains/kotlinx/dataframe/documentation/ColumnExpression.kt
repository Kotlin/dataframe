package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.api.*

/**
 * ## Column Expression
 * In many DSLs, the lambda `expr {}` can be used to
 * create a temporary new column by defining an expression to fill up each row.
 *
 * These DSLs include (but are not limited to):
 * [The Add DSL][AddDsl.expr], [The Columns Selection DSL][ColumnsSelectionDsl.expr], and
 * [The Create DataFrame DSL][CreateDataFrameDsl.expr].
 *
 * The `expr {}` call functions like a mapping statement iterating over the object it's called on.
 */
internal interface ColumnExpression {

    /**
     * ## Column Expression
     * Create a temporary new column by defining an expression to fill up each row.
     *
     * See [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] for more information.
     */
    interface CommonDocs
}

/** [Column Expression][ColumnExpression] */
internal interface ColumnExpressionLink
