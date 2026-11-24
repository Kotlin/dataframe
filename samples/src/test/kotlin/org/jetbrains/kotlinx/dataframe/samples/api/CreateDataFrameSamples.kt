package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DynamicDataFrameBuilder
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.preserve
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.value
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test
import java.io.File
import kotlin.random.nextInt
import kotlin.reflect.typeOf
import kotlin.random.Random as KotlinRandom

class CreateDataFrameSamples : DataFrameSampleHelper("createDataFrame", "api") {

    @DataSchema
    class Person(val name: String, val age: Int)

    @Test
    fun createEmptyDataFrame() {
        // SampleStart
        val df = emptyDataFrame<Person>()
        // SampleEnd
        // @DataSchema gives the typed accessors
        df.name.size() shouldBe 0
        df.columnsCount() shouldBe 2
        df.rowsCount() shouldBe 0
    }

    @Test
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
    fun createDataFrameFromColumns() {
        // SampleStart
        // DataFrame with 2 columns
        val df = dataFrameOf(
            "name" to columnOf("Alice", "Bob", "Charlie"),
            "age" to columnOf(15, 20, 22),
        )
        // SampleEnd
    }

    @Test
    @Suppress("ktlint:standard:argument-list-wrapping") // one row of values per line
    fun createDataFrameOf() {
        // SampleStart
        // DataFrame with 2 columns and 3 rows
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
            "Charlie", 100,
        )
        // SampleEnd
        df.saveDfHtmlSample()
    }

    @Test
    fun createDataFrameBuilderFill() {
        // SampleStart
        // Every column: 3 rows filled with 0
        val filled = dataFrameOf("a", "b").fill(3, 0)
        // Every column: 3 rows of null values of type Int?
        val empty = dataFrameOf("a", "b").nulls<Int>(3)
        // SampleEnd
        filled["a"].values shouldBe listOf(0, 0, 0)
        empty["a"].type shouldBe typeOf<Int?>()
    }

    @Test
    fun createDataFrameFromMap() {
        // SampleStart
        val map = mapOf(
            "name" to listOf("Alice", "Bob", "Charlie"),
            "age" to listOf(15, 20, 22),
        )

        // DataFrame with 2 columns
        map.toDataFrame()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun createRandomDataFrame() {
        // stable random + clean examples
        @Suppress("LocalVariableName", "ktlint:standard:property-naming")
        val Random = KotlinRandom(42)

        fun <T> List<T>.random() = this.random(Random)
        // SampleStart
        val categories = listOf("Electronics", "Books", "Clothing")
        // DataFrame with 4 columns and 7 rows
        (0 until 7).toDataFrame {
            "productId" from { "P${1000 + it}" }
            "category" from { categories.random() }
            "price" from { Random.nextDouble(10.0, 500.0) }
            "inStock" from { Random.nextInt(0..100) }
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun createNestedRandomDataFrame() {
        // stable random + clean examples
        @Suppress("LocalVariableName", "ktlint:standard:property-naming")
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
                "yearEstablished" from { Random.nextInt(1950..2020) }
            }

            // Frame Column
            "reviews" from {
                val reviewCount = Random.nextInt(0..7)
                (0 until reviewCount).toDataFrame {
                    val ratings: DataColumn<Int> = expr { Random.nextInt(1..5) }
                    val comments = ratings.map {
                        when (it) {
                            5 -> listOf(
                                "Amazing quality!",
                                "Best purchase ever!",
                                "Highly recommend!",
                                "Absolutely perfect!",
                            )

                            4 -> listOf("Great product!", "Very satisfied", "Good value for money", "Would buy again")

                            3 -> listOf("It's okay", "Does the job", "Average quality", "Neither good nor bad")

                            2 -> listOf("Could be better", "Disappointed", "Not what I expected", "Poor quality")

                            else -> listOf("Terrible!", "Not worth the price", "Complete waste of money", "Do not buy!")
                        }.random()
                    }

                    "author" from { "User${Random.nextInt(1000..10000)}" }
                    ratings into "rating"
                    comments into "comment"
                }
            }
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun createDataFrameWithFill() {
        // SampleStart
        // Multiplication table
        (1..10).toDataFrame {
            (1..10).forEach { x ->
                "$x" from { x * it }
            }
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readDataFrameFromValues() {
        // SampleStart
        val names = listOf("Alice", "Bob", "Charlie")
        val df = names.toDataFrame()
        df.add("length") { value.length }
        // SampleEnd
        df.value.toList() shouldBe names
    }

    @Test
    fun toDataFrameColumn() {
        // SampleStart
        val files = listOf(File("data.csv"), File("data1.csv"))
        val df = files.toDataFrame(columnName = "data")
        // SampleEnd
        df.saveDfHtmlSample()
    }

    @Test
    fun toDataFrameLists() {
        // SampleStart
        val lines =
            """
            1
            00:00:05,000 --> 00:00:07,500
            This is the first subtitle.

            2
            00:00:08,000 --> 00:00:10,250
            This is the second subtitle.
            """.trimIndent().lines()

        lines.chunked(4) { it.take(3) }
            .toDataFrame(header = listOf("n", "timestamp", "text"))
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun readDataFrameFromObject() {
        // SampleStart
        data class Person(val name: String, val age: Int)

        val persons = listOf(
            Person("Alice", 15),
            Person("Bob", 20),
            Person("Charlie", 22),
        )

        val df = persons.toDataFrame()
        // SampleEnd
        df.saveDfHtmlSample()
    }

    @Test
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
        df.saveDfHtmlSample()
    }

    @Test
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
        df.saveDfHtmlSample()
    }

    @Test
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
        val other = columnOf(4, 5, 6) named "col"
        peek(dataFrameOf(col), dataFrameOf(other))
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun createDataFrameFromIterableHeader() {
        // SampleStart
        val header = listOf("name", "age")
        val df = dataFrameOf(header)("Alice", 20, "Bob", 25)
        // SampleEnd
        df.columnNames() shouldBe listOf("name", "age")
    }

    @Test
    fun createDataFrameFromPairsOfLists() {
        // SampleStart
        val df = dataFrameOf(
            "name" to listOf("Alice", "Bob"),
            "age" to listOf(15, 20),
        )
        // SampleEnd
        df["age"].values shouldBe listOf(15, 20)
    }

    @Test
    fun createDataFrameFromVarargHeaderFill() {
        // SampleStart
        // The reified argument is an upper bound: with Number, the columns are inferred as Int
        val df = dataFrameOf("a", "b") { listOf<Number>(1, 2) }
        // SampleEnd
        df["a"].type shouldBe typeOf<Int>()
    }

    @Test
    fun createDataFrameFromHeaderFill() {
        // SampleStart
        // The reified argument is an upper bound: with Number, the columns are inferred as Int
        val df = dataFrameOf(listOf("a", "b")) { listOf<Number>(1, 2) }
        // SampleEnd
        df["a"].type shouldBe typeOf<Int>()
    }

    @Test
    fun createDataFrameFromChars() {
        // SampleStart
        // The characters become the column names
        val df = dataFrameOf('a'..'c')(1, 2, 3, 4, 5, 6)
        // SampleEnd
        df.columnNames() shouldBe listOf("a", "b", "c")
    }

    @Test
    fun createDataFrameFromBuilderColumns() {
        // SampleStart
        // The columns are renamed to the header names
        val df = dataFrameOf("name", "age")(columnOf("Alice", "Bob"), columnOf(15, 20))
        // SampleEnd
        df["name"].values shouldBe listOf("Alice", "Bob")
    }

    @Test
    fun createDataFrameFromValuesBuilder() {
        // SampleStart
        // The lambda receives the column name and returns the values of that column
        val df = dataFrameOf("a", "bb") { name -> listOf(name.length, name.length * 2) }
        // SampleEnd
        df["a"].values shouldBe listOf(1, 2)
        df["bb"].values shouldBe listOf(2, 4)
    }

    @Test
    fun createDataFrameWithColumns() {
        // SampleStart
        // The lambda receives the column name and builds the whole column
        val df = dataFrameOf("a", "bb").withColumns { name -> columnOf(name.length, name.length + 1) }
        // SampleEnd
        df["a"].values shouldBe listOf(1, 2)
        df["bb"].values shouldBe listOf(2, 3)
    }

    @Test
    fun createDataFrameFillFrameColumn() {
        // SampleStart
        val userDf = dataFrameOf("name")("Alice", "Bob")

        // Every cell holds the whole DataFrame
        val df = dataFrameOf("user1", "user2").fill(2, userDf)
        // SampleEnd
        df["user1"].values.count() shouldBe 2
        (df["user1"].values.first() as DataFrame<*>).columnNames() shouldBe listOf("name")
    }

    @Test
    fun createDataFrameFillIndexed() {
        // SampleStart
        // The lambda receives the row index and the column name
        val df = dataFrameOf("a", "b").fillIndexed(3) { index, _ -> index }
        // SampleEnd
        df["a"].values shouldBe listOf(0, 1, 2)
        df["b"].values shouldBe listOf(0, 1, 2)
    }

    @Test
    fun createDataFrameFillByIndex() {
        // SampleStart
        // The lambda receives the row index
        val df = dataFrameOf("a", "b").fill(3) { it * it }
        // SampleEnd
        df["a"].values shouldBe listOf(0, 1, 4)
        df["b"].values shouldBe listOf(0, 1, 4)
    }

    @Test
    fun createDataFrameRandomBuilders() {
        // SampleStart
        val ints = dataFrameOf("i").randomInt(3)
        val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
        val longs = dataFrameOf("l").randomLong(3)
        val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
        val doubles = dataFrameOf("d").randomDouble(3)
        val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
        val floats = dataFrameOf("f").randomFloat(3)
        val flags = dataFrameOf("flag").randomBoolean(3)
        // SampleEnd
        ints.rowsCount() shouldBe 3
        boundedLongs["bl"].values.all { (it as Long) in 1L..10L } shouldBe true
        boundedDoubles["bd"].values.all { (it as Double) < 10.0 } shouldBe true
    }
}
