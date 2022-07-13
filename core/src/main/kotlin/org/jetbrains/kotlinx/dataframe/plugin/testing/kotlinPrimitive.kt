package org.jetbrains.kotlinx.dataframe.plugin.testing

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable

@Interpretable(KotlinPrimitive::class)
public fun kotlinPrimitive(v: Any?): Any? {
    return v
}

public class KotlinPrimitive : AbstractInterpreter<Any?>() {
    public val Arguments.v: Any? by arg<Any?>()

    override fun Arguments.interpret(): Any? {
        return v
    }
}
