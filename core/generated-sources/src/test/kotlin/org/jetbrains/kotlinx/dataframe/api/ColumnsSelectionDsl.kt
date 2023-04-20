package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.samples.api.*
import org.junit.Test
import kotlin.reflect.typeOf

open class ColumnsSelectionDslTests : TestBase() {

    @Test
    fun first() {
        listOf(
            df.select { name },
            df.select { first() },
            df.select { all().first() },
            df.select { first { it.name().startsWith("n") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },

            df.select { name.first { col -> col.any { it == "Alice" } } },
            df.select { name.colsOf<String>().first { col -> col.any { it == "Alice" } } },

            df.select { "name".first { col -> col.any { it == "Alice" } } },
            df.select { "name".colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } } },

            df.select { Person::name.first { col -> col.any { it == "Alice" } } },
            df.select { Person::name.colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } } },

            df.select { pathOf("name").first { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } } },

            df.select { it["name"].first { col -> col.any { it == "Alice" } } },
            df.select { it["name"].colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }

    @Test
    fun last() {
        listOf(
            df.select { isHappy },
            df.select { last() },
            df.select { all().last() },
            df.select { last { it.name().startsWith("is") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },

            df.select { name.last { col -> col.any { it == "Alice" } } },
            df.select { name.colsOf<String>().last { col -> col.any { it == "Alice" } } },

            df.select { "name".last { col -> col.any { it == "Alice" } } },
            df.select { "name".colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } } },

            df.select { Person::name.last { col -> col.any { it == "Alice" } } },
            df.select { Person::name.colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } } },

            df.select { pathOf("name").last { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } } },

            df.select { it["name"].last { col -> col.any { it == "Alice" } } },
            df.select { it["name"].colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }

    @Test
    fun single() {
        val singleDf = df.select { take(1) }

        listOf(
            df.select { name },
            singleDf.select { name },
            singleDf.select { single() },
            singleDf.select { all().single() },
            df.select { single { it.name().startsWith("n") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },

            df.select { name.single { col -> col.any { it == "Alice" } } },
            df.select { name.colsOf<String>().single { col -> col.any { it == "Alice" } } },

            df.select { "name".single { col -> col.any { it == "Alice" } } },
            df.select { "name".colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } } },

            df.select { Person::name.single { col -> col.any { it == "Alice" } } },
            df.select { Person::name.colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } } },

            df.select { pathOf("name").single { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } } },

            df.select { it["name"].single { col -> col.any { it == "Alice" } } },
            df.select { it["name"].colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }

    @Test
    fun col() {
        listOf(
            df.select { age },

            df.select { col("age") },
            df.select { col<Int>("age") },

            df.select { col(pathOf("age")) },
            df.select { col<Int>(pathOf("age")) },

            df.select { col(Person::age) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },

            df.select { colGroup("name").col("firstName") },
            df.select { colGroup("name").col<String>("firstName") },

            df.select { colGroup("name").col(pathOf("firstName")) },
            df.select { colGroup("name").col<String>(pathOf("firstName")) },

            df.select { colGroup("name").col(Name::firstName) },
        ).shouldAllBeEqual()
    }

    @Test
    fun colGroup() {
        listOf(
            dfGroup.select { name },

            dfGroup.select { colGroup("name") },
            dfGroup.select { colGroup<Name>("name") },

            dfGroup.select { colGroup(pathOf("name")) },
            dfGroup.select { colGroup<Name>(pathOf("name")) },

            dfGroup.select { colGroup(Person::name) },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { name.firstName },

            dfGroup.select { colGroup("name").colGroup("firstName") },
            dfGroup.select { colGroup("name").colGroup<FirstNames>("firstName") },

            dfGroup.select { colGroup("name").colGroup(pathOf("firstName")) },
            dfGroup.select { colGroup("name").colGroup<FirstNames>(pathOf("firstName")) },

            dfGroup.select { colGroup("name").colGroup(Name2::firstName) },
        ).shouldAllBeEqual()

        dfGroup.select {
            "name"["firstName"]["firstName", "secondName"]
        } shouldBe dfGroup.select {
            name.firstName["firstName"] and name.firstName["secondName"]
        }
    }

    @DataSchema
    interface PersonWithFrame : Person {
        val frameCol: DataFrame<Person>
    }

    @Test
    fun frameCol() {
        val frameCol by frameColumn<Person>()

        val dfWithFrames = df
            .add {
                expr { df } into frameCol
            }
            .convert { name }.to {
                val firstName by it.asColumnGroup().firstName
                val lastName by it.asColumnGroup().lastName

                @Suppress("NAME_SHADOWING")
                val frameCol by it.map { df }.asFrameColumn()

                dataFrameOf(firstName, lastName, frameCol).asColumnGroup("name")
            }

        listOf(
            dfWithFrames.select { frameCol },

            dfWithFrames.select { frameCol("frameCol") },
            dfWithFrames.select { frameCol<Person>("frameCol") },

            dfWithFrames.select { frameCol(pathOf("frameCol")) },
            dfWithFrames.select { frameCol<Person>(pathOf("frameCol")) },

            dfWithFrames.select { frameCol(PersonWithFrame::frameCol) },
        ).shouldAllBeEqual()

        listOf(
            dfWithFrames.select { name[frameCol] },

            dfWithFrames.select { colGroup("name").frameCol("frameCol") },
            dfWithFrames.select { colGroup("name").frameCol<Person>("frameCol") },

            dfWithFrames.select { colGroup("name").frameCol(pathOf("frameCol")) },
            dfWithFrames.select { colGroup("name").frameCol<Person>(pathOf("frameCol")) },

            dfWithFrames.select { colGroup("name").frameCol(PersonWithFrame::frameCol) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with predicate`() {
        listOf(
            df.select { cols(name, age, city, weight, isHappy) },
            df.select { all().cols() },
            df.select { cols() },
            df.select { all() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },
            df.select { name }.select { all() },
            df.select { name }.select { cols() },
            df.select { name }.select { cols().all() },
            df.select { name }.select { all().cols() },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(name, age, weight) },

            df.select { cols { "e" in it.name() } },
//            df.select { this[{ "e" in it.name() }] },

            df.select { all().cols { "e" in it.name() } },
            df.select { all()[{ "e" in it.name() }] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols { "Name" in it.name() } },
//            df.select { name[{ "Name" in it.name() }] },

            df.select { name.colsOf<String>().cols { "Name" in it.name() } },
            df.select { name.colsOf<String>()[{ "Name" in it.name() }] },

            df.select { "name".cols { "Name" in it.name() } },
            df.select { "name"[{ "Name" in it.name() }] },

            df.select { Person::name.cols { "Name" in it.name() } },
            df.select { Person::name[{ "Name" in it.name() }] },

            df.select { pathOf("name").cols { "Name" in it.name() } },
            df.select { pathOf("name")[{ "Name" in it.name() }] },

            df.select { it["name"].cols { "Name" in it.name() } },
            df.select { it["name"][{ "Name" in it.name() }] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with column references`() {
        listOf(
            df.select { name and age },

            df.select { cols(name, age) },
            df.select { this[name, age] },
            df.select { it[name, age] },

            df.select { all().cols(name, age) },
            df.select { all()[name, age] },
        ).shouldAllBeEqual()

        val firstName by column<String>()
        val lastName by column<String>()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(firstName, lastName) },
//            df.select { name[name.firstName, name.lastName] },

            df.select { name.colsOf<String>().cols(firstName, lastName) },
            df.select { name.colsOf<String>()[firstName, lastName] },

            df.select { name.select { cols(this@select.firstName, this@select.lastName) } },

            df.select { "name".cols(firstName, lastName) },
            df.select { "name"[firstName, lastName] },

            df.select { Person::name.cols(firstName, lastName) },
            df.select { Person::name[firstName, lastName] },

            df.select { pathOf("name").cols(firstName, lastName) },
            df.select { pathOf("name")[firstName, lastName] },

            df.select { it["name"].cols(firstName, lastName) },
            df.select { it["name"][firstName, lastName] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with column names`() {
        listOf(
            df.select { name and age },

            df.select { cols("name", "age") },
            df.select { this["name", "age"] },
            df.select { it["name", "age"] },

            df.select { all().cols("name", "age") },
            df.select { all()["name", "age"] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols("firstName", "lastName") },
//            df.select { name["firstName", "lastName"] },

            df.select { name.colsOf<String>().cols("firstName", "lastName") },
            df.select { name.colsOf<String>()["firstName", "lastName"] },

            df.select { "name".cols("firstName", "lastName") },
            df.select { "name"["firstName", "lastName"] },
            df.select { "name"["firstName"] and "name"["lastName"] },

            df.select { Person::name.cols("firstName", "lastName") },
            df.select { Person::name["firstName", "lastName"] },

            df.select { pathOf("name").cols("firstName", "lastName") },
            df.select { pathOf("name")["firstName", "lastName"] },

            df.select { it["name"].cols("firstName", "lastName") },
            df.select { it["name"]["firstName", "lastName"] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with column paths`() {
        listOf(
            df.select { name.firstName },

            df.select { cols(pathOf("name", "firstName")) },
            df.select { this[pathOf("name", "firstName")] },
            df.select { it[pathOf("name", "firstName")] },

            df.select { all().cols(pathOf("name", "firstName")) },
            df.select { all()[pathOf("name", "firstName")] },

            df.select { pathOf("name", "firstName") },
        ).shouldAllBeEqual()

        listOf(
            df.select { name and age },

            df.select { cols(pathOf("name"), pathOf("age")) },
            df.select { this[pathOf("name"), pathOf("age")] },
            df.select { it[pathOf("name"), pathOf("age")] },

            df.select { all().cols(pathOf("name"), pathOf("age")) },
            df.select { all()[pathOf("name"), pathOf("age")] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(pathOf("firstName"), pathOf("lastName")) },
//            df.select { name[pathOf("firstName"), pathOf("lastName")] },

            df.select { name.colsOf<String>().cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { name.colsOf<String>()[pathOf("firstName"), pathOf("lastName")] },

            df.select { "name".cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { "name"[pathOf("firstName"), pathOf("lastName")] },

            df.select { Person::name.cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { Person::name[pathOf("firstName"), pathOf("lastName")] },

            df.select { pathOf("name").cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { pathOf("name")[pathOf("firstName"), pathOf("lastName")] },

            df.select { it["name"].cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { it["name"][pathOf("firstName"), pathOf("lastName")] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with KProperties`() {
        listOf(
            df.select { name and age },

            df.select { cols(Person::name, Person::age) },
            df.select { this[Person::name, Person::age] },
            df.select { it[Person::name, Person::age] },

            df.select { all().cols(Person::name, Person::age) },
            df.select { all()[Person::name, Person::age] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(Name::firstName, Name::lastName) },
            df.select { name[Name::firstName, Name::lastName] },

            df.select { name.colsOf<String>().cols(Name::firstName, Name::lastName) },
            df.select { name.colsOf<String>()[Name::firstName, Name::lastName] },

            df.select { "name".cols(Name::firstName, Name::lastName) },
            df.select { "name"[Name::firstName, Name::lastName] },

            df.select { Person::name.cols(Name::firstName, Name::lastName) },
            df.select { Person::name[Name::firstName, Name::lastName] },

            df.select { pathOf("name").cols(Name::firstName, Name::lastName) },
            df.select { pathOf("name")[Name::firstName, Name::lastName] },

            df.select { it["name"].cols(Name::firstName, Name::lastName) },
            df.select { it["name"][Name::firstName, Name::lastName] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with indices`() {
        listOf(
            df.select { name and age },

            df.select { cols(0, 1) },
            df.select { this[0, 1] },
            df.select { it[0, 1] },

            df.select { all().cols(0, 1) },
            df.select { all()[0, 1] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(0, 1) },
//            df.select { name[0, 1] },

            df.select { name.colsOf<String>().cols(0, 1) },
            df.select { name.colsOf<String>()[0, 1] },

            df.select { "name".cols(0, 1) },
            df.select { "name"[0, 1] },

            df.select { Person::name.cols(0, 1) },
            df.select { Person::name[0, 1] },

            df.select { pathOf("name").cols(0, 1) },
            df.select { pathOf("name")[0, 1] },

            df.select { it["name"].cols(0, 1) },
//            df.select { it["name"][0, 1] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with range`() {
        listOf(
            df.select { name and age },

            df.select { cols(0..1) },
            df.select { this[0..1] },
            df.select { it[0..1] },

            df.select { all().cols(0..1) },
            df.select { all()[0..1] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(0..1) },
//            df.select { name[0..1] },

            df.select { name.colsOf<String>().cols(0..1) },
            df.select { name.colsOf<String>()[0..1] },

            df.select { "name".cols(0..1) },
            df.select { "name"[0..1] },

            df.select { Person::name.cols(0..1) },
            df.select { Person::name[0..1] },

            df.select { pathOf("name").cols(0..1) },
            df.select { pathOf("name")[0..1] },

            df.select { it["name"].cols(0..1) },
//            df.select { it["name"][0..1] },
        ).shouldAllBeEqual()
    }
}
