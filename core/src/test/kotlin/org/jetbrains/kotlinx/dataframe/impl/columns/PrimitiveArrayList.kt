package org.jetbrains.kotlinx.dataframe.impl.columns

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test

class PrimitiveArrayListTests {

    @Test
    fun `test primitive array list`() {
        val list = PrimitiveArrayList<Int>() as PrimitiveArrayList<Any>
        list.addBoxed(1)
        shouldThrow<ClassCastException> { list.remove(2.0) }
        list.addAll(listOf(2, 3))

        (list as PrimitiveArrayList<Int>).toIntArray() shouldBe intArrayOf(1, 2, 3)
    }

    @Test
    fun `test empty primitive array list`() {
        val list = PrimitiveArrayList<Any>()
        list.isEmpty() shouldBe true
        list.size shouldBe 0

        list.removeBoxed(1234) shouldBe false
        list.remove(1234.2) shouldBe false

        list.addBoxed(1)
        list.canAdd(1) shouldBe true
        list.canAdd(1.0) shouldBe false

        shouldThrow<ClassCastException> { list.addBoxed(1.0) }

        list.isEmpty() shouldBe false
        list.size shouldBe 1
        list.clear()
        list.isEmpty() shouldBe true
        list.size shouldBe 0

        list.state shouldBe PrimitiveArrayList.State.INT
    }

    @Test
    fun `test specific primitive array list`() {
        val list = PrimitiveArrayList<Int>()
        list += 1
        list += 2
        list += 3
        list.toIntArray() shouldBe intArrayOf(1, 2, 3)
    }
}
