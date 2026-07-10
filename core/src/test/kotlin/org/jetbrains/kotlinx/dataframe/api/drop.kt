package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

class DropTests : ColumnsSelectionDslTests() {

    // Issue #1926
    @Test
    fun `removing nulls from ValueCol of DF should turn into FrameCol`() {
        val df = dataFrameOf(
            DataColumn.createValueColumn("col", listOf(df, null)),
        )
        df["col"].let {
            it.kind() shouldBe ColumnKind.Value
            it.type() shouldBe typeOf<DataFrame<Person>?>()
        }
        val updatedDf = df.update("col").where { it == null }.with { emptyDataFrame<Person>() }
        updatedDf["col"].let {
            // correct
            it.kind() shouldBe ColumnKind.Frame
            it.type().isSubtypeOf(typeOf<DataFrame<*>>()) shouldBe true
        }

        df["col"].dropNulls() // should work correctly
        val droppedDf = df.dropNulls("col")
        droppedDf["col"].let {
            it.kind() shouldBe ColumnKind.Frame
            it.type().isSubtypeOf(typeOf<DataFrame<*>>()) shouldBe true
        }

        val takenDf = droppedDf.take(1)
        takenDf["col"].let {
            it.kind() shouldBe ColumnKind.Frame
            it.type().isSubtypeOf(typeOf<DataFrame<*>>()) shouldBe true
        }
    }

    @Test
    fun `drop and dropLast`() {
        listOf(
            df.select { name.lastName },
            df.select { name.dropCols(1) },
            df.select { "name".dropCols(1) },
            df.select { Person::name.dropCols(1) },
            df.select { pathOf("name").dropCols(1) },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLastCols(1) },
            df.select { "name".dropLastCols(1) },
            df.select { Person::name.dropLastCols(1) },
            df.select { pathOf("name").dropLastCols(1) },
        ).shouldAllBeEqual()
    }

    @Test
    fun `dropWhile and dropLastWhile`() {
        listOf(
            df.select { name.lastName },
            df.select { name.dropColsWhile { it.name == "firstName" } },
            df.select { "name".dropColsWhile { it.name == "firstName" } },
            df.select { Person::name.dropColsWhile { it.name == "firstName" } },
            df.select { pathOf("name").dropColsWhile { it.name == "firstName" } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.dropLastColsWhile { it.name == "lastName" } },
            df.select { "name".dropLastColsWhile { it.name == "lastName" } },
            df.select { Person::name.dropLastColsWhile { it.name == "lastName" } },
            df.select { pathOf("name").dropLastColsWhile { it.name == "lastName" } },
        ).shouldAllBeEqual()
    }
}
