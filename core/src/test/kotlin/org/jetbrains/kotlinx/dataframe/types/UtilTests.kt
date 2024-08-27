package org.jetbrains.kotlinx.dataframe.types

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.asArrayAsListOrNull
import org.jetbrains.kotlinx.dataframe.impl.commonParent
import org.jetbrains.kotlinx.dataframe.impl.commonParents
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.commonTypeListifyValues
import org.jetbrains.kotlinx.dataframe.impl.createType
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.jetbrains.kotlinx.dataframe.impl.isArray
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import org.jetbrains.kotlinx.dataframe.impl.replaceGenericTypeParametersWithUpperbound
import org.jetbrains.kotlinx.dataframe.util.TypeOf
import org.junit.Test
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
class UtilTests {

    private val `List(Int)` = typeOf<List<Int>>()
    private val `List(Int)?` = typeOf<List<Int>?>()
    private val `List(Int?)` = typeOf<List<Int?>>()

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
        emptyList<KClass<*>>().createType(nullable = false) shouldBe TypeOf.NOTHING
        emptyList<KClass<*>>().createType(nullable = true) shouldBe TypeOf.NULLABLE_NOTHING

        listOf(Nothing::class).createType(nullable = false) shouldBe TypeOf.NOTHING
        listOf(Nothing::class).createType(nullable = true) shouldBe TypeOf.NULLABLE_NOTHING
    }

    @Test
    fun `commonType classes test`() {
        emptyList<KClass<*>>().commonType(false, `List(Int)`) shouldBe `List(Int)`
        emptyList<KClass<*>>().commonType(true, `List(Int)`) shouldBe `List(Int)?`

        listOf(Nothing::class).commonType(false) shouldBe TypeOf.NOTHING
        listOf(Nothing::class).commonType(true) shouldBe TypeOf.NULLABLE_NOTHING

        emptyList<KClass<*>>().commonType(false, null) shouldBe TypeOf.NOTHING
        emptyList<KClass<*>>().commonType(true, null) shouldBe TypeOf.NULLABLE_NOTHING
    }

    val a = listOf(1, 2.0, "a")
    val b = listOf(1, 2.0, "a", null)

    val c: Int = 1

    @Test
    fun `guessValueType no listification`() {
        guessValueType(sequenceOf(1, 2)) shouldBe TypeOf.INT
        guessValueType(sequenceOf(1, 2, null)) shouldBe TypeOf.NULLABLE_INT

        guessValueType(sequenceOf(1, 2.0)) shouldBe TypeOf.NUMBER
        guessValueType(sequenceOf(1, 2.0, null)) shouldBe TypeOf.NULLABLE_NUMBER

        guessValueType(sequenceOf(1, 2.0, "a")) shouldBe typeOf<Comparable<*>>()
        guessValueType(sequenceOf(1, 2.0, "a", null)) shouldBe typeOf<Comparable<*>?>()

        guessValueType(sequenceOf(1, 2.0, "a", listOf(1, 2))) shouldBe TypeOf.ANY
        guessValueType(sequenceOf(1, 2.0, "a", null, listOf(1, 2))) shouldBe TypeOf.NULLABLE_ANY

        guessValueType(sequenceOf(null, null)) shouldBe TypeOf.NULLABLE_NOTHING
        guessValueType(emptySequence()) shouldBe TypeOf.NOTHING

        guessValueType(sequenceOf(listOf<Int?>(null))) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(emptyList<Int>())) shouldBe typeOf<List<Nothing>>()
        guessValueType(sequenceOf(listOf<Int?>(null), emptyList<Int>())) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(emptyList<Int>(), null)) shouldBe typeOf<List<Nothing>?>()

        guessValueType(sequenceOf(listOf(1), emptyList())) shouldBe `List(Int)`
        guessValueType(sequenceOf(listOf(1, null), emptyList())) shouldBe `List(Int?)`
        guessValueType(sequenceOf(listOf(1), listOf(null))) shouldBe `List(Int?)`

        guessValueType(sequenceOf(1, emptyList<Any?>())) shouldBe TypeOf.ANY

        guessValueType(sequenceOf(1, 2, listOf(1), emptySet<Any>())) shouldBe TypeOf.ANY
        guessValueType(sequenceOf(listOf(1), setOf(1.0, 2.0))) shouldBe typeOf<Collection<Number>>()
    }

    @Test
    fun `guessValueType with listification`() {
        guessValueType(sequenceOf(1, 2), listifyValues = true) shouldBe TypeOf.INT
        guessValueType(sequenceOf(1, 2, null), listifyValues = true) shouldBe TypeOf.NULLABLE_INT

        guessValueType(sequenceOf(1, 2.0), listifyValues = true) shouldBe TypeOf.NUMBER
        guessValueType(sequenceOf(1, 2.0, null), listifyValues = true) shouldBe TypeOf.NULLABLE_NUMBER

        guessValueType(sequenceOf(1, 2.0, "a"), listifyValues = true) shouldBe typeOf<Comparable<*>>()
        guessValueType(sequenceOf(1, 2.0, "a", null), listifyValues = true) shouldBe typeOf<Comparable<*>?>()

        guessValueType(sequenceOf(1, 2, listOf(1)), listifyValues = true) shouldBe `List(Int)`
        guessValueType(sequenceOf(1, 2, listOf(1), null), listifyValues = true) shouldBe `List(Int)`
        guessValueType(sequenceOf(1, 2, listOf(1, null)), listifyValues = true) shouldBe `List(Int?)`
        guessValueType(sequenceOf(1, 2, listOf(1, null), null), listifyValues = true) shouldBe `List(Int?)`

        guessValueType(sequenceOf(1, 2, listOf(null)), listifyValues = true) shouldBe `List(Int?)`
        guessValueType(sequenceOf(1, 2, listOf(null), null), listifyValues = true) shouldBe `List(Int?)`

        guessValueType(sequenceOf(emptyList<Int>()), listifyValues = true) shouldBe typeOf<List<Nothing>>()
        guessValueType(sequenceOf(emptyList<Int>(), null), listifyValues = true) shouldBe typeOf<List<Nothing>>()
        guessValueType(sequenceOf(listOf(null)), listifyValues = true) shouldBe typeOf<List<Nothing?>>()
        guessValueType(sequenceOf(listOf(null), null), listifyValues = true) shouldBe typeOf<List<Nothing?>>()

        guessValueType(
            values = sequenceOf(1, 2, listOf(1), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe TypeOf.ANY
        guessValueType(
            values = sequenceOf(1, 2, listOf(1), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe TypeOf.NULLABLE_ANY
        guessValueType(
            values = sequenceOf(1, 2, listOf(1, null), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe TypeOf.ANY
        guessValueType(
            values = sequenceOf(1, 2, listOf(1, null), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe TypeOf.NULLABLE_ANY

        guessValueType(
            values = sequenceOf(1, 2, listOf(null), emptySet<Any>()),
            listifyValues = true,
        ) shouldBe TypeOf.ANY
        guessValueType(
            values = sequenceOf(1, 2, listOf(null), null, emptySet<Any>()),
            listifyValues = true,
        ) shouldBe TypeOf.NULLABLE_ANY

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
        listOf(null).commonType(false) shouldBe TypeOf.NULLABLE_ANY
//        listOf(null).commonType(true) shouldBe null
        listOf(TypeOf.INT, TypeOf.ANY).commonType() shouldBe TypeOf.ANY
        listOf(TypeOf.INT, typeOf<List<Any>>()).commonType() shouldBe TypeOf.ANY
        listOf(TypeOf.INT, typeOf<List<Any>?>()).commonType() shouldBe TypeOf.NULLABLE_ANY
        listOf(TypeOf.INT, TypeOf.INT).commonType() shouldBe TypeOf.INT
        listOf(TypeOf.INT, TypeOf.NULLABLE_INT).commonType() shouldBe TypeOf.NULLABLE_INT
        listOf(TypeOf.INT, TypeOf.NULLABLE_NOTHING).commonType() shouldBe TypeOf.NULLABLE_INT
        listOf(TypeOf.INT, TypeOf.NOTHING).commonType() shouldBe TypeOf.INT
        listOf(typeOf<Comparable<Int>>(), typeOf<Comparable<Int>>()).commonType() shouldBe typeOf<Comparable<Int>>()
//        listOf(typeOf<Comparable<Int>>(), typeOf<Comparable<String>>()).commonType() shouldBe typeOf<Comparable<*>>()
//        listOf(INT_LIST, typeOf<List<String>>()).commonType(false) shouldBe typeOf<List<Comparable<Nothing>>>()
//        listOf(INT_LIST, typeOf<List<String>>()).commonType() shouldBe typeOf<List<Comparable<*>>>()
//        listOf(INT_LIST, typeOf<Set<String>>()).commonType(false) shouldBe typeOf<Collection<Comparable<Nothing>>>()
//        listOf(INT_LIST, typeOf<Set<String>>()).commonType() shouldBe typeOf<Collection<Comparable<*>>>()
//        listOf(INT_LIST, typeOf<List<List<Any>>>()).commonType() shouldBe typeOf<List<Any>>()
//        listOf(INT_LIST, typeOf<List<List<Any>?>>()).commonType(false) shouldBe typeOf<List<Any?>>()
//        listOf(INT_LIST, typeOf<List<List<Any>?>>()).commonType() shouldBe typeOf<List<*>>()
//        listOf(typeOf<List<Nothing>>(), typeOf<Set<Nothing>>()).commonType() shouldBe typeOf<Collection<Nothing>>()
        listOf(TypeOf.NOTHING).commonType() shouldBe TypeOf.NOTHING
        listOf(TypeOf.NULLABLE_NOTHING).commonType() shouldBe TypeOf.NULLABLE_NOTHING
//        emptyList<KType>().commonType() shouldBe NOTHING
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

        listOf(TypeOf.INT, typeOf<List<Any>>()).commonType() shouldBe TypeOf.ANY
        listOf(TypeOf.INT, typeOf<List<Any>?>()).commonType() shouldBe TypeOf.NULLABLE_ANY
        listOf(TypeOf.INT, TypeOf.INT).commonType() shouldBe TypeOf.INT
        listOf(TypeOf.INT, TypeOf.NULLABLE_INT).commonType() shouldBe TypeOf.NULLABLE_INT
        listOf(TypeOf.INT, TypeOf.NULLABLE_NOTHING).commonType() shouldBe TypeOf.NULLABLE_INT
        listOf(TypeOf.INT, TypeOf.NOTHING).commonType() shouldBe TypeOf.INT
        listOf(
            `List(Int)`,
            typeOf<List<String>>(),
        ).commonType(false) shouldBe typeOf<List<out Comparable<Nothing>>>()
        listOf(`List(Int)`, typeOf<List<String>>()).commonType() shouldBe typeOf<List<out Comparable<*>>>()
        listOf(
            `List(Int)`,
            typeOf<Set<String>>(),
        ).commonType(false) shouldBe typeOf<Collection<out Comparable<Nothing>>>()
        listOf(`List(Int)`, typeOf<Set<String>>()).commonType() shouldBe typeOf<Collection<out Comparable<*>>>()
        listOf(`List(Int)`, typeOf<List<List<Any>>>()).commonType() shouldBe typeOf<List<out Any>>()
        listOf(`List(Int)`, typeOf<List<List<Any>?>>()).commonType(false) shouldBe typeOf<List<out Any?>>()
        listOf(`List(Int)`, typeOf<List<List<Any>?>>()).commonType() shouldBe typeOf<List<*>>()
        listOf(typeOf<List<Nothing>>(), typeOf<Set<Nothing>>()).commonType() shouldBe typeOf<Collection<out Nothing>>()
        listOf(TypeOf.NOTHING).commonType() shouldBe TypeOf.NOTHING
        listOf(TypeOf.NULLABLE_NOTHING).commonType() shouldBe TypeOf.NULLABLE_NOTHING
        listOf<KType>().commonType() shouldBe TypeOf.ANY
    }

    @Test
    fun `commonTypeListifyValues test`() {
        // TODO issue #471: Type inference incorrect w.r.t. variance
        listOf<KType>().commonTypeListifyValues() shouldBe TypeOf.ANY
        listOf(TypeOf.INT, TypeOf.INT).commonTypeListifyValues() shouldBe TypeOf.INT
        listOf(TypeOf.INT, TypeOf.NULLABLE_INT).commonTypeListifyValues() shouldBe TypeOf.NULLABLE_INT
        listOf(TypeOf.INT, TypeOf.NULLABLE_NOTHING).commonTypeListifyValues() shouldBe TypeOf.NULLABLE_INT
        listOf(TypeOf.INT, TypeOf.NOTHING).commonTypeListifyValues() shouldBe TypeOf.INT
        listOf(
            TypeOf.INT,
            TypeOf.DOUBLE,
            TypeOf.NULLABLE_NOTHING
        ).commonTypeListifyValues() shouldBe TypeOf.NULLABLE_NUMBER
        listOf(
            `List(Int)`,
            typeOf<List<String>>(),
        ).commonTypeListifyValues() shouldBe typeOf<List<out Comparable<*>>>()
        listOf(
            `List(Int)`,
            typeOf<Set<String>>(),
        ).commonTypeListifyValues() shouldBe typeOf<Collection<out Comparable<*>>>()

        listOf(
            TypeOf.INT,
            `List(Int)`,
        ).commonTypeListifyValues() shouldBe `List(Int)`

        listOf(
            TypeOf.INT,
            `List(Int)`,
            TypeOf.NULLABLE_NOTHING,
        ).commonTypeListifyValues() shouldBe `List(Int)`

        listOf(
            TypeOf.INT,
            `List(Int?)`,
            TypeOf.NULLABLE_NOTHING,
        ).commonTypeListifyValues() shouldBe `List(Int?)`

        listOf(
            typeOf<List<Nothing>>(),
            typeOf<List<Nothing>>(),
        ).commonTypeListifyValues() shouldBe typeOf<List<Nothing>>()

        listOf(
            typeOf<List<Nothing>>(),
            TypeOf.NULLABLE_NOTHING,
        ).commonTypeListifyValues() shouldBe typeOf<List<Nothing>>()

        listOf(
            TypeOf.INT,
            `List(Int)`,
            typeOf<Set<Any>>(),
        ).commonTypeListifyValues() shouldBe TypeOf.ANY

        listOf(
            TypeOf.INT,
            `List(Int)`,
            typeOf<Set<Any>>(),
            TypeOf.NULLABLE_NOTHING,
        ).commonTypeListifyValues() shouldBe TypeOf.NULLABLE_ANY

        listOf(
            typeOf<List<Nothing>>(),
            typeOf<Set<Nothing>>(),
        ).commonTypeListifyValues() shouldBe typeOf<Collection<out Nothing>>()

        listOf(
            typeOf<List<Nothing>>(),
            typeOf<Set<Nothing>>(),
            TypeOf.NULLABLE_NOTHING,
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
}
