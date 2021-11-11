package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dfsOf
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.length
import org.jetbrains.kotlinx.dataframe.api.lowercase
import org.jetbrains.kotlinx.dataframe.api.mapKeys
import org.jetbrains.kotlinx.dataframe.api.mapValues
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.mergeRows
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.movingAverage
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.nullToZero
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.api.pivotCount
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.shuffle
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sortWith
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toFloat
import org.jetbrains.kotlinx.dataframe.api.toLeft
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.api.toTop
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withNull
import org.jetbrains.kotlinx.dataframe.api.withValue
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.pathOf
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.streams.toList

class Modify : TestBase() {

    @Test
    fun update() {
        // SampleStart
        df.update { age }.with { it * 2 }
        df.update { dfsOf<String>() }.with { it.uppercase() }
        df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
        df.update { weight }.at(1..4).notNull { it / 2 }
        df.update { name.lastName and age }.at(1, 3, 4).withNull()
        df.update { age }.with { movingAverage(2) { age }.toInt() }
        // SampleEnd
    }

    @Test
    fun convert() {
        // SampleStart
        df.convert { age }.with { it.toDouble() }
        df.convert { dfsOf<String>() }.with { it.toCharArray().toList() }
        // SampleEnd
    }

    @Test
    fun convertTo() {
        // SampleStart
        df.convert { age }.to<Double>()
        df.convert { numberCols() }.to<String>()
        df.convert { name.firstName and name.lastName }.to { it.length() }
        df.convert { weight }.toFloat()
        // SampleEnd
    }

    @Test
    fun parseAll() {
        // SampleStart
        df.parse()
        // SampleEnd
    }

    @Test
    fun parseSome() {
        // SampleStart
        df.parse { age and weight }
        // SampleEnd
    }

    @Test
    fun parseWithOptions() {
        // SampleStart
        df.parse(options = ParserOptions(locale = Locale.CHINA, dateTimeFormatter = DateTimeFormatter.ISO_WEEK_DATE))
        // SampleEnd
    }

    @Test
    fun globalParserOptions() {
        // SampleStart
        DataFrame.parser.locale = Locale.FRANCE
        DataFrame.parser.addDateTimeFormat("dd.MM.uuuu HH:mm:ss")
        // SampleEnd
    }

    @Test
    fun replace() {
        // SampleStart
        df.replace { name }.with { name.firstName }
        df.replace { stringCols() }.with { it.lowercase() }
        df.replace { age }.with { 2021 - age named "year" }
        // SampleEnd
    }

    @Test
    fun shuffle() {
        // SampleStart
        df.shuffle()
        // SampleEnd
    }

    @Test
    fun fillNulls() {
        // SampleStart
        df.fillNulls { intCols() }.with { -1 }
        // same as
        df.update { intCols() }.where { it == null }.with { -1 }
        // SampleEnd
    }

    @Test
    fun nullToZero() {
        // SampleStart
        df.nullToZero { weight }
        // SampleEnd
    }

    @Test
    fun move() {
        // SampleStart
        df.move { age }.toLeft()

        df.move { weight }.to(1)

        // age -> info.age
        // weight -> info.weight
        df.move { age and weight }.into { pathOf("info", it.name) }
        df.move { age and weight }.into { "info"[it.name] }
        df.move { age and weight }.under("info")

        // name.firstName -> fullName.first
        // name.lastName -> fullName.last
        df.move { name.firstName and name.lastName }.into { pathOf("fullName", it.name.dropLast(4)) }

        // a|b|c -> a.b.c
        // a|d|e -> a.d.e
        dataFrameOf("a|b|c", "a|d|e")(0, 0)
            .move { all() }.into { it.name.split("|").toPath() }

        // name.firstName -> firstName
        // name.lastName -> lastName
        df.move { name.cols() }.toTop()

        // a.b.e -> be
        // c.d.e -> de
        df.move { dfs { it.name == "e" } }.toTop { it.parent!!.name + it.name }
        // SampleEnd
    }

    @Test
    fun sortBy_properties() {
        // SampleStart
        df.sortBy { age }
        df.sortBy { age and name.firstName.desc }
        df.sortBy { weight.nullsLast }
        // SampleEnd
    }

    @Test
    fun sortBy_accessors() {
        // SampleStart
        val age by column<Int>()
        val weight by column<Int?>()
        val name by columnGroup()
        val firstName by name.column<String>()

        df.sortBy { age }
        df.sortBy { age and firstName }
        df.sortBy { weight.nullsLast }
        // SampleEnd
    }

    @Test
    fun sortBy_strings() {
        // SampleStart
        df.sortBy("age")
        df.sortBy { "age" and "name"["firstName"].desc }
        df.sortBy { "weight".nullsLast }
        // SampleEnd
    }

    @Test
    fun sortByDesc_properties() {
        // SampleStart
        df.sortByDesc { age and weight }
        // SampleEnd
    }

    @Test
    fun sortByDesc_accessors() {
        // SampleStart
        val age by column<Int>()
        val weight by column<Int?>()

        df.sortByDesc { age and weight }
        // SampleEnd
    }

    @Test
    fun sortByDesc_strings() {
        // SampleStart
        df.sortByDesc("age", "weight")
        // SampleEnd
    }

    @Test
    fun sortWith() {
        // SampleStart
        df.sortWith { row1, row2 ->
            when {
                row1.age < row2.age -> -1
                row1.age > row2.age -> 1
                else -> row1.name.firstName.compareTo(row2.name.firstName)
            }
        }
        // SampleEnd
    }

    @Test
    fun split_properties() {
        // SampleStart
        df.split { name.firstName }.with { it.chars().toList() }.inplace()

        df.split { name }.with { it.values() }.into("nameParts")

        df.split { name.lastName }.by(" ").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun split_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()

        df.split { firstName }.with { it.chars().toList() }.inplace()

        df.split { name }.with { it.values() }.into("nameParts")

        df.split { lastName }.by(" ").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun split_strings() {
        // SampleStart
        df.split { "name"["firstName"]<String>() }.with { it.chars().toList() }.inplace()

        df.split { name }.with { it.values() }.into("nameParts")

        df.split { "name"["lastName"] }.by(" ").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun splitRegex() {
        val merged = df.merge { name.lastName and name.firstName }.with { it[0] + " (" + it[1] + ")" }.into("name")
        val name by column<String>()
        // SampleStart
        merged.split { name }.with("""(.*) \((.*)\)""".toRegex()).inward("firstName", "lastName")
        // SampleEnd
    }

    @Test
    fun splitIntoRows_properties() {
        // SampleStart
        df.split { name.firstName }.with { it.chars().toList() }.intoRows()

        df.split { name }.with { it.values() }.intoRows()
        // SampleEnd
    }

    @Test
    fun splitIntoRows_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()

        df.split { firstName }.with { it.chars().toList() }.intoRows()

        df.split { name }.with { it.values() }.intoRows()
        // SampleEnd
    }

    @Test
    fun splitIntoRows_strings() {
        // SampleStart
        df.split { "name"["firstName"]<String>() }.with { it.chars().toList() }.intoRows()

        df.split { group("name") }.with { it.values() }.intoRows()
        // SampleEnd
    }

    @Test
    fun explode() {
        // SampleStart
        val df2 = df.convert { age }.with { (1..it step 4).toList() }

        df2.explode { age }
        // SampleEnd
    }

    @Test
    fun mergeRows() {
        // SampleStart
        df.mergeRows { name and age and weight and isHappy }
        // SampleEnd
    }

    @Test
    fun gatherNames() {
        val pivoted = df.dropNulls { city }.pivotCount { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.cast<Int>().where { it > 0 }.into("city")
        // SampleEnd
    }

    @Test
    fun gather() {
        val pivoted = df.dropNulls { city }.pivotCount { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.into("city", "population")
        // SampleEnd
    }

    @Test
    fun gatherWithMapping() {
        val pivoted = df.dropNulls { city }.pivotCount { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.cast<Int>().mapKeys { it.lowercase() }.mapValues { 1.0 / it }.into("city", "density")
        // SampleEnd
    }

    @Test
    fun insert_properties() {
        // SampleStart
        df.insert("year of birth") { 2021 - age }.after { age }
        // SampleEnd
    }

    @Test
    fun insert_accessors() {
        // SampleStart
        val year = column<Int>("year of birth")
        val age by column<Int>()

        df.insert(year) { 2021 - age }.after { age }
        // SampleEnd
    }

    @Test
    fun insert_strings() {
        // SampleStart
        df.insert("year of birth") { 2021 - "age"<Int>() }.after("age")
        // SampleEnd
    }

    @Test
    fun insertColumn() {
        // SampleStart
        val score by columnOf(4, 5, 3, 5, 4, 5, 3)
        df.insert(score).at(2)
        // SampleEnd
    }

    @Test
    fun concat() {
        val otherDf = df
        // SampleStart
        df.concat(otherDf)
        // SampleEnd
    }

    @Test
    fun concatIterable() {
        // SampleStart
        listOf(df[0..1], df[4..5]).concat()
        // SampleEnd
    }

    @Test
    fun concatFrameColumn() {
        // SampleStart
        val frameColumn by columnOf(df[0..1], df[4..5])
        frameColumn.concat()
        // SampleEnd
    }

    @Test
    fun concatGroupedDataFrame() {
        // SampleStart
        df.groupBy { name }.concat()
        // SampleEnd
    }
}
