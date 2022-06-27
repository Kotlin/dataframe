package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
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
    fun `parse json with nested json array with mixed values`() {
        val json = """[
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6, {"a": "b"}, [1, {"a" : "b"}],8]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        val group = df["a"] as ColumnGroup<*>
        group.columnsCount() shouldBe 3
        group["b"].type() shouldBe typeOf<Int?>()
        group["value"].type() shouldBe typeOf<String?>()
        group["array"].type() shouldBe typeOf<DataFrame<*>>()
        val nestedDf = group.getFrameColumn("array")[2]
        nestedDf["a"].type() shouldBe typeOf<String?>()
        nestedDf["value"].type() shouldBe typeOf<Int?>()
        nestedDf["array"].type() shouldBe typeOf<DataFrame<*>>()
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

    @Test
    fun `list serialization`() {
        val df = dataFrameOf("a")(listOf(1, 2, 3))
        DataFrame.readJsonStr(df.toJson()) shouldBe df
    }

    @Test
    fun `literal json field named 'value'`() {
        val json = """
                {
                    "data": {
                        "source": {
                            "value": "123"
                        }
                    }
                }
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
        df[0].getColumnGroup("data").getColumnGroup("source")["value"] shouldBe "123"
    }

    @Test
    fun `array json field named 'value'`() {
        val json = """{ "value": ["123"] }"""

        val df = DataFrame.readJsonStr(json)
        df[0]["value"] shouldBe listOf("123")
    }

    @Test
    fun `record json field named 'value'`() {
        val json = """{ "value": { "test" : "123" } }"""

        val df = DataFrame.readJsonStr(json)
        df[0].getColumnGroup("value")["test"] shouldBe "123"
    }

    @Test
    fun `json field named 'array'`() {
        val json = """
            {
                "data": {
                    "source": {
                        "array": "123"
                    }
                }
            }
        """.trimIndent()

        val df = DataFrame.readJsonStr(json)
        df[0].getColumnGroup("data").getColumnGroup("source")["array"] shouldBe "123"
    }

    @Test
    fun `array json field named 'array'`() {
        val json = """
            [{
              "a": {
                "value": "text",
                "array": []
              }
            }, {
              "a": {
                "b": 2,
                "array": []
              }
            }, {
              "a": {
                "array": [6, 7, 8]
              }
            }]
        """.trimIndent()

        val df = DataFrame.readJsonStr(json)
        val group = df.getColumnGroup("a")
        group["array"].type() shouldBe typeOf<List<Int>>()
        group["value"].type() shouldBe typeOf<String?>()
        group["b"].type() shouldBe typeOf<Int?>()
    }
}
