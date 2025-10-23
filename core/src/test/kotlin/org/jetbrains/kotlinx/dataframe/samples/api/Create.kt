@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
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
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.junit.Test
import java.io.File
import kotlin.random.Random as KotlinRandom

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
    fun createRandomDataFrame() {
        // stable random + clean examples
        @Suppress("LocalVariableName")
        val Random = KotlinRandom(42)
        fun <T> List<T>.random() = this.random(Random)
        // SampleStart
        val categories = listOf("Electronics", "Books", "Clothing")
        // DataFrame with 4 columns and 7 rows
        (0 until 7).toDataFrame {
            "productId" from { "P${1000 + it}" }
            "category" from { categories.random() }
            "price" from { Random.nextDouble(10.0, 500.0) }
            "inStock" from { Random.nextInt(0, 100) }
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createNestedRandomDataFrame() {
        // stable random + clean examples
        @Suppress("LocalVariableName")
        val Random = KotlinRandom(42)
        fun <T> List<T>.random() = this.random(Random)
        // SampleStart
        val categories = listOf("Electronics", "Books", "Clothing")
        // DataFrame with 5 columns and 7 rows
        (0 until 7).toDataFrame {
            "productId" from { "P${1000 + it}" }
            "category" from { categories.random() }
            "price" from { Random.nextDouble(10.0, 500.0) }

            // Column Group
            "manufacturer" {
                "country" from { listOf("USA", "China", "Germany", "Japan").random() }
                "yearEstablished" from { Random.nextInt(1950, 2020) }
            }

            // Frame Column
            "reviews" from {
                val reviewCount = Random.nextInt(0, 8)
                (0 until reviewCount).toDataFrame {
                    val ratings: DataColumn<Int> = expr { Random.nextInt(1, 6) }
                    val comments = ratings.map {
                        when (it) {
                            5 -> listOf("Amazing quality!", "Best purchase ever!", "Highly recommend!", "Absolutely perfect!")
                            4 -> listOf("Great product!", "Very satisfied", "Good value for money", "Would buy again")
                            3 -> listOf("It's okay", "Does the job", "Average quality", "Neither good nor bad")
                            2 -> listOf("Could be better", "Disappointed", "Not what I expected", "Poor quality")
                            else -> listOf("Terrible!", "Not worth the price", "Complete waste of money", "Do not buy!")
                        }.random()
                    }

                    "author" from { "User${Random.nextInt(1000, 9999)}" }
                    ratings into "rating"
                    comments into "comment"
                }
            }
        }
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
    fun createNestedDataFrameInplace() {
        // SampleStart
        // DataFrame with 2 columns and 3 rows
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", "Bob", "Charlie"),
                "lastName" to columnOf("Cooper", "Dylan", "Daniels"),
            ),
            "age" to columnOf(15, 20, 100),
        )
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDataFrameWithFill() {
        // SampleStart
        // Multiplication table
        (1..10).toDataFrame {
            (1..10).forEach { x ->
                "$x" from { x * it }
            }
        }
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
        // DataFrame with 2 columns
        val df = dataFrameOf(
            "name" to columnOf("Alice", "Bob", "Charlie"),
            "age" to columnOf(15, 20, 22)
        )
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
    fun readDataFrameFromValues() {
        // SampleStart
        val names = listOf("Alice", "Bob", "Charlie")
        // TODO fix with plugin???
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

    @Test
    @TransformDataFrameExpressions
    fun toDataFrameLists() {
        // SampleStart
        val lines = """
            1
            00:00:05,000 --> 00:00:07,500
            This is the first subtitle.

            2
            00:00:08,000 --> 00:00:10,250
            This is the second subtitle.
        """.trimIndent().lines()

        lines.chunked(4) { it.take(3) }.toDataFrame(header = listOf("n", "timestamp", "text"))
        // SampleEnd
    }
}
