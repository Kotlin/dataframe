package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumnGroupOrNull
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
        df.getColumnGroupOrNull("address") // null: there is no such column
        // SampleEnd
        df.getColumnGroup("name").name() shouldBe "name"
        df.getColumnGroup(0).name() shouldBe "name"
        df.getColumnGroup(pathOf("name")).name() shouldBe "name"
        df.getColumnGroup { name }.name() shouldBe "name"
        df.getColumnGroupOrNull("name")?.name() shouldBe "name"
        df.getColumnGroupOrNull("address") shouldBe null

        shouldThrow<IllegalArgumentException> { df.getColumnGroup("address") }
        shouldThrow<IllegalArgumentException> { df.getColumnGroup(5) }
        shouldThrow<ClassCastException> { df.getColumnGroup("age") } // not a column group
        shouldThrow<ClassCastException> { df.getColumnGroupOrNull("age") }
    }

    @Test
    fun getFrameColumn() {
        // SampleStart
        val grouped = df.groupBy { city }.toDataFrame() // has the FrameColumn "group"
        grouped.getFrameColumn("group") // FrameColumn<*>
        grouped.getFrameColumn(pathOf("group")) // FrameColumn<*>
        // SampleEnd
        grouped.getFrameColumn("group").name() shouldBe "group"
        grouped.getFrameColumn(pathOf("group")).name() shouldBe "group"

        shouldThrow<IllegalArgumentException> { grouped.getFrameColumn("groups") }
        shouldThrow<IllegalArgumentException> { grouped.getFrameColumn(pathOf("group", "age")) }
        shouldThrow<ClassCastException> { grouped.getFrameColumn("city") } // not a frame column
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
