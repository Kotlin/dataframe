package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class ContainsTests {

    @Test
    fun `column contains`() {
        val col by columnOf(1, 3, 5)
        col.contains(3) shouldBe true
        col.contains(2) shouldBe false
    }

    @Test
    fun `column group contains`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        val col = df.asColumnGroup("col")
        col.contains(df[0]) shouldBe true
        col.contains(df.update("b").withValue(0)[0]) shouldBe false
    }

    @Test
    fun `contains column`() {
        val a by column<Int>()
        val df = dataFrameOf("a")(1, 2)
        (a in df) shouldBe true
        df.containsColumn(a) shouldBe true
        df.containsColumn("a") shouldBe true
        df.containsColumn(df["a"]) shouldBe true
        val b by column<Int>()
        (b in df) shouldBe false
        df.containsColumn(b) shouldBe false
    }

    @Test
    fun `contains nested column`() {
        val g by columnGroup()
        val a by g.column<Int>()

        val df = dataFrameOf("a")(1, 2).group("a").into("g")
        (a in df) shouldBe true
    }

    @Test
    fun `row contains key`() {
        val a by column<Int>()
        val b by column<Int>()
        data class A(val a: Int, val b: Int)

        val df = dataFrameOf("a")(1, 2)
        val row = df[0]

        row.containsKey("a") shouldBe true
        row.containsKey(a) shouldBe true
        row.containsKey(A::a) shouldBe true
        (A::a in row) shouldBe true
        (a in row) shouldBe true

        row.containsKey("b") shouldBe false
        row.containsKey(b) shouldBe false
        row.containsKey(A::b) shouldBe false
    }
}
