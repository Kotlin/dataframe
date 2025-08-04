package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class FilterTests : ColumnsSelectionDslTests() {

    @Test
    fun `filter`() {
        listOf(
            df.select { cols(name, age, weight) },
            df.select { all().filter { "e" in it.name() } },
            df.select { all().cols { "e" in it.name() } },
        ).shouldAllBeEqual()

        df.select { all().filter { true } } shouldBe df.select { all() }
        df.select { all().filter { false } } shouldBe df.select { none() }
    }

    @Test
    fun `filter column group`() {
        listOf(
            df.select { name },
            df.select { colsAtAnyDepth().colGroups().filter { it.data.containsColumn("firstName") } }
        )
    }
}
