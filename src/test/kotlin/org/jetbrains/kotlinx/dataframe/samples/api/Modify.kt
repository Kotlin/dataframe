package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dfsOf
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.fillNA
import org.jetbrains.kotlinx.dataframe.api.fillNaNs
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.flatten
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.gt
import org.jetbrains.kotlinx.dataframe.api.implode
import org.jetbrains.kotlinx.dataframe.api.insert
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.intoList
import org.jetbrains.kotlinx.dataframe.api.length
import org.jetbrains.kotlinx.dataframe.api.lowercase
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.mapKeys
import org.jetbrains.kotlinx.dataframe.api.mapValues
import org.jetbrains.kotlinx.dataframe.api.match
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanFor
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.api.perCol
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.pivotCount
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.shuffle
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sortWith
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toFloat
import org.jetbrains.kotlinx.dataframe.api.toLeft
import org.jetbrains.kotlinx.dataframe.api.toMap
import org.jetbrains.kotlinx.dataframe.api.toPath
import org.jetbrains.kotlinx.dataframe.api.toTop
import org.jetbrains.kotlinx.dataframe.api.under
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withNull
import org.jetbrains.kotlinx.dataframe.api.withValue
import org.jetbrains.kotlinx.dataframe.api.withZero
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
        df.update { weight }.at(1..4).notNull { it / 2 }
        df.update { name.lastName and age }.at(1, 3, 4).withNull()
        // SampleEnd
    }

    @Test
    fun updateWith() {
        // SampleStart
        df.update { city }.with { name.firstName + " from " + it }
        // SampleEnd
    }

    @Test
    fun updateWithConst() {
        // SampleStart
        df.update { city }.where { name.firstName == "Alice" }.withValue("Paris")
        // SampleEnd
    }

    @Test
    fun updatePerColumn() {
        val updated =
            // SampleStart
            df.update { numberCols() }.perCol { mean(skipNA = true) }
        // SampleEnd
        updated.age.ndistinct() shouldBe 1
        updated.weight.ndistinct() shouldBe 1

        val means = df.meanFor(skipNA = true) { numberCols() }
        df.update { numberCols() }.perCol(means) shouldBe updated
        df.update { numberCols() }.perCol(means.toMap() as Map<String, Double>) shouldBe updated
    }

    @Test
    fun updatePerRowCol() {
        val updated =
            // SampleStart
            df.update { stringCols() }.perRowCol { row, col -> col.name() + ": " + row.index() }
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
    fun fillNaNs() {
        // SampleStart
        df.fillNaNs { doubleCols() }.withZero()
        // SampleEnd
    }

    @Test
    fun fillNA() {
        // SampleStart
        df.fillNA { weight }.withValue(-1)
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
        df.split { name.firstName }.by { it.chars().toList() }.inplace()

        df.split { name }.by { it.values() }.into("nameParts")

        df.split { name.lastName }.by(" ").default("").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun split_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()

        df.split { firstName }.by { it.chars().toList() }.inplace()

        df.split { name }.by { it.values() }.into("nameParts")

        df.split { lastName }.by(" ").default("").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun split_strings() {
        // SampleStart
        df.split { "name"["firstName"]<String>() }.by { it.chars().toList() }.inplace()

        df.split { name }.by { it.values() }.into("nameParts")

        df.split { "name"["lastName"] }.by(" ").default("").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun splitRegex() {
        val merged = df.merge { name.lastName and name.firstName }.by { it[0] + " (" + it[1] + ")" }.into("name")
        val name by column<String>()
        // SampleStart
        merged.split { name }
            .match("""(.*) \((.*)\)""")
            .inward("firstName", "lastName")
        // SampleEnd
    }

    @Test
    fun splitIntoRows_properties() {
        // SampleStart
        df.split { name.firstName }.by { it.chars().toList() }.intoRows()

        df.split { name }.by { it.values() }.intoRows()
        // SampleEnd
    }

    @Test
    fun splitIntoRows_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()

        df.split { firstName }.by { it.chars().toList() }.intoRows()

        df.split { name }.by { it.values() }.intoRows()
        // SampleEnd
    }

    @Test
    fun splitIntoRows_strings() {
        // SampleStart
        df.split { "name"["firstName"]<String>() }.by { it.chars().toList() }.intoRows()

        df.split { group("name") }.by { it.values() }.intoRows()
        // SampleEnd
    }

    @Test
    fun merge() {
        // SampleStart
        // Merge two columns into one column "fullName"
        df.merge { name.firstName and name.lastName }.by(" ").into("fullName")
        // SampleEnd
    }

    @Test
    fun mergeIntoList() {
        // SampleStart
        // Merge data from two columns into List<String>
        df.merge { name.firstName and name.lastName }.by(",").intoList()
        // SampleEnd
    }

    @Test
    fun mergeSameWith() {
        // SampleStart
        df.merge { name.firstName and name.lastName }
            .by { it[0] + " (" + it[1].uppercase() + ")" }
            .into("fullName")
        // SampleEnd
    }

    @Test
    fun mergeDifferentWith() {
        // SampleStart
        df.merge { name.firstName and age and isHappy }
            .by { "${it[0]} aged ${it[1]} is " + (if (it[2] as Boolean) "" else "not ") + "happy" }
            .into("status")
        // SampleEnd
    }

    @Test
    fun mergeDefault() {
        // SampleStart
        df.merge { numberCols() }.into("data")
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
    fun implode() {
        // SampleStart
        df.implode { name and age and weight and isHappy }
        // SampleEnd
    }

    @Test
    fun gatherNames() {
        val pivoted = df.dropNulls { city }.pivotCount(inward = false) { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.cast<Int>()
            .where { it > 0 }.into("city")
        // SampleEnd
    }

    @Test
    fun gather() {
        val pivoted = df.dropNulls { city }.pivotCount(inward = false) { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.into("city", "population")
        // SampleEnd
    }

    @Test
    fun gatherWithMapping() {
        val pivoted = df.dropNulls { city }.pivotCount(inward = false) { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.cast<Int>()
            .mapKeys { it.lowercase() }
            .mapValues { 1.0 / it }
            .into("city", "density")
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
    fun concatRows() {
        // SampleStart
        val rows = listOf(df[2], df[4], df[5])
        rows.concat()
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
    fun concatGroupBy() {
        // SampleStart
        df.groupBy { name }.concat()
        // SampleEnd
    }

    @Test
    fun add_properties() {
        // SampleStart
        df.add("year of birth") { 2021 - age }
        // SampleEnd
    }

    @Test
    fun add_accessors() {
        // SampleStart
        val age by column<Int>()

        df.add("year of birth") { 2021 - age }
        // SampleEnd
    }

    @Test
    fun add_strings() {
        // SampleStart
        df.add("year of birth") { 2021 - "age"<Int>() }
        // SampleEnd
    }

    @Test
    fun addExisting() {
        // SampleStart
        val score by columnOf(4, 3, 5, 2, 1, 3, 5)

        df.add(score)
        df + score
        // SampleEnd
    }

    @Test
    fun addDataFrame() {
        val otherDf = df.select { name named "name2" }
        // SampleStart
        df.add(otherDf)
        // SampleEnd
    }

    @Test
    fun addMany_properties() {
        // SampleStart
        df.add {
            "year of birth" from 2021 - age
            age gt 18 into "is adult"
            name.lastName.length() into "last name length"
            "full name" from { name.firstName + " " + name.lastName }
        }
        // SampleEnd
    }

    @Test
    fun addMany_accessors() {
        // SampleStart
        val yob = column<Int>("year of birth")
        val lastNameLength = column<Int>("last name length")
        val age by column<Int>()
        val isAdult = column<Boolean>("is adult")
        val fullName = column<String>("full name")
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()

        df.add {
            yob from 2021 - age
            age gt 18 into isAdult
            lastName.length() into lastNameLength
            fullName from { firstName() + " " + lastName() }
        }
        // SampleEnd
    }

    @Test
    fun addMany_strings() {
        // SampleStart
        df.add {
            "year of birth" from 2021 - "age"<Int>()
            "age"<Int>() gt 18 into "is adult"
            "name"["lastName"]<String>().length() into "last name length"
            "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
        }
        // SampleEnd
    }

    @Test
    fun map_properties() {
        // SampleStart
        df.map {
            "year of birth" from 2021 - age
            age gt 18 into "is adult"
            name.lastName.length() into "last name length"
            "full name" from { name.firstName + " " + name.lastName }
            +city
        }
        // SampleEnd
    }

    @Test
    fun map_accessors() {
        // SampleStart
        val yob = column<Int>("year of birth")
        val lastNameLength = column<Int>("last name length")
        val age by column<Int>()
        val isAdult = column<Boolean>("is adult")
        val fullName = column<String>("full name")
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()
        val city by column<String?>()

        df.map {
            yob from 2021 - age
            age gt 18 into isAdult
            lastName.length() into lastNameLength
            fullName from { firstName() + " " + lastName() }
            +city
        }
        // SampleEnd
    }

    @Test
    fun map_strings() {
        // SampleStart
        df.map {
            "year of birth" from 2021 - "age"<Int>()
            "age"<Int>() gt 18 into "is adult"
            "name"["lastName"]<String>().length() into "last name length"
            "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
            +"city"
        }
        // SampleEnd
    }

    @Test
    fun group() {
        // SampleStart
        df.group { age and city }.into("info")

        df.group { all() }.into { it.type().toString() }.print()
        // SampleEnd
    }

    @Test
    fun ungroup() {
        // SampleStart
        // name.firstName -> firstName
        // name.lastName -> lastName
        df.ungroup { name }
        // SampleEnd
    }

    @Test
    fun flatten() {
        // SampleStart
        // name.firstName -> firstName
        // name.lastName -> lastName
        df.flatten { name }
        // SampleEnd
    }

    @Test
    fun flattenAll() {
        // SampleStart
        df.flatten()
        // SampleEnd
    }
}
