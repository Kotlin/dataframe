package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test
import kotlin.random.Random
import kotlin.reflect.typeOf

class NormalizedValueCountsTests {

    private val df = dataFrameOf(
        "label" to columnOf("b", "a", "b", "c"),
        "number" to columnOf(1, 2, 1, 3),
    )

    @Test
    fun `column normalization returns Double proportions`() {
        val result = df["label"].valueCounts(normalize = true)

        result.columnNames() shouldBe listOf("label", "count")
        result["label"].values() shouldBe listOf("b", "a", "c")
        result["count"].values() shouldBe listOf(0.5, 0.25, 0.25)
        result["count"].type() shouldBe typeOf<Double>()
    }

    @Test
    fun `normalization false preserves absolute column counts`() {
        df["label"].valueCounts(normalize = false) shouldBe df["label"].valueCounts()
        df["label"].valueCounts(normalize = false)["count"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `column denominator excludes both null and NaN by default`() {
        val values = columnOf(1.0, null, Double.NaN, 1.0, 2.0) named "value"
        val result = values.valueCounts(normalize = true)

        result["value"].values() shouldBe listOf(1.0, 2.0)
        result["count"].values() shouldBe listOf(2.0 / 3, 1.0 / 3)
    }

    @Test
    fun `including missing values includes their counts in the denominator`() {
        val values = columnOf(1.0, null, Double.NaN, 1.0, null, Double.NaN) named "value"
        val result = values.valueCounts(sort = false, dropNA = false, normalize = true)

        result["value"].values().take(2) shouldBe listOf(1.0, null)
        (result["value"][2] as Double).isNaN() shouldBe true
        result["count"].values() shouldBe List(3) { 1.0 / 3 }
    }

    @Test
    fun `empty column has no rows and a nonnullable Double count type`() {
        val values = DataColumn.createValueColumn<String>("value", emptyList())
        val result = values.valueCounts(normalize = true)

        result.nrow shouldBe 0
        result["value"].type() shouldBe typeOf<String>()
        result["count"].type() shouldBe typeOf<Double>()
    }

    @Test
    fun `all excluded values produce no fabricated proportion row`() {
        val values = columnOf<Double?>(null, Double.NaN, null) named "value"
        val result = values.valueCounts(normalize = true)

        result.nrow shouldBe 0
        result["count"].values() shouldBe emptyList<Double>()
        result["count"].type() shouldBe typeOf<Double>()
    }

    @Test
    fun `all missing values can be counted explicitly`() {
        val values = columnOf<Double?>(null, Double.NaN, null) named "value"
        val result = values.valueCounts(dropNA = false, normalize = true)

        result["count"].values() shouldBe listOf(2.0 / 3, 1.0 / 3)
    }

    @Test
    fun `column named count does not normalize the observed values`() {
        val values = columnOf(10, 20, 10) named "count"
        val result = values.valueCounts(normalize = true)

        result.columnNames() shouldBe listOf("count", "count1")
        result["count"].values() shouldBe listOf(10, 20)
        result["count1"].values() shouldBe listOf(2.0 / 3, 1.0 / 3)
    }

    @Test
    fun `custom column result name retains collision handling`() {
        val result = df["label"].valueCounts(resultColumn = "label", normalize = true)

        result.columnNames() shouldBe listOf("label", "label1")
        result["label1"].values() shouldBe listOf(0.5, 0.25, 0.25)
    }

    @Test
    fun `ascending normalization preserves the existing stable count order`() {
        val result = df["label"].valueCounts(ascending = true, normalize = true)

        result["label"].values() shouldBe listOf("a", "c", "b")
        result["count"].values() shouldBe listOf(0.25, 0.25, 0.5)
    }

    @Test
    fun `unsorted normalization retains first occurrence order`() {
        val result = df["label"].valueCounts(sort = false, ascending = true, normalize = true)

        result["label"].values() shouldBe listOf("b", "a", "c")
        result["count"].values() shouldBe listOf(0.5, 0.25, 0.25)
    }

    @Test
    fun `dataframe normalization counts complete rows`() {
        val result = df.valueCounts(normalize = true)

        result shouldBe dataFrameOf(
            "label" to columnOf("b", "a", "c"),
            "number" to columnOf(1, 2, 3),
            "count" to columnOf(0.5, 0.25, 0.25),
        )
    }

    @Test
    fun `selector and string forms agree`() {
        df.valueCounts(normalize = true) { "label"<String>() } shouldBe
            df.valueCounts("label", normalize = true)
        df.valueCounts(normalize = true) { "label"<String>() and "number"<Int>() } shouldBe
            df.valueCounts("label", "number", normalize = true)
    }

    @Test
    fun `missing values outside the selected columns do not reduce the denominator`() {
        val input = dataFrameOf(
            "label" to columnOf("a", "a", "b"),
            "other" to columnOf(1.0, null, Double.NaN),
        )
        val result = input.valueCounts("label", normalize = true)

        result["count"].values() shouldBe listOf(2.0 / 3, 1.0 / 3)
    }

    @Test
    fun `missing values in any selected column reduce the denominator`() {
        val input = dataFrameOf(
            "label" to columnOf("a", "a", "b", null),
            "number" to columnOf(1.0, Double.NaN, 2.0, 3.0),
        )
        val result = input.valueCounts("label", "number", normalize = true)

        result.nrow shouldBe 2
        result["count"].values() shouldBe listOf(0.5, 0.5)
    }

    @Test
    fun `dataframe normalization can include missing rows`() {
        val input = dataFrameOf("label" to columnOf("a", null, "a", null, "b"))
        val result = input.valueCounts(dropNA = false, normalize = true)

        result["label"].values() shouldBe listOf("a", null, "b")
        result["count"].values() shouldBe listOf(0.4, 0.4, 0.2)
    }

    @Test
    fun `selected and unselected dataframe name collisions retain the generated count name`() {
        val selectedCollision = df.valueCounts("label", resultColumn = "label", normalize = true)
        val unselectedCollision = df.valueCounts("label", resultColumn = "number", normalize = true)

        selectedCollision.columnNames() shouldBe listOf("label", "label1")
        unselectedCollision.columnNames() shouldBe listOf("label", "number1")
        selectedCollision["label1"].values() shouldBe listOf(0.5, 0.25, 0.25)
        unselectedCollision["number1"].values() shouldBe listOf(0.5, 0.25, 0.25)
    }

    @Test
    fun `normalization false preserves absolute dataframe counts`() {
        df.valueCounts(normalize = false) shouldBe df.valueCounts()
        df.valueCounts("label", normalize = false) shouldBe df.valueCounts("label")
        df.valueCounts(normalize = false) { "label"<String>() } shouldBe df.valueCounts("label")
        df.valueCounts(normalize = false)["count"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `empty dataframe keeps the selected columns and a Double count type`() {
        val result = df.take(0).valueCounts("label", normalize = true)

        result.nrow shouldBe 0
        result.columnNames() shouldBe listOf("label", "count")
        result["count"].type() shouldBe typeOf<Double>()
    }

    @Test
    fun `nested observed columns are retained`() {
        val input = df.group("label").into("details")
        val result = input.valueCounts(normalize = true)

        (result["details"] is ColumnGroup<*>) shouldBe true
        result["count"].values() shouldBe listOf(0.5, 0.25, 0.25)
    }

    @Test
    fun `legacy column calls retain their typed ValueCount schema`() {
        val result: DataFrame<ValueCount> = df["label"].valueCounts(true, false, true, "count")

        result.count.values() shouldBe listOf(2, 1, 1)
        result["count"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `normalization matches an independent frequency dictionary`() {
        val random = Random(20261019)
        repeat(100) {
            val input = List(random.nextInt(0, 60)) { random.nextInt(-1, 5).takeIf { it >= 0 } }
            for (dropNA in listOf(false, true)) {
                val retained = if (dropNA) input.filterNotNull() else input
                val expected = retained.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
                val values = DataColumn.createValueColumn<Int?>("value", input)
                val result = values.valueCounts(dropNA = dropNA, normalize = true)

                result["value"].values() shouldBe expected.map { it.key }
                result["count"].values() shouldBe expected.map { it.value.toDouble() / retained.size }
            }
        }
    }
}
