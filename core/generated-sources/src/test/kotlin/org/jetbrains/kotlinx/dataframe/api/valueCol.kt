package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class ValueColTests : ColumnsSelectionDslTests() {

    @Test
    fun `valueCol exceptions`() {
        shouldThrow<IllegalArgumentException> {
            df.select { valueCol("name") }
        }
        shouldThrow<IllegalStateException> {
            df.select { valueCol("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            df.select { name.valueCol("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            df.select { "age".valueCol("test") }
        }
        shouldThrow<IndexOutOfBoundsException> {
            df.select { valueCol(100) }
        }
    }

    @Test
    fun `valueCol at top-level`() {
        val ageAccessor = valueColumn<Int>("age")
        listOf(
            df.select { age },
            df.select { valueCol(ageAccessor) },
            df.select { valueCol("age") },
            df.select { valueCol<Int>("age") },
            df.select { valueCol(pathOf("age")) },
            df.select { valueCol<Int>(pathOf("age")) },
            df.select { valueCol(Person::age) },
            df.select { all().valueCol(1) },
            df.select { valueCol(1) },
            df.select { valueCol<Int>(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `valueCol at lower level`() {
        val firstNameAccessor = valueColumn<String>("firstName")
        listOf(
            df.select { name.firstName },
            // reference
            df.select { name.valueCol(firstNameAccessor) },
            df.select { colGroup("name").valueCol(firstNameAccessor) },
            df.select { "name".valueCol(firstNameAccessor) },
            df.select { NonDataSchemaPerson::name.valueCol(firstNameAccessor) },
            df.select { Person::name.valueCol(firstNameAccessor) },
            df.select { pathOf("name").valueCol(firstNameAccessor) },
            // name
            df.select { name.valueCol("firstName") },
            df.select { name.valueCol<String>("firstName") },
            df.select { colGroup("name").valueCol("firstName") },
            df.select { colGroup("name").valueCol<String>("firstName") },
            df.select { "name".valueCol("firstName") },
            df.select { "name".valueCol<String>("firstName") },
            df.select { NonDataSchemaPerson::name.valueCol("firstName") },
            df.select { NonDataSchemaPerson::name.valueCol<String>("firstName") },
            df.select { Person::name.valueCol("firstName") },
            df.select { Person::name.valueCol<String>("firstName") },
            df.select { pathOf("name").valueCol("firstName") },
            df.select { pathOf("name").valueCol<String>("firstName") },
            // path
            df.select { name.valueCol(pathOf("firstName")) },
            df.select { name.valueCol<String>(pathOf("firstName")) },
            df.select { colGroup("name").valueCol(pathOf("firstName")) },
            df.select { colGroup("name").valueCol<String>(pathOf("firstName")) },
            df.select { "name".valueCol(pathOf("firstName")) },
            df.select { "name".valueCol<String>(pathOf("firstName")) },
            df.select { NonDataSchemaPerson::name.valueCol(pathOf("firstName")) },
            df.select { NonDataSchemaPerson::name.valueCol<String>(pathOf("firstName")) },
            df.select { Person::name.valueCol(pathOf("firstName")) },
            df.select { Person::name.valueCol<String>(pathOf("firstName")) },
            df.select { pathOf("name").valueCol(pathOf("firstName")) },
            df.select { pathOf("name").valueCol<String>(pathOf("firstName")) },
            df.select { valueCol("name"["firstName"]) },
            df.select { valueCol<String>("name"["firstName"]) },
            df.select { asSingleColumn().valueCol("name"["firstName"]) },
            df.select { asSingleColumn().valueCol<String>("name"["firstName"]) },
            // property
            df.select { name.valueCol(Name::firstName) },
            df.select { colGroup("name").valueCol(Name::firstName) },
            df.select { "name".valueCol(Name::firstName) },
            df.select { NonDataSchemaPerson::name.valueCol(Name::firstName) },
            df.select { Person::name.valueCol(Name::firstName) },
            df.select { pathOf("name").valueCol(Name::firstName) },
            // index
            df.select { name.valueCol(0) },
            df.select { name.valueCol<String>(0) },
            df.select { colGroup("name").valueCol(0) },
            df.select { colGroup("name").valueCol<String>(0) },
            df.select { "name".valueCol(0) },
            df.select { "name".valueCol<String>(0) },
            df.select { NonDataSchemaPerson::name.valueCol(0) },
            df.select { NonDataSchemaPerson::name.valueCol<String>(0) },
            df.select { Person::name.valueCol(0) },
            df.select { Person::name.valueCol<String>(0) },
            df.select { pathOf("name").valueCol(0) },
            df.select { pathOf("name").valueCol<String>(0) },
        ).shouldAllBeEqual()
    }
}
