package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.plugin.*
import org.jetbrains.kotlinx.dataframe.plugin.testing.test

@Interpretable(RowValueExpressionIdentity::class)
public fun <T, R> rowValueExpression(v: RowValueExpression<T, Any?, R>): RowValueExpression<T, Any?, R> {
    return v
}

public class RowValueExpressionIdentity : AbstractInterpreter<TypeApproximation>() {
    internal val Arguments.v: TypeApproximation by type()
    override fun Arguments.interpret(): TypeApproximation {
        return v
    }
}

internal fun rowValueExpressionTest() {
    test(id = "rowValueExpression_1", rowValueExpression<Any?, _> { 42 })
}
