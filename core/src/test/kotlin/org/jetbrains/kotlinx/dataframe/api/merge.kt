package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class MergeTests {

    @Test
    fun `merged values are collected into a list in selection order`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        val res = df.merge { "b"<Int>() and "a"<Int>() }.into("merged")
        res.columnNames() shouldBe listOf("merged")
        res["merged"].values() shouldBe listOf(listOf(2, 1), listOf(4, 3))
    }

    @Test
    fun `merged column replaces the first selected column and other columns stay unchanged`() {
        val df = dataFrameOf("a", "b", "c")(1, 2, 3)
        val res = df.merge { "a"<Int>() and "b"<Int>() }.into("merged")
        res.columnNames() shouldBe listOf("merged", "c")
        res["c"].values() shouldBe listOf(3)
    }

    @Test
    fun `merge by column names collects values into a list`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        val res = df.merge("a", "b").into("merged")
        res["merged"].values() shouldBe listOf(listOf(1, 2), listOf(3, 4))
    }

    @Test
    fun `by joins the values into a string`() {
        val df = dataFrameOf("a", "b")("x", "y", "z", null)
        val res = df.merge { "a"<String?>() and "b"<String?>() }.by(" + ").into("sum")
        res["sum"].values() shouldBe listOf("x + y", "z + null")
    }

    @Test
    fun `by passes prefix, postfix, limit and truncated to the join`() {
        val df = dataFrameOf("a", "b", "c")(1, 2, 3)
        val res = df.merge { "a"<Int>() and "b"<Int>() and "c"<Int>() }
            .by(
                separator = ", ",
                prefix = "[",
                postfix = "]",
                limit = 2,
                truncated = "!",
            )
            .into("s")
        res["s"].values() shouldBe listOf("[1, 2, !]")
    }

    @Test
    fun `notNull excludes null values from the merged list`() {
        val df = dataFrameOf("a", "b")(1, null, 3, 4)
        val res = df.merge { "a"<Int?>() and "b"<Int?>() }.notNull().into("merged")
        res["merged"].values() shouldBe listOf(listOf(1), listOf(3, 4))
    }

    @Test
    fun `notNull excludes a row value from a column group with all null values`() {
        val df = dataFrameOf("first", "last")(
            "Alice", "Cooper",
            null, null,
        ).group { "first"<String?>() and "last"<String?>() }.into("name")

        val res = df.merge { "name"<DataRow<*>>() }.notNull()
            .by { it.joinToString { row -> row.values().joinToString() } }
            .into("s")

        // the row where all values of the group are null is merged into an empty value
        res["s"].values() shouldBe listOf("Alice, Cooper", "")
    }

    @Test
    fun `transform receives the source row as receiver and collected values as a list`() {
        val df = dataFrameOf("a", "b", "c")(1, 2, 3)
        val res = df.merge { "a"<Int>() and "b"<Int>() }
            .by { "row: ${it.joinToString()} at ${this["c"]}" }
            .into("s")
        res["s"].values() shouldBe listOf("row: 1, 2 at 3")
    }

    @Test
    fun `asStrings joins the values with the default separator`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        val res = df.merge { "a"<Int>() and "b"<Int>() }.asStrings().into("s")
        res["s"].values() shouldBe listOf("1, 2", "3, 4")
    }

    @Test
    fun `intoList returns merged values without changing the dataframe`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        val merged = df.merge { "a"<Int>() and "b"<Int>() }.by("-").intoList()
        merged shouldBe listOf("1-2", "3-4")
        df.columnNames() shouldBe listOf("a", "b")
    }

    @Test
    fun `intoList excludes null values when notNull is set`() {
        val df = dataFrameOf("a", "b")(1, null, 3, 4)
        val merged = df.merge { "a"<Int?>() and "b"<Int?>() }.notNull().intoList()
        merged shouldBe listOf(listOf(1), listOf(3, 4))
    }

    @Test
    fun `intoList excludes null values when notNull is set after a transform`() {
        val df = dataFrameOf("a", "b")(1, null, 3, 4)
        val merged = df.merge { "a"<Int?>() and "b"<Int?>() }.notNull().by(" ").intoList()
        merged shouldBe listOf("1", "3 4")
    }

    @Test
    fun `intoList excludes an all-null row value from a column group when notNull is set`() {
        val df = dataFrameOf("first", "last")(
            "Alice", "Cooper",
            null, null,
        ).group { "first"<String?>() and "last"<String?>() }.into("name")

        val merged = df.merge { "name"<DataRow<*>>() }.notNull().intoList()

        (merged[0][0] as DataRow<*>).values() shouldBe listOf("Alice", "Cooper")
        merged[1] shouldBe emptyList<Any?>()
    }

    @Test
    fun `into an existing column replaces it in place`() {
        val df = dataFrameOf("first", "last", "age", "city")(
            "Alice", "Cooper", 15, "London",
        ).group { "first"<String>() and "last"<String>() }.into("name")

        val res = df.merge { "age"<Int>() }.by { "age: ${it[0]}" }.into("name")

        // the whole "name" column group is replaced by the merged column, which keeps its position
        res.columnNames() shouldBe listOf("name", "city")
        res["name"].values() shouldBe listOf("age: 15")
    }

    @Test
    fun `into a new nested path creates the missing column groups`() {
        val df = dataFrameOf("first", "last", "age")("Alice", "Cooper", 15)
            .group { "first"<String>() and "last"<String>() }
            .into("name")

        val res = df.merge { "age"<Int>() }.by { "age: ${it[0]}" }.into(pathOf("person", "fullName"))

        res.columnNames() shouldBe listOf("name", "person")
        res[pathOf("person", "fullName")].values() shouldBe listOf("age: 15")
    }

    @Test
    fun `into a new sub-path inside an existing column group`() {
        val df = dataFrameOf("first", "last", "age")("Alice", "Cooper", 15)
            .group { "first"<String>() and "last"<String>() }
            .into("name")

        val res = df.merge { "age"<Int>() }.by { "age: ${it[0]}" }.into(pathOf("name", "note"))

        res.columnNames() shouldBe listOf("name")
        res[pathOf("name", "first")].values() shouldBe listOf("Alice")
        res[pathOf("name", "last")].values() shouldBe listOf("Cooper")
        res[pathOf("name", "note")].values() shouldBe listOf("age: 15")
    }

    @Test
    fun `into an existing nested column replaces it`() {
        val df = dataFrameOf("first", "last", "age")("Alice", "Cooper", 15)
            .group { "first"<String>() and "last"<String>() }
            .into("name")
            .move { "name"<DataRow<*>>() }
            .under("person")

        val res = df.merge { "age"<Int>() }.by { "age: ${it[0]}" }.into(pathOf("person", "name"))

        res.columnNames() shouldBe listOf("person")
        res[pathOf("person", "name")].values() shouldBe listOf("age: 15")
    }
}
