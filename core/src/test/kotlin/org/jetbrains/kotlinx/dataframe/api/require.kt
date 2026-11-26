package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.exceptions.ColumnNotFoundException
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
        val exception = shouldThrow<ColumnNotFoundException> {
            df.requireColumn { "name"["unknown"]<String>() }
        }
        exception.columnName shouldBe "name/unknown"
        exception.message shouldBe
            "Column 'name/unknown' not found among columns of 'name': [firstName, lastName]."
    }

    @Test
    fun `require missing parent message includes available columns`() {
        val exception = shouldThrow<ColumnNotFoundException> {
            df.requireColumn { "name2"["unknown"]<String>() }
        }
        exception.columnName shouldBe "name2/unknown"
        exception.message shouldBe
            "Column 'name2' not found among [name, age, city, weight, isHappy]."
    }

    @Test
    fun `require deep missing parent message uses nearest existing ancestor`() {
        val exception = shouldThrow<ColumnNotFoundException> {
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
    fun `require throws ColumnNotFoundException for a missing top-level column`() {
        val exception = shouldThrow<ColumnNotFoundException> { df.requireColumn { "unknown"<String>() } }
        (exception is IllegalStateException) shouldBe true
        exception.columnName shouldBe "unknown"
        exception.message shouldBe "Column 'unknown' not found among [name, age, city, weight, isHappy]."
    }

    @Test
    fun `require rethrows IllegalStateException from the selector code as is`() {
        val exception = shouldThrow<IllegalStateException> {
            df.requireColumn<_, String> { error("boom") }
        }
        exception.message shouldBe "boom"
    }

    @Test
    fun `require rejects nullable column for non-null required type`() {
        val df = dataFrameOf("age" to columnOf(1, null))
        val throwable = shouldThrow<IllegalArgumentException> {
            df.requireColumn { "age"<Int>() }
        }
        throwable.message shouldBe
            "Column 'age' has type 'kotlin.Int?', which is not subtype of required 'kotlin.Int' type."
    }

    @Test
    fun `require accepts nullable column for nullable required type`() {
        val df = dataFrameOf("age" to columnOf(1, null))
        shouldNotThrowAny { df.requireColumn { "age"<Int?>() } }
    }
}
