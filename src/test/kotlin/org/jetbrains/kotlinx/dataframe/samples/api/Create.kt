package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.createDataFrame
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.preserve
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.frameColumn
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

class Create : TestBase() {

    @Test
    fun createValueByColumnOf() {
        // SampleStart
        val name by columnOf("Alice", "Bob")
        // SampleEnd
    }

    @Test
    fun createValueByToColumn() {
        // SampleStart
        listOf("Alice", "Bob").toColumn("name")
        // SampleEnd
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun createValueColumnInferred() {
        // SampleStart
        val values = listOf("Alice", null, 1, 2.5).subList(2, 4)

        values.toColumn("data").type willBe typeOf<Any?>()
        values.toColumn("data", inferType = true).type willBe typeOf<Number>()
        values.toColumn("data", inferNulls = true).type willBe typeOf<Any>()
        values.toColumn("data", inferType = true, inferNulls = false).type willBe typeOf<Number?>()
        values.toColumnOf<Number?>("data").type willBe typeOf<Number?>()
        // SampleEnd
    }

    @Test
    fun createColumnRenamed() {
        // SampleStart
        val column = columnOf("Alice", "Bob") named "name"
        // SampleEnd
    }

    @Test
    fun createColumnGroup() {
        // SampleStart
        val firstName by columnOf("Alice", "Bob")
        val lastName by columnOf("Cooper", "Marley")

        val name by columnOf(firstName, lastName)
        // or
        listOf(firstName, lastName).toColumn("name")
        // SampleEnd
    }

    @Test
    fun createFrameColumn() {
        // SampleStart
        val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
        val df2 = dataFrameOf("name", "temp")("Mark", 36.6)

        val groups by columnOf(df1, df2)
        // or
        listOf(df1, df2).toColumn("groups")

        // SampleEnd
    }

    @Test
    fun createColumnAccessor() {
        // SampleStart
        val name by column<String>()
        // SampleEnd
    }

    @Test
    fun createColumnAccessorRenamed() {
        // SampleStart
        val accessor = column<String>("complex column name")
        // SampleEnd
    }

    @Test
    fun createDeepColumnAccessor() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        // SampleEnd
    }

    @Test
    fun createGroupOrFrameColumnAccessor() {
        // SampleStart
        val columns by columnGroup()
        val frames by frameColumn()
        // SampleEnd
    }

    @Test
    fun createDataFrameOf() {
        // SampleStart
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
            "Mark", 100
        )
        // SampleEnd
    }

    @Test
    fun createDataFrameWithFill() {
        // SampleStart
        val df = dataFrameOf('a'..'z') { 1..10 }
        // SampleEnd
    }

    @Test
    fun createDataFrameFillConstant() {
        // SampleStart
        val names = listOf("first", "second", "third")
        val df = dataFrameOf(names).fill(15, true)
        // SampleEnd
    }

    @Test
    fun createDataFrameWithRandom() {
        // SampleStart
        val df = dataFrameOf(1..5).randomDouble(7)
        // SampleEnd
    }

    @Test
    fun createDataFrameFromColumns() {
        // SampleStart
        val name by columnOf("Alice", "Bob")
        val age by columnOf(15, 20)

        val df1 = dataFrameOf(name, age)
        val df2 = listOf(name, age).toDataFrame()
        // SampleEnd
    }

    @Test
    fun createDataFrameFromMap() {
        // SampleStart
        val map = mapOf("name" to listOf("Alice", "Bob"), "age" to listOf(15, 20))
        val df = map.toDataFrame()
        // SampleEnd
    }

    @Test
    fun createDataFrameFromObject() {
        // SampleStart
        data class Person(val name: String, val age: Int)
        val persons = listOf(Person("Alice", 15), Person("Bob", 20))

        val df = persons.createDataFrame()
        // SampleEnd
        df.ncol() shouldBe 2
        df.nrow() shouldBe 2
        df["name"].type() shouldBe getType<String>()
        df["age"].type() shouldBe getType<Int>()
    }

    @Test
    fun createDataFrameFromObjectExplicit() {
        // SampleStart
        data class Person(val name: String, val age: Int)
        val persons = listOf(Person("Alice", 15), Person("Bob", 20))

        val df = persons.createDataFrame {
            "name" from { it.name }
            "year of birth" from { 2021 - it.age }
        }
        // SampleEnd
        df.ncol() shouldBe 2
        df.nrow() shouldBe 2
        df["name"].type() shouldBe getType<String>()
        df["year of birth"].type() shouldBe getType<Int>()
    }

    @Test
    fun createDataFrameFromDeepObject() {
        // SampleStart
        data class Name(val firstName: String, val lastName: String)
        data class Score(val subject: String, val value: Int)
        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val students = listOf(
            Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
            Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5)))
        )

        val df = students.createDataFrame(depth = 2)
        // SampleEnd
        df.ncol() shouldBe 3
        df.nrow() shouldBe 2
        df["name"].kind shouldBe ColumnKind.Group
        df["name"]["firstName"].type() shouldBe getType<String>()
        df["scores"].kind shouldBe ColumnKind.Frame
    }

    @Test
    fun createDataFrameFromDeepObjectWithExclude() {
        data class Name(val firstName: String, val lastName: String)
        data class Score(val subject: String, val value: Int)
        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val students = listOf(
            Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
            Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5)))
        )

        // SampleStart
        val df = students.createDataFrame {
            // add value column
            "year of birth" from { 2021 - it.age }

            // scan all properties
            properties(depth = 2) {
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
        df.ncol() shouldBe 5
        df.nrow() shouldBe 2
        df["name"].kind shouldBe ColumnKind.Value
        df["name"].type shouldBe getType<Name>()
        df["scores"].kind shouldBe ColumnKind.Frame
        df["summary"]["min score"].values shouldBe listOf(3, 5)
    }
}
