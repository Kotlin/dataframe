package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

class GetTests {

    @Test
    fun `exceptions from empty dataframe`() {
        val empty = emptyDataFrame()
        shouldThrow<NoSuchElementException> {
            empty.first()
        }
        shouldThrow<NoSuchElementException> {
            empty.last()
        }
        shouldThrow<IndexOutOfBoundsException> {
            empty[0]
        }
    }

    @Test
    fun `get value from row`() {
        val a by column<Int>()
        val c by column<Int>()
        data class A(val a: Int, val b: Int, val c: Int)

        val df = dataFrameOf("a", "b")(1, 2)
        val row = df[0]

        row["a"] shouldBe 1
        row.getValue<Int>("a") shouldBe 1
        row.getValue(a) shouldBe 1
        row.getValue(A::a) shouldBe 1

        row.getValueOrNull<Int>("c") shouldBe null
        row.getValueOrNull(c) shouldBe null
        row.getValueOrNull(A::c) shouldBe null

        shouldThrow<IllegalArgumentException> { row.getValue<Int>("c") }
        shouldThrow<IllegalArgumentException> { row.getValue(c) }
        shouldThrow<IllegalArgumentException> { row.getValue(A::c) }

        val added = df.add(A::c) { "3" }[0]

        shouldThrow<ClassCastException> { added.getValue(c) + 1 }
        shouldThrow<ClassCastException> { added.getValue<Int>("c") + 1 }
        shouldThrow<ClassCastException> { added.getValue(A::c) + 1 }
    }
}
