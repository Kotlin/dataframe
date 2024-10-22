package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class ColGroupTests : ColumnsSelectionDslTests() {

    @Test
    fun `colGroup exceptions`() {
        shouldThrow<IllegalArgumentException> {
            dfGroup.select { colGroup("age") }
        }
        shouldThrow<IllegalStateException> {
            dfGroup.select { colGroup("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            dfGroup.select { name.colGroup("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            dfGroup.select { "age".colGroup("test") }
        }
        shouldThrow<IndexOutOfBoundsException> {
            dfGroup.select { colGroup(100) }
        }
    }

    @Test
    fun `colGroup at top-level`() {
        val nameAccessor = columnGroup<Name2>("name")
        listOf(
            dfGroup.select { name },
            dfGroup.select { colGroup(nameAccessor) },
            dfGroup.select { colGroup("name") },
            dfGroup.select { colGroup<Name2>("name") },
            dfGroup.select { colGroup(pathOf("name")) },
            dfGroup.select { colGroup<Name2>(pathOf("name")) },
            dfGroup.select { colGroup(Person2::name) },
            dfGroup.select { colGroups().colGroup(0) },
            dfGroup.select { all().colGroup(0) },
            dfGroup.select { colGroup(0) },
            dfGroup.select { colGroup<Name2>(0) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `frameCol at lower level`() {
        val firstNamesAccessor = columnGroup<FirstNames>("firstName")
        listOf(
            dfGroup.select { name.firstName },
            // reference
            dfGroup.select { name.colGroup(firstNamesAccessor) },
            dfGroup.select { colGroup("name").colGroup(firstNamesAccessor) },
            dfGroup.select { "name".colGroup(firstNamesAccessor) },
            dfGroup.select { NonDataSchemaPerson::name.colGroup(firstNamesAccessor) },
            dfGroup.select { Person2::name.colGroup(firstNamesAccessor) },
            dfGroup.select { pathOf("name").colGroup(firstNamesAccessor) },
            // name
            dfGroup.select { name.colGroup("firstName") },
            dfGroup.select { name.colGroup<FirstNames>("firstName") },
            dfGroup.select { colGroup("name").colGroup("firstName") },
            dfGroup.select { colGroup("name").colGroup<FirstNames>("firstName") },
            dfGroup.select { "name".colGroup("firstName") },
            dfGroup.select { "name".colGroup<FirstNames>("firstName") },
            dfGroup.select { NonDataSchemaPerson::name.colGroup("firstName") },
            dfGroup.select { NonDataSchemaPerson::name.colGroup<FirstNames>("firstName") },
            dfGroup.select { Person2::name.colGroup("firstName") },
            dfGroup.select { Person2::name.colGroup<FirstNames>("firstName") },
            dfGroup.select { pathOf("name").colGroup("firstName") },
            dfGroup.select { pathOf("name").colGroup<FirstNames>("firstName") },
            // path
            dfGroup.select { name.colGroup(pathOf("firstName")) },
            dfGroup.select { name.colGroup<FirstNames>(pathOf("firstName")) },
            dfGroup.select { colGroup("name").colGroup(pathOf("firstName")) },
            dfGroup.select { colGroup("name").colGroup<FirstNames>(pathOf("firstName")) },
            dfGroup.select { "name".colGroup(pathOf("firstName")) },
            dfGroup.select { "name".colGroup<FirstNames>(pathOf("firstName")) },
            dfGroup.select { NonDataSchemaPerson::name.colGroup(pathOf("firstName")) },
            dfGroup.select { NonDataSchemaPerson::name.colGroup<FirstNames>(pathOf("firstName")) },
            dfGroup.select { Person::name.colGroup(pathOf("firstName")) },
            dfGroup.select { Person::name.colGroup<FirstNames>(pathOf("firstName")) },
            dfGroup.select { pathOf("name").colGroup(pathOf("firstName")) },
            dfGroup.select { pathOf("name").colGroup<FirstNames>(pathOf("firstName")) },
            dfGroup.select { colGroup("name"["firstName"]) },
            dfGroup.select { colGroup<FirstNames>("name"["firstName"]) },
            dfGroup.select { asSingleColumn().colGroup("name"["firstName"]) },
            dfGroup.select { asSingleColumn().colGroup<FirstNames>("name"["firstName"]) },
            // property
            dfGroup.select { name.colGroup(Name2::firstName) },
            dfGroup.select { colGroup("name").colGroup(Name2::firstName) },
            dfGroup.select { "name".colGroup(Name2::firstName) },
            dfGroup.select { NonDataSchemaPerson::name.colGroup(Name2::firstName) },
            dfGroup.select { Person::name.colGroup(Name2::firstName) },
            dfGroup.select { pathOf("name").colGroup(Name2::firstName) },
            // index
            dfGroup.select { name.colGroup(0) },
            dfGroup.select { name.colGroup<FirstNames>(0) },
            dfGroup.select { colGroup("name").colGroup(0) },
            dfGroup.select { colGroup("name").colGroup<FirstNames>(0) },
            dfGroup.select { "name".colGroup(0) },
            dfGroup.select { "name".colGroup<FirstNames>(0) },
            dfGroup.select { NonDataSchemaPerson::name.colGroup(0) },
            dfGroup.select { NonDataSchemaPerson::name.colGroup<FirstNames>(0) },
            dfGroup.select { Person2::name.colGroup(0) },
            dfGroup.select { Person2::name.colGroup<FirstNames>(0) },
            dfGroup.select { pathOf("name").colGroup(0) },
            dfGroup.select { pathOf("name").colGroup<FirstNames>(0) },
        ).shouldAllBeEqual()
    }
}
