package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.shouldBe
import org.junit.Test

class TruncateTests : RenderingTestsBase() {

    @Test
    fun `truncate str`() {
        "123456789".truncate(5) shouldBe "12..."
    }

    @Test
    fun `truncate str to ellipsis`() {
        "123456789".truncate(3) shouldBe "..."
    }

    @Test
    fun `no truncate`() {
        "123".truncate(3) shouldBe "123"
    }

    @Test
    fun truncateMany() {
        listOf("1", "2", "34567890").truncate(15) shouldBe "[1, 2, 3456...]"
    }

    @Test
    fun truncateMany2() {
        listOf("1", "2345678", "9").truncate(15) shouldBe "[1, 2345678, 9]"
    }

    @Test
    fun truncateMany7() {
        listOf("1", "23456789", "0").truncate(15) shouldBe "[1, 2345..., 0]"
    }

    @Test
    fun truncateMany3() {
        listOf("1", "2345", "6789").truncate(15) shouldBe "[1, 2345, 6789]"
    }

    @Test
    fun truncateMany4() {
        listOf("1", "234567", "89012").truncate(15) shouldBe "[1, 2..., 8...]"
    }

    @Test
    fun truncateMany5() {
        listOf("1", "234567", "8901").truncate(15) shouldBe "[1, 2..., 8901]"
    }

    @Test
    fun truncateMany6() {
        listOf("1", "234567", "8", "9").truncate(15) shouldBe "[1, 23..., ...]"
    }

    @Test
    fun truncateMany8() {
        listOf("123456", "789", "0", "1").truncate(15) shouldBe "[123456, ...]"
    }

    @Test
    fun truncateMany9() {
        listOf("123456789", "789", "0", "1").truncate(15) shouldBe "[12345..., ...]"
    }

    @Test
    fun truncateManyMany() {
        listOf(1).truncate(3) shouldBe "[1]"
        listOf(10).truncate(4) shouldBe "[10]"
        listOf(1, 2).truncate(4) shouldBe "[..]"
        listOf(100).truncate(4) shouldBe "[..]"
        listOf(100).truncate(5) shouldBe "[100]"
        listOf(1000).truncate(5) shouldBe "[...]"
        listOf(1, 2).truncate(5) shouldBe "[...]"
        listOf(1, 2).truncate(6) shouldBe "[1, 2]"
    }

    @Test
    fun `run truncate row`() {
        testTruncates(
            rowOf("name" to "Alice", "age" to 10),
            listOf(
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
        )
    }

    private fun testTruncates(value: Any?, truncates: List<String>) {
        val start = truncates[0].length
        val end = truncates.last().length
        val actual = (start..end).map { value.truncate(it) }
        val expected = (start..end).map { len -> truncates.indexOfFirst { it.length > len }.let { if (it == -1) truncates.last() else truncates[it - 1] } }
        actual shouldBe expected
    }

    @Test
    fun `run truncate many`() {
        val value = listOf("Alice", "Bob", "Billy")
        testTruncates(
            value,
            listOf(
                "[..]",
                "[...]",
                "[A..., ...]",
                "[Alice, ...]",
                "[Alice, Bob, B...]",
                "[Alice, Bob, Billy]",
            )
        )
    }
}
