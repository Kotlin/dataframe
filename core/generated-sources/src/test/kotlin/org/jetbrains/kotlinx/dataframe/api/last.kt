package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class LastTests : ColumnsSelectionDslTests() {
    @Test
    fun `ColumnsSelectionDsl last`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".lastCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { columnGroup(Person::age).lastCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.lastCol() }
        }
        shouldThrow<NoSuchElementException> {
            df.select { last { false } }
        }

        listOf(
            df.select { isHappy },
            df.select { last() },
            df.select { all().last() },
            df.select { last { it.name().startsWith("is") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.colsOf<String>().last { col -> col.any { it == "Alice" } } },
            df.select { name.lastCol { col -> col.any { it == "Alice" } } },
            df.select { "name".lastCol { col -> col.any { it == "Alice" } } },
            df.select { Person::name.lastCol { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.lastCol { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").lastCol { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().lastCol { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }
}
