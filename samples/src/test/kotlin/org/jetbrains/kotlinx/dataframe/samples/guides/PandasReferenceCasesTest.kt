package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

/**
 * The Kotlin half of `pandas-reference-cases.json`: one test per case.
 *
 * Each case there is one Pandas expression with the value it must give on `sales.csv` (`expect`) and, when
 * Kotlin DataFrame gives a different value on the same data, `expectKotlin`. `samples/pandas-validation/
 * validate_pandas.py` runs the Pandas half of the same file. Together the two halves say: this reference row
 * does what the page claims, in both libraries, on the same table.
 *
 * The Kotlin counterparts are in [PandasReferenceKotlinSide]. Expected values were taken from real runs of each
 * side, not from one side compared with the other.
 */
@RunWith(Parameterized::class)
class PandasReferenceCasesTest(private val id: String) {

    @Test
    fun caseHoldsInKotlinDataFrame() {
        val case = PandasCases.load(RESOURCE).single { it.id == id }
        PandasCases.failure(case, PandasReferenceKotlinSide().side[id]) shouldBe null
    }

    companion object {
        private const val RESOURCE = "/pandas-reference-cases.json"

        @JvmStatic
        @Parameters(name = "{0}")
        fun ids(): List<Array<Any>> = PandasCases.load(RESOURCE).map { arrayOf<Any>(it.id) }
    }
}
