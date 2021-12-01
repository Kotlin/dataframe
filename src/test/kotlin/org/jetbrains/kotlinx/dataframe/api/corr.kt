package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.doubles.ToleranceMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

class CorrTests {

    val df = dataFrameOf("a", "b", "c")(
        3, true, 1,
        6, false, 2
    )

    @Test
    fun `corr with boolean`() {
        val corr = df.corr("a", "b").with("c")
        corr.nrow shouldBe 2
        corr.ncol shouldBe 2
        corr.getColumn(0) shouldBe (columnOf("a", "b") named "column")
        corr.getColumn(1).name() shouldBe "c"
        corr["c"][0] as Double should ToleranceMatcher(1.0, 0.01)
        corr["c"][1] as Double should ToleranceMatcher(-1.0, 0.01)
    }

    @Test
    fun `corr group`() {
        val corr = df.group("a", "b").into("g")
            .corr("g").with("c")

        corr shouldBe df.corr("a", "b").with("c").rename("column" to "g")
    }

    @Test
    fun `corr itself`() {
        val corr = df.corr()
        val expected = dataFrameOf("column", "a", "b", "c")(
            "a", 1.0, -1.0, 1.0,
            "b", -1.0, 1.0, -1.0,
            "c", 1.0, -1.0, 1.0
        )
        corr.columns().zip(expected.columns()).forEach { (a, b) ->
            a.type() shouldBe b.type()
            if (a.isNumber()) {
                a.name() shouldBe b.name()
                a.values().zip(b.values()).forEach { (v1, v2) ->
                    v1 as Double should ToleranceMatcher(v2 as Double, 0.01)
                }
            } else {
                a shouldBe b
            }
        }
    }
}
