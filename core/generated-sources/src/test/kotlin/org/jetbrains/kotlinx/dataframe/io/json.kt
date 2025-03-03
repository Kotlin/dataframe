package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.instanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.allNulls
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.columnsCount
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toFloat
import org.jetbrains.kotlinx.dataframe.api.toMap
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.io.SERIALIZATION_VERSION
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.COLUMNS
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.DATA
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.KIND
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.KOTLIN_DATAFRAME
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.METADATA
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.NCOL
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.NROW
import org.jetbrains.kotlinx.dataframe.impl.io.SerializationKeys.VERSION
import org.jetbrains.kotlinx.dataframe.impl.io.readJsonImpl
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.nullableNothingType
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ANY_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
import org.jetbrains.kotlinx.dataframe.parseJsonStr
import org.jetbrains.kotlinx.dataframe.testJson
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test
import kotlin.Double
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class JsonTests {

    @Test
    fun `parse json array with header`() {
        @Language("json")
        val json =
            """
            [
                [1, "a"],
                [2, "b"],
                [3, "c"],
                [4, "d"],
                [5, "e"]                
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, header = listOf("numbers", "letters"))
            .alsoDebug()

        df.columnsCount() shouldBe 2
        df.rowsCount() shouldBe 5
        df["numbers"].type() shouldBe typeOf<Int>()
        df["letters"].type() shouldBe typeOf<String>()
        df["numbers"].values() shouldBe listOf(1, 2, 3, 4, 5)
        df["letters"].values() shouldBe listOf("a", "b", "c", "d", "e")
    }

    @Test
    fun `parse json array with header Any`() {
        @Language("json")
        val json =
            """
            [
                [1, "a"],
                [2, "b"],
                [3, "c"],
                [4, "d"],
                [5, "e"]                
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, header = listOf("numbers", "letters"), typeClashTactic = ANY_COLUMNS)
            .alsoDebug()

        df.columnsCount() shouldBe 2
        df.rowsCount() shouldBe 5
        df["numbers"].type() shouldBe typeOf<Int>()
        df["letters"].type() shouldBe typeOf<String>()
        df["numbers"].values() shouldBe listOf(1, 2, 3, 4, 5)
        df["letters"].values() shouldBe listOf("a", "b", "c", "d", "e")
    }

    @Test
    fun parseJson1() {
        @Language("json")
        val json =
            """
            [
                {"a":1, "b":"text"},
                {"a":2, "b":5, "c":4.5}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json).alsoDebug()
        df.columnsCount() shouldBe 3
        df.rowsCount() shouldBe 2
        df["a"].type() shouldBe typeOf<Int>()
        df["b"].type() shouldBe typeOf<Comparable<*>>()
        df["c"].type() shouldBe typeOf<Float?>()
    }

    @Test
    fun parseJson1Any() {
        @Language("json")
        val json =
            """
            [
                {"a":1, "b":"text"},
                {"a":2, "b":5, "c":4.5}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 3
        df.rowsCount() shouldBe 2
        df["a"].type() shouldBe typeOf<Int>()
        df["b"].type() shouldBe typeOf<Comparable<*>>()
        df["c"].type() shouldBe typeOf<Float?>()
    }

    @Test
    fun parseJson2() {
        @Language("json")
        val json =
            """
            [
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6,7,8]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json).alsoDebug()
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
        val json =
            """
            [
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6,7,8]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
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
        val json =
            """
            [
                {"a":[3, 5]},
                {},
                {"a":[3.4, 5.6]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        df["a"].type() shouldBe typeOf<List<Double>>()
        df[1]["a"] shouldBe emptyList<Int>()
    }

    @Test
    fun parseJson3Any() {
        @Language("json")
        val json =
            """
            [
                {"a":[3, 5]},
                {},
                {"a":[3.4, 5.6]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 3
        df["a"].type() shouldBe typeOf<List<Double>>()
        df[1]["a"] shouldBe emptyList<Int>()
    }

    @Test
    fun parseJson4() {
        @Language("json")
        val json =
            """
            [
                {"a":[ {"b":2}, {"c":3} ]},
                {"a":[ {"b":4.0}, {"d":5} ]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 2
        val group = df["a"] as FrameColumn<*>
        group[0].alsoDebug().let {
            it.columnsCount() shouldBe 3
            it.rowsCount() shouldBe 2
            it["b"].type() shouldBe typeOf<Double?>()
            it["c"].type() shouldBe typeOf<Int?>()
            it["d"].type() shouldBe typeOf<Int?>()
            it["b"].values.toList() shouldBe listOf(2, null)
            it["c"].values.toList() shouldBe listOf(null, 3)
            it["d"].values.toList() shouldBe listOf(null, null)
        }

        group[1].alsoDebug().let {
            it.columnsCount() shouldBe 3
            it.rowsCount() shouldBe 2
            it["b"].type() shouldBe typeOf<Double?>()
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
        val json =
            """
            [
                {"a":[ {"b":2}, {"c":3} ]},
                {"a":[ {"b":4}, {"d":5} ]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 2
        val group = df["a"] as FrameColumn<*>
        group[0].alsoDebug().let {
            it.columnsCount() shouldBe 3
            it.rowsCount() shouldBe 2
            it["b"].type() shouldBe typeOf<Int?>()
            it["c"].type() shouldBe typeOf<Int?>()
            it["d"].type() shouldBe typeOf<Int?>()
            it["b"].values.toList() shouldBe listOf(2, null)
            it["c"].values.toList() shouldBe listOf(null, 3)
            it["d"].values.toList() shouldBe listOf(null, null)
        }

        group[1].alsoDebug().let {
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
    fun `json and number unification`() {
        @Language("json")
        val json =
            """
            [
                {"a":1},
                {"a":2.0},
                {"a":3},
                {"a":4.5}
            ]
            """.trimIndent()
        val df1 = DataFrame.readJsonStr(json, unifyNumbers = true).alsoDebug()
        df1.columnsCount() shouldBe 1
        df1.rowsCount() shouldBe 4
        df1["a"].type() shouldBe typeOf<Double>()
        df1["a"].values.toList() shouldBe listOf(1.0, 2.0, 3.0, 4.5)

        val df2 = DataFrame.readJsonStr(json, unifyNumbers = false).alsoDebug()
        df2.columnsCount() shouldBe 1
        df2.rowsCount() shouldBe 4
        df2["a"].type() shouldBe typeOf<Number>()
        df2["a"].values.toList() shouldBe listOf(1, 2.0f, 3, 4.5f)
    }

    @Test
    fun `parse json with nested json array with mixed values`() {
        @Language("json")
        val json =
            """
            [
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6, {"a": "b"}, [1, {"a" : "b"}],8]},
                {"a":[{"a": "b"}, {"a" : "c"}, {"a" : "d"}]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json).alsoDebug()
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
        group.getFrameColumn("array")[3].alsoDebug().let {
            it.columnsCount() shouldBe 3
            it.rowsCount() shouldBe 3
            it["a"].type() shouldBe typeOf<String>()
            it["a"].values.toList() shouldBe listOf("b", "c", "d")
        }
    }

    @Test
    fun `parse json with nested json array with mixed values Any`() {
        @Language("json")
        val json =
            """
            [
                {"a":"text"},
                {"a":{"b":2}},
                {"a":[6, {"a": "b"}, [1, {"a" : "b"}],8]},
                {"a":[{"a": "b"}, {"a" : "c"}, {"a" : "d"}]}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 4
        val a = df["a"] as ValueColumn<*>
        a.type() shouldBe typeOf<Any>()
        a[0] shouldBe "text"
        (a[1] as DataRow<*>).let {
            it.columnsCount() shouldBe 1
            it["b"] shouldBe 2
        }
        (a[2] as List<*>).let {
            it[0] shouldBe 6
            (it[1] as DataRow<*>).let {
                it.columnsCount() shouldBe 1
                it["a"] shouldBe "b"
            }
            (it[2] as List<*>).let {
                it[0] shouldBe 1
                (it[1] as DataRow<*>).let {
                    it.columnsCount() shouldBe 1
                    it["a"] shouldBe "b"
                }
            }
            it[3] shouldBe 8
        }
        (a[3] as DataFrame<*>)
            .alsoDebug()
            .let {
                it.columnsCount() shouldBe 1
                it.rowsCount() shouldBe 3
                it["a"].type() shouldBe typeOf<String>()
                it["a"].values.toList() shouldBe listOf("b", "c", "d")
            }
    }

    @Test
    fun `write df with primitive types`() {
        val df = dataFrameOf("colInt", "colDouble?", "colBoolean?")(
            1, 1.0, true,
            2, null, false,
            3, 3.0, null,
        ).alsoDebug("df:")

        val res = DataFrame.readJsonStr(df.toJson()).alsoDebug("res:")
        res shouldBe df.convert { colsOf<Double?>() }.toFloat()
    }

    @Test
    fun `write df with primitive types Any`() {
        val df = dataFrameOf("colInt", "colDouble?", "colBoolean?")(
            1, 1.0, true,
            2, null, false,
            3, 3.0, null,
        ).alsoDebug("df:")

        val res =
            DataFrame.readJsonStr(df.toJson(), typeClashTactic = ANY_COLUMNS).alsoDebug("res:")
        res shouldBe df.convert { colsOf<Double?>() }.toFloat()
    }

    @Test
    fun `NaN double serialization Any`() {
        val df = dataFrameOf("v")(1.1, Double.NaN)
        df["v"].type() shouldBe typeOf<Double>()

        val df2 = DataFrame.readJsonStr(df.toJson(), typeClashTactic = ANY_COLUMNS)
        df2["v"].type() shouldBe typeOf<Float>()
        df2 shouldBe df.convert("v").toFloat()
    }

    @Test
    fun `NaN float serialization`() {
        val df = dataFrameOf("v")(1.1f, Float.NaN)
        df["v"].type() shouldBe typeOf<Float>()
        val actual = DataFrame.readJsonStr(df.toJson()).convert("v").toFloat()
        actual shouldBe df
    }

    @Test
    fun `NaN float serialization Any`() {
        val df = dataFrameOf("v")(1.1f, Float.NaN)
        df["v"].type() shouldBe typeOf<Float>()
        val actual = DataFrame.readJsonStr(df.toJson(), typeClashTactic = ANY_COLUMNS).convert("v").toFloat()
        actual shouldBe df
    }

    @Test
    fun `NaN string serialization`() {
        val df = dataFrameOf("v")("NaM", "NaN")
        df["v"].type() shouldBe typeOf<String>()
        DataFrame.readJsonStr(df.toJson()) shouldBe df
    }

    @Test
    fun `NaN string serialization Any`() {
        val df = dataFrameOf("v")("NaM", "NaN")
        df["v"].type() shouldBe typeOf<String>()
        DataFrame.readJsonStr(df.toJson(), typeClashTactic = ANY_COLUMNS) shouldBe df
    }

    @Test
    fun `list serialization`() {
        val df = dataFrameOf("a")(listOf(1, 2, 3))
        DataFrame.readJsonStr(df.toJson()) shouldBe df
    }

    @Test
    fun `list serialization Any`() {
        val df = dataFrameOf("a")(listOf(1, 2, 3))
        DataFrame.readJsonStr(df.toJson(), typeClashTactic = ANY_COLUMNS) shouldBe df
    }

    @Test
    fun `list serialization with nulls`() {
        val df = dataFrameOf("a")(listOf(1, 2, 3), null)
        val text = df.toJson()
        val df1 = DataFrame.readJsonStr(text)
        df1["a"][1] shouldBe emptyList<Int>()
    }

    @Test
    fun `list serialization with nulls Any`() {
        val df = dataFrameOf("a")(listOf(1, 2, 3), null)
        val text = df.toJson()
        val df1 = DataFrame.readJsonStr(text, typeClashTactic = ANY_COLUMNS)
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
        val json =
            """
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

        val df = DataFrame.readJsonStr(json).alsoDebug()
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
        val json =
            """
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
        val json =
            """
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

        val df = DataFrame.readJsonStr(json).alsoDebug()
        val group = df.getColumnGroup("a")
        group["array"].type() shouldBe typeOf<List<Int>>()
        group["value"].type() shouldBe typeOf<String?>()
        group["b"].type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `value field name clash`() {
        @Language("json")
        val json =
            """
            [
                {"a":"text", "c": 1},
                {"a":{"b":2,"value":1.0, "array": null, "array1":12}},
                {"a":[6,7,8]},
                null
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json).alsoDebug()
        df.columnsCount() shouldBe 2
        df.rowsCount() shouldBe 4
        df["c"].type() shouldBe typeOf<Int?>()
        val group = df["a"] as ColumnGroup<*>
        group.columnsCount() shouldBe 6
        group["b"].type() shouldBe typeOf<Int?>()
        group["value"].type() shouldBe typeOf<Float?>()
        group["value1"].type() shouldBe typeOf<String?>()
        group["array"].type() shouldBe nothingType(nullable = true)

        val schema = df.schema().toString()
        schema shouldContain "Nothing?"
        schema shouldNotContain "Void?"

        group["array1"].type() shouldBe typeOf<Int?>()
        group["array2"].type() shouldBe typeOf<List<Int>>()
    }

    @Test
    fun `value field (no) name clash Any`() {
        @Language("json")
        val json =
            """
            [
                {"a":"text", "c": 1},
                {"a":{"b":2,"value":1.0, "array": null, "array1":12}},
                {"a":[6,7,8]},
                null
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 2
        df.rowsCount() shouldBe 4
        val c = df["c"] as ValueColumn<*>
        c.type() shouldBe typeOf<Int?>()
        c[0] shouldBe 1
        c[1..3].allNulls() shouldBe true
        val a = df["a"] as ValueColumn<*>
        a.type() shouldBe typeOf<Any?>()
        a[0] shouldBe "text"
        (a[1] as DataRow<*>).let {
            it.columnsCount() shouldBe 4
            it["b"] shouldBe 2
            it["value"] shouldBe 1.0
            it["array"] shouldBe null
            it["array1"] shouldBe 12
        }
        a[2] shouldBe listOf(6, 7, 8)
        a[3] shouldBe null
    }

    @Test
    fun `objects with null Any`() {
        @Language("json")
        val json =
            """
            [
                {"a":{"b":1}},
                {"a":{"b":2}},
                {"a":{"b": null}},
                {"a": {}},
                {"a": null},
                {},
                null
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 7
        val a = df["a"] as ColumnGroup<*>
        a.columnsCount() shouldBe 1
        a["b"].let {
            it.type() shouldBe typeOf<Int?>()
            it[0] shouldBe 1
            it[1] shouldBe 2
            it[2..6].allNulls() shouldBe true
        }
    }

    @Test
    fun `primitive arrays with null Any`() {
        @Language("json")
        val json =
            """
            [
                {"a":[1,2,3]},
                {"a":[null]},
                {"a":[]},
                {"a": null},
                {},
                null
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 6
        val a = df["a"] as ValueColumn<*>
        a.type shouldBe typeOf<List<Int?>>()
        a[0] shouldBe listOf(1, 2, 3)
        a[1] shouldBe listOf(null)
        a[2..5].forEach {
            it shouldBe emptyList<Int?>()
        }
    }

    @Test
    fun `non-primitive arrays with null Any`() {
        @Language("json")
        val json =
            """
            [
                {"a":[null, null]},
                {"a":[{"b" : 1},{"b":  2}]},
                {"a":[]},
                {"a": null},
                {},
                null
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS).alsoDebug()
        df.columnsCount() shouldBe 1
        df.rowsCount() shouldBe 6
        val a = df["a"] as FrameColumn<*>
        a[0].let {
            it.columnsCount() shouldBe 1
            it.rowsCount() shouldBe 2
            it["b"].let {
                it.type() shouldBe typeOf<Int?>()
                it[0] shouldBe null
                it[1] shouldBe null
            }
        }
        a[1].let {
            it.columnsCount() shouldBe 1
            it.rowsCount() shouldBe 2
            it["b"].let {
                it.type() shouldBe typeOf<Int>()
                it[0] shouldBe 1
                it[1] shouldBe 2
            }
        }
        a[2..5].forEach {
            it.columnsCount() shouldBe 0
            it.rowsCount() shouldBe 0
        }
    }

    @Test
    fun `Listification test Array Value`() {
        @Language("json")
        val json =
            """
            [
                {"a":[1,2,3]},
                {"a":null},
                {"a":1}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(
            text = json,
            typeClashTactic = ARRAY_AND_VALUE_COLUMNS,
            keyValuePaths = listOf(JsonPath()),
        ).alsoDebug()
    }

    @Test
    fun `Listification test Any column`() {
        @Language("json")
        val json =
            """
            [
                {"a":[1,2,3]},
                {"a":null},
                {"a":1}
            ]
            """.trimIndent()
        val df = DataFrame.readJsonStr(
            text = json,
            typeClashTactic = ANY_COLUMNS,
            keyValuePaths = listOf(JsonPath()),
        ).alsoDebug()
    }

    @Test
    fun `KeyValue property Array Value`() {
        @Language("json")
        val json =
            """
            [
                {"a":{"b":1}},
                {"a":{"c": 2, "d": null, "b":[1, 2, 3]}},
                {"a":{}},
                {"a": null},
                {},
                null
            ]
            """.trimIndent()

        // before
        val noKeyValue = DataFrame.readJsonStr(json, typeClashTactic = ARRAY_AND_VALUE_COLUMNS)
            .alsoDebug()

//        ⌌-------------------------------------------------------⌍
//        |  | a:{b:{value:Int?, array:List<Int>}, c:Int?, d:Any?}|
//        |--|----------------------------------------------------|
//        | 0|         { b:{ value:1, array:[] }, c:null, d:null }|
//        | 1|  { b:{ value:null, array:[1, 2, 3] }, c:2, d:null }|
//        | 2|      { b:{ value:null, array:[] }, c:null, d:null }|
//        | 3|      { b:{ value:null, array:[] }, c:null, d:null }|
//        | 4|      { b:{ value:null, array:[] }, c:null, d:null }|
//        | 5|      { b:{ value:null, array:[] }, c:null, d:null }|
//        ⌎-------------------------------------------------------⌏
        noKeyValue.columnsCount() shouldBe 1
        noKeyValue.rowsCount() shouldBe 6
        noKeyValue["a"].also {
            it shouldBe instanceOf<ColumnGroup<*>>()
            it as ColumnGroup<*>

            it["b"].type() shouldBe typeOf<DataRow<*>>()
            it["b"]["value"].type() shouldBe typeOf<Int?>()
            it["b"]["array"].type() shouldBe typeOf<List<Int>>()
            it["c"].type() shouldBe typeOf<Int?>()
            it["d"].type() shouldBe nothingType(nullable = true)

            it[0].let {
                (it["b"] as DataRow<*>).toMap() shouldBe mapOf("value" to 1, "array" to emptyList<Int>())
                it["c"] shouldBe null
                it["d"] shouldBe null
            }
            it[1].let {
                (it["b"] as DataRow<*>).toMap() shouldBe mapOf("value" to null, "array" to listOf(1, 2, 3))
                it["c"] shouldBe 2
                it["d"] shouldBe null
            }
            (it as ColumnGroup<*>)[2..5].forEach {
                it.let {
                    (it["b"] as DataRow<*>).toMap() shouldBe mapOf("value" to null, "array" to emptyList<Int>())
                    it["c"] shouldBe null
                    it["d"] shouldBe null
                }
            }
        }

        // $["a"] should be read as keyValue
        val keyValuePaths = listOf(
            JsonPath().append("a"),
        )

        // after
        val withKeyValue =
            DataFrame.readJsonStr(json, keyValuePaths = keyValuePaths, typeClashTactic = ARRAY_AND_VALUE_COLUMNS)
                .alsoDebug()
                .also {
                    it["a"][1].let { it as AnyFrame }.alsoDebug()
                }
//        ⌌------------------------------⌍
//        |  | a:[key:String, value:Any?]|
//        |--|---------------------------|
//        | 0| [1 x 2] { key:b, value:1 }|
//        | 1|                    [3 x 2]| ->  { key:c, value:2 }
//        | 2|                    [0 x 2]|     { key:d, value:null }
//        | 3|                    [0 x 2]|     { key:b, value:[1,2,3] }
//        | 4|                    [0 x 2]|
//        | 5|                    [0 x 2]|
//        ⌎------------------------------⌏

        withKeyValue.columnsCount() shouldBe 1
        withKeyValue.rowsCount() shouldBe 6
        withKeyValue["a"].also {
            it shouldBe instanceOf<FrameColumn<*>>()
            it as FrameColumn<*>

            it[0].let {
                it.columnsCount() shouldBe 2
                it.rowsCount() shouldBe 1
                it["key"].let {
                    it.type() shouldBe typeOf<String>()
                    it[0] shouldBe "b"
                }
                it["value"].let {
                    it.type() shouldBe typeOf<Int>() // tightened by values, but Int? is also valid of course
                    it[0] shouldBe 1
                }
            }
            it[1].let {
                it.columnsCount() shouldBe 2
                it.rowsCount() shouldBe 3
                it["key"].let {
                    it.type() shouldBe typeOf<String>()
                    it[0] shouldBe "c"
                    it[1] shouldBe "d"
                }
                it["value"].let {
                    it.type() shouldBe typeOf<Any?>()
                    it[0] shouldBe 2
                    it[1] shouldBe null
                }
            }
            it[2..5].forEach {
                it.columnsCount() shouldBe 2
                it.rowsCount() shouldBe 0

                it["key"].type() shouldBe typeOf<String>()
                it["value"].type() shouldBeIn listOf(typeOf<Any?>(), typeOf<Any>()) // no data, so Any(?) ValueColumn
            }
        }
    }

    @Test
    fun `KeyValue property Any`() { // TODO needs more tests
        @Language("json")
        val json =
            """
            [
                {"a":{"b": 1}},
                {"a":{"c": 2, "d": null, "b":[1, 2, 3]}},
                {"a":{}},
                {"a": null},
                {},
                null
            ]
            """.trimIndent()

        // before
        val noKeyValue = DataFrame.readJsonStr(json, typeClashTactic = ANY_COLUMNS)
            .alsoDebug()

//        ⌌------------------------------⌍
//        |  | a:{b:Any?, c:Int?, d:Any?}|
//        |--|---------------------------|
//        | 0|                    { b:1 }|
//        | 1|{ b:[1,2,3], c:2, d: null }|
//        | 2|                        { }|
//        | 3|                        { }|
//        | 4|                        { }|
//        | 5|                        { }|
//        ⌎------------------------------⌏
        noKeyValue.columnsCount() shouldBe 1
        noKeyValue.rowsCount() shouldBe 6
        noKeyValue["a"].also {
            it shouldBe instanceOf<ColumnGroup<*>>()
            it as ColumnGroup<*>

            it["b"].type() shouldBe typeOf<Any?>()
            it["c"].type() shouldBe typeOf<Int?>()
            it["d"].type() shouldBe nullableNothingType

            it[0].toMap() shouldBe mapOf("b" to 1, "c" to null, "d" to null)
            it[1].toMap() shouldBe mapOf("b" to listOf(1, 2, 3), "c" to 2, "d" to null)
            (it as ColumnGroup<*>)[2..5].forEach {
                it.toMap() shouldBe mapOf("b" to null, "c" to null, "d" to null)
            }
        }

        // $["a"] should be read as keyValue
        val keyValuePaths = listOf(
            JsonPath().append("a"),
        )

        // after
        val withKeyValue = DataFrame.readJsonStr(
            text = json,
            keyValuePaths = keyValuePaths,
            typeClashTactic = ANY_COLUMNS,
        ).alsoDebug().also {
            it["a"][1].let { it as AnyFrame }.alsoDebug()
        }

//        ⌌------------------------------⌍
//        |  | a:[key:String, value:Any?]|
//        |--|---------------------------|
//        | 0| [1 x 2] { key:b, value:1 }|
//        | 1|                    [3 x 2]| ->  { key:c, value:2 }
//        | 2|                    [0 x 2]|     { key:d, value:null }
//        | 3|                    [0 x 2]|     { key:b, value:[1,2,3] }
//        | 4|                    [0 x 2]|
//        | 5|                    [0 x 2]|
//        ⌎------------------------------⌏
        withKeyValue.columnsCount() shouldBe 1
        withKeyValue.rowsCount() shouldBe 6
        withKeyValue["a"].also {
            it shouldBe instanceOf<FrameColumn<*>>()
            it as FrameColumn<*>

            it[0].let {
                it.columnsCount() shouldBe 2
                it.rowsCount() shouldBe 1
                it["key"].let {
                    it.type() shouldBe typeOf<String>()
                    it[0] shouldBe "b"
                }
                it["value"].let {
                    it.type() shouldBe typeOf<Int>() // tightened by values, but Int? is also valid of course
                    it[0] shouldBe 1
                }
            }
            it[1].let {
                it.columnsCount() shouldBe 2
                it.rowsCount() shouldBe 3
                it["key"].let {
                    it.type() shouldBe typeOf<String>()
                    it[0] shouldBe "c"
                    it[1] shouldBe "d"
                }
                it["value"].let {
                    it.type() shouldBe typeOf<Any?>()
                    it[0] shouldBe 2
                    it[1] shouldBe null
                    it[2] shouldBe listOf(1, 2, 3)
                }
            }
            it[2..5].forEach {
                it.columnsCount() shouldBe 2
                it.rowsCount() shouldBe 0

                it["key"].type() shouldBe typeOf<String>()
                it["value"].type() shouldBeIn listOf(typeOf<Any?>(), typeOf<Any>()) // no data, so Any(?) ValueColumn
            }
        }
    }

    @Test
    fun `nulls in columns should be encoded explicitly`() {
        val df = dataFrameOf("a", "b")("1", null, "2", 12)
        df.toJson() shouldContain "\"b\":null"
//        df.toJson(canonical = true) shouldContain "\"b\":null"
    }

    @Test
    fun `json with metadata flat table`() {
        @Language("json")
        val data =
            """
            [{"id":3602279,"node_id":"MDEwOlJlcG9zaXRvcnkzNjAyMjc5","name":"kotlin-web-demo","full_name":"JetBrains/kotlin-web-demo"}]
            """.trimIndent()
        val df = DataFrame.readJsonStr(data)
        val jsonStr = df.toJsonWithMetadata(df.rowsCount()).trimIndent()
        val json = parseJsonStr(jsonStr)

        json[VERSION]!!.jsonPrimitive.content shouldBe SERIALIZATION_VERSION

        val metadata = json[METADATA]!!.jsonObject
        metadata[NROW]!!.jsonPrimitive.int shouldBe 1
        metadata[NCOL]!!.jsonPrimitive.int shouldBe 4
        val columns = metadata[COLUMNS]!!.jsonArray.map { it.jsonPrimitive.content }
        columns shouldBe listOf("id", "node_id", "name", "full_name")

        val decodedData = json[KOTLIN_DATAFRAME]!!.jsonArray
        val decodedDf = DataFrame.readJsonStr(decodedData.toString())
        decodedDf shouldBe df
    }

    @Test
    fun `json with metadata column group`() {
        @Language("json")
        val data =
            """
            [{"permissions":{"admin":false,"maintain":false,"push":false,"triage":false,"pull":true}}]
            """.trimIndent()
        val df = DataFrame.readJsonStr(data)
        val jsonStr = df.toJsonWithMetadata(df.rowsCount()).trimIndent()
        val json = parseJsonStr(jsonStr)

        val row = json[KOTLIN_DATAFRAME]!!.jsonArray[0].jsonObject

        val permissions = row["permissions"]!!.jsonObject
        val metadata = permissions[METADATA]!!.jsonObject
        metadata[KIND]!!.jsonPrimitive.content shouldBe ColumnKind.Group.toString()

        val decodedData = permissions[DATA]!!.jsonObject

        decodedData["admin"]!!.jsonPrimitive.boolean shouldBe false
        decodedData["maintain"]!!.jsonPrimitive.boolean shouldBe false
        decodedData["push"]!!.jsonPrimitive.boolean shouldBe false
        decodedData["triage"]!!.jsonPrimitive.boolean shouldBe false
        decodedData["pull"]!!.jsonPrimitive.boolean shouldBe true
    }

    @Test
    fun `json with metadata frame column`() {
        val df = DataFrame.readJson(testJson("repositories"))
        val jsonStr = df.toJsonWithMetadata(df.rowsCount()).trimIndent()
        val json = parseJsonStr(jsonStr)
        val row = json[KOTLIN_DATAFRAME]!!.jsonArray[0].jsonObject

        val contributors = row["contributors"]!!.jsonObject

        val metadata = contributors[METADATA]!!.jsonObject
        metadata[KIND]!!.jsonPrimitive.content shouldBe ColumnKind.Frame.toString()
        metadata[NCOL]!!.jsonPrimitive.int shouldBe 8
        metadata[NROW]!!.jsonPrimitive.int shouldBe 29

        val decodedData = contributors[DATA]!!.jsonArray
        decodedData.size shouldBe 29

        val decodedDf = DataFrame.readJsonStr(decodedData.toString())
        decodedDf shouldBe df[0]["contributors"] as AnyFrame
    }

    @Test
    fun `json with metadata test row limit`() {
        val df = DataFrame.readJson(testJson("repositories"))
        val nestedFrameRowLimit = 20
        val jsonStr = df.toJsonWithMetadata(df.rowsCount(), nestedFrameRowLimit).trimIndent()
        val json = parseJsonStr(jsonStr)
        val row = json[KOTLIN_DATAFRAME]!!.jsonArray[0].jsonObject

        val contributors = row["contributors"]!!.jsonObject

        val metadata = contributors[METADATA]!!.jsonObject
        metadata[KIND]!!.jsonPrimitive.content shouldBe ColumnKind.Frame.toString()
        metadata[NCOL]!!.jsonPrimitive.int shouldBe 8
        metadata[NROW]!!.jsonPrimitive.int shouldBe 29

        val decodedData = contributors[DATA]!!.jsonArray
        decodedData.size shouldBe nestedFrameRowLimit
    }

    @Test
    fun `serialize column with list of objects`() {
        val df = dataFrameOf("col")(Regex(".+").findAll("abc").toList())
        val json = shouldNotThrowAny { df.toJson() }
        val list = DataFrame.readJsonStr(json)["col"][0].shouldBeInstanceOf<List<*>>()
        list[0].shouldBeInstanceOf<String>()
    }

    @Test
    fun `serialize column with list of primitives`() {
        val df = dataFrameOf("col")(listOf(1, 2, 3))
        val json = df.toJson()
        DataFrame.readJsonStr(json) shouldBe df
    }

    @Test
    fun `parse invalid literal`() {
        // https://github.com/Kotlin/kotlinx.serialization/issues/2511
        val json = Json.decodeFromString<JsonElement>("""[jetbrains, jetbrains-youtrack, youtrack, youtrack-api]""")
        shouldThrow<IllegalStateException> {
            readJsonImpl(json, true, emptyList())
        }
    }
}
