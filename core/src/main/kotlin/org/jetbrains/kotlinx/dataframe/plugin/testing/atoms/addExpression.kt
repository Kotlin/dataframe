package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.api.AddExpression
import org.jetbrains.kotlinx.dataframe.plugin.*
import org.jetbrains.kotlinx.dataframe.plugin.testing.test

@Interpretable(AddExpressionIdentity::class)
public fun <T, R> addExpression(v: AddExpression<T, R>): AddExpression<T, R> {
    return v
}

public class AddExpressionIdentity : AbstractInterpreter<TypeApproximation>() {
    internal val Arguments.v: TypeApproximation by type()
    override fun Arguments.interpret(): TypeApproximation {
        return v
    }
}

internal fun addExpressionTest() {
    test(id = "addExpression_1", call = addExpression<Any?, _> { 42 })
    test(id = "addExpression_2", call = addExpression<Any?, Any?> { 42 })
}
