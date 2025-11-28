package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.api.ComparisonDescription
import org.jetbrains.kotlinx.dataframe.impl.api.compareDataFramesImpl
import org.jetbrains.kotlinx.dataframe.impl.api.myersDifferenceAlgorithmImpl
import org.junit.Test
import kotlin.Pair

@DataSchema
internal class SchemaForThisTest(val integer: Int, val string: String) : DataRowSchema

class CompareDataFramesTest {

    // compareDataFrames region

    @Test
    fun `Need both to delete and insert rows, preserving some rows`() {
        val dfA = dataFrameOf(
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(2, "c"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForThisTest(2, "c"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(2, "c"),
        )
        val comparison = compareDataFramesImpl(dfA, dfB)
        comparison shouldBe dataFrameOf(
            ComparisonDescription(0, "dfA", true, null, null),
            ComparisonDescription(1, "dfA", true, null, null),
            ComparisonDescription(1, "dfB", null, true, 2),
            ComparisonDescription(5, "dfA", true, null, null),
            ComparisonDescription(5, "dfB", null, true, 6),
        )
    }

    @Test
    fun `need to do nothing`() {
        val dfA = dataFrameOf(
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
        )
        val comparison = compareDataFramesImpl(dfA, dfB)
        comparison shouldBe emptyDataFrame()
    }

    @Test
    fun `need to remove each row of dfA and insert each row of dfB`() {
        val dfA = dataFrameOf(
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(2, "c"),
        )
        val dfB = dataFrameOf(
            SchemaForThisTest(3, "d"),
            SchemaForThisTest(4, "e"),
            SchemaForThisTest(5, "f"),
        )
        val comparison = compareDataFramesImpl(dfA, dfB)
        comparison shouldBe dataFrameOf(
            ComparisonDescription(0, "dfA", true, null, null),
            ComparisonDescription(1, "dfA", true, null, null),
            ComparisonDescription(2, "dfA", true, null, null),
            ComparisonDescription(0, "dfB", null, true, 2),
            ComparisonDescription(1, "dfB", null, true, 2),
            ComparisonDescription(2, "dfB", null, true, 2),
        )
    }

    // end region

    // Myers algorithm region

    @Test
    fun `Need both to delete and insert rows, preserving some rows, Myers algorithm`() {
        val dfA = dataFrameOf(
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(2, "c"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForThisTest(2, "c"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(2, "c"),
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
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
        )
        val dfB = dataFrameOf(
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(0, "a"),
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
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(2, "c"),
        )
        val dfB = dataFrameOf(
            SchemaForThisTest(3, "d"),
            SchemaForThisTest(4, "e"),
            SchemaForThisTest(5, "f"),
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
        val dfA = emptyDataFrame<SchemaForThisTest>()
        val dfB = dataFrameOf(
            SchemaForThisTest(0, "a"),
            SchemaForThisTest(1, "b"),
            SchemaForThisTest(2, "c"),
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
