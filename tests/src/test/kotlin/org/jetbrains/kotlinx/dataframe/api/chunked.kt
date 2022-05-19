package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.size
import org.junit.Test

class ChunkedTests {

    @Test
    fun chunkedColumnGroup() {
        val a by columnOf(listOf(1, 2, 3).toColumn("b"), listOf(4, 5, 6).toColumn("c"))
        val chunked = a.asColumnGroup().chunked(2)
        chunked.size shouldBe 2
        chunked.name() shouldBe "a"
        chunked[1].rowsCount() shouldBe 1
    }
}
