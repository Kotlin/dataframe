package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.excludeJoin
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.fullJoin
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.junit.Test

class Join : TestBase() {

    private val other = df.add("year") { 2021 - age }.select { name and city and "year" }

    @Test
    fun join_properties() {
        // SampleStart
        df.join(other) { name and city }
        // SampleEnd
    }

    @Test
    fun join_accessors() {
        // SampleStart
        val name by columnGroup()
        val city by column<String>()

        df.join(other) { name and city }
        // SampleEnd
    }

    @Test
    fun join_strings() {
        // SampleStart
        df.join(other, "name", "city")
        // SampleEnd
    }

    @Test
    fun joinDefault() {
        // SampleStart
        df.join(other)
        // SampleEnd
    }

    class Right
    val DataFrame<Right>.fullName: ColumnGroup<Name> get() = getColumnGroup("fullName").cast()

    @Test
    fun joinWithMatch_properties() {
        val other = other.rename { name }.into("fullName").cast<Right>()
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
    
    @Test
    fun joinSpecial_properties() {
        // SampleStart
        df.innerJoin(other) { name and city }
        df.leftJoin(other) { name and city }
        df.rightJoin(other) { name and city }
        df.fullJoin(other) { name and city }
        df.excludeJoin(other) { name and city }
        // SampleEnd
    }

    @Test
    fun joinSpecial_accessors() {
        // SampleStart
        val name by columnGroup()
        val city by column<String>()

        df.innerJoin(other) { name and city }
        df.leftJoin(other) { name and city }
        df.rightJoin(other) { name and city }
        df.fullJoin(other) { name and city }
        df.excludeJoin(other) { name and city }
        // SampleEnd
    }

    @Test
    fun joinSpecial_strings() {
        // SampleStart
        df.innerJoin(other, "name", "city")
        df.leftJoin(other, "name", "city")
        df.rightJoin(other, "name", "city")
        df.fullJoin(other, "name", "city")
        df.excludeJoin(other, "name", "city")
        // SampleEnd
    }
}
