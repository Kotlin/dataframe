package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
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
        // SampleEnd
        column!!.saveDfHtmlSample()
        column.name() shouldBe "age"
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
    fun getColumnGroupByPath() {
        // SampleStart
        val group = df.getColumnGroup(pathOf("name"))
        // SampleEnd

        group.name() shouldBe "name"
    }

    @Test
    fun getColumnGroupByIndex() {
        // SampleStart
        val group = df.getColumnGroup(0)
        // SampleEnd

        group.name() shouldBe "name"
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
        val columnsTopLevel = df.getColumns("age", "city") // top-level only
        val columns = df.getColumns { "age" and "name"["firstName"] } // nested
        // SampleEnd

        columnsTopLevel.map { it.name() } shouldBe listOf("age", "city")
        columns.map { it.name() } shouldBe listOf("age", "firstName")
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
    fun getColumnIndexByColumn() {
        // SampleStart
        val index = df.getColumnIndex(df.city)
        // SampleEnd

        index shouldBe 2
    }

    @Test
    fun getColumnPathBySelector() {
        // SampleStart
        val path = df.getColumnPath { name.firstName }
        // SampleEnd

        path shouldBe pathOf("name", "firstName")
    }

    @Test
    fun getColumnPathsBySelector() {
        // SampleStart
        val paths = df.getColumnPaths { name.allCols() }
        // SampleEnd

        paths shouldBe listOf(pathOf("name", "firstName"), pathOf("name", "lastName"))
    }

    @Test
    fun getColumnWithPathBySelector() {
        // SampleStart
        val columnWithPath = df.getColumnWithPath { name.firstName }
        // SampleEnd

        columnWithPath.path shouldBe pathOf("name", "firstName")
    }

    @Test
    fun getColumnsWithPathsBySelector() {
        // SampleStart
        val columnsWithPaths = df.getColumnsWithPaths { age and name.firstName }
        // SampleEnd

        columnsWithPaths.map { it.path } shouldBe listOf(pathOf("age"), pathOf("name", "firstName"))
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
}
