package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test
import kotlin.reflect.typeOf

class ValueCountsTests {

    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Bob", "Alice", "Charlie", "Alice"),
        "city" to columnOf("London", "London", "London", "Moscow", "Paris"),
        "age" to columnOf(15, 20, 15, 30, 15),
    )

    // region DataColumn

    @Test
    fun `value counts of a column`() {
        val languages by columnOf("Kotlin", "Kotlin", null, null, "C++")
        val languageCounts = languages.valueCounts()
        languageCounts["languages"].values() shouldBe listOf("Kotlin", "C++")
        languageCounts.count.values() shouldBe listOf(2, 1)
    }

    @Test
    fun `value counts of a column keeps the column name and adds an Int count column`() {
        val result = df["city"].valueCounts()

        result.columnNames() shouldBe listOf("city", "count")
        result["city"].type() shouldBe typeOf<String>()
        result["count"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `value counts of a column is sorted by count descending by default`() {
        val result = df["city"].valueCounts()

        result["city"].values() shouldBe listOf("London", "Moscow", "Paris")
        result["count"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `value counts of a column with ascending sorting`() {
        val result = df["city"].valueCounts(ascending = true)

        result["city"].values() shouldBe listOf("Moscow", "Paris", "London")
        result["count"].values() shouldBe listOf(1, 1, 3)
    }

    @Test
    fun `value counts of a column without sorting keeps the order of the first occurrence`() {
        val result = df["name"].valueCounts(sort = false)

        result["name"].values() shouldBe listOf("Alice", "Bob", "Charlie")
        result["count"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `value counts of a column drops NA values by default`() {
        val values by columnOf(1.0, null, Double.NaN, 1.0, null, 2.0)
        val result = values.valueCounts()

        result["values"].values() shouldBe listOf(1.0, 2.0)
        result["values"].type() shouldBe typeOf<Double>()
        result.count.values() shouldBe listOf(2, 1)
    }

    @Test
    fun `value counts of a column with dropNA false counts nulls`() {
        val values by columnOf(1.0, null, 1.0, 2.0, null)
        val result = values.valueCounts(dropNA = false, sort = false)

        result["values"].values() shouldBe listOf(1.0, null, 2.0)
        result["values"].type() shouldBe typeOf<Double?>()
        result.count.values() shouldBe listOf(2, 2, 1)
    }

    @Test
    fun `value counts of a column with dropNA false counts NaNs`() {
        val values by columnOf(1.0, Double.NaN, 1.0, Double.NaN, 2.0)
        val result = values.valueCounts(dropNA = false, sort = false)
        val resultValues = result["values"].values().toList()

        resultValues[0] shouldBe 1.0
        (resultValues[1] as Double).isNaN() shouldBe true
        resultValues[2] shouldBe 2.0
        result.count.values() shouldBe listOf(2, 2, 1)
    }

    @Test
    fun `value counts of a column with a custom result column name`() {
        val result = df["city"].valueCounts(resultColumn = "number")

        result.columnNames() shouldBe listOf("city", "number")
        result["number"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `value counts of an empty column`() {
        val result = df["city"].filter { false }.valueCounts()

        result.columnNames() shouldBe listOf("city", "count")
        result.rowsCount() shouldBe 0
    }

    @Test
    fun `value counts of a column with a result column name clashing with the column name`() {
        val count by columnOf("a", "b", "a")
        val result = count.valueCounts()

        result.columnNames() shouldBe listOf("count", "count1")
        result["count"].values() shouldBe listOf("a", "b")
        result["count1"].values() shouldBe listOf(2, 1)
    }

    // endregion

    // region DataFrame

    @Test
    fun `value counts of a DataFrame counts distinct rows`() {
        val result = df.valueCounts()

        result shouldBe dataFrameOf(
            "name" to columnOf("Alice", "Bob", "Charlie", "Alice"),
            "city" to columnOf("London", "London", "Moscow", "Paris"),
            "age" to columnOf(15, 20, 30, 15),
            "count" to columnOf(2, 1, 1, 1),
        )
    }

    @Test
    fun `value counts of a DataFrame with a columns selector`() {
        val result = df.valueCounts { "name"<String>() and "city"<String>() }

        result shouldBe dataFrameOf(
            "name" to columnOf("Alice", "Bob", "Charlie", "Alice"),
            "city" to columnOf("London", "London", "Moscow", "Paris"),
            "count" to columnOf(2, 1, 1, 1),
        )
    }

    @Test
    fun `value counts of a DataFrame with a single selected column`() {
        val result = df.valueCounts { "city"<String>() }

        result shouldBe dataFrameOf(
            "city" to columnOf("London", "Moscow", "Paris"),
            "count" to columnOf(3, 1, 1),
        )
    }

    @Test
    fun `value counts of a DataFrame by column names`() {
        df.valueCounts("name", "city") shouldBe df.valueCounts { "name"<String>() and "city"<String>() }
    }

    @Test
    fun `value counts of a DataFrame without sorting keeps the order of the first occurrence`() {
        val result = df.valueCounts(sort = false) { "city"<String>() }

        result["city"].values() shouldBe listOf("London", "Moscow", "Paris")
        result["count"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `value counts of a DataFrame with ascending sorting`() {
        val result = df.valueCounts(ascending = true) { "city"<String>() }

        result["city"].values() shouldBe listOf("Moscow", "Paris", "London")
        result["count"].values() shouldBe listOf(1, 1, 3)
    }

    @Test
    fun `value counts of a DataFrame drops rows with NA in any selected column by default`() {
        val dfWithNulls = dataFrameOf(
            "name" to columnOf("Alice", "Bob", null, "Alice"),
            "city" to columnOf("London", null, "Moscow", "London"),
            "weight" to columnOf(50.0, 60.0, null, Double.NaN),
        )
        val result = dfWithNulls.valueCounts()

        result.columnNames() shouldBe listOf("name", "city", "weight", "count")
        result["name"].values() shouldBe listOf("Alice")
        result["city"].values() shouldBe listOf("London")
        result["count"].values() shouldBe listOf(1)
    }

    @Test
    fun `value counts of a DataFrame ignores NA in columns that are not selected`() {
        val dfWithNulls = dataFrameOf(
            "name" to columnOf("Alice", "Bob", "Alice"),
            "city" to columnOf("London", null, "London"),
            "weight" to columnOf(50.0, 60.0, Double.NaN),
        )
        val result = dfWithNulls.valueCounts { "name"<String>() }

        result.columnNames() shouldBe listOf("name", "count")
        result["name"].values() shouldBe listOf("Alice", "Bob")
        result["count"].values() shouldBe listOf(2, 1)
    }

    @Test
    fun `value counts of a DataFrame with dropNA false counts rows with NA`() {
        val dfWithNulls = dataFrameOf(
            "name" to columnOf("Alice", "Bob", null, "Alice"),
            "city" to columnOf("London", null, "Moscow", "London"),
            "weight" to columnOf(50.0, 60.0, null, Double.NaN),
        )
        val result = dfWithNulls.valueCounts(dropNA = false, sort = false)
        val weightValues = result["weight"].values().toList()

        result["name"].values() shouldBe listOf("Alice", "Bob", null, "Alice")
        result["city"].values() shouldBe listOf("London", null, "Moscow", "London")
        result["count"].values() shouldBe listOf(1, 1, 1, 1)
        weightValues[0] shouldBe 50.0
        weightValues[1] shouldBe 60.0
        weightValues[2] shouldBe null
        (weightValues[3] as Double).isNaN() shouldBe true
    }

    @Test
    fun `value counts of a DataFrame with a custom result column name`() {
        val result = df.valueCounts(resultColumn = "occurrences") { "city"<String>() }

        result.columnNames() shouldBe listOf("city", "occurrences")
        result["city"].values() shouldBe listOf("London", "Moscow", "Paris")
        result["occurrences"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `value counts of a DataFrame with a result column name clashing with a selected column`() {
        val result = df.valueCounts(resultColumn = "city") { "city"<String>() }

        result.columnNames() shouldBe listOf("city", "city1")
        result["city"].values() shouldBe listOf("London", "Moscow", "Paris")
        result["city1"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `value counts of a DataFrame with a result column name clashing with a column that is not selected`() {
        val result = df.valueCounts(resultColumn = "age") { "city"<String>() }

        result.columnNames() shouldBe listOf("city", "age1")
        result["age1"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `value counts of a DataFrame with a default result column name clashing with an existing column`() {
        val dfWithCount = df.add("count") { 1 }
        val result = dfWithCount.valueCounts { "city"<String>() }

        result.columnNames() shouldBe listOf("city", "count1")
        result["count1"].values() shouldBe listOf(3, 1, 1)
    }

    @Test
    fun `count column of a DataFrame result is of type Int`() {
        df.valueCounts()["count"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `value counts of an empty DataFrame`() {
        df.drop(df.nrow).valueCounts().count() shouldBe 0
    }

    // endregion
}
