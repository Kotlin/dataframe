package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.junit.Test
import kotlin.reflect.typeOf

class ConcatTests {

    private data class EmptySchema(val a: Int, val b: String)

    private fun concatWithEverySchemaUnifyingOverload(first: DataFrame<*>, second: DataFrame<*>): List<DataFrame<*>> =
        listOf(
            DataFrame.empty().concat(first, second),
            first concat second,
            first.concat(listOf(second)),
            first.concat(listOf(second[0])),
            columnOf(first, second).concat(),
            first[0].concat(second[0]),
            listOf(first, second).concat(),
        )

    // region DataColumn

    @Test
    fun `data column concat appends values from another column`() {
        val col1 = columnOf(1, 2).named("first")
        val col2 = columnOf(3, 4).named("second")

        col1.concat(col2) shouldBe columnOf(1, 2, 3, 4).named("first")
    }

    @Test
    fun `data column concat appends values from multiple columns`() {
        val col1 = columnOf(1, 2).named("first")
        val col2 = columnOf(3).named("second")
        val col3 = columnOf(4, 5).named("third")

        col1.concat(col2, col3) shouldBe columnOf(1, 2, 3, 4, 5).named("first")
    }

    @Test
    fun `data column concat finds a common nullable type`() {
        val integers = columnOf(1, 2).named("numbers")
        val doubles = columnOf(3.0, null).named("doubles")

        val result = integers.concat(doubles)

        result shouldBe columnOf<Number?>(1, 2, 3.0, null).named("numbers")
    }

    @Test
    fun `data column concat derives nullability from resulting values`() {
        val first = DataColumn.createValueColumn<Int?>("first", listOf(1, 2))
        val second = DataColumn.createValueColumn<Int?>("second", listOf(3, 4))

        first.type() shouldBe typeOf<Int?>()
        second.type() shouldBe typeOf<Int?>()
        first.concat(second).type() shouldBe typeOf<Int>()
    }

    @Test
    fun `data column concat preserves a shared runtime type except nullability`() {
        val first = DataColumn.createValueColumn<Number?>("first", listOf(1, 2))
        val second = DataColumn.createValueColumn<Number?>("second", listOf(3, 4))

        first.type() shouldBe typeOf<Number?>()
        second.type() shouldBe typeOf<Number?>()
        first.concat(second).type() shouldBe typeOf<Number>()
    }

    @Test
    fun `data column concat with one input preserves its runtime type`() {
        val column = DataColumn.createValueColumn<Number?>("numbers", listOf(1, 2))

        column.concat().let { result ->
            result shouldBeSameInstanceAs column
            result.type() shouldBe typeOf<Number?>()
        }
        listOf(column).concat().let { result ->
            result shouldBeSameInstanceAs column
            result.type() shouldBe typeOf<Number?>()
        }
    }

    @Test
    fun `data column concat uses runtime types when all inputs are empty`() {
        DataColumn.emptyOf<Int>("ints").concat(DataColumn.emptyOf<String>("strings")).type() shouldBe
            typeOf<Comparable<*>>()
        DataColumn.emptyOf<Int?>("first").concat(DataColumn.emptyOf<Int?>("second")).type() shouldBe
            typeOf<Int>()
    }

    @Test
    fun `data column concat with an empty column preserves values and type`() {
        val values = columnOf(1, 2).named("values")
        val empty = DataColumn.empty("empty")

        values.concat(empty) shouldBe values
        empty.concat(values) shouldBe columnOf(1, 2).named("empty")
    }

    @Test
    fun `frame column concat concatenates all stored dataframes`() {
        val df1 = dataFrameOf(
            "a" to columnOf(1, 2),
            "b" to columnOf("x", "y"),
        )
        val df2 = dataFrameOf(
            "b" to columnOf("z"),
            "c" to columnOf(true),
        )
        val frames = columnOf(df1, df2).named("frames")

        val result = frames.concat()

        result shouldBe dataFrameOf(
            "a" to columnOf<Int?>(1, 2, null),
            "b" to columnOf("x", "y", "z"),
            "c" to columnOf<Boolean?>(null, null, true),
        )
    }

    @Test
    fun `empty frame column concat returns an empty dataframe`() {
        emptyList<DataFrame<Any>>().toFrameColumn("frames").concat() shouldBe DataFrame.empty()
    }

    @Test
    fun `collection column concat flattens collection values`() {
        val collections = columnOf<Collection<Int>>(listOf(1, 2), emptySet(), setOf(3, 4))
        val result = collections.concat()

        result shouldBe listOf(1, 2, 3, 4)
    }

    // endregion

    // region DataRow

    @Test
    fun `data row concat correctly creates a dataframe from rows with the same schema and preserves order`() {
        val row1 = dataFrameOf("id", "value")(1, "a")[0]
        val row2 = dataFrameOf("id", "value")(2, "b")[0]
        val row3 = dataFrameOf("id", "value")(3, "c")[0]

        row1.concat(row2, row3) shouldBe dataFrameOf("id", "value")(1, "a", 2, "b", 3, "c")
    }

    @Test
    fun `data row concat unifies rows with different schemas`() {
        val row1 = dataFrameOf("a")(1)[0]
        val row2 = dataFrameOf("b")("x")[0]
        val row3 = dataFrameOf("a", "b")(2, "y")[0]

        val result = row1.concat(row2, row3)

        result shouldBe dataFrameOf(
            "a" to columnOf<Int?>(1, null, 2),
            "b" to columnOf<String?>(null, "x", "y"),
        )
    }

    @Test
    fun `data row concat infers a common runtime type`() {
        val integer = dataFrameOf("value")(1)[0]
        val double = dataFrameOf("value")(2.5)[0]

        val result = integer.concat(double)

        result shouldBe dataFrameOf("value" to columnOf<Number>(1, 2.5))
        result["value"].type() shouldBe typeOf<Number>()
    }

    // endregion

    // region DataFrame

    @Test
    fun `dataframe vararg concat appends all frames in order`() {
        val df1 = dataFrameOf("id", "value")(1, "a", 2, "b")
        val df2 = dataFrameOf("id", "value")(3, "c")
        val df3 = dataFrameOf("id", "value")(4, "d", 5, "e")

        df1.concat(df2, df3) shouldBe
            dataFrameOf("id", "value")(1, "a", 2, "b", 3, "c", 4, "d", 5, "e")
    }

    @Test
    fun `infix dataframe concat appends rows from another dataframe`() {
        val df1 = dataFrameOf("id", "value")(1, "a", 2, "b")
        val df2 = dataFrameOf("id", "value")(3, "c")

        (df1 concat df2) shouldBe dataFrameOf("id", "value")(1, "a", 2, "b", 3, "c")
    }

    @Test
    fun `infix dataframe concat unifies schemas and fills missing columns with null`() {
        val df1 = dataFrameOf(
            "id" to columnOf(1, 2),
            "left" to columnOf("a", "b"),
        )
        val df2 = dataFrameOf(
            "id" to columnOf(3),
            "right" to columnOf(true),
        )

        (df1 concat df2) shouldBe dataFrameOf(
            "id" to columnOf(1, 2, 3),
            "left" to columnOf<String?>("a", "b", null),
            "right" to columnOf<Boolean?>(null, null, true),
        )
    }

    @Test
    fun `infix dataframe concat finds lowest common type for columns with the same name`() {
        val df1 = dataFrameOf("value" to columnOf(1, 2))
        val df2 = dataFrameOf("value" to columnOf(3.5))

        (df1 concat df2) shouldBe dataFrameOf("value" to columnOf<Number>(1, 2, 3.5))
    }

    @Test
    fun `dataframe concat rows appends iterable rows`() {
        val df1 = dataFrameOf("id", "value")(1, "a")
        val df2 = dataFrameOf("id", "value")(2, "b", 3, "c")

        df1.concat(df2.rows()) shouldBe dataFrameOf("id", "value")(1, "a", 2, "b", 3, "c")
    }

    @Test
    fun `dataframe concat frames appends iterable frames`() {
        val df1 = dataFrameOf("id")(1)
        val frames = listOf(dataFrameOf("id")(2), dataFrameOf("id")(3))

        df1.concat(frames) shouldBe dataFrameOf("id")(1, 2, 3)
    }

    @Test
    fun `dataframe concat without appended frames returns the receiver instance`() {
        val df = DataFrame.emptyOf<EmptySchema>()

        df.concat() shouldBeSameInstanceAs df
        df.concat(emptyList<DataFrame<EmptySchema>>()) shouldBeSameInstanceAs df
    }

    @Test
    fun `dataframe concat with an empty dataframe preserves rows and schema`() {
        val df = dataFrameOf("id", "value")(1, "a", 2, "b")
        val empty = DataFrame.empty()

        empty.concat(df).let { result ->
            result shouldBe df
            result.schema() shouldBe df.schema()
        }
        df.concat(empty).let { result ->
            result shouldBe df
            result.schema() shouldBe df.schema()
        }
    }

    @Test
    fun `zero-row appended dataframe contributes its columns`() {
        val df = dataFrameOf("a")(1)
        val empty = dataFrameOf("z" to DataColumn.emptyOf<String>())
        val result = df concat empty

        result shouldBe dataFrameOf(
            "a" to columnOf(1),
            "z" to DataColumn.createValueColumn<String?>("z", listOf(null)),
        )
        result["z"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `missing list column is filled with empty lists`() {
        val withList = dataFrameOf(
            "name" to columnOf("Alice"),
            "values" to columnOf(listOf(1, 2, 3)),
        )
        val withoutList = dataFrameOf("name" to columnOf("Charlie"))
        val expected = dataFrameOf(
            "name" to columnOf("Alice", "Charlie"),
            "values" to columnOf(listOf(1, 2, 3), emptyList()),
        )

        concatWithEverySchemaUnifyingOverload(withList, withoutList).forEach { result ->
            result shouldBe expected
            result["values"].type() shouldBe typeOf<List<Int>>()
        }
    }

    @Test
    fun `missing frame column is filled with empty dataframes`() {
        val firstFrame = dataFrameOf("score")(100)
        val withFrames = dataFrameOf(
            "name" to columnOf("Alice"),
            "details" to columnOf(firstFrame),
        )
        val withoutFrames = dataFrameOf("name" to columnOf("Charlie"))
        val expected = dataFrameOf(
            "name" to columnOf("Alice", "Charlie"),
            "details" to columnOf(firstFrame, DataFrame.empty()),
        )

        concatWithEverySchemaUnifyingOverload(withFrames, withoutFrames).forEach { result ->
            result shouldBe expected
            result["details"].type() shouldBe typeOf<DataFrame<*>>()
        }
    }

    @Test
    fun `missing column group keeps the group and fills nested columns with null`() {
        val withGrades = dataFrameOf(
            "name" to columnOf("Alice"),
            "grades" to dataFrameOf(
                "math" to columnOf(100.0),
                "english" to columnOf(90.0),
            ).asColumnGroup(),
        )
        val withoutGrades = dataFrameOf("name" to columnOf("Charlie"))
        val expected = dataFrameOf(
            "name" to columnOf("Alice", "Charlie"),
            "grades" to dataFrameOf(
                "math" to columnOf<Double?>(100.0, null),
                "english" to columnOf<Double?>(90.0, null),
            ).asColumnGroup(),
        )

        concatWithEverySchemaUnifyingOverload(withGrades, withoutGrades).forEach { result ->
            result shouldBe expected
            result.getColumnGroup("grades")["math"].type() shouldBe typeOf<Double?>()
            result.getColumnGroup("grades")["english"].type() shouldBe typeOf<Double?>()
        }
    }

    @Test
    fun `concat empty dataframes with columns preserves schema`() {
        val typed = DataFrame.emptyOf<EmptySchema>()
        (typed concat typed).let { result ->
            result shouldBe typed
            result.schema() shouldBe typed.schema()
        }

        val inferred = dataFrameOf(
            "a" to DataColumn.empty(),
            "b" to DataColumn.empty(),
        )
        (inferred concat inferred).let { result ->
            result shouldBe inferred
            result.schema() shouldBe inferred.schema()
        }
    }

    @Test
    fun `concat dataframes without columns preserves total row count`() {
        val emptyDf1 = DataFrame.empty(2)
        val emptyDf2 = DataFrame.empty(3)
        val result = emptyDf1 concat emptyDf2

        result shouldBe DataFrame.empty(5)
    }

    // endregion

    // region GroupBy

    @Test
    fun `groupBy concat preserves group order without adding group keys`() {
        val df = dataFrameOf(
            "value" to listOf(1, 2, 3, 4),
            "type" to listOf("a", "b", "a", "b"),
        )
        val grouped = df.groupBy {
            expr { "Category: ${"type"<String>().uppercase()}" } named "category"
        }

        grouped.concat() shouldBe dataFrameOf(
            "value" to listOf(1, 3, 2, 4),
            "type" to listOf("a", "a", "b", "b"),
        )
    }

    @Test
    fun `groupBy concat preserves group columns with the same names as keys`() {
        val df = dataFrameOf(
            "value" to listOf(1, 2, 3, 4),
            "type" to listOf("a", "b", "a", "b"),
        )
        val grouped = df.groupBy("type").updateGroups { update("type").with { "changed" } }

        grouped.concat() shouldBe dataFrameOf(
            "value" to listOf(1, 3, 2, 4),
            "type" to listOf("changed", "changed", "changed", "changed"),
        )
    }

    @Test
    fun `groupBy concatWithKeys adds missing keys with values repeated for every group row`() {
        val df = dataFrameOf(
            "value" to listOf(1, 2, 3, 4),
            "type" to listOf("a", "b", "a", "b"),
        )
        val grouped = df.groupBy {
            expr { "Category: ${"type"<String>().uppercase()}" } named "category"
        }

        grouped.concatWithKeys() shouldBe dataFrameOf(
            "value" to listOf(1, 3, 2, 4),
            "type" to listOf("a", "a", "b", "b"),
            "category" to listOf("Category: A", "Category: A", "Category: B", "Category: B"),
        )
    }

    @Test
    fun `groupBy concatWithKeys does not overwrite a group column with the same name as a key`() {
        val df = dataFrameOf(
            "value" to listOf(1, 2, 3, 4),
            "type" to listOf("one", "two", "three", "four"),
        )
        val grouped = df.groupBy {
            expr { if ("value"<Int>() % 2 == 0) "even" else "odd" } named "type"
        }

        grouped.concatWithKeys() shouldBe dataFrameOf(
            "value" to listOf(1, 3, 2, 4),
            "type" to listOf("one", "three", "two", "four"),
        )
    }

    // endregion

    // region ReducedGroupBy

    @Test
    fun `reduced groupBy concat applies the reducer and preserves group order`() {
        val df = dataFrameOf(
            "value" to listOf(1, 2, 3, 4),
            "type" to listOf("a", "b", "a", "b"),
        )
        val reduced = df.groupBy("type").first()

        reduced.concat() shouldBe dataFrameOf(
            "value" to listOf(1, 2),
            "type" to listOf("a", "b"),
        )
    }

    @Test
    fun `reduced groupBy concat represents a null reducer result with one empty row`() {
        val df = dataFrameOf(
            "value" to listOf(1, 2, 3, 4),
            "type" to listOf("a", "b", "a", "b"),
        )
        val reduced = df.groupBy("type").first { "value"<Int>() == 3 }

        reduced.concat() shouldBe dataFrameOf(
            "value" to columnOf<Int?>(3, null),
            "type" to columnOf<String?>("a", null),
        )
    }

    @Test
    fun `reduced groupBy concat unifies different reduced row schemas`() {
        val df = dataFrameOf(
            "key" to columnOf("a", "b"),
            "value" to columnOf(1, 2),
        )
        val reduced = df.groupBy("key").updateGroups {
            if (it["key"][0] == "b") it.add("extra") { "y" } else it
        }.first()

        reduced.concat() shouldBe dataFrameOf(
            "key" to columnOf("a", "b"),
            "value" to columnOf(1, 2),
            "extra" to columnOf<String?>(null, "y"),
        )
    }

    // endregion

    // region Iterable

    @Test
    fun `iterable dataframe concat preserves input order`() {
        val frames = listOf(
            dataFrameOf("id")(1, 2),
            dataFrameOf("id")(3),
            dataFrameOf("id")(4, 5),
        )

        frames.concat() shouldBe dataFrameOf("id")(1, 2, 3, 4, 5)
    }

    @Test
    fun `empty iterable dataframe concat returns an empty dataframe`() {
        emptyList<DataFrame<Any>>().concat() shouldBe DataFrame.empty()
    }

    @Test
    fun `iterable data column concat preserves input order and first name`() {
        val columns = listOf(
            columnOf(1, 2).named("first"),
            columnOf(3).named("second"),
            columnOf(4, 5).named("third"),
        )

        columns.concat() shouldBe columnOf(1, 2, 3, 4, 5).named("first")
    }

    @Test
    fun `empty iterable data column concat returns an empty column`() {
        emptyList<DataColumn<Int>>().concat() shouldBe DataColumn.empty()
    }

    @Test
    fun `iterable nullable data row concat represents a null element as a row with null values`() {
        val row1 = dataFrameOf("id", "value")(1, "a")[0]
        val row2 = dataFrameOf("id", "value")(2, "b")[0]
        val rows = listOf(row1, null, row2)

        rows.concat() shouldBe dataFrameOf(
            "id" to columnOf<Int?>(1, null, 2),
            "value" to columnOf<String?>("a", null, "b"),
        )
    }

    @Test
    fun `iterable containing only null rows preserves the number of elements`() {
        val rows = listOf<DataRow<Any>?>(null, null)

        rows.concat() shouldBe DataFrame.empty(2)
    }

    @Test
    fun `empty iterable data row concat returns an empty dataframe`() {
        emptyList<DataRow<Any>?>().concat() shouldBe DataFrame.empty()
    }

    // endregion
}
