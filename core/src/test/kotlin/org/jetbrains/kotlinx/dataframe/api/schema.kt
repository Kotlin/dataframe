package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.junit.Test

class SchemaTests {

    @Test
    fun `columns order test`() {
        val row = dataFrameOf("c", "b")(4, 5).first()
        val df = dataFrameOf("abc", "a", "a123", "nested")(1, 2, 3, row).cast<Schema>()
        df.schema().toString() shouldBe df.compileTimeSchema().toString()
    }

    @Test
    fun `schema is read from the columns, not from the type argument`() {
        // the type argument declares `b` and `c`, while the columns are `name` and `age`
        val df = dataFrameOf("name", "age")("Alice", 15).cast<Nested>()
        df.schema().toString() shouldBe
            """
            |name: String
            |age: Int
            """.trimMargin()
    }

    @Test
    fun `frame column schema keeps only the columns all its dataframes have`() {
        val outer = dataFrameOf("g")(
            dataFrameOf("a", "b")(1, 2),
            dataFrameOf("a", "c")(3, 4),
        )
        // only `a` is in both, so neither `b` nor `c` is in the schema
        outer.schema().toString() shouldBe
            """
            |g: *
            |    a: Int
            |
            """.trimMargin()
    }

    @Test
    fun `empty dataframes in a frame column are left out of the schema`() {
        val outer = dataFrameOf("g")(
            dataFrameOf("a", "b")(1, 2),
            DataFrame.empty(),
        )
        // the empty dataframe does not take `a` and `b` away
        outer.schema().toString() shouldBe
            """
            |g: *
            |    a: Int
            |    b: Int
            |
            """.trimMargin()
    }

    @Test
    fun `row schema describes the whole dataframe and is the same for every row`() {
        val df = dataFrameOf("name", "age")("Alice", 15, "Bob", 20)
        val expected =
            """
            |name: String
            |age: Int
            """.trimMargin()
        df[0].schema().toString() shouldBe expected
        df[1].schema().toString() shouldBe expected
    }

    @Test
    fun `row of a column group has the schema of that group`() {
        val df = dataFrameOf("name", "age")("Alice", 15).group("name", "age").into("g")
        val groupRow = df.first()["g"] as DataRow<*>
        groupRow.schema().toString() shouldBe
            """
            |name: String
            |age: Int
            """.trimMargin()
    }

    @Test
    fun `groupBy schema has the key columns and a frame column named group`() {
        val df = dataFrameOf("city", "age")("London", 1, "Paris", 2)
        df.groupBy("city").schema().toString() shouldBe
            """
            |city: String
            |group: *
            |    city: String
            |    age: Int
            |
            """.trimMargin()
    }

    @Test
    fun `KDoc example -- compile-time schema comes from the type argument`() {
        val df = dataFrameOf("name", "age")("Alice", 15).cast<Person>()
        df.compileTimeSchema().toString() shouldBe
            """
            |name: String
            |age: Int
            """.trimMargin()
        // unordered, the marker is an interface, so the order is the one reflection reports
        // the properties in — neither the declaration order nor the order of the columns
        df.compileTimeSchema(ordered = false).toString() shouldBe
            """
            |age: Int
            |name: String
            """.trimMargin()
    }

    @Test
    fun `compile-time and runtime schema differ when the type does not fit the columns`() {
        val df = dataFrameOf("name")("Alice").cast<Person>()
        df.schema().columns.keys shouldBe setOf("name")
        df.compileTimeSchema().columns.keys shouldBe setOf("age", "name")
    }

    @Test
    fun `a column the runtime schema does not have comes first when ordered`() {
        // `age` has no counterpart among the columns, so it has no position to be sorted into
        val df = dataFrameOf("name")("Alice").cast<Person>()
        df.compileTimeSchema().columns.keys.toList() shouldBe listOf("age", "name")
    }

    @Test
    fun `unordered, a data class marker keeps its primary constructor order`() {
        // the marker has a primary constructor, so that order wins over the reflection order
        val df = dataFrameOf("name", "age")("Alice", 15).cast<PersonRecord>()
        df.compileTimeSchema(ordered = false).columns.keys.toList() shouldBe listOf("name", "age")
    }

    @Test
    fun `compile-time schema of a dataframe without a schema marker has no columns`() {
        val untyped = dataFrameOf("a")(1)
        untyped.compileTimeSchema().columns.keys shouldBe emptySet()
    }
}

private interface Schema {
    val a: Int
    val abc: Int
    val a123: Int
    val nested: DataRow<Nested>
}

private interface Nested {
    val b: Int
    val c: Int
}

// the schema marker of the `compileTimeSchema` KDoc example, annotated exactly as it is there
@DataSchema
private interface Person {
    val name: String
    val age: Int
}

// same properties, but as a marker that has a primary constructor to take the column order from
@DataSchema
private data class PersonRecord(val name: String, val age: Int)

// `SchemaKDocExampleTests` cannot host the `compileTimeSchema` example: it extends `TestBase`,
// which brings its own sample `Person` schema into scope and shadows the marker declared here.
class SchemaKDocExampleTests : TestBase() {

    @Test
    fun `KDoc example -- schema of a row`() {
        // the row gives the columns of df, not the single row it stands for
        df.first().schema().columns.keys.toList() shouldBe
            listOf("name", "age", "city", "weight", "isHappy")
    }

    @Test
    fun `KDoc example -- schema of a dataframe`() {
        df.schema().toString() shouldBe
            """
            |name:
            |    firstName: String
            |    lastName: String
            |age: Int
            |city: String?
            |weight: Int?
            |isHappy: Boolean
            """.trimMargin()
    }

    @Test
    fun `KDoc example -- schema of a groupBy`() {
        // the frame column holding the groups carries the default name `group`
        df.groupBy { city }.schema().toString() shouldBe
            """
            |city: String?
            |group: *
            |    name:
            |        firstName: String
            |        lastName: String
            |    age: Int
            |    city: String?
            |    weight: Int?
            |    isHappy: Boolean
            |
            """.trimMargin()
    }
}
