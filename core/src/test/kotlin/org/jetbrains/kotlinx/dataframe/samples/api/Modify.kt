package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.*
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
    fun updateAsFrame() {
        val res =
            // SampleStart
            df.update { name }.asFrame { select { lastName } }
        // SampleEnd
        res shouldBe df.remove { name.firstName }
    }

    @Test
    fun updatePerColumn() {
        val updated =
            // SampleStart
            df.update { colsOf<Number?>() }.perCol { mean(skipNA = true) }
        // SampleEnd
        updated.age.countDistinct() shouldBe 1
        updated.weight.countDistinct() shouldBe 1

        val means = df.meanFor(skipNA = true) { colsOf() }
        df.update { colsOf<Number?>() }.perCol(means) shouldBe updated
        df.update { colsOf<Number?>() }.perCol(means.toMap() as Map<String, Double>) shouldBe updated
    }

    @Test
    fun updatePerRowCol() {
        val updated =
            // SampleStart
            df.update { colsOf<String?>() }.perRowCol { row, col -> col.name() + ": " + row.index() }
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
        df.convert { colsOf<Number>() }.to<String>()
        df.convert { name.firstName and name.lastName }.to { it.length() }
        df.convert { weight }.toFloat()
        // SampleEnd
    }

    enum class Direction {
        NORTH, SOUTH, WEST, EAST
    }

    @Test
    fun convertToEnum() {
        // SampleStart
        dataFrameOf("direction")("NORTH", "WEST")
            .convert("direction").to<Direction>()
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
        DataFrame.parser.addDateTimePattern("dd.MM.uuuu HH:mm:ss")
        // SampleEnd
        DataFrame.parser.resetToDefault()
    }

    @Test
    fun replace() {
        // SampleStart
        df.replace { name }.with { name.firstName }
        df.replace { colsOf<String?>() }.with { it.lowercase() }
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
    fun reverse() {
        // SampleStart
        df.reverse()
        // SampleEnd
    }

    @Test
    fun fillNulls() {
        // SampleStart
        df.fillNulls { colsOf<Int?>() }.with { -1 }
        // same as
        df.update { colsOf<Int?>() }.where { it == null }.with { -1 }
        // SampleEnd
    }

    @Test
    fun fillNaNs() {
        // SampleStart
        df.fillNaNs { colsOf<Double>() }.withZero()
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
        df.move { age and weight }.into { pathOf("info", it.name()) }
        df.move { age and weight }.into { "info"[it.name()] }
        df.move { age and weight }.under("info")

        // name.firstName -> fullName.first
        // name.lastName -> fullName.last
        df.move { name.firstName and name.lastName }.into { pathOf("fullName", it.name().dropLast(4)) }

        // a|b|c -> a.b.c
        // a|d|e -> a.d.e
        dataFrameOf("a|b|c", "a|d|e")(0, 0)
            .move { all() }.into { it.name().split("|").toPath() }

        // name.firstName -> firstName
        // name.lastName -> lastName
        df.move { name.cols() }.toTop()

        // a.b.e -> be
        // c.d.e -> de
        df.move { dfs { it.name() == "e" } }.toTop { it.parentName + it.name() }
        // SampleEnd
    }

    @Test
    fun sortBy_properties() {
        // SampleStart
        df.sortBy { age }
        df.sortBy { age and name.firstName.desc() }
        df.sortBy { weight.nullsLast() }
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
        df.sortBy { weight.nullsLast() }
        // SampleEnd
    }

    @Test
    fun sortBy_strings() {
        // SampleStart
        df.sortBy("age")
        df.sortBy { "age" and "name"["firstName"].desc() }
        df.sortBy { "weight".nullsLast() }
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
    fun reorder_properties() {
        // SampleStart
        df.reorder { age..isHappy }.byName()
        // SampleEnd
    }

    @Test
    fun reorder_accessors() {
        // SampleStart
        val age by column<Int>()
        val isHappy by column<Boolean>()

        df.reorder { age..isHappy }.byName()
        // SampleEnd
    }

    @Test
    fun reorder_strings() {
        // SampleStart
        df.reorder { "age".."isHappy" }.byName()
    }

    @Test
    fun reorderSome() {
        // SampleStart
        val df = dataFrameOf("c", "d", "a", "b")(
            3, 4, 1, 2,
            1, 1, 1, 1
        )
        df.reorder("d", "b").cast<Int>().by { sum() } // [c, b, a, d]
            // SampleEnd
            .columnNames() shouldBe listOf("c", "b", "a", "d")
        // SampleEnd
    }

    @Test
    fun reorderInGroup() {
        // SampleStart
        df.reorder { name }.byName(desc = true) // [name.lastName, name.firstName]
            // SampleEnd
            .name.columnNames() shouldBe listOf("lastName", "firstName")
    }

    @Test
    fun splitInplace_properties() {
        // SampleStart
        df.split { name.firstName }.by { it.chars().toList() }.inplace()
        // SampleEnd
    }

    @Test
    fun splitInplace_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()

        df.split { firstName }.by { it.chars().toList() }.inplace()
        // SampleEnd
    }

    @Test
    fun splitInplace_strings() {
        // SampleStart
        df.split { "name"["firstName"]<String>() }.by { it.chars().toList() }.inplace()
        // SampleEnd
    }

    @Test
    fun split_properties() {
        // SampleStart
        df.split { name }.by { it.values() }.into("nameParts")

        df.split { name.lastName }.by(" ").default("").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun split_accessors() {
        // SampleStart
        val name by columnGroup()
        val lastName by name.column<String>()

        df.split { name }.by { it.values() }.into("nameParts")

        df.split { lastName }.by(" ").default("").inward { "word$it" }
        // SampleEnd
    }

    @Test
    fun split_strings() {
        // SampleStart
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
    fun splitFrameColumn() {
        // SampleStart
        val df1 = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            4, 5, 6
        )
        val df2 = dataFrameOf("a", "b")(
            5, 6,
            7, 8,
            9, 10
        )
        val group by columnOf(df1, df2)
        val id by columnOf("x", "y")
        val df = dataFrameOf(id, group)

        df.split { group }.intoColumns()
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
        df.merge { colsOf<Number>() }.into("data")
        // SampleEnd
    }

    @Test
    fun explode_accessors() {
        // SampleStart
        val a by columnOf(1, 2)
        val b by columnOf(listOf(1, 2), listOf(3, 4))

        val df = dataFrameOf(a, b)

        df.explode { b }
        // SampleEnd
    }

    @Test
    fun explode_strings() {
        // SampleStart
        val df = dataFrameOf("a", "b")(
            1, listOf(1, 2),
            2, listOf(3, 4)
        )

        df.explode("b")
        // SampleEnd
    }

    @Test
    fun explodeSeveral() {
        // SampleStart
        val a by columnOf(listOf(1, 2), listOf(3, 4, 5))
        val b by columnOf(listOf(1, 2, 3), listOf(4, 5))

        val df = dataFrameOf(a, b)
        df.explode { a and b }
        // SampleEnd
    }

    @Test
    fun explodeColumnList() {
        // SampleStart
        val col by columnOf(listOf(1, 2), listOf(3, 4))

        col.explode()
        // SampleEnd
    }

    @Test
    fun explodeColumnFrames() {
        // SampleStart
        val col by columnOf(
            dataFrameOf("a", "b")(1, 2, 3, 4),
            dataFrameOf("a", "b")(5, 6, 7, 8)
        )

        col.explode()
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
        val pivoted = df.dropNulls { city }.pivotCounts(inward = false) { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.cast<Int>()
            .where { it > 0 }.keysInto("city")
        // SampleEnd
    }

    @Test
    fun gather() {
        val pivoted = df.dropNulls { city }.pivotCounts(inward = false) { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }.into("city", "population")
        // SampleEnd
    }

    @Test
    fun gatherWithMapping() {
        val pivoted = df.dropNulls { city }.pivotCounts(inward = false) { city }
        // SampleStart
        pivoted.gather { "London".."Tokyo" }
            .cast<Int>()
            .where { it > 10 }
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
    fun concatDfs() {
        val df1 = df
        val df2 = df
        // SampleStart
        df.concat(df1, df2)
        // SampleEnd
    }

    @Test
    fun concatColumns() {
        // SampleStart
        val a by columnOf(1, 2)
        val b by columnOf(3, 4)
        a.concat(b)
            // SampleEnd
            .shouldBe(columnOf(1, 2, 3, 4).named("a"))
    }

    @Test
    fun concatColumnsIterable() {
        // SampleStart
        val a by columnOf(1, 2)
        val b by columnOf(3, 4)
        listOf(a, b).concat()
            // SampleEnd
            .shouldBe(columnOf(1, 2, 3, 4).named("a"))
    }

    @Test
    fun concatIterable() {
        val df1 = df
        val df2 = df
        // SampleStart
        listOf(df1, df2).concat()
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
        val x = dataFrameOf("a", "b")(
            1, 2,
            3, 4
        )
        val y = dataFrameOf("b", "c")(
            5, 6,
            7, 8
        )
        val frameColumn by columnOf(x, y)
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
        val yearOfBirth by column<Int>("year of birth")

        df.add(yearOfBirth) { 2021 - age }
        // SampleEnd
        val added = df.add(yearOfBirth) { 2021 - age }
        added[yearOfBirth].name() shouldBe "year of birth"
    }

    @Test
    fun add_strings() {
        // SampleStart
        df.add("year of birth") { 2021 - "age"<Int>() }
        // SampleEnd
    }

    @Test
    fun addRecurrent() {
        // SampleStart
        df.add("fibonacci") {
            if (index() < 2) 1
            else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
        }
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
    fun addDfs() {
        val df1 = df.select { name named "name2" }
        val df2 = df.select { age named "age2" }
        // SampleStart
        df.add(df1, df2)
        // SampleEnd
    }

    @Test
    fun addMany_properties() {
        // SampleStart
        df.add {
            "year of birth" from 2021 - age
            age gt 18 into "is adult"
            "details" {
                name.lastName.length() into "last name length"
                "full name" from { name.firstName + " " + name.lastName }
            }
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
        val details by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()

        df.add {
            yob from 2021 - age
            age gt 18 into isAdult
            details from {
                lastName.length() into lastNameLength
                fullName from { firstName() + " " + lastName() }
            }
        }
        // SampleEnd
    }

    @Test
    fun addMany_strings() {
        // SampleStart
        df.add {
            "year of birth" from 2021 - "age"<Int>()
            "age"<Int>() gt 18 into "is adult"
            "details" {
                "name"["lastName"]<String>().length() into "last name length"
                "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
            }
        }
        // SampleEnd
    }

    @Test
    fun remove_properties() {
        // SampleStart
        df.remove { name and weight }
        // SampleEnd
    }

    @Test
    fun remove_accessors() {
        // SampleStart
        val name by columnGroup()
        val weight by column<Int?>()

        df.remove { name and weight }
        // SampleEnd
    }

    @Test
    fun remove_strings() {
        // SampleStart
        df.remove("name", "weight")
        // SampleEnd
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
    }

    @Test
    fun mapToColumn_accessors() {
        // SampleStart
        val age by column<Int>()
        val yearOfBirth by column<Int>("year of birth")

        df.mapToColumn(yearOfBirth) { 2021 - age }
        // SampleEnd
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
            "year of birth" from 2021 - age
            age gt 18 into "is adult"
            name.lastName.length() into "last name length"
            "full name" from { name.firstName + " " + name.lastName }
            +city
        }
        // SampleEnd
    }

    @Test
    fun mapMany_accessors() {
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

        df.mapToFrame {
            yob from 2021 - age
            age gt 18 into isAdult
            lastName.length() into lastNameLength
            fullName from { firstName() + " " + lastName() }
            +city
        }
        // SampleEnd
    }

    @Test
    fun mapMany_strings() {
        // SampleStart
        df.mapToFrame {
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

    @Test
    fun multiCallOperations() {
        // SampleStart
        df.update { age }.where { city == "Paris" }.with { it - 5 }
            .filter { isHappy && age > 100 }
            .move { name.firstName and name.lastName }.after { isHappy }
            .merge { age and weight }.by { "Age: ${it[0]}, weight: ${it[1]}" }.into("info")
            .rename { isHappy }.into("isOK")
        // SampleEnd
    }

    class MyType(val value: Int)

    @DataSchema
    class MySchema(val a: MyType, val b: MyType, val c: Int)

    fun customConvertersData() {
        // SampleStart
        class MyType(val value: Int)

        @DataSchema
        class MySchema(val a: MyType, val b: MyType, val c: Int)

        // SampleEnd
    }
    @Test
    fun customConverters() {
        // SampleStart
        val df = dataFrameOf("a", "b")(1, "2")
        df.convertTo<MySchema> {
            convert<Int>().with { MyType(it) } // converts `a` from Int to MyType
            parser { MyType(it.toInt()) } // converts `b` from String to MyType
            fill { c }.with { a.value + b.value } // computes missing column `c`
        }
        // SampleEnd
    }
}
