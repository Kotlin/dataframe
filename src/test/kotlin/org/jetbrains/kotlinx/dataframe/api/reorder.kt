package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.junit.Test

class ReorderTests {

    @Test
    fun simple() {
        val df = dataFrameOf("b", "c", "a").fill(1, 0)
        df.reorder { all() }.byName().columnNames() shouldBe listOf("a", "b", "c")
        df.reorder { "a" and "c" }.byName().columnNames() shouldBe listOf("b", "a", "c")
        df.reorder { "a" and "b" }.byName().columnNames() shouldBe listOf("a", "c", "b")
    }

    @Test
    fun nested() {
        val df = dataFrameOf("b", "c", "a").fill(1, 0)
            .group("c", "a").into("a")

        df.reorder { all() }.byName().columnNames() shouldBe listOf("a", "b")

        val sorted1 = df.reorder { "a".all() }.byName()
        sorted1.columnNames() shouldBe listOf("b", "a")
        sorted1["a"].asColumnGroup().columnNames() shouldBe listOf("a", "c")

        val sorted2 = df.reorder { allDfs(true) }.byName()
        sorted2.columnNames() shouldBe listOf("a", "b")
        sorted2["a"].asColumnGroup().columnNames() shouldBe listOf("a", "c")
    }
}
