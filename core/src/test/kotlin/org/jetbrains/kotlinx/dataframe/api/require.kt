package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.Test

class RequireTests : ColumnsSelectionDslTests() {

    @Test
    fun `require returns same dataframe for existing typed column`() {
        val checked = df.require { "name"["firstName"]<String>() }
        checked shouldBe df
    }

    @Test
    fun `require throws on type mismatch`() {
        val throwable = shouldThrow<IllegalArgumentException> {
            df.require { "name"["firstName"]<Int>() }
        }
        throwable.message shouldBe
            "Column 'name/firstName' has type 'kotlin.String', which is not subtype of required 'kotlin.Int' type."
    }

    @Test
    fun `require throws when column cannot be resolved`() {
        val exception = shouldThrowAny {
            df.require { "name"["unknown"]<String>() }
        }
        println(exception)
    }
}
