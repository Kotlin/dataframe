package org.jetbrains.kotlinx.dataframe.plugin.stringApi

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.compileTimeSchema
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.first

abstract class CommonTestData {
    val df: DataFrame<Df> = dataFrameOf("key", "value", "other", "nullable", "nan")(
        "a",
        1,
        10,
        1,
        Double.NaN,
        "a",
        2,
        20,
        null,
        2.0,
        "b",
        3,
        30,
        3,
        Double.NaN,
    ).cast<Df>()
    val dfRaw: DataFrame<Any> = df.cast<Any>()

    @DataSchema
    class Df(
        val key: String,
        val value: Int,
        val other: Int,
        val nullable: Int?,
        val nan: Double,
    )

    val other: DataFrame<Other> = dataFrameOf("key", "label")("a", "first", "c", "third").cast<Other>()
    val otherRaw: DataFrame<Any> = other.cast<Any>()

    @DataSchema
    interface Other {
        val key: String
        val label: String
    }

    val nested: DataFrame<Nested> = dataFrameOf("group")(dataFrameOf("value")(1, 2).first()).cast<Nested>()
    val nestedRaw: DataFrame<Any> = nested.cast<Any>()

    @DataSchema
    data class Nested(val group: Group) {
        @DataSchema
        data class Group(val value: Int)
    }

    val lists: DataFrame<Lists> = dataFrameOf("values")(listOf(1, 2), emptyList<Int>()).cast<Lists>()
    val listsRaw: DataFrame<Any> = lists.cast<Any>()

    @DataSchema
    class Lists(val values: List<Int>)

    val records: DataFrame<RecordsFrame> = dataFrameOf("ab" to columnOf(Record("foo", 42))).cast<RecordsFrame>()
    val recordsRaw: DataFrame<Any> = records.cast<Any>()

    @DataSchema
    data class RecordsFrame(val ab: Record)

    class Record(val a: String, val b: Int)

    inline infix fun <reified T, reified T1> DataFrame<T>.matches(other: DataFrame<T1>) {
        compileTimeSchema() shouldBe other.compileTimeSchema()
    }

    inline infix fun <reified T, reified T1> DataRow<T>.matches(other: DataRow<T1>) {
        df().compileTimeSchema() shouldBe other.df().compileTimeSchema()
    }
}
