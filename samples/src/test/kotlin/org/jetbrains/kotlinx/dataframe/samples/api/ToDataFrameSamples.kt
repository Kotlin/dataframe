package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.preserve
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrameFromPairs
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.junit.Test
import java.io.File

// The KDoc examples of `toDataFrame` / `toDataFrameFromPairs` (`core/.../api/toDataFrame.kt`).
// The `Student` hierarchy mirrors the `toDataFrame` samples on `createDataFrame.md`,
// so the KDoc and the page show one dataset.
class ToDataFrameSamples {

    @Test
    fun toDataFrameFromObjects() {
        // SampleStart
        data class Person(val name: String, val age: Int)

        val persons = listOf(Person("Alice", 15), Person("Bob", 20), Person("Charlie", 22))

        val df = persons.toDataFrame()
        // SampleEnd
        df.columnNames() shouldBe listOf("name", "age")
        df["age"].toList() shouldBe listOf(15, 20, 22)
    }

    @Test
    fun toDataFrameWithMaxDepth() {
        // SampleStart
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val students = listOf(
            Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
            Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
        )

        val df = students.toDataFrame(maxDepth = 1)
        // SampleEnd
        df.columnNames() shouldBe listOf("name", "age", "scores")
        df["name"].kind() shouldBe ColumnKind.Group
        df["age"].kind() shouldBe ColumnKind.Value
        df["scores"].kind() shouldBe ColumnKind.Frame
    }

    @Test
    fun toDataFrameWithDsl() {
        // SampleStart
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val students = listOf(
            Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
            Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
        )

        val df = students.toDataFrame {
            // add column
            "year of birth" from { 2021 - it.age }

            // scan all properties
            properties(maxDepth = 1) {
                exclude(Score::subject) // `subject` property will be skipped from object graph traversal
                preserve<Name>() // `Name` objects will be stored as-is without transformation into DataFrame
            }

            // add column group
            "summary" {
                "max score" from { it.scores.maxOf { it.value } }
                "min score" from { it.scores.minOf { it.value } }
            }
        }
        // SampleEnd
        df.columnNames() shouldBe listOf("year of birth", "name", "age", "scores", "summary")
        df["year of birth"].toList() shouldBe listOf(2006, 2001)
        df["name"].kind() shouldBe ColumnKind.Value
        df["scores"].kind() shouldBe ColumnKind.Frame
        df["summary"].asColumnGroup().columnNames() shouldBe listOf("max score", "min score")
    }

    @Test
    fun toDataFrameWithColumnName() {
        // SampleStart
        val files = listOf(File("data.csv"), File("data1.csv"))

        val df = files.toDataFrame(columnName = "data")
        // SampleEnd
        df.columnNames() shouldBe listOf("data")
        df["data"].toList() shouldBe files
    }

    @Test
    fun toDataFrameFromMaps() {
        // SampleStart
        val maps = listOf(
            mapOf("name" to "Alice", "age" to 15),
            mapOf("name" to "Bob", "city" to "Moscow"),
        )

        val df = maps.toDataFrame()
        // SampleEnd
        df.columnNames() shouldBe listOf("name", "age", "city")
        df["name"].toList() shouldBe listOf("Alice", "Bob")
        df["age"].toList() shouldBe listOf(15, null)
        df["city"].toList() shouldBe listOf(null, "Moscow")
    }

    @Test
    fun toDataFrameFromMapOfColumns() {
        // SampleStart
        val map = mapOf("name" to listOf("Alice", "Bob"), "age" to listOf(15, 20))

        val df = map.toDataFrame()
        // SampleEnd
        df.columnNames() shouldBe listOf("name", "age")
        df["name"].toList() shouldBe listOf("Alice", "Bob")
        df["age"].toList() shouldBe listOf(15, 20)
    }

    @Test
    fun toDataFrameFromPairs() {
        // SampleStart
        val df = listOf(
            pathOf("name") to listOf("Alice", "Bob"),
            pathOf("details", "age") to listOf(15, 20),
            pathOf("details", "city") to listOf("London", "Dubai"),
        ).toDataFrameFromPairs()
        // SampleEnd
        df.columnNames() shouldBe listOf("name", "details")
        df["details"].asColumnGroup().columnNames() shouldBe listOf("age", "city")
        df["name"].toList() shouldBe listOf("Alice", "Bob")
    }
}
