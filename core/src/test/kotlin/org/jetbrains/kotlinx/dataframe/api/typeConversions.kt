package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.junit.Test
import java.util.NoSuchElementException
import kotlin.reflect.typeOf

/**
 * Pins the KDoc contracts of `api/typeConversions.kt`: casts and nullability (`asNumbers`, `asComparable`,
 * `castTo*`), to-array conversions, unchecked kind casts, identity views, column builders
 * from iterables and arrays, accessors, ColumnSet casts, DataFrame/DataRow/Map conversions,
 * and `NullabilityOptions.applyNullability`.
 */
@Suppress("ktlint:standard:argument-list-wrapping")
class TypeConversionsTests {

    // region asNumbers / asComparable

    @Test
    fun `asNumbers casts a number column to a ValueColumn keeping its values and runtime type`() {
        val col: DataColumn<Any?> = columnOf(1, 2, 3)
        val numbers = col.asNumbers()
        numbers.toList() shouldBe listOf(1, 2, 3)
        // the runtime type of the column stays the same — this is a cast, not a conversion
        numbers.type() shouldBe typeOf<Int>()
    }

    @Test
    fun `asNumbers throws IllegalArgumentException on a non-number column`() {
        val col: DataColumn<Any?> = columnOf("a", "b")
        shouldThrow<IllegalArgumentException> { col.asNumbers() }
    }

    @Test
    fun `asComparable casts a column of comparable values keeping its values`() {
        val col: DataColumn<Int> = columnOf(3, 1, 2)
        col.asComparable().toList() shouldBe listOf(3, 1, 2)
    }

    @Test
    fun `asComparable throws IllegalArgumentException when the values are not mutually comparable`() {
        val col: DataColumn<Any> = columnOf<Any>(1, "a", true)
        shouldThrow<IllegalArgumentException> { col.asComparable() }
    }

    @Test
    fun `asNumbers on a DataColumn of Any keeps the values`() {
        val col: DataColumn<Any> = columnOf<Any>(1, 2)
        col.asNumbers().toList() shouldBe listOf(1, 2)
    }

    @Test
    fun `asComparable casts a nullable column of comparable values keeping its values`() {
        val col: DataColumn<Int?> = columnOf(1, null, 3)
        col.asComparable().toList() shouldBe listOf(1, null, 3)
    }

    // endregion

    // region nullability casts

    @Test
    fun `castToNotNullable throws IllegalArgumentException when the column has nulls`() {
        val col: DataColumn<Int?> = columnOf(1, null, 3).rename("x")
        val exception = shouldThrow<IllegalArgumentException> { col.castToNotNullable() }
        exception.message shouldBe "Column `x` has nulls"
    }

    @Test
    fun `castToNotNullable returns the same values when there are no nulls`() {
        val col: DataColumn<Int?> = columnOf(1, 2, 3)
        col.castToNotNullable().toList() shouldBe listOf(1, 2, 3)
    }

    @Test
    fun `castToNullable changes only the static type, the stored column type stays the same`() {
        val col: DataColumn<Int> = columnOf(1, 2, 3)
        // the cast succeeds, but the stored type of the column is still Int
        col.castToNullable().hasNulls() shouldBe false
        col.castToNullable().type() shouldBe typeOf<Int>()
    }

    // endregion

    // region to arrays

    private val numbers: DataColumn<Number> = columnOf<Number>(1, 2L, 3.5f)

    @Test
    fun `toFloatArray converts the number values and returns a copy`() {
        val array = numbers.toFloatArray()
        array.toList() shouldBe listOf(1.0f, 2.0f, 3.5f)
        array[0] = 99f
        // the array is a copy — changing it doesn't affect the column
        numbers[0] shouldBe 1
    }

    @Test
    fun `toDoubleArray converts the number values`() {
        numbers.toDoubleArray().toList() shouldBe listOf(1.0, 2.0, 3.5)
    }

    @Test
    fun `toIntArray truncates the number values`() {
        numbers.toIntArray().toList() shouldBe listOf(1, 2, 3)
    }

    @Test
    fun `toLongArray truncates the number values`() {
        numbers.toLongArray().toList() shouldBe listOf(1L, 2L, 3L)
    }

    @Test
    fun `toShortArray truncates the number values`() {
        numbers.toShortArray().toList() shouldBe listOf<Short>(1, 2, 3)
    }

    @Test
    fun `toByteArray truncates the number values`() {
        numbers.toByteArray().toList() shouldBe listOf<Byte>(1, 2, 3)
    }

    @Test
    fun `toTypedArray returns all values copied into a new array`() {
        val col: DataColumn<Int> = columnOf(3, 1, 2)
        val array = col.toTypedArray()
        array.toList() shouldBe listOf(3, 1, 2)
        array[0] = 99
        col[0] shouldBe 3
    }

    // endregion

    // region unchecked kind casts

    @Test
    fun `asValueColumn throws ClassCastException on a column that is not a value column`() {
        val group = dataFrameOf("first" to columnOf("Alice", "Bob")).asColumnGroup("group")
        // a ColumnGroup is a column at runtime, so the cast succeeds and the failure comes from asValueColumn
        val column: DataColumn<*> = group as DataColumn<*>
        shouldThrow<ClassCastException> { column.asValueColumn() }
    }

    @Test
    fun `asColumnGroup throws ClassCastException on a column that is not a group`() {
        val col: DataColumn<*> = columnOf(1, 2)
        shouldThrow<ClassCastException> { col.asColumnGroup() }
    }

    @Test
    fun `asFrameColumn throws ClassCastException on a column that is not a frame column`() {
        val col: DataColumn<*> = columnOf(1, 2)
        shouldThrow<ClassCastException> { col.asFrameColumn() }
    }

    // endregion

    // region identity views

    @Test
    fun `a ColumnGroup is both a column and a DataFrame`() {
        val group: ColumnGroup<*> = dataFrameOf("first" to columnOf("Alice", "Bob")).asColumnGroup("group")
        (group.asDataColumn() as Any === group as Any) shouldBe true
        (group.asDataFrame() as Any === group as Any) shouldBe true
    }

    @Test
    fun `FrameColumn asDataColumn returns the same object`() {
        val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
        val col = DataColumn.createFrameColumn("data", listOf(df1))
        (col.asDataColumn() as Any === col as Any) shouldBe true
    }

    @Test
    fun `FrameColumn toValueColumn always throws`() {
        val col = DataColumn.createFrameColumn("data", listOf(dataFrameOf("name")("Alice")))
        // the always-on column-kind invariant: a ValueColumn cannot contain AnyFrame values
        shouldThrow<IllegalArgumentException> { col.toValueColumn() }
    }

    @Test
    fun `asFrameColumn casts a column of DataFrames to a FrameColumn`() {
        val df1 = dataFrameOf("name")("Alice")
        val column: DataColumn<DataFrame<*>> = DataColumn.createFrameColumn("data", listOf(df1))
        val frameColumn = column.asFrameColumn()
        frameColumn.kind() shouldBe ColumnKind.Frame
        frameColumn.toList() shouldBe listOf(df1)
    }

    @Test
    fun `asColumnGroup casts a column of DataRows to a ColumnGroup`() {
        val group = dataFrameOf("first" to columnOf("Alice", "Bob")).asColumnGroup("person")
        val column: DataColumn<DataRow<*>> = group.asDataColumn()
        column.asColumnGroup().columnNames() shouldBe listOf("first")
    }

    @Test
    fun `asDataFrame returns a column of DataRows as a DataFrame`() {
        val group = dataFrameOf("first" to columnOf("Alice", "Bob")).asColumnGroup("person")
        val column: DataColumn<DataRow<*>> = group.asDataColumn()
        column.asDataFrame().columnNames() shouldBe listOf("first")
    }

    // endregion

    // region toDataFrame / builders

    @Test
    fun `BaseColumn toDataFrame creates a single-column DataFrame`() {
        val col = columnOf(1, 2).rename("x")
        val df = col.toDataFrame()
        df.columnNames() shouldBe listOf("x")
        df.rowsCount() shouldBe 2
        df["x"].toList() shouldBe listOf(1, 2)
    }

    @Test
    fun `toColumn takes the type from T and computes hasNulls from the values by default`() {
        val col = listOf(1, null).toColumn("x")
        col.type() shouldBe typeOf<Int?>()
        col.hasNulls() shouldBe true
    }

    @Test
    fun `toColumn with Infer Type infers the type from the values`() {
        // the example from the createColumn documentation page
        val values: List<Any?> = listOf(1, 2.5)
        values.toColumn("data", Infer.Type).type() shouldBe typeOf<Number>()
    }

    @Test
    fun `KDoc example toColumn`() {
        val col = listOf("Alice", "Bob").toColumn("name")
        col.name() shouldBe "name"
        col.toList() shouldBe listOf("Alice", "Bob")
    }

    @Test
    fun `KDoc example toColumnOf casts the values to T unchecked`() {
        val values: List<Any?> = listOf(1, 2.5)
        val col = values.toColumnOf<Number?>("data")
        col.type() shouldBe typeOf<Number?>()
        col.toList() shouldBe listOf(1, 2.5)
    }

    @Test
    fun `toValueColumn creates a value column with the given name and reified type`() {
        val col = listOf(1, 2).toValueColumn("v")
        col.name() shouldBe "v"
        col.type() shouldBe typeOf<Int>()
        col.toList() shouldBe listOf(1, 2)
    }

    @Test
    fun `toFrameColumn creates a frame column from the frames`() {
        val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
        val df2 = dataFrameOf("name", "age")("Charlie", 30)
        val col = listOf(df1, df2).toFrameColumn("frames")
        col.kind() shouldBe ColumnKind.Frame
        col.size() shouldBe 2
        col[0].columnNames() shouldBe listOf("name", "age")
    }

    @Test
    fun `toColumnGroup nests the given columns under a group with the given name`() {
        val first = columnOf("Alice", "Bob").rename("first")
        val last = columnOf("Cooper", "Marley").rename("last")
        val group = listOf<BaseColumn<*>>(first, last).toColumnGroup("fullName")
        group.kind() shouldBe ColumnKind.Group
        group.name() shouldBe "fullName"
        group.columnNames() shouldBe listOf("first", "last")
    }

    @Test
    fun `toColumnGroupOf creates a group typed as T without a runtime check`() {
        val first = columnOf("Alice", "Bob").rename("first")
        val last = columnOf("Cooper", "Marley").rename("last")
        val group = listOf<BaseColumn<*>>(first, last).toColumnGroupOf<Unit>("g")
        group.kind() shouldBe ColumnKind.Group
        group.name() shouldBe "g"
        group.columnNames() shouldBe listOf("first", "last")
    }

    @Test
    fun `toPath creates a column path from the names`() {
        listOf("a", "b").toPath() shouldBe ColumnPath(listOf("a", "b"))
        arrayOf("a", "b").toPath() shouldBe ColumnPath(listOf("a", "b"))
    }

    @Test
    fun `Array toValueColumn creates a column with the reified type`() {
        val col = arrayOf(1, 2).toValueColumn("v")
        col.name() shouldBe "v"
        col.type() shouldBe typeOf<Int>()
        col.toList() shouldBe listOf(1, 2)
    }

    // endregion

    // region accessors

    @Test
    fun `String toColumnAccessor resolves the column`() {
        val df = dataFrameOf("age")(10, 20)
        df.getColumn { "age".toColumnAccessor() }.name() shouldBe "age"
    }

    @Test
    fun `String toColumnOf resolves the column with the given type`() {
        val df = dataFrameOf("age")(10, 20)
        df.getColumn { "age".toColumnOf<Int>() }.name() shouldBe "age"
    }

    @Test
    fun `ColumnReference toColumnAccessor returns an existing accessor unchanged`() {
        val accessor = "age".toColumnAccessor()
        (accessor.toColumnAccessor() === accessor) shouldBe true
    }

    @Test
    fun `ColumnReference toColumnAccessor creates an accessor from a renamed reference`() {
        val reference = "x".toColumnOf<Int>().rename("y")
        reference.toColumnAccessor().name() shouldBe "y"
    }

    @Test
    fun `ColumnPath toColumnAccessor resolves a nested column`() {
        val person = dataFrameOf("first" to columnOf("Alice", "Bob")).asColumnGroup("person")
        val df = dataFrameOf("person" to person.asDataColumn())
        df.getColumn { ColumnPath(listOf("person", "first")).toColumnAccessor() }.name() shouldBe "first"
    }

    // endregion

    // region ColumnSet / SingleColumn casts

    @Test
    fun `ColumnSet asNumbers leaves the columns usable when they are numbers`() {
        val df = dataFrameOf("a", "b")(1, 2L, 3, 4L)
        df.select { all().asNumbers() }.columnNames() shouldBe listOf("a", "b")
    }

    @Test
    fun `ColumnSet asNumbers is a fully unchecked cast, the columns stay unchanged`() {
        val df = dataFrameOf("a", "s")(1, "x", 2, "y")
        val selected = df.select { all().asNumbers() }
        // no check is performed — the string column keeps its values and type
        selected["s"].toList() shouldBe listOf("x", "y")
        selected["s"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `SingleColumn asNumbers selects a number column`() {
        val df = dataFrameOf("a", "b")(1, 2L, 3, 4L)
        df.select { "a".toColumnOf<Any>().asNumbers() }.columnNames() shouldBe listOf("a")
    }

    @Test
    fun `ColumnSet asComparable leaves inter-comparable columns usable`() {
        val df = dataFrameOf("a", "b")(1, 2L, 3, 4L)
        df.select { all().asComparable() }.columnNames() shouldBe listOf("a", "b")
    }

    @Test
    fun `SingleColumn asComparable selects a comparable column`() {
        val df = dataFrameOf("a", "b")(1, 2L, 3, 4L)
        df.select { "a".toColumnOf<Any>().asComparable() }.columnNames() shouldBe listOf("a")
    }

    // endregion

    // region DataFrame conversions

    @Test
    fun `toMap maps column names to their value lists`() {
        val df = dataFrameOf("a", "b")(1, "x", 2, "y")
        df.toMap() shouldBe mapOf("a" to listOf(1, 2), "b" to listOf("x", "y"))
    }

    @Test
    fun `toMap returns copies of the value lists, mutating them doesn't affect the DataFrame`() {
        val df = dataFrameOf("a")(1, 2)
        val map = df.toMap()
        // the downcast proves the returned list is a detached copy: the mutation succeeds
        // while the DataFrame still sees its original values
        (map["a"]!! as MutableList<Any?>).add(99)
        df["a"].toList() shouldBe listOf(1, 2)
    }

    @Test
    fun `toList returns a detached list, mutating it doesn't affect the DataFrame`() {
        val df = dataFrameOf("a")(1, 2)
        // the downcast proves the returned list is a detached copy: the mutation succeeds
        // while the DataFrame still sees its original values
        (df["a"].toList() as MutableList<Any?>).add(99)
        df["a"].toList() shouldBe listOf(1, 2)
    }

    @Test
    fun `values returns a read-only view, mutating it throws and doesn't affect the DataFrame`() {
        val df = dataFrameOf("a")(1, 2)
        // the downcast succeeds (any Java List is a MutableList), but the mutation throws
        shouldThrow<UnsupportedOperationException> { (df["a"].values() as MutableList<Any?>).add(99) }
        df["a"].toList() shouldBe listOf(1, 2)
    }

    @Test
    fun `asColumnGroup wraps a DataFrame into a group with the given name`() {
        val df = dataFrameOf("x")(1, 2)
        val group = df.asColumnGroup("g")
        group.kind() shouldBe ColumnKind.Group
        group.name() shouldBe "g"
        group.columnNames() shouldBe listOf("x")
        // the nested columns are not copied
        (group.columns().single() as Any === df.columns().single() as Any) shouldBe true
    }

    @Test
    fun `asColumnGroup renames an existing group without copying its columns`() {
        val df = dataFrameOf("x")(1, 2)
        val group = df.asColumnGroup("g")
        val renamed = group.asColumnGroup("new")
        renamed.name() shouldBe "new"
        (renamed.columns().single() as Any === group.columns().single() as Any) shouldBe true
    }

    private fun groupedDf(): DataFrame<*> {
        val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
        val df2 = dataFrameOf("name", "age")("Charlie", 30)
        val dataCol by columnOf(df1, df2)
        val keyCol by columnOf(1, 2)
        return dataFrameOf(dataCol, keyCol)
    }

    @Test
    fun `asGroupBy by column name treats the frame column as the groups`() {
        val groupBy = groupedDf().asGroupBy("dataCol")
        groupBy.groups.size() shouldBe 2
        groupBy.groups[0].columnNames() shouldBe listOf("name", "age")
        // the remaining columns of the DataFrame become the key columns
        groupBy.keys.columnNames() shouldBe listOf("keyCol")
    }

    @Test
    fun `asGroupBy by column name throws when there is no such column`() {
        val exception = shouldThrow<IllegalArgumentException> { groupedDf().asGroupBy("missing") }
        exception.message shouldBe "Column not found: 'missing'"
    }

    @Test
    fun `asGroupBy by column name throws ClassCastException when the column is not a frame column`() {
        shouldThrow<ClassCastException> { groupedDf().asGroupBy("keyCol") }
    }

    @Test
    fun `asGroupBy without arguments throws IllegalArgumentException when there is more than one frame column`() {
        val df1 = dataFrameOf("name")("Alice")
        val frameCol = DataColumn.createFrameColumn("d1", listOf(df1))
        shouldThrow<IllegalArgumentException> {
            dataFrameOf("d1" to frameCol, "d2" to frameCol).asGroupBy()
        }
    }

    @Test
    fun `asGroupBy without arguments throws NoSuchElementException when there is no frame column`() {
        shouldThrow<NoSuchElementException> {
            dataFrameOf("a")(1).asGroupBy()
        }
    }

    @Test
    fun `asGroupBy by selector places the frame column last in the GroupBy`() {
        val df1 = dataFrameOf("name")("Alice")
        val df2 = dataFrameOf("name")("Bob")
        val dataCol by columnOf(df1, df2)
        val keyCol by columnOf(1, 2)
        val df = dataFrameOf(dataCol, keyCol)

        val groupBy = df.asGroupBy { dataCol }
        // GroupBy exposes no public accessor for the column order of its internal DataFrame,
        // so the "placed last" contract is pinned through the internal df
        (groupBy as GroupByImpl<*, *>).df.columnNames() shouldBe listOf("keyCol", "dataCol")
    }

    // endregion

    // region DataRow / Map conversions

    @Test
    fun `DataRow toDataFrame returns a single-row DataFrame`() {
        val df = dataFrameOf("a", "b")(1, "x", 2, "y")
        val single = df[1].toDataFrame()
        single.rowsCount() shouldBe 1
        single["a"].toList() shouldBe listOf(2)
    }

    @Test
    fun `DataRow toMap maps column names to the row values`() {
        val df = dataFrameOf("a", "b")(1, "x", 2, "y")
        df[1].toMap() shouldBe mapOf("a" to 2, "b" to "y")
    }

    @Test
    fun `Map toDataRow reverses DataRow toMap`() {
        val df = dataFrameOf("a", "b")(1, "x", 2, "y")
        val row = df[1].toMap().toDataRow()
        row["a"] shouldBe 2
        row["b"] shouldBe "y"
    }

    // endregion

    // region NullabilityOptions

    @Test
    fun `applyNullability throws NullabilityException in Checking mode when the data has nulls`() {
        shouldThrow<NullabilityException> {
            NullabilityOptions.Checking.applyNullability(listOf(1, null), expectedNulls = false)
        }
    }

    @Test
    fun `applyNullability computes the nullability according to the option`() {
        NullabilityOptions.Infer.applyNullability(listOf(1, null), expectedNulls = false) shouldBe true
        NullabilityOptions.Infer.applyNullability(listOf(1, 2), expectedNulls = false) shouldBe false
        NullabilityOptions.Widening.applyNullability(listOf(1, null), expectedNulls = false) shouldBe true
    }

    // endregion
}
