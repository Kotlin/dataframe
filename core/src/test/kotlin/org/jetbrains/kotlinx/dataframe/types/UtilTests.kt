package org.jetbrains.kotlinx.dataframe.types

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.commonParent
import org.jetbrains.kotlinx.dataframe.impl.commonParents
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.createType
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
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
        commonParents(IllegalArgumentException::class, UnsupportedOperationException::class) shouldBe
            listOf(RuntimeException::class)
        commonParents(Nothing::class, Nothing::class) shouldBe listOf(Nothing::class)
        commonParents() shouldBe emptyList()
    }

    @Test
    fun commonParentTests() {
        commonParent(Int::class, Int::class) shouldBe Int::class
        commonParent(Double::class, Int::class) shouldBe Number::class
        commonParent(Int::class, String::class) shouldBe Comparable::class
        commonParent(String::class, Int::class) shouldBe Comparable::class
        commonParent(Nothing::class, Nothing::class) shouldBe Nothing::class
        commonParent(Int::class, Nothing::class) shouldBe Int::class
        commonParent() shouldBe null
        commonParent(List::class, Map::class) shouldBe Any::class
    }

    @Test
    fun `createType test`() {
        emptyList<KClass<*>>().createType(nullable = false) shouldBe typeOf<Any>()
        emptyList<KClass<*>>().createType(nullable = true) shouldBe typeOf<Any?>()

        listOf(Nothing::class).createType(nullable = false) shouldBe nothingType(nullable = false)
        listOf(Nothing::class).createType(nullable = true) shouldBe nothingType(nullable = true)
    }

    @Test
    fun `commonType test`() {
        emptyList<KClass<*>>().commonType(false, typeOf<List<Int>>()) shouldBe typeOf<List<Int>>()
        emptyList<KClass<*>>().commonType(true, typeOf<List<Int>>()) shouldBe typeOf<List<Int>?>()

        listOf(Nothing::class).commonType(false) shouldBe nothingType(nullable = false)
        listOf(Nothing::class).commonType(true) shouldBe nothingType(nullable = true)
    }

    @Test
    fun `guessValueType no listification`() {
        guessValueType(sequenceOf(1, 2)) shouldBe typeOf<Int>()
        guessValueType(sequenceOf(1, 2, null)) shouldBe typeOf<Int?>()

        guessValueType(sequenceOf(1, 2.0)) shouldBe typeOf<Number>()
        guessValueType(sequenceOf(1, 2.0, null)) shouldBe typeOf<Number?>()

        guessValueType(sequenceOf(1, 2.0, "a")) shouldBe typeOf<Comparable<*>>()
        guessValueType(sequenceOf(1, 2.0, "a", null)) shouldBe typeOf<Comparable<*>?>()

        guessValueType(sequenceOf(1, 2.0, "a", listOf(1, 2))) shouldBe typeOf<Any>()
        guessValueType(sequenceOf(1, 2.0, "a", null, listOf(1, 2))) shouldBe typeOf<Any?>()

        guessValueType(sequenceOf(null, null)) shouldBe nothingType(nullable = true)

        guessValueType(sequenceOf(listOf<Int?>(null))) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(emptyList<Int>())) shouldBe typeOf<List<Nothing>>()
        guessValueType(sequenceOf(listOf<Int?>(null), emptyList<Int>())) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(emptyList<Int>(), null)) shouldBe typeOf<List<Nothing>?>()

        guessValueType(sequenceOf(listOf(1), emptyList())) shouldBe typeOf<List<Int>>()
        guessValueType(sequenceOf(listOf(1, null), emptyList())) shouldBe typeOf<List<Int?>>()
        guessValueType(sequenceOf(listOf(1), listOf(null))) shouldBe typeOf<List<Int?>>()

        guessValueType(sequenceOf(1, emptyList<Any?>())) shouldBe typeOf<Any>()

        guessValueType(sequenceOf(1, 2, listOf(1), emptyMap<Any, Any>())) shouldBe typeOf<Any>()
    }

    @Test
    fun `guessValueType with listification`() {
        guessValueType(sequenceOf(1, 2), listifyValues = true) shouldBe typeOf<Int>()
        guessValueType(sequenceOf(1, 2, null), listifyValues = true) shouldBe typeOf<Int?>()

        guessValueType(sequenceOf(1, 2.0), listifyValues = true) shouldBe typeOf<Number>()
        guessValueType(sequenceOf(1, 2.0, null), listifyValues = true) shouldBe typeOf<Number?>()

        guessValueType(sequenceOf(1, 2.0, "a"), listifyValues = true) shouldBe typeOf<Comparable<*>>()
        guessValueType(sequenceOf(1, 2.0, "a", null), listifyValues = true) shouldBe typeOf<Comparable<*>?>()

        guessValueType(sequenceOf(1, 2, listOf(1)), listifyValues = true) shouldBe typeOf<List<Int>>()
        guessValueType(sequenceOf(1, 2, listOf(1), null), listifyValues = true) shouldBe typeOf<List<Int>>()
        guessValueType(sequenceOf(1, 2, listOf(1, null)), listifyValues = true) shouldBe typeOf<List<Int?>>()
        guessValueType(sequenceOf(1, 2, listOf(1, null), null), listifyValues = true) shouldBe typeOf<List<Int?>>()

        guessValueType(sequenceOf(1, 2, listOf(null)), listifyValues = true) shouldBe typeOf<List<Int?>>()
        guessValueType(sequenceOf(1, 2, listOf(null), null), listifyValues = true) shouldBe typeOf<List<Int?>>()

        guessValueType(sequenceOf(emptyList<Int>()), listifyValues = true) shouldBe typeOf<List<Nothing>>()
        guessValueType(sequenceOf(emptyList<Int>(), null), listifyValues = true) shouldBe typeOf<List<Nothing>>()
        guessValueType(sequenceOf(listOf(null)), listifyValues = true) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(listOf(null), null), listifyValues = true) shouldBe typeOf<List<Nothing?>>()

        guessValueType(
            values = sequenceOf(1, 2, listOf(1), emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(1), null, emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(1, null), emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(1, null), null, emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()

        guessValueType(
            values = sequenceOf(1, 2, listOf(null), emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<List<Any>>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(null), null, emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()

        guessValueType(
            values = sequenceOf(emptyList<Int>(), emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<List<Any>>()
        guessValueType(
            values = sequenceOf(emptyList<Int>(), null, emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()
        guessValueType(
            values = sequenceOf(listOf(null), emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()
        guessValueType(
            values = sequenceOf(listOf(null), null, emptyMap<Any, Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()
    }

    @Test
    fun `baseType test no listification`() {

    }

    @Test
    fun `baseType test with listification`() {

    }
}
