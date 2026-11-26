package org.jetbrains.kotlinx.dataframe.impl

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import java.net.URL
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Instant

class Rendering {

    @Test
    fun `renderType by KType test`() {
        // null
        renderType(null) shouldBe "*"

        // Nothing?
        renderType(typeOf<Nothing?>()) shouldBe "Nothing?"

        // Primitive arrays
        renderType(typeOf<IntArray>()) shouldBe "IntArray"
        renderType(typeOf<ByteArray>()) shouldBe "ByteArray"
        renderType(typeOf<LongArray>()) shouldBe "LongArray"
        renderType(typeOf<DoubleArray>()) shouldBe "DoubleArray"
        renderType(typeOf<FloatArray>()) shouldBe "FloatArray"
        renderType(typeOf<BooleanArray>()) shouldBe "BooleanArray"

        // Array<T>
        renderType(typeOf<Array<Int>>()) shouldBe "Array<Int>"
        renderType(typeOf<Array<String>>()) shouldBe "Array<String>"
        renderType(typeOf<Array<Int?>>()) shouldBe "Array<Int?>"
        renderType(typeOf<Array<*>>()) shouldBe "Array<*>"

        // URL
        renderType(typeOf<URL>()) shouldBe "URL"

        // kotlinx.datetime
        renderType(typeOf<LocalDateTime>()) shouldBe "LocalDateTime"
        renderType(typeOf<LocalTime>()) shouldBe "LocalTime"

        // kotlin.collections
        renderType(typeOf<List<Int>>()) shouldBe "List<Int>"
        renderType(typeOf<Map<String, Int>>()) shouldBe "Map<String, Int>"
        renderType(typeOf<Set<String>>()) shouldBe "Set<String>"
        renderType(typeOf<MutableList<Int>>()) shouldBe "List<Int>"

        // kotlin.time
        renderType(typeOf<Duration>()) shouldBe "Duration"
        renderType(typeOf<Instant>()) shouldBe "Instant"

        // kotlin.*
        renderType(typeOf<Int>()) shouldBe "Int"
        renderType(typeOf<String>()) shouldBe "String"
        renderType(typeOf<Boolean>()) shouldBe "Boolean"
        renderType(typeOf<Double>()) shouldBe "Double"

        // nullable types
        renderType(typeOf<Int?>()) shouldBe "Int?"
        renderType(typeOf<String?>()) shouldBe "String?"
        renderType(typeOf<List<Int>?>()) shouldBe "List<Int>?"
        renderType(typeOf<List<Int?>>()) shouldBe "List<Int?>"

        // dataframe types
        renderType(typeOf<DataFrame<*>>()) shouldBe "DataFrame<*>"

        // non-kotlin types (fully qualified)
        renderType(typeOf<java.math.BigDecimal>()) shouldBe "java.math.BigDecimal"

        // variance
        renderType(typeOf<List<*>>()) shouldBe "List<*>"
        renderType(typeOf<Comparable<in Int>>()) shouldBe "Comparable<in Int>"
    }
}
