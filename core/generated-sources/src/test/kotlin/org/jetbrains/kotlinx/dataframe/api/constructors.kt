package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class ConstructorsTests : ColumnsSelectionDslTests() {

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
}
