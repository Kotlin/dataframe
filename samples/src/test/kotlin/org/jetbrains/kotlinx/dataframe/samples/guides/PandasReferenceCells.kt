@file:Suppress("UNUSED_VARIABLE", "UNUSED_EXPRESSION", "unused", "UNUSED_PARAMETER")

package org.jetbrains.kotlinx.dataframe.samples.guides

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.addAll
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnsCount
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.countDistinct
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.diff
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.fullJoin
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.movingAverage
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.nulls
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.shuffle
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.tail
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readExcel
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.readParquet
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.jetbrains.kotlinx.dataframe.io.writeArrowFeather
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import org.jetbrains.kotlinx.dataframe.io.writeExcel
import org.jetbrains.kotlinx.dataframe.io.writeJson
import org.jetbrains.kotlinx.dataframe.size
import java.io.File
import java.sql.Connection

/**
 * Compile-time twins of the Kotlin cells in `pandasToKotlinDataFrameReference.md`.
 *
 * Nothing here runs: the file only has to COMPILE. [PandasReferenceGuardTest] reads the reference page and fails
 * when a Kotlin cell of a table does not occur in this file, so a cell cannot be added or edited without a
 * compiled counterpart. Cells are copied verbatim (spacing is ignored). The columns of [RefRow] are the names the
 * tables use: `a`, `b`, `y`, `v`, `rev`, `cost`, `revenue` are numbers, `s`, `r`, `p` are text, `d` has gaps.
 *
 * Cells that contain `...` are templates ("any selector here") and are not part of this file.
 * What the cells DO is checked elsewhere: see [PandasReferenceCasesTest].
 */
class PandasReferenceCells {

    @DataSchema
    interface RefRow {
        val a: Int
        val b: Int
        val s: String
        val y: Int
        val r: String
        val p: String
        val rev: Int
        val cost: Int
        val v: Int
        val revenue: Int
        val d: Double?
        val l: List<Int>
    }

    fun createReadWrite(
        path: String,
        file: File,
        connection: Connection,
        df: DataFrame<RefRow>,
    ) {
        dataFrameOf("a" to listOf(1, 2))
        DataFrame.readCsv(path)
        DataFrame.readJson(path)
        DataFrame.readArrowFeather(path)
        DataFrame.readParquet(path)
        DataFrame.readSqlTable(connection, "name")
        DataFrame.readSqlQuery(connection, "SELECT * FROM sales")
        DataFrame.readExcel(file)
        df.writeExcel(file)
        df.writeCsv(file)
        df.writeJson(file)
        df.writeArrowFeather(file)
    }

    fun inspect(df: DataFrame<RefRow>, n: Int) {
        df.head(n)
        df.take(n)
        df.tail(n)
        df.rowsCount()
        df.columnsCount()
        df.size()
        df.columnNames()
        df.schema()
        df.describe()
        df.describe().select { name and nulls }
        df.countDistinct { a }
        df.valueCounts { a }
        df.select { a }.distinct()
    }

    fun selectFilterSort(df: DataFrame<RefRow>, n: Int) {
        df.a
        df["a"]
        df.select { a and b }
        df.select("a", "b")
        df.filter { a > 10 }
        df.filter { "a"<Int>() > 10 }
        df.filter { a > 1 && s == "x" }
        df.filter { "a"<Int>() > 1 && "s"<String>() == "x" }
        df.sortBy { a and b }
        df.sortBy("a", "b")
        df.sortByDesc { a }
        df.sortByDesc("a")
        df.take(n)
        df.drop(n)
    }

    fun addChangeRenameRemove(df: DataFrame<RefRow>) {
        df.add("c") { a + b }
        df.add("c") { if (a > 1) "hi" else "lo" }
        df.update { a }.with { it * 2 }
        df.update { a }.where { y == 2024 }.with { it * 2 }
        df.convert { y }.to<String>()
        df.rename("a" to "b")
        df.rename { a }.to("b")
        df.remove { a }
        df.remove("a")
        df.a.map { it * 2 }
    }

    fun missingValues(df: DataFrame<RefRow>) {
        df.count { d == null }
        df.fillNulls { d }.with { 0.0 }
        df.dropNulls { d }
        df.dropNulls()
        df.add("c") { a * (d ?: 0.0) }
    }

    // A cell is copied from a table row, so it stays on one line even where ktlint would wrap it.
    @Suppress("ktlint:standard:statement-wrapping")
    fun groupAggregatePivot(df: DataFrame<RefRow>) {
        df.groupBy { r }.sum { revenue }
        df.groupBy { r }.count()
        df.groupBy { r }.aggregate { sum { rev } into "t"; mean { cost } into "m" }
        df.groupBy { r and p }.aggregate { sum { rev } into "t" }
        df.pivot { y }.groupBy { r }.sum { v }.ungroup("y")
    }

    fun combine(df1: DataFrame<RefRow>, df2: DataFrame<RefRow>) {
        df1.join(df2, "id")
        df1.leftJoin(df2, "id")
        df1.concat(df2)
        df1.rightJoin(df2, "id")
        df1.fullJoin(df2, "id")
        df1.addAll(df2)
        df1.join(df2) { "a" match right["b"] }
    }

    fun reshapeAndSequences(df: DataFrame<RefRow>, n: Int) {
        df.select { r and a and b }.gather { a and b }.into("k", "v")
        df.explode { l }
        df.split { s }.by("-").into("x", "y")
        df.a.cumSum()
        df.add("prev") { prev()?.a }
        df.add("d") { diff(0) { a } }
        df.add("ma") { movingAverage(3) { it.a } }
        df.valueCounts { a }
        df.sortByDesc { a }.take(n)
        df.sortBy { a }.take(n)
        df.shuffle().take(n)
        df.join(df.groupBy { r }.aggregate { sum { cost } into "total" }, "r")
    }

    fun stringsDatesRowByRow(df: DataFrame<RefRow>) {
        df.s.map { it.uppercase() }
        df.filter { s.contains("or") }
        df.s.map { it.length }
        df.map { a - b }
        df.convert { s }.toLocalDate()
    }

    @Suppress("ktlint:standard:statement-wrapping")
    fun iterateAndCompute(df: DataFrame<RefRow>) {
        for (row in df) { row.revenue }
        df.first()
        df.a.mean()
        df.a.sum()
        df.a.median()
        df.a.percentile(25.0)
    }

    @Suppress("ktlint:standard:statement-wrapping")
    fun namesThatDoNotExist(df: DataFrame<RefRow>, other: DataFrame<RefRow>, file: File) {
        df.groupBy { a }
        df.filter { a > 1 }
        df.rowsCount()
        df.columnsCount()
        df.size()
        df.schema()
        df.describe().select { name and nulls }
        df.columnNames()
        df.join(other, "id")
        df.dropNulls()
        df.writeCsv(file)
        df.countDistinct { a }
        df.convert { a }.to<String>()
        df.valueCounts { a }
        df.select { a }.distinct()
        df.a.toList()
        df.count { d == null }
        for (row in df) { row.revenue }
        df.rename("a" to "b")
        df.values()
    }
}
