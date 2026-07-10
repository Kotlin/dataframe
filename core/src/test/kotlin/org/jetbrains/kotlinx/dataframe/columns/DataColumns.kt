package org.jetbrains.kotlinx.dataframe.columns

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.core.BuildConfig
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person
import org.jetbrains.kotlinx.dataframe.testSets.person.BaseTest
import org.junit.Test
import java.net.URI
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

class DataColumns : BaseTest() {
    @Test
    fun `create column with platform type from Api`() {
        val df1 = listOf(1, 2, 3).toDataFrame {
            expr { URI.create("http://example.com") } into "text"
        }
        df1["text"].type().toString() shouldBe "java.net.URI"
    }

    @Test
    fun `create column with nullable platform type from Api`() {
        val df1 = listOf(1, 2, 3).toDataFrame {
            expr { i -> URI.create("http://example.com").takeIf { i == 2 } } into "text"
        }
        df1["text"].type().toString() shouldBe "java.net.URI?"
    }

    @Test
    fun `create column with nullable platform type from factory method`() {
        val col = listOf(URI.create("http://example.com"), null).toColumn("a")
        col.type().toString() shouldBe "java.net.URI?"
    }

    @Test
    fun `allow no nulls in frame columns`() {
        if (BuildConfig.DEBUG) {
            shouldThrow<IllegalArgumentException> {
                DataColumn.createFrameColumn(
                    name = "",
                    groups = listOf(dataFrameOf("a")(1), null) as List<AnyFrame>,
                )
            }
        }

        DataColumn.createByType(
            name = "",
            values = listOf(dataFrameOf("a")(1), null),
        ).kind() shouldBe ColumnKind.Value
    }

    @Test
    fun `allow no non-null dataframe value columns`() {
        // ordinarily this is a type-check only, in DEBUG mode, it's an instance check
        shouldThrow<IllegalArgumentException> {
            DataColumn.createValueColumn(
                name = "",
                values = listOf(dataFrameOf("a")(1), dataFrameOf("a")(2)),
            )
        }
    }

    // Tests for issue #1926
    val nullableDfValueColumnDf = dataFrameOf(
        DataColumn.createValueColumn("col", listOf(df, null)),
    )

    @Test
    fun `checking nullableDfValueColumnDf creation`() {
        nullableDfValueColumnDf["col"].let {
            it.kind() shouldBe ColumnKind.Value
            it.type().isSubtypeOf(typeOf<DataFrame<*>?>()) shouldBe true
        }
    }

    @Test
    fun `test removing nulls update from nullable DF ValueCol should turn into FrameCol`() {
        val updatedDf = nullableDfValueColumnDf
            .update("col").where { it == null }.with { emptyDataFrame<Person>() }
        updatedDf["col"].let {
            it.kind() shouldBe ColumnKind.Frame
            it.type().isSubtypeOf(typeOf<DataFrame<*>>()) shouldBe true
        }
    }

    @Test
    fun `test dropNulls from nullable DF ValueCol should turn into FrameCol`() {
        shouldNotThrowAny {
            nullableDfValueColumnDf["col"].dropNulls()
        }
        val droppedDf = nullableDfValueColumnDf.dropNulls("col")
        droppedDf["col"].let {
            it.kind() shouldBe ColumnKind.Frame
            it.type().isSubtypeOf(typeOf<DataFrame<*>>()) shouldBe true
        }
    }

    @Test
    fun `test take from nullable DF ValueCol should turn into FrameCol`() {
        shouldNotThrowAny {
            nullableDfValueColumnDf["col"].take(1)
        }
        val takenDf = nullableDfValueColumnDf.take(1)
        takenDf["col"].let {
            it.kind() shouldBe ColumnKind.Frame
            it.type().isSubtypeOf(typeOf<DataFrame<*>>()) shouldBe true
        }
    }
}
