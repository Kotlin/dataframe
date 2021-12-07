package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import org.jetbrains.kotlinx.dataframe.emptyDataFrame
import org.junit.Test

class GetTests {

    @Test
    fun `exceptions from empty dataframe`() {
        val empty = emptyDataFrame()
        shouldThrow<NoSuchElementException> {
            empty.first()
        }
        shouldThrow<NoSuchElementException> {
            empty.last()
        }
        shouldThrow<IndexOutOfBoundsException> {
            empty[0]
        }
    }
}
