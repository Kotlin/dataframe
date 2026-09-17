package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.firstOrNull
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.mapToFrame
import org.jetbrains.kotlinx.dataframe.api.mapToFrames
import org.jetbrains.kotlinx.dataframe.api.mapToRows
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class MapSamples : DataFrameSampleHelper("map", "api") {

    // the first seven people, so that the page, the KDocs and the tests of `map` all show one dataset
    val df = peopleDf.take(7)

    @Test
    fun mapDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun map() {
        // SampleStart
        df.map { 2021 - it.age }
        // SampleEnd
    }

    @Test
    fun mapToColumn_properties() {
        // SampleStart
        df.mapToColumn("year of birth") { 2021 - age }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun mapToColumn_strings() {
        // SampleStart
        df.mapToColumn("year of birth") { 2021 - "age"<Int>() }
        // SampleEnd
    }

    @Test
    fun mapMany_properties() {
        // SampleStart
        df.mapToFrame {
            "year of birth" from { 2021 - age }
            expr { age > 18 } into "is adult"
            name.lastName.map { it.length } into "last name length"
            "full name" from { name.firstName + " " + name.lastName }
            +city
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun mapMany_strings() {
        // SampleStart
        df.mapToFrame {
            "year of birth" from { 2021 - "age"<Int>() }
            expr { "age"<Int>() > 18 } into "is adult"
            "name"["lastName"]<String>().map { it.length } into "last name length"
            "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
            +"city"
        }
        // SampleEnd
    }

    @Test
    fun mapOnColumn() {
        // SampleStart
        // A column of last name lengths; it keeps the name of the original column,
        // so it is renamed here
        df.name.lastName.map { it.length }.rename("lastNameLength")
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun mapIndexedOnColumn() {
        // SampleStart
        // "1. Alice", "2. Bob", ...
        df.name.firstName.mapIndexed { i, firstName -> "${i + 1}. $firstName" }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun mapOnGroupBy() {
        // SampleStart
        // The number of people per city, as a list, in the order of the groups: [1, 1, 2, 1, 1, 1]
        df.groupBy { city }.map { group.rowsCount() }
        // SampleEnd
    }

    @Test
    fun mapToRowsOnGroupBy() {
        // SampleStart
        // The oldest person of every city, one row per city
        df.groupBy { city }.mapToRows { group.sortByDesc { age }.firstOrNull() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun mapToFramesOnGroupBy() {
        // SampleStart
        // The two oldest people for each first name, as a frame column:
        // every frame keeps all the columns of the original, including the first name it was grouped by
        df.groupBy { name.firstName }.mapToFrames { group.sortByDesc { age }.take(2) }
            // SampleEnd
            .saveDfHtmlSample()
    }
}
