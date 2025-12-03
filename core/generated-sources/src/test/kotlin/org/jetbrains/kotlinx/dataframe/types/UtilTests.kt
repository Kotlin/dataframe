package org.jetbrains.kotlinx.dataframe.types

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.asArrayAsListOrNull
import org.jetbrains.kotlinx.dataframe.impl.commonParent
import org.jetbrains.kotlinx.dataframe.impl.commonParents
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.commonTypeListifyValues
import org.jetbrains.kotlinx.dataframe.impl.createType
import org.jetbrains.kotlinx.dataframe.impl.getUnifiedNumberClassOrNull
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.jetbrains.kotlinx.dataframe.impl.isArray
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.replaceGenericTypeParametersWithUpperbound
import org.jetbrains.kotlinx.dataframe.toCode
import org.junit.Test
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class UtilTests {

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `isArray tests`() {
        // KClass isArray
        BooleanArray::class.isArray shouldBe true
        UIntArray::class.isArray shouldBe true
        Array::class.isArray shouldBe true

        // KClass isPrimitiveArray
        BooleanArray::class.isPrimitiveArray shouldBe true
        UIntArray::class.isPrimitiveArray shouldBe true
        Array::class.isPrimitiveArray shouldBe false

        // KType isArray
        typeOf<BooleanArray>().isArray shouldBe true
        typeOf<UIntArray>().isArray shouldBe true
        typeOf<Array<Int>>().isArray shouldBe true
        typeOf<Array<Int?>>().isArray shouldBe true
        typeOf<Array<*>>().isArray shouldBe true

        // KType isPrimitiveArray
        typeOf<BooleanArray>().isPrimitiveArray shouldBe true
        typeOf<UIntArray>().isPrimitiveArray shouldBe true
        typeOf<Array<Int>>().isPrimitiveArray shouldBe false
        typeOf<Array<Int?>>().isPrimitiveArray shouldBe false
        typeOf<Array<*>>().isPrimitiveArray shouldBe false

        // Any isArray
        booleanArrayOf().isArray shouldBe true
        uintArrayOf().isArray shouldBe true
        arrayOf(1).isArray shouldBe true
        arrayOf(1, null).isArray shouldBe true
        arrayOfNulls<Any?>(1).isArray shouldBe true

        // Any isPrimitiveArray
        booleanArrayOf().isPrimitiveArray shouldBe true
        uintArrayOf().isPrimitiveArray shouldBe true
        arrayOf(1).isPrimitiveArray shouldBe false
        arrayOf(1, null).isPrimitiveArray shouldBe false
        arrayOfNulls<Any?>(1).isPrimitiveArray shouldBe false

        // Any asArrayToList
        booleanArrayOf(true, false).asArrayAsListOrNull() shouldBe listOf(true, false)
        uintArrayOf(1u, 2u).asArrayAsListOrNull() shouldBe listOf(1u, 2u)
        arrayOf(1, 2).asArrayAsListOrNull() shouldBe listOf(1, 2)
        arrayOf(1, null).asArrayAsListOrNull() shouldBe listOf(1, null)
        arrayOfNulls<Any?>(1).asArrayAsListOrNull() shouldBe listOf(null)
        1.asArrayAsListOrNull() shouldBe null
    }

    @Test
    fun commonParentsTests() {
        commonParents(Int::class, Int::class) shouldBe listOf(Int::class)
        commonParents(Double::class, Int::class) shouldBe listOf(Comparable::class, Number::class)
        commonParents(Int::class, String::class) shouldBe listOf(Comparable::class, Serializable::class)
        commonParents(IllegalArgumentException::class, UnsupportedOperationException::class) shouldBe
            listOf(RuntimeException::class)
        commonParents(Nothing::class, Nothing::class) shouldBe listOf(Nothing::class)
        commonParents() shouldBe emptyList()
        commonParents(List::class, Set::class) shouldBe listOf(Collection::class)
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
        commonParent(List::class, Set::class) shouldBe Collection::class
    }

    @Test
    fun `createType test`() {
        emptyList<KClass<*>>().createType(nullable = false) shouldBe nothingType(nullable = false)
        emptyList<KClass<*>>().createType(nullable = true) shouldBe nothingType(nullable = true)

        listOf(Nothing::class).createType(nullable = false) shouldBe nothingType(nullable = false)
        listOf(Nothing::class).createType(nullable = true) shouldBe nothingType(nullable = true)
    }

    @Test
    fun `commonType classes test`() {
        emptyList<KClass<*>>().commonType(false, typeOf<List<Int>>()) shouldBe typeOf<List<Int>>()
        emptyList<KClass<*>>().commonType(true, typeOf<List<Int>>()) shouldBe typeOf<List<Int>?>()

        listOf(Nothing::class).commonType(false) shouldBe nothingType(nullable = false)
        listOf(Nothing::class).commonType(true) shouldBe nothingType(nullable = true)

        emptyList<KClass<*>>().commonType(false, null) shouldBe nothingType(nullable = false)
        emptyList<KClass<*>>().commonType(true, null) shouldBe nothingType(nullable = true)
    }

    val a = listOf(1, 2.0, "a")
    val b = listOf(1, 2.0, "a", null)

    val c: Int = 1

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
        guessValueType(emptySequence()) shouldBe nothingType(nullable = false)

        guessValueType(sequenceOf(listOf<Int?>(null))) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(emptyList<Int>())) shouldBe typeOf<List<Nothing>>()
        guessValueType(sequenceOf(listOf<Int?>(null), emptyList<Int>())) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(emptyList<Int>(), null)) shouldBe typeOf<List<Nothing>?>()

        guessValueType(sequenceOf(listOf(1), emptyList())) shouldBe typeOf<List<Int>>()
        guessValueType(sequenceOf(listOf(1, null), emptyList())) shouldBe typeOf<List<Int?>>()
        guessValueType(sequenceOf(listOf(1), listOf(null))) shouldBe typeOf<List<Int?>>()

        guessValueType(sequenceOf(1, emptyList<Any?>())) shouldBe typeOf<Any>()

        guessValueType(sequenceOf(1, 2, listOf(1), emptySet<Any>())) shouldBe typeOf<Any>()
        guessValueType(sequenceOf(listOf(1), setOf(1.0, 2.0))) shouldBe typeOf<Collection<Number>>()

        guessValueType(
            sequenceOf(DataColumn.empty(), columnOf(1)),
            allColsMakesRow = true,
        ) shouldBe typeOf<DataRow<*>>()

        guessValueType(
            sequenceOf(columnOf("a"), columnOf(1)),
            allColsMakesRow = true,
        ) shouldBe typeOf<DataRow<*>>()
        guessValueType(
            sequenceOf(columnOf("a"), columnOf(1)),
        ) shouldBe typeOf<DataColumn<*>>()
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
            values = sequenceOf(1, 2, listOf(1), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(1), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(1, null), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(1, null), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()

        guessValueType(
            values = sequenceOf(1, 2, listOf(null), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any>()
        guessValueType(
            values = sequenceOf(1, 2, listOf(null), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Any?>()

        guessValueType(
            values = sequenceOf(emptyList(), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Collection<Nothing>>()
        guessValueType(
            values = sequenceOf(emptyList<Int>(), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Collection<Nothing>?>()
        guessValueType(
            values = sequenceOf(listOf(null), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Collection<Nothing?>>()
        guessValueType(
            values = sequenceOf(listOf(null), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe typeOf<Collection<Nothing?>?>()
    }

    interface UpperBound

    interface TypeWithUpperbound1<T : UpperBound>

    interface TestType1<T : UpperBound> : TypeWithUpperbound1<T>

    interface TestTypeIn1<in T> : Comparable<T>

    interface TestType2<S : UpperBound> : TestTypeIn1<TestType1<S>>

    @Test
    fun replaceGenericTypeParametersWithUpperbound() {
        val typeWithUpperboundT = TestType1::class.supertypes.first() // TypeWithUpperbound<T>
        typeWithUpperboundT.replaceGenericTypeParametersWithUpperbound() shouldBe
            typeOf<TypeWithUpperbound1<UpperBound>>()

        val comparableTypeT = TestTypeIn1::class.supertypes.first() // Comparable<T>
        comparableTypeT.replaceGenericTypeParametersWithUpperbound() shouldBe typeOf<Comparable<Nothing>>()

        val nestedTypeWithUpperboundT = TestType2::class.supertypes.first() // TestTypeIn1<TestType1<S>>
        nestedTypeWithUpperboundT.replaceGenericTypeParametersWithUpperbound() shouldBe
            typeOf<TestTypeIn1<TestType1<UpperBound>>>()
    }

    interface AbstractType<T>

    @Test
    fun `commonType KTypes test`() {
        // TODO issue #471: Type inference incorrect w.r.t. variance
        listOf(null).commonType(false) shouldBe typeOf<Any?>()
//        listOf(null).commonType(true) shouldBe null
        listOf(typeOf<Int>(), typeOf<Any>()).commonType() shouldBe typeOf<Any>()
        listOf(typeOf<Int>(), typeOf<List<Any>>()).commonType() shouldBe typeOf<Any>()
        listOf(typeOf<Int>(), typeOf<List<Any>?>()).commonType() shouldBe typeOf<Any?>()
        listOf(typeOf<Int>(), typeOf<Int>()).commonType() shouldBe typeOf<Int>()
        listOf(typeOf<Int>(), typeOf<Int?>()).commonType() shouldBe typeOf<Int?>()
        listOf(typeOf<Int>(), nothingType(true)).commonType() shouldBe typeOf<Int?>()
        listOf(typeOf<Int>(), nothingType(false)).commonType() shouldBe typeOf<Int>()
        listOf(typeOf<Comparable<Int>>(), typeOf<Comparable<Int>>()).commonType() shouldBe typeOf<Comparable<Int>>()
//        listOf(typeOf<Comparable<Int>>(), typeOf<Comparable<String>>()).commonType() shouldBe typeOf<Comparable<*>>()
//        listOf(typeOf<List<Int>>(), typeOf<List<String>>()).commonType(false) shouldBe typeOf<List<Comparable<Nothing>>>()
//        listOf(typeOf<List<Int>>(), typeOf<List<String>>()).commonType() shouldBe typeOf<List<Comparable<*>>>()
//        listOf(typeOf<List<Int>>(), typeOf<Set<String>>()).commonType(false) shouldBe typeOf<Collection<Comparable<Nothing>>>()
//        listOf(typeOf<List<Int>>(), typeOf<Set<String>>()).commonType() shouldBe typeOf<Collection<Comparable<*>>>()
//        listOf(typeOf<List<Int>>(), typeOf<List<List<Any>>>()).commonType() shouldBe typeOf<List<Any>>()
//        listOf(typeOf<List<Int>>(), typeOf<List<List<Any>?>>()).commonType(false) shouldBe typeOf<List<Any?>>()
//        listOf(typeOf<List<Int>>(), typeOf<List<List<Any>?>>()).commonType() shouldBe typeOf<List<*>>()
//        listOf(typeOf<List<Nothing>>(), typeOf<Set<Nothing>>()).commonType() shouldBe typeOf<Collection<Nothing>>()
        listOf(nothingType(false)).commonType() shouldBe nothingType(false)
        listOf(nothingType(true)).commonType() shouldBe nothingType(true)
//        emptyList<KType>().commonType() shouldBe nothingType(false)
        listOf(
            typeOf<AbstractType<Int>>(),
            typeOf<AbstractType<Int>>(),
        ).commonType() shouldBe typeOf<AbstractType<Int>>()
//        listOf(typeOf<AbstractType<Int>>(), typeOf<AbstractType<Any>>()).commonType() shouldBe typeOf<AbstractType<out Any>>()
//        listOf(typeOf<AbstractType<Int>>(), typeOf<AbstractType<in Int>>()).commonType() shouldBe typeOf<AbstractType<in Int>>()
        listOf(
            typeOf<AbstractType<in Int>>(),
            typeOf<AbstractType<in Int>>(),
        ).commonType() shouldBe typeOf<AbstractType<in Int>>()
//        listOf(typeOf<AbstractType<Int>>(), typeOf<AbstractType<out Int>>()).commonType() shouldBe typeOf<AbstractType<out Int>>()
        listOf(
            typeOf<AbstractType<out Int>>(),
            typeOf<AbstractType<out Int>>(),
        ).commonType() shouldBe typeOf<AbstractType<out Int>>()
//        listOf(typeOf<AbstractType<out Int>>(), typeOf<AbstractType<in Int>>()).commonType(useStar = false) shouldBe typeOf<AbstractType<out Any?>>()
//        listOf(typeOf<AbstractType<out Int>>(), typeOf<AbstractType<in Int>>()).commonType() shouldBe typeOf<AbstractType<*>>()
//        listOf(
//            typeOf<AbstractType<in Int>>(),
//            typeOf<AbstractType<Any>>()
//        ).commonType() shouldBe typeOf<AbstractType<in Int>>()

        listOf(typeOf<Int>(), typeOf<List<Any>>()).commonType() shouldBe typeOf<Any>()
        listOf(typeOf<Int>(), typeOf<List<Any>?>()).commonType() shouldBe typeOf<Any?>()
        listOf(typeOf<Int>(), typeOf<Int>()).commonType() shouldBe typeOf<Int>()
        listOf(typeOf<Int>(), typeOf<Int?>()).commonType() shouldBe typeOf<Int?>()
        listOf(typeOf<Int>(), nothingType(true)).commonType() shouldBe typeOf<Int?>()
        listOf(typeOf<Int>(), nothingType(false)).commonType() shouldBe typeOf<Int>()
        listOf(
            typeOf<List<Int>>(),
            typeOf<List<String>>(),
        ).commonType(false) shouldBe typeOf<List<out Comparable<Nothing>>>()
        listOf(typeOf<List<Int>>(), typeOf<List<String>>()).commonType() shouldBe typeOf<List<out Comparable<*>>>()
        listOf(
            typeOf<List<Int>>(),
            typeOf<Set<String>>(),
        ).commonType(false) shouldBe typeOf<Collection<out Comparable<Nothing>>>()
        listOf(typeOf<List<Int>>(), typeOf<Set<String>>()).commonType() shouldBe typeOf<Collection<out Comparable<*>>>()
        listOf(typeOf<List<Int>>(), typeOf<List<List<Any>>>()).commonType() shouldBe typeOf<List<out Any>>()
        listOf(typeOf<List<Int>>(), typeOf<List<List<Any>?>>()).commonType(false) shouldBe typeOf<List<out Any?>>()
        listOf(typeOf<List<Int>>(), typeOf<List<List<Any>?>>()).commonType() shouldBe typeOf<List<*>>()
        listOf(typeOf<List<Nothing>>(), typeOf<Set<Nothing>>()).commonType() shouldBe typeOf<Collection<out Nothing>>()
        listOf(nothingType(false)).commonType() shouldBe nothingType(false)
        listOf(nothingType(true)).commonType() shouldBe nothingType(true)
        listOf<KType>().commonType() shouldBe typeOf<Any>()
    }

    @Test
    fun `commonTypeListifyValues test`() {
        // TODO issue #471: Type inference incorrect w.r.t. variance
        listOf<KType>().commonTypeListifyValues() shouldBe typeOf<Any>()
        listOf(typeOf<Int>(), typeOf<Int>()).commonTypeListifyValues() shouldBe typeOf<Int>()
        listOf(typeOf<Int>(), typeOf<Int?>()).commonTypeListifyValues() shouldBe typeOf<Int?>()
        listOf(typeOf<Int>(), nothingType(true)).commonTypeListifyValues() shouldBe typeOf<Int?>()
        listOf(typeOf<Int>(), nothingType(false)).commonTypeListifyValues() shouldBe typeOf<Int>()
        listOf(typeOf<Int>(), typeOf<Double>(), nothingType(true)).commonTypeListifyValues() shouldBe typeOf<Number?>()
        listOf(
            typeOf<List<Int>>(),
            typeOf<List<String>>(),
        ).commonTypeListifyValues() shouldBe typeOf<List<out Comparable<*>>>()
        listOf(
            typeOf<List<Int>>(),
            typeOf<Set<String>>(),
        ).commonTypeListifyValues() shouldBe typeOf<Collection<out Comparable<*>>>()

        listOf(
            typeOf<Int>(),
            typeOf<List<Int>>(),
        ).commonTypeListifyValues() shouldBe typeOf<List<Int>>()

        listOf(
            typeOf<Int>(),
            typeOf<List<Int>>(),
            nothingType(true),
        ).commonTypeListifyValues() shouldBe typeOf<List<Int>>()

        listOf(
            typeOf<Int>(),
            typeOf<List<Int?>>(),
            nothingType(true),
        ).commonTypeListifyValues() shouldBe typeOf<List<Int?>>()

        listOf(
            typeOf<List<Nothing>>(),
            typeOf<List<Nothing>>(),
        ).commonTypeListifyValues() shouldBe typeOf<List<Nothing>>()

        listOf(
            typeOf<List<Nothing>>(),
            nothingType(true),
        ).commonTypeListifyValues() shouldBe typeOf<List<Nothing>>()

        listOf(
            typeOf<Int>(),
            typeOf<List<Int>>(),
            typeOf<Set<Any>>(),
        ).commonTypeListifyValues() shouldBe typeOf<Any>()

        listOf(
            typeOf<Int>(),
            typeOf<List<Int>>(),
            typeOf<Set<Any>>(),
            nothingType(true),
        ).commonTypeListifyValues() shouldBe typeOf<Any?>()

        listOf(
            typeOf<List<Nothing>>(),
            typeOf<Set<Nothing>>(),
        ).commonTypeListifyValues() shouldBe typeOf<Collection<out Nothing>>()

        listOf(
            typeOf<List<Nothing>>(),
            typeOf<Set<Nothing>>(),
            nothingType(true),
        ).commonTypeListifyValues() shouldBe typeOf<Collection<out Nothing>?>()

        listOf(
            typeOf<List<Nothing?>>(),
            typeOf<Set<Nothing>>(),
        ).commonTypeListifyValues() shouldBe typeOf<Collection<out Nothing?>>()

        listOf(
            typeOf<List<Nothing?>>(),
            typeOf<Set<Nothing>?>(),
        ).commonTypeListifyValues() shouldBe typeOf<Collection<out Nothing?>?>()
    }

    /**
     * See [UnifyingNumbers] for more information.
     * ```
     *           (BigDecimal)
     *            /      \
     *     (BigInteger)   \
     *        /   \        \
     * <~ ULong   Long ~> Double ..
     * ..   |    /   |   /   |  \..
     *   \  |   /    |  /    |
     *     UInt     Int    Float
     * ..   |    /   |   /      \..
     *   \  |   /    |  /
     *    UShort   Short
     *      |    /   |
     *      |   /    |
     *    UByte     Byte
     *        \     /
     *        \    /
     *       Nothing
     * ```
     */
    @Test
    fun `common number types`() {
        // Same type
        getUnifiedNumberClassOrNull(Int::class, Int::class) shouldBe Int::class
        getUnifiedNumberClassOrNull(Double::class, Double::class) shouldBe Double::class

        // Direct parent-child relationships
        getUnifiedNumberClassOrNull(Int::class, UShort::class) shouldBe Int::class
        getUnifiedNumberClassOrNull(Long::class, UInt::class) shouldBe Long::class
        getUnifiedNumberClassOrNull(Double::class, Float::class) shouldBe Double::class
        getUnifiedNumberClassOrNull(UShort::class, Short::class) shouldBe Int::class
        getUnifiedNumberClassOrNull(UByte::class, Byte::class) shouldBe Short::class

        getUnifiedNumberClassOrNull(UByte::class, UShort::class) shouldBe UShort::class

        // Multi-level relationships
        getUnifiedNumberClassOrNull(Byte::class, Int::class) shouldBe Int::class
        getUnifiedNumberClassOrNull(UByte::class, Long::class) shouldBe Long::class
        getUnifiedNumberClassOrNull(Short::class, Double::class) shouldBe Double::class
        getUnifiedNumberClassOrNull(UInt::class, Int::class) shouldBe Long::class

        // Top-level types
        getUnifiedNumberClassOrNull(BigDecimal::class, Double::class) shouldBe BigDecimal::class
        getUnifiedNumberClassOrNull(BigInteger::class, Long::class) shouldBe BigInteger::class
        getUnifiedNumberClassOrNull(BigDecimal::class, BigInteger::class) shouldBe BigDecimal::class

        // Distant relationships
        getUnifiedNumberClassOrNull(Byte::class, BigDecimal::class) shouldBe BigDecimal::class
        getUnifiedNumberClassOrNull(UByte::class, Double::class) shouldBe Double::class

        // Complex type promotions
        getUnifiedNumberClassOrNull(Int::class, Float::class) shouldBe Double::class
        getUnifiedNumberClassOrNull(Long::class, Double::class) shouldBe BigDecimal::class
        getUnifiedNumberClassOrNull(ULong::class, Double::class) shouldBe BigDecimal::class
        getUnifiedNumberClassOrNull(BigInteger::class, Double::class) shouldBe BigDecimal::class

        // Edge case with null
        getUnifiedNumberClassOrNull(null, Int::class) shouldBe Int::class
    }

    @Test
    fun `get dataFrameOf constructor`() {
        val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
            "Alice", "Cooper", 15, "London", 54, true,
            "Bob", "Dylan", 45, "Dubai", 87, true,
            "Charlie", "Daniels", 20, "Moscow", null, false,
            "Charlie", "Chaplin", 40, "Milan", null, true,
            "Bob", "Marley", 30, "Tokyo", 68, true,
            "Alice", "Wolf", 20, null, 55, false,
            "Charlie", "Byrd", 30, "Moscow", 90, true,
        ).group("firstName", "lastName").into("name")

        df.toCode() shouldBe
            """
            val df = dataFrameOf(
                "name" to columnOf(
                    "firstName" to columnOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
                    "lastName" to columnOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
                ),
                "age" to columnOf(15, 45, 20, 40, 30, 20, 30),
                "city" to columnOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
                "weight" to columnOf(54, 87, null, null, 68, 55, 90),
                "isHappy" to columnOf(true, true, false, true, true, false, true),
            )
            """.trimIndent()
    }
}
