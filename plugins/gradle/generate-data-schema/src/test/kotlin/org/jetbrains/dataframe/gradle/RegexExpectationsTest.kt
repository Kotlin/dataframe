package org.jetbrains.dataframe.gradle

import io.kotest.matchers.shouldBe
import org.junit.Test

class RegexExpectationsTest {
    // for package name validation
    @Test
    fun `regex split returns non empty list for empty string`() {
        "".split("123".toRegex()) shouldBe listOf("")
    }

    @Test
    fun `regex split ignore delimiter`() {
        "1.2.3".split("\\.".toRegex()) shouldBe listOf("1", "2", "3")
    }
}
