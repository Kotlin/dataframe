package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.reflect.typeOf

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

    @Test
    fun `cast selector does not bypass the runtime type check`() {
        val df = dataFrameOf("age" to columnOf(1, 2, 3))
        val throwable = shouldThrow<IllegalArgumentException> {
            df.requireColumn { "age"<Int>().cast<String>() }
        }
        throwable.message shouldBe
            "Column 'age' has type 'kotlin.Int', which is not subtype of required 'kotlin.String' type."
    }

    @Test
    fun `cast selector passes when the runtime column type already fits the new type`() {
        val df = dataFrameOf("name" to columnOf<Any>("Joe"))
        val checked = df.requireColumn { "name"<Any>().cast<String>() }
        checked["name"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `require throws IllegalStateException when column cannot be resolved`() {
        shouldThrow<IllegalStateException> { df.requireColumn { "name2"["unknown"]<String>() } }
    }

    @Test
    fun `require rejects nullable column for non-null required type`() {
        val df = dataFrameOf("age" to columnOf(1, null))
        val throwable = shouldThrow<IllegalArgumentException> {
            df.requireColumn { "age"<Int>() }
        }
        throwable.message shouldBe
            "Column 'age' has type 'kotlin.Int?', which is not subtype of required 'kotlin.Int' type."
        shouldNotThrowAny { df.requireColumn { "age"<Int?>() } }
    }
}
