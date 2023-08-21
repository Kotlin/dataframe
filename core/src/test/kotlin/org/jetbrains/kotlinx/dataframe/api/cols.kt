package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class ColsTests : ColumnsSelectionDslTests() {

    @Test
    fun `cols and get with predicate`() {
        listOf(
            df.select { cols(name, age, city, weight, isHappy) },
            df.select { all().cols() },
            df.select { cols() },
            df.select { all() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },
            df.select { name }.select { all() },
            df.select { name }.select { cols() },
            df.select { name }.select { cols().all() },
            df.select { name }.select { all().cols() },
        ).shouldAllBeEqual()

        listOf(
            df.select { cols(name, age, weight) },

            df.select { cols { "e" in it.name() } },
            df.select { this[{ "e" in it.name() }] },

            df.select { all().cols { "e" in it.name() } },
            df.select { all()[{ "e" in it.name() }] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols { "Name" in it.name() } },
            df.select { name[{ "Name" in it.name() }] },

            df.select { name.colsOf<String>().cols { "Name" in it.name() } },
            df.select { name.colsOf<String>()[{ "Name" in it.name() }] },

            df.select { "name".cols { "Name" in it.name() } },
            df.select { "name"[{ "Name" in it.name() }] },

            df.select { Person::name.cols { "Name" in it.name() } },
            df.select { Person::name[{ "Name" in it.name() }] },

            df.select { NonDataSchemaPerson::name.cols { "Name" in it.name() } },
            df.select { NonDataSchemaPerson::name[{ "Name" in it.name() }] },

            df.select { pathOf("name").cols { "Name" in it.name() } },
            df.select { pathOf("name")[{ "Name" in it.name() }] },

            df.select { it["name"].asColumnGroup().cols { "Name" in it.name() } },
            df.select { it["name"].asColumnGroup()[{ "Name" in it.name() }] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with column references`() {
        listOf(
            df.select { name and age },

            df.select { cols(name, age) },
            df.select { this[name, age] },
            df.select { it[name, age] },

//            df.select { all().cols(name, age) },
//            df.select { all()[name, age] },
        ).shouldAllBeEqual()

        val firstName by column<String>()
        val lastName by column<String>()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(firstName, lastName) },
            df.select { name[firstName, lastName] },

//            df.select { name.colsOf<String>().cols(firstName, lastName) },
//            df.select { name.colsOf<String>()[firstName, lastName] },

            df.select {
                name.select {
                    cols(this@select.firstName, this@select.lastName)
                }
            },

            df.select {
                it["name"].asColumnGroup().select {
                    cols("firstName", "lastName")
                }
            },

            df.select { "name".cols(firstName, lastName) },
            df.select { "name"[firstName, lastName] },

            df.select { Person::name.cols(firstName, lastName) },
            df.select { Person::name[firstName, lastName] },

            df.select { NonDataSchemaPerson::name.cols(firstName, lastName) },
            df.select { NonDataSchemaPerson::name[firstName, lastName] },

            df.select { pathOf("name").cols(firstName, lastName) },
            df.select { pathOf("name")[firstName, lastName] },

            df.select { it["name"].asColumnGroup().cols(firstName, lastName) },
            df.select { it["name"].asColumnGroup()[firstName, lastName] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with column names`() {
        listOf(
            df.select { name and age },

            df.select { cols("name", "age") },
            df.select { this["name", "age"] },
            df.select { it["name", "age"] },

//            df.select { all().cols("name", "age") },
//            df.select { all()["name", "age"] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols("firstName", "lastName") },
            df.select { name["firstName", "lastName"] },

//            df.select { name.colsOf<String>().cols("firstName", "lastName") },
//            df.select { name.colsOf<String>()["firstName", "lastName"] },

            df.select { "name".cols("firstName", "lastName") },
            df.select { "name"["firstName", "lastName"] },
            df.select { "name"["firstName"] and "name"["lastName"] },

            df.select { Person::name.cols("firstName", "lastName") },
            df.select { Person::name["firstName", "lastName"] },

            df.select { NonDataSchemaPerson::name.cols("firstName", "lastName") },
            df.select { NonDataSchemaPerson::name["firstName", "lastName"] },

            df.select { pathOf("name").cols("firstName", "lastName") },
            df.select { pathOf("name")["firstName", "lastName"] },

            df.select { it["name"].asColumnGroup().cols("firstName", "lastName") },
            df.select { it["name"].asColumnGroup()["firstName", "lastName"] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with column paths`() {
        listOf(
            df.select { name.firstName },

            df.select { cols(pathOf("name", "firstName")) },
            df.select { this[pathOf("name", "firstName")] },
            df.select { it[pathOf("name", "firstName")] },

//            df.select { all().cols(pathOf("name", "firstName")) },
//            df.select { all()[pathOf("name", "firstName")] },

            df.select { pathOf("name", "firstName") },
        ).shouldAllBeEqual()

        listOf(
            df.select { name and age },

            df.select { cols(pathOf("name"), pathOf("age")) },
            df.select { this[pathOf("name"), pathOf("age")] },
            df.select { it[pathOf("name"), pathOf("age")] },

//            df.select { all().cols(pathOf("name"), pathOf("age")) },
//            df.select { all()[pathOf("name"), pathOf("age")] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { name[pathOf("firstName"), pathOf("lastName")] },

//            df.select { name.colsOf<String>().cols(pathOf("firstName"), pathOf("lastName")) },
//            df.select { name.colsOf<String>()[pathOf("firstName"), pathOf("lastName")] },

            df.select { "name".cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { "name"[pathOf("firstName"), pathOf("lastName")] },

            df.select { Person::name.cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { Person::name[pathOf("firstName"), pathOf("lastName")] },

            df.select { NonDataSchemaPerson::name.cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { NonDataSchemaPerson::name[pathOf("firstName"), pathOf("lastName")] },

            df.select { pathOf("name").cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { pathOf("name")[pathOf("firstName"), pathOf("lastName")] },

            df.select { it["name"].asColumnGroup().cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { it["name"].asColumnGroup()[pathOf("firstName"), pathOf("lastName")] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with KProperties`() {
        listOf(
            df.select { name and age },

            df.select { cols(Person::name, Person::age) },
            df.select { this[Person::name, Person::age] },
            df.select { it[Person::name, Person::age] },

//            df.select { all().cols(Person::name, Person::age) },
//            df.select { all()[Person::name, Person::age] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },

            df.select { name.cols(Name::firstName, Name::lastName) },
            df.select { name[Name::firstName, Name::lastName] },

//            df.select { name.colsOf<String>().cols(Name::firstName, Name::lastName) },
//            df.select { name.colsOf<String>()[Name::firstName, Name::lastName] },

            df.select { "name".cols(Name::firstName, Name::lastName) },
            df.select { "name"[Name::firstName, Name::lastName] },

            df.select { Person::name.asColumnGroup().cols(Name::firstName, Name::lastName) },
            df.select { Person::name.asColumnGroup()[Name::firstName, Name::lastName] },

            df.select { NonDataSchemaPerson::name.asColumnGroup().cols(Name::firstName, Name::lastName) },
            df.select { NonDataSchemaPerson::name.asColumnGroup()[Name::firstName, Name::lastName] },

            df.select { pathOf("name").cols(Name::firstName, Name::lastName) },
            df.select { pathOf("name")[Name::firstName, Name::lastName] },

            df.select { it["name"].asColumnGroup().cols(Name::firstName, Name::lastName) },
            df.select { it["name"].asColumnGroup()[Name::firstName, Name::lastName] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with indices`() {
        listOf(
            df.select { name and age },
            df.select { cols(0, 1) },
            df.select { all().cols(0, 1) },
            df.select { all()[0, 1] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols(0, 1) },
            df.select { name.colsOf<String>().cols(0, 1) },
            df.select { name.colsOf<String>()[0, 1] },
            df.select { "name".cols(0, 1) },
            df.select { Person::name.cols(0, 1) },
            df.select { pathOf("name").cols(0, 1) },
            df.select { it["name"].asColumnGroup().cols(0, 1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with range`() {
        listOf(
            df.select { name and age },
            df.select { cols(0..1) },
            df.select { all().cols(0..1) },
            df.select { all()[0..1] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols(0..1) },
            df.select { name.colsOf<String>().cols(0..1) },
            df.select { name.colsOf<String>()[0..1] },
            df.select { "name".cols(0..1) },
            df.select { Person::name.cols(0..1) },
            df.select { NonDataSchemaPerson::name.cols(0..1) },
            df.select { pathOf("name").cols(0..1) },
            df.select { it["name"].asColumnGroup().cols(0..1) },
        ).shouldAllBeEqual()
    }
}
