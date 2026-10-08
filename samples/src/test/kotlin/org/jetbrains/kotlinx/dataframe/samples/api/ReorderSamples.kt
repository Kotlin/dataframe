package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.byDesc
import org.jetbrains.kotlinx.dataframe.api.byName
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.reorder
import org.jetbrains.kotlinx.dataframe.api.reorderColumnsBy
import org.jetbrains.kotlinx.dataframe.api.reorderColumnsByName
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

// The examples of the `reorder` page and of the `reorder` KDocs: change them together.
@Suppress("ktlint:standard:argument-list-wrapping")
class ReorderSamples : DataFrameSampleHelper("reorder", "api") {

    // [name[firstName, lastName], age, city, weight, isHappy]
    val df = peopleDf.take(7)

    @Test
    fun reorderDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun reorder_properties() {
        // SampleStart
        df.reorder { age..isHappy }.byName()
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("name", "age", "city", "isHappy", "weight")
            }.saveDfHtmlSample()
    }

    @Test
    fun reorder_strings() {
        // SampleStart
        df.reorder { "age".."isHappy" }.byName()
            // SampleEnd
            .columnNames() shouldBe listOf("name", "age", "city", "isHappy", "weight")
    }

    @Test
    fun reorderSome() {
        // SampleStart
        val df = dataFrameOf("c", "d", "a", "b")(
            3, 4, 1, 2,
            1, 1, 1, 1,
        )
        df.reorder("d", "b").cast<Int>().by { sum() }
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("c", "b", "a", "d") }
            .saveDfHtmlSample()
    }

    @Test
    fun reorderByDesc() {
        // SampleStart
        val df = dataFrameOf("c", "d", "a", "b")(
            3, 4, 1, 2,
            1, 1, 1, 1,
        )
        df.reorder("a", "b").cast<Int>().byDesc { sum() }
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("c", "d", "b", "a") }
            .saveDfHtmlSample()
    }

    @Test
    fun reorderEqualKeys_properties() {
        // SampleStart
        df.reorder { age and name and city }.byDesc { it.name().length }
            // SampleEnd
            .also {
                // "name" and "city" have names of the same length, so "name" stays before "city"
                it.columnNames() shouldBe listOf("name", "city", "age", "weight", "isHappy")
            }.saveDfHtmlSample()
    }

    @Test
    fun reorderEqualKeys_strings() {
        // SampleStart
        df.reorder("age", "name", "city").byDesc { it.name().length }
            // SampleEnd
            .columnNames() shouldBe listOf("name", "city", "age", "weight", "isHappy")
    }

    @Test
    fun reorderInDifferentGroups_properties() {
        // SampleStart
        df.reorder { age and city and name.firstName and name.lastName }.byName(desc = true)
            // SampleEnd
            .also {
                // "age" and "city" change places at the top level, "firstName" and "lastName" inside "name"
                it.columnNames() shouldBe listOf("name", "city", "age", "weight", "isHappy")
                it.getColumnGroup("name").columnNames() shouldBe listOf("lastName", "firstName")
            }.saveDfHtmlSample()
    }

    @Test
    fun reorderInDifferentGroups_strings() {
        // SampleStart
        df.reorder { "age" and "city" and "name"["firstName"] and "name"["lastName"] }.byName(desc = true)
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("name", "city", "age", "weight", "isHappy")
                it.getColumnGroup("name").columnNames() shouldBe listOf("lastName", "firstName")
            }
    }

    @Test
    fun reorderInGroup_properties() {
        // SampleStart
        df.reorder { name }.byName(desc = true)
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("name", "age", "city", "weight", "isHappy")
                it.getColumnGroup("name").columnNames() shouldBe listOf("lastName", "firstName")
            }.saveDfHtmlSample()
    }

    @Test
    fun reorderInGroup_strings() {
        // SampleStart
        df.reorder("name").byName(desc = true)
            // SampleEnd
            .getColumnGroup("name").columnNames() shouldBe listOf("lastName", "firstName")
    }

    @Test
    fun reorderColumnsBy() {
        // SampleStart
        df.reorderColumnsBy { name().length }
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("age", "name", "city", "weight", "isHappy")
                it.getColumnGroup("name").columnNames() shouldBe listOf("lastName", "firstName")
            }.saveDfHtmlSample()
    }

    @Test
    fun reorderColumnsByTopLevel() {
        // SampleStart
        df.reorderColumnsBy(atAnyDepth = false) { name().length }
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("age", "name", "city", "weight", "isHappy")
                it.getColumnGroup("name").columnNames() shouldBe listOf("firstName", "lastName")
            }.saveDfHtmlSample()
    }

    @Test
    fun reorderColumnsByName() {
        // SampleStart
        df.reorderColumnsByName(desc = true)
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("weight", "name", "isHappy", "city", "age")
                // with the default atAnyDepth = true, the nested columns of "name" are reordered too
                it.getColumnGroup("name").columnNames() shouldBe listOf("lastName", "firstName")
            }.saveDfHtmlSample()
    }

    @Test
    fun reorderColumnsByNameTopLevel() {
        // SampleStart
        df.reorderColumnsByName(atAnyDepth = false, desc = true)
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("weight", "name", "isHappy", "city", "age")
                it.getColumnGroup("name").columnNames() shouldBe listOf("firstName", "lastName")
            }.saveDfHtmlSample()
    }
}
