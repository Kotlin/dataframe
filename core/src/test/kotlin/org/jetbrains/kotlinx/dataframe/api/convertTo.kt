package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.jetbrains.kotlinx.dataframe.kind
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class ConvertToTests {

    @DataSchema
    data class GroupSchema(val a: Int)

    @DataSchema
    data class DataFrameSchema(val groups: DataFrame<GroupSchema>)

    @Test
    fun `convert frame column with empty frames`() {
        val groups by columnOf(dataFrameOf("a")("1"), DataFrame.empty())
        val df = dataFrameOf(groups)

        val converted = df.convertTo<DataFrameSchema>()

        converted[groups].forEach {
            it["a"].type() shouldBe typeOf<Int>()
        }
    }

    data class A(val value: Int)

    @DataSchema
    data class Schema(val a: A)

    @Test
    fun `convert with parser`() {
        val df = dataFrameOf("a")("1")

        shouldThrow<TypeConverterNotFoundException> {
            df.convertTo<Schema>()
        }

        df.convertTo<Schema> { parser { A(it.toInt()) } }
            .single()
            .a.value shouldBe 1

        // shortcut for:
        df.convertTo<Schema> { convert<String>().with { A(it.toInt()) } }
            .single()
            .a.value shouldBe 1
    }

    @Test
    fun `convert from char with parser`() {
        val df = dataFrameOf("a")('1')

        shouldThrow<TypeConverterNotFoundException> {
            df.convertTo<Schema>()
        }

        // Char -> String -> Target
        df.convertTo<Schema> { parser { A(it.toInt()) } }
            .single()
            .a.value shouldBe 1

        // shortcut for:
        df.convertTo<Schema> { convert<String>().with { A(it.toInt()) } }
            .single()
            .a.value shouldBe 1

        // Char -> Target
        df.convertTo<Schema> {
            parser<A> { error("should not be triggered if convert<Char>() is present") }
            convert<String>().with<_, A> { error("should not be triggered if convert<Char>() is present") }

            convert<Char>().with { A(it.digitToInt()) }
        }.single().a.value shouldBe 1
    }

    @Test
    fun `convert with converter`() {
        val df = dataFrameOf("a")(1)

        shouldThrow<TypeConverterNotFoundException> {
            df.convertTo<Schema>()
        }

        df.convertTo<Schema> { convert<Int>().with { A(it) } }
            .single()
            .a.value shouldBe 1
    }

    @Test
    fun `convert nulls to not nulls with converter`() {
        val df = dataFrameOf("a")("1", null)

        val converted = df.convertTo<Schema> {
            convert<String?>().with { it?.let { A(it.toInt()) } ?: A(0) }
        }
        val expected = dataFrameOf("a")(A(1), A(0))

        converted shouldBe expected
    }

    @JvmInline
    value class IntClass(val value: Int)

    @DataSchema
    interface IntSchema {
        val a: IntClass?
    }

    @Test
    fun `convert value class with converter`() {
        dataFrameOf("a")("1%")
            .convertTo<IntSchema> {
                parser { IntClass(it.dropLast(1).toInt()) }
            } shouldBe dataFrameOf("a")(IntClass(1))
    }

    @Test
    fun `convert nulls with converter`() {
        dataFrameOf("a")("1%", null)
            .convertTo<IntSchema> {
                parser { IntClass(it.dropLast(1).toInt()) }
            } shouldBe dataFrameOf("a")(IntClass(1), null)
    }

    @Test
    fun `convert with nullable converter argument`() {
        val df = dataFrameOf("a")("1")

        val converted = df.convertTo<IntSchema> {
            convert<String?>().with {
                it?.let { IntClass(it.toInt()) }
            }
        }
        val expected = dataFrameOf("a")(IntClass(1))

        converted shouldBe expected
    }

    @DataSchema
    data class Location(val name: String, val gps: Gps?)

    @DataSchema
    data class Gps(val latitude: Double, val longitude: Double)

    // @Test TODO: https://github.com/Kotlin/dataframe/issues/177
    fun `convert df with nullable DataRow`() {
        val locations: AnyFrame = dataFrameOf("name", "gps")(
            "Home", Gps(0.0, 0.0),
            "Away", null,
        )

        locations.print(borders = true, title = true, columnTypes = true)
        locations.schema().print()

        val converted = locations.convertTo<Location>()

        converted shouldBe locations
    }

    @Test
    fun `convert df with nullable DataRow to itself`() {
        val locations: DataFrame<Location> = listOf(
            Location("Home", Gps(0.0, 0.0)),
            Location("Away", null),
        ).toDataFrame()

        val converted = locations.convertTo<Location>()

        converted shouldBe locations
    }

    @DataSchema
    data class DataSchemaWithAnyFrame(val dfs: AnyFrame?)

    private fun locationsFrame(): DataFrame<Location?> =
        listOf(
            Location("Home", Gps(0.0, 0.0)),
            Location("Away", null),
            null,
        ).toDataFrame()
            .alsoDebug("locations:")

    private fun gpsFrame(): DataFrame<Gps?> =
        listOf(
            Gps(0.0, 0.0),
            null,
        ).toDataFrame()
            .alsoDebug("gps:")

    @Test
    fun test() {
        val df1 = dataFrameOf("a")(1, 2, 3)
        val df2 = dataFrameOf("b")(4, 5)
        val frameColumn by columnOf(df1, df2, null)
        val df = dataFrameOf(frameColumn).alsoDebug()
//        ⌌---------------⌍
//        |  | untitled:[]|
//        |--|------------|
//        | 0|     [3 x 1]|
//        | 1|     [2 x 1]|
//        | 2|     [0 x 0]|
//        ⌎---------------⌏
//
//        untitled: *
    }

    @Test
    fun `convert df with AnyFrame containing locations to itself`() {
        val locations = locationsFrame()

        listOf(DataSchemaWithAnyFrame(locations))
            .toDataFrame()
            .alsoDebug("df1:")
            .convertTo<DataSchemaWithAnyFrame>()
    }

    @Test
    fun `convert df with AnyFrame containing gps to itself`() {
        val gps = gpsFrame()

        listOf(DataSchemaWithAnyFrame(gps))
            .toDataFrame()
            .alsoDebug("df2:")
            .convertTo<DataSchemaWithAnyFrame>()
    }

    @Test
    fun `convert df with preserved AnyFrame containing null and gps to itself`() {
        val gps = gpsFrame()

        listOf(
            DataSchemaWithAnyFrame(null),
            DataSchemaWithAnyFrame(gps),
        ).toDataFrame { properties { preserve(DataFrame::class) } }
            .alsoDebug("df3 before convert:")
            .convertTo<DataSchemaWithAnyFrame>()
    }

    @Test
    fun `convert df with preserved null AnyFrame to itself`() {
        listOf(
            DataSchemaWithAnyFrame(null),
        ).toDataFrame { properties { preserve(DataFrame::class) } }
            .alsoDebug("df4 before convert:")
            .convertTo<DataSchemaWithAnyFrame>()
    }

    @Test
    fun `convert raw df with AnyFrame column to itself`() {
        val locations = locationsFrame()
        val gps = gpsFrame()

        val df: DataFrame<*> = dataFrameOf(
            columnOf(locations, gps, null).named("dfs"),
        ).alsoDebug("df5a:")

        df.convertTo<DataSchemaWithAnyFrame>()
    }

    @Test
    fun `convert df with preserved mixed AnyFrame values to itself repeatedly`() {
        val locations = locationsFrame()
        val gps = gpsFrame()

        listOf(
            DataSchemaWithAnyFrame(null),
            DataSchemaWithAnyFrame(locations),
            DataSchemaWithAnyFrame(gps),
        ).toDataFrame { properties { preserve(DataFrame::class) } }
            .alsoDebug("df5 before convert:")
            .convertTo<DataSchemaWithAnyFrame>()
            .alsoDebug("df5 after convert:")
            .convertTo<DataSchemaWithAnyFrame>()
            .alsoDebug("df5 after second convert:")
    }

    interface KeyValue<T> {
        val key: String
        val value: T
    }

    @DataSchema
    interface MySchema : KeyValue<Int>

    @Test
    fun `Convert generic interface to itself`() {
        val df = dataFrameOf("key", "value")(
            "a", 1,
            "b", 2,
        ).alsoDebug()
        val converted = df.convertTo<MySchema>().alsoDebug()
        converted shouldBe df
    }

    @DataSchema
    data class NullableColumnResult(val a: Int, val b: Int?)

    @Test
    fun `convert with missing nullable column`() {
        val df = dataFrameOf("a")(1, 2)
        val converted = df.convertTo<NullableColumnResult>()
        converted shouldBe listOf(NullableColumnResult(1, null), NullableColumnResult(2, null)).toDataFrame()
    }

    @Test
    fun `convert with custom fill of missing columns`() {
        val locations = listOf(
            Location("Home", Gps(1.0, 1.0)),
            Location("Away", null),
        ).toDataFrame().cast<Location>()

        val converted = locations
            .remove { gps.longitude }
            .cast<Unit>()
            .convertTo<Location> {
                fill { gps.longitude }.with { gps.latitude }
            }

        converted shouldBe locations.update { gps.longitude }.with { gps.latitude }
    }

    @DataSchema
    data class Entry1(val v: Int)

    @DataSchema
    data class FrameColumnResult1(val d: DataFrame<Entry1>)

    @Test
    fun `convert column of empty lists into FrameColumn`() {
        dataFrameOf("d")(emptyList<Any>(), emptyList<Any>())
            .convertTo<FrameColumnResult1>() shouldBe
            dataFrameOf("d")(DataFrame.emptyOf<Entry1>(), DataFrame.emptyOf<Entry1>())
    }

    @DataSchema
    data class Entry2(val v: Int)

    @DataSchema
    data class FrameColumnResult2(val d: DataFrame<Entry2>)

    @Test
    fun `convert ColumnGroup into FrameColumn`() {
        val columnGroup = DataColumn.createColumnGroup("d", dataFrameOf("v")(1, 2))
        columnGroup.kind() shouldBe ColumnKind.Group
        val res = dataFrameOf(columnGroup).convertTo<FrameColumnResult2>()
        val frameColumn = res.getFrameColumn("d")
        frameColumn.kind shouldBe ColumnKind.Frame
        frameColumn.values() shouldBe listOf(dataFrameOf("v")(1), dataFrameOf("v")(2))
    }

    @DataSchema
    data class Entry3(val v: Int)

    @DataSchema
    data class FrameColumnResult3(val d: DataFrame<Entry3>)

    @Test
    fun `convert ValueColumn of lists, nulls and frames into FrameColumn`() {
        val emptyList: List<Any?> = emptyList()
        val listOfRows: List<AnyRow> = dataFrameOf("v")(1, 2).rows().toList()
        val frame: DataFrame<Entry3> = listOf(Entry3(3), Entry3(4)).toDataFrame()

        val src = DataColumn.createValueColumn("d", listOf(emptyList, listOfRows, frame, null)).toDataFrame()
        src["d"].kind shouldBe ColumnKind.Value

        val df = src.convertTo<FrameColumnResult3>()
        val frameColumn = df.getFrameColumn("d")
        frameColumn.kind shouldBe ColumnKind.Frame
        frameColumn.toList() shouldBe listOf(
            DataFrame.emptyOf<Entry3>(),
            dataFrameOf("v")(1, 2),
            dataFrameOf("v")(3, 4),
            DataFrame.emptyOf<Entry3>(),
        )
    }

    enum class SimpleEnum { A, B }

    @DataSchema
    interface SchemaWithNullableEnum {
        val a: SimpleEnum?
    }

    @Test
    fun `convert Char to Enum`() {
        val df = dataFrameOf("a")('A', 'B', null)

        val converted = df.convertTo<SchemaWithNullableEnum>()
        converted["a"].type() shouldBe typeOf<SimpleEnum?>()
        converted shouldBe dataFrameOf("a")(SimpleEnum.A, SimpleEnum.B, null)
    }

    @Test
    fun `convert Char to Enum custom charParser`() {
        val df = dataFrameOf("a")('a', 'b', null)

        val converted = df.convertTo<SchemaWithNullableEnum> {
            parser { SimpleEnum.valueOf(it.uppercase()) }
        }
        converted["a"].type() shouldBe typeOf<SimpleEnum?>()
        converted shouldBe dataFrameOf("a")(SimpleEnum.A, SimpleEnum.B, null)
    }
}
