package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

/** Tests of [inferType]: how the type of a column is inferred from the values stored in it. */
@Suppress("ktlint:standard:argument-list-wrapping")
class InferTypeTests {

    @Test
    fun `infer type 1`() {
        val col by columnOf<Comparable<*>>("Alice", 1, 3.5)
        col.type() shouldBe typeOf<Comparable<*>>()
        val filtered = col.filter { it is String }
        filtered.type() shouldBe typeOf<Comparable<*>>()
        filtered.inferType().type() shouldBe typeOf<String>()
    }

    open class A<T>(val value: T)

    class B<T>(value: T) : A<T>(value)

    @Test
    fun `infer type with argument`() {
        val col by columnOf(1)
        val df = dataFrameOf(col)
        val converted = df.convert(col).with(Infer.None) {
            B(it) as A<Int>
        }
        converted[col].type() shouldBe typeOf<A<Int>>()
        converted.inferType(col)[col].type() shouldBe typeOf<B<Int>>()
    }

    /** A column with the given values, but a deliberately broad `Any?` type. */
    private fun broad(name: String, values: List<Any?>): DataColumn<Any?> =
        DataColumn.createValueColumn(name, values, typeOf<Any?>())

    @Test
    fun `infer type narrows a nullable column keeping its nulls`() {
        val col = broad("col", listOf(1, null, 2))
        col.type() shouldBe typeOf<Any?>()
        val inferred = col.inferType()
        inferred.type() shouldBe typeOf<Int?>()
        inferred.values().toList() shouldBe listOf(1, null, 2)
    }

    @Test
    fun `infer type infers the nearest common supertype of mixed values`() {
        val col = broad("col", listOf(1, 2.0))
        val inferred = col.inferType()
        inferred.type() shouldBe typeOf<Number>()
        // values are not converted along the way: Int stays Int, Double stays Double
        inferred.values().map { it?.javaClass?.name } shouldBe listOf("java.lang.Integer", "java.lang.Double")
    }

    @Test
    fun `infer type does not parse strings into numbers`() {
        val col = broad("col", listOf("1", "2", "3"))
        val inferred = col.inferType()
        inferred.type() shouldBe typeOf<String>()
        inferred.values().toList() shouldBe listOf("1", "2", "3")
    }

    @Test
    fun `infer type never infers outside the current column type`() {
        // Int and Double alone would infer as Number, but Number is not Comparable,
        // so the current column type stays the upper bound of the inferred type
        val col = DataColumn.createValueColumn("col", listOf<Any?>(1, 2.0), typeOf<Comparable<*>>())
        col.inferType().type() shouldBe typeOf<Comparable<*>>()
    }

    @Test
    fun `infer type on an all-null column infers nullable Nothing`() {
        val col = broad("col", listOf(null, null))
        val inferred = col.inferType()
        inferred.type() shouldBe typeOf<Nothing?>()
        inferred.size() shouldBe 2
    }

    @Test
    fun `infer type on an empty column infers Any`() {
        val col = broad("col", emptyList())
        val inferred = col.inferType()
        inferred.type() shouldBe typeOf<Any>()
        inferred.size() shouldBe 0
    }

    /** A frame with a column group `g { a: Any?, b: Any? }` holding narrow values. */
    private fun groupedFrame(): AnyFrame {
        val a = broad("a", listOf("x", "y"))
        val b = broad("b", listOf(1L, 2L))
        return dataFrameOf(a, b).group { a and b }.into("g")
    }

    /** The column group `g` keeps its kind, its nested types, and its values. */
    private fun assertGroupUntouched(df: AnyFrame) {
        val g = df["g"].asColumnGroup()
        g.kind() shouldBe ColumnKind.Group
        g["a"].type() shouldBe typeOf<Any?>()
        g["b"].type() shouldBe typeOf<Any?>()
        g["a"].values().toList() shouldBe listOf("x", "y")
        g["b"].values().toList() shouldBe listOf(1L, 2L)
    }

    @Test
    fun `infer type on a column group returns it unchanged`() {
        val col: DataColumn<*> = groupedFrame()["g"]

        val inferred = col.inferType()

        val g = inferred.asColumnGroup()
        g.kind() shouldBe ColumnKind.Group
        g["a"].type() shouldBe typeOf<Any?>()
        g["b"].type() shouldBe typeOf<Any?>()
    }

    @Test
    fun `infer type leaves a selected column group unchanged`() {
        val df = groupedFrame()

        val inferred = df.inferType("g")

        assertGroupUntouched(inferred)
    }

    @Test
    fun `infer type by columns selector leaves a selected column group unchanged`() {
        val df = groupedFrame()

        val inferred = df.inferType { pathOf("g") }

        assertGroupUntouched(inferred)
    }

    @Test
    fun `dataframe inferType refines value columns at any depth and keeps column groups`() {
        val top = broad("top", listOf(1, null))
        val a = broad("a", listOf("x", "y"))
        val b = broad("b", listOf(1L, 2L))
        val df = dataFrameOf(top, a, b).group { a and b }.into("g")

        val inferred = df.inferType()

        inferred["top"].type() shouldBe typeOf<Int?>()
        inferred["g"].kind() shouldBe ColumnKind.Group
        val g = inferred["g"].asColumnGroup()
        g["a"].type() shouldBe typeOf<String>()
        g["b"].type() shouldBe typeOf<Long>()
        inferred.columnNames() shouldBe listOf("top", "g")
        inferred.rowsCount() shouldBe 2
    }

    @Test
    fun `dataframe inferType keeps frame columns as frame columns`() {
        val top = broad("top", listOf(1, null))
        val frames = listOf(dataFrameOf("q")(1), dataFrameOf("q")(2))
        val frameCol = DataColumn.createFrameColumn("fr", frames)
        val df = dataFrameOf(top, frameCol)

        val inferred = df.inferType()

        inferred["fr"].kind() shouldBe ColumnKind.Frame
        inferred["fr"].type() shouldBe typeOf<DataFrame<*>>()
        inferred["fr"].asFrameColumn().values().first()["q"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `infer type by column name leaves a selected frame column unchanged`() {
        val top = broad("top", listOf(1, null))
        val frames = listOf(dataFrameOf("q")(1), dataFrameOf("q")(2))
        val frameCol = DataColumn.createFrameColumn("fr", frames)
        val df = dataFrameOf(top, frameCol)

        val inferred = df.inferType("fr")

        inferred["fr"].kind() shouldBe ColumnKind.Frame
        inferred["fr"].type() shouldBe typeOf<DataFrame<*>>()
        inferred["fr"].asFrameColumn().values().first()["q"].type() shouldBe typeOf<Int>()
        inferred["top"].type() shouldBe typeOf<Any?>()
    }

    @Test
    fun `dataframe inferType by column name refines only the named column`() {
        val top = broad("top", listOf(1, null))
        val other = broad("other", listOf("x", "y"))
        val df = dataFrameOf(top, other)

        val inferred = df.inferType("top")

        inferred["top"].type() shouldBe typeOf<Int?>()
        inferred["other"].type() shouldBe typeOf<Any?>()
        inferred.columnNames() shouldBe listOf("top", "other")
    }

    @Test
    fun `dataframe inferType by columns selector refines matched columns`() {
        val top = broad("top", listOf(1, null))
        val other = broad("other", listOf("x", "y"))
        val df = dataFrameOf(top, other)

        val inferred = df.inferType { colsOf<Any?>() }

        inferred["top"].type() shouldBe typeOf<Int?>()
        inferred["other"].type() shouldBe typeOf<String>()
        inferred.columnNames() shouldBe listOf("top", "other")
    }

    @Test
    fun `dataframe inferType on an empty dataframe refines its columns`() {
        val col = broad("col", emptyList())
        val df = dataFrameOf(col)

        val inferred = df.inferType()

        inferred.rowsCount() shouldBe 0
        inferred["col"].type() shouldBe typeOf<Any>()
    }

    @Test
    fun `refined types enable type-based column selection`() {
        val value = broad("value", listOf(1, null))
        val df = dataFrameOf(value)

        val refined = df.inferType("value")
        val selected = refined.select { colsOf<Int?>() }

        selected.columnNames() shouldBe listOf("value")
    }
}
