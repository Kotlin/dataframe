package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable

public data class Context @Interpretable(ContextConstructor::class) constructor(val i: Int) {
    @Interpretable(MemberFunctionId::class)
    public fun id(): Context = this
}

internal class ContextConstructor : AbstractInterpreter<Context>() {
    val Arguments.i: Int by arg()
    override fun Arguments.interpret(): Context {
        return Context(i)
    }
}

internal class MemberFunctionId : AbstractInterpreter<Context>() {
    val Arguments.receiver: Context by arg()

    override fun Arguments.interpret(): Context {
        return receiver
    }
}
