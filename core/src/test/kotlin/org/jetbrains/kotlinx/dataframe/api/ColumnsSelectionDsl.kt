package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test
import kotlin.reflect.typeOf

open class ColumnsSelectionDslTests : TestBase() {

    @Test
    fun first() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".firstChild() }
        }

        // works as usual
        df.select { first() }
        // works on ColumnSet<Int>
        df.select { colsOf<Int>().first() }
        // recognized as SingleColumn<DataRow<*>>
        df.select { name.first() }
        // recognized as impossible to call, because it's a SingleColumn<Int>
//        df.select { age.first() }
        // unsafe because of string API, but has runtime check
        df.select { "name".firstChild() }
        // unsafe because of string API, but has runtime check
        df.select { pathOf("name").firstChild() }
        // recognized as SingleColumn<DataRow<*>>
        df.select { Person::name.first() }
        // recognized as SingleColumn<Int>, so impossible to call
//        df.select { Person::age.first() }
        // if not recognized correctly (e.g. because of lack of DataRow<> around type), asColumnGroup() can be used
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.asColumnGroup().first() }
        }
        // unfortunately impossible to call like this, because of AnyCol type
//        df.select { it["name"].first() }
        // but you can use asColumnGroup() to explicitly specify the column type
        df.select { it["name"].asColumnGroup().first() }
        // or use the accessor like this
        df.select { colGroup("name").first() }

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

            df.select { "name".firstChild { col -> col.any { it == "Alice" } } },
            df.select { "name".colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } } },

            df.select { Person::name.asColumnGroup().first { col -> col.any { it == "Alice" } } },
            df.select { Person::name.colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } } },

            df.select { pathOf("name").firstChild { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } } },

            df.select { it["name"].asColumnGroup().first { col -> col.any { it == "Alice" } } },
            df.select {
                it["name"].asColumnGroup().colsOf<String>(typeOf<String>()).first { col -> col.any { it == "Alice" } }
            },
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

            df.select { "name".lastChild { col -> col.any { it == "Alice" } } },
            df.select { "name".colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } } },

            df.select { Person::name.last { col -> col.any { it == "Alice" } } },
            df.select { Person::name.colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } } },

            df.select { pathOf("name").lastChild { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } } },

            df.select { it["name"].asColumnGroup().last { col -> col.any { it == "Alice" } } },
            df.select {
                it["name"].asColumnGroup().colsOf<String>(typeOf<String>()).last { col -> col.any { it == "Alice" } }
            },
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

            df.select { "name".singleChild { col -> col.any { it == "Alice" } } },
            df.select { "name".colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } } },

            df.select { Person::name.single { col -> col.any { it == "Alice" } } },
            df.select { Person::name.colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } } },

            df.select { pathOf("name").singleChild { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } } },

            df.select { it["name"].asColumnGroup().single { col -> col.any { it == "Alice" } } },
            df.select {
                it["name"].asColumnGroup().colsOf<String>(typeOf<String>()).single { col -> col.any { it == "Alice" } }
            },
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


        listOf(
            df.select { col(0) },

            df.select { this[0] },
        ).shouldAllBeEqual()
        // TODO
    }

    @Test
    fun valueCol() {
        listOf(
            df.select { age },

            df.select { valueCol("age") },
            df.select { valueCol<Int>("age") },

            df.select { valueCol(pathOf("age")) },
            df.select { valueCol<Int>(pathOf("age")) },

            df.select { valueCol(Person::age) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },

            df.select { colGroup("name").valueCol("firstName") },
            df.select { colGroup("name").valueCol<String>("firstName") },

            df.select { colGroup("name").valueCol(pathOf("firstName")) },
            df.select { colGroup("name").valueCol<String>(pathOf("firstName")) },

            df.select { colGroup("name").valueCol(Name::firstName) },
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

    private val frameCol by frameColumn<Person>()

    private val dfWithFrames = df
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

    @Test
    fun frameCol() {
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
    fun `cols, filter and get with predicate`() {
        listOf(
            df.select { cols(name, age, city, weight, isHappy) },
            df.select { all().cols() },
            df.select { cols() },
            df.select { all() },
            df.select { filter { true } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },
            df.select { name }.select { all() },
            df.select { name }.select { cols() },
            df.select { name }.select { filter { true } },
            df.select { name }.select { cols().all() },
            df.select { name }.select { filter { true }.all() },
            df.select { name }.select { all().cols() },
            df.select { name }.select { all().filter { true } },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(name, age, weight) },

            df.select { cols { "e" in it.name() } },
            df.select { filter { "e" in it.name() } },
//            df.select { this[{ "e" in it.name() }] },

            df.select { all().cols { "e" in it.name() } },
            df.select { all().filter { "e" in it.name() } },
            df.select { all()[{ "e" in it.name() }] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols { "Name" in it.name() } },
            df.select { name.filter { "Name" in it.name() } },
//            df.select { name[{ "Name" in it.name() }] },

            df.select { name.colsOf<String>().cols { "Name" in it.name() } },
            df.select { name.colsOf<String>().filter { "Name" in it.name() } },
            df.select { name.colsOf<String>()[{ "Name" in it.name() }] },

            df.select { "name".cols { "Name" in it.name() } },
            df.select { "name".filterChildren { "Name" in it.name() } },
            df.select { "name"[{ "Name" in it.name() }] },

            df.select { Person::name.cols { "Name" in it.name() } },
            df.select { Person::name.filter { "Name" in it.name() } },
            df.select { Person::name[{ "Name" in it.name() }] },

            df.select { pathOf("name").cols { "Name" in it.name() } },
            df.select { pathOf("name").filterChildren { "Name" in it.name() } },
            df.select { pathOf("name")[{ "Name" in it.name() }] },

            df.select { it["name"].asColumnGroup().cols { "Name" in it.name() } },
            df.select { it["name"].asColumnGroup().filter { "Name" in it.name() } },
//            df.select { it["name"].asColumnGroup()[{ "Name" in it.name() }] },
        ).shouldAllBeEqual()
    }

    @Test
    fun valueCols() {
        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { all().valueCols() },
            df.select { valueCols() },
        ).shouldAllBeEqual()

        listOf(
            df.select { age },

            df.select { age }.select { all() },
            df.select { age }.select { valueCols() },
            df.select { age }.select { valueCols().all() },
            df.select { age }.select { all().valueCols() },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, weight) },
            df.select { valueCols { "e" in it.name() } },
            df.select { all().valueCols { "e" in it.name() } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.valueCols { "Name" in it.name() } },
            df.select { name.colsOf<String>().valueCols { "Name" in it.name() } },
            df.select { "name".valueCols { "Name" in it.name() } },
            df.select { Person::name.valueCols { "Name" in it.name() } },
            df.select { pathOf("name").valueCols { "Name" in it.name() } },
            df.select { it["name"].asColumnGroup().valueCols { "Name" in it.name() } },
        ).shouldAllBeEqual()
    }

    @Test
    fun colGroups() {
        listOf(
            df.select { name },

            df.select { all().colGroups() },
            df.select { colGroups() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },

            df.select { name }.select { all() },
            df.select { name }.select { colGroups() },
            df.select { name }.select { colGroups().all() },
            df.select { name }.select { all().colGroups() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },
            df.select { colGroups { "e" in it.name() } },
            df.select { all().colGroups { "e" in it.name() } },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { name.firstName },

            dfGroup.select { name.colGroups { "Name" in it.name() } },
            dfGroup.select { name.colsOf<AnyRow> { "Name" in it.name() } },
            dfGroup.select { name.colsOf<AnyRow>().colGroups { "Name" in it.name() } },
            dfGroup.select { "name".colGroups { "Name" in it.name() } },
            dfGroup.select { Person::name.colGroups { "Name" in it.name() } },
            dfGroup.select { pathOf("name").colGroups { "Name" in it.name() } },
            dfGroup.select { it["name"].asColumnGroup().colGroups { "Name" in it.name() } },
        ).shouldAllBeEqual()
    }

    @Test
    fun frameCols() {
        listOf(
            dfWithFrames.select { frameCol },

            dfWithFrames.select { all().frameCols() },
            dfWithFrames.select { frameCols() },
        ).shouldAllBeEqual()

        listOf(
            dfWithFrames.select { name[frameCol] },

            dfWithFrames.select { name[frameCol] }.select { all() },
            dfWithFrames.select { name[frameCol] }.select { frameCols() },
            dfWithFrames.select { name[frameCol] }.select { frameCols().all() },
            dfWithFrames.select { name[frameCol] }.select { all().frameCols() },
        ).shouldAllBeEqual()

        listOf(
            dfWithFrames.select { frameCol },
            dfWithFrames.select { frameCols { "e" in it.name() } },
            dfWithFrames.select { all().frameCols { "e" in it.name() } },
        ).shouldAllBeEqual()

        listOf(
            dfWithFrames.select { name[frameCol] },

            dfWithFrames.select { name.frameCols { "frame" in it.name() } },
            dfWithFrames.select { name.colsOf<AnyFrame> { "frame" in it.name() } },
            dfWithFrames.select { name.colsOf<AnyFrame>().frameCols { "frame" in it.name() } },
            dfWithFrames.select { "name".frameCols { "frame" in it.name() } },
            dfWithFrames.select { Person::name.frameCols { "frame" in it.name() } },
            dfWithFrames.select { pathOf("name").frameCols { "frame" in it.name() } },
            dfWithFrames.select { it["name"].asColumnGroup().frameCols { "frame" in it.name() } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols of kind`() {
        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { all().valueCols() },
            df.select { valueCols() },
            df.select { colsOfKind(Value) },
            df.select { colsOfKind(Value, Value) },
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

            df.select {
                name.select {
                    cols(this@select.firstName, this@select.lastName)
                }
            },

            df.select {
                it["name"].asColumnGroup().select {
                    cols("firstName", "lastName")
                }
            },

            df.select { "name".cols(firstName, lastName) },
            df.select { "name"[firstName, lastName] },

            df.select { Person::name.cols(firstName, lastName) },
            df.select { Person::name[firstName, lastName] },

            df.select { pathOf("name").cols(firstName, lastName) },
            df.select { pathOf("name")[firstName, lastName] },

            df.select { it["name"].asColumnGroup().cols(firstName, lastName) },
//            df.select { it["name"].asColumnGroup()[firstName, lastName] },
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

            df.select { it["name"].asColumnGroup().cols("firstName", "lastName") },
//            df.select { it["name"].asColumnGroup().["firstName", "lastName"] },
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

            df.select { it["name"].asColumnGroup().cols(pathOf("firstName"), pathOf("lastName")) },
//            df.select { it["name"].asColumnGroup()[pathOf("firstName"), pathOf("lastName")] },
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

            df.select { Person::name.asColumnGroup().cols(Name::firstName, Name::lastName) },
            df.select { Person::name.asColumnGroup()[Name::firstName, Name::lastName] },

            df.select { pathOf("name").cols(Name::firstName, Name::lastName) },
            df.select { pathOf("name")[Name::firstName, Name::lastName] },

            df.select { it["name"].asColumnGroup().cols(Name::firstName, Name::lastName) },
            df.select { it["name"].asColumnGroup()[Name::firstName, Name::lastName] },
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
//            df.select { name[0, 1] }, //takes rows instead of columns

            df.select { name.colsOf<String>().cols(0, 1) },
            df.select { name.colsOf<String>()[0, 1] },

            df.select { "name".cols(0, 1) },
            df.select { "name"[0, 1] },

            df.select { Person::name.cols(0, 1) },
            df.select { Person::name[0, 1] },

            df.select { pathOf("name").cols(0, 1) },
            df.select { pathOf("name")[0, 1] },

            df.select { it["name"].asColumnGroup().cols(0, 1) },
//            df.select { it["name"].asColumnGroup()[0, 1] },
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

            df.select { it["name"].asColumnGroup().cols(0..1) },
//            df.select { it["name"].asColumnGroup()[0..1] },
        ).shouldAllBeEqual()
    }

    @Test
    fun roots() {
        df.select { cols(name.firstName, name.lastName, age).roots() } shouldBe
            df.select { cols(name.firstName, name.lastName, age) }

        df.select { cols(name.firstName, name.lastName, age, name).roots() } shouldBe
            df.select { cols(name, age) }
    }

    @Test
    fun select() {
        listOf(
            df.select {
                name.firstName and name.lastName
            },
            df.select {
                name.select { firstName and lastName }
            },
            df.select {
                name { firstName and lastName }
            },
            df.select {
                "name".select {
                    colsOf<String>()
                }
            },
            df.select {
                "name" {
                    colsOf<String>()
                }
            },
            df.select {
                colGroup("name").select {
                    colsOf<String>()
                }
            },
            df.select {
                colGroup<Name>("name").select {
                    colsOf<String>()
                }
            },
            df.select {
                (colGroup<Name>("name")) {
                    colsOf<String>()
                }
            },
            df.select {
                colGroup<Name>("name")() {
                    colsOf<String>()
                }
            },
            df.select {
                "name".select {
                    "firstName" and "lastName"
                }
            },
            df.select {
                "name" {
                    "firstName" and "lastName"
                }
            },
            df.select {
                pathOf("name").select {
                    "firstName" and "lastName"
                }
            },
            df.select {
                pathOf("name")() {
                    "firstName" and "lastName"
                }
            },
            df.select {
                it["name"].asColumnGroup().select {
                    colsOf<String>()
                }
            },
            df.select {
                it["name"].asColumnGroup()() {
                    colsOf<String>()
                }
            },
            df.select {
                name {
                    colsOf<String>()
                }
            },
            df.select {
                (it["name"].asColumnGroup()) {
                    colsOf<String>()
                }
            },
            df.select {
                Person::name.select {
                    firstName and lastName
                }
            },
            df.select {
                Person::name {
                    firstName and lastName
                }
            },
            df.select {
                "name"<DataRow<Name>>().select {
                    colsOf<String>()
                }
            },
            df.select {
                "name"<DataRow<Name>>()() {
                    colsOf<String>()
                }
            },
            df.select {
                colGroup("name").select {
                    colsOf<String>()
                }
            },
            df.select {
                colGroup("name")() {
                    colsOf<String>()
                }
            },
        ).shouldAllBeEqual()
    }

    @Test
    fun children() {
        listOf(
            df.select { all() },

            df.select { children() },

            df.select { cols() },

            df.select { filter { true } }
        ).shouldAllBeEqual()

        listOf(
            df.select { all().rec() },

            df.select { children().rec() },

            df.select { cols().rec() },

            df.select { filter { true }.rec() }
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { all().children() },

            df.select { colGroups().children() },

            df.select { name.children() },
        ).shouldAllBeEqual()
    }

    @Test
    fun `take and takeLast`() {
        listOf(
            df.select { name.firstName },
            df.select { name.take(1) },
            df.select { "name".takeChildren(1) },
            df.select { Person::name.take(1) },
            df.select { pathOf("name").takeChildren(1) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.takeLast(1) },
            df.select { "name".takeLastChildren(1) },
            df.select { Person::name.takeLast(1) },
            df.select { pathOf("name").takeLastChildren(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `drop and dropLast`() {
        listOf(
            df.select { name.lastName },
            df.select { name.drop(1) },
            df.select { "name".dropChildren(1) },
            df.select { Person::name.drop(1) },
            df.select { pathOf("name").dropChildren(1) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLast(1) },
            df.select { "name".dropLastChildren(1) },
            df.select { Person::name.dropLast(1) },
            df.select { pathOf("name").dropLastChildren(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `takeWhile and takeLastWhile`() {
        listOf(
            df.select { name.firstName },
            df.select { name.takeWhile { it.name == "firstName" } },
            df.select { "name".takeChildrenWhile { it.name == "firstName" } },
            df.select { Person::name.takeWhile { it.name == "firstName" } },
            df.select { pathOf("name").takeChildrenWhile { it.name == "firstName" } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.takeLastWhile { it.name == "lastName" } },
            df.select { "name".takeLastChildrenWhile { it.name == "lastName" } },
            df.select { Person::name.takeLastWhile { it.name == "lastName" } },
            df.select { pathOf("name").takeLastChildrenWhile { it.name == "lastName" } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `dropWhile and dropLastWhile`() {
        listOf(
            df.select { name.lastName },
            df.select { name.dropWhile { it.name == "firstName" } },
            df.select { "name".dropChildrenWhile { it.name == "firstName" } },
            df.select { Person::name.dropWhile { it.name == "firstName" } },
            df.select { pathOf("name").dropChildrenWhile { it.name == "firstName" } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLastWhile { it.name == "lastName" } },
            df.select { "name".dropLastChildrenWhile { it.name == "lastName" } },
            df.select { Person::name.dropLastWhile { it.name == "lastName" } },
            df.select { pathOf("name").dropLastChildrenWhile { it.name == "lastName" } },
        ).shouldAllBeEqual()
    }

    @Test
    fun nameContains() {
        listOf(
            df.select { name.firstName },
            df.select { name.nameContains("first") },
            df.select { "name".nameContains("first") },
            df.select { Person::name.nameContains("first") },
            df.select { pathOf("name").nameContains("first") },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.nameContains(Regex("last")) },
            df.select { "name".nameContains(Regex("last")) },
            df.select { Person::name.nameContains(Regex("last")) },
            df.select { pathOf("name").nameContains(Regex("last")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `nameStartsWith and nameEndsWith`() {
        listOf(
            df.select { name.firstName },
            df.select { name.nameStartsWith("first") },
            df.select { "name".nameStartsWith("first") },
            df.select { Person::name.nameStartsWith("first") },
            df.select { pathOf("name").nameStartsWith("first") },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.nameEndsWith("Name") },
            df.select { "name".nameEndsWith("Name") },
            df.select { Person::name.nameEndsWith("Name") },
            df.select { pathOf("name").nameEndsWith("Name") },
        ).shouldAllBeEqual()
    }

    @Test
    fun and() {
        df.select {
            age and name.select {
                firstName and lastName
            }
        }

        df.select {
            age and (
                name
                )
        }

        df.select {
            age and colGroup(Person::name).select {
                firstName and lastName
            }
        }

        df.select {
            it { it { it { age } } }
        }

        df.select {
            age and
                name
        }

        df.select {
            "age"<Int>() and name.firstName

            select { this { age } }
        }

    }
}
