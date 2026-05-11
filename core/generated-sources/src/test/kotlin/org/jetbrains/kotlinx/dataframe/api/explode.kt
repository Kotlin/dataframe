package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class ExplodeTests {

    @Test
    fun `explode into`() {
        val df = dataFrameOf("a" to listOf(1), "b" to listOf(listOf(2, 3)))
        val exploded = df.explode { "b" into "c" }
        val expected = dataFrameOf("a" to listOf(1, 1), "c" to listOf(2, 3))
        exploded shouldBe expected
    }

    @Test
    fun `explode list and duplicate value`() {
        val exploded = dataFrameOf(
            "a", "b",
        )(
            1, listOf(2, 3),
        ).explode()
        exploded shouldBe dataFrameOf("a", "b")(1, 2, 1, 3)
    }

    @Test
    fun `explode list and frame column`() {
        val exploded = dataFrameOf(
            "a", "b", "c", "d",
        )(
            1, listOf(2, 3), dataFrameOf("x", "y")(4, 5, 6, 7), listOf(8),
        )
            .explode()
            .ungroup("c")
        exploded shouldBe dataFrameOf("a", "b", "x", "y", "d")(
            1, 2, 4, 5, 8,
            1, 3, 6, 7, null,
        )
    }

    @Test
    fun `explode nothing`() {
        val df = dataFrameOf("a", "b")(1, 2)
        df.explode() shouldBe df
    }

    @Test
    fun `explode multiple aligned columns`() {
        val a by columnOf(listOf(1, 2), listOf(3, 4, 5))
        val b by columnOf(listOf(1, 2, 3), listOf(4, 5))

        val df = dataFrameOf(a, b)
        val exploded = df.explode { a and b }

        val expected = dataFrameOf("a", "b")(
            1, 1,
            2, 2,
            null, 3,
            3, 4,
            4, 5,
            5, null,
        )

        exploded shouldBe expected
    }

    @Test
    fun `explode with empty list and dropEmpty true`() {
        val df = dataFrameOf("a", "b")(
            1, listOf(1, 2),
            2, emptyList<Int>(),
            3, listOf(3),
        )

        val exploded = df.explode(dropEmpty = true)

        val expected = dataFrameOf("a", "b")(
            1, 1,
            1, 2,
            3, 3,
        )

        exploded shouldBe expected
    }

    @Test
    fun `explode with empty list and dropEmpty false`() {
        val df = dataFrameOf("a", "b")(
            1, listOf(1, 2),
            2, emptyList<Int>(),
            3, listOf(3),
        )

        val exploded = df.explode(dropEmpty = false)

        val expected = dataFrameOf("a", "b")(
            1, 1,
            1, 2,
            2, null,
            3, 3,
        )

        exploded shouldBe expected
    }

    @Test
    fun `explode DataColumn of lists`() {
        val col by columnOf(listOf(1, 2), listOf(3, 4))

        val exploded = col.explode()
        val expected = columnOf(1, 2, 3, 4) named "col"

        exploded shouldBe expected
    }

    @Test
    fun `explode FrameColumn into ColumnGroup`() {
        val col by columnOf(
            dataFrameOf("x", "y")(1, 2, 3, 4),
            dataFrameOf("x", "y")(5, 6, 7, 8),
        )

        val exploded = col.explode()

        val expected = dataFrameOf("x", "y")(
            1, 2,
            3, 4,
            5, 6,
            7, 8,
        ).asColumnGroup("col")

        exploded shouldBe expected
    }
}
