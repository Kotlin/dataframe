package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class FirstTests : ColumnsSelectionDslTests() {

    @Test
    fun `ColumnsSelectionDsl first`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".firstChild() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.asColumnGroup().firstChild() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.firstChild() }
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
            df.select { name.firstChild { col -> col.any { it == "Alice" } } },
            df.select { "name".firstChild { col -> col.any { it == "Alice" } } },
            df.select { Person::name.firstChild { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.firstChild { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").firstChild { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().firstChild { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }
}
