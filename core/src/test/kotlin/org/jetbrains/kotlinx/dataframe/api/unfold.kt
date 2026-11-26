package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.shouldHaveColumn
import org.jetbrains.kotlinx.dataframe.shouldHaveColumnGroup
import org.jetbrains.kotlinx.dataframe.shouldHaveFrameColumn
import org.junit.Ignore
import org.junit.Test
import kotlin.reflect.typeOf

class UnfoldTests {
    @Test
    fun unfold() {
        val df = dataFrameOf(
            "col" to listOf(A("123", 321)),
        )

        val res = df.unfold { col("col") }
        res[pathOf("col", "str")][0] shouldBe "123"
        res[pathOf("col", "i")][0] shouldBe 321
    }

    @Test
    fun `unfold deep`() {
        val df1 = dataFrameOf(
            "col" to listOf(
                Group(
                    "1",
                    listOf(
                        Person("Alice", "Cooper", 15, "London"),
                        Person("Bob", "Dylan", 45, "Dubai"),
                    ),
                ),
                Group(
                    "2",
                    listOf(
                        Person("Charlie", "Daniels", 20, "Moscow"),
                        Person("Charlie", "Chaplin", 40, "Milan"),
                    ),
                ),
            ),
        )

        df1.unfold { col("col") }[pathOf("col", "participants")].type() shouldBe typeOf<List<Person>>()

        df1.unfold(maxDepth = 2) { col("col") }[pathOf("col", "participants")][0].shouldBeInstanceOf<AnyFrame> {
            it["firstName"][0] shouldBe "Alice"
        }
    }

    @Test
    fun `keep value type`() {
        val values = listOf(1, 2, 3, 4)
        val df2 = dataFrameOf("int" to values)
        val column = df2.unfold { col("int") }["int"]
        column.type() shouldBe typeOf<Int>()
        column.values() shouldBe values
    }

    data class A(val str: String, val i: Int)

    data class Person(
        val firstName: String,
        val lastName: String,
        val age: Int,
        val city: String?,
    )

    data class Group(val id: String, val participants: List<Person>)

    @Test
    fun `unfold pair of dataframe structures`() {
        val schema = dataFrameOf("b" to columnOf(42)).cast<SimpleDataSchema>()

        val df = dataFrameOf("pairs" to columnOf(schema to schema.first()))
            .unfold("pairs")

        df.schema().asClue {
            val pairsGroup = df.shouldHaveColumnGroup("pairs")
            pairsGroup.shouldHaveFrameColumn("first") {
                it[0].shouldHaveColumn<Int>("b")
            }
            pairsGroup.shouldHaveColumnGroup("second") {
                it.shouldHaveColumn<Int>("b")
            }
        }
    }

    @DataSchema
    data class SimpleDataSchema(val b: Int)

    @Test
    fun `unfold pair of dataschema object structures`() {
        val element = SimpleDataSchema(42)
        val df = dataFrameOf("pairs" to columnOf(listOf(element) to element))
            .unfold("pairs")

        df.schema().asClue {
            val pairsGroup = df.shouldHaveColumnGroup("pairs")
            pairsGroup.shouldHaveFrameColumn("first") {
                it[0].shouldHaveColumn<Int>("b")
            }
            pairsGroup.shouldHaveColumnGroup("second") {
                it.shouldHaveColumn<Int>("b")
            }
        }
    }

    // region the dataset of the `unfold` KDoc and website page

    data class Name(val firstName: String, val lastName: String)

    data class Score(val subject: String, val value: Int)

    data class Student(val name: Name, val age: Int, val scores: List<Score>)

    private val students = dataFrameOf(
        "id" to columnOf(1, 2),
        "student" to columnOf(
            Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
            Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
        ),
        "year" to columnOf(2021, 2022),
    )

    // endregion

    @Test
    fun `unfold turns a column of objects into a column group with a column per property`() {
        val res = students.unfold("student")
        // the order of the constructor, not the alphabetical one
        res["student"].asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores")
        res[pathOf("student", "age")].values() shouldBe listOf(15, 20)
    }

    @Test
    fun `unfold keeps the name and the place of the column and does not change other columns`() {
        val res = students.unfold("student")
        res.columnNames() shouldBe listOf("id", "student", "year")
        res["student"].kind() shouldBe ColumnKind.Group
        res["id"].values() shouldBe listOf(1, 2)
        res["year"].values() shouldBe listOf(2021, 2022)
    }

    @Test
    fun `unfold with default maxDepth keeps nested objects and lists as values`() {
        val res = students.unfold("student")
        res[pathOf("student", "name")].type() shouldBe typeOf<Name>()
        res[pathOf("student", "name")].values() shouldBe listOf(Name("Alice", "Cooper"), Name("Bob", "Marley"))
        res[pathOf("student", "scores")].type() shouldBe typeOf<List<Score>>()
    }

    @Test
    fun `unfold with maxDepth 1 turns nested objects into column groups and lists of objects into frame columns`() {
        val res = students.unfold(maxDepth = 1) { col("student") }
        res[pathOf("student", "name")].asColumnGroup().columnNames() shouldBe listOf("firstName", "lastName")
        res[pathOf("student", "name", "firstName")].values() shouldBe listOf("Alice", "Bob")
        res[pathOf("student", "scores")].kind() shouldBe ColumnKind.Frame
        res[pathOf("student", "scores")][0] shouldBe dataFrameOf("subject", "value")("math", 4, "biology", 3)
    }

    data class Course(val title: String, val teacher: Student)

    @Test
    fun `unfold with maxDepth 2 goes one level deeper than with maxDepth 1`() {
        val df = dataFrameOf(
            "course" to columnOf(Course("Algebra", Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4))))),
        )
        val depth1 = df.unfold(maxDepth = 1) { col("course") }
        depth1[pathOf("course", "teacher")].asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores")
        depth1[pathOf("course", "teacher", "name")].type() shouldBe typeOf<Name>()

        val depth2 = df.unfold(maxDepth = 2) { col("course") }
        depth2[pathOf("course", "teacher", "name", "firstName")].values() shouldBe listOf("Alice")
        depth2[pathOf("course", "teacher", "scores")].kind() shouldBe ColumnKind.Frame
    }

    @Test
    fun `unfold with roots makes columns only for the given properties`() {
        // the new columns follow the order of the constructor, not the order of the roots
        val res = students.unfold(Student::age, Student::name) { col("student") }
        res["student"].asColumnGroup().columnNames() shouldBe listOf("name", "age")
    }

    @Test
    fun `DataFrame unfold reads the properties of the type of the column`() {
        // the values are `Pet`s, but the type of the column is `Named`
        val pets = DataColumn.createValueColumn("pet", listOf(Pet("Rex", "dog"), Pet("Tom", "cat")), typeOf<Named>())
        val res = dataFrameOf(pets).unfold("pet")
        res["pet"].asColumnGroup().columnNames() shouldBe listOf("name")
    }

    enum class Level { BEGINNER, ADVANCED }

    class NoProperties

    @Test
    fun `unfold leaves a column of enums as it is`() {
        val df = dataFrameOf("level" to columnOf(Level.BEGINNER, Level.ADVANCED))
        df.unfold("level") shouldBe df
    }

    @Test
    fun `unfold leaves a column of objects without public properties as it is`() {
        val df = dataFrameOf("marker" to columnOf(NoProperties(), NoProperties()))
        df.unfold("marker") shouldBe df
    }

    @Test
    fun `unfold leaves a column of type Any as it is`() {
        // objects of different classes: the type of the column is Any
        val df = dataFrameOf("mixed" to columnOf<Any>(Name("Alice", "Cooper"), Score("math", 4)))
        df["mixed"].type() shouldBe typeOf<Any>()
        df.unfold("mixed") shouldBe df
    }

    @Test
    fun `unfold leaves a column of type Any as it is even when all objects are of one class`() {
        val df = dataFrameOf("name" to listOf<Any>(Name("Alice", "Cooper")).toColumn())
        df["name"].type() shouldBe typeOf<Any>()
        df.unfold("name") shouldBe df
        df.unfold { "name"<Any>() } shouldBe df
    }

    class Bean {
        var zeta: Int = 1
        var alpha: Int = 2
    }

    @Test
    fun `unfold orders the columns of a class without a primary constructor by name`() {
        val res = dataFrameOf("bean" to columnOf(Bean())).unfold("bean")
        // declared as `zeta`, then `alpha`
        res["bean"].asColumnGroup().columnNames() shouldBe listOf("alpha", "zeta")
    }

    class WithGetter {
        fun getScore(): Int = 4
    }

    @Test
    fun `unfold with a getter-like function among roots makes a column named after the property`() {
        val res = dataFrameOf("item" to columnOf(WithGetter())).unfold(WithGetter::getScore) { col("item") }
        res["item"].asColumnGroup().columnNames() shouldBe listOf("score")
        res[pathOf("item", "score")].values() shouldBe listOf(4)
    }

    @Test
    fun `unfold leaves column groups and frame columns as they are`() {
        val grouped = students.unfold("student")
        grouped.unfold("student") shouldBe grouped

        val frames = dataFrameOf("frames" to columnOf(dataFrameOf("x")(1), dataFrameOf("x")(2)))
        frames["frames"].kind() shouldBe ColumnKind.Frame
        frames.unfold("frames") shouldBe frames
    }

    @Test
    fun `unfold of null objects gives nulls in every new column`() {
        val df = dataFrameOf("name" to columnOf(Name("Alice", "Cooper"), null))
        val res = df.unfold("name")
        res[pathOf("name", "firstName")].type() shouldBe typeOf<String?>()
        res[pathOf("name", "firstName")].values() shouldBe listOf("Alice", null)
        res[pathOf("name", "lastName")].values() shouldBe listOf("Cooper", null)
    }

    @Test
    fun `unfold of null objects gives an empty dataframe in a frame column`() {
        val df = dataFrameOf(
            "student" to columnOf(Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4))), null),
        )
        val res = df.unfold(maxDepth = 1) { col("student") }
        res[pathOf("student", "age")].values() shouldBe listOf(15, null)
        res[pathOf("student", "scores")].kind() shouldBe ColumnKind.Frame
        res[pathOf("student", "scores")][1] shouldBe DataFrame.empty()
    }

    @Test
    fun `unfold ignores roots on a column of simple values`() {
        val df = dataFrameOf("word" to columnOf("abc", "de"))
        df.unfold(String::length) { col("word") } shouldBe df
    }

    @Test
    fun `unfold several columns by name`() {
        val df = dataFrameOf(
            "name" to columnOf(Name("Alice", "Cooper")),
            "score" to columnOf(Score("math", 4)),
        )
        val res = df.unfold("name", "score")
        res["name"].asColumnGroup().columnNames() shouldBe listOf("firstName", "lastName")
        res["score"].asColumnGroup().columnNames() shouldBe listOf("subject", "value")
    }

    @Test
    fun `DataColumn unfold returns a column group with the name of the column`() {
        val column: DataColumn<Student> = students["student"].cast()
        val res: AnyCol = column.unfold()
        res.name() shouldBe "student"
        res.asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores")
        res.asColumnGroup()["age"].values() shouldBe listOf(15, 20)
    }

    @Test
    fun `DataColumn unfold with maxDepth 1 turns nested objects into column groups and lists of objects into frame columns`() {
        val column: DataColumn<Student> = students["student"].cast()
        val res = column.unfold(maxDepth = 1).asColumnGroup()
        res["name"].asColumnGroup().columnNames() shouldBe listOf("firstName", "lastName")
        res["scores"].kind() shouldBe ColumnKind.Frame
    }

    @Test
    fun `DataColumn unfold returns the column itself when it cannot be unfolded`() {
        val ages = columnOf(15, 20)
        ages.unfold() shouldBeSameInstanceAs ages

        val group = students.unfold("student")["student"]
        group.unfold() shouldBeSameInstanceAs group
    }

    interface Named {
        val name: String
    }

    data class Pet(override val name: String, val kind: String) : Named

    @Test
    fun `DataColumn unfold reads the properties of the static type of the column`() {
        val column: DataColumn<Named> = columnOf(Pet("Rex", "dog"), Pet("Tom", "cat")).cast()
        // `kind` is a property of `Pet`, but not of `Named`
        column.unfold().asColumnGroup().columnNames() shouldBe listOf("name")
    }

    @Test
    fun `DataColumn unfold reads the properties of the values when the static type is Any`() {
        val column: AnyCol = students["student"]
        column.unfold().asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores")

        val anyColumn: DataColumn<Any> = students["student"].cast()
        anyColumn.unfold().asColumnGroup().columnNames() shouldBe listOf("name", "age", "scores")
    }

    @Test
    fun `DataColumn unfold reads the properties of the static type when the type of the column is Any`() {
        val column: DataColumn<Name> = listOf<Any>(Name("Alice", "Cooper")).toColumn("name").cast()
        column.type() shouldBe typeOf<Any>()
        column.unfold().asColumnGroup().columnNames() shouldBe listOf("firstName", "lastName")
    }

    @Test
    fun `DataColumn unfold returns the column itself when both the static type and the type of the column are Any`() {
        val column: DataColumn<Any> = listOf<Any>(Name("Alice", "Cooper")).toColumn("name")
        column.type() shouldBe typeOf<Any>()
        column.unfold() shouldBeSameInstanceAs column
    }

    // region known issues: `unfold` is not consistent with `toDataFrame` yet
    // TODO(#2114): enable these tests once the issue is fixed

    @Ignore("unfold ignores roots on a column of simple values")
    @Test
    fun `unfold applies roots to a column of simple values`() {
        // replaces `unfold ignores roots on a column of simple values` once fixed
        val df = dataFrameOf("word" to columnOf("abc", "de"))
        val res = df.unfold(String::length) { col("word") }
        // the same as `listOf("abc", "de").toDataFrame(String::length)`
        res["word"].asColumnGroup().columnNames() shouldBe listOf("length")
        res[pathOf("word", "length")].values() shouldBe listOf(3, 2)
    }

    @Ignore("unfold stores the exceptions as values when roots are not properties of the column type")
    @Test
    fun `unfold rejects roots that are not properties of the column type`() {
        shouldThrow<IllegalArgumentException> {
            students.unfold(Score::value) { col("student") }
        }
    }

    @Ignore("unfold turns a column of lists into a column group with the `size` property")
    @Test
    fun `unfold turns a column of lists of objects into a frame column`() {
        val df = dataFrameOf(
            "scores" to columnOf(listOf(Score("math", 4), Score("biology", 3)), listOf(Score("music", 5))),
        )
        val res = df.unfold("scores")
        res["scores"].kind() shouldBe ColumnKind.Frame
        res["scores"][0] shouldBe dataFrameOf("subject", "value")("math", 4, "biology", 3)
        res["scores"][1] shouldBe dataFrameOf("subject", "value")("music", 5)
    }

    @Ignore("unfold turns a column of maps into a column group with the properties of Map")
    @Test
    fun `unfold leaves a column of maps as it is`() {
        val df = dataFrameOf("scores" to columnOf(mapOf("math" to 4), mapOf("music" to 5)))
        df.unfold("scores") shouldBe df
    }

    // endregion
}
