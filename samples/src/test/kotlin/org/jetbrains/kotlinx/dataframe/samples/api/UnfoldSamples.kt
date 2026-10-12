package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.unfold
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

// the classes and the objects of the `toDataFrame` example on `createDataFrame.md`,
// so that both pages show the same data; they are local, as there, so every sample declares them
class UnfoldSamples : DataFrameSampleHelper("unfold", "api") {

    @Test
    fun unfoldDf() {
        // SampleStart
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleEnd
        df.saveDfHtmlSample()
    }

    @Test
    fun unfold_properties() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleStart
        df.unfold { student }
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("id", "student", "year")
                it["student"].asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores")
                it[pathOf("student", "name")][0] shouldBe Name("Alice", "Cooper")
            }
            .saveDfHtmlSample()
    }

    @Test
    fun unfold_strings() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleStart
        df.unfold("student")
            // SampleEnd
            .also { it["student"].asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores") }
    }

    @Test
    fun unfoldMaxDepth_properties() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleStart
        df.unfold(maxDepth = 1) { student }
            // SampleEnd
            .also {
                it[pathOf("student", "name")].asColumnGroup().columnNames() shouldBe listOf("firstName", "lastName")
                it[pathOf("student", "scores")].kind() shouldBe ColumnKind.Frame
                it[pathOf("student", "scores")][0] shouldBe
                    dataFrameOf("subject", "value")("math", 4, "biology", 3)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun unfoldMaxDepth_strings() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleStart
        df.unfold(maxDepth = 1) { col("student") }
            // SampleEnd
            .also { it[pathOf("student", "scores")].kind() shouldBe ColumnKind.Frame }
    }

    @Test
    fun unfoldRoots_properties() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleStart
        df.unfold(Student::name, Student::age) { student }
            // SampleEnd
            .also { it["student"].asColumnGroup().columnNames() shouldBe listOf("name", "age") }
            .saveDfHtmlSample()
    }

    @Test
    fun unfoldRoots_strings() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleStart
        df.unfold(Student::name, Student::age) { col("student") }
            // SampleEnd
            .also { it["student"].asColumnGroup().columnNames() shouldBe listOf("name", "age") }
    }

    @Test
    fun unfoldOnColumn() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val df = dataFrameOf(
            "id" to columnOf(1, 2),
            "student" to columnOf(
                Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
                Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
            ),
            "year" to columnOf(2021, 2022),
        )
        // SampleStart
        df.student.unfold(maxDepth = 1)
            // SampleEnd
            .also {
                it.name() shouldBe "student"
                it.asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores")
            }
            .saveDfHtmlSample()
    }
}
