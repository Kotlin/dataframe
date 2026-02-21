package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.api.ComparisonDescription
import org.jetbrains.kotlinx.dataframe.impl.api.DataFrameOfComparison
import org.jetbrains.kotlinx.dataframe.impl.api.RowOfComparison
import org.jetbrains.kotlinx.dataframe.impl.api.compareDataFramesImpl
import org.jetbrains.kotlinx.dataframe.impl.api.myersDifferenceAlgorithmImpl
import org.junit.Test
import kotlin.Pair

@DataSchema
internal class SchemaForCompareDfTest(val integer: Int, val string: String) : DataRowSchema

class CompareDataFramesTest {

    // compareDataFrames region

    @Test
    fun `Need both to delete and insert rows, preserving some rows`() {
        val dfA = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(2, "c"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForCompareDfTest(2, "c"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(2, "c"),
        )
        val comparison = compareDataFramesImpl(dfA, dfB)
        comparison shouldBe dataFrameOf(
            ComparisonDescription(0, DataFrameOfComparison.DFA, RowOfComparison.REMOVED, null, dfA[0]),
            ComparisonDescription(1, DataFrameOfComparison.DFA, RowOfComparison.REMOVED, null, dfA[1]),
            ComparisonDescription(1, DataFrameOfComparison.DFB, RowOfComparison.INSERTED, 2, dfB[1]),
            ComparisonDescription(5, DataFrameOfComparison.DFA, RowOfComparison.REMOVED, null, dfA[5]),
            ComparisonDescription(5, DataFrameOfComparison.DFB, RowOfComparison.INSERTED, 6, dfB[5]),
        )
    }

    @Test
    fun `need to do nothing`() {
        val dfA = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
        )
        val comparison = compareDataFramesImpl(dfA, dfB)
        comparison shouldBe emptyDataFrame()
    }

    @Test
    fun `need to remove each row of dfA and insert each row of dfB`() {
        val dfA = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(2, "c"),
        )
        val dfB = dataFrameOf(
            SchemaForCompareDfTest(3, "d"),
            SchemaForCompareDfTest(4, "e"),
            SchemaForCompareDfTest(5, "f"),
        )
        val comparison = compareDataFramesImpl(dfA, dfB)
        comparison shouldBe dataFrameOf(
            ComparisonDescription(0, DataFrameOfComparison.DFA, RowOfComparison.REMOVED, null, dfA[0]),
            ComparisonDescription(1, DataFrameOfComparison.DFA, RowOfComparison.REMOVED, null, dfA[1]),
            ComparisonDescription(2, DataFrameOfComparison.DFA, RowOfComparison.REMOVED, null, dfA[2]),
            ComparisonDescription(0, DataFrameOfComparison.DFB, RowOfComparison.INSERTED, 2, dfB[0]),
            ComparisonDescription(1, DataFrameOfComparison.DFB, RowOfComparison.INSERTED, 2, dfB[1]),
            ComparisonDescription(2, DataFrameOfComparison.DFB, RowOfComparison.INSERTED, 2, dfB[2]),
        )
    }

    // end region

    // Myers algorithm region

    @Test
    fun `Need both to delete and insert rows, preserving some rows, Myers algorithm`() {
        val dfA = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(2, "c"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForCompareDfTest(2, "c"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(2, "c"),
        )
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 0),
            Pair(2, 0),
            Pair(3, 1),
            Pair(3, 2),
            Pair(4, 3),
            Pair(5, 4),
            Pair(6, 4),
            Pair(7, 5),
            Pair(7, 6),
        )
    }

    @Test
    fun `need to do nothing, Myers algorithm`() {
        val dfA = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(0, "a"),
        )
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 1),
            Pair(2, 2),
            Pair(3, 3),
        )
    }

    @Test
    fun `need to remove each row of dfA and insert each row of dfB, Myers Algorithm`() {
        val dfA = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(2, "c"),
        )
        val dfB = dataFrameOf(
            SchemaForCompareDfTest(3, "d"),
            SchemaForCompareDfTest(4, "e"),
            SchemaForCompareDfTest(5, "f"),
        )
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 0),
            Pair(2, 0),
            Pair(3, 0),
            Pair(3, 1),
            Pair(3, 2),
            Pair(3, 3),
        )
    }

    @Test
    fun `need to add each row, Myers algorithm`() {
        val dfA = emptyDataFrame<SchemaForCompareDfTest>()
        val dfB = dataFrameOf(
            SchemaForCompareDfTest(0, "a"),
            SchemaForCompareDfTest(1, "b"),
            SchemaForCompareDfTest(2, "c"),
        )
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(0, 1),
            Pair(0, 2),
            Pair(0, 3),
        )
    }
}
