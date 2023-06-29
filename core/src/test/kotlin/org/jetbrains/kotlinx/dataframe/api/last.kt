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
            df.select { "age".lastChild() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.asColumnGroup().lastChild() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.lastChild() }
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
            df.select { name.lastChild { col -> col.any { it == "Alice" } } },
            df.select { "name".lastChild { col -> col.any { it == "Alice" } } },
            df.select { Person::name.lastChild { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.lastChild { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").lastChild { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().lastChild { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }
}
