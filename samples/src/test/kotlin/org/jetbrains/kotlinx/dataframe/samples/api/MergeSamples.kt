package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.asStrings
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.intoList
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

// The calls and expected values here back the `@sample`s in the `merge` KDocs and feed the samples
// of the `merge` page of the documentation website; the expected values all come from real runs.
class MergeSamples : DataFrameSampleHelper("merge", "api") {

    private val fullNames = listOf(
        "Alice Cooper",
        "Bob Dylan",
        "Charlie Daniels",
        "Charlie Chaplin",
        "Bob Marley",
        "Alice Wolf",
        "Charlie Byrd",
        "Alice Smith",
        "Bob Brown",
        "Charlie Johnson",
    )

    // `colsOf<Number>()` selects only `age`: `weight` is nullable (`Int?`), and `Int?` is not a subtype of `Number`
    private val ages = listOf(
        listOf(15),
        listOf(45),
        listOf(20),
        listOf(40),
        listOf(30),
        listOf(20),
        listOf(30),
        listOf(30),
        listOf(15),
        listOf(18),
    )

    private val dataList = listOf(
        listOf(15, 54),
        listOf(45, 87),
        listOf(20, null),
        listOf(40, null),
        listOf(30, 68),
        listOf(20, 55),
        listOf(30, 90),
        listOf(30, 52),
        listOf(15, 60),
        listOf(18, 70),
    )

    @Test
    fun mergeDf() {
        // SampleStart
        peopleDf
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun merge_properties() {
        // SampleStart
        // Merge two columns into one column "fullName"
        peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
            // SampleEnd
            .also { it["fullName"].values() shouldBe fullNames }
            .defaultHeaderFormatting { "fullName"() }
            .saveDfHtmlSample()
    }

    @Test
    fun merge_strings() {
        // SampleStart
        // Merge two columns into one column "fullName"
        peopleDf.merge { colGroup("name").col("firstName") and colGroup("name").col("lastName") }
            .by(" ").into("fullName")
            // SampleEnd
            .also { it["fullName"].values() shouldBe fullNames }
    }

    @Test
    fun mergeColumnNames() {
        // SampleStart
        // Merge top-level columns by their names into one column "data" of lists
        peopleDf.merge("age", "weight").into("data")
            // SampleEnd
            .also { it["data"].values() shouldBe dataList }
            .defaultHeaderFormatting { "data"() }
            .saveDfHtmlSample()
    }

    @Test
    fun mergeDefault() {
        // SampleStart
        // By default, when no `by` or `asStrings` is specified, values will be merged into the List
        peopleDf.merge { colsOf<Number>() }.into("data")
            // SampleEnd
            .also { it["data"].values() shouldBe ages }
            .defaultHeaderFormatting { "data"() }
            .saveDfHtmlSample()
    }

    @Test
    fun mergeSameWith() {
        // SampleStart
        // "transform" accepts a List of collected values for every row typed by their common type
        peopleDf.merge { name.firstName and name.lastName }
            .by { it.joinToString(" ") }
            .into("fullName")
            // SampleEnd
            .also { it["fullName"].values() shouldBe fullNames }
            .defaultHeaderFormatting { "fullName"() }
            .saveDfHtmlSample()
    }

    @Test
    fun mergeDifferentWith() {
        // SampleStart
        // When heterogeneous columns are merged, they may need to be cast to valid types in "transform"
        peopleDf.merge { name.firstName and age and isHappy }
            .by { "${it[0]} aged ${it[1]} is " + (if (it[2] as Boolean) "" else "not ") + "happy" }
            .into("status")
            // SampleEnd
            .also {
                it["status"].values() shouldBe listOf(
                    "Alice aged 15 is happy",
                    "Bob aged 45 is happy",
                    "Charlie aged 20 is not happy",
                    "Charlie aged 40 is happy",
                    "Bob aged 30 is happy",
                    "Alice aged 20 is not happy",
                    "Charlie aged 30 is happy",
                    "Alice aged 30 is not happy",
                    "Bob aged 15 is not happy",
                    "Charlie aged 18 is not happy",
                )
            }
            .defaultHeaderFormatting { "status"() }
            .saveDfHtmlSample()
    }

    @Test
    fun mergeAsStrings() {
        // SampleStart
        // Merge values into a single String
        peopleDf.merge { name.firstName and name.lastName }.asStrings().into("fullName")
            // SampleEnd
            .also { it["fullName"].values() shouldBe fullNames.map { it.replace(" ", ", ") } }
            .defaultHeaderFormatting { "fullName"() }
            .saveDfHtmlSample()
    }

    @Test
    fun mergeNotNull() {
        // SampleStart
        // Merge two columns into one column "info" excluding null values
        peopleDf.merge { name.firstName and weight }.notNull().by(" ").into("info")
            // SampleEnd
            .also {
                it["info"].values() shouldBe listOf(
                    "Alice 54",
                    "Bob 87",
                    "Charlie",
                    "Charlie",
                    "Bob 68",
                    "Alice 55",
                    "Charlie 90",
                    "Alice 52",
                    "Bob 60",
                    "Charlie 70",
                )
            }
            .defaultHeaderFormatting { "info"() }
            .saveDfHtmlSample()
    }

    @Test
    fun mergeIntoList() {
        // SampleStart
        // Merge data from two columns into List<String>
        peopleDf.merge { name.firstName and name.lastName }.by(",").intoList()
            // SampleEnd
            .let {
                it shouldBe fullNames.map { it.replace(" ", ",") }
                // the DataFrame is not changed: the selected columns stay in place
                peopleDf.columnNames() shouldBe listOf("name", "age", "city", "weight", "isHappy")
                it.joinToString("\n").saveTextSample()
            }
    }

    @Test
    fun mergeIntoPath() {
        // SampleStart
        // Merge the two name columns into a new nested column; the "person" column group is created
        peopleDf.merge { name.firstName and name.lastName }.by(" ").into(pathOf("person", "fullName"))
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("person", "age", "city", "weight", "isHappy")
                it[pathOf("person", "fullName")].values() shouldBe fullNames
            }
            .defaultHeaderFormatting { "person"() }
            .saveDfHtmlSample()
    }

    @Test
    fun mergeSplitRoundTrip() {
        // SampleStart
        // merge is the reverse of split: the merged column can be split back into two columns
        val merged = peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
        merged.split { col("fullName") }.by(" ").into("firstName", "lastName")
            // SampleEnd
            .also {
                it["firstName"].values() shouldBe peopleDf[pathOf("name", "firstName")].values()
                it["lastName"].values() shouldBe peopleDf[pathOf("name", "lastName")].values()
            }
            .defaultHeaderFormatting { "firstName"() and "lastName"() }
            .saveDfHtmlSample()
    }
}
