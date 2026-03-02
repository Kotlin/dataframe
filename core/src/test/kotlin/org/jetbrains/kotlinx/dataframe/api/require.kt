package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import org.junit.Test

class RequireTests : ColumnsSelectionDslTests() {

    @Test
    fun `require returns same dataframe for existing typed column`() {
        val checked = df.requireColumn { "name"["firstName"]<String>() }
        checked shouldBe df
    }

    @Test
    fun `require throws on type mismatch`() {
        val throwable = shouldThrow<IllegalArgumentException> {
            df.requireColumn { "name"["firstName"]<Int>() }
        }
        throwable.message shouldBe
            "Column 'name/firstName' has type 'kotlin.String', which is not subtype of required 'kotlin.Int' type."
    }

    @Test
    fun `require throws when column cannot be resolved`() {
        val exception = shouldThrowAny {
            df.requireColumn { "name"["unknown"]<String>() }
        }
        exception.message shouldBe
            "Column 'name/unknown' not found among columns of 'name': [firstName, lastName]."
    }

    @Test
    fun `require missing parent message includes available columns`() {
        val exception = shouldThrowAny {
            df.requireColumn { "name2"["unknown"]<String>() }
        }
        exception.message shouldBe
            "Column 'name2' not found among [name, age, city, weight, isHappy]."
    }

    @Test
    fun `require deep missing parent message uses nearest existing ancestor`() {
        val exception = shouldThrowAny {
            df.requireColumn { "name"["unknownGroup"]["value"]<String>() }
        }
        exception.message shouldBe
            "Column 'name/unknownGroup' not found among columns of 'name': [firstName, lastName]."
    }
}
