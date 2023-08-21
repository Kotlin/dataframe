package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class SingleTests : ColumnsSelectionDslTests() {

    @Test
    fun `ColumnsSelectionDsl single`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".singleCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { columnGroup(Person::age).singleCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.singleCol() }
        }
        shouldThrow<NoSuchElementException> {
            df.select { single { false } }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { single { true } }
        }

        val singleDf = df.select { take(1) }

        listOf(
            df.select { name },
            singleDf.select { name },
            singleDf.select { single() },
            singleDf.select { all().single() },
            df.select { single { it.name().startsWith("n") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.colsOf<String>().single { col -> col.any { it == "Alice" } } },
            df.select { name.singleCol { col -> col.any { it == "Alice" } } },
            df.select { "name".singleCol { col -> col.any { it == "Alice" } } },
            df.select { Person::name.singleCol { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.singleCol { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").singleCol { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().singleCol { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }
}
