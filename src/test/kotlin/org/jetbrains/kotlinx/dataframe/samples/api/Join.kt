package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.junit.Test

class Join : TestBase() {

    @Test
    fun join() {
        val other = df.add("year") { 2021 - age }.select { name and city and "year" }
        // SampleStart
        df.join(other) { name and city }
        // SampleEnd
    }

    @Test
    fun joinDefault() {
        val other = df.add("year") { 2021 - age }.select { name and city and "year" }
        // SampleStart
        df.join(other)
        // SampleEnd
    }

    class Right
    val DataFrame<Right>.fullName: ColumnGroup<Name> get() = getColumnGroup("fullName").cast()

    @Test
    fun joinWithMatch_properties() {
        val other = df.add("year") { 2021 - age }.select { name named "fullName" and "year" }.cast<Right>()
        val joined =
            // SampleStart
            df.join(other) { name match right.fullName }
        // SampleEnd
        joined.nrow() shouldBe df.nrow()
        joined.ncol() shouldBe df.ncol() + 1
    }

    @Test
    fun joinWithMatch_accessors() {
        val other = df.add("year") { 2021 - age }.select { name named "fullName" and "year" }
        // SampleStart
        val name by columnGroup()
        val fullName by columnGroup()

        df.join(other) { name match fullName }
        // SampleEnd
    }

    @Test
    fun joinWithMatch_strings() {
        val other = df.add("year") { 2021 - age }.select { name named "fullName" and "year" }
        // SampleStart
        df.join(other) { "name" match "fullName" }
        // SampleEnd
    }
}
