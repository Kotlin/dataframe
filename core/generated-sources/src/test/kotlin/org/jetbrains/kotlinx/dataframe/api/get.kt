package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.junit.Test
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

class GetTests {

    @Test
    fun `exceptions from empty dataframe`() {
        val empty = DataFrame.empty()
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

    @DataSchema
    data class Schema(val a: Int)

    @Test
    fun `create typed frame column accessor`() {
        val df = dataFrameOf(
            columnOf(
                dataFrameOf("a")(1),
                dataFrameOf("a", "b")(2, 3, 4, 5)
            ).named("x")
        )
        val x by frameColumn<Schema>()
        df[x][0].a[0] shouldBe 1
        df[1][x].a[1] shouldBe 4
    }

    @Test
    fun `create typed column group accessor`() {
        val df = dataFrameOf(
            dataFrameOf("a", "b")(1, 2, 3, 4).asColumnGroup("x")
        )
        val x by columnGroup<Schema>()
        df[x][0].a shouldBe 1
        df[1][x].a shouldBe 3
    }

    @Test
    fun `throw meaningful exception when traverse columns in DataRow`() {
        val df = dataFrameOf("a")(null)
        val throwable = shouldThrowAny {
            df[0].getColumnGroup("a")
        }
        throwable.message shouldContain "Cannot cast null value of a ValueColumn to"
    }
}
