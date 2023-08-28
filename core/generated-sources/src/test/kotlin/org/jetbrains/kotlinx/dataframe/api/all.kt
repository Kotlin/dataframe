package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class AllTests : ColumnsSelectionDslTests() {

    @Test
    fun `all exceptions`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".allCols() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { "age".allColsBefore("") }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { "age".allColsAfter("") }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { "age".allColsUpTo("") }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { "age".allColsFrom("") }
        }

        df.select { allAfter("nonExistent") } shouldBe df.select { none() }
        df.select { allBefore("nonExistent") } shouldBe df.select { all() }
        df.select { allUpTo("nonExistent") } shouldBe df.select { all() }
        df.select { allFrom("nonExistent") } shouldBe df.select { none() }
    }

    @Test
    fun all() {
        listOf(
            df.select { cols(name, age, city, weight, isHappy) },

            df.select { all() },
            df.select { all().all() },
            df.select { cols().all() },
            df.select { cols() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.allCols() },
            df.select { name.allCols().all() },
        ).shouldAllBeEqual()
    }

    @Test
    fun `all subset top-level`() {
        listOf(
            df.select { name and age },

            df.select { allBefore(city) },
            df.select { allBefore("city") },
            df.select { allBefore(Person::city) },
            df.select { allBefore(pathOf("city")) },


            df.select { allUpTo(age) },
            df.select { allUpTo("age") },
            df.select { allUpTo(Person::age) },
            df.select { allUpTo(pathOf("age")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { weight and isHappy },

            df.select { allAfter { city } },
            df.select { allAfter { first { it.name.startsWith("c") } } },
            df.select { allAfter(city) },
            df.select { allAfter("city") },
            df.select { allAfter(Person::city) },
            df.select { allAfter(pathOf("city")) },

            df.select { allFrom(weight) },
            df.select { allFrom("weight") },
            df.select { allFrom(Person::weight) },
            df.select { allFrom(pathOf("weight")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `all on columnSet`() {
        val cityAccessor = column<String>("city")
        listOf(
            df.select { weight and isHappy },

            df.select { cols().allAfter { nameContains("city").single() } }.alsoDebug(),
            df.select { cols().allAfter { cityAccessor } },
            df.select { cols().allAfter(city) },
            df.select { cols().allAfter(cityAccessor) },
            df.select { cols().allAfter("city") },
            df.select { cols().allAfter(Person::city) },
            df.select { cols().allAfter(pathOf("city")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `all subset at lower level`() {
        listOf(
            df.select { age },
            df.select { colsOf<Int?>().allBefore(weight) },
            df.select { allBefore(weight).colsOf<Int?>() },
        ).shouldAllBeEqual()

        val firstNameAccessor = column<String>("firstName")

        listOf(
            df.select { name.lastName },

            df.select { name.allColsAfter { firstName } },
            df.select { name.allColsAfter { firstNameAccessor } },
            df.select { name.allColsAfter(name.firstName) },
            df.select { name.allColsAfter(firstNameAccessor) },
            df.select { name.allColsAfter("firstName") },
            df.select { name.allColsAfter(Name::firstName) },
            df.select { name.allColsAfter(pathOf("firstName")) },
            df.select { name.allColsAfter(pathOf("name", "firstName")) },

            df.select { "name".allColsAfter { firstNameAccessor } },
            df.select { "name".allColsAfter(name.firstName) },
            df.select { "name".allColsAfter(firstNameAccessor) },
            df.select { "name".allColsAfter("firstName") },
            df.select { "name".allColsAfter(Name::firstName) },
            df.select { "name".allColsAfter(pathOf("firstName")) },
            df.select { "name".allColsAfter(pathOf("name", "firstName")) },

            df.select { Person::name.allColsAfter { firstNameAccessor } },
            df.select { Person::name.allColsAfter(name.firstName) },
            df.select { Person::name.allColsAfter(firstNameAccessor) },
            df.select { Person::name.allColsAfter("firstName") },
            df.select { Person::name.allColsAfter(Name::firstName) },
            df.select { Person::name.allColsAfter(pathOf("firstName")) },
            df.select { Person::name.allColsAfter(pathOf("name", "firstName")) },

            df.select { pathOf("name").allColsAfter { firstNameAccessor } },
            df.select { pathOf("name").allColsAfter(name.firstName) },
            df.select { pathOf("name").allColsAfter(firstNameAccessor) },
            df.select { pathOf("name").allColsAfter("firstName") },
            df.select { pathOf("name").allColsAfter(Name::firstName) },
            df.select { pathOf("name").allColsAfter(pathOf("firstName")) },
            df.select { pathOf("name").allColsAfter(pathOf("name", "firstName")) },
        ).shouldAllBeEqual()

        // TODO

    }
}
