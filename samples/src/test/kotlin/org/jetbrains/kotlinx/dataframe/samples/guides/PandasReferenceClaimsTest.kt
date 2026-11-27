package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.size
import org.junit.Test

/**
 * Claims of `pandasToKotlinDataFrameReference.md` that are about Kotlin DataFrame alone, so they have no Pandas
 * expression and do not belong to `pandas-reference-cases.json`: one test per sentence, literal values.
 */
class PandasReferenceClaimsTest {

    private val kotlin = PandasReferenceKotlinSide()
    private val df = kotlin.df

    @Test
    fun everyKotlinCounterpartHasACaseAndTheOtherWayRound() {
        val ids = PandasCases.load("/pandas-reference-cases.json").map { it.id }
        kotlin.side.keys.sorted() shouldBe ids.sorted()
    }

    @Test
    fun sizePrintsRowsThenColumns() {
        // a square table cannot tell the two apart, so the second frame has 6 rows and 2 columns
        df.size().toString() shouldBe "6 x 6"
        df.select { region and revenue }.size().toString() shouldBe "6 x 2"
    }

    @Test
    fun aggregateWithoutAColumnGivesOneNestedColumn() {
        df.groupBy { region }.aggregate { sum() }.columnNames() shouldBe listOf("region", "aggregated")
    }

    @Test
    fun groupBySumWithoutAColumnSumsEveryNumericColumn() {
        df.groupBy { region }.sum().columnNames() shouldBe listOf("region", "year", "revenue", "cost", "discount")
    }

    @Test
    fun gatherKeepsEveryOtherColumn() {
        df.gather { revenue and cost }.into("metric", "amount").columnNames() shouldBe
            listOf("region", "product", "year", "discount", "metric", "amount")
    }

    @Test
    fun innerJoinKeepsOnlyTheMatchedRows() {
        df.innerJoin(kotlin.managers(), "region").rowsCount() shouldBe 6
    }

    @Test
    fun unmatchedRowsOfALeftJoinHoldNulls() {
        kotlin.managers().leftJoin(df, "region")["revenue"].toList().last() shouldBe null
    }
}
