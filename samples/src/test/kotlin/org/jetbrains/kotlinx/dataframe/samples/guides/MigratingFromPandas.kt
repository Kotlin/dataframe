@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.api.ungroup
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readCsvStr
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import org.jetbrains.kotlinx.dataframe.io.writeJson
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Ignore
import org.junit.Test
import java.io.File

/**
 * Samples of the "Coming from Pandas" guide (`topics/guides/migratingFromPandas.md`).
 *
 * The Pandas side of every sample lives in the same page as a hand-written `python` block and is run by
 * `samples/pandas-validation/validate_pandas.py` on the same data. Numbers asserted below were taken from that
 * run, so a change on either side fails one of the two. See `samples/pandas-validation/README.md`.
 */
class MigratingFromPandas : DataFrameSampleHelper("migratingFromPandas", "guides") {

    @DataSchema
    interface Sales {
        val region: String
        val product: String
        val year: Int
        val revenue: Int
        val cost: Int
        val discount: Double?
    }

    // The same six rows as `src/test/resources/sales.csv` and as `df` in validate_pandas.py.
    // Product names are longer than one letter on purpose: a one-letter CSV value is read as `Char`.
    private val df: DataFrame<Sales> = dataFrameOf(
        "region" to listOf("North", "North", "South", "South", "North", "South"),
        "product" to listOf("Pen", "Ink", "Pen", "Ink", "Pen", "Pen"),
        "year" to listOf(2023, 2023, 2023, 2024, 2024, 2024),
        "revenue" to listOf(100, 150, 80, 200, 120, 90),
        "cost" to listOf(60, 90, 70, 120, 70, 50),
        "discount" to listOf(0.1, null, 0.05, null, 0.2, 0.0),
    ).cast()

    @Test
    fun pandasCreateDataFrame() {
        // SampleStart
        val df = dataFrameOf(
            "region" to listOf("North", "North", "South", "South", "North", "South"),
            "product" to listOf("Pen", "Ink", "Pen", "Ink", "Pen", "Pen"),
            "year" to listOf(2023, 2023, 2023, 2024, 2024, 2024),
            "revenue" to listOf(100, 150, 80, 200, 120, 90),
            "cost" to listOf(60, 90, 70, 120, 70, 50),
            "discount" to listOf(0.1, null, 0.05, null, 0.2, 0.0),
        )
        // SampleEnd
        df.rowsCount() shouldBe 6
        // the schema that the page's later examples rely on (they use the hand-written `Sales` of this class)
        df.schema().toString() shouldBe
            "region: String\nproduct: String\nyear: Int\nrevenue: Int\ncost: Int\ndiscount: Double?"
        df.saveDfHtmlSample()
    }

    @Ignore("The shown path is relative to the reader's own project; pandasReadCsvResource reads the same file")
    @Test
    fun pandasReadCsv() {
        // SampleStart
        val df = DataFrame.readCsv("sales.csv")
        // SampleEnd
    }

    @Test
    fun pandasReadCsvResource() {
        val df = DataFrame.readCsv(javaClass.getResource("/sales.csv")!!)
        df.schema().toString() shouldBe
            "region: String\nproduct: String\nyear: Int\nrevenue: Int\ncost: Int\ndiscount: Double?"
        df.cast<Sales>().count { discount == null } shouldBe 2 // empty CSV cell -> null
        // the file holds exactly the table that `df` creates in code (validate_pandas.py checks the Pandas side)
        for (name in this.df.columnNames()) df[name].toList() shouldBe this.df[name].toList()
    }

    @Test
    fun pandasFilterRows() {
        // SampleStart
        df.filter { revenue > 100 && region == "North" }
            // SampleEnd
            .also { it.rowsCount() shouldBe 2 } // pandas: the same 2 rows
            .saveDfHtmlSample()
    }

    @Test
    fun pandasAddColumn() {
        // SampleStart
        df.add("profit") { revenue - cost }
            // SampleEnd
            .also { it["profit"].toList() shouldBe listOf(40, 60, 10, 80, 50, 40) }
            .saveDfHtmlSample()
    }

    @Test
    fun pandasCountNulls() {
        // SampleStart
        df.count { discount == null }
            // SampleEnd
            .also { it shouldBe 2 } // pandas: df.discount.isna().sum() == 2
    }

    @Test
    fun pandasFillMissingDiscount() {
        // SampleStart
        df.fillNulls { discount }.with { 0.0 }
            // SampleEnd
            .also { it["discount"].toList() shouldBe listOf(0.1, 0.0, 0.05, 0.0, 0.2, 0.0) }
            .saveDfHtmlSample()
    }

    @Test
    fun pandasDropMissingDiscount() {
        // SampleStart
        df.dropNulls { discount }
            // SampleEnd
            .also { it["revenue"].toList() shouldBe listOf(100, 80, 120, 90) } // the rows without a discount are gone
            .saveDfHtmlSample()
    }

    @Test
    fun pandasNullableArithmetic() {
        // SampleStart
        // `discount` is `Double?`: the compiler makes you decide what null means.
        df.add("net") { revenue * (1 - (discount ?: 0.0)) }
            // SampleEnd
            .also { it["net"].toList() shouldBe listOf(90.0, 150.0, 76.0, 200.0, 96.0, 90.0) }
            .saveDfHtmlSample()
    }

    @Test
    fun pandasGroupByAggregate() {
        // SampleStart
        df.groupBy { region }.aggregate {
            sum { revenue } into "total"
            mean { cost } into "avgCost"
        }
            // SampleEnd
            .also {
                // pandas: the same numbers
                it["region"].toList() shouldBe listOf("North", "South")
                it["total"].toList() shouldBe listOf(370, 370)
                it["avgCost"].toList() shouldBe listOf(73.33333333333333, 80.0)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun pandasPivotRevenueByYear() {
        // SampleStart
        df.pivot { year }.groupBy { region }.sum { revenue }.ungroup("year")
            // SampleEnd
            .also {
                it.columnNames() shouldBe listOf("region", "2023", "2024")
                it["2023"].toList() shouldBe listOf(250, 80)
                it["2024"].toList() shouldBe listOf(120, 290)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun pandasLeftJoin() {
        // SampleStart
        val managers = dataFrameOf(
            "region" to listOf("North", "South", "East"),
            "manager" to listOf("Ann", "Bob", "Eve"),
        )
        managers.leftJoin(df, "region")
            // SampleEnd
            .also {
                it.rowsCount() shouldBe 7
                it["revenue"].toList().last() shouldBe null // East has no sales: its row holds nulls
            }
            .saveDfHtmlSample()
    }

    @Test
    fun pandasWriteCsv() {
        val csv = File.createTempFile("sales-out", ".csv").apply { deleteOnExit() }
        // SampleStart
        df.writeCsv(csv)
        // SampleEnd
        DataFrame.readCsv(csv).rowsCount() shouldBe 6
    }

    @Test
    fun pandasWriteJson() {
        val json = File.createTempFile("sales-out", ".json").apply { deleteOnExit() }
        // SampleStart
        df.writeJson(json)
        // SampleEnd
        DataFrame.readJson(json).rowsCount() shouldBe 6
    }

    // ---- Claims of the "Things that will surprise you" section: one test per sentence, literal values.

    @Test
    fun nothingChangesInPlace() {
        df.filter { revenue > 100 }.rowsCount() shouldBe 3
        df.rowsCount() shouldBe 6
    }

    @Test
    fun describeCoversAllColumnsAndHasNulls() {
        val described = df.describe()
        described.rowsCount() shouldBe 6 // all six columns, pandas: only the 4 numeric ones
        described.columnNames() shouldContainAll listOf("nulls", "top", "freq")
    }

    @Test
    fun percentilesDifferFromPandas() {
        // The 25th percentile of the six revenues: R8 (used here) 89.17, R7 (the Pandas default) 92.5.
        df.revenue.percentile(25.0) shouldBe 89.16666666666666
    }

    @Test
    fun dropRemovesFirstRowsNotColumns() {
        val dropped = df.drop(4)
        dropped.revenue.toList() shouldBe listOf(120, 90) // the first four rows are gone, not the last ones
        dropped.columnNames() shouldBe df.columnNames()
    }

    @Test
    fun oneLetterCsvValuesAreChars() {
        DataFrame.readCsvStr("product\nA\nB\n").schema().toString() shouldBe "product: Char"
    }

    @Test
    fun groupsKeepTheOrderOfFirstAppearance() {
        // pandas groupby(["region", "product"]) sorts the groups by key and gives 150, 220, 200, 170;
        // pandas groupby(..., sort=False) gives the same order as here.
        df.groupBy { region and product }.sum { revenue }["revenue"].toList() shouldBe listOf(220, 150, 170, 200)
    }

    @Test
    fun pivotWithoutUngroupNestsColumns() {
        df.pivot { year }.groupBy { region }.sum { revenue }.columnNames() shouldBe listOf("region", "year")
    }
}
