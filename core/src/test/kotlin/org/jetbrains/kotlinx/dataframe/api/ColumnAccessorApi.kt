package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.junit.Test

class ColumnAccessorApiTests {

    // the second row has no score
    private val dfWithNulls = dataFrameOf("score")(1.0, null, 0.5)

    private val dfWithoutNulls = dataFrameOf("score")(1.0, 0.5)

    private fun DataFrame<*>.countScores(accessor: ColumnAccessor<Double?>) =
        filter { accessor.getValue(this) != null }.rowsCount()

    @Test
    fun `KDoc example nullable`() {
        val scores = dataFrameOf("name", "score")("Alice", 1.0, "Bob", 0.5)
        // no "score" column here, so `concat` fills it with `null` for this row
        val other = dataFrameOf("name")("Charlie")
        val df = scores.concat(other)

        val score by column<Double>()
        val scoreOrNull = score.nullable()

        df.countScores(scoreOrNull) shouldBe 2
    }

    @Test
    fun `a null read through a non-nullable accessor fails only at the first use`() {
        val score = column<Double>("score")
        val row = dfWithNulls[1]

        // the value arrives as `null`, even though the accessor is typed as `Double`
        val raw: Any? = score.getValue(row)
        raw shouldBe null

        // the failure happens later, when the value is used as a `Double`
        shouldThrow<NullPointerException> { score.getValue(row) > 0.0 }
    }

    @Test
    fun `nullable returns the same accessor with a nullable value type`() {
        val score = column<Double>("score")

        // the return type is the contract here, so it is written down explicitly
        val scoreOrNull: ColumnAccessor<Double?> = score.nullable()

        scoreOrNull shouldBeSameInstanceAs score
    }

    @Test
    fun `nullable keeps the name and the path`() {
        val nested = column<Int>(ColumnPath(listOf("group", "value"))).nullable()

        nested.name() shouldBe "value"
        nested.path() shouldBe ColumnPath(listOf("group", "value"))
    }

    @Test
    fun `nullable keeps the name of a column group accessor`() {
        val group: ColumnAccessor<DataRow<*>?> = columnGroup("group").nullable()

        group.name() shouldBe "group"
    }

    @Test
    fun `nullable does not require the column to exist`() {
        // there is no column with this name anywhere, and still nothing fails
        val missing: ColumnAccessor<Double?> = column<Double>("missing").nullable()

        missing.name() shouldBe "missing"
    }

    @Test
    fun `nullable is allowed for a column without nulls`() {
        val scoreOrNull = column<Double>("score").nullable()

        dfWithoutNulls.countScores(scoreOrNull) shouldBe 2
    }

    @Test
    fun `nullable on an already nullable accessor changes nothing`() {
        val score = column<Double?>("score")

        val twice: ColumnAccessor<Double?> = score.nullable()

        twice shouldBeSameInstanceAs score
    }

    @Test
    fun `castToNullable on an accessor returns a ColumnReference`() {
        val score = column<Double>("score")

        // the declared type is `ColumnReference`, so the `ColumnAccessor` members are gone,
        // even though the object itself is still the very same accessor
        val scoreOrNull: ColumnReference<Double?> = score.castToNullable()

        scoreOrNull shouldBeSameInstanceAs score
    }
}
