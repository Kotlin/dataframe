package org.jetbrains.kotlinx.dataframe.plugin.testing

import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.plugin.type

@Interpretable(TypeIdentity::class)
public fun <T, R> type(v: RowExpression<T, R>): RowExpression<T, R> {
    return v
}

public class TypeIdentity : AbstractInterpreter<TypeApproximation>() {
    internal val Arguments.v: TypeApproximation by type()

    override fun Arguments.interpret(): TypeApproximation {
        return v
    }
}

internal fun typeTest() {
    test(id = "type_1", call = type<Any?, _> { 42 })
}
