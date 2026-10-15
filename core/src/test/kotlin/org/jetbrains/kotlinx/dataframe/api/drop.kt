package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
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

    @Test
    fun `dropWhile with a predicate that holds for every row drops all rows`() {
        val numbers = dataFrameOf("n")(1, 2, 3)

        val empty = numbers.dropWhile { true }

        empty.rowsCount() shouldBe 0
        empty.columnNames() shouldBe listOf("n")
    }

    @Test
    fun `dropWhile drops the leading rows that satisfy the predicate`() {
        val numbers = dataFrameOf("n")(1, 2, 3, 1)

        numbers.dropWhile { "n"<Int>() < 3 } shouldBe dataFrameOf("n")(3, 1)
    }
}
