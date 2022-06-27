package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Test
import kotlin.reflect.*

class JsonTests {

    @Test
    fun parseJson1() {
        val json = """[
                {"a":1, "b":"text"},
                {"a":2, "b":5, "c":4.5}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df.columnsCount() shouldBe 3
        df.rowsCount() shouldBe 2
        df["a"].type() shouldBe typeOf<Int>()
        df["b"].type() shouldBe typeOf<Comparable<*>>()
        df["c"].type() shouldBe typeOf<Double?>()
    }

    @Test
    fun parseJson2() {
        val json = """[
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6,7,8]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        println(df)
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        val group = df["a"] as ColumnGroup<*>
        group.columnsCount() shouldBe 3
        group["b"].type() shouldBe typeOf<Int?>()
        group["value"].type() shouldBe typeOf<String?>()
        group["array"].type() shouldBe typeOf<List<Int>>()
    }

    @Test
    fun parseJson3() {
        val json = """[
                {"a":[3, 5]},
                {},
                {"a":[3.4, 5.6]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        df["a"].type() shouldBe typeOf<List<Number>>()
        df[1]["a"] shouldBe emptyList<Int>()
    }

    @Test
    fun parseJson4() {
        val json = """[
                {"a":[ {"b":2}, {"c":3} ]},
                {"a":[ {"b":4}, {"d":5} ]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 2
        println(df)
        df
        val group = df["a"] as FrameColumn<*>
    }

    @Test
    fun `write df with primitive types`() {
        val df = dataFrameOf("colInt", "colDouble?", "colBoolean?")(
            1, 1.0, true,
            2, null, false,
            3, 3.0, null
        )

        val res = DataFrame.readJsonStr(df.toJson())
        res shouldBe df
    }

    @Test
    fun `NaN double serialization`() {
        val df = dataFrameOf("v")(1.1, Double.NaN)
        df["v"].type() shouldBe typeOf<Double>()
        DataFrame.readJsonStr(df.toJson()) shouldBe df
    }

    @Test
    fun `NaN float serialization`() {
        val df = dataFrameOf("v")(1.1f, Float.NaN)
        df["v"].type() shouldBe typeOf<Float>()
        DataFrame.readJsonStr(df.toJson()) shouldBe df.convert("v").toDouble()
    }

    @Test
    fun `NaN string serialization`() {
        val df = dataFrameOf("v")("NaM", "NaN")
        df["v"].type() shouldBe typeOf<String>()
        DataFrame.readJsonStr(df.toJson()) shouldBe df
    }
}
