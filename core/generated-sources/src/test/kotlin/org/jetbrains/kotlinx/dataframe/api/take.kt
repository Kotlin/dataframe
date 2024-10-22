package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class TakeTests : ColumnsSelectionDslTests() {

    @Test
    fun `take and takeLast`() {
        listOf(
            df.select { name.firstName },
            df.select { name.takeCols(1) },
            df.select { "name".takeCols(1) },
            df.select { Person::name.takeCols(1) },
            df.select { pathOf("name").takeCols(1) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.takeLastCols(1) },
            df.select { "name".takeLastCols(1) },
            df.select { Person::name.takeLastCols(1) },
            df.select { pathOf("name").takeLastCols(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `takeWhile and takeLastWhile`() {
        listOf(
            df.select { name.firstName },
            df.select { name.takeColsWhile { it.name == "firstName" } },
            df.select { "name".takeColsWhile { it.name == "firstName" } },
            df.select { Person::name.takeColsWhile { it.name == "firstName" } },
            df.select { pathOf("name").takeColsWhile { it.name == "firstName" } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.lastName },
            df.select { name.takeLastColsWhile { it.name == "lastName" } },
            df.select { "name".takeLastColsWhile { it.name == "lastName" } },
            df.select { Person::name.takeLastColsWhile { it.name == "lastName" } },
            df.select { pathOf("name").takeLastColsWhile { it.name == "lastName" } },
        ).shouldAllBeEqual()
    }
}
