package org.jetbrains.dataframe.ksp

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.junit.Test

class ImportedSchemaSerializationTests {
    @Test
    fun test() {
        val df = dataFrameOf(
            "a" to columnOf(""),
            "group" to columnOf(
                "b" to columnOf(1),
                "c" to columnOf(3.0),
            ),
            "frame" to columnOf(
                dataFrameOf(
                    "abc" to columnOf(111),
                ),
            ),
        )

        val res = df.schema().toJsonString()
        res shouldBe
            """
            {
                "schema": {
                    "a": "kotlin.String",
                    "group: ColumnGroup": {
                        "b": "kotlin.Int",
                        "c": "kotlin.Double"
                    },
                    "frame: FrameColumn": {
                        "abc": "kotlin.Int"
                    }
                }
            }
            """.trimIndent()
    }

    @Test
    fun testEmpty() {
        val df = DataFrame.empty()

        df.schema().toJsonString() shouldBe
            """
            {
                "schema": {}
            }
            """.trimIndent()
    }
}
