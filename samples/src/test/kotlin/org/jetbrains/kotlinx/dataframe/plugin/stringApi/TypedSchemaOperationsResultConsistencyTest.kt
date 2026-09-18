package org.jetbrains.kotlinx.dataframe.plugin.stringApi

import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.byName
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.excludeJoin
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.fillNA
import org.jetbrains.kotlinx.dataframe.api.fillNaNs
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.filterJoin
import org.jetbrains.kotlinx.dataframe.api.flatten
import org.jetbrains.kotlinx.dataframe.api.fullJoin
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.implode
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxFor
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanFor
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minFor
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.moveTo
import org.jetbrains.kotlinx.dataframe.api.moveToEnd
import org.jetbrains.kotlinx.dataframe.api.moveToStart
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileFor
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.reorder
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.stdFor
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toStart
import org.jetbrains.kotlinx.dataframe.api.unfold
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withZero
import org.junit.Test

/**
 * Making sure compiler evaluates String API overloads same as CS DSL overloads on typed input
 * User scenario when dataframe itself is typed, but user chose to use String overload for simplicity
 * => we do not loose information
 * Sometimes some precision is expected to be lost, see [Merge0]
 */
@Suppress("FunctionName", "TestFunctionName")
class TypedSchemaOperationsResultConsistencyTest : CommonTestData() {
    @Test
    fun DataFrameCumSum() {
        df.cumSum("value") matches df.cumSum { value }
    }

    @Test
    fun DataFrameGroupBy() {
        df.groupBy("key").toDataFrame() matches df.groupBy { key }.toDataFrame()
    }

    @Test
    fun DataFrameUnfold() {
        records.unfold("ab") matches records.unfold { ab }
    }

    @Test
    fun Distinct0() {
        df.distinct("key") matches df.distinct { key }
    }

    @Test
    fun DropNa0() {
        df.dropNA("nullable") matches df.dropNA { nullable }
    }

    @Test
    fun DropNulls0() {
        df.dropNulls("nullable") matches df.dropNulls { nullable }
    }

    @Test
    fun ExcludeJoin() {
        df.excludeJoin(other, "key") matches df.excludeJoin(other) { key }
    }

    @Test
    fun Explode0() {
        lists.explode("values") matches lists.explode { values }
    }

    @Test
    fun ExplodeColumns() {
        lists[0].explode("values") matches lists[0].explode { values }
    }

    @Test
    fun FillNaNs0() {
        df.fillNaNs("nan").withZero() matches df.fillNaNs { nan }.withZero()
    }

    @Test
    fun FillNulls0() {
        df.fillNulls("nullable").withZero() matches df.fillNulls { nullable }.withZero()
    }

    @Test
    fun `FillNulls0 fillNulls to fillNA`() {
        df.fillNA("nullable").withZero() matches df.fillNulls { nullable }.withZero()
    }

    @Test
    fun `FillNulls0 fillNA to fillNulls`() {
        df.fillNulls("nullable").withZero() matches df.fillNA { nullable }.withZero()
    }

    @Test
    fun `FillNulls0 fillNA to fillNA`() {
        df.fillNA("nullable").withZero() matches df.fillNA { nullable }.withZero()
    }

    @Test
    fun FilterJoin() {
        df.filterJoin(other, "key") matches df.filterJoin(other) { key }
    }

    @Test
    fun Flatten0() {
        nested.flatten("group") matches nested.flatten { group }
    }

    @Test
    fun FullJoin() {
        df.fullJoin(other, "key") matches df.fullJoin(other) { key }
    }

    @Test
    fun Gather0() {
        df.gather("value").into("name", "result") matches
            df.gather { value }.into("name", "result").convert { result }.with { it as Any? }
    }

    @Test
    fun Group0() {
        df.group("value").into("values") matches df.group { value }.into("values")
    }

    @Test
    fun GroupByCumSum() {
        df.groupBy("key").cumSum("value").toDataFrame() matches df.groupBy { key }.cumSum { value }.toDataFrame()
    }

    @Test
    fun GroupByMax0() {
        df.groupBy("key").maxFor("value") matches df.groupBy { key }.maxFor { value }
    }

    @Test
    fun GroupByMax2() {
        df.groupBy("key").max("value", name = "max") matches df.groupBy { key }.max("max") { value }
    }

    @Test
    fun GroupByMean0() {
        df.groupBy("key").meanFor("value") matches df.groupBy { key }.meanFor { value }
    }

    @Test
    fun GroupByMean2() {
        df.groupBy("key").mean("value", name = "mean") matches df.groupBy { key }.mean("mean") { value }
    }

    @Test
    fun GroupByMedian0() {
        df.groupBy("key").medianFor("value") matches df.groupBy { key }.medianFor { value }
    }

    @Test
    fun GroupByMedian2() {
        df.groupBy("key").median("value", name = "median") matches df.groupBy { key }.median("median") { value }
    }

    @Test
    fun GroupByMin0() {
        df.groupBy("key").minFor("value") matches df.groupBy { key }.minFor { value }
    }

    @Test
    fun GroupByMin2() {
        df.groupBy("key").min("value", name = "min") matches df.groupBy { key }.min("min") { value }
    }

    @Test
    fun GroupByPercentile0() {
        df.groupBy("key").percentileFor(0.5, "value") matches df.groupBy { key }.percentileFor(0.5) { value }
    }

    @Test
    fun GroupByPercentile2() {
        df.groupBy("key").percentile(0.5, "value", name = "p") matches df.groupBy { key }.percentile(0.5, "p") { value }
    }

    @Test
    fun GroupByStd0() {
        df.groupBy("key").stdFor("value") matches df.groupBy { key }.stdFor { value }
    }

    @Test
    fun GroupByStd2() {
        df.groupBy("key").std("value", name = "std") matches df.groupBy { key }.std("std") { value }
    }

    @Test
    fun GroupBySum0() {
        df.groupBy("key").sumFor("value") matches df.groupBy { key }.sumFor { value }
    }

    @Test
    fun GroupBySum2() {
        df.groupBy("key").sum("value", name = "sum") matches df.groupBy { key }.sum("sum") { value }
    }

    @Test
    fun Implode() {
        df.implode("value") matches df.implode { value }
    }

    @Test
    fun InnerJoin() {
        df.innerJoin(other, "key") matches df.innerJoin(other) { key }
    }

    @Test
    fun Join0() {
        df.join(other, "key") matches df.join(other) { key }
    }

    @Test
    fun LeftJoin() {
        df.leftJoin(other, "key") matches df.leftJoin(other) { key }
    }

    @Test
    fun Max1() {
        df.maxFor("value") matches df.maxFor { value }
    }

    @Test
    fun Mean1() {
        df.meanFor("value") matches df.meanFor { value }
    }

    @Test
    fun Median1() {
        df.medianFor("value") matches df.medianFor { value }
    }

    @Test
    fun Merge0() {
        df.merge("key").into("keys") matches df.merge { key }.into("keys").convert { keys }.with { it as List<Any?> }
    }

    @Test
    fun Min1() {
        df.minFor("value") matches df.minFor { value }
    }

    @Test
    fun Move0() {
        df.move("value").toStart() matches df.move { value }.toStart()
    }

    @Test
    fun MoveTo1() {
        df.moveTo(0, "value") matches df.moveTo(0) { value }
    }

    @Test
    fun `MoveTo1 inside group`() {
        df.moveTo(0, "value") matches df.moveTo(0, false) { value }
    }

    @Test
    fun MoveToEnd1() {
        df.moveToEnd("value") matches df.moveToEnd { value }
    }

    @Test
    fun `MoveToEnd1 inside group`() {
        df.moveToEnd("value") matches df.moveToEnd(false) { value }
    }

    @Test
    fun MoveToStart1() {
        df.moveToStart("value") matches df.moveToStart { value }
    }

    @Test
    fun `MoveToStart1 inside group`() {
        df.moveToStart("value") matches df.moveToStart(false) { value }
    }

    @Test
    fun Percentile1() {
        df.percentileFor(0.5, "value") matches df.percentileFor(0.5) { value }
    }

    @Test
    fun Remove0() {
        df.remove("other") matches df.remove { other }
    }

    @Test
    fun Rename() {
        df.rename("other").to("renamed") matches df.rename { other }.to("renamed")
    }

    @Test
    fun Reorder() {
        df.reorder("other", "value").byName() matches df.reorder { other and value }.byName()
    }

    @Test
    fun RightJoin() {
        df.rightJoin(other, "key") matches df.rightJoin(other) { key }
    }

    @Test
    fun Select0() {
        df.select("key", "value") matches df.select { key and value }
    }

    @Test
    fun Split0() {
        df.split("key").by { listOf("a", "b") }.into("a", "b") matches
            df.split { key }.by { listOf("a", "b") }.into("a", "b")
    }

    @Test
    fun Std1() {
        df.stdFor("value") matches df.stdFor { value }
    }

    @Test
    fun Sum1() {
        df.sumFor("value") matches df.sumFor { value }
    }

    @Test
    fun Ungroup0() {
        df.group("value").into("values").ungroup("values") matches df.group { value }.into("values").ungroup { values }
    }

    @Test
    fun Update0() {
        df.update("value").with { (it as Int) + 1 } matches df.update { value }.with { it + 1 }
    }

    @Test
    fun ValueCounts() {
        df.valueCounts("key") matches df.valueCounts { key }
    }
}
