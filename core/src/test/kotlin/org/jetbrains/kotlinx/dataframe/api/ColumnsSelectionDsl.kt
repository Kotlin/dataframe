package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
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
            age and name.allColsExcept {
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
                name.allColsExcept { lastName }
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
