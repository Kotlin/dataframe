package org.jetbrains.dataframe.codeGen

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.io.fromJson
import org.jetbrains.dataframe.io.fromJsonStr
import org.junit.Test

class MatchSchemeTests {

    @DataFrameType(isOpen = false)
    interface Snippet {
        val position: Int
        val info: String
    }

    @DataFrameType(isOpen = false)
    interface Item {
        val kind: String
        val id: String
        val snippet: TypedDataFrameRow<Snippet>
    }

    @DataFrameType(isOpen = false)
    interface PageInfo {
        val totalResults: Int
        val resultsPerPage: Int
        val snippets: TypedDataFrame<Snippet>
    }

    @DataFrameType
    interface DataRecord {
        val kind: String
        val items: TypedDataFrame<Item>
        val pageInfo: TypedDataFrameRow<PageInfo>
    }

    val json = """
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

    val df = TypedDataFrame.fromJsonStr(json)

    val typed = df.typed<DataRecord>()

    @Test
    fun `marker is reused`(){

        val codeGen = CodeGenerator()
        codeGen.generate(DataRecord::class)
        codeGen.generate(typed, :: typed) shouldBe emptyList()
        val generated = codeGen.generate(df, :: df)
        generated.size shouldBe 1
        generated[0].split("\n").size shouldBe 1
    }

    val modified = df.add("new"){4}

    @Test
    fun `marker is implemented`(){

        val codeGen = CodeGenerator()
        codeGen.generate(DataRecord::class)
        val generated = codeGen.generate(modified, ::modified)
        generated.size shouldBe 2
        generated[0].contains(DataRecord::class.simpleName!!) shouldBe true
    }
}