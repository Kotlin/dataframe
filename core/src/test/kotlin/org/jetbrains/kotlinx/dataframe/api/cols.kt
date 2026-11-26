package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
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
    fun `cols exceptions`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".allCols() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { cols("non-existent") }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { name.cols("non-existent") }
        }

        // calls are always relative to the receiver
        shouldThrow<IllegalArgumentException> {
            df.select { name.cols(name.firstName) }
        }

        // "breaking spec" and using a column accessor where it doesn't belong does not work.
        shouldThrow<IllegalArgumentException> {
            df.select select1@{
                name.select {
                    cols(this@select1.name.firstName)
                }
            }
        }
    }

    @Test
    fun `cols and get with predicate`() {
        listOf(
            df.select { cols(name, age, city, weight, isHappy) },
            df.select { all().all() },
            df.select { all() },
            df.select { all() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name },
            df.select { name }.select { all() },
            df.select { name }.select { all() },
            df.select { name }.select { all().all() },
            df.select { name }.select { all().all() },
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
        ).shouldAllBeEqual()

        val firstName by column<String>()
        val lastName by column<String>()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols(firstName, lastName) },
            df.select { name[firstName, lastName] },
            df.select {
                name.select {
                    cols(firstName, lastName)
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
            df.select { cols<String>("name", "age") },
            df.select { this["name", "age"] },
            df.select { it["name", "age"] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols("firstName", "lastName") },
            df.select { name.cols<String>("firstName", "lastName") },
            df.select { name["firstName", "lastName"] },
            df.select { "name".cols("firstName", "lastName") },
            df.select { "name".cols<String>("firstName", "lastName") },
            df.select { "name"["firstName", "lastName"] },
            df.select { "name"["firstName"] and "name"["lastName"] },
            df.select { Person::name.cols("firstName", "lastName") },
            df.select { Person::name.cols<String>("firstName", "lastName") },
            df.select { Person::name["firstName", "lastName"] },
            df.select { NonDataSchemaPerson::name.cols("firstName", "lastName") },
            df.select { NonDataSchemaPerson::name.cols<String>("firstName", "lastName") },
            df.select { NonDataSchemaPerson::name["firstName", "lastName"] },
            df.select { pathOf("name").cols("firstName", "lastName") },
            df.select { pathOf("name").cols<String>("firstName", "lastName") },
            df.select { pathOf("name")["firstName", "lastName"] },
            df.select { it["name"].asColumnGroup().cols("firstName", "lastName") },
            df.select { it["name"].asColumnGroup().cols<String>("firstName", "lastName") },
            df.select { it["name"].asColumnGroup()["firstName", "lastName"] },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with column paths`() {
        listOf(
            df.select { name.firstName },
            df.select { cols(pathOf("name", "firstName")) },
            df.select { cols<String>(pathOf("name", "firstName")) },
            df.select { this[pathOf("name", "firstName")] },
            df.select { it[pathOf("name", "firstName")] },
            df.select { pathOf("name", "firstName") },
        ).shouldAllBeEqual()

        listOf(
            df.select { name and age },
            df.select { cols(pathOf("name"), pathOf("age")) },
            df.select { cols<Any>(pathOf("name"), pathOf("age")) },
            df.select { this[pathOf("name"), pathOf("age")] },
            df.select { it[pathOf("name"), pathOf("age")] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { name.cols<String>(pathOf("firstName"), pathOf("lastName")) },
            df.select { name[pathOf("firstName"), pathOf("lastName")] },
            df.select { "name".cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { "name".cols<String>(pathOf("firstName"), pathOf("lastName")) },
            df.select { "name"[pathOf("firstName"), pathOf("lastName")] },
            df.select { Person::name.cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { Person::name.cols<String>(pathOf("firstName"), pathOf("lastName")) },
            df.select { Person::name[pathOf("firstName"), pathOf("lastName")] },
            df.select { NonDataSchemaPerson::name.cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { NonDataSchemaPerson::name.cols<String>(pathOf("firstName"), pathOf("lastName")) },
            df.select { NonDataSchemaPerson::name[pathOf("firstName"), pathOf("lastName")] },
            df.select { pathOf("name").cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { pathOf("name").cols<String>(pathOf("firstName"), pathOf("lastName")) },
            df.select { pathOf("name")[pathOf("firstName"), pathOf("lastName")] },
            df.select { it["name"].asColumnGroup().cols(pathOf("firstName"), pathOf("lastName")) },
            df.select { it["name"].asColumnGroup().cols<String>(pathOf("firstName"), pathOf("lastName")) },
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
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols(Name::firstName, Name::lastName) },
            df.select { name[Name::firstName, Name::lastName] },
            df.select { "name".cols(Name::firstName, Name::lastName) },
            df.select { "name"[Name::firstName, Name::lastName] },
            df.select { columnGroup(Person::name).cols(Name::firstName, Name::lastName) },
            df.select { columnGroup(Person::name)[Name::firstName, Name::lastName] },
            df.select { columnGroup(NonDataSchemaPerson::name).cols(Name::firstName, Name::lastName) },
            df.select { columnGroup(NonDataSchemaPerson::name)[Name::firstName, Name::lastName] },
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
            df.select { cols<Any>(0, 1) },
            df.select { all().cols(0, 1) },
            df.select { all()[0, 1] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols(0, 1) },
            df.select { name.cols<String>(0, 1) },
            df.select { name.colsOf<String>().cols(0, 1) },
            df.select { name.colsOf<String>().cols(0, 1) },
            df.select { name.colsOf<String>()[0, 1] },
            df.select { "name".cols(0, 1) },
            df.select { "name".cols<String>(0, 1) },
            df.select { Person::name.cols(0, 1) },
            df.select { Person::name.cols<String>(0, 1) },
            df.select { pathOf("name").cols(0, 1) },
            df.select { pathOf("name").cols<String>(0, 1) },
            df.select { it["name"].asColumnGroup().cols(0, 1) },
            df.select { it["name"].asColumnGroup().cols<String>(0, 1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `cols and get with range`() {
        listOf(
            df.select { name and age },
            df.select { cols(0..1) },
            df.select { cols<Any>(0..1) },
            df.select { all().cols(0..1) },
            df.select { all()[0..1] },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName and name.lastName },
            df.select { name.cols(0..1) },
            df.select { name.cols<String>(0..1) },
            df.select { name.colsOf<String>().cols(0..1) },
            df.select { name.colsOf<String>()[0..1] },
            df.select { "name".cols(0..1) },
            df.select { "name".cols<String>(0..1) },
            df.select { Person::name.cols(0..1) },
            df.select { Person::name.cols<String>(0..1) },
            df.select { NonDataSchemaPerson::name.cols(0..1) },
            df.select { NonDataSchemaPerson::name.cols<String>(0..1) },
            df.select { pathOf("name").cols(0..1) },
            df.select { pathOf("name").cols<String>(0..1) },
            df.select { it["name"].asColumnGroup().cols(0..1) },
            df.select { it["name"].asColumnGroup().cols<String>(0..1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `triple nested accessor edge case`() {
        listOf(
            dfGroup.select { name.firstName.firstName and name.lastName },
            // column paths are relative to the receiver
            dfGroup.select { name.cols("firstName"["firstName"], pathOf("lastName")) },
            // column accessors can only be relative
            dfGroup.select { name.cols(colGroup("firstName").col("firstName"), col("lastName")) },
            // so not absolute
//            dfGroup.select { name.cols(name.firstName.firstName, name.lastName) },
        ).shouldAllBeEqual()
    }
}
