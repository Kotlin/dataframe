package org.jetbrains.kotlinx.dataframe.plugin

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.junit.Test
import kotlin.reflect.typeOf

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

    data class Data(val a: Int)

    class Subject1 : AbstractInterpreter<Data>() {
        val Arguments.a by arg { i: Int -> Data(i) }

        override fun Arguments.interpret(): Data {
            return a
        }
    }

    @Test
    fun `custom argument converter`() {
        val subject1 = Subject1()
        subject1.interpret(mapOf("a" to 1)) shouldBe Data(1)
        subject1.expectedArguments.first().klass shouldBe typeOf<Int>()
    }
}
