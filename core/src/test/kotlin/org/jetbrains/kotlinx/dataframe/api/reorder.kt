package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class ReorderTests {

    @Test
    fun simple() {
        val df = dataFrameOf("b", "c", "a").fill(1, 0)
        df.reorder { all() }.byName().columnNames() shouldBe listOf("a", "b", "c")
        df.reorder { "a" and "c" }.byName().columnNames() shouldBe listOf("b", "a", "c")
        df.reorder { "a" and "b" }.byName().columnNames() shouldBe listOf("a", "c", "b")
    }

    @Test
    fun nested() {
        val df = dataFrameOf("b", "c", "a")
            .fill(1, 0)
            .group("c", "a")
            .into("a")

        df.reorder { all() }.byName().columnNames() shouldBe listOf("a", "b")

        val sorted1 = df.reorder { "a".allCols() }.byName()
        sorted1.columnNames() shouldBe listOf("b", "a")
        sorted1["a"].asColumnGroup().columnNames() shouldBe listOf("a", "c")

        val sorted2 = df.reorder { colsAtAnyDepth() }.byName()
        sorted2.columnNames() shouldBe listOf("a", "b")
        sorted2["a"].asColumnGroup().columnNames() shouldBe listOf("a", "c")
    }

    // [d, c, b, a, g[z, y]]
    private val grouped = dataFrameOf("d", "c", "b", "a", "z", "y")(1, 2, 3, 4, 5, 6).group("z", "y").into("g")

    // [k, frame[k, v2, v1]]
    private val withFrameColumn = dataFrameOf("k", "v2", "v1")(1, 2, 3, 1, 4, 5).groupBy("k").toDataFrame("frame")

    @Test
    fun `values move together with their columns`() {
        val df = dataFrameOf("d", "c", "b", "a")(1, 2, 3, 4)

        df.reorder("d", "a").byName() shouldBe dataFrameOf("a", "c", "b", "d")(4, 2, 3, 1)
    }

    @Test
    fun `KDoc example - cast and by keep the values of the columns`() {
        val df = dataFrameOf("c", "d", "a", "b")(
            3, 4, 1, 2,
            1, 1, 1, 1,
        )

        df.reorder("d", "b").cast<Int>().by { sum() } shouldBe dataFrameOf("c", "b", "a", "d")(
            3, 2, 1, 4,
            1, 1, 1, 1,
        )
    }

    @Test
    fun `reorderColumnsBy does not change the values in the columns`() {
        val df = dataFrameOf("d", "c", "b", "a")(1, 2, 3, 4)

        df.reorderColumnsByName() shouldBe dataFrameOf("a", "b", "c", "d")(4, 3, 2, 1)
    }

    @Test
    fun `columns from different groups are reordered within their own groups`() {
        val reordered = grouped.reorder { "d" and "b" and "g"["z"] and "g"["y"] }.byName()

        // "b" and "d" swap places at the top level, "y" and "z" swap places inside "g"
        reordered.columnNames() shouldBe listOf("b", "c", "d", "a", "g")
        reordered.getColumnGroup("g").columnNames() shouldBe listOf("y", "z")
    }

    @Test
    fun `a column group selected together with other columns moves as a whole`() {
        val reordered = grouped.reorder { "a" and "g" }.byDesc { it.name() }

        reordered.columnNames() shouldBe listOf("d", "c", "b", "g", "a")
        // the nested columns of "g" keep their order
        reordered.getColumnGroup("g").columnNames() shouldBe listOf("z", "y")
    }

    @Test
    fun `byDesc puts the column with the largest value first`() {
        val df = dataFrameOf("c", "d", "a", "b")(
            3, 4, 1, 2,
            1, 1, 1, 1,
        )

        df.reorder("a", "b").cast<Int>().byDesc { sum() }.columnNames() shouldBe listOf("c", "d", "b", "a")
    }

    // "a" and "d" have the length 1, "bb" and "cc" have the length 2
    private val equalNameLengths = dataFrameOf("bb", "a", "cc", "d")(1, 2, 3, 4)

    @Test
    fun `by keeps the original order of columns with equal values`() {
        equalNameLengths.reorder { all() }.by { it.name().length }.columnNames() shouldBe
            listOf("a", "d", "bb", "cc")
    }

    @Test
    fun `byDesc keeps the original order of columns with equal values`() {
        equalNameLengths.reorder { all() }.byDesc { it.name().length }.columnNames() shouldBe
            listOf("bb", "cc", "a", "d")
    }

    @Test
    fun `reorderColumnsBy reorders columns at any depth by default`() {
        val reordered = grouped.reorderColumnsBy { name() }

        reordered.columnNames() shouldBe listOf("a", "b", "c", "d", "g")
        reordered.getColumnGroup("g").columnNames() shouldBe listOf("y", "z")
    }

    @Test
    fun `reorderColumnsBy with atAnyDepth = false reorders only top-level columns`() {
        val reordered = grouped.reorderColumnsBy(atAnyDepth = false) { name() }

        reordered.columnNames() shouldBe listOf("a", "b", "c", "d", "g")
        reordered.getColumnGroup("g").columnNames() shouldBe listOf("z", "y")
    }

    // [g[z, y]]: the only top-level column is a column group
    private val singleGroup = dataFrameOf("z", "y")(1, 2).group("z", "y").into("g")

    @Test
    fun `reorderColumnsBy with atAnyDepth = false keeps nested columns of a single column group`() {
        singleGroup.reorderColumnsBy(atAnyDepth = false) { name() }.getColumnGroup("g").columnNames() shouldBe
            listOf("z", "y")
    }

    @Test
    fun `reorderColumnsByName with atAnyDepth = false keeps nested columns of a single column group`() {
        singleGroup.reorderColumnsByName(atAnyDepth = false).getColumnGroup("g").columnNames() shouldBe
            listOf("z", "y")
    }

    @Test
    fun `reorderColumnsByName with atAnyDepth reorders nested columns of a single column group`() {
        singleGroup.reorderColumnsByName().getColumnGroup("g").columnNames() shouldBe listOf("y", "z")
    }

    @Test
    fun `reorderColumnsBy with desc = true applies descending order`() {
        val reordered = grouped.reorderColumnsBy(desc = true) { name() }

        reordered.columnNames() shouldBe listOf("g", "d", "c", "b", "a")
        reordered.getColumnGroup("g").columnNames() shouldBe listOf("z", "y")
    }

    @Test
    fun `reorderColumnsBy with atAnyDepth reorders columns of every dataframe in a frame column`() {
        val reordered = withFrameColumn.reorderColumnsBy { name() }

        reordered.columnNames() shouldBe listOf("frame", "k")
        reordered.getFrameColumn("frame").values().map { it.columnNames() } shouldBe
            listOf(listOf("k", "v1", "v2"))
    }

    @Test
    fun `reorderColumnsBy with atAnyDepth reorders columns of a frame column inside a column group`() {
        // [k, grp[frame[k, v2, v1]]]
        val df = withFrameColumn.group("frame").into("grp")

        val frame = df.reorderColumnsBy { name() }.getColumnGroup("grp").getFrameColumn("frame")
        frame.values().map { it.columnNames() } shouldBe listOf(listOf("k", "v1", "v2"))
    }

    @Test
    fun `reorderColumnsBy with atAnyDepth = false does not reorder columns in a frame column`() {
        val reordered = withFrameColumn.reorderColumnsBy(atAnyDepth = false) { name() }

        reordered.columnNames() shouldBe listOf("frame", "k")
        reordered.getFrameColumn("frame").values().map { it.columnNames() } shouldBe
            listOf(listOf("k", "v2", "v1"))
    }

    @Test
    fun `reorderColumnsBy sorts by the value of the expression`() {
        val df = dataFrameOf("ccc", "a", "bb")(1, 2, 3)

        df.reorderColumnsBy { name().length }.columnNames() shouldBe listOf("a", "bb", "ccc")
    }

    @Test
    fun `reorderColumnsByName sorts columns by name at any depth`() {
        val reordered = grouped.reorderColumnsByName()

        reordered.columnNames() shouldBe listOf("a", "b", "c", "d", "g")
        reordered.getColumnGroup("g").columnNames() shouldBe listOf("y", "z")
    }

    @Test
    fun `reorderColumnsByName sorts only top-level columns in descending order`() {
        val reordered = grouped.reorderColumnsByName(atAnyDepth = false, desc = true)

        reordered.columnNames() shouldBe listOf("g", "d", "c", "b", "a")
        reordered.getColumnGroup("g").columnNames() shouldBe listOf("z", "y")
    }
}
