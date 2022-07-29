package org.jetbrains.kotlinx.dataframe.plugin

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpreter
import org.jetbrains.kotlinx.dataframe.annotations.Present
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl
import org.jetbrains.kotlinx.dataframe.api.Infer
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
        Subject().interpret(mapOf("a" to Interpreter.Success(1), "b" to Interpreter.Success(2))) shouldBe Interpreter.Success(3)
    }

    data class Data(val a: Int)

    class Subject1 : AbstractInterpreter<Data>() {
        val Arguments.a by argConvert { i: Int -> Data(i) }

        override fun Arguments.interpret(): Data {
            return a
        }
    }

    @Test
    fun `custom argument converter`() {
        val subject1 = Subject1()
        subject1.interpret(mapOf("a" to Interpreter.Success(1))) shouldBe Interpreter.Success(Data(1))
        subject1.expectedArguments.first().klass shouldBe typeOf<Int>()
    }

    class Subject2 : AbstractInterpreter<Int>() {
        val Arguments.a by arg(defaultValue = Present(123))
        val Arguments.b by argConvert(defaultValue = Present(123)) { it: Int -> 321 }

        override fun Arguments.interpret(): Int {
            return a + b
        }
    }

    @Test
    fun `default value`() {
        Subject2().interpret(mapOf()) shouldBe Interpreter.Success(246)
    }

    class EnumSubject : AbstractInterpreter<Infer>() {
        val Arguments.v: Infer by enum()

        override fun Arguments.interpret(): Infer {
            return v
        }
    }

    @Test
    fun `enum`() {
        val res = EnumSubject().interpret(
            mapOf(
                "v" to Interpreter.Success(
                    DataFrameCallableId(
                        "org.jetbrains.kotlinx.dataframe.api",
                        "Infer",
                        "Type"
                    )
                )
            )
        )
        res shouldBe Interpreter.Success(Infer.Type)
    }

    class DataFrameIdSubject() : AbstractInterpreter<PluginDataFrameSchema>() {
        val Arguments.receiver by dataFrame()

        override val Arguments.startingSchema: PluginDataFrameSchema? get() = receiver

        override fun Arguments.interpret(): PluginDataFrameSchema {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun `starting schema`() {
        val schema = PluginDataFrameSchema(listOf(SimpleCol("col", TypeApproximationImpl("kotlin.Int", false))))
        val res = DataFrameIdSubject().startingSchema(mapOf("receiver" to Interpreter.Success(schema)))
        res shouldBe schema
    }
}
