package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class FirstTests : ColumnsSelectionDslTests() {

    @Test
    fun `ColumnsSelectionDsl first`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".firstCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { columnGroup(Person::age).firstCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.firstCol() }
        }
        shouldThrow<NoSuchElementException> {
            df.select { first { false } }
        }

        listOf(
            df.select { name },
            df.select { first() },
            df.select { all().first() },
            df.select { first { it.name().startsWith("n") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.colsOf<String>().first { col -> col.any { it == "Alice" } } },
            df.select { name.firstCol { col -> col.any { it == "Alice" } } },
            df.select { "name".firstCol { col -> col.any { it == "Alice" } } },
            df.select { Person::name.firstCol { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.firstCol { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").firstCol { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().firstCol { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }
}
