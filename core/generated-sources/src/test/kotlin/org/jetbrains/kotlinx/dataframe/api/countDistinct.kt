package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

class CountDistinctTests {

    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Alice", "Bob", "Charlie"),
        "age" to columnOf(15, 15, 20, 25),
        "group" to columnOf(1, 1, 1, 2),
    )

    @Test
    fun `countDistinct on GroupBy`() {
        val result = df.groupBy("group").countDistinct()
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "countDistinct" to columnOf(2, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on GroupBy with custom result name`() {
        val result = df.groupBy("group").countDistinct("unique")
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "unique" to columnOf(2, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on GroupBy with one unique row`() {
        val df = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Alice"),
            "age" to columnOf(15, 15, 15),
            "group" to columnOf(1, 1, 1),
        )
        val result = df.groupBy("group").countDistinct()
        val expected = dataFrameOf(
            "group" to columnOf(1),
            "countDistinct" to columnOf(1),
        )
        result shouldBe expected
    }

    // TODO: check columns as well when #1531 is fixed
    @Test
    fun `countDistinct on empty GroupBy`() {
        df
            .drop(df.nrow)
            .groupBy("group").countDistinct()
            .count() shouldBe 0
    }

    @Test
    fun `countDistinct on GroupBy with nulls`() {
        val result = df
            .append(null, null, 1)
            .groupBy("group").countDistinct()
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "countDistinct" to columnOf(3, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on GroupBy with null group key`() {
        val result = df
            .append("Dave", 30, null)
            .groupBy("group").countDistinct()
        val expected = dataFrameOf(
            "group" to columnOf(1, 2, null),
            "countDistinct" to columnOf(2, 1, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on GroupBy with columns selector`() {
        val result = df.groupBy("group").countDistinct { "name"<String>() }
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "countDistinct" to columnOf(2, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on GroupBy with columns selector (not distinct only by selected column)`() {
        val df = dataFrameOf(
            "name" to columnOf("Alice", "Bob", "Charlie"),
            "age" to columnOf(15, 15, 20),
            "group" to columnOf(1, 1, 2),
        )
        val result = df.groupBy("group").countDistinct { "age"<Int>() }
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "countDistinct" to columnOf(1, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on GroupBy with multiple columns selector`() {
        val df = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Bob", "Charlie"),
            "age" to columnOf(15, 15, 20, 25),
            "group" to columnOf(1, 1, 1, 2),
            "city" to columnOf("London", "Moscow", "London", "Paris"),
        )
        val result = df.groupBy("group").countDistinct { "name"<String>() and "age"<Int>() }
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "countDistinct" to columnOf(2, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on grouped DataFrame with columns selector and custom result name`() {
        val result = df.groupBy("group").countDistinct(resultName = "unique") { "name"<String>() }
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "unique" to columnOf(2, 1),
        )
        result shouldBe expected
    }

    @Test
    fun `countDistinct on grouped DataFrame with multiple columns selector with nulls`() {
        val result = df
            .append(null, null, 1)
            .groupBy("group")
            .countDistinct { "name"<String>() and "age"<Int>() }
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "countDistinct" to columnOf(3, 1),
        )
        result shouldBe expected
    }
}
