package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable

@Interpretable(InterpretationError::class)
fun interpretationError() {}

class InterpretationError : AbstractInterpreter<Unit>() {
    override fun Arguments.interpret() {
        error("Hi")
    }
}

