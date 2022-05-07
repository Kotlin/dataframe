package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test

class MapTests {

    @Test
    fun `map frame column with empty frames`() {
        val frames by columnOf(dataFrameOf("a")(1), DataFrame.Empty)
        frames.map { it.firstOrNull() }.size() shouldBe frames.size()
    }
}
