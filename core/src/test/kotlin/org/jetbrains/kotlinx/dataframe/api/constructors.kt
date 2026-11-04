package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion.InferWithUpperbound
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.impl.api.WrongNumberOfValuesException
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test
import kotlin.reflect.typeOf

private class PersonWithoutSchema(val name: String, val age: Int)

@Suppress("ktlint:standard:argument-list-wrapping")
class ConstructorsTests {

    @Test
    fun `columnOf correct types`() {
        val intCol = columnOf(1, 2, 3)
        intCol.type shouldBe typeOf<Int>()

        val stringCol = columnOf("a", "b", "c")
        stringCol.type shouldBe typeOf<String>()

        val nullableIntCol = columnOf(1, 2, null)
        nullableIntCol.type shouldBe typeOf<Int?>()

        val doubleCol = columnOf(1.0, 2.5, 3.7)
        doubleCol.type shouldBe typeOf<Double>()

        val booleanCol = columnOf(true, false, true)
        booleanCol.type shouldBe typeOf<Boolean>()

        val arrayIntCol = columnOf(arrayOf(1))
        arrayIntCol.type shouldBe typeOf<Array<Int>>()

        val intArrayCol = columnOf(intArrayOf(1))
        intArrayCol.type shouldBe typeOf<IntArray>()

        val nullableArrayIntCol = columnOf(arrayOf(1, null))
        nullableArrayIntCol.type shouldBe typeOf<Array<Int?>>()
    }

    @Test
    fun `untitled column naming`() {
        val builder = DynamicDataFrameBuilder()
        repeat(5) {
            builder.add(columnOf(1, 2, 3))
        }
        builder.toDataFrame() shouldBe dataFrameOf(List(5) { columnOf(1, 2, 3) })
    }

    @Test
    fun `duplicated name`() {
        val builder = DynamicDataFrameBuilder()
        val columnName = "columnName"
        val columnA = columnOf(1, 2, 3) named columnName
        val columnB = columnOf(4, 5, 6) named columnName
        builder.add(columnA)
        builder.add(columnB)
        val df = builder.toDataFrame()
        df.columnsCount() shouldBe 2
        df.columnNames() shouldBe listOf(columnName, "${columnName}1")
    }

    @Test
    fun `get by new name`() {
        val builder = DynamicDataFrameBuilder()
        val columnName = "columnName"
        val columnA = columnOf(1, 2, 3) named columnName
        val columnB = columnOf(4, 5, 6) named columnName
        builder.add(columnA)
        val newName = builder.add(columnB)
        builder.get(newName)!!.values shouldBe columnB.values
    }

    @Test
    fun `duplicated column`() {
        val builder = DynamicDataFrameBuilder()
        val columnName = "columnName"
        val columnA = columnOf(1, 2, 3) named columnName
        val columnB = columnOf(4, 5, 6) named columnName
        builder.add(columnA)
        builder.add(columnB)
        builder.add(columnA)
        val df = builder.toDataFrame()
        df.columnsCount() shouldBe 2
        df.columnNames() shouldBe listOf(columnName, "${columnName}1")
    }

    @Test
    fun `builder without duplicate check adds identical column under new name`() {
        val builder = DynamicDataFrameBuilder(checkDuplicateValues = false)
        builder.add(columnOf(1, 2) named "a")
        val second = builder.add(columnOf(1, 2) named "a")
        val df = builder.toDataFrame()

        second shouldBe "a1"
        df.columnNames() shouldBe listOf("a", "a1")
        df["a"].values shouldBe listOf(1, 2)
        df["a1"].values shouldBe listOf(1, 2)
    }

    @Test
    fun `add from values infers type`() {
        val builder = DynamicDataFrameBuilder()

        val mixed = builder.add(listOf<Number>(1, 2.5), "mixed")
        builder.get(mixed)!!.type shouldBe typeOf<Number>()

        val strs = builder.add(listOf("x", "y"), "strs")
        builder.get(strs)!!.type shouldBe typeOf<String>()

        val unnamed = builder.add(listOf(1))
        unnamed shouldBe "untitled"
    }

    @Test
    fun `dataFrameOf with nothing columns`() {
        dataFrameOf("a" to emptyList())["a"].type shouldBe nothingType(false)
        dataFrameOf("a" to listOf(null))["a"].type shouldBe nothingType(true)
    }

    // region createColumn

    @Test
    fun `guess column group from rows`() {
        val row = dataFrameOf("a", "b")(1, 2).single()
        val col = createColumnGuessingType(
            values = listOf(row, DataRow.empty),
            suggestedType = InferWithUpperbound(typeOf<AnyRow>()),
        )
        col shouldBe columnOf(row, DataRow.empty)

        col.hasNulls() shouldBe false
        col.type() shouldBe typeOf<AnyRow>()
        col.kind() shouldBe ColumnKind.Group
        col[0] shouldBe row
        @Suppress("DEPRECATION_ERROR")
        col[1].isEmpty() shouldBe true
    }

    @Test
    fun `guess column group from rows with null`() {
        val row = dataFrameOf("a", "b")(1, 2).single()
        val col = createColumnGuessingType(
            values = listOf(row, DataRow.empty, null),
            suggestedType = InferWithUpperbound(typeOf<AnyRow?>()),
        )
        col shouldBe columnOf(row, DataRow.empty, null)

        col.hasNulls() shouldBe false
        col.type() shouldBe typeOf<AnyRow>()
        col.kind() shouldBe ColumnKind.Group
        col[0] shouldBe row
        @Suppress("DEPRECATION_ERROR")
        col[1]!!.isEmpty() shouldBe true
        @Suppress("DEPRECATION_ERROR")
        col[2]!!.isEmpty() shouldBe true
    }

    @Test
    fun `guess column group from columns`() {
        val col1 = columnOf(1, 2)
        val col2 = columnOf("a", "b")
        val col = createColumnGuessingType(
            values = listOf(col1, col2),
            suggestedType = InferWithUpperbound(typeOf<AnyCol>()),
            allColsMakesColGroup = true,
        )
        col shouldBe columnOf(col1, col2)

        col as ColumnGroup<*>

        col.hasNulls() shouldBe false
        col.type() shouldBe typeOf<AnyRow>()
        col.kind() shouldBe ColumnKind.Group
        col.getColumn(0).values shouldBe col1.values
        col.getColumn(1).values shouldBe col2.values
    }

    @Test
    fun `guess value column from columns and null`() {
        val col1 = columnOf(1, 2)
        val col2 = columnOf("a", "b")
        val col = createColumnGuessingType(
            values = listOf(col1, col2, null),
            suggestedType = InferWithUpperbound(typeOf<AnyCol?>()),
        )
        col.values shouldBe columnOf(col1, col2, null).values

        col.hasNulls() shouldBe true
        col.type() shouldBe typeOf<DataColumn<*>?>() // becomes a column with value columns and nulls
        col.kind() shouldBe ColumnKind.Value
        col[0] shouldBe col1
        col[1] shouldBe col2
        col[2] shouldBe null
    }

    @Test
    fun `guess frame column from dataframes and null`() {
        val df1 = dataFrameOf("a", "b")(1, 2)
        val df2 = dataFrameOf("a", "b")(3, 4)
        val col = createColumnGuessingType(
            values = listOf(df1, df2, null),
            suggestedType = InferWithUpperbound(typeOf<AnyCol?>()),
        )
        col.values shouldBe columnOf(df1, df2, null).values

        col.hasNulls() shouldBe false
        col.type() shouldBe typeOf<AnyFrame>() // becomes frame column, making nulls empty dataframes
        col.kind() shouldBe ColumnKind.Frame
        col[0] shouldBe df1
        col[1] shouldBe df2
        col[2] shouldBe DataFrame.empty()
    }

    @Test
    fun `guess value column from nulls`() {
        val col = createColumnGuessingType(
            values = listOf(null, null),
            suggestedType = InferWithUpperbound(nothingType(true)),
        )
        col.values shouldBe columnOf<Any?>(null, null).values

        col.hasNulls() shouldBe true
        col.type() shouldBe nothingType(true)
        col.kind() shouldBe ColumnKind.Value
        col[0] shouldBe null
        col[1] shouldBe null
    }

    @Test
    fun `column of empty iterable keeps declared type`() {
        val col = column(emptyList<Int>())

        col.type shouldBe typeOf<Int>()
        col.kind() shouldBe ColumnKind.Value
        col.size shouldBe 0
        col.name() shouldBe ""
    }

    @Test
    fun `columnOf of mixed values has the upper bound type`() {
        val col = columnOf<Number>(1, 2.5)

        col.type shouldBe typeOf<Number>()
        col.values shouldBe listOf(1, 2.5)
    }

    @Test
    fun `columnOf infers a narrower type from homogeneous values`() {
        val col = columnOf<Number>(1, 2)

        col.type shouldBe typeOf<Int>()
        col.values shouldBe listOf(1, 2)
    }

    @Test
    fun `columnOf of lists stays value column`() {
        val col = columnOf(listOf(1, 2), listOf(3, 4))

        col.type shouldBe typeOf<List<Int>>()
        col.kind() shouldBe ColumnKind.Value
        col.values shouldBe listOf(listOf(1, 2), listOf(3, 4))
    }

    @Test
    fun `columnOf widens type when values include a collection`() {
        val col = columnOf(1, null, listOf(1, 2, 3))

        col.type shouldBe typeOf<Any?>()
        col.kind() shouldBe ColumnKind.Value
        col.values shouldBe listOf(1, null, listOf(1, 2, 3))
    }

    @Test
    fun `column of iterable uses declared type without inference`() {
        val col = column(listOf<Number>(1, 2))

        col.type shouldBe typeOf<Number>()
        col.values shouldBe listOf(1, 2)
    }

    @Test
    fun `column of rows creates column group`() {
        val df = dataFrameOf("a")(1, 2)
        val col = column(listOf(df[0], df[1]))

        col.kind() shouldBe ColumnKind.Group
        col.type shouldBe typeOf<DataRow<*>>()
        col.name() shouldBe ""
    }

    @Test
    fun `columnOf of columns creates column group`() {
        val col = columnOf(columnOf(1, 2), columnOf(3, 4))

        col.kind() shouldBe ColumnKind.Group
        col.type shouldBe typeOf<DataRow<*>>()
        col.name() shouldBe ""
    }

    @Test
    fun `columnOf of frames creates frame column`() {
        val col = columnOf(dataFrameOf("a")(1, 2), dataFrameOf("b")(3, 4))

        col.kind() shouldBe ColumnKind.Frame
        col.type shouldBe typeOf<DataFrame<*>>()
    }

    @Test
    fun `columnOf of frames with null keeps empty dataframe`() {
        val col = columnOf(dataFrameOf("a")(1, 2), null)

        col.kind() shouldBe ColumnKind.Frame
        col.values.map { v -> v?.let { (it as DataFrame<*>).rowsCount() } } shouldBe listOf(2, 0)
    }

    @Test
    fun `KDoc example columnOf of name to column pairs`() {
        val columnGroup = columnOf(
            "a" to columnOf("a1", "a2", "a3"),
            "b" to columnOf(1, 2, 3),
        )

        columnGroup.kind() shouldBe ColumnKind.Group
        columnGroup.getColumn(0).values shouldBe listOf("a1", "a2", "a3")
        columnGroup.getColumn(1).values shouldBe listOf(1, 2, 3)
    }

    // endregion

    // region dataFrameOf

    @Test
    fun `dataFrameOf withColumns`() {
        val df = dataFrameOf("value", "value2", "frameCol").withColumns {
            when (it) {
                "value" -> columnOf(1, 2, 3, null)

                "value2" -> columnOf(
                    columnOf(1, 2),
                    columnOf(3, 4),
                    columnOf(5, null),
                    null,
                )

                "frameCol" -> columnOf(
                    dataFrameOf("a", "b")(1, 2),
                    dataFrameOf("a", "b")(3, 4),
                    dataFrameOf("a", "b")(5, null),
                    null,
                )

                else -> error("Unexpected column name: $it")
            }
        }

        df["value"].type shouldBe typeOf<Int?>()
        df["value"].kind() shouldBe ColumnKind.Value

        df["value2"].type shouldBe typeOf<DataColumn<Int?>?>()
        df["value2"].kind() shouldBe ColumnKind.Value

        df["frameCol"].type shouldBe typeOf<DataFrame<*>>()
        df["frameCol"].kind() shouldBe ColumnKind.Frame
        df["frameCol"].last() shouldBe DataFrame.empty()
    }

    @Test
    fun `dataFrameOf invoke`() {
        val df1 = dataFrameOf("value", "value2", "frameCol") {
            when (it) {
                "value" -> listOf(1, 2, 3, null)

                "value2" -> listOf(
                    columnOf(1, 2),
                    columnOf(3, 4),
                    columnOf(5, null),
                    null,
                )

                "frameCol" -> listOf(
                    dataFrameOf("a", "b")(1, 2),
                    dataFrameOf("a", "b")(3, 4),
                    dataFrameOf("a", "b")(5, null),
                    null,
                )

                else -> error("Unexpected column name: $it")
            }
        }

        val df2 = dataFrameOf("value", "value2", "frameCol").invoke {
            when (it) {
                "value" -> listOf(1, 2, 3, null)

                "value2" -> listOf(columnOf(1, 2), columnOf(3, 4), columnOf(5, null), null)

                "frameCol" -> listOf(
                    dataFrameOf("a", "b")(1, 2),
                    dataFrameOf("a", "b")(3, 4),
                    dataFrameOf("a", "b")(5, null),
                    null,
                )

                else -> error("Unexpected column name: $it")
            }
        }

        val names = listOf("value", "value2", "frameCol")
        val df3 = dataFrameOf(listOf(1, 2, 3)) {
            when (it) {
                1 -> listOf(1, 2, 3, null)

                2 -> listOf(columnOf(1, 2), columnOf(3, 4), columnOf(5, null), null)

                3 -> listOf(
                    dataFrameOf("a", "b")(1, 2),
                    dataFrameOf("a", "b")(3, 4),
                    dataFrameOf("a", "b")(5, null),
                    null,
                )

                else -> error("Unexpected column name: $it")
            }
        }.rename { all() }.to { names[it.name.toInt() - 1] }

        val df4 = dataFrameOf(names).invoke {
            when (it) {
                "value" -> listOf(1, 2, 3, null)

                "value2" -> listOf(columnOf(1, 2), columnOf(3, 4), columnOf(5, null), null)

                "frameCol" -> listOf(
                    dataFrameOf("a", "b")(1, 2),
                    dataFrameOf("a", "b")(3, 4),
                    dataFrameOf("a", "b")(5, null),
                    null,
                )

                else -> error("Unexpected column name: $it")
            }
        }

        df1 shouldBe df2
        df2 shouldBe df3
        df3 shouldBe df4

        df1["value"].type shouldBe typeOf<Int?>()
        df1["value"].kind() shouldBe ColumnKind.Value

        df1["value2"].type shouldBe typeOf<DataColumn<*>?>()
        df1["value2"].kind() shouldBe ColumnKind.Value

        df1["frameCol"].type shouldBe typeOf<DataFrame<*>>()
        df1["frameCol"].kind() shouldBe ColumnKind.Frame
        df1["frameCol"].last() shouldBe DataFrame.empty()
    }

    @Test
    fun `dataFrameOf fill`() {
        val df1 = dataFrameOf("a", "b").fill(2, "lol")

        df1["a"].values shouldBe listOf("lol", "lol")
        df1["a"].kind() shouldBe ColumnKind.Value
        df1["b"].values shouldBe listOf("lol", "lol")
        df1["b"].kind() shouldBe ColumnKind.Value

        val df2 = dataFrameOf("a", "b").fill(2, dataFrameOf("a", "b")(1, 2))
        df2["a"].type() shouldBe typeOf<DataFrame<*>>()
        df2["a"].kind() shouldBe ColumnKind.Frame
        df2["b"].type() shouldBe typeOf<DataFrame<*>>()
        df2["b"].kind() shouldBe ColumnKind.Frame

        val df3 = dataFrameOf("a", "b").fill(2) { it }
        df3["a"].values shouldBe listOf(0, 1)
        df3["a"].kind() shouldBe ColumnKind.Value
        df3["b"].values shouldBe listOf(0, 1)
        df3["b"].kind() shouldBe ColumnKind.Value

        val df4 = dataFrameOf("a", "b").fill(2) { dataFrameOf("a", "b")(1, 2) }
        df4["a"].type() shouldBe typeOf<DataFrame<*>>()
        df4["a"].kind() shouldBe ColumnKind.Frame
        df4["b"].type() shouldBe typeOf<DataFrame<*>>()
        df4["b"].kind() shouldBe ColumnKind.Frame

        val a = listOf(1, 2)
        val b = listOf(dataFrameOf("a", "b")(1, 2), null)
        val df5 = dataFrameOf("a", "b").fillIndexed(2) { it, colName ->
            when (colName) {
                "a" -> a[it]
                "b" -> b[it]
                else -> error("Unexpected column name: $colName")
            }
        }
        df5["a"].values shouldBe a
        df5["a"].kind() shouldBe ColumnKind.Value
        df5["b"].values shouldBe listOf(b[0], DataFrame.empty())
        df5["b"].kind() shouldBe ColumnKind.Frame
    }

    @Test
    fun `dataFrameOf throws on duplicate column names`() {
        shouldThrow<DuplicateColumnNamesException> {
            dataFrameOf(listOf(columnOf(1) named "a", columnOf(2) named "a"))
        }
    }

    @Test
    fun `dataFrameOf throws on unequal column sizes`() {
        shouldThrow<UnequalColumnSizesException> {
            dataFrameOf(columnOf(1, 2) named "a", columnOf(3) named "b")
        }
    }

    @Test
    fun `dataFrameOf of empty column list is empty`() {
        val cols: List<BaseColumn<*>> = emptyList()
        val df = dataFrameOf(cols)

        df.rowsCount() shouldBe 0
        df.columnsCount() shouldBe 0
    }

    @Test
    fun `unnamed columns get unique untitled names`() {
        val df = dataFrameOf(columnOf(1), columnOf(2))

        df.columnNames() shouldBe listOf("untitled", "untitled1")
        df.rowsCount() shouldBe 1
    }

    @Test
    fun `unnamed column does not clash with literal untitled`() {
        val unnamedFirst = dataFrameOf(columnOf(1), columnOf(2) named "untitled")
        unnamedFirst.columnNames() shouldBe listOf("untitled1", "untitled")

        val literalFirst = dataFrameOf(columnOf(1) named "untitled", columnOf(2))
        literalFirst.columnNames() shouldBe listOf("untitled", "untitled1")
    }

    @Test
    fun `dataFrameOf invoke rejects indivisible values`() {
        val e = shouldThrow<WrongNumberOfValuesException> {
            dataFrameOf("a", "b")(1, 2, 3)
        }
        e.message shouldBe "Number of values 3 is not divisible by number of columns 2"
    }

    @Test
    fun `dataFrameOf with empty header throws even without values`() {
        @Suppress("DEPRECATION")
        val byDeprecated = shouldThrow<WrongNumberOfValuesException> {
            dataFrameOf(emptyList<String>(), emptyList())
        }
        byDeprecated.message shouldBe "Number of values 0 is not divisible by number of columns 0"

        val byModern = shouldThrow<WrongNumberOfValuesException> {
            dataFrameOf(emptyList<String>()).withValues(emptyList())
        }
        byModern.message shouldBe "Number of values 0 is not divisible by number of columns 0"
    }

    @Test
    fun `dataFrameOf with one column and no values creates empty frame`() {
        @Suppress("DEPRECATION")
        val df = dataFrameOf(listOf("a"), emptyList())

        df.rowsCount() shouldBe 0
        df.columnsCount() shouldBe 1
        df["a"].type shouldBe nothingType(false)
    }

    @Test
    fun `dataFrameOf invoke fills values row by row`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)

        df["a"].values shouldBe listOf(1, 3)
        df["b"].values shouldBe listOf(2, 4)
    }

    @Test
    fun `dataFrameOf of pairs of lists infers unified number type`() {
        val mixed = dataFrameOf("a" to listOf(1, 2.5))
        mixed["a"].type shouldBe typeOf<Number>()

        val uniform = dataFrameOf("a" to listOf(1, 2))
        uniform["a"].type shouldBe typeOf<Int>()
    }

    @Test
    fun `dataFrameOf of pairs throws on duplicate names`() {
        shouldThrow<DuplicateColumnNamesException> {
            dataFrameOf("a" to listOf(1), "a" to listOf(2))
        }
    }

    @Test
    fun `dataFrameOf of pairs throws on unequal list sizes`() {
        shouldThrow<UnequalColumnSizesException> {
            dataFrameOf("a" to listOf(1, 2), "b" to listOf(3))
        }
    }

    @Test
    fun `dataFrameOf of non-string header stringifies names`() {
        val df = dataFrameOf(listOf(1, 2), fill = { listOf(it) })

        df.columnNames() shouldBe listOf("1", "2")
        df.rowsCount() shouldBe 1
        df["1"].values shouldBe listOf(1)
        df["2"].values shouldBe listOf(2)
    }

    @Test
    fun `dataFrameOf of CharProgression builds header from chars`() {
        val df = dataFrameOf('a'..'c')(1, 2, 3, 4, 5, 6)

        df.columnNames() shouldBe listOf("a", "b", "c")
        df["a"].values shouldBe listOf(1, 4)
        df["b"].values shouldBe listOf(2, 5)
        df["c"].values shouldBe listOf(3, 6)
    }

    @Test
    fun `dataFrameOf of column references takes their names`() {
        val df = dataFrameOf(column<String>("a"), column<String>("b")).fill(2, 0)

        df.columnNames() shouldBe listOf("a", "b")
        df["a"].values shouldBe listOf(0, 0)
    }

    @Test
    fun `deprecated dataFrameOf of header and values fills row by row`() {
        @Suppress("DEPRECATION")
        val df = dataFrameOf(listOf("a", "b"), listOf(1, 2, 3, 4))

        df["a"].values shouldBe listOf(1, 3)
        df["b"].values shouldBe listOf(2, 4)
    }

    @Test
    fun `KDoc example dataFrameOf of name to column pairs`() {
        val df = dataFrameOf(
            "a" to columnOf(1, 2),
            "b" to columnOf(1.0, 2.0),
            "group" to columnOf(
                "nestedA" to columnOf("42", "abc"),
            ),
        )

        df.columnNames() shouldBe listOf("a", "b", "group")
        df["a"].values shouldBe listOf(1, 2)
        df["b"].values shouldBe listOf(1.0, 2.0)
        df["group"].kind() shouldBe ColumnKind.Group
        df["group"]["nestedA"].values shouldBe listOf("42", "abc")
    }

    @Test
    fun `builder invoke requires equal column count`() {
        val e = shouldThrow<IllegalArgumentException> {
            dataFrameOf("a", "b").invoke(listOf(columnOf(1)))
        }
        e.message shouldBe "Number of columns differs from number of column names"
    }

    @Test
    fun `builder invoke renames columns to header`() {
        val df = dataFrameOf("x")(columnOf(1, 2, 3))

        df.columnNames() shouldBe listOf("x")
        df["x"].values shouldBe listOf(1, 2, 3)
    }

    @Test
    fun `builder fill with null creates nullable nothing column`() {
        val df = dataFrameOf("x").fill(2, null)

        df["x"].type shouldBe typeOf<Nothing?>()
        df["x"].values shouldBe listOf(null, null)
    }

    @Test
    fun `builder nulls creates nullable typed column`() {
        val df = dataFrameOf("x").nulls<Int>(3)

        df["x"].type shouldBe typeOf<Int?>()
        df["x"].values shouldBe listOf(null, null, null)
    }

    @Test
    fun `builder random columns have declared types and stay in range`() {
        val ints = dataFrameOf("i").randomInt(5, 1..3)
        ints["i"].type shouldBe typeOf<Int>()
        ints.rowsCount() shouldBe 5
        ints["i"].values.all { (it as Int) in 1..3 } shouldBe true

        val doubles = dataFrameOf("d").randomDouble(2, 1.0..2.0)
        doubles["d"].type shouldBe typeOf<Double>()
        doubles["d"].values.all { (it as Double) in 1.0..2.0 } shouldBe true

        val floats = dataFrameOf("f").randomFloat(2)
        floats["f"].type shouldBe typeOf<Float>()

        val longs = dataFrameOf("l").randomLong(2, 1L..3L)
        longs["l"].type shouldBe typeOf<Long>()
        longs["l"].values.all { (it as Long) in 1L..3L } shouldBe true

        val bools = dataFrameOf("b").randomBoolean(2)
        bools["b"].type shouldBe typeOf<Boolean>()

        val noRangeDoubles = dataFrameOf("d").randomDouble(2)
        noRangeDoubles["d"].type shouldBe typeOf<Double>()

        val noRangeLongs = dataFrameOf("l").randomLong(2)
        noRangeLongs["l"].type shouldBe typeOf<Long>()

        // the upper bound of randomInt ranges is included, of randomLong/randomDouble — not
        val boundaryInts = dataFrameOf("i").randomInt(100, 1..2)
        boundaryInts["i"].values.all { (it as Int) in 1..2 } shouldBe true
        boundaryInts["i"].values.any { it == 2 } shouldBe true

        val boundaryLongs = dataFrameOf("l").randomLong(100, 1L..2L)
        boundaryLongs["l"].values.all { (it as Long) == 1L } shouldBe true

        val boundaryDoubles = dataFrameOf("d").randomDouble(100, 1.0..2.0)
        boundaryDoubles["d"].values.all { (it as Double) < 2.0 } shouldBe true
    }

    @Test
    fun `builder randomInt on empty range throws`() {
        shouldThrow<IllegalArgumentException> {
            dataFrameOf("x").randomInt(3, IntRange.EMPTY)
        }
    }

    @Test
    fun `builder invoke with sequence fills row by row`() {
        val df = dataFrameOf("a", "b").invoke(sequenceOf(1, 2, 3, 4))

        df["a"].values shouldBe listOf(1, 3)
        df["b"].values shouldBe listOf(2, 4)
    }

    @Test
    fun `builder invoke infers each column type from its values`() {
        val df = dataFrameOf("a", "b")(1, "x")

        df["a"].type shouldBe typeOf<Int>()
        df["a"].values shouldBe listOf(1)
        df["b"].type shouldBe typeOf<String>()
        df["b"].values shouldBe listOf("x")
    }

    // endregion

    @Suppress("ktlint:standard:argument-list-wrapping")
    @Test
    fun `dataFrameOf with local class`() {
        // issue #928
        data class Car(val type: String, val model: String)

        val cars: DataFrame<*> = dataFrameOf("owner", "car")(
            "Max", Car("audi", "a8"),
            "Tom", Car("toyota", "corolla"),
        )

        cars["car"].type shouldBe typeOf<Car>()

        val unfolded = cars.unfold("car")
        unfolded["car"]["type"].type shouldBe typeOf<String>()
        unfolded["car"]["model"].type shouldBe typeOf<String>()

        val cars2 = listOf(
            Car("audi", "a8"),
            Car("toyota", "corolla"),
        ).toDataFrame()

        cars2["type"].type shouldBe typeOf<String>()
        cars2["model"].type shouldBe typeOf<String>()
    }

    // region emptyDataFrame

    @Test
    fun `emptyDataFrame of Unit has no columns`() {
        val df = emptyDataFrame<Unit>()

        df.rowsCount() shouldBe 0
        df.columnsCount() shouldBe 0
    }

    @Test
    fun `emptyDataFrame of class without DataSchema creates columns from properties`() {
        val df = emptyDataFrame<PersonWithoutSchema>()

        df.rowsCount() shouldBe 0
        df.columnNames() shouldBe listOf("name", "age")
        df["name"].type shouldBe typeOf<String>()
        df["age"].type shouldBe typeOf<Int>()
    }

    @Test
    fun `emptyDataFrame differs from DataFrame empty`() {
        DataFrame.Empty.rowsCount() shouldBe 0
        DataFrame.Empty.columnsCount() shouldBe 0

        val emptyRows = DataFrame.empty(2)
        emptyRows.rowsCount() shouldBe 2
        emptyRows.columnsCount() shouldBe 0
    }

    // endregion

    // region create ColumnPath

    @Test
    fun `pathOf without names is empty path`() {
        val path = pathOf()

        path.size shouldBe 0
        path shouldBe ColumnPath.EMPTY
        path.toString() shouldBe "[]"
    }

    @Test
    fun `pathOf with names keeps order and name is the last segment`() {
        val path = pathOf("a", "b")

        path.size shouldBe 2
        path.name() shouldBe "b"
        path.toString() shouldBe "[a, b]"
    }

    @Test
    fun `empty path has no name`() {
        shouldThrow<NoSuchElementException> {
            ColumnPath.EMPTY.name()
        }
    }

    // endregion
}
