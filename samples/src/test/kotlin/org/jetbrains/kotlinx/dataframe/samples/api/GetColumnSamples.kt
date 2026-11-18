package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumnIndex
import org.jetbrains.kotlinx.dataframe.api.getColumnPath
import org.jetbrains.kotlinx.dataframe.api.getColumnPaths
import org.jetbrains.kotlinx.dataframe.api.getColumnWithPath
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.getFrameColumn
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class GetColumnSamples : DataFrameSampleHelper("getColumn", "api") {
    val df = peopleDf

    @Test
    fun getColumnDf() {
        // SampleStart
        val input = df
        // SampleEnd
        input.saveDfHtmlSample()
    }

    @Test
    fun getColumnByName_properties() {
        // SampleStart
        val column = df.getColumn { age }
        // SampleEnd
        column.saveDfHtmlSample()

        df.getColumn { age }.name() shouldBe "age"
    }

    @Test
    fun getColumnByName_strings() {
        // SampleStart
        df.getColumn("age")
        // SampleEnd

        df.getColumn("age").name() shouldBe "age"
    }

    @Test
    fun getColumnByPath() {
        // SampleStart
        val column = df.getColumn(pathOf("name", "firstName"))
        // SampleEnd
        column.saveDfHtmlSample()

        df.getColumn(pathOf("name", "firstName")).name() shouldBe "firstName"
    }

    @Test
    fun getColumnByIndex() {
        // SampleStart
        val column = df.getColumn(1)
        // SampleEnd
        column.saveDfHtmlSample()

        df.getColumn(1).name() shouldBe "age"
    }

    @Test
    fun getColumnOrNull_properties() {
        // SampleStart
        val column = df.getColumnOrNull { age }
        df.getColumnOrNull(-1)
        // SampleEnd
        column?.saveDfHtmlSample()

        df.getColumnOrNull { age }?.name() shouldBe "age"
        df.getColumnOrNull(-1) shouldBe null
    }

    @Test
    fun getColumnOrNull_strings() {
        // SampleStart
        df.getColumnOrNull("address")
        // SampleEnd

        df.getColumnOrNull("address") shouldBe null
    }

    @Test
    fun getColumnGroupByName_properties() {
        // SampleStart
        val group = df.getColumnGroup { name }
        // SampleEnd
        group.saveDfHtmlSample()

        df.getColumnGroup { name }.name() shouldBe "name"
    }

    @Test
    fun getColumnGroupByName_strings() {
        // SampleStart
        df.getColumnGroup("name")
        // SampleEnd

        df.getColumnGroup("name").name() shouldBe "name"
    }

    @Test
    fun getColumnOrNullAsColumnGroup() {
        // SampleStart
        val group = df.getColumnOrNull("name")?.asColumnGroup()
        df.getColumnOrNull("address")?.asColumnGroup()
        // SampleEnd
        group?.saveDfHtmlSample()

        df.getColumnOrNull("name")?.asColumnGroup()?.name() shouldBe "name"
        df.getColumnOrNull("address")?.asColumnGroup() shouldBe null
        shouldThrow<ClassCastException> { df.getColumnOrNull("age")?.asColumnGroup() }
    }

    @Test
    fun getFrameColumnDf() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame("frame")
        // SampleEnd
        grouped.saveDfHtmlSample()
    }

    @Test
    fun getFrameColumnByName() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame("frame")
        val frameColumn = grouped.getFrameColumn("frame")
        // SampleEnd
        frameColumn.saveDfHtmlSample()

        grouped.getFrameColumn("frame").name() shouldBe "frame"
    }

    @Test
    fun getFrameColumnByPath() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame("frame")
        val frameColumn = grouped.getFrameColumn(pathOf("frame"))
        // SampleEnd
        frameColumn.saveDfHtmlSample()

        grouped.getFrameColumn(pathOf("frame")).name() shouldBe "frame"
    }

    @Test
    fun getFrameColumnByIndex() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame("frame")
        val frameColumn = grouped.getFrameColumn(1)
        // SampleEnd
        frameColumn.saveDfHtmlSample()

        grouped.getFrameColumn(1).name() shouldBe "frame"
    }

    @Test
    fun getFrameColumnBySelector_properties() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame("frame")
        val frameColumn = grouped.getFrameColumn { frame }
        // SampleEnd
        frameColumn.saveDfHtmlSample()

        grouped.getFrameColumn { frame }.name() shouldBe "frame"
    }

    @Test
    fun getFrameColumnBySelector_strings() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame("frame")
        grouped.getFrameColumn { frameCol<Any?>("frame") }
        // SampleEnd

        grouped.getFrameColumn { frameCol<Any?>("frame") }.name() shouldBe "frame"
    }

    @Test
    fun getColumnsByNames_properties() {
        // SampleStart
        val columns = df.getColumns { age and name.firstName }
        // SampleEnd
        columns.toDataFrame().saveDfHtmlSample()

        df.getColumns { age and name.firstName }.map { it.name() } shouldBe listOf("age", "firstName")
    }

    @Test
    fun getColumnsByNames_strings() {
        // SampleStart
        df.getColumns("age", "city")
        // SampleEnd

        df.getColumns("age", "city").map { it.name() } shouldBe listOf("age", "city")
    }

    @Test
    fun getColumnIndex() {
        // SampleStart
        val indices = listOf(
            df.getColumnIndex("city"),
            df.getColumnIndex("address"),
            df.getColumnIndex(df.name.firstName),
        )
        val output = indices.joinToString()
        // SampleEnd
        output.saveTextSample()

        indices shouldBe listOf(2, -1, -1)
    }

    @Test
    fun getColumnPaths_web() {
        // SampleStart
        val singlePath = df.getColumnPath { name.firstName }
        val paths = df.getColumnPaths { name.allCols() }
        val output = "Single path: $singlePath\nAll paths: $paths"
        // SampleEnd
        output.saveTextSample()

        singlePath shouldBe pathOf("name", "firstName")
        paths shouldBe listOf(pathOf("name", "firstName"), pathOf("name", "lastName"))
    }

    @Test
    fun getColumnsWithPaths_web() {
        // SampleStart
        val columnWithPath = df.getColumnWithPath { name.firstName }
        val columnsWithPaths = df.getColumnsWithPaths { age and name.firstName }
        val output = "Single column: ${columnWithPath.name()} at ${columnWithPath.path}\n" +
            "Columns: ${columnsWithPaths.map { it.name() to it.path }}"
        // SampleEnd
        output.saveTextSample()

        columnWithPath.path shouldBe pathOf("name", "firstName")
        columnsWithPaths.map { it.path } shouldBe listOf(pathOf("age"), pathOf("name", "firstName"))
    }

    @Test
    fun getColumn() {
        // SampleStart
        df.getColumn("age") // by the name of a top-level column
        df.getColumn(pathOf("name", "firstName")) // by a path, also to a nested column
        df.getColumn(1) // by the index of a top-level column: "age"
        df.getColumn { name.firstName } // by a column selector, typed: DataColumn<String>
        // SampleEnd
        df.getColumn("age").name() shouldBe "age"
        df.getColumn(pathOf("name", "firstName")).name() shouldBe "firstName"
        df.getColumn(1).name() shouldBe "age"
        df.getColumn { name.firstName }.name() shouldBe "firstName"

        val notFound = shouldThrow<IllegalArgumentException> { df.getColumn("address") }
        notFound.message shouldBe "Column not found: 'address'"
        shouldThrow<IllegalArgumentException> { df.getColumn("firstName") } // not a top-level column
        shouldThrow<IllegalArgumentException> { df.getColumn(pathOf("name", "address")) }
        val outOfBounds = shouldThrow<IllegalArgumentException> { df.getColumn(5) }
        outOfBounds.message shouldBe "Column index is out of bounds: 5. Columns count = 5"
        shouldThrow<IllegalArgumentException> { df.getColumn(-1) }
        shouldThrow<IllegalStateException> { df.getColumn { "address"<String>() } }
    }

    @Test
    fun getColumnGroup() {
        // SampleStart
        df.getColumnGroup("name") // ColumnGroup<*>
        df.getColumnGroup(0) // ColumnGroup<*>: "name"
        df.getColumnGroup(pathOf("name")) // ColumnGroup<*>
        df.getColumnGroup { name } // ColumnGroup<Name>
        df.getColumnOrNull("address")?.asColumnGroup() // null: there is no such column
        // SampleEnd
        df.getColumnGroup("name").name() shouldBe "name"
        df.getColumnGroup(0).name() shouldBe "name"
        df.getColumnGroup(pathOf("name")).name() shouldBe "name"
        df.getColumnGroup { name }.name() shouldBe "name"
        df.getColumnOrNull("address")?.asColumnGroup() shouldBe null

        shouldThrow<IllegalArgumentException> { df.getColumnGroup("address") }
        shouldThrow<IllegalArgumentException> { df.getColumnGroup(5) }
        shouldThrow<ClassCastException> { df.getColumnGroup("age") } // not a column group
    }

    @Test
    fun getFrameColumn() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame("frame") // has the FrameColumn "frame"
        grouped.getFrameColumn("frame") // FrameColumn<*>
        grouped.getFrameColumn(pathOf("frame")) // FrameColumn<*>
        grouped.getFrameColumn(1) // FrameColumn<*>: "frame"
        grouped.getFrameColumn { frameCol<Any?>("frame") } // FrameColumn<Any?>
        // SampleEnd
        grouped.getFrameColumn("frame").name() shouldBe "frame"
        grouped.getFrameColumn(pathOf("frame")).name() shouldBe "frame"
        grouped.getFrameColumn(1).name() shouldBe "frame"
        grouped.getFrameColumn { frameCol<Any?>("frame") }.name() shouldBe "frame"

        shouldThrow<IllegalArgumentException> { grouped.getFrameColumn("group") }
        shouldThrow<IllegalArgumentException> { grouped.getFrameColumn(pathOf("frame", "age")) }
        shouldThrow<IllegalArgumentException> { grouped.getFrameColumn(2) }
        shouldThrow<ClassCastException> { grouped.getFrameColumn(0) } // "city" is not a frame column
        shouldThrow<ClassCastException> { grouped.getFrameColumn("city") } // not a frame column
        shouldThrow<ClassCastException> { df.getFrameColumn(2) } // "city" is not a frame column
    }

    @Test
    fun getColumns() {
        // SampleStart
        df.getColumns { age and name.firstName } // [age, firstName]
        df.getColumns("city", "age") // [city, age]: in the order of the arguments
        df.getColumnIndex(df.city) // 2
        // SampleEnd
        df.getColumns { age and name.firstName }.map { it.name() } shouldBe listOf("age", "firstName")
        df.getColumns("city", "age").map { it.name() } shouldBe listOf("city", "age")
        df.getColumnIndex(df.city) shouldBe 2
        df.getColumnIndex(df.name.firstName) shouldBe -1 // not a top-level column

        shouldThrow<IllegalStateException> { df.getColumns("city", "address") }
    }

    @Test
    fun getColumnPaths() {
        // SampleStart
        df.getColumnPaths { name.allCols() } // [pathOf("name", "firstName"), pathOf("name", "lastName")]
        df.getColumnPath { name.lastName } // pathOf("name", "lastName")
        df.getColumnsWithPaths { age and name.firstName } // [age] and [firstName] with their paths
        df.getColumnWithPath { name.firstName }.path // pathOf("name", "firstName")
        // SampleEnd
        df.getColumnPaths { name.allCols() } shouldBe
            listOf(pathOf("name", "firstName"), pathOf("name", "lastName"))
        df.getColumnPath { name.lastName } shouldBe pathOf("name", "lastName")
        df.getColumnsWithPaths { age and name.firstName }.map { it.path } shouldBe
            listOf(pathOf("age"), pathOf("name", "firstName"))
        df.getColumnWithPath { name.firstName }.path shouldBe pathOf("name", "firstName")

        shouldThrow<IllegalStateException> { df.getColumnPath { "address"<String>() } }
        shouldThrow<IllegalStateException> { df.getColumnWithPath { "address"<String>() } }
        shouldThrow<IllegalStateException> { df.getColumnPaths { "address"<String>() } }
        shouldThrow<IllegalStateException> { df.getColumnsWithPaths { "address"<String>() } }
    }
}
