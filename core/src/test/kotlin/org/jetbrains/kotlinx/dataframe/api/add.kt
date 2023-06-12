package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.junit.Test
import kotlin.reflect.typeOf

class AddTests {

    @Test
    fun `add with new`() {
        val x by columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4)
        val df = dataFrameOf(x)
        val added = df.add("Y") { if (x() == 0) 0 else (prev()?.newValue() ?: 0) + 1 }
        val expected = listOf(1, 2, 0, 1, 2, 3, 4, 0, 1, 2)
        added["Y"].values() shouldBe expected
    }

    @Test
    fun `throw for newValue at the next row`() {
        val x by columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4)
        val df = dataFrameOf(x)
        shouldThrow<IndexOutOfBoundsException> {
            df.add("y") { next()?.newValue() ?: 1 }
        }
    }

    private fun <T> AnyFrame.addValue(value: T) = add("value") { listOf(value) }

    @Test
    fun `add with generic function`() {
        val df = dataFrameOf("a")(1).addValue(2)
        df["value"].type() shouldBe typeOf<List<Any?>>()
    }

    @DataSchema
    data class Row(val x: Int, val y: String)

    @Test
    fun `add with limited type`() {
        val df = listOf(Row(1, "a"), Row(2, "b"), Row(3, "c")).toDataFrame()

        val stringColumn = column<String>("stringColumn")

        df.add(stringColumn) { y }
//        df.add(stringColumn) { x }

//        df.add<Any, Row>(Row::y) { x }
//        df.add<Int, Row>(Row::x) { x }
    }
}
