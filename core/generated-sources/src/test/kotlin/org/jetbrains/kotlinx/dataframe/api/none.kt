package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class NoneTests : ColumnsSelectionDslTests() {
    @Test
    fun none() {
        df.select { none() }.let {
            it.nrow shouldBe 0
            it.ncol shouldBe 0
        }

        listOf(
            df.select { none() and age },
            df.select { age },
            df.select { name.select { none() } and age },
        ).shouldAllBeEqual()
    }
}
