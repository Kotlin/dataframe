package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.allNulls
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.junit.Test
import kotlin.reflect.typeOf

class ReadTests {

    @Test
    fun readJsonNulls() {
        val data = """
            [{"a":null, "b":1},{"a":null, "b":2}]
        """.trimIndent()

        val df = DataFrame.readJsonStr(data)
        df.ncol shouldBe 2
        df.nrow shouldBe 2
        df["a"].hasNulls() shouldBe true
        df["a"].allNulls() shouldBe true
        df.all { it["a"] == null } shouldBe true
        df["a"].type() shouldBe typeOf<Any?>()
        df["b"].hasNulls() shouldBe false
    }

    @Test
    fun readFrameColumn() {
        val data = """
            [{"a":[{"b":[]}]},{"a":[]},{"a":[{"b":[{"c":1}]}]}]
        """.trimIndent()
        val df = DataFrame.readJsonStr(data)
        df.nrow shouldBe 3
        val a = df["a"].asAnyFrameColumn()
        a[1].nrow shouldBe 0
        a[0].nrow shouldBe 1
        a[2].nrow shouldBe 1
        val schema = a.schema.value
        schema.columns.size shouldBe 1
        val schema2 = schema.columns["b"] as ColumnSchema.Frame
        schema2.schema.columns.size shouldBe 1
        schema2.schema.columns["c"]!!.kind shouldBe ColumnKind.Value
    }

    @Test
    fun readFrameColumnEmptySlice() {
        val data = """
            [ [], [ {"a": [{"q":2},{"q":3}] } ] ]
        """.trimIndent()

        val df = DataFrame.readJsonStr(data)
        df.nrow shouldBe 2
        df.ncol shouldBe 1
        val empty = df[0][0] as AnyFrame
        empty.nrow shouldBe 0
        empty.ncol shouldBe 0
    }

    @Test
    fun `read big decimal`() {
        val data = """
            [[3452345234345, 7795.34000000], [12314123532, 7795.34000000]]
        """.trimIndent()
        val df = DataFrame.readJsonStr(data)
        println(df.getColumn("array").cast<List<Number>>()[0][1].javaClass)
    }

    @Test
    fun `array of arrays`() {
        val data = """
            {
                "values": [[1,2,3],[4,5,6],[7,8,9]]
            }
        """.trimIndent()
        val df = DataFrame.readJsonStr(data)
        val values by column<List<List<Int>>>()
        df[values][0][1][1] shouldBe 5
    }

    @Test
    fun `read json with header`() {
        val data = """
            [[1,2,3],
            [4,5,6]]
        """.trimIndent()
        val header = listOf("a", "b", "c")
        val df = DataFrame.readJsonStr(data, header)
        df.rowsCount() shouldBe 2
        df.columnsCount() shouldBe 3
        df.columnNames() shouldBe header
        df.columnTypes() shouldBe List(3) { typeOf<Int>() }
    }
}
