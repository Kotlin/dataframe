@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.DynamicDataFrameBuilder
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.ValueProperty
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.frameColumn
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.preserve
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.value
import org.jetbrains.kotlinx.dataframe.api.withValues
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.io.File
import kotlin.reflect.typeOf

class Create : TestBase() {

    @Test
    @TransformDataFrameExpressions
    fun createValueByColumnOf() {
        // SampleStart
        // Create ValueColumn with name 'student' and two elements of type String
        val student by columnOf("Alice", "Bob")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createValueByToColumn() {
        // SampleStart
        listOf("Alice", "Bob").toColumn("name")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnAccessorsUsage() {
        // SampleStart
        val age by column<Int>()

        // Access fourth cell in the "age" column of dataframe `df`.
        // This expression returns `Int` because variable `age` has `ColumnAccessor<Int>` type.
        // If dataframe `df` has no column "age" or column "age" has type which is incompatible with `Int`,
        // runtime exception will be thrown.
        df[age][3] + 5

        // Access first cell in the "age" column of dataframe `df`.
        df[0][age] * 2

        // Returns new dataframe sorted by age column (ascending)
        df.sortBy(age)

        // Returns new dataframe with the column "year of birth" added
        df.add("year of birth") { 2021 - age }

        // Returns new dataframe containing only rows with age > 30
        df.filter { age > 30 }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnAccessorToColumn() {
        // SampleStart
        val age by column<Int>()
        val ageCol1 = age.withValues(15, 20)
        val ageCol2 = age.withValues(1..10)
        // SampleEnd

        ageCol2.size() shouldBe 10
    }

    @Test
    @TransformDataFrameExpressions
    fun columnAccessorMap() {
        // SampleStart
        val age by column<Int>()
        val year by age.map { 2021 - it }

        df.filter { year > 2000 }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnAccessorComputed_properties() {
        // SampleStart
        val fullName by column(df) { name.firstName + " " + name.lastName }

        df[fullName]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnAccessorComputed_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()

        val fullName by column { firstName() + " " + lastName() }

        df[fullName]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnAccessorComputed_strings() {
        // SampleStart

        val fullName by column { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }

        df[fullName]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createValueColumnInferred() {
        // SampleStart
        val values: List<Any?> = listOf(1, 2.5)

        values.toColumn("data") // type: Any?
        values.toColumn("data", Infer.Type) // type: Number
        values.toColumn("data", Infer.Nulls) // type: Any
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createValueColumnOfType() {
        // SampleStart
        val values: List<Any?> = listOf(1, 2.5)

        values.toColumnOf<Number?>("data") // type: Number?
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createColumnRenamed() {
        // SampleStart
        val column = columnOf("Alice", "Bob") named "student"
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createColumnGroup() {
        // SampleStart
        val firstName by columnOf("Alice", "Bob")
        val lastName by columnOf("Cooper", "Marley")

        // Create ColumnGroup with two nested columns
        val fullName by columnOf(firstName, lastName)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createFrameColumn() {
        // SampleStart
        val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
        val df2 = dataFrameOf("name", "temp")("Charlie", 36.6)

        // Create FrameColumn with two elements of type DataFrame
        val frames by columnOf(df1, df2)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createColumnAccessor() {
        // SampleStart
        val name by column<String>()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createColumnAccessorRenamed() {
        // SampleStart
        val accessor by column<String>("complex column name")
        // SampleEnd
        accessor.name() shouldBe "complex column name"
    }

    @Test
    @TransformDataFrameExpressions
    fun createDeepColumnAccessor() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createGroupOrFrameColumnAccessor() {
        // SampleStart
        val columns by columnGroup()
        val frames by frameColumn()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createEmptyDataFrame() {
        // SampleStart
        val df = emptyDataFrame<Any>()
        // SampleEnd
        df.columnsCount() shouldBe 0
        df.rowsCount() shouldBe 0
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameOf() {
        // SampleStart
        // DataFrame with 2 columns and 3 rows
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
            "Charlie", 100,
        )
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameOfPairs() {
        // SampleStart
        // DataFrame with 2 columns and 3 rows
        val df = dataFrameOf(
            "name" to listOf("Alice", "Bob", "Charlie"),
            "age" to listOf(15, 20, 100),
        )
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameWithFill() {
        // SampleStart
        // Multiplication table
        dataFrameOf(1..10) { x -> (1..10).map { x * it } }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameFillConstant() {
        // SampleStart
        val names = listOf("first", "second", "third")

        // DataFrame with 3 columns, fill each column with 15 `true` values
        val df = dataFrameOf(names).fill(15, true)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameWithRandom() {
        // SampleStart
        // 5 columns filled with 7 random double values:
        val names = (1..5).map { "column$it" }
        dataFrameOf(names).randomDouble(7)

        // 5 columns filled with 7 random double values between 0 and 1 (inclusive)
        dataFrameOf(names).randomDouble(7, 0.0..1.0).print()

        // 5 columns filled with 7 random int values between 0 and 100 (inclusive)
        dataFrameOf(names).randomInt(7, 0..100).print()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameFromColumns() {
        // SampleStart

        val name by columnOf("Alice", "Bob", "Charlie")
        val age by columnOf(15, 20, 22)

        // DataFrame with 2 columns
        val df = dataFrameOf(name, age)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameFromMap() {
        // SampleStart
        val map = mapOf("name" to listOf("Alice", "Bob", "Charlie"), "age" to listOf(15, 20, 22))

        // DataFrame with 2 columns
        map.toDataFrame()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameFromIterable() {
        // SampleStart
        val name by columnOf("Alice", "Bob", "Charlie")
        val age by columnOf(15, 20, 22)

        listOf(name, age).toDataFrame()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameFromNamesAndValues() {
        // SampleStart
        val names = listOf("name", "age")
        val values = listOf(
            "Alice", 15,
            "Bob", 20,
            "Charlie", 22,
        )
        val df = dataFrameOf(names, values)
        // SampleEnd
        df.columnNames() shouldBe listOf("name", "age")
        df.rowsCount() shouldBe 3
        df["name"].type() shouldBe typeOf<String>()
        df["age"].type() shouldBe typeOf<Int>()
    }

    @Test
    @TransformDataFrameExpressions
    fun readDataFrameFromValues() {
        // SampleStart
        val names = listOf("Alice", "Bob", "Charlie")
        val df = names.toDataFrame() as DataFrame<ValueProperty<String>>
        df.add("length") { value.length }
        // SampleEnd
        df.value.toList() shouldBe names
    }

    @Test
    @TransformDataFrameExpressions
    fun readDataFrameFromObject() {
        // SampleStart
        data class Person(val name: String, val age: Int)

        val persons = listOf(Person("Alice", 15), Person("Bob", 20), Person("Charlie", 22))

        val df = persons.toDataFrame()
        // SampleEnd
        df.columnsCount() shouldBe 2
        df.rowsCount() shouldBe 3
        df["name"].type() shouldBe typeOf<String>()
        df["age"].type() shouldBe typeOf<Int>()
    }

    @Test
    @TransformDataFrameExpressions
    fun readDataFrameFromDeepObject() {
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
        df.columnsCount() shouldBe 3
        df.rowsCount() shouldBe 2
        df["name"].kind shouldBe ColumnKind.Group
        df["name"]["firstName"].type() shouldBe typeOf<String>()
        df["scores"].kind shouldBe ColumnKind.Frame
    }

    @Test
    @TransformDataFrameExpressions
    fun readDataFrameFromDeepObjectWithExclude() {
        data class Name(val firstName: String, val lastName: String)

        data class Score(val subject: String, val value: Int)

        data class Student(val name: Name, val age: Int, val scores: List<Score>)

        val students = listOf(
            Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
            Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
        )

        // SampleStart
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
        df.columnsCount() shouldBe 5
        df.rowsCount() shouldBe 2
        df["name"].kind shouldBe ColumnKind.Value
        df["name"].type shouldBe typeOf<Name>()
        df["scores"].kind shouldBe ColumnKind.Frame
        df["summary"]["min score"].values() shouldBe listOf(3, 5)
    }

    @Test
    @TransformDataFrameExpressions
    fun duplicatedColumns() {
        // SampleStart
        fun peek(vararg dataframes: AnyFrame): AnyFrame {
            val builder = DynamicDataFrameBuilder()
            for (df in dataframes) {
                df.columns().firstOrNull()?.let { builder.add(it) }
            }
            return builder.toDataFrame()
        }

        val col by columnOf(1, 2, 3)
        peek(dataFrameOf(col), dataFrameOf(col))
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun toDataFrameColumn() {
        // SampleStart
        val files = listOf(File("data.csv"), File("data1.csv"))
        val df = files.toDataFrame(columnName = "data")
        // SampleEnd
    }
}
