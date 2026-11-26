package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.junit.Test
import kotlin.reflect.typeOf

class ReverseTests {

    @Test
    fun dataframe() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        df.reverse() shouldBe dataFrameOf("a", "b")(3, 4, 1, 2)
    }

    /** `reverse` on a [DataFrame] only changes the row order; the schema stays the same. */
    @Test
    fun `dataframe keeps schema and size`() {
        val df = dataFrameOf("a", "b")(1, "x", 2, "y", 3, "z")
        val reversed = df.reverse()

        reversed.schema() shouldBe df.schema()
        reversed.rowsCount() shouldBe df.rowsCount()
    }

    @Test
    fun column() {
        val col by columnOf(1, 2, 3)
        col.reverse() shouldBe listOf(3, 2, 1).toColumn("col")
    }

    /** `reverse` on a [DataColumn] keeps the name and type of the column. */
    @Test
    fun `column keeps name and type`() {
        val col by columnOf(1, 2, 3)
        val reversed = col.reverse()

        reversed.name() shouldBe col.name()
        reversed.type() shouldBe col.type()
        reversed.size() shouldBe col.size()
    }

    /**
     * A column reversed through the [DataColumn] overload stays the same [ColumnKind] as before,
     * even though the overload is typed to return a plain [DataColumn].
     */
    @Test
    fun `column overload keeps the column kind`() {
        val a by columnOf(1, 2)
        // the DataColumn<*> types are load-bearing: they make the calls below resolve to the
        // DataColumn overload instead of the ColumnGroup / FrameColumn / ValueColumn ones
        val group: DataColumn<*> = columnOf(a).named("group")
        val frame: DataColumn<*> = columnOf(dataFrameOf("a")(1), dataFrameOf("a")(2)).named("frame")
        val value: DataColumn<*> = columnOf(1, 2).named("value")

        group.reverse().kind() shouldBe ColumnKind.Group
        frame.reverse().kind() shouldBe ColumnKind.Frame
        value.reverse().kind() shouldBe ColumnKind.Value
    }

    /**
     * A [ColumnGroup] is not a [DataColumn] subtype, so getting the group type back after the
     * [DataColumn] overload requires [asColumnGroup].
     */
    @Test
    fun `column group type can be restored after the column overload`() {
        val a by columnOf(1, 2, 3)
        val group: DataColumn<*> = columnOf(a).named("group")

        val reversed: ColumnGroup<*> = group.reverse().asColumnGroup()

        reversed.name() shouldBe "group"
        reversed["a"].toList() shouldBe listOf(3, 2, 1)
    }

    @Test
    fun columnGroup() {
        val a by columnOf(1, 2)
        val b by columnOf(3, 4)
        val col by columnOf(a, b)
        col.reverse() shouldBe columnOf(a.reverse(), b.reverse()).named("col")
    }

    /**
     * `reverse` on a [ColumnGroup] reverses the group as a whole: the nested structure is unchanged
     * and values in all nested columns stay aligned row-wise.
     *
     * Note that `columnOf(a, b)` is statically a [DataColumn], so a group has to be built explicitly
     * for the [ColumnGroup] overload to be the one under test.
     */
    @Test
    fun `column group reverses rows as a whole`() {
        val group: ColumnGroup<*> =
            DataColumn.createColumnGroup("group", dataFrameOf("a", "b")(1, "x", 2, "y", 3, "z"))
        val reversed: ColumnGroup<*> = group.reverse()

        // name and nested structure are unchanged
        reversed.name() shouldBe group.name()
        reversed.schema() shouldBe group.schema()

        // values stay aligned row-wise: whole rows are reversed, not each column independently
        // (a per-column shift would pair 3 with "x" instead of "z")
        reversed.rows().map { it["a"] to it["b"] } shouldBe listOf(3 to "z", 2 to "y", 1 to "x")

        // a ColumnGroup is a DataFrame, so this is the same row reversal as DataFrame.reverse
        reversed shouldBe group.asDataFrame().reverse()
    }

    /**
     * `reverse` on a [FrameColumn] only reverses the order of the frames;
     * rows inside each frame keep their original order.
     */
    @Test
    fun frameColumn() {
        val df1 = dataFrameOf("a")(1, 2)
        val df2 = dataFrameOf("a")(3, 4)
        val col by columnOf(df1, df2)
        val reversed = col.reverse()

        // the frames swap places, but rows inside them are untouched:
        // had they been reversed too, the frames would hold (4, 3) and (2, 1)
        reversed shouldBe columnOf(df2, df1).named("col")
    }

    /** `reverse` on a [ValueColumn] returns a [ValueColumn], not a plain [DataColumn]. */
    @Test
    fun valueColumn() {
        // columnOf() is statically a DataColumn, so the ValueColumn has to be built explicitly;
        // the ValueColumn<Int> type below is the actual assertion — it is checked by the compiler
        val col: ValueColumn<Int> = DataColumn.createValueColumn("col", listOf(1, 2, 3), typeOf<Int>())
        val reversed: ValueColumn<Int> = col.reverse()

        reversed.toList() shouldBe listOf(3, 2, 1)
        reversed.kind() shouldBe ColumnKind.Value
        reversed.name() shouldBe "col"
        reversed.type() shouldBe col.type()
    }
}
