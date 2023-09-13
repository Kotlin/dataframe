package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.jetbrains.kotlinx.dataframe.samples.api.thirdName
import org.junit.Test

class ColsInGroupsTests : ColumnsSelectionDslTests() {

    @Test
    fun `cols In Groups`() {
        listOf(
            df.select { name { firstName and lastName } },

            df.select { colsInGroups() },
            df.select { all().colsInGroups() },
            df.select { colGroups().colsInGroups() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },

            df.select { colsInGroups { "first" in it.name } },
            df.select { all().colsInGroups { "first" in it.name } },
            df.select { colGroups().colsInGroups { "first" in it.name } },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { name.firstName { cols(firstName, secondName, thirdName) } },

            dfGroup.select { name.colsInGroups() },
            dfGroup.select { "name".colsInGroups() },
            dfGroup.select { Person2::name.colsInGroups() },
            dfGroup.select { pathOf("name").colsInGroups() },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { name.firstName.firstName },

            dfGroup.select { name.colsInGroups { "first" in it.name } },
            dfGroup.select { "name".colsInGroups { "first" in it.name } },
            dfGroup.select { Person2::name.colsInGroups { "first" in it.name } },
            dfGroup.select { pathOf("name").colsInGroups { "first" in it.name } },
        ).shouldAllBeEqual()
    }
}
