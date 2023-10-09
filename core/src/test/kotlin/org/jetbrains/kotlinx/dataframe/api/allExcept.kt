package org.jetbrains.kotlinx.dataframe.api

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
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class AllExceptTests : ColumnsSelectionDslTests() {

    @Test
    fun `top-level`() {
        listOf(
            df.select { cols(age, weight, isHappy) },

            df.select { allExcept { name and city } },
            df.select { allExcept { cols { it.name in listOf("name", "city") } } },
            df.select { allExcept(name, city) },
            df.select { allExcept(name and city) },
            df.select { allExcept("name", "city") },
            df.select { allExcept(Person::name, Person::city) },
            df.select { allExcept(pathOf("name"), pathOf("city")) },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(age, city, weight, isHappy) },

            df.select { allExcept { name } },
            df.select { allExcept { cols { it.name == "name" } } },
            df.select { allExcept(name) },
            df.select { allExcept("name") },
            df.select { allExcept(Person::name) },
            df.select { allExcept(pathOf("name")) },
        ).shouldAllBeEqual()
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
    }

    @Test
    fun `empty group`() {
        // TODO
        df.select {
            all() except name.allCols()
        }.alsoDebug()
    }

    @Test
    fun `relative path`() {
        listOf(
            df.select { name.allColsExcept { lastName } }.alsoDebug(),

//            df.select { name.allColsExcept("name"["lastName"]) }.alsoDebug(),
            df.select { name.allColsExcept { "lastName"() } }.alsoDebug(),
            df.select { name.allColsExcept("lastName") }.alsoDebug(),
        ).shouldAllBeEqual()
    }

    @Test
    fun temp() {
        df.alsoDebug()

        df.select {
            name.allColsExcept("lastName")
        }.alsoDebug()

        df.select {
            name.allColsExcept("name"["lastName"])
        }.alsoDebug()
    }

    @Test
    fun `lower level`() {
//        df.select {
//            name.allColsExcept("name"["lastName"])
//        }.alsoDebug()

        df.select {
//            name.allColsExcept("lastName")
            name.allColsExcept(first { true })
        }.alsoDebug()

        val lastNameAccessor = column<String>("lastName")
        val fullLastNameAccessor = column<String>(pathOf("name", "lastName"))
        listOf(
            df.select { name.firstName },

            df.select { name.allColsExcept { lastName } },
            df.select { name.allColsExcept { cols { it.name == "lastName" } } },
            df.select { name.allColsExcept(name.lastName, name.lastName) },
            df.select { name.allColsExcept(lastNameAccessor, lastNameAccessor) },
            df.select { name.allColsExcept("lastName", "lastName") },
            df.select { name.allColsExcept(Name::lastName, Name::lastName) },
            df.select { name.allColsExcept(pathOf("lastName"), pathOf("lastName")) },

            df.select { name allColsExcept name.lastName },
            df.select { name allColsExcept (name.lastName and name.lastName) },
//            df.select { name allColsExcept (name.lastName and "lastName") },//
            df.select { name.allCols() except name.lastName },
            df.select { name allColsExcept fullLastNameAccessor },
            df.select { name.allCols() except fullLastNameAccessor },

            df.select { name allColsExcept "lastName" },
            df.select { name allColsExcept Name::lastName },
            df.select { name allColsExcept pathOf("lastName") },
        ).shouldAllBeEqual()
    }

    @Test
    fun `ambiguous cases`() {
        @Language("json")
        val json = """
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

            df.select { "a".allColsExcept("a") }.alsoDebug(), // ambiguous!
//            df.select { "a".allColsExcept("a"["a"]) }.alsoDebug(),
        ).shouldAllBeEqual()
    }
}
