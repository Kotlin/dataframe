package org.jetbrains.kotlinx.dataframe.samples.guides

import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.addAll
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnNames
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
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.shuffle
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.tail
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readExcel
import org.jetbrains.kotlinx.dataframe.io.readParquet
import org.jetbrains.kotlinx.dataframe.io.writeExcel
import org.jetbrains.kotlinx.dataframe.size
import java.io.File

/**
 * The Kotlin DataFrame counterparts of the cases in `pandas-reference-cases.json`: one lambda per case `id`, run on
 * the table of `sales.csv`. Used by [PandasReferenceCasesTest] (one test per case) and [PandasReferenceClaimsTest].
 */
internal class PandasReferenceKotlinSide {

    @DataSchema
    interface Sales {
        val region: String
        val product: String
        val year: Int
        val revenue: Int
        val cost: Int
        val discount: Double?
    }

    val df: DataFrame<Sales> = DataFrame.readCsv(javaClass.getResource("/sales.csv")!!).cast()

    fun managers() =
        dataFrameOf(
            "region" to listOf("North", "South", "East"),
            "manager" to listOf("Ann", "Bob", "Eve"),
        )

    /** One entry per `id` of the JSON file. */
    val side: Map<String, () -> Any?> = mapOf(
        "shape" to { listOf(df.rowsCount(), df.columnsCount()) },
        "shapeNonSquare" to { df.select { region and revenue }.let { listOf(it.rowsCount(), it.columnsCount()) } },
        "sizeNonSquare" to { df.select { region and revenue }.size().toString() },
        "columnNames" to { df.columnNames() },
        "headTwo" to { df.head(2).revenue.toList() },
        "tailTwo" to { df.tail(2).revenue.toList() },
        "filterTwoConditions" to { df.filter { revenue > 100 && region == "North" }.rowsCount() },
        "sortByDescending" to { df.sortByDesc { revenue }.revenue.toList() },
        "sortByTwoColumns" to { df.sortBy { year and revenue }.revenue.toList() },
        "dropFirstRows" to { df.drop(4).revenue.toList() },
        "firstRow" to { df.first().revenue },
        "iterateRows" to { buildList { for (row in df) add(row.revenue) } },
        "countMissing" to { df.count { discount == null } },
        "dropMissingRows" to { df.dropNulls { discount }.rowsCount() },
        "dropNullsAllColumns" to {
            dataFrameOf("a" to listOf(1, null, 3), "b" to listOf(null, 2, 3)).dropNulls().rowsCount()
        },
        "fillMissingValues" to { df.fillNulls { discount }.with { 0.0 }["discount"].toList() },
        "describeNullsOfDiscount" to { df.describe().first { name == "discount" }.nulls },
        "distinctCount" to { df.countDistinct { region } },
        "distinctValues" to { df.select { region }.distinct()["region"].toList() },
        "valueCounts" to { df.valueCounts { region }["count"].toList().sortedBy { it as Int } },
        "groupSum" to { df.groupBy { region }.sum { revenue }["revenue"].toList() },
        "groupCount" to { df.groupBy { region }.count()["count"].toList() },
        "pivotValues" to {
            df.pivot { year }.groupBy { region }.sum { revenue }.ungroup("year")
                .rows().map { listOf(it["2023"], it["2024"]) }
        },
        "innerJoinRowCount" to { df.join(managers(), "region").rowsCount() },
        "leftJoinRowCount" to { managers().leftJoin(df, "region").rowsCount() },
        "rightJoinRowCount" to { df.rightJoin(managers(), "region").rowsCount() },
        "fullJoinRowCount" to { df.fullJoin(managers(), "region").rowsCount() },
        "concatRowCount" to { df.concat(df).rowsCount() },
        "mean" to { df.revenue.mean() },
        "median" to { df.revenue.median() },
        "percentile25" to { df.revenue.percentile(25.0) },
        "describeCoversColumns" to { df.describe()["name"].toList() },
        "groupOrderTwoKeys" to { totalsByRegionAndProduct() },
        "groupOrderSortFalse" to { totalsByRegionAndProduct() },
        "readParquetRows" to { parquet().rowsCount() },
        "readParquetColumns" to { parquet().columnNames() },
        "readParquetSum" to { parquet()["revenue"].toList().sumOf { (it as Number).toLong() } },
        "readParquetMissing" to { parquet()["discount"].toList().count { it == null } },
        "excelRoundTripRows" to { excelRoundTrip().rowsCount() },
        "excelRoundTripColumns" to { excelRoundTrip().columnNames() },
        "excelNumberTypes" to {
            excelRoundTrip().let { back ->
                listOf("year", "revenue", "cost", "discount").map { back[it].type().toString() }
            }
        },
        "meltShape" to { melted().let { listOf(it.rowsCount(), it.columnNames()) } },
        "meltOrder" to { melted()["amount"].toList() },
        "explodeValues" to {
            dataFrameOf("k" to listOf("a", "b"), "v" to listOf(listOf(1, 2), listOf(3))).explode("v")["v"].toList()
        },
        "splitValues" to {
            dataFrameOf("s" to listOf("a-b", "c-d")).split("s").by("-").into("x", "y")
                .let { listOf(it["x"].toList(), it["y"].toList()) }
        },
        "concatColumns" to { df.select { region }.addAll(df.select { cost }).columnNames() },
        "joinDifferentKeyNamesRows" to { joinedOnDifferentNames().rowsCount() },
        "joinDifferentKeyNamesColumns" to { joinedOnDifferentNames().columnNames() },
        "cumulativeSum" to { df.revenue.cumSum().toList() },
        "shiftByOne" to { df.add("prev") { prev()?.revenue }["prev"].toList() },
        "diffFromPrevious" to { df.add("d") { diff(0) { revenue } }["d"].toList() },
        // Inside the lambda `it` is a row of the window; a bare `revenue` would read the current row.
        "movingAverage" to { df.add("ma") { movingAverage(3) { it.revenue } }["ma"].toList() },
        "valueCountsNormalized" to {
            df.valueCounts { region }["count"].toList().map { (it as Int).toDouble() / df.rowsCount() }
        },
        "nLargest" to { df.sortByDesc { revenue }.take(2).revenue.toList() },
        "nSmallest" to { df.sortBy { revenue }.take(2).revenue.toList() },
        "sampleCount" to { df.shuffle().take(3).rowsCount() },
        "stringUpper" to { df.region.map { it.uppercase() }.toList() },
        "stringContains" to { df.filter { region.contains("or") }.rowsCount() },
        "stringLength" to { df.region.map { it.length }.toList() },
        "rowWise" to { df.map { revenue - cost }.toList() },
        "dateYear" to {
            dataFrameOf("d" to listOf("2023-01-15", "2024-03-02")).convert("d").toLocalDate()["d"]
                .map { (it as LocalDate).year }.toList()
        },
        "groupTransform" to {
            df.join(df.groupBy { region }.aggregate { sum { cost } into "total" }, "region")["total"].toList()
        },
    )

    private fun totalsByRegionAndProduct() = df.groupBy { region and product }.sum { revenue }["revenue"].toList()

    private fun parquet() = DataFrame.readParquet(File(javaClass.getResource("/sales-pandas.parquet")!!.toURI()))

    private fun excelRoundTrip(): DataFrame<*> {
        val file = File.createTempFile("sales", ".xlsx").apply { deleteOnExit() }
        df.writeExcel(file)
        return DataFrame.readExcel(file)
    }

    private fun melted() =
        df.select { region and revenue and cost }.gather { revenue and cost }.into("metric", "amount")

    private fun joinedOnDifferentNames() =
        df.join(managers().rename("region" to "area")) { "region" match right["area"] }
}
