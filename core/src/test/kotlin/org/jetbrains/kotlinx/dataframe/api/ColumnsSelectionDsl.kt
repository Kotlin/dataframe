package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

open class ColumnsSelectionDslTests : TestBase() {

    @DataSchema
    interface PersonWithFrame : Person {
        val frameCol: DataFrame<Person>
    }

    protected val frameCol by frameColumn<Person>()

    protected val dfWithFrames = df
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
    fun `how does except work`() {
        dfGroup.select {
            age and name.exceptNew {
                firstName.secondName
            }
        }.alsoDebug()

        dfGroup.select {
            age and name.allExcept {
                firstName.secondName
            }
        }.alsoDebug()
    }

    @Test
    fun `allExcept with selector`() {
        listOf(
            df.select {
                name.firstName
            }.alsoDebug(),

            df.select {
                name.select { firstName }
            },
            df.select {
                name { firstName }
            },
            df.select {
                name.allExcept { lastName }
            },
//            df.select {
//                name - { lastName }
//            },
//            df.select {
//                name.remove { lastName }
//            },
//            df.remove {
//                name.lastName
//            },
//            df.remove {
//                name { lastName }
//            },
//            df.select {
//                name.except(name.lastName)
//            },
//            df.select {
//                name - (name.lastName and name.lastName)
//            },
        ).shouldAllBeEqual()
    }

    @Test
    fun `take and takeLast`() {
        listOf(
            df.select { name.firstName },
            df.select { name.takeChildren(1) },
            df.select { "name".takeChildren(1) },
            df.select { Person::name.takeChildren(1) },
            df.select { pathOf("name").takeChildren(1) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.takeLastChildren(1) },
            df.select { "name".takeLastChildren(1) },
            df.select { Person::name.takeLastChildren(1) },
            df.select { pathOf("name").takeLastChildren(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `drop and dropLast`() {
        listOf(
            df.select { name.lastName },
            df.select { name.dropChildren(1) },
            df.select { "name".dropChildren(1) },
            df.select { Person::name.dropChildren(1) },
            df.select { pathOf("name").dropChildren(1) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLastChildren(1) },
            df.select { "name".dropLastChildren(1) },
            df.select { Person::name.dropLastChildren(1) },
            df.select { pathOf("name").dropLastChildren(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `takeWhile and takeLastWhile`() {
        listOf(
            df.select { name.firstName },
            df.select { name.takeChildrenWhile { it.name == "firstName" } },
            df.select { "name".takeChildrenWhile { it.name == "firstName" } },
            df.select { Person::name.takeChildrenWhile { it.name == "firstName" } },
            df.select { pathOf("name").takeChildrenWhile { it.name == "firstName" } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.takeLastChildrenWhile { it.name == "lastName" } },
            df.select { "name".takeLastChildrenWhile { it.name == "lastName" } },
            df.select { Person::name.takeLastChildrenWhile { it.name == "lastName" } },
            df.select { pathOf("name").takeLastChildrenWhile { it.name == "lastName" } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `dropWhile and dropLastWhile`() {
        listOf(
            df.select { name.lastName },
            df.select { name.dropChildrenWhile { it.name == "firstName" } },
            df.select { "name".dropChildrenWhile { it.name == "firstName" } },
            df.select { Person::name.dropChildrenWhile { it.name == "firstName" } },
            df.select { pathOf("name").dropChildrenWhile { it.name == "firstName" } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLastChildrenWhile { it.name == "lastName" } },
            df.select { "name".dropLastChildrenWhile { it.name == "lastName" } },
            df.select { Person::name.dropLastChildrenWhile { it.name == "lastName" } },
            df.select { pathOf("name").dropLastChildrenWhile { it.name == "lastName" } },
        ).shouldAllBeEqual()
    }

    @Test
    fun nameContains() {
        listOf(
            df.select { name.firstName },
            df.select { name.childrenNameContains("first") },
            df.select { "name".childrenNameContains("first") },
            df.select { Person::name.childrenNameContains("first") },
            df.select { pathOf("name").childrenNameContains("first") },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.childrenNameContains(Regex("last")) },
            df.select { "name".childrenNameContains(Regex("last")) },
            df.select { Person::name.childrenNameContains(Regex("last")) },
            df.select { pathOf("name").childrenNameContains(Regex("last")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `nameStartsWith and nameEndsWith`() {
        listOf(
            df.select { name.firstName },
            df.select { name.childrenNameStartsWith("first") },
            df.select { "name".childrenNameStartsWith("first") },
            df.select { Person::name.childrenNameStartsWith("first") },
            df.select { pathOf("name").childrenNameStartsWith("first") },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.childrenNameEndsWith("Name") },
            df.select { "name".childrenNameEndsWith("Name") },
            df.select { Person::name.childrenNameEndsWith("Name") },
            df.select { pathOf("name").childrenNameEndsWith("Name") },
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

//            select { this { age } }
        }
    }
}
