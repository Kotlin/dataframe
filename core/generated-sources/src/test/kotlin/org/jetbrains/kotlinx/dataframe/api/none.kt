package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

class NoneTests : ColumnsSelectionDslTests() {

    @Test
    fun none() {
        df.select { none() }.let {
            it.nrow shouldBe 0
            it.ncol shouldBe 0
        }
    }
}
