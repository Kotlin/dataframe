package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConverterNotFoundException
import org.junit.Test
import kotlin.reflect.typeOf

class ConvertToTests {

    @Test
    fun `convert frame column with empty frames`() {
        val groups by columnOf(dataFrameOf("a")("1"), DataFrame.empty())
        val df = dataFrameOf(groups)

        @DataSchema
        data class GroupSchema(val a: Int)

        @DataSchema
        data class DataFrameSchema(val groups: DataFrame<GroupSchema>)

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

        df.convertTo<Schema> {
            parser { A(it.toInt()) }
        }
            .single().a.value shouldBe 1
    }

    @Test
    fun `convert with converter`() {
        val df = dataFrameOf("a")(1)

        shouldThrow<TypeConverterNotFoundException> {
            df.convertTo<Schema>()
        }

        df.convertTo<Schema> {
            convert<Int>().with { A(it) }
        }.single().a.value shouldBe 1
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
    data class Location(
        val name: String,
        val gps: Gps?,
    )

    @DataSchema
    data class Gps(
        val latitude: Double,
        val longitude: Double,
    )

    @Test
    fun `convert df with nullable DataRow to itself`() {
        val locations: DataFrame<Location> = listOf(
            Location("Home", Gps(0.0, 0.0)),
            Location("Away", null),
        ).toDataFrame()

        val converted = locations.convertTo<Location>()

        converted shouldBe locations
    }
}
