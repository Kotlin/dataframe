package org.jetbrains.kotlinx.dataframe.types

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.impl.commonParent
import org.jetbrains.kotlinx.dataframe.impl.commonParents
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.junit.Test
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class UtilTests {

    @Test
    fun commonParentsTests() {
        commonParents(Int::class, Int::class) shouldBe listOf(Int::class)
        commonParents(Double::class, Int::class) shouldBe listOf(Comparable::class, Number::class)
        commonParents(Int::class, String::class) shouldBe listOf(Comparable::class, Serializable::class)
        commonParents(IllegalArgumentException::class, UnsupportedOperationException::class) shouldBe listOf(
            RuntimeException::class
        )
    }

    @Test
    fun commonParentTests() {
        commonParent(Int::class, Int::class) shouldBe Int::class
        commonParent(Double::class, Int::class) shouldBe Number::class
        commonParent(Int::class, String::class) shouldBe Comparable::class
        commonParent(String::class, Int::class) shouldBe Comparable::class
    }

    @Test
    fun `commonType for empty`() {
        emptyList<KClass<*>>().commonType(false, typeOf<List<Int>>()) shouldBe typeOf<List<Int>>()
        emptyList<KClass<*>>().commonType(true, typeOf<List<Int>>()) shouldBe typeOf<List<Int>?>()
    }

    @Test
    fun listification() {
        val seq = sequenceOf(
            1,
            null,
            listOf(1.1, 2.1),
        )
        guessValueType(seq) shouldBe typeOf<Any?>()
        guessValueType(seq, listifyValues = true) shouldBe typeOf<List<Number>>()

        val column = columnOf(1.5, null, listOf(1.3, 2.6))
        column.type() shouldBe typeOf<Any?>()
    }
}
