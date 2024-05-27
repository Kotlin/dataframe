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
import org.jetbrains.kotlinx.dataframe.samples.api.secondName
import org.jetbrains.kotlinx.dataframe.samples.api.thirdName
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class AllExceptTests : ColumnsSelectionDslTests() {

    @Test
    fun `exceptions`() {
        shouldThrow<IllegalStateException> {
            dfGroup.select {
                name.firstName.allColsExcept("firstName"["secondName"])
            }
        }

        shouldThrow<IllegalStateException> {
            dfGroup.select {
                name.firstName.allColsExcept(pathOf("name", "firstName", "secondName"))
            }
        }
    }

    @Test
    fun `empty group`() {
        df.select { name.allColsExcept { all() } } shouldBe df.select { none() }

        df.select { allExcept { all() } } shouldBe df.select { none() }

        df.select { allExcept { name.allCols() } }.alsoDebug()

        df.remove { name.allCols() }.alsoDebug()
    }

    @Test
    fun `top-level`() {
        listOf(
            df.select { cols(age, weight, isHappy) },

            df.select { allExcept { name and city } },
            df.select { allExcept(name and city) }, // legacy, but does no harm, so supported
            df.select { allExcept { cols { it.name in listOf("name", "city") } } },
            df.select { allExcept("name", "city") },
            df.select { allExcept(Person::name, Person::city) },
            df.select { allExcept(pathOf("name"), pathOf("city")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { allExcept { name } },
            df.select { allExcept(name) }, // legacy, but does no harm, so supported
            df.select { allExcept { cols { it.name == "name" } } },
            df.select { allExcept("name") },
            df.select { allExcept(Person::name) },
            df.select { allExcept(pathOf("name")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { all() },

            df.select { allExcept { none() } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name and name.firstName }.alsoDebug()
        ).shouldAllBeEqual()

        df.select { (name and name.firstName and name.firstName) except name.firstName }.alsoDebug()

        df.select { (name and name and name.firstName) except name.firstName }.alsoDebug()
    }

    @Test
    fun `on columnSet`() {
        val cityAccessor = column<String?>("city")
        val nameAccessor = column<String>("name")
        listOf(
            df.select { cols(age, weight, isHappy) },

            df.select { cols().except { cols { it.name in listOf("name", "city") } } },
            df.select { cols().except { cityAccessor and nameAccessor } },
            df.select { cols().except { city and name } },
            df.select { cols().except(city and name) },
            df.select { cols().except(city, name) },
            df.select { cols().except(cityAccessor, nameAccessor) },
            df.select { cols().except("city", "name") },
            df.select { cols().except(Person::city, Person::name) },
            df.select { cols().except(pathOf("city"), pathOf("name")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { cols() except { cols { it.name == "name" } } },
            df.select { cols() except cols { it.name == "name" } },
            df.select { cols() except nameAccessor },
            df.select { cols() except name },
            df.select { cols() except "name" },
            df.select { cols() except Person::name },
            df.select { cols() except pathOf("name") },
        ).shouldAllBeEqual()

        listOf(
            df.select { all() },

            df.select { all() except { none() } },
            df.select { all() except none() },
        ).shouldAllBeEqual()

        // might not work as expected, use colsAtAnyDepth instead
        df.select { name.allCols() except { cols { "last" in it.name } } }
        df.select { name.allCols() except cols { "last" in it.name } }
        // or, you know
        df.select { name.cols { "last" !in it.name } }

        listOf(
            df.select { name.firstName },

            df.select { name.allCols() except { colsAtAnyDepth { "last" in it.name } } },
            df.select { name.allCols() except colsAtAnyDepth { "last" in it.name } },
            df.select { name.allCols() except { name.lastName } },
            df.select { name.allCols() except name.lastName },
            df.select { name.allCols() except { colGroup("name").col("lastName") } },
            df.select { name.allCols() except colGroup("name").col("lastName") },
            df.select { name.allCols() except { "name"["lastName"] } },
            df.select { name.allCols() except "name"["lastName"] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.allCols() },

            df.select { name.allCols() except { none() } },
            df.select { name.allCols() except none() },
        ).shouldAllBeEqual()
    }

    @Test
    fun `on lower level`() {
        val lastNameAccessor = column<String>("lastName")
        listOf(
            df.select { name.firstName },

            df.select { name.allColsExcept { lastName } },
            df.select { name.allColsExcept { lastNameAccessor } },
//            df.select { name.allColsExcept(name.lastName) }, // blocked
//            df.select { name.allColsExcept(lastNameAccessor) }, // blocked
            df.select { name.allColsExcept("lastName") },
            df.select { name.allColsExcept("lastName", "lastName") },
            df.select { name.allColsExcept(Name::lastName) },
            df.select { name.allColsExcept(Name::lastName, Name::lastName) },
            df.select { name.allColsExcept(pathOf("lastName")) },
            df.select { name.allColsExcept(pathOf("lastName"), pathOf("lastName")) },
//            df.select { name.allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { name.allColsExcept { cols { "last" in it.name } } },

            df.select { "name".allColsExcept { lastNameAccessor } },
//            df.select { "name".allColsExcept(name.lastName) }, // blocked
//            df.select { "name".allColsExcept(lastNameAccessor) }, // blocked
            df.select { "name".allColsExcept("lastName") },
            df.select { "name".allColsExcept("lastName", "lastName") },
            df.select { "name".allColsExcept(Name::lastName) },
            df.select { "name".allColsExcept(Name::lastName, Name::lastName) },
            df.select { "name".allColsExcept(pathOf("lastName")) },
            df.select { "name".allColsExcept(pathOf("lastName"), pathOf("lastName")) },
//            df.select { "name".allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { "name".allColsExcept { cols { "last" in it.name } } },

//            df.select { Person::name.allColsExcept { lastName } }, https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument
//            df.select { Person::name.allColsExcept { lastNameAccessor } }, https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument
//            df.select { Person::name.allColsExcept(name.lastName) }, // blocked
//            df.select { Person::name.allColsExcept(lastNameAccessor) }, // blocked
            df.select { Person::name.allColsExcept("lastName") },
            df.select { Person::name.allColsExcept("lastName", "lastName") },
            df.select { Person::name.allColsExcept(Name::lastName) },
            df.select { Person::name.allColsExcept(Name::lastName, Name::lastName) },
            df.select { Person::name.allColsExcept(pathOf("lastName")) },
            df.select { Person::name.allColsExcept(pathOf("lastName"), pathOf("lastName")) },
//            df.select { Person::name.allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { Person::name.allColsExcept { cols { "last" in it.name } } },

            df.select { NonDataSchemaPerson::name.allColsExcept { lastName } },
            df.select { NonDataSchemaPerson::name.allColsExcept { lastNameAccessor } },
//            df.select { NonDataSchemaPerson::name.allColsExcept(name.lastName) }, // blocked
//            df.select { NonDataSchemaPerson::name.allColsExcept(lastNameAccessor) }, // blocked
            df.select { NonDataSchemaPerson::name.allColsExcept("lastName") },
            df.select { NonDataSchemaPerson::name.allColsExcept("lastName", "lastName") },
            df.select { NonDataSchemaPerson::name.allColsExcept(Name::lastName) },
            df.select { NonDataSchemaPerson::name.allColsExcept(Name::lastName, Name::lastName) },
            df.select { NonDataSchemaPerson::name.allColsExcept(pathOf("lastName")) },
            df.select { NonDataSchemaPerson::name.allColsExcept(pathOf("lastName"), pathOf("lastName")) },
//            df.select { NonDataSchemaPerson::name.allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { NonDataSchemaPerson::name.allColsExcept { cols { "last" in it.name } } },

            df.select { pathOf("name").allColsExcept { lastNameAccessor } },
//            df.select { pathOf("name").allColsExcept(name.lastName) }, // blocked
//            df.select { pathOf("name").allColsExcept(lastNameAccessor) }, // blocked
            df.select { pathOf("name").allColsExcept("lastName") },
            df.select { pathOf("name").allColsExcept("lastName", "lastName") },
            df.select { pathOf("name").allColsExcept(Name::lastName) },
            df.select { pathOf("name").allColsExcept(Name::lastName, Name::lastName) },
            df.select { pathOf("name").allColsExcept(pathOf("lastName")) },
            df.select { pathOf("name").allColsExcept(pathOf("lastName"), pathOf("lastName")) },
//            df.select { pathOf("name").allColsExcept(pathOf("name", "lastName")) }, // breaks
            df.select { pathOf("name").allColsExcept { cols { "last" in it.name } } },
        ).shouldAllBeEqual()
    }

    @Test
    fun `2 levels deep`() {
        listOf(
            dfGroup.remove { name.firstName.secondName }.select { name.allCols() }.alsoDebug(),

            dfGroup.select {
                name.allColsExcept("firstName"["secondName"])
            },
            dfGroup.select {
                name.allColsExcept { firstName.secondName and firstName.secondName }
            },
            dfGroup.select {
                name.allColsExcept { colGroup("firstName").col("secondName") }
            },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.remove { name.firstName.secondName }.select { name.firstName.allCols() }.alsoDebug(),

            dfGroup.select {
                name.firstName.allColsExcept("secondName")
            },
            dfGroup.select {
                name.firstName.allColsExcept { secondName }
            },
            dfGroup.select {
                name.firstName.allColsExcept(pathOf("secondName"))
            },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { name.firstName { secondName and thirdName } },
            dfGroup.select { name { firstName.allColsExcept("firstName") } }.alsoDebug(),
            dfGroup.select { name { firstName.allColsExcept(pathOf("firstName")) } }.alsoDebug(),
            dfGroup.select { (name.allColsExcept("firstName"["firstName"])).first().asColumnGroup().allCols() },
            dfGroup.remove { name.firstName.firstName }.select { name.firstName.allCols() },
        ).shouldAllBeEqual()

        val secondNameAccessor = column<String?>("secondName")
        val thirdNameAccessor = column<String?>("thirdName")

        listOf(
            dfGroup.select { name.firstName.firstName },

            dfGroup.select { name.firstName.allColsExcept { secondName and thirdName } },
            dfGroup.select { name.firstName.allColsExcept { secondNameAccessor and thirdNameAccessor } },
            dfGroup.select { name.firstName.allColsExcept("secondName", "thirdName") },
            dfGroup.select { name.firstName.allColsExcept(FirstNames::secondName, FirstNames::thirdName) },
            dfGroup.select { name.firstName.allColsExcept(pathOf("secondName"), pathOf("thirdName")) },
            dfGroup.select { name.firstName.allColsExcept { cols { it.name in listOf("secondName", "thirdName") } } },

            dfGroup.select { name.firstName { allExcept { secondName and thirdName } } },
            dfGroup.select { name.firstName { allExcept { secondNameAccessor and thirdNameAccessor } } },
            dfGroup.select { name.firstName { allExcept("secondName", "thirdName") } },
            dfGroup.select { name.firstName { allExcept(FirstNames::secondName, FirstNames::thirdName) } },
            dfGroup.select { name.firstName { allExcept(pathOf("secondName"), pathOf("thirdName")) } },
            dfGroup.select { name.firstName { allExcept { cols { it.name in listOf("secondName", "thirdName") } } } },

            dfGroup.select { name { firstName.allColsExcept { secondName and thirdName } } },
            dfGroup.select { name { firstName.allColsExcept { secondNameAccessor and thirdNameAccessor } } },
            dfGroup.select { name { firstName.allColsExcept("secondName", "thirdName") } },
            dfGroup.select { name { firstName.allColsExcept(FirstNames::secondName, FirstNames::thirdName) } },
            dfGroup.select { name { firstName.allColsExcept(pathOf("secondName"), pathOf("thirdName")) } },
            dfGroup.select { name { firstName.allColsExcept { cols { it.name in listOf("secondName", "thirdName") } } } },

            dfGroup.select { name { firstName { allExcept { secondName and thirdName } } } },
            dfGroup.select { name { firstName { allExcept { secondNameAccessor and thirdNameAccessor } } } },
            dfGroup.select { name { firstName { allExcept("secondName", "thirdName") } } },
            dfGroup.select { name { firstName { allExcept(FirstNames::secondName, FirstNames::thirdName) } } },
            dfGroup.select { name { firstName { allExcept(pathOf("secondName"), pathOf("thirdName")) } } },
            dfGroup.select { name { firstName { allExcept { cols { it.name in listOf("secondName", "thirdName") } } } } },
        ).shouldAllBeEqual()
    }

    @OptIn(ExperimentalExceptCsDsl::class)
    @Test
    fun `except experiments`() {
        val firstNameAccessor = column<String>("firstName")
        listOf(
            df.select { name }.remove { name.firstName }.alsoDebug(),
            df.select { cols(name) except name.firstName },

            df.select { name exceptNew { cols { "first" in it.name } } },
            df.select { name.exceptNew { cols { "first" in it.name } and cols { "first" in it.name } } },
            df.select { name exceptNew { firstName } },
            df.select { name.exceptNew { firstNameAccessor } },
            df.select { name exceptNew { firstName and firstName } },
            df.select { name.exceptNew { firstNameAccessor and firstNameAccessor } },
//            df.select { name exceptNew name.firstName }, // not allowed
//            df.select { name.exceptNew(name.firstName and name.firstName) }, // not allowed
//            df.select { name exceptNew firstNameAccessor }, // not allowed
//            df.select { name.exceptNew(firstNameAccessor and firstNameAccessor) }, // not allowed
            df.select { name exceptNew "firstName" },
            df.select { name.exceptNew("firstName") },
            df.select { name.exceptNew("firstName", "firstName") },
            df.select { name exceptNew Name::firstName },
            df.select { name.exceptNew(Name::firstName) },
            df.select { name.exceptNew(Name::firstName, Name::firstName) },
            df.select { name exceptNew pathOf("firstName") },
            df.select { name.exceptNew(pathOf("firstName")) },
            df.select { name.exceptNew(pathOf("firstName"), pathOf("firstName")) },

            df.select { "name" exceptNew { cols { "first" in it.name } } },
            df.select { "name".exceptNew { cols { "first" in it.name } and cols { "first" in it.name } } },
            df.select { "name".exceptNew { firstNameAccessor } },
            df.select { "name".exceptNew { firstNameAccessor and firstNameAccessor } },
//            df.select { "name" exceptNew name.firstName }, // not allowed
//            df.select { "name".exceptNew(name.firstName and name.firstName) }, // not allowed
//            df.select { "name" exceptNew firstNameAccessor }, // not allowed
//            df.select { "name".exceptNew(firstNameAccessor and firstNameAccessor) }, // not allowed
            df.select { "name" exceptNew "firstName" },
            df.select { "name".exceptNew("firstName") },
            df.select { "name".exceptNew("firstName", "firstName") },
            df.select { "name" exceptNew Name::firstName },
            df.select { "name".exceptNew(Name::firstName) },
            df.select { "name".exceptNew(Name::firstName, Name::firstName) },
            df.select { "name" exceptNew pathOf("firstName") },
            df.select { "name".exceptNew(pathOf("firstName")) },
            df.select { "name".exceptNew(pathOf("firstName"), pathOf("firstName")) },

            // https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument
//            df.select { Person::name exceptNew { cols { "first" in it.name } } },
//            df.select { Person::name.exceptNew { cols { "first" in it.name } and cols { "first" in it.name } } },
//            df.select { Person::name exceptNew { firstName } },
//            df.select { Person::name.exceptNew { firstNameAccessor } },
//            df.select { Person::name exceptNew { firstName and firstName } },
//            df.select { Person::name.exceptNew { firstNameAccessor and firstNameAccessor } },
//            df.select { Person::name exceptNew name.firstName }, // not allowed
//            df.select { Person::name.exceptNew(name.firstName and name.firstName) }, // not allowed
//            df.select { Person::name exceptNew firstNameAccessor }, // not allowed
//            df.select { Person::name.exceptNew(firstNameAccessor and firstNameAccessor) }, // not allowed
            df.select { Person::name exceptNew "firstName" },
            df.select { Person::name.exceptNew("firstName") },
            df.select { Person::name.exceptNew("firstName", "firstName") },
            df.select { Person::name exceptNew Name::firstName },
            df.select { Person::name.exceptNew(Name::firstName) },
            df.select { Person::name.exceptNew(Name::firstName, Name::firstName) },
            df.select { Person::name exceptNew pathOf("firstName") },
            df.select { Person::name.exceptNew(pathOf("firstName")) },
            df.select { Person::name.exceptNew(pathOf("firstName"), pathOf("firstName")) },

            df.select { NonDataSchemaPerson::name exceptNew { cols { "first" in it.name } } },
            df.select { NonDataSchemaPerson::name.exceptNew { cols { "first" in it.name } and cols { "first" in it.name } } },
            df.select { NonDataSchemaPerson::name exceptNew { firstName } },
            df.select { NonDataSchemaPerson::name.exceptNew { firstNameAccessor } },
            df.select { NonDataSchemaPerson::name exceptNew { firstName and firstName } },
            df.select { NonDataSchemaPerson::name.exceptNew { firstNameAccessor and firstNameAccessor } },
//            df.select { NonDataSchemaPerson::name exceptNew name.firstName }, // not allowed
//            df.select { NonDataSchemaPerson::name.exceptNew(name.firstName and name.firstName) }, // not allowed
//            df.select { NonDataSchemaPerson::name exceptNew firstNameAccessor }, // not allowed
//            df.select { NonDataSchemaPerson::name.exceptNew(firstNameAccessor and firstNameAccessor) }, // not allowed
            df.select { NonDataSchemaPerson::name exceptNew "firstName" },
            df.select { NonDataSchemaPerson::name.exceptNew("firstName") },
            df.select { NonDataSchemaPerson::name.exceptNew("firstName", "firstName") },
            df.select { NonDataSchemaPerson::name exceptNew Name::firstName },
            df.select { NonDataSchemaPerson::name.exceptNew(Name::firstName) },
            df.select { NonDataSchemaPerson::name.exceptNew(Name::firstName, Name::firstName) },
            df.select { NonDataSchemaPerson::name exceptNew pathOf("firstName") },
            df.select { NonDataSchemaPerson::name.exceptNew(pathOf("firstName")) },
            df.select { NonDataSchemaPerson::name.exceptNew(pathOf("firstName"), pathOf("firstName")) },

            df.select { pathOf("name") exceptNew { cols { "first" in it.name } } },
            df.select { pathOf("name").exceptNew { cols { "first" in it.name } and cols { "first" in it.name } } },
            df.select { pathOf("name").exceptNew { firstNameAccessor } },
            df.select { pathOf("name").exceptNew { firstNameAccessor and firstNameAccessor } },
//            df.select { pathOf("name") exceptNew name.firstName }, // not allowed
//            df.select { pathOf("name").exceptNew(name.firstName and name.firstName) }, // not allowed
//            df.select { pathOf("name") exceptNew firstNameAccessor }, // not allowed
//            df.select { pathOf("name").exceptNew(firstNameAccessor and firstNameAccessor) }, // not allowed
            df.select { pathOf("name") exceptNew "firstName" },
            df.select { pathOf("name").exceptNew("firstName") },
            df.select { pathOf("name").exceptNew("firstName", "firstName") },
            df.select { pathOf("name") exceptNew Name::firstName },
            df.select { pathOf("name").exceptNew(Name::firstName) },
            df.select { pathOf("name").exceptNew(Name::firstName, Name::firstName) },
            df.select { pathOf("name") exceptNew pathOf("firstName") },
            df.select { pathOf("name").exceptNew(pathOf("firstName")) },
            df.select { pathOf("name").exceptNew(pathOf("firstName"), pathOf("firstName")) },
        ).shouldAllBeEqual()
    }
}
