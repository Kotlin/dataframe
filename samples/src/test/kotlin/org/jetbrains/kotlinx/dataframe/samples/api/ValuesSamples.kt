package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.valuesNotNull
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class ValuesSamples : DataFrameSampleHelper("values", "api") {

    @DataSchema
    interface Person {
        val city: String
        val name: String
        val age: Int
        val weight: Int?
    }

    private val df: DataFrame<Person> = dataFrameOf(
        "city" to listOf("London", "London", "Milan", "Milan"),
        "name" to listOf("Alice", "Bob", "Alice", "Alice"),
        "age" to listOf(15, 20, 25, 30),
        "weight" to listOf(55, null, 55, null),
    ).cast()

    @Test
    fun values() {
        // SampleStart
        df.values()
        df.values(byRows = true)
        df.values { age and weight }
        // SampleEnd
        df.values().toList() shouldBe listOf(
            "London", "London", "Milan", "Milan",
            "Alice", "Bob", "Alice", "Alice",
            15, 20, 25, 30,
            55, null, 55, null,
        )
    }

    @Test
    fun valuesDf() {
        // SampleStart
        val allValues: Sequence<Any?> = df.values()
        // SampleEnd
        allValues.toList() shouldBe listOf(
            "London", "London", "Milan", "Milan",
            "Alice", "Bob", "Alice", "Alice",
            15, 20, 25, 30,
            55, null, 55, null,
        )
    }

    @Test
    fun valuesDfByRows() {
        // SampleStart
        val allValues: Sequence<Any?> = df.values(byRows = true)
        // SampleEnd
        allValues.toList() shouldBe listOf(
            "London", "Alice", 15, 55,
            "London", "Bob", 20, null,
            "Milan", "Alice", 25, 55,
            "Milan", "Alice", 30, null,
        )
    }

    @Test
    fun values_properties() {
        // SampleStart
        // all "age" values, then all "weight" values
        val values: Sequence<Int?> = df.values { age and weight }
        // SampleEnd
        values.toList() shouldBe listOf(15, 20, 25, 30, 55, null, 55, null)
    }

    @Test
    fun valuesByRow_properties() {
        // SampleStart
        // for each row, "age" and "weight" — row by row
        val values: Sequence<Int?> = df.values(byRow = true) { age and weight }
        // SampleEnd
        values.toList() shouldBe listOf(15, 55, 20, null, 25, 55, 30, null)
    }

    @Test
    fun valuesNotNullDf() {
        // SampleStart
        val values: Sequence<Any> = df.valuesNotNull()
        // SampleEnd
        values.toList() shouldBe listOf(
            "London", "London", "Milan", "Milan",
            "Alice", "Bob", "Alice", "Alice",
            15, 20, 25, 30,
            55, 55,
        )
    }

    @Test
    fun valuesNotNull_properties() {
        // SampleStart
        val values: Sequence<Int> = df.valuesNotNull { age and weight }
        // SampleEnd
        values.toList() shouldBe listOf(15, 20, 25, 30, 55, 55)
    }

    @Test
    fun valuesInGroupBy_properties() {
        // SampleStart
        // for every city, the "name" and "age" values of its people, as lists
        val result = df.groupBy { city }.values { name and age }
        // SampleEnd
        result shouldBe dataFrameOf(
            "city" to listOf("London", "Milan"),
            "name" to listOf(listOf("Alice", "Bob"), listOf("Alice", "Alice")),
            "age" to listOf(listOf(15, 20), listOf(25, 30)),
        )
    }

    @Test
    fun valuesInGroupBy_strings() {
        // SampleStart
        val result = df.groupBy("city").values("name", "age")
        // SampleEnd
        result shouldBe dataFrameOf(
            "city" to listOf("London", "Milan"),
            "name" to listOf(listOf("Alice", "Bob"), listOf("Alice", "Alice")),
            "age" to listOf(listOf(15, 20), listOf(25, 30)),
        )
    }

    @Test
    fun valuesInGroupByAll_properties() {
        // SampleStart
        // all columns except the keys, as lists per city
        val result = df.groupBy { city }.values()
        // SampleEnd
        result shouldBe dataFrameOf(
            "city" to listOf("London", "Milan"),
            "name" to listOf(listOf("Alice", "Bob"), listOf("Alice", "Alice")),
            "age" to listOf(listOf(15, 20), listOf(25, 30)),
            "weight" to listOf(listOf(55, null), listOf(55, null)),
        )
    }

    @Test
    fun valuesInGroupByDropNA_properties() {
        // SampleStart
        // `null` values are skipped in the "weight" lists, while "name" lists stay whole
        val result = df.groupBy { city }.values(dropNA = true) { name and weight }
        // SampleEnd
        result shouldBe dataFrameOf(
            columnOf("London", "Milan") named "city",
            columnOf(listOf("Alice", "Bob"), listOf("Alice", "Alice")) named "name",
            DataColumn.createValueColumn("weight", listOf(listOf(55), listOf(55)), typeOf<List<Int?>>()),
        )
    }

    @Test
    fun valuesInGroupByDistinct_properties() {
        // SampleStart
        // only distinct "name" values per city
        val result = df.groupBy { city }.values(distinct = true) { name }
        // SampleEnd
        result shouldBe dataFrameOf(
            "city" to listOf("London", "Milan"),
            "name" to listOf(listOf("Alice", "Bob"), listOf("Alice")),
        )
    }

    @Test
    fun valuesInReducedGroupBy_properties() {
        // SampleStart
        // the "name" and "weight" of the youngest person in each city
        val youngest = df.groupBy { city }.minBy { age }.values { name and weight }
        // the same, for all columns except the keys
        val youngestAll = df.groupBy { city }.minBy { age }.values()
        // SampleEnd
        youngest shouldBe dataFrameOf(
            "city" to listOf("London", "Milan"),
            "name" to listOf("Alice", "Alice"),
            "weight" to listOf(55, 55),
        )
        youngestAll shouldBe dataFrameOf(
            "city" to listOf("London", "Milan"),
            "name" to listOf("Alice", "Alice"),
            "age" to listOf(15, 25),
            "weight" to listOf(55, 55),
        )
    }

    @Test
    fun valuesInReducedGroupBy_strings() {
        // SampleStart
        val youngest = df.groupBy("city").minBy("age").values("name", "weight")
        // SampleEnd
        youngest shouldBe dataFrameOf(
            "city" to listOf("London", "Milan"),
            "name" to listOf("Alice", "Alice"),
            "weight" to listOf(55, 55),
        )
    }

    @Test
    fun valuesInPivot_properties() {
        // SampleStart
        // the "age" values of each city, as a single row
        val result = df.pivot { city }.values { age }
        // the same, for all columns except the pivot column
        val resultAll = df.pivot { city }.values()
        // SampleEnd
        result shouldBe dataFrameOf(
            "London" to listOf(listOf(15, 20)),
            "Milan" to listOf(listOf(25, 30)),
        )[0]
        resultAll shouldBe dataFrameOf(
            columnOf(
                columnOf(listOf("Alice", "Bob")) named "name",
                columnOf(listOf(15, 20)) named "age",
                columnOf(listOf(55, null)) named "weight",
            ) named "London",
            columnOf(
                columnOf(listOf("Alice", "Alice")) named "name",
                columnOf(listOf(25, 30)) named "age",
                columnOf(listOf(55, null)) named "weight",
            ) named "Milan",
        )[0]
    }

    @Test
    fun valuesInPivot_strings() {
        // SampleStart
        val result = df.pivot("city").values("age")
        // SampleEnd
        result shouldBe dataFrameOf(
            "London" to listOf(listOf(15, 20)),
            "Milan" to listOf(listOf(25, 30)),
        )[0]
    }

    @Test
    fun valuesInReducedPivot_properties() {
        // SampleStart
        // the "name" and "weight" of the youngest person in each city
        val youngest = df.pivot { city }.minBy { age }.values { name and weight }
        // the same, for all columns except the pivot column
        val youngestAll = df.pivot { city }.minBy { age }.values()
        // SampleEnd
        youngest shouldBe dataFrameOf(
            columnOf(columnOf("Alice") named "name", columnOf(55) named "weight") named "London",
            columnOf(columnOf("Alice") named "name", columnOf(55) named "weight") named "Milan",
        )[0]
        youngestAll shouldBe dataFrameOf(
            columnOf(
                columnOf("Alice") named "name",
                columnOf(15) named "age",
                columnOf(55) named "weight",
            ) named "London",
            columnOf(
                columnOf("Alice") named "name",
                columnOf(25) named "age",
                columnOf(55) named "weight",
            ) named "Milan",
        )[0]
    }

    @Test
    fun valuesInReducedPivot_strings() {
        // SampleStart
        val youngest = df.pivot("city").minBy("age").values("name", "weight")
        // SampleEnd
        youngest shouldBe dataFrameOf(
            columnOf(columnOf("Alice") named "name", columnOf(55) named "weight") named "London",
            columnOf(columnOf("Alice") named "name", columnOf(55) named "weight") named "Milan",
        )[0]
    }

    @Test
    fun valuesInPivotGroupBy_properties() {
        // SampleStart
        // for every person, the "age" values per city: a single value when there is one,
        // a list when there are several, and an empty cell when there is nothing to collect
        val result = df.pivot { city }.groupBy { name }.values { age }
        // with dropNA, a cell whose values are all `null` becomes empty as well
        val withoutNA = df.pivot { city }.groupBy { name }.values(dropNA = true) { weight }
        // SampleEnd
        result.columnNames() shouldBe listOf("name", "city")
        result.getColumnGroup("city").columnNames() shouldBe listOf("London", "Milan")
        result["name"].values() shouldBe listOf("Alice", "Bob")
        // a single value when there is one, a list when there are several,
        // an empty list when there are no rows
        result["city"]["London"].values() shouldBe listOf(15, 20)
        result["city"]["Milan"].values() shouldBe listOf(listOf(25, 30), emptyList<Int>())
        // a cell whose values are all `null` becomes null
        withoutNA["city"]["London"].values() shouldBe listOf(55, null)
        withoutNA["city"]["Milan"].values() shouldBe listOf(55, null)
    }

    @Test
    fun valuesInPivotGroupBy_strings() {
        // SampleStart
        val result = df.pivot("city").groupBy("name").values("age")
        // SampleEnd
        result.columnNames() shouldBe listOf("name", "city")
        result.getColumnGroup("city").columnNames() shouldBe listOf("London", "Milan")
        result["name"].values() shouldBe listOf("Alice", "Bob")
        result["city"]["London"].values() shouldBe listOf(15, 20)
        result["city"]["Milan"].values() shouldBe listOf(listOf(25, 30), emptyList<Int>())
    }

    @Test
    fun valuesInReducedPivotGroupBy_properties() {
        // SampleStart
        // the "weight" of the youngest person for every name and city
        val result = df.pivot { city }.groupBy { name }.minBy { age }.values { weight }
        // SampleEnd
        result shouldBe dataFrameOf(
            "name" to listOf("Alice", "Bob"),
            "London" to listOf(55, null),
            "Milan" to listOf(55, null),
        ).group("London", "Milan").into("city")
    }

    @Test
    fun valuesInReducedPivotGroupBy_strings() {
        // SampleStart
        val result = df.pivot("city").groupBy("name").minBy("age").values("weight")
        // SampleEnd
        result shouldBe dataFrameOf(
            "name" to listOf("Alice", "Bob"),
            "London" to listOf(55, null),
            "Milan" to listOf(55, null),
        ).group("London", "Milan").into("city")
    }
}
