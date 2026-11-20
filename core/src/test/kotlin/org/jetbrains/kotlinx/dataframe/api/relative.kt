package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

class RelativeTests {

    private val df = dataFrameOf("value")(10, 20, 30, 40, 50)

    @Test
    fun `relative ranges entirely outside the dataframe return no rows`() {
        df[2].relative(3..5) shouldBe df[emptyList<Int>()]
        df[2].relative(-5..-3) shouldBe df[emptyList<Int>()]
    }

    @Test
    fun `empty relative ranges remain empty even beyond the dataframe bounds`() {
        for (offsets in listOf(IntRange.EMPTY, 5..3, -3..-5)) {
            df[2].relative(offsets) shouldBe df[emptyList<Int>()]
        }
    }

    @Test
    fun `relative ranges include only the overlapping rows`() {
        df[2].relative(-4..0) shouldBe df[0..2]
        df[2].relative(0..4) shouldBe df[2..4]
        df[2].relative(-4..4) shouldBe df
    }

    @Test
    fun `relative ranges handle extreme offsets without overflow`() {
        df[2].relative(Int.MIN_VALUE..Int.MAX_VALUE) shouldBe df
        df[2].relative(0..Int.MAX_VALUE) shouldBe df[2..4]
        df[2].relative(Int.MIN_VALUE..0) shouldBe df[0..2]
        df[2].relative(Int.MAX_VALUE..Int.MAX_VALUE) shouldBe df[emptyList<Int>()]
        df[2].relative(Int.MIN_VALUE..Int.MIN_VALUE) shouldBe df[emptyList<Int>()]
    }

    @Test
    fun `relative ranges at the first and last rows drop missing neighbors`() {
        df[0].relative(-2..0) shouldBe df[0..0]
        df[4].relative(0..2) shouldBe df[4..4]
        df[0].relative(-2..-1) shouldBe df[emptyList<Int>()]
        df[4].relative(1..2) shouldBe df[emptyList<Int>()]
    }

    @Test
    fun `relative ranges on a single row handle empty and overlapping windows`() {
        val single = dataFrameOf("value")(10)

        single[0].relative(-1..1) shouldBe single
        single[0].relative(-2..-1) shouldBe single[emptyList<Int>()]
        single[0].relative(1..2) shouldBe single[emptyList<Int>()]
        single[0].relative(IntRange.EMPTY) shouldBe single[emptyList<Int>()]
    }

    @Test
    fun `relative ranges agree with iterable offsets`() {
        for (rowIndex in 0 until df.nrow) {
            for (start in -7..7) {
                for (end in -7..7) {
                    val offsets = start..end
                    df[rowIndex].relative(offsets) shouldBe df[rowIndex].relative(offsets.toList())
                }
            }
        }
    }

    @Test
    fun `iterable relative offsets retain order and duplicates`() {
        df[2].relative(listOf(2, -2, 0, 2, 8, -8)) shouldBe df[4, 0, 2, 4]
    }
}
