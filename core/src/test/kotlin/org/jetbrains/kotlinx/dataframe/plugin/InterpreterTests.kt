package org.jetbrains.kotlinx.dataframe.plugin

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.junit.Test

class InterpreterTests {

    class Subject : AbstractInterpreter<Int>() {
        val Arguments.a by arg<Int>()
        val Arguments.b by arg<Int>()

        override fun Arguments.interpret(): Int {
            return a + b
        }
    }

    @Test
    fun `expected args == actual args`() {
        Subject().interpret(mapOf("a" to 1, "b" to 2)) shouldBe 3
    }
}
