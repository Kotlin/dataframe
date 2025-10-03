package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion.InferWithUpperbound
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test
import kotlin.reflect.typeOf

class ConstructorsTests {

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
        }.rename { all() }.into { names[it.name.toInt() - 1] }

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
}
