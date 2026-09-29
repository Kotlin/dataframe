package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.forEachIndexed
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class IterateSamples : DataFrameSampleHelper("iterate", "api") {

    // the first seven people, so that the page, the KDocs and the tests of `forEach` all show one dataset
    val df = peopleDf.take(7)

    // runs [block] and returns the lines it printed, so the page can show them and the test can assert them
    private fun printedLines(block: () -> Unit): List<String> {
        val original = System.out
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer, true))
        try {
            block()
        } finally {
            System.setOut(original)
        }
        return buffer.toString().lines().dropLastWhile { it.isEmpty() }
    }

    @Test
    fun iterateDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun iterateRows_properties() {
        val lines = printedLines {
            // SampleStart
            for (row in df) {
                println(row.age)
            }

            df.forEach {
                println(it.age)
            }

            df.rows().forEach {
                println(it.age)
            }
            // SampleEnd
        }
        // the three ways go over the same rows, in the same order
        val ages = listOf("15", "45", "20", "40", "30", "20", "30")
        lines shouldBe ages + ages + ages
    }

    @Test
    fun iterateRows_strings() {
        val lines = printedLines {
            // SampleStart
            for (row in df) {
                println(row["age"])
            }

            df.forEach {
                println(it["age"])
            }

            df.rows().forEach {
                println(it["age"])
            }
            // SampleEnd
        }
        val ages = listOf("15", "45", "20", "40", "30", "20", "30")
        lines shouldBe ages + ages + ages
    }

    @Test
    fun iterateColumns() {
        val lines = printedLines {
            // SampleStart
            df.columns().forEach {
                println(it.name())
            }
            // SampleEnd
        }
        lines shouldBe listOf("name", "age", "city", "weight", "isHappy")
    }

    @Test
    fun iterateCells() {
        val lines = printedLines {
            // SampleStart
            // from top to bottom, then from left to right
            df.values().forEach {
                println(it)
            }

            // from left to right, then from top to bottom
            df.values(byRows = true).forEach {
                println(it)
            }
            // SampleEnd
        }
        // 7 rows x 5 columns = 35 cells, printed twice; a column group cell prints as the whole nested row
        lines.size shouldBe 70
        // by columns: all the names, then the first age
        lines.take(8) shouldBe listOf(
            "{ firstName:Alice, lastName:Cooper }",
            "{ firstName:Bob, lastName:Dylan }",
            "{ firstName:Charlie, lastName:Daniels }",
            "{ firstName:Charlie, lastName:Chaplin }",
            "{ firstName:Bob, lastName:Marley }",
            "{ firstName:Alice, lastName:Wolf }",
            "{ firstName:Charlie, lastName:Byrd }",
            "15",
        )
        // by rows: the whole first row, then the name of the second person
        lines.drop(35).take(6) shouldBe listOf(
            "{ firstName:Alice, lastName:Cooper }",
            "15",
            "London",
            "54",
            "true",
            "{ firstName:Bob, lastName:Dylan }",
        )
    }

    @Test
    fun forEachOnColumn() {
        val lines = printedLines {
            // SampleStart
            // Prints the ages, from the first value to the last one: 15, 45, 20, 40, 30, 20, 30
            df.age.forEach { println(it) }
            // SampleEnd
        }
        lines shouldBe listOf("15", "45", "20", "40", "30", "20", "30")
    }

    @Test
    fun forEachIndexedOnColumn() {
        val lines = printedLines {
            // SampleStart
            // Prints the first names, numbered: "1. Alice", "2. Bob", ...
            df.name.firstName.forEachIndexed { i, firstName -> println("${i + 1}. $firstName") }
            // SampleEnd
        }
        lines shouldBe listOf(
            "1. Alice",
            "2. Bob",
            "3. Charlie",
            "4. Charlie",
            "5. Bob",
            "6. Alice",
            "7. Charlie",
        )
    }

    @Test
    fun forEachOnGroupBy_properties() {
        val lines = printedLines {
            // SampleStart
            // Prints the number of people per city: "London: 1", "Dubai: 1", "Moscow: 2", ...
            df.groupBy { city }.forEach { (key, group) -> println("${key.city}: ${group.rowsCount()}") }
            // SampleEnd
        }
        lines shouldBe listOf("London: 1", "Dubai: 1", "Moscow: 2", "Milan: 1", "Tokyo: 1", "null: 1")
    }

    @Test
    fun forEachOnGroupBy_strings() {
        val lines = printedLines {
            // SampleStart
            // Prints the number of people per city: "London: 1", "Dubai: 1", "Moscow: 2", ...
            df.groupBy("city").forEach { (key, group) -> println("${key["city"]}: ${group.rowsCount()}") }
            // SampleEnd
        }
        lines shouldBe listOf("London: 1", "Dubai: 1", "Moscow: 2", "Milan: 1", "Tokyo: 1", "null: 1")
    }
}
