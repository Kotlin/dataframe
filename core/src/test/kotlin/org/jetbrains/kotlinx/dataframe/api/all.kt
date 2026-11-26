package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.jetbrains.kotlinx.dataframe.samples.api.thirdName
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
            df.select { all().all() },
            df.select { all() },
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
            df.select { allBefore { city } },
            df.select { allBefore { first { it.name.startsWith("c") } } },
            df.select { allBefore(city) },
            df.select { allBefore("city") },
            df.select { allBefore(Person::city) },
            df.select { allBefore(pathOf("city")) },
            df.select { allUpTo { age } },
            df.select { allUpTo { first { it.name.startsWith("a") } } },
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
            df.select { allFrom { weight } },
            df.select { allFrom { first { it.name.startsWith("w") } } },
            df.select { allFrom(weight) },
            df.select { allFrom("weight") },
            df.select { allFrom(Person::weight) },
            df.select { allFrom(pathOf("weight")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `all on columnSet`() {
        val cityAccessor = column<String?>("city")
        val weightAccessor = column<Int?>("weight")
        listOf(
            df.select { weight and isHappy },
            df.select { all().allAfter { "city" in it.name } },
            df.select { all().allAfter { it.data == cityAccessor() } },
            df.select { all().allAfter { it.data == city } },
            df.select { all().allAfter(city) },
            df.select { all().allAfter(cityAccessor) },
            df.select { all().allAfter("city") },
            df.select { all().allAfter(Person::city) },
            df.select { all().allAfter(pathOf("city")) },
            df.select { all().allFrom { "weight" in it.name } },
            df.select { all().allFrom { it.data == weightAccessor() } },
            df.select { all().allFrom { it.data == weight } },
            df.select { all().allFrom(weight) },
            df.select { all().allFrom(weightAccessor) },
            df.select { all().allFrom("weight") },
            df.select { all().allFrom(Person::weight) },
            df.select { all().allFrom(pathOf("weight")) },
        ).shouldAllBeEqual()

        val ageAccessor = column<Int>("age")
        listOf(
            df.select { name and age },
            df.select { all().allBefore { "city" in it.name } },
            df.select { all().allBefore { it.data == cityAccessor() } },
            df.select { all().allBefore { it.data == city } },
            df.select { all().allBefore(city) },
            df.select { all().allBefore(cityAccessor) },
            df.select { all().allBefore("city") },
            df.select { all().allBefore(Person::city) },
            df.select { all().allBefore(pathOf("city")) },
            df.select { all().allUpTo { "age" in it.name } },
            df.select { all().allUpTo { it.data == ageAccessor() } },
            df.select { all().allUpTo { it.data == age } },
            df.select { all().allUpTo(age) },
            df.select { all().allUpTo(ageAccessor) },
            df.select { all().allUpTo("age") },
            df.select { all().allUpTo(Person::age) },
            df.select { all().allUpTo(pathOf("age")) },
        ).shouldAllBeEqual()
    }

    @Suppress("CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
    @Test
    fun `all subset at lower level after and from`() {
        listOf(
            df.select { age },
            df.select { colsOf<Int?>().allBefore(weight) },
            df.select { allBefore(weight).colsOf<Int?>() },
        ).shouldAllBeEqual()

        val firstNameAccessor = column<String>("firstName")
        val lastNameAccessor = column<String>("lastName")

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
            df.select { NonDataSchemaPerson::name.allColsAfter { firstName } },
            df.select { NonDataSchemaPerson::name.allColsAfter { firstNameAccessor } },
            df.select { NonDataSchemaPerson::name.allColsAfter(name.firstName) },
            df.select { NonDataSchemaPerson::name.allColsAfter(firstNameAccessor) },
            df.select { NonDataSchemaPerson::name.allColsAfter("firstName") },
            df.select { NonDataSchemaPerson::name.allColsAfter(Name::firstName) },
            df.select { NonDataSchemaPerson::name.allColsAfter(pathOf("firstName")) },
            df.select { NonDataSchemaPerson::name.allColsAfter(pathOf("name", "firstName")) },
            df.select { pathOf("name").allColsAfter { firstNameAccessor } },
            df.select { pathOf("name").allColsAfter(name.firstName) },
            df.select { pathOf("name").allColsAfter(firstNameAccessor) },
            df.select { pathOf("name").allColsAfter("firstName") },
            df.select { pathOf("name").allColsAfter(Name::firstName) },
            df.select { pathOf("name").allColsAfter(pathOf("firstName")) },
            df.select { pathOf("name").allColsAfter(pathOf("name", "firstName")) },
            df.select { name.allColsFrom { lastName } },
            df.select { name.allColsFrom { lastNameAccessor } },
            df.select { name.allColsFrom(name.lastName) },
            df.select { name.allColsFrom(lastNameAccessor) },
            df.select { name.allColsFrom("lastName") },
            df.select { name.allColsFrom(Name::lastName) },
            df.select { name.allColsFrom(pathOf("lastName")) },
            df.select { name.allColsFrom(pathOf("name", "lastName")) },
            df.select { "name".allColsFrom { lastNameAccessor } },
            df.select { "name".allColsFrom(name.lastName) },
            df.select { "name".allColsFrom(lastNameAccessor) },
            df.select { "name".allColsFrom("lastName") },
            df.select { "name".allColsFrom(Name::lastName) },
            df.select { "name".allColsFrom(pathOf("lastName")) },
            df.select { "name".allColsFrom(pathOf("name", "lastName")) },
            df.select { Person::name.allColsFrom { lastNameAccessor } },
            df.select { Person::name.allColsFrom(name.lastName) },
            df.select { Person::name.allColsFrom(lastNameAccessor) },
            df.select { Person::name.allColsFrom("lastName") },
            df.select { Person::name.allColsFrom(Name::lastName) },
            df.select { Person::name.allColsFrom(pathOf("lastName")) },
            df.select { Person::name.allColsFrom(pathOf("name", "lastName")) },
            df.select { NonDataSchemaPerson::name.allColsFrom { lastName } },
            df.select { NonDataSchemaPerson::name.allColsFrom { lastNameAccessor } },
            df.select { NonDataSchemaPerson::name.allColsFrom(name.lastName) },
            df.select { NonDataSchemaPerson::name.allColsFrom(lastNameAccessor) },
            df.select { NonDataSchemaPerson::name.allColsFrom("lastName") },
            df.select { NonDataSchemaPerson::name.allColsFrom(Name::lastName) },
            df.select { NonDataSchemaPerson::name.allColsFrom(pathOf("lastName")) },
            df.select { NonDataSchemaPerson::name.allColsFrom(pathOf("name", "lastName")) },
            df.select { pathOf("name").allColsFrom { lastNameAccessor } },
            df.select { pathOf("name").allColsFrom(name.lastName) },
            df.select { pathOf("name").allColsFrom(lastNameAccessor) },
            df.select { pathOf("name").allColsFrom("lastName") },
            df.select { pathOf("name").allColsFrom(Name::lastName) },
            df.select { pathOf("name").allColsFrom(pathOf("lastName")) },
            df.select { pathOf("name").allColsFrom(pathOf("name", "lastName")) },
        ).shouldAllBeEqual()
    }

    @Suppress("CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
    @Test
    fun `all subset at lower level before and upTo`() {
        val firstNameAccessor = column<String>("firstName")
        val lastNameAccessor = column<String>("lastName")

        listOf(
            df.select { name.firstName },
            df.select { name.allColsBefore { lastName } },
            df.select { name.allColsBefore { lastNameAccessor } },
            df.select { name.allColsBefore(name.lastName) }, // full path
            df.select { name.allColsBefore(lastNameAccessor) },
            df.select { name.allColsBefore("lastName") },
            df.select { name.allColsBefore(Name::lastName) },
            df.select { name.allColsBefore(pathOf("lastName")) },
            df.select { name.allColsBefore(pathOf("name", "lastName")) }, // full path
            df.select { "name".allColsBefore { lastNameAccessor } },
            df.select { "name".allColsBefore(name.lastName) },
            df.select { "name".allColsBefore(lastNameAccessor) },
            df.select { "name".allColsBefore("lastName") },
            df.select { "name".allColsBefore(Name::lastName) },
            df.select { "name".allColsBefore(pathOf("lastName")) },
            df.select { "name".allColsBefore(pathOf("name", "lastName")) },
            df.select { Person::name.allColsBefore { lastNameAccessor } },
            df.select { Person::name.allColsBefore(name.lastName) },
            df.select { Person::name.allColsBefore(lastNameAccessor) },
            df.select { Person::name.allColsBefore("lastName") },
            df.select { Person::name.allColsBefore(Name::lastName) },
            df.select { Person::name.allColsBefore(pathOf("lastName")) },
            df.select { Person::name.allColsBefore(pathOf("name", "lastName")) },
            df.select { NonDataSchemaPerson::name.allColsBefore { lastName } },
            df.select { NonDataSchemaPerson::name.allColsBefore { lastNameAccessor } },
            df.select { NonDataSchemaPerson::name.allColsBefore(name.lastName) },
            df.select { NonDataSchemaPerson::name.allColsBefore(lastNameAccessor) },
            df.select { NonDataSchemaPerson::name.allColsBefore("lastName") },
            df.select { NonDataSchemaPerson::name.allColsBefore(Name::lastName) },
            df.select { NonDataSchemaPerson::name.allColsBefore(pathOf("lastName")) },
            df.select { NonDataSchemaPerson::name.allColsBefore(pathOf("name", "lastName")) },
            df.select { pathOf("name").allColsBefore { lastNameAccessor } },
            df.select { pathOf("name").allColsBefore(name.lastName) },
            df.select { pathOf("name").allColsBefore(lastNameAccessor) },
            df.select { pathOf("name").allColsBefore("lastName") },
            df.select { pathOf("name").allColsBefore(Name::lastName) },
            df.select { pathOf("name").allColsBefore(pathOf("lastName")) },
            df.select { pathOf("name").allColsBefore(pathOf("name", "lastName")) },
            df.select { name.allColsUpTo { firstName } },
            df.select { name.allColsUpTo { firstNameAccessor } },
            df.select { name.allColsUpTo(name.firstName) },
            df.select { name.allColsUpTo(firstNameAccessor) },
            df.select { name.allColsUpTo("firstName") },
            df.select { name.allColsUpTo(Name::firstName) },
            df.select { name.allColsUpTo(pathOf("firstName")) },
            df.select { name.allColsUpTo(pathOf("name", "firstName")) },
            df.select { "name".allColsUpTo { firstNameAccessor } },
            df.select { "name".allColsUpTo(name.firstName) },
            df.select { "name".allColsUpTo(firstNameAccessor) },
            df.select { "name".allColsUpTo("firstName") },
            df.select { "name".allColsUpTo(Name::firstName) },
            df.select { "name".allColsUpTo(pathOf("firstName")) },
            df.select { "name".allColsUpTo(pathOf("name", "firstName")) },
            df.select { Person::name.allColsUpTo { firstNameAccessor } },
            df.select { Person::name.allColsUpTo(name.firstName) },
            df.select { Person::name.allColsUpTo(firstNameAccessor) },
            df.select { Person::name.allColsUpTo("firstName") },
            df.select { Person::name.allColsUpTo(Name::firstName) },
            df.select { Person::name.allColsUpTo(pathOf("firstName")) },
            df.select { Person::name.allColsUpTo(pathOf("name", "firstName")) },
            df.select { NonDataSchemaPerson::name.allColsUpTo { firstName } },
            df.select { NonDataSchemaPerson::name.allColsUpTo { firstNameAccessor } },
            df.select { NonDataSchemaPerson::name.allColsUpTo(name.firstName) },
            df.select { NonDataSchemaPerson::name.allColsUpTo(firstNameAccessor) },
            df.select { NonDataSchemaPerson::name.allColsUpTo("firstName") },
            df.select { NonDataSchemaPerson::name.allColsUpTo(Name::firstName) },
            df.select { NonDataSchemaPerson::name.allColsUpTo(pathOf("firstName")) },
            df.select { NonDataSchemaPerson::name.allColsUpTo(pathOf("name", "firstName")) },
            df.select { pathOf("name").allColsUpTo { firstNameAccessor } },
            df.select { pathOf("name").allColsUpTo(name.firstName) },
            df.select { pathOf("name").allColsUpTo(firstNameAccessor) },
            df.select { pathOf("name").allColsUpTo("firstName") },
            df.select { pathOf("name").allColsUpTo(Name::firstName) },
            df.select { pathOf("name").allColsUpTo(pathOf("firstName")) },
            df.select { pathOf("name").allColsUpTo(pathOf("name", "firstName")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `2 levels deep`() {
        val secondNameAccessor = column<String?>("secondName")
        val thirdNameAccessor = column<String?>("thirdName")
        listOf(
            dfGroup.select { name.firstName { firstName and secondName } },
            dfGroup.select { name.firstName.allColsBefore { thirdName } },
            dfGroup.select { name.firstName.allColsBefore { thirdNameAccessor } },
            dfGroup.select { name.firstName.allColsBefore(name.firstName.thirdName) },
            dfGroup.select { name.firstName.allColsBefore("thirdName") },
            dfGroup.select { name.firstName.allColsBefore(FirstNames::thirdName) },
            dfGroup.select { name.firstName.allColsBefore(thirdNameAccessor) },
            dfGroup.select { name.firstName.allColsBefore(pathOf("thirdName")) },
            dfGroup.select { name.firstName.allColsBefore(pathOf("name", "firstName", "thirdName")) },
            dfGroup.select { name.firstName.allColsUpTo { secondName } },
            dfGroup.select { name.firstName.allColsUpTo { secondNameAccessor } },
            dfGroup.select { name.firstName.allColsUpTo(name.firstName.secondName) },
            dfGroup.select { name.firstName.allColsUpTo("secondName") },
            dfGroup.select { name.firstName.allColsUpTo(FirstNames::secondName) },
            dfGroup.select { name.firstName.allColsUpTo(secondNameAccessor) },
            dfGroup.select { name.firstName.allColsUpTo(pathOf("secondName")) },
            dfGroup.select { name.firstName.allColsUpTo(pathOf("name", "firstName", "secondName")) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `ambiguous cases`() {
        @Language("json")
        val json =
            """
            {
                 "a": {
                     "a": 1,
                     "b": 2
                 }
            }
            """.trimIndent()

        val df = DataFrame.readJsonStr(json).alsoDebug()

        listOf(
            df.select { "a"["b"] }.alsoDebug(),
            df.select { "a".allColsAfter("a") },
            df.select { "a".allColsAfter("a"["a"]) },
        ).shouldAllBeEqual()
    }
}
