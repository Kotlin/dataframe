package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.columns.asValueColumn
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class ColumnsSelectionDslTests : TestBase() {

    @Test
    fun first() {
        df.select { all().first() } shouldBe df.select { first() }

        df.select { all().first() } shouldBe df.select { name }

        df.select { first() } shouldBe df.select { name }

        df.select { first { it.name().startsWith("a") } } shouldBe df.select { age }

        df.select {
            name.first {
                it.any { it == "Alice" }
            }
        } shouldBe df.select {
            name.colsOf<String>().first {
                it.any { it == "Alice" }
            }
        }

        df.select {
            "name".first {
                it.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            Person::name.first {
                it.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }
    }

    @Test
    fun last() {
        df.select { all().last() } shouldBe df.select { last() }

        df.select { all().last() } shouldBe df.select { isHappy }

        df.select { last() } shouldBe df.select { isHappy }

        df.select { last { it.name().startsWith("a") } } shouldBe df.select { age }

        df.select {
            name.last {
                it.any { it == "Alice" }
            }
        } shouldBe df.select {
            name.colsOf<String>().last {
                it.any { it == "Alice" }
            }
        }

        df.select {
            "name".last {
                it.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            Person::name.last {
                it.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }
    }

    @Test
    fun single() {
        val singleDf = df.select { take(1) }
        singleDf.select { all().single() } shouldBe singleDf.select { single() }

        singleDf.select { all().single() } shouldBe singleDf.select { name }

        singleDf.select { single() } shouldBe singleDf.select { name }

        df.select { single { it.name().startsWith("a") } } shouldBe df.select { age }

        df.select {
            name.single {
                it.any { it == "Alice" }
            }
        } shouldBe df.select {
            name.colsOf<String>().single {
                it.any { it == "Alice" }
            }
        }

        df.select {
            "name".single {
                it.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            Person::name.single {
                it.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }
    }

    @Test
    fun col() {
        df.select { col("age") } shouldBe df.select { age }
        df.select { col<Int>("age") } shouldBe df.select { age }
        df.select { col(pathOf("age")) } shouldBe df.select { age }
        df.select { col<Int>(pathOf("age")) } shouldBe df.select { age }
        df.select { col(Person::age) } shouldBe df.select { age }

        df.select { colGroup("name").col("firstName") } shouldBe df.select { name.firstName }
        df.select { colGroup("name").col<String>("firstName") } shouldBe df.select { name.firstName }
        df.select { colGroup("name").col(pathOf("firstName")) } shouldBe df.select { name.firstName }
        df.select { colGroup("name").col<String>(pathOf("firstName")) } shouldBe df.select { name.firstName }
        df.select { colGroup("name").col(Name::firstName) } shouldBe df.select { name.firstName }
    }

    @DataSchema
    interface FirstNames {
        val firstName: String
        val secondName: String?
        val thirdName: String?
    }

    @DataSchema
    interface MyName : Name {
        val firstNames: FirstNames
    }

    @Test
    fun colGroup() {
        val firstNames by columnGroup<FirstNames>()
        val dfGroup = df.convert { name.firstName }.to {
            val firstName by it
            val secondName by it.map<_, String?> { null }.asValueColumn()
            val thirdName by it.map<_, String?> { null }.asValueColumn()

            dataFrameOf(firstName, secondName, thirdName)
                .cast<FirstNames>(verify = true)
                .asColumnGroup(firstNames)
        }

        dfGroup.print(columnTypes = true, title = true)

        dfGroup.select { colGroup("name") } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup<String>("name") } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup(pathOf("name")) } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup<String>(pathOf("name")) } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup(Person::name) } shouldBe dfGroup.select { name }

        dfGroup.select { colGroup("name").colGroup("firstNames") } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup<String>("firstNames") } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup(pathOf("firstNames")) } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup<String>(pathOf("firstNames")) } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup(MyName::firstNames) } shouldBe dfGroup.select { name[firstNames] }
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

        dfWithFrames.select { frameCol("frameCol") } shouldBe dfWithFrames.select { frameCol }
        dfWithFrames.select { frameCol<Person>("frameCol") } shouldBe dfWithFrames.select { frameCol }
        dfWithFrames.select { frameCol(pathOf("frameCol")) } shouldBe dfWithFrames.select { frameCol }
        dfWithFrames.select { frameCol<Person>(pathOf("frameCol")) } shouldBe dfWithFrames.select { frameCol }
        dfWithFrames.select { frameCol(PersonWithFrame::frameCol) } shouldBe dfWithFrames.select { frameCol }

        dfWithFrames.select { colGroup("name").frameCol("frameCol") } shouldBe dfWithFrames.select { name[frameCol] }
        dfWithFrames.select { colGroup("name").frameCol<Person>("frameCol") } shouldBe dfWithFrames.select { name[frameCol] }
        dfWithFrames.select { colGroup("name").frameCol(pathOf("frameCol")) } shouldBe dfWithFrames.select { name[frameCol] }
        dfWithFrames.select { colGroup("name").frameCol<Person>(pathOf("frameCol")) } shouldBe dfWithFrames.select { name[frameCol] }
        dfWithFrames.select { colGroup("name").frameCol(PersonWithFrame::frameCol) } shouldBe dfWithFrames.select { name[frameCol] }
    }
}
