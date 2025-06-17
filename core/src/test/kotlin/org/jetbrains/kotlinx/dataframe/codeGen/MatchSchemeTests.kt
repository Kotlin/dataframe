package org.jetbrains.kotlinx.dataframe.codeGen

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.generateCode
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.impl.codeGen.ReplCodeGenerator
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.Equals
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsDerived
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsSuper
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.None
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.LENIENT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS
import org.junit.Test

class MatchSchemeTests {

    @DataSchema(isOpen = false)
    interface Snippet {
        val position: Int
        val info: String
    }

    @DataSchema(isOpen = false)
    interface Item {
        val kind: String
        val id: String
        val snippet: DataRow<Snippet>
    }

    @DataSchema(isOpen = false)
    interface PageInfo {
        val totalResults: Int
        val resultsPerPage: Int
        val snippets: DataFrame<Snippet>
    }

    @DataSchema
    interface DataRecord {
        val kind: String
        val items: DataFrame<Item>
        val pageInfo: DataRow<PageInfo>
    }

    val json =
        """
        {
            "kind": "qq",
            "pageInfo": {
                "totalResults": 2,
                "resultsPerPage": 3,
                "snippets": [
                    {
                        "position": 3,
                        "info": "str"
                    },
                    {
                        "position": 5,
                        "info": "txt"
                    }
                ]
            },
            "items": [
                {
                    "kind": "asd",
                    "id": "zxc",
                    "snippet": {
                        "position": 2,
                        "info": "qwe"
                    }
                }
            ]
        }
        """.trimIndent()

    val df = DataFrame.readJsonStr(json)

    val typed = df.cast<DataRecord>()

    @Test
    fun `marker is reused`() {
        val codeGen = ReplCodeGenerator.create()
        codeGen.process(DataRecord::class)
        codeGen.process(typed, ::typed).hasConverter shouldBe false
        val generated = codeGen.process(df, ::df)
        generated.declarations.split("\n").size shouldBe 1
    }

    val modified = df.add("new") { 4 }

    @Test
    fun `marker is implemented`() {
        val codeGen = ReplCodeGenerator.create()
        codeGen.process(DataRecord::class)
        val generated = codeGen.process(modified, ::modified)
        generated.declarations.contains(DataRecord::class.simpleName!!) shouldBe true
    }

    @Test
    fun printSchema() {
        val res = df.generateCode(false, true)
        println(res)
    }

    @Test
    fun `simple data schema comparison`() {
        val scheme1 = dataFrameOf(
            "a" to columnOf(1, 2, 3, null),
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
        ).schema()

        val scheme2 = dataFrameOf(
            "a" to columnOf(1, 2, 3, 4),
            "b" to columnOf(1.0, 2.0, 3.0, 4.0),
        ).schema()

        val scheme3 = dataFrameOf(
            "c" to columnOf(1, 2, 3, 4),
        ).schema()

        scheme1.compare(scheme1, LENIENT) shouldBe Equals
        scheme2.compare(scheme2, LENIENT) shouldBe Equals
        scheme1.compare(scheme2, LENIENT) shouldBe IsSuper
        scheme2.compare(scheme1, LENIENT) shouldBe IsDerived
        scheme1.compare(scheme3, LENIENT) shouldBe None

        scheme1.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe Equals
        scheme2.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe Equals
        scheme1.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsSuper
        scheme2.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsDerived
        scheme1.compare(scheme3, STRICT_FOR_NESTED_SCHEMAS) shouldBe None

        scheme1.compare(scheme1, STRICT) shouldBe Equals
        scheme2.compare(scheme2, STRICT) shouldBe Equals
        scheme1.compare(scheme2, STRICT) shouldBe None
        scheme2.compare(scheme1, STRICT) shouldBe None
    }

    @Test
    fun `nested data schema comparison`() {
        val scheme1 = dataFrameOf(
            "a" to columnOf(
                "b" to columnOf(1.0, 2.0, 3.0, null),
            ),
        ).schema()

        val scheme2 = dataFrameOf(
            "a" to columnOf(
                "b" to columnOf(1.0, 2.0, 3.0, 4.0),
            ),
        ).schema()

        val scheme3 = dataFrameOf(
            "c" to columnOf(1, 2, 3, 4),
        ).schema()

        val scheme4 = dataFrameOf(
            "a" to columnOf(
                "b" to columnOf(1.0, 2.0, 3.0, null),
            ),
            "c" to columnOf(1, 2, 3, 4),
        ).schema()

        scheme1.compare(scheme1, LENIENT) shouldBe Equals
        scheme2.compare(scheme2, LENIENT) shouldBe Equals
        scheme1.compare(scheme2, LENIENT) shouldBe IsSuper
        scheme2.compare(scheme1, LENIENT) shouldBe IsDerived
        scheme1.compare(scheme3, LENIENT) shouldBe None

        scheme1.compare(scheme4, LENIENT) shouldBe IsSuper
        scheme4.compare(scheme1, LENIENT) shouldBe IsDerived

        scheme1.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe Equals
        scheme2.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe Equals
        scheme1.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe None
        scheme2.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe None
        scheme1.compare(scheme3, STRICT_FOR_NESTED_SCHEMAS) shouldBe None

        scheme1.compare(scheme4, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsSuper
        scheme4.compare(scheme1, STRICT_FOR_NESTED_SCHEMAS) shouldBe IsDerived
        scheme2.compare(scheme4, STRICT_FOR_NESTED_SCHEMAS) shouldBe None
        scheme4.compare(scheme2, STRICT_FOR_NESTED_SCHEMAS) shouldBe None

        scheme1.compare(scheme1, STRICT) shouldBe Equals
        scheme2.compare(scheme2, STRICT) shouldBe Equals
        scheme1.compare(scheme2, STRICT) shouldBe None
        scheme2.compare(scheme1, STRICT) shouldBe None
        scheme1.compare(scheme3, STRICT) shouldBe None
        scheme3.compare(scheme1, STRICT) shouldBe None
    }
}
