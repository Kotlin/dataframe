package org.jetbrains.kotlinx.dataframe.impl

import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.reflect.typeOf

class TypeUtils {
    @Test
    fun `isPrimitiveArray test`() {
        // All primitive array types -> true
        typeOf<IntArray>().isPrimitiveArray shouldBe true
        typeOf<ByteArray>().isPrimitiveArray shouldBe true
        typeOf<ShortArray>().isPrimitiveArray shouldBe true
        typeOf<LongArray>().isPrimitiveArray shouldBe true
        typeOf<FloatArray>().isPrimitiveArray shouldBe true
        typeOf<DoubleArray>().isPrimitiveArray shouldBe true
        typeOf<BooleanArray>().isPrimitiveArray shouldBe true
        typeOf<CharArray>().isPrimitiveArray shouldBe true

        // Unsigned primitive arrays -> true
        @OptIn(ExperimentalUnsignedTypes::class)
        run {
            typeOf<UByteArray>().isPrimitiveArray shouldBe true
            typeOf<UShortArray>().isPrimitiveArray shouldBe true
            typeOf<UIntArray>().isPrimitiveArray shouldBe true
            typeOf<ULongArray>().isPrimitiveArray shouldBe true
        }

        // Nullable primitive arrays -> true
        typeOf<IntArray?>().isPrimitiveArray shouldBe true
        typeOf<ByteArray?>().isPrimitiveArray shouldBe true
        typeOf<DoubleArray?>().isPrimitiveArray shouldBe true
        typeOf<BooleanArray?>().isPrimitiveArray shouldBe true

        // Array<T> (has type arguments) -> false
        typeOf<Array<Int>>().isPrimitiveArray shouldBe false
        typeOf<Array<Int?>>().isPrimitiveArray shouldBe false
        typeOf<Array<String>>().isPrimitiveArray shouldBe false
        typeOf<Array<*>>().isPrimitiveArray shouldBe false
        typeOf<Array<Any>>().isPrimitiveArray shouldBe false

        // Non-array types -> false
        typeOf<Int>().isPrimitiveArray shouldBe false
        typeOf<String>().isPrimitiveArray shouldBe false
        typeOf<List<Int>>().isPrimitiveArray shouldBe false
        typeOf<Map<String, Int>>().isPrimitiveArray shouldBe false
        typeOf<Any>().isPrimitiveArray shouldBe false
        typeOf<Nothing?>().isPrimitiveArray shouldBe false
    }
}
