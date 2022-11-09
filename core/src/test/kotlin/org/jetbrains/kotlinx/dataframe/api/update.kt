package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test

class UpdateTests {

    @Test
    fun `update empty dataframe with missing column`() {
        val df = DataFrame.Empty
        val col by column<Int>()
        df.update { col }.with { 2 } shouldBe df
    }
}
