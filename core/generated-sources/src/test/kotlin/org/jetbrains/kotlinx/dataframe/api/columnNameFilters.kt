package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class ColumnNameFiltersTests : ColumnsSelectionDslTests() {

    @Test
    fun nameContains() {
        listOf(
            df.select { age },

            df.select { nameContains("age") },
            df.select { nameContains("AGE", ignoreCase = true) },
            df.select { nameContains(Regex("age")) },

            df.select { all().nameContains("age") },
            df.select { all().nameContains("AGE", ignoreCase = true) },
            df.select { all().nameContains(Regex("age")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },

            df.select { name.colsNameContains("first") },
            df.select { "name".colsNameContains("first") },
            df.select { Person::name.colsNameContains("first") },
            df.select { pathOf("name").colsNameContains("first") },

            df.select { name.colsNameContains("FIRST", ignoreCase = true) },
            df.select { "name".colsNameContains("FIRST", ignoreCase = true) },
            df.select { Person::name.colsNameContains("FIRST", ignoreCase = true) },
            df.select { pathOf("name").colsNameContains("FIRST", ignoreCase = true) },

            df.select { name.colsNameContains(Regex("first")) },
            df.select { "name".colsNameContains(Regex("first")) },
            df.select { Person::name.colsNameContains(Regex("first")) },
            df.select { pathOf("name").colsNameContains(Regex("first")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `nameStartsWith and nameEndsWith`() {
        listOf(
            df.select { age },

            df.select { nameStartsWith("age") },
            df.select { nameStartsWith("AGE", ignoreCase = true) },
            df.select { nameEndsWith("age") },
            df.select { nameEndsWith("AGE", ignoreCase = true) },

            df.select { all().nameStartsWith("age") },
            df.select { all().nameStartsWith("AGE", ignoreCase = true) },
            df.select { all().nameEndsWith("age") },
            df.select { all().nameEndsWith("AGE", ignoreCase = true) },
        ).shouldAllBeEqual()


        listOf(
            df.select { name.firstName },

            df.select { name.colsNameStartsWith("first") },
            df.select { "name".colsNameStartsWith("first") },
            df.select { Person::name.colsNameStartsWith("first") },
            df.select { pathOf("name").colsNameStartsWith("first") },

            df.select { name.colsNameStartsWith("FIRST", ignoreCase = true) },
            df.select { "name".colsNameStartsWith("FIRST", ignoreCase = true) },
            df.select { Person::name.colsNameStartsWith("FIRST", ignoreCase = true) },
            df.select { pathOf("name").colsNameStartsWith("FIRST", ignoreCase = true) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.colsNameEndsWith("Name") },
            df.select { "name".colsNameEndsWith("Name") },
            df.select { Person::name.colsNameEndsWith("Name") },
            df.select { pathOf("name").colsNameEndsWith("Name") },

            df.select { name.colsNameEndsWith("NAME", ignoreCase = true) },
            df.select { "name".colsNameEndsWith("NAME", ignoreCase = true) },
            df.select { Person::name.colsNameEndsWith("NAME", ignoreCase = true) },
            df.select { pathOf("name").colsNameEndsWith("NAME", ignoreCase = true) },
        ).shouldAllBeEqual()
    }
}
