package org.jetbrains.dataframe.rendering

import io.kotest.matchers.shouldBe
import org.junit.Test

class TooltipTests : RenderingTestsBase() {

    @Test
    fun `long str`() {
        "12345678".tooltip(5) shouldBe "12345678"
    }

    @Test
    fun `short str`() {
        "1234".tooltip(5) shouldBe null
    }

    @Test
    fun row() {
        val data = rowOf("name" to "Alice", "age" to 10)
        val tooltip = "name: Alice\nage: 10"
        data.tooltip(5) shouldBe tooltip
        data.tooltip(15) shouldBe tooltip
    }
}
