package org.jetbrains.kotlinx.dataframe

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.mapNotNullGroups
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.withValues
import org.jetbrains.kotlinx.dataframe.codeGen.generateCode
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.junit.Test

class GatherTests {

    //region Data Source

    @Language("json")
    val df = """
            [
                {
                    "name": "abc",
                    "normal": {
                        "c1": "a",
                        "c2": "b",
                        "c3": "c"
                    },
                    "reversed": {
                        "c1": "c",
                        "c2": "b",
                        "c3": "a"
                    },
                    "first": {
                        "c1": "c"
                    }
                },
                {
                    "name": "qw",
                    "normal": {
                        "c1": "q",
                        "c2": "w"
                    },
                    "reversed": {
                        "c1": "w",
                        "c2": "q"
                    },
                    "first": {
                        "c1": "q"
                    }
                }
            ]
        """.let {
        DataFrame.readJsonStr(it)
    }

    //endregion

    val generatedCode = df.generateCode("Marker")

    //region Generated code

    @DataSchema(isOpen = false)
    interface Marker1 {
        val c1: String
        val c2: String
        val c3: String?
    }

    @DataSchema(isOpen = false)
    interface Marker2 {
        val c1: String
        val c2: String
        val c3: String?
    }

    @DataSchema(isOpen = false)
    interface Marker3 {
        val c1: String
    }

    @DataSchema
    interface Marker {
        val name: String
        val normal: DataRow<Marker1>
        val reversed: DataRow<Marker2>
        val first: DataRow<Marker3>
    }

    //endregion

    val typed = df.cast<Marker>()

    @Test
    fun gather() {
        val mode by column<String>()
        val gathered = typed.gather { except(name) }.into(mode)

        val expected = typed.groupBy { name }.mapNotNullGroups {
            val cols = columns().drop(1).map { it.asColumnGroup() } // drop 'name' column
            val dataRows = cols.map { it[0] }

            val newDf = listOf(
                name.withValues(List(cols.size) { name[0] }),
                mode.withValues(cols.map { it.name }),
                dataRows.map { it.tryGet("c1") as? String }.toColumn("c1", inferType = true),
                dataRows.map { it.tryGet("c2") as? String }.toColumn("c2", inferType = true),
                column("c3", dataRows.map { it.tryGet("c3") as? String })
            ).toDataFrame()

            newDf
        }.union()

        gathered shouldBe expected
    }

    @Test
    fun `generated code is fully typed`() {
        generatedCode.contains("<*>") shouldBe false
    }
}
