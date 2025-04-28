package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.junit.Test
import kotlin.reflect.typeOf

class UnfoldTests {
    @Test
    fun unfold() {
        val df = dataFrameOf(
            "col" to listOf(A("123", 321)),
        )

        val res = df.unfold { col("col") }
        res[pathOf("col", "str")][0] shouldBe "123"
        res[pathOf("col", "i")][0] shouldBe 321
    }

    @Test
    fun `unfold deep`() {
        val df1 = dataFrameOf(
            "col" to listOf(
                Group(
                    "1",
                    listOf(
                        Person("Alice", "Cooper", 15, "London"),
                        Person("Bob", "Dylan", 45, "Dubai"),
                    ),
                ),
                Group(
                    "2",
                    listOf(
                        Person("Charlie", "Daniels", 20, "Moscow"),
                        Person("Charlie", "Chaplin", 40, "Milan"),
                    ),
                ),
            ),
        )

        df1.unfold { col("col") }[pathOf("col", "participants")].type() shouldBe typeOf<List<Person>>()

        df1.unfold(maxDepth = 2) { col("col") }[pathOf("col", "participants")][0].shouldBeInstanceOf<AnyFrame> {
            it["firstName"][0] shouldBe "Alice"
        }
    }

    @Test
    fun `keep value type`() {
        val values = listOf(1, 2, 3, 4)
        val df2 = dataFrameOf("int" to values)
        val column = df2.unfold { col("int") }["int"]
        column.type() shouldBe typeOf<Int>()
        column.values() shouldBe values
    }

    data class A(val str: String, val i: Int)

    data class Person(
        val firstName: String,
        val lastName: String,
        val age: Int,
        val city: String?,
    )

    data class Group(val id: String, val participants: List<Person>)
}
