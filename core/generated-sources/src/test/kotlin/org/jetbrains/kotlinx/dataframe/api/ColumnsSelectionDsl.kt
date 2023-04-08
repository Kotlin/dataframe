package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
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
            name.first { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select {
            name.colsOf<String>().first { col ->
                col.any { it == "Alice" }
            }
        }

        df.select {
            "name".first { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            Person::name.first { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            pathOf("name").first { col ->
                col.any { it == "Alice" }
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
            name.last { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select {
            name.colsOf<String>().last { col ->
                col.any { it == "Alice" }
            }
        }

        df.select {
            "name".last { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            Person::name.last { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            pathOf("name").last { col ->
                col.any { it == "Alice" }
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
            name.single { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select {
            name.colsOf<String>().single { col ->
                col.any { it == "Alice" }
            }
        }

        df.select {
            "name".single { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            Person::name.single { col ->
                col.any { it == "Alice" }
            }
        } shouldBe df.select { name.firstName }

        df.select {
            pathOf("name").single { col ->
                col.any { it == "Alice" }
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

        dfGroup.select { colGroup("name") } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup<Name>("name") } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup(pathOf("name")) } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup<Name>(pathOf("name")) } shouldBe dfGroup.select { name }
        dfGroup.select { colGroup(Person::name) } shouldBe dfGroup.select { name }

        dfGroup.select { colGroup("name").colGroup("firstNames") } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup<String>("firstNames") } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup(pathOf("firstNames")) } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup<String>(pathOf("firstNames")) } shouldBe dfGroup.select { name[firstNames] }
        dfGroup.select { colGroup("name").colGroup(MyName::firstNames) } shouldBe dfGroup.select { name[firstNames] }

        dfGroup.select {
            "name"["firstNames"]["firstName", "secondName"]
        } shouldBe dfGroup.select {
            name[firstNames]["firstName"] and name[firstNames]["secondName"]
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

    @Test
    fun `cols and get with predicate`() {
        df.select { all().cols() } shouldBe df.select { cols() }
        df.select { all().cols { "e" in it.name() } } shouldBe df.select {
            cols { "e" in it.name() }
        }
        df.select { all()[{ "e" in it.name() }] } shouldBe df.select {
//            this[{ "e" in it.name() }]
            cols { "e" in it.name() }
        }

        df.select {
            name.cols {
                "Name" in it.name()
            }
        } shouldBe df.select {
            name.colsOf<String>().cols {
                "Name" in it.name()
            }
        }

        df.select {
//            name[{ "Name" in it.name() }]
            name.cols { "Name" in it.name() }
        } shouldBe df.select {
            name.colsOf<String>()[{ "Name" in it.name() }]
        }

        df.select {
            "name".cols { "Name" in it.name() }
        } shouldBe df.select {
            Person::name.cols { "Name" in it.name() }
        }

        df.select {
            "name"[{ "Name" in it.name() }]
        } shouldBe df.select {
            Person::name[{ "Name" in it.name() }]
        }

        df.select {
            pathOf("name").cols { "Name" in it.name() }
        } shouldBe df.select {
            "name"[{ "Name" in it.name() }]
        }

        df.select {
            pathOf("name").cols { "Name" in it.name() }
        } shouldBe df.select {
            pathOf("name")[{ "Name" in it.name() }]
        }
    }

    @Test
    fun `cols and get with column references`() {
        df.select { all().cols(name, age) } shouldBe df.select { cols(name, age) }
        df.select { all()[name, age] } shouldBe df.select { this[name, age] }

        val firstName by column<String>()
        val lastName by column<String>()
        df.select {
            name.cols(firstName, lastName)
        } shouldBe df.select {
            name.colsOf<String>().cols(firstName, lastName)
        }

        df.select {
            name.cols(name.firstName, name.lastName)
        } shouldBe df.select {
            name.colsOf<String>().cols(name.firstName, name.lastName)
        }.also { it.print() }

        df.select {
//            name[name.firstName, name.lastName]
            name.cols(name.firstName, name.lastName)
        } shouldBe df.select {
            name.colsOf<String>()[name.firstName, name.lastName]
        }

        df.select {
            "name".cols(name.firstName, name.lastName)
        } shouldBe df.select {
            Person::name.cols(name.firstName, name.lastName)
        }

        df.select {
            "name"[name.firstName, name.lastName]
        } shouldBe df.select {
            Person::name[name.firstName, name.lastName]
        }

        df.select {
            pathOf("name").cols(name.firstName, name.lastName)
        } shouldBe df.select {
            pathOf("name")[name.firstName, name.lastName]
        }
    }

    @Test
    fun `cols and get with column names`() {
        df.select { all().cols("name", "age") } shouldBe df.select { cols("name", "age") }
        df.select { all()["name", "age"] } shouldBe df.select { this["name", "age"] }

        df.select {
            name.cols("firstName", "lastName")
        } shouldBe df.select {
            name.colsOf<String>().cols("firstName", "lastName")
        }

        df.select {
//            name["firstName", "lastName"]
            name.cols("firstName", "lastName")
        } shouldBe df.select {
            name.colsOf<String>()["firstName", "lastName"]
        }

        df.select {
            "name".cols("firstName", "lastName")
        } shouldBe df.select {
            Person::name.cols("firstName", "lastName")
        }

        df.select {
            "name"["firstName", "lastName"]
        } shouldBe df.select {
            Person::name["firstName", "lastName"]
        }

        df.select {
            pathOf("name").cols("firstName", "lastName")
        } shouldBe df.select {
            pathOf("name")["firstName", "lastName"]
        }
    }

    @Test
    fun `cols and get with column paths`() {
        listOf(
            df.select {
                all().cols(pathOf("name", "firstName"))
            },
            df.select {
                cols(pathOf("name", "firstName"))
            },
            df.select {
                pathOf("name", "firstName")
            },
            df.select {
                name.firstName
            },
        ).reduce { acc, dataFrame ->
            acc shouldBe dataFrame
            dataFrame
        }

        df.select { all().cols(pathOf("name"), pathOf("age")) } shouldBe df.select {
            cols(
                pathOf("name"),
                pathOf("age")
            )
        }
        df.select { all()[pathOf("name"), pathOf("age")] } shouldBe df.select { this[pathOf("name"), pathOf("age")] }

        df.select {
            name.cols(pathOf("firstName"), pathOf("lastName"))
        } shouldBe df.select {
            name.colsOf<String>().cols(pathOf("firstName"), pathOf("lastName"))
        }

        df.select {
//            name[pathOf("firstName"), pathOf("lastName")]
            name.cols(pathOf("firstName"), pathOf("lastName"))
        } shouldBe df.select {
            name.colsOf<String>()[pathOf("firstName"), pathOf("lastName")]
        }

        df.select {
            "name".cols(pathOf("firstName"), pathOf("lastName"))
        } shouldBe df.select {
            Person::name.cols(pathOf("firstName"), pathOf("lastName"))
        }

        df.select {
            "name"[pathOf("firstName"), pathOf("lastName")]
        } shouldBe df.select {
            Person::name[pathOf("firstName"), pathOf("lastName")]
        }

        df.select {
            pathOf("name").cols(pathOf("firstName"), pathOf("lastName"))
        } shouldBe df.select {
            pathOf("name")[pathOf("firstName"), pathOf("lastName")]
        }
    }

    @Test
    fun `cols and get with KProperties`() {
        df.select { all().cols(Person::name, Person::age) } shouldBe df.select { cols(Person::name, Person::age) }
        df.select { all()[Person::name, Person::age] } shouldBe df.select { this[Person::name, Person::age] }

        df.select {
            name.cols(Name::firstName, Name::lastName)
        } shouldBe df.select {
            name.colsOf<String>().cols(Name::firstName, Name::lastName)
        }

        df.select {
            name[Name::firstName, Name::lastName]
        } shouldBe df.select {
            name.colsOf<String>()[Name::firstName, Name::lastName]
        }

        df.select {
            "name".cols(Name::firstName, Name::lastName)
        } shouldBe df.select {
            Person::name.cols(Name::firstName, Name::lastName)
        }

        df.select {
            "name"[Name::firstName, Name::lastName]
        } shouldBe df.select {
            Person::name[Name::firstName, Name::lastName]
        }

        df.select {
            pathOf("name").cols(Name::firstName, Name::lastName)
        } shouldBe df.select {
            pathOf("name")[Name::firstName, Name::lastName]
        }
    }

    @Test
    fun `cols and get with indices`() {
        df.select { all().cols(0, 1) } shouldBe df.select { cols(0, 1) }
        df.select { all()[0, 1] } shouldBe df.select { this[0, 1] }

        df.select {
            name.cols(0, 1)
        } shouldBe df.select {
            name.colsOf<String>().cols(0, 1)
        }

        df.select {
//            name[0, 1]
            name.cols(0, 1)
        } shouldBe df.select {
            name.colsOf<String>()[0, 1]
        }

        df.select {
            "name".cols(0, 1)
        } shouldBe df.select {
            Person::name.cols(0, 1)
        }

        df.select {
            "name"[0, 1]
        } shouldBe df.select {
            Person::name[0, 1]
        }

        df.select {
            pathOf("name").cols(0, 1)
        } shouldBe df.select {
            pathOf("name")[0, 1]
        }
    }

    @Test
    fun `cols and get with range`() {
        df.select { all().cols(0..1) } shouldBe df.select { cols(0..1) }
        df.select { all()[0..1] } shouldBe df.select { this[0..1] }

        df.select {
            name.cols(0..1)
        } shouldBe df.select {
            name.colsOf<String>().cols(0..1)
        }

        df.select {
//            name[0..1]
            name.cols(0..1)
        } shouldBe df.select {
            name.colsOf<String>()[0..1]
        }

        df.select {
            "name".cols(0..1)
        } shouldBe df.select {
            Person::name.cols(0..1)
        }

        df.select {
            "name"[0..1]
        } shouldBe df.select {
            Person::name[0..1]
        }

        df.select {
            pathOf("name").cols(0..1)
        } shouldBe df.select {
            pathOf("name")[0..1]
        }
    }
}
