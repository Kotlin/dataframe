package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.plugin.testing.test

//public data class Context @Interpretable(ContextConstructor::class) constructor(val i: Int) {
//    @Interpretable(MemberFunctionId::class)
//    public fun id(): Context = this
//}
//
//internal class ContextConstructor : AbstractInterpreter<Context>() {
//    val Arguments.i: Int by arg()
//    override fun Arguments.interpret(): Context {
//        return Context(i)
//    }
//}

@Interpretable(TypeParameterId::class)
public fun <T> typeParameter() { }

internal class TypeParameterId : AbstractInterpreter<TypeApproximation>() {
    val Arguments.typeArg0: TypeApproximation by arg()

    override fun Arguments.interpret(): TypeApproximation {
        return typeArg0
    }
}

internal fun testTypeParameter() {
    test(id = "typeParameter_1", call = typeParameter<Int>())
}
