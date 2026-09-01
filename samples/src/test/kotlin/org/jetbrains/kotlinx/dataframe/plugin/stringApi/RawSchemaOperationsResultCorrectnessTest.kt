package org.jetbrains.kotlinx.dataframe.plugin.stringApi

import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.byName
import org.jetbrains.kotlinx.dataframe.api.cast
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
import org.junit.Ignore
import org.junit.Test

/**
 * Making sure compiler evaluation of String API overload on raw dataframe (no schema) is correct
 * whatever columns appear as a result are "type safe";
 * no ColumnNotFound or ClassCast exceptions will happen if property is used.
 * User scenario when df has no schema, some operations are invoked, and as a result we enrich schema
 */
@Suppress("FunctionName", "TestFunctionName")
class RawSchemaOperationsResultCorrectnessTest : CommonTestData() {
    @Test
    @Ignore
    fun DataFrameCumSum() {
        dfRaw.cumSum("value") matches df.cumSum { value }.select { value.cast<Any?>() }
    }

    @Test
    fun DataFrameGroupBy() {
        dfRaw.groupBy("key").toDataFrame() matches df.groupBy { key }.toDataFrame()
            .convert { group }.with { it.select { key.cast<Any?>() } }
            .select { key.cast<Any?>() and group }
    }

    @Test
    @Ignore
    fun DataFrameUnfold() {
        recordsRaw.unfold("ab") matches records.unfold { ab }.select { ab.cast<Any?>() }
    }

    @Test
    fun Distinct0() {
        dfRaw.distinct("key") matches df.distinct { key }.select { key.cast<Any?>() }
    }

    @Test
    fun DropNa0() {
        dfRaw.dropNA("nullable") matches df.dropNA { nullable }.select { nullable.cast<Any>() }
    }

    @Test
    fun DropNulls0() {
        dfRaw.dropNulls("nullable") matches df.dropNulls { nullable }.select { nullable.cast<Any>() }
    }

    @Test
    fun ExcludeJoin() {
        dfRaw.excludeJoin(otherRaw, "key") matches df.excludeJoin(other) { key }.select { none() }
    }

    @Test
    fun Explode0() {
        listsRaw.explode("values") matches lists.explode { values }.select { values.cast<Any?>() }
    }

    @Test
    fun ExplodeColumns() {
        listsRaw[0].explode("values") matches lists[0].explode { values }.select { values.cast<Any?>() }
    }

    @Test
    @Ignore
    fun FillNaNs0() {
        dfRaw.fillNaNs("nan").withZero() matches df.fillNaNs { nan }.withZero().select { nan.cast<Any?>() }
    }

    @Test
    fun FillNulls0() {
        dfRaw.fillNulls("nullable").withZero() matches
            df.fillNulls { nullable }.withZero().select { nullable.cast<Any>() }
    }

    @Test
    fun `FillNulls0 fillNulls to fillNA`() {
        dfRaw.fillNA("nullable").withZero() matches df.fillNulls { nullable }.withZero().select { nullable.cast<Any>() }
    }

    @Test
    fun `FillNulls0 fillNA to fillNulls`() {
        dfRaw.fillNulls("nullable").withZero() matches df.fillNA { nullable }.withZero().select { nullable.cast<Any>() }
    }

    @Test
    fun `FillNulls0 fillNA to fillNA`() {
        dfRaw.fillNA("nullable").withZero() matches df.fillNA { nullable }.withZero().select { nullable.cast<Any>() }
    }

    @Test
    fun FilterJoin() {
        dfRaw.filterJoin(otherRaw, "key") matches df.filterJoin(other) { key }.select { none() }
    }

    @Test
    fun Flatten0() {
        nestedRaw.flatten("group") matches nested.flatten { group }.select { none() }
    }

    @Test
    fun FullJoin() {
        dfRaw.fullJoin(otherRaw, "key") matches df.fullJoin(other) { key }.select { none() }
    }

    @Test
    fun Gather0() {
        dfRaw.gather("value").into("name", "result") matches
            df.gather { value }.into("name", "result").convert { result }.with { it as Any? }
                .select { name and result }
    }

    @Test
    fun Group0() {
        dfRaw.group("value").into("values") matches df.group { value }.into("values").select { values }
            .convert { values.value }.with { it as Any? }
    }

    @Test
    fun GroupByCumSum() {
        dfRaw.groupBy("key").cumSum("value").toDataFrame() matches df.groupBy { key }.cumSum { value }.toDataFrame()
            .convert { group }.with { it.select { key.cast<Any?>() } }
            .select { key.cast<Any?>() and group }
    }

    @Test
    fun GroupByMax0() {
        dfRaw.groupBy("key").maxFor("value") matches
            df.groupBy { key }.maxFor { value }.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun GroupByMax2() {
        dfRaw.groupBy("key").max("value", name = "max") matches
            df.groupBy { key }.max("max") { value }.select { key.cast<Any?>() and max.cast<Any?>() }
    }

    @Test
    fun GroupByMean0() {
        dfRaw.groupBy("key").meanFor("value") matches
            df.groupBy { key }.meanFor { value }.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun GroupByMean2() {
        dfRaw.groupBy("key").mean("value", name = "mean") matches
            df.groupBy { key }.mean("mean") { value }.select { key.cast<Any?>() and mean.cast<Any?>() }
    }

    @Test
    fun GroupByMedian0() {
        dfRaw.groupBy("key").medianFor("value") matches
            df.groupBy { key }.medianFor { value }.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun GroupByMedian2() {
        dfRaw.groupBy("key").median("value", name = "median") matches
            df.groupBy { key }.median("median") { value }.select { key.cast<Any?>() and median.cast<Any?>() }
    }

    @Test
    fun GroupByMin0() {
        dfRaw.groupBy("key").minFor("value") matches
            df.groupBy { key }.minFor { value }.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun GroupByMin2() {
        dfRaw.groupBy("key").min("value", name = "min") matches
            df.groupBy { key }.min("min") { value }.select { key.cast<Any?>() and min.cast<Any?>() }
    }

    @Test
    fun GroupByPercentile0() {
        dfRaw.groupBy("key").percentileFor(0.5, "value") matches
            df.groupBy { key }.percentileFor(0.5) { value }.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun GroupByPercentile2() {
        dfRaw.groupBy("key").percentile(0.5, "value", name = "p") matches
            df.groupBy { key }.percentile(0.5, "p") { value }.select { key.cast<Any?>() and p.cast<Any?>() }
    }

    @Test
    fun GroupByStd0() {
        dfRaw.groupBy("key").stdFor("value") matches
            df.groupBy { key }.stdFor { value }.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun GroupByStd2() {
        dfRaw.groupBy("key").std("value", name = "std") matches
            df.groupBy { key }.std("std") { value }.select { key.cast<Any?>() and std.cast<Any?>() }
    }

    @Test
    fun GroupBySum0() {
        dfRaw.groupBy("key").sumFor("value") matches
            df.groupBy { key }.sumFor { value }.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun GroupBySum2() {
        dfRaw.groupBy("key").sum("value", name = "sum") matches
            df.groupBy { key }.sum("sum") { value }.select { key.cast<Any?>() and sum.cast<Any?>() }
    }

    @Test
    fun Implode() {
        dfRaw.implode("value") matches df.implode { value }.select { value.cast<List<Any?>>() }
    }

    @Test
    fun InnerJoin() {
        dfRaw.innerJoin(otherRaw, "key") matches df.innerJoin(other) { key }.select { none() }
    }

    @Test
    fun Join0() {
        dfRaw.join(otherRaw, "key") matches df.join(other) { key }.select { none() }
    }

    @Test
    fun LeftJoin() {
        dfRaw.leftJoin(otherRaw, "key") matches df.leftJoin(other) { key }.select { none() }
    }

    @Test
    fun Max1() {
        dfRaw.maxFor("value") matches df.maxFor { value }.df().select { value.cast<Any?>() }[0]
    }

    @Test
    fun Mean1() {
        dfRaw.meanFor("value") matches df.meanFor { value }.df().select { value.cast<Any?>() }[0]
    }

    @Test
    fun Median1() {
        dfRaw.medianFor("value") matches df.medianFor { value }.df().select { value.cast<Any?>() }[0]
    }

    @Test
    fun Merge0() {
        dfRaw.merge("key").into("keys") matches
            df.merge { key }.into("keys").convert { keys }.with { it as List<Any?> }
                .select { keys }
    }

    @Test
    fun Min1() {
        dfRaw.minFor("value") matches df.minFor { value }.df().select { value.cast<Any?>() }[0]
    }

    @Test
    fun Move0() {
        dfRaw.move("value").toStart() matches df.move { value }.toStart().select { value.cast<Any?>() }
    }

    @Test
    fun MoveTo1() {
        dfRaw.moveTo(0, "value") matches df.moveTo(0) { value }.select { value.cast<Any?>() }
    }

    @Test
    fun `MoveTo1 inside group`() {
        dfRaw.moveTo(0, "value") matches df.moveTo(0, false) { value }.select { value.cast<Any?>() }
    }

    @Test
    fun MoveToEnd1() {
        dfRaw.moveToEnd("value") matches df.moveToEnd { value }.select { value.cast<Any?>() }
    }

    @Test
    fun `MoveToEnd1 inside group`() {
        dfRaw.moveToEnd("value") matches df.moveToEnd(false) { value }.select { value.cast<Any?>() }
    }

    @Test
    fun MoveToStart1() {
        dfRaw.moveToStart("value") matches df.moveToStart { value }.select { value.cast<Any?>() }
    }

    @Test
    fun `MoveToStart1 inside group`() {
        dfRaw.moveToStart("value") matches df.moveToStart(false) { value }.select { value.cast<Any?>() }
    }

    @Test
    fun Percentile1() {
        dfRaw.percentileFor(0.5, "value") matches df.percentileFor(0.5) { value }.df().select { value.cast<Any?>() }[0]
    }

    @Test
    fun Remove0() {
        dfRaw.remove("other") matches df.remove { other }.select { none() }
    }

    @Test
    fun Rename() {
        dfRaw.rename("other").to("renamed") matches df.rename { other }.to("renamed").select { renamed.cast<Any?>() }
    }

    @Test
    fun Reorder() {
        dfRaw.reorder("other", "value").byName() matches df.reorder { other and value }.byName()
            .select { other.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun RightJoin() {
        dfRaw.rightJoin(otherRaw, "key") matches df.rightJoin(other) { key }.select { none() }
    }

    @Test
    fun Select0() {
        dfRaw.select("key", "value") matches df.select { key.cast<Any?>() and value.cast<Any?>() }
    }

    @Test
    fun Split0() {
        dfRaw.split("key").by { listOf("a", "b") }.into("a", "b") matches
            df.split { key }.by { listOf("a", "b") }.into("a", "b")
                .select { a and b }
    }

    @Test
    fun Std1() {
        dfRaw.stdFor("value") matches df.stdFor { value }.df().select { value.cast<Any?>() }[0]
    }

    @Test
    fun Sum1() {
        dfRaw.sumFor("value") matches df.sumFor { value }.df().select { value.cast<Any?>() }[0]
    }

    @Test
    fun Ungroup0() {
        dfRaw.group("value").into("values").ungroup("values") matches
            df.group { value }.into("values")
                .ungroup { values }.select { value.cast<Any?>() }
    }

    @Test
    fun Update0() {
        dfRaw.update("value").with { (it as Int) + 1 } matches
            df.update { value }.with { it + 1 }.select { value.cast<Any>() }
    }

    @Test
    fun ValueCounts() {
        dfRaw.valueCounts("key") matches df.valueCounts { key }.select { key.cast<Any?>() and count }
    }
}
