package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.asStrings
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.intoList
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

// The dataset (`peopleDf`) and the calls are taken from the `merge` page of the documentation website,
// so the KDoc, the page and these tests show one example. Every expected value below backs the
// corresponding `@sample` in the `merge` KDocs.
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

    // `colsOf<Number>()` selects only `age`: `weight` is nullable (Int?) and not a subtype of Number
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

    @Test
    fun merge_properties() {
        // SampleStart
        // Merge two columns into one column "fullName"
        peopleDf.merge { name.firstName and name.lastName }.by(" ").into("fullName")
            // SampleEnd
            .also { it["fullName"].values() shouldBe fullNames }
    }

    @Test
    fun merge_strings() {
        // SampleStart
        // Merge the "age" and "weight" columns into one column "data" of lists
        peopleDf.merge("age", "weight").into("data")
            // SampleEnd
            .also {
                it["data"].values() shouldBe listOf(
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
            }
    }

    @Test
    fun mergeDefault_properties() {
        // SampleStart
        // Merge all columns of type Number into one column "data" of lists
        peopleDf.merge { colsOf<Number>() }.into("data")
            // SampleEnd
            .also { it["data"].values() shouldBe ages }
    }

    @Test
    fun mergeNotNull_properties() {
        // SampleStart
        // Merge two columns into one, skipping null values
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
    }

    @Test
    fun mergeSameWith_properties() {
        // SampleStart
        peopleDf.merge { name.firstName and name.lastName }
            .by { it[0] + " (" + it[1].uppercase() + ")" }
            .into("fullName")
            // SampleEnd
            .also {
                it["fullName"].values() shouldBe listOf(
                    "Alice (COOPER)",
                    "Bob (DYLAN)",
                    "Charlie (DANIELS)",
                    "Charlie (CHAPLIN)",
                    "Bob (MARLEY)",
                    "Alice (WOLF)",
                    "Charlie (BYRD)",
                    "Alice (SMITH)",
                    "Bob (BROWN)",
                    "Charlie (JOHNSON)",
                )
            }
    }

    @Test
    fun mergeDifferentWith_properties() {
        // SampleStart
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
    }

    @Test
    fun mergeAsStrings_properties() {
        // SampleStart
        // Merge two columns into one column "fullName" of strings
        peopleDf.merge { name.firstName and name.lastName }.asStrings().into("fullName")
            // SampleEnd
            .also {
                it["fullName"].values() shouldBe listOf(
                    "Alice, Cooper",
                    "Bob, Dylan",
                    "Charlie, Daniels",
                    "Charlie, Chaplin",
                    "Bob, Marley",
                    "Alice, Wolf",
                    "Charlie, Byrd",
                    "Alice, Smith",
                    "Bob, Brown",
                    "Charlie, Johnson",
                )
            }
    }

    @Test
    fun mergeIntoList_properties() {
        // SampleStart
        // Merge data from two columns into List<String>
        peopleDf.merge { name.firstName and name.lastName }.by(",").intoList()
            // SampleEnd
            .let {
                it shouldBe listOf(
                    "Alice,Cooper",
                    "Bob,Dylan",
                    "Charlie,Daniels",
                    "Charlie,Chaplin",
                    "Bob,Marley",
                    "Alice,Wolf",
                    "Charlie,Byrd",
                    "Alice,Smith",
                    "Bob,Brown",
                    "Charlie,Johnson",
                )
                // the DataFrame is not changed
                peopleDf.columnNames() shouldBe listOf("name", "age", "city", "weight", "isHappy")
            }
    }

    @Test
    fun mergeIntoPath_properties() {
        // SampleStart
        // Merge two columns into a nested column "person" -> "fullName"
        peopleDf.merge { name.firstName and name.lastName }.by(" ").into(pathOf("person", "fullName"))
            // SampleEnd
            .also {
                it[pathOf("person", "fullName")].values() shouldBe fullNames
                it.columnNames() shouldBe listOf("person", "age", "city", "weight", "isHappy")
            }
    }
}
