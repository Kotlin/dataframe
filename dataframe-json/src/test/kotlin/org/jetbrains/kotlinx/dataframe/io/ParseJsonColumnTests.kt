package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asFrameColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import kotlin.reflect.typeOf
import kotlin.test.Test

/**
 * Tests that strings containing JSON in a [String] column can be parsed via [parse],
 * where JSON arrays become [DataFrame]s (forming a [FrameColumn])
 * and JSON objects become [DataRow]s (forming a [ColumnGroup]).
 */
class ParseJsonColumnTests {

    companion object {
        @[BeforeAll JvmStatic]
        fun `setup ParserOptions`() {
            DataFrame.parser.parseToDataFrameReadSource = true
        }

        @[AfterAll JvmStatic]
        fun `reset ParserOptions`() {
            DataFrame.parser.resetToDefault()
        }
    }

    @Test
    fun `parse column of json arrays into FrameColumn`() {
        @Language("json")
        val a = """[1, 2, 3]"""

        @Language("json")
        val b = """[4, 5, 6]"""

        val col = columnOf(a, b)
        val parsed = col.parse()

        parsed.isFrameColumn() shouldBe true
        val frameCol = parsed.asFrameColumn()
        frameCol.size() shouldBe 2
        frameCol[0]["value"].values().toList() shouldBe listOf(1, 2, 3)
        frameCol[1]["value"].values().toList() shouldBe listOf(4, 5, 6)
    }

    @Test
    fun `parse column of json objects into ColumnGroup`() {
        @Language("json")
        val a = """{"x": 1, "y": "a"}"""

        @Language("json")
        val b = """{"x": 2, "y": "b"}"""

        val col = columnOf(a, b)
        val parsed = col.parse()

        parsed.isColumnGroup() shouldBe true
        val group = parsed.asColumnGroup()
        group.columnsCount() shouldBe 2
        group["x"].type() shouldBe typeOf<Int>()
        group["y"].type() shouldBe typeOf<String>()
        group["x"].values().toList() shouldBe listOf(1, 2)
        group["y"].values().toList() shouldBe listOf("a", "b")
    }

    @Test
    fun `parse dataframe column of json arrays into FrameColumn`() {
        @Language("json")
        val a = """[10, 20]"""

        @Language("json")
        val b = """[30, 40, 50]"""

        val df = dataFrameOf("data")(a, b)
        val parsed = df.parse("data")

        parsed.rowsCount() shouldBe 2
        parsed["data"].isFrameColumn() shouldBe true
        val frameCol = parsed.getFrameColumn("data")
        frameCol[0]["value"].values().toList() shouldBe listOf(10, 20)
        frameCol[1]["value"].values().toList() shouldBe listOf(30, 40, 50)
    }

    @Test
    fun `parse dataframe column of json objects into ColumnGroup`() {
        @Language("json")
        val a = """{"name": "Alice", "age": 30}"""

        @Language("json")
        val b = """{"name": "Bob", "age": 25}"""

        val df = dataFrameOf("person")(a, b)
        val parsed = df.parse("person")

        parsed.rowsCount() shouldBe 2
        parsed["person"].isColumnGroup() shouldBe true
        val group = parsed.getColumnGroup("person")
        group.columnsCount() shouldBe 2
        group["name"].values().toList() shouldBe listOf("Alice", "Bob")
        group["age"].values().toList() shouldBe listOf(30, 25)
        group["name"].type() shouldBe typeOf<String>()
        group["age"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `parse column of json arrays of objects`() {
        @Language("json")
        val a = """[{"k": 1}, {"k": 2}]"""

        @Language("json")
        val b = """[{"k": 3}, {"k": 4}, {"k": 5}]"""

        val parsed = columnOf(a, b).parse()

        parsed.isFrameColumn() shouldBe true
        val frameCol = parsed.asFrameColumn()
        frameCol.size() shouldBe 2
        frameCol[0]["k"].values().toList() shouldBe listOf(1, 2)
        frameCol[1]["k"].values().toList() shouldBe listOf(3, 4, 5)
    }

    @Test
    fun `parse column of nested json objects`() {
        @Language("json")
        val a = """{"outer": {"inner": 1}}"""

        @Language("json")
        val b = """{"outer": {"inner": 2}}"""

        val parsed = columnOf(a, b).parse()

        parsed.isColumnGroup() shouldBe true
        val outer = parsed.asColumnGroup().getColumnGroup("outer")
        outer["inner"].type() shouldBe typeOf<Int>()
        outer["inner"].values().toList() shouldBe listOf(1, 2)
    }

    @Test
    fun `parse column of json objects containing arrays`() {
        @Language("json")
        val a = """{"name": "list1", "values": [1, 2, 3]}"""

        @Language("json")
        val b = """{"name": "list2", "values": [4, 5]}"""

        val parsed = columnOf(a, b).parse()

        parsed.isColumnGroup() shouldBe true
        val group = parsed.asColumnGroup()
        group["name"].values().toList() shouldBe listOf("list1", "list2")
        group["values"].type() shouldBe typeOf<List<Int>>()
        group["values"].values().toList() shouldBe listOf(listOf(1, 2, 3), listOf(4, 5))
    }

    @Test
    fun `parse column of json arrays with whitespace`() {
        val col = columnOf("  [1, 2, 3]  ", "\n[4, 5]\t")
        val parsed = col.parse()

        parsed.isFrameColumn() shouldBe true
        val frameCol = parsed.asFrameColumn()
        frameCol.size() shouldBe 2
        frameCol[0]["value"].values().toList() shouldBe listOf(1, 2, 3)
        frameCol[1]["value"].values().toList() shouldBe listOf(4, 5)
    }

    @Test
    fun `parse dataframe with multiple json columns`() {
        @Language("json")
        val obj1 = """{"a": 1}"""

        @Language("json")
        val obj2 = """{"a": 2}"""

        @Language("json")
        val arr1 = """[1, 2]"""

        @Language("json")
        val arr2 = """[3, 4]"""

        val df = dataFrameOf("obj", "arr")(
            obj1,
            arr1,
            obj2,
            arr2,
        )
        val parsed = df.parse()

        parsed.rowsCount() shouldBe 2
        parsed["obj"].isColumnGroup() shouldBe true
        parsed["arr"].isFrameColumn() shouldBe true

        val objGroup = parsed.getColumnGroup("obj")
        objGroup["a"].values().toList() shouldBe listOf(1, 2)

        val arrFrame = parsed.getFrameColumn("arr")
        arrFrame[0]["value"].values().toList() shouldBe listOf(1, 2)
        arrFrame[1]["value"].values().toList() shouldBe listOf(3, 4)
    }
}
