package org.jetbrains.kotlinx.dataframe.impl.columns

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test

class PrimitiveArrayListTests {

    @Test
    fun `test primitive array list`() {
        val list = PrimitiveArrayList<Int>(PrimitiveArrayList.State.INT) as PrimitiveArrayList<Any>
        list.add(1)
        list.remove(2.0) shouldBe false
        list.addAll(listOf(2, 3))

        (list as PrimitiveArrayList<Int>).toIntArray() shouldBe intArrayOf(1, 2, 3)
    }

    @Test
    fun `test empty primitive array list`() {
        val list = PrimitiveArrayList<Any>()
        list.isEmpty() shouldBe true
        list.size shouldBe 0

        list.remove(1234) shouldBe false
        list.remove(1234.2) shouldBe false

        list.add(1)
        list.canAdd(1) shouldBe true
        list.canAdd(1.0) shouldBe false

        shouldThrow<IllegalArgumentException> { list.add(1.0) }

        list.isEmpty() shouldBe false
        list.size shouldBe 1
        list.clear()
        list.isEmpty() shouldBe true
        list.size shouldBe 0

        list.state shouldBe PrimitiveArrayList.State.INT
    }
}
