package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test
import kotlin.reflect.*

class JsonTests {

    @Test
    fun parseJson1() {
        @Language("json")
        val json = """[
                {"a":1, "b":"text"},
                {"a":2, "b":5, "c":4.5}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
            .alsoDebug()
        df.columnsCount() shouldBe 3
        df.rowsCount() shouldBe 2
        df["a"].type() shouldBe typeOf<Int>()
        df["b"].type() shouldBe typeOf<Comparable<*>>()
        df["c"].type() shouldBe typeOf<Double?>()
    }

    @Test
    fun parseJson1Any() {
        @Language("json")
        val json = """[
                {"a":1, "b":"text"},
                {"a":2, "b":5, "c":4.5}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS)
            .alsoDebug()
        df.columnsCount() shouldBe 3
        df.rowsCount() shouldBe 2
        df["a"].type() shouldBe typeOf<Int>()
        df["b"].type() shouldBe typeOf<Comparable<*>>()
        df["c"].type() shouldBe typeOf<Double?>()
    }

    @Test
    fun parseJson2() {
        @Language("json")
        val json = """[
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6,7,8]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
            .alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        val group = df["a"] as ColumnGroup<*>
        group.columnsCount() shouldBe 3
        group["b"].type() shouldBe typeOf<Int?>()
        group["value"].type() shouldBe typeOf<String?>()
        group["array"].type() shouldBe typeOf<List<Int>>()
    }

    @Test
    fun parseJson2Any() {
        @Language("json")
        val json = """[
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6,7,8]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS)
            .alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        val a = df["a"] as ValueColumn<*>
        a.type() shouldBe typeOf<Any>()
        a[0] shouldBe "text"
        (a[1] as DataRow<*>)["b"] shouldBe 2
        a[2] shouldBe listOf(6, 7, 8)
    }

    @Test
    fun parseJson3() {
        @Language("json")
        val json = """[
                {"a":[3, 5]},
                {},
                {"a":[3.4, 5.6]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
            .alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        df["a"].type() shouldBe typeOf<List<Number>>()
        df[1]["a"] shouldBe emptyList<Int>()
    }

    @Test
    fun parseJson3Any() {
        @Language("json")
        val json = """[
                {"a":[3, 5]},
                {},
                {"a":[3.4, 5.6]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS)
            .alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        df["a"].type() shouldBe typeOf<List<Number>>()
        df[1]["a"] shouldBe emptyList<Int>()
    }

    @Test
    fun parseJson4() {
        @Language("json")
        val json = """[
                {"a":[ {"b":2}, {"c":3} ]},
                {"a":[ {"b":4}, {"d":5} ]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
            .alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 2
        val group = df["a"] as FrameColumn<*>
        group[0].alsoDebug()
            .let {
                it.columnsCount() shouldBe 3
                it.rowsCount() shouldBe 2
                it["b"].type() shouldBe typeOf<Int?>()
                it["c"].type() shouldBe typeOf<Int?>()
                it["d"].type() shouldBe typeOf<Int?>()
                it["b"].values.toList() shouldBe listOf(2, null)
                it["c"].values.toList() shouldBe listOf(null, 3)
                it["d"].values.toList() shouldBe listOf(null, null)
            }

        group[1].alsoDebug()
            .let {
                it.columnsCount() shouldBe 3
                it.rowsCount() shouldBe 2
                it["b"].type() shouldBe typeOf<Int?>()
                it["c"].type() shouldBe typeOf<Int?>()
                it["d"].type() shouldBe typeOf<Int?>()
                it["b"].values.toList() shouldBe listOf(4, null)
                it["c"].values.toList() shouldBe listOf(null, null)
                it["d"].values.toList() shouldBe listOf(null, 5)
            }
    }

    @Test
    fun parseJson4Any() {
        @Language("json")
        val json = """[
                {"a":[ {"b":2}, {"c":3} ]},
                {"a":[ {"b":4}, {"d":5} ]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS)
            .alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 2
        val group = df["a"] as FrameColumn<*>
        group[0].alsoDebug()
            .let {
                it.columnsCount() shouldBe 3
                it.rowsCount() shouldBe 2
                it["b"].type() shouldBe typeOf<Int?>()
                it["c"].type() shouldBe typeOf<Int?>()
                it["d"].type() shouldBe typeOf<Int?>()
                it["b"].values.toList() shouldBe listOf(2, null)
                it["c"].values.toList() shouldBe listOf(null, 3)
                it["d"].values.toList() shouldBe listOf(null, null)
            }

        group[1].alsoDebug()
            .let {
                it.columnsCount() shouldBe 3
                it.rowsCount() shouldBe 2
                it["b"].type() shouldBe typeOf<Int?>()
                it["c"].type() shouldBe typeOf<Int?>()
                it["d"].type() shouldBe typeOf<Int?>()
                it["b"].values.toList() shouldBe listOf(4, null)
                it["c"].values.toList() shouldBe listOf(null, null)
                it["d"].values.toList() shouldBe listOf(null, 5)
            }
    }

    @Test
    fun `parse json with nested json array with mixed values`() {
        @Language("json")
        val json = """[
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6, {"a": "b"}, [1, {"a" : "b"}],8]},
                {"a":[{"a": "b"}, {"a" : "c"}, {"a" : "d"}]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json)
            .alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 4
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
    fun `parse json with nested json array with mixed values Any`() {
        @Language("json")
        val json = """[
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6, {"a": "b"}, [1, {"a" : "b"}],8]},
                {"a":[{"a": "b"}, {"a" : "c"}, {"a" : "d"}]}
            ]
        """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 4
        val a = df["a"] as ValueColumn<*>
        a.type() shouldBe typeOf<Any>()
        // TODO
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
    fun `list serialization with nulls`() {
        val df = dataFrameOf("a")(listOf(1, 2, 3), null)
        val text = df.toJson()
        val df1 = DataFrame.readJsonStr(text)
        df1["a"][1] shouldBe emptyList<Int>()
    }

    @Test
    fun `serialize column with name 'value'`() {
        val df = dataFrameOf("a")(dataFrameOf("value")(1, 2, 3))

        @Language("json")
        val json = df.toJson()
        json shouldContain "\"value\":1"
        val df1 = DataFrame.readJsonStr(json)
        df shouldBe df1
    }

    @Test
    fun `literal json field named 'value'`() {
        @Language("json")
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
        @Language("json")
        val json = """{ "value": ["123"] }"""

        val df = DataFrame.readJsonStr(json)
        df[0]["value"] shouldBe listOf("123")
    }

    @Test
    fun `record json field named 'value'`() {
        @Language("json")
        val json = """{ "value": { "test" : "123" } }"""

        val df = DataFrame.readJsonStr(json)
        df[0].getColumnGroup("value")["test"] shouldBe "123"
    }

    @Test
    fun `json field named 'array'`() {
        @Language("json")
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
        @Language("json")
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
