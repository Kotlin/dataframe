package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.apache.arrow.vector.util.Text
import org.jetbrains.kotlinx.dataframe.testArrowFeather
import org.junit.Test


internal class ArrowKtTest {
    @Test
    fun testReadingFromFile() {
        val feather = testArrowFeather("data-arrow_2.0.0_uncompressed")
        val df = feather.readDataFrame()
        df.rowsCount() shouldBe 1
        df.columnNames() shouldBe listOf("a", "b", "c", "d")
        df["a"].toList() shouldBe listOf("one")
        df["b"].toList() shouldBe listOf(2.0)
        df["c"].toList() shouldBe listOf(mapOf(
            "c1" to Text("inner"),
            "c2" to 4.0,
            "c3" to 50.0
        ))
        df["d"].toList() shouldBe listOf("four")
    }
}
