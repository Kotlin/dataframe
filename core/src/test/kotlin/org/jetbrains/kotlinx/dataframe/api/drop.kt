package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class DropTests : ColumnsSelectionDslTests() {

    @Test
    fun `drop and dropLast`() {
        listOf(
            df.select { name.lastName },
            df.select { name.dropCols(1) },
            df.select { "name".dropCols(1) },
            df.select { Person::name.dropCols(1) },
            df.select { pathOf("name").dropCols(1) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLastCols(1) },
            df.select { "name".dropLastCols(1) },
            df.select { Person::name.dropLastCols(1) },
            df.select { pathOf("name").dropLastCols(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `dropWhile and dropLastWhile`() {
        listOf(
            df.select { name.lastName },
            df.select { name.dropColsWhile { it.name == "firstName" } },
            df.select { "name".dropColsWhile { it.name == "firstName" } },
            df.select { Person::name.dropColsWhile { it.name == "firstName" } },
            df.select { pathOf("name").dropColsWhile { it.name == "firstName" } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLastColsWhile { it.name == "lastName" } },
            df.select { "name".dropLastColsWhile { it.name == "lastName" } },
            df.select { Person::name.dropLastColsWhile { it.name == "lastName" } },
            df.select { pathOf("name").dropLastColsWhile { it.name == "lastName" } },
        ).shouldAllBeEqual()
    }
}
