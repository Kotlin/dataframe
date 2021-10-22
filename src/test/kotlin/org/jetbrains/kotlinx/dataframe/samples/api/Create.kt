package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.frameColumn
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

class Create : TestBase() {

    @Test
    fun createValueColumn() {
        // SampleStart
        val name by columnOf("Alice", "Bob")
        // or
        listOf("Alice", "Bob").toColumn("name")
        // SampleEnd
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun createValueColumnInferred() {
        // SampleStart
        val values = listOf("Alice", null, 1, 2.5).subList(2, 4)

        values.toColumn("data").type willBe typeOf<Any?>()
        values.toColumn("data", inferType = true).type willBe typeOf<Number>()
        values.toColumn("data", inferNulls = true).type willBe typeOf<Any>()
        values.toColumn("data", inferType = true, inferNulls = false).type willBe typeOf<Number?>()
        values.toColumnOf<Number?>("data").type willBe typeOf<Number?>()
        // SampleEnd
    }

    @Test
    fun createColumnRenamed() {
        // SampleStart
        val column = columnOf("Alice", "Bob") named "name"
        // SampleEnd
    }

    @Test
    fun createColumnGroup() {
        // SampleStart
        val firstName by columnOf("Alice", "Bob")
        val lastName by columnOf("Cooper", "Marley")

        val name by columnOf(firstName, lastName)
        // or
        listOf(firstName, lastName).toColumn("name")
        // SampleEnd
    }

    @Test
    fun createFrameColumn() {
        // SampleStart
        val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
        val df2 = dataFrameOf("name", "temp")("Mark", 36.6)

        val groups by columnOf(df1, df2)
        // or
        listOf(df1, df2).toColumn("groups")

        // SampleEnd
    }

    @Test
    fun createColumnAccessor() {
        // SampleStart
        val name by column<String>()
        // SampleEnd
    }

    @Test
    fun createColumnAccessorRenamed() {
        // SampleStart
        val accessor = column<String>("complex column name")
        // SampleEnd
    }

    @Test
    fun createDeepColumnAccessor() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        // SampleEnd
    }

    @Test
    fun createGroupOrFrameColumnAccessor() {
        // SampleStart
        val columns by columnGroup()
        val frames by frameColumn()
        // SampleEnd
    }
}
