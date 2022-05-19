package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.reflect.typeOf

class InferTypeTests {

    @Test
    fun `infer type 1`() {
        val col by columnOf("Alice", 1, 3.5)
        col.type() shouldBe typeOf<Comparable<*>>()
        val filtered = col.filter { it is String }
        filtered.type() shouldBe typeOf<Comparable<*>>()
        filtered.inferType().type() shouldBe typeOf<String>()
    }

    open class A<T>(val value: T)
    class B<T>(value: T) : A<T>(value)

    @Test
    fun `infer type with argument`() {
        val col by columnOf(1)
        val df = dataFrameOf(col)
        val converted = df.convert(col).with(Infer.None) {
            B(it) as A<Int>
        }
        converted[col].type() shouldBe typeOf<A<Int>>()
        converted.inferType(col)[col].type() shouldBe typeOf<B<Int>>()
    }
}
