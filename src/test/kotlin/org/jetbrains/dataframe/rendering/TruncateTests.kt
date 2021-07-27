package org.jetbrains.dataframe.rendering

import io.kotest.matchers.shouldBe
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.io.DisplayConfiguration
import org.jetbrains.dataframe.io.formatter
import org.jetbrains.dataframe.jupyter.DefaultCellRenderer
import org.jetbrains.dataframe.manyOf
import org.jsoup.Jsoup
import org.junit.Test

class TruncateTests {

    private fun Any?.format(limit: Int): String = Jsoup.parse(formatter.format(this, DefaultCellRenderer, DisplayConfiguration(cellContentLimit = limit))).text()

    @Test
    fun truncate() {
        "123456789".format(5) shouldBe "12..."
    }

    @Test
    fun `truncate all`() {
        "123456789".format(3) shouldBe "..."
    }

    @Test
    fun `no truncate`() {
        "123".format(3) shouldBe "123"
    }

    @Test
    fun truncateMany() {
        manyOf("1","2","34567890").format(15) shouldBe "[1, 2, 3456...]"
    }

    @Test
    fun truncateMany2() {
        manyOf("1","2345678","9").format(15) shouldBe "[1, 2345678, 9]"
    }

    @Test
    fun truncateMany7() {
        manyOf("1","23456789","0").format(15) shouldBe "[1, 2345..., 0]"
    }

    @Test
    fun truncateMany3() {
        manyOf("1","2345","6789").format(15) shouldBe "[1, 2345, 6789]"
    }

    @Test
    fun truncateMany4() {
        manyOf("1","234567","89012").format(15) shouldBe "[1, 2..., 8...]"
    }

    @Test
    fun truncateMany5() {
        manyOf("1","234567","8901").format(15) shouldBe "[1, 2..., 8901]"
    }

    @Test
    fun truncateMany6() {
        manyOf("1","234567","8", "9").format(15) shouldBe "[1, 23..., ...]"
    }

    @Test
    fun truncateMany8() {
        manyOf("123456","789","0", "1").format(15) shouldBe "[123456, ...]"
    }

    @Test
    fun truncateMany9() {
        manyOf("123456789","789","0", "1").format(15) shouldBe "[12345..., ...]"
    }

    @Test
    fun truncateMany10() {
        manyOf(1).format(3) shouldBe "[1]"
        manyOf(10).format(4) shouldBe "[10]"
        manyOf(1, 2).format(4) shouldBe "[..]"
        manyOf(100).format(4) shouldBe "[..]"
        manyOf(100).format(5) shouldBe "[100]"
        manyOf(1000).format(5) shouldBe "[...]"
        manyOf(1, 2).format(5) shouldBe "[...]"
        manyOf(1, 2).format(6) shouldBe "[1, 2]"
    }

    private fun rowOf(vararg pairs: Pair<String, Any?>) = dataFrameOf(pairs.map {it.first}).withValues(pairs.map{ it.second})[0]

    @Test
    fun truncateRow() {
        val row = rowOf("name" to "Alice", "age" to 10)

        val truncates = listOf(
            "{..}",
            "{...}",
            "{ ...}",
            "{ ... }",
            "{ n..., a... }",
            "{ na..., a... }",
            "{ nam..., a... }",
            "{ name..., a... }",
            "{ name:..., a... }",
            "{ name: ..., a... }",
            "{ name: A..., a... }",
            "{ name: Alice, a... }",
            "{ name: Alice, ag... }",
            "{ name: Alice, age... }",
            "{ name: Alice, age: 10 }"
        )
        truncates.forEach {
            row.format(it.length) shouldBe it
        }
        for(i in truncates[3].length until truncates[4].length)
            row.format(i) shouldBe truncates[3]
    }
}
