package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.junit.Test
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class ColumnAccessorApiTests {

    // the second row has no score
    private val dfWithNulls = dataFrameOf("score")(1.0, null, 0.5)

    private val dfWithoutNulls = dataFrameOf("score")(1.0, 0.5)

    /**
     * The value type of this accessor, exactly as the compiler infers it here.
     *
     * A type annotation would not do: [ColumnAccessor] is covariant, so `ColumnAccessor<Double?>` also
     * accepts a `ColumnAccessor<Double>` and such an assertion would hold without [nullable] as well.
     * `reified` captures the type argument itself, which is the only thing [nullable] changes.
     */
    private inline fun <reified T> ColumnAccessor<T>.valueType(): KType = typeOf<T>()

    /** The static type of this expression, as opposed to the class of the object behind it. */
    private inline fun <reified R> R.staticType(): KType = typeOf<R>()

    @Test
    fun `KDoc example nullable`() {
        val scores = dataFrameOf("name", "score")("Alice", 1.0, "Bob", 0.5)
        // no "score" column here, so `concat` fills it with `null` for this row
        val other = dataFrameOf("name")("Charlie")
        val df = scores.concat(other)
        val row = df.last()

        val score by column<Double>()
        val scoreOrNull = score.nullable()

        // typed as `Double`, the `null` is invisible to the compiler and throws when the value is used
        shouldThrow<NullPointerException> { score.getValue(row) > 0.0 }

        // typed as `Double?`, the same `null` has to be handled
        (scoreOrNull.getValue(row)?.let { it > 0.0 } == true) shouldBe false
    }

    @Test
    fun `a null read through a non-nullable Double accessor throws when the value is used as a Double`() {
        val score = column<Double>("score")
        val row = dfWithNulls[1]

        // the value arrives as `null`, even though the accessor is typed as `Double`
        val raw: Any? = score.getValue(row)
        raw shouldBe null

        // the failure happens later, when the value is used as a `Double`
        shouldThrow<NullPointerException> { score.getValue(row) > 0.0 }
    }

    @Test
    fun `nullable widens the value type of the accessor`() {
        val score = column<Double>("score")

        score.valueType() shouldBe typeOf<Double>()
        score.nullable().valueType() shouldBe typeOf<Double?>()
    }

    @Test
    fun `nullable returns the very same accessor`() {
        val score = column<Double>("score")

        score.nullable() shouldBeSameInstanceAs score
    }

    @Test
    fun `nullable keeps the name and the path`() {
        val nested = column<Int>(ColumnPath(listOf("group", "value"))).nullable()

        nested.name() shouldBe "value"
        nested.path() shouldBe ColumnPath(listOf("group", "value"))
    }

    @Test
    fun `nullable keeps the name of a column group accessor`() {
        val group = columnGroup("group").nullable()

        group.name() shouldBe "group"
        group.valueType() shouldBe typeOf<DataRow<*>?>()
    }

    @Test
    fun `nullable does not require the column to exist`() {
        // there is no column with this name anywhere, and still nothing fails
        val missing = column<Double>("missing").nullable()

        missing.name() shouldBe "missing"
        missing.valueType() shouldBe typeOf<Double?>()
    }

    @Test
    fun `nullable is allowed for a column without nulls`() {
        val scoreOrNull = column<Double>("score").nullable()

        scoreOrNull.getValue(dfWithoutNulls[0]) shouldBe 1.0
        scoreOrNull.getValue(dfWithoutNulls[1]) shouldBe 0.5
    }

    @Test
    fun `nullable on an already nullable accessor changes nothing`() {
        val score = column<Double?>("score")

        val twice = score.nullable()

        twice shouldBeSameInstanceAs score
        twice.valueType() shouldBe typeOf<Double?>()
    }

    @Test
    fun `castToNullable on an accessor returns a ColumnReference`() {
        val score = column<Double>("score")

        // the `ColumnAccessor` members are gone from the result, even though the object itself
        // is still the very same accessor
        score.castToNullable().staticType() shouldBe typeOf<ColumnReference<Double?>>()
        score.castToNullable() shouldBeSameInstanceAs score
    }
}
