package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class GenerateCodeTests {
    @Test
    fun `generateDataClasses with soft keyword`() {
        val df = dataFrameOf("value" to columnOf(123))
        val dataClass = df.generateDataClasses().value
        val expected =
            """
            @DataSchema
            data class DataEntry(
                val value: Int
            )
            """.trimIndent()

        dataClass shouldBe expected
    }
}
