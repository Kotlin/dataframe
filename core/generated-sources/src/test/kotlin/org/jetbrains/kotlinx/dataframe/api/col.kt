package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class ColTests : ColumnsSelectionDslTests() {

    @Test
    fun `col on renamed column`() {
        df.select {
            colGroup("name").named("name1").col("firstName")
        }.alsoDebug()
    }

    @Test
    fun `col exceptions`() {
        shouldThrow<IllegalStateException> {
            df.select { col("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            df.select { name.col("nonExisting") }
        }
        shouldThrow<IllegalStateException> {
            df.select { "age".col("test") }
        }
        shouldThrow<IndexOutOfBoundsException> {
            df.select { col(100) }
        }
    }

    @Test
    fun `col at top-level`() {
        val ageAccessor = column<Int>("age")
        listOf(
            df.select { age },

            @Suppress("DEPRECATION")
            df.select { col(ageAccessor) },

            df.select { col("age") },
            df.select { col<Int>("age") },

            df.select { col(pathOf("age")) },
            df.select { col<Int>(pathOf("age")) },

            df.select { col(Person::age) },

            df.select { all().col(1) },
            df.select { all()[1] },
            df.select { col(1) },
            df.select { col<Int>(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `col at lower level`() {
        val firstNameAccessor = column<String>("firstName")
        listOf(
            df.select { name.firstName },

            // reference
            df.select { name.col(firstNameAccessor) },
            df.select { colGroup("name").col(firstNameAccessor) },
            df.select { "name".col(firstNameAccessor) },
            df.select { NonDataSchemaPerson::name.col(firstNameAccessor) },
            df.select { Person::name.col(firstNameAccessor) },
            df.select { pathOf("name").col(firstNameAccessor) },

            // name
            df.select { name.col("firstName") },
            df.select { name.col<String>("firstName") },
            df.select { colGroup("name").col("firstName") },
            df.select { colGroup("name").col<String>("firstName") },
            df.select { "name".col("firstName") },
            df.select { "name".col<String>("firstName") },
            df.select { NonDataSchemaPerson::name.col("firstName") },
            df.select { NonDataSchemaPerson::name.col<String>("firstName") },
            df.select { Person::name.col("firstName") },
            df.select { Person::name.col<String>("firstName") },
            df.select { pathOf("name").col("firstName") },
            df.select { pathOf("name").col<String>("firstName") },

            // path
            df.select { name.col(pathOf("firstName")) },
            df.select { name.col<String>(pathOf("firstName")) },
            df.select { colGroup("name").col(pathOf("firstName")) },
            df.select { colGroup("name").col<String>(pathOf("firstName")) },
            df.select { "name".col(pathOf("firstName")) },
            df.select { "name".col<String>(pathOf("firstName")) },
            df.select { NonDataSchemaPerson::name.col(pathOf("firstName")) },
            df.select { NonDataSchemaPerson::name.col<String>(pathOf("firstName")) },
            df.select { Person::name.col(pathOf("firstName")) },
            df.select { Person::name.col<String>(pathOf("firstName")) },
            df.select { pathOf("name").col(pathOf("firstName")) },
            df.select { pathOf("name").col<String>(pathOf("firstName")) },

            df.select { col("name"["firstName"]) },
            df.select { col<String>("name"["firstName"]) },
            df.select { asSingleColumn().col("name"["firstName"]) },
            df.select { asSingleColumn().col<String>("name"["firstName"]) },

            // property
            df.select { name.col(Name::firstName) },
            df.select { colGroup("name").col(Name::firstName) },
            df.select { "name".col(Name::firstName) },
            df.select { NonDataSchemaPerson::name.col(Name::firstName) },
            df.select { Person::name.col(Name::firstName) },
            df.select { pathOf("name").col(Name::firstName) },

            // index
            df.select { name.col(0) },
            df.select { name.col<String>(0) },
            df.select { colGroup("name").col(0) },
            df.select { colGroup("name").col<String>(0) },
            df.select { "name".col(0) },
            df.select { "name".col<String>(0) },
            df.select { NonDataSchemaPerson::name.col(0) },
            df.select { NonDataSchemaPerson::name.col<String>(0) },
            df.select { Person::name.col(0) },
            df.select { Person::name.col<String>(0) },
            df.select { pathOf("name").col(0) },
            df.select { pathOf("name").col<String>(0) },
        ).shouldAllBeEqual()
    }
}
