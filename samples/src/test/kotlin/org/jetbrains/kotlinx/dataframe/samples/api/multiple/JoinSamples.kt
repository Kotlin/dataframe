package org.jetbrains.kotlinx.dataframe.samples.api.multiple

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.excludeJoin
import org.jetbrains.kotlinx.dataframe.api.filterJoin
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.fullJoin
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.kandy.letsplot.style.LayoutParameters.Companion.background
import org.junit.Test

class JoinSamples : DataFrameSampleHelper("join", "api") {

    @DataSchema
    interface DfAges {
        val age: Int
        val firstName: String
    }

    private val dfAges = dataFrameOf(
        "firstName" to listOf("Alice", "Bob", "Charlie"),
        "age" to listOf(14, 45, 20),
    ).cast<DfAges>()

    @DataSchema
    interface DfCities {
        val city: String
        val name: String
    }

    private val dfCities = dataFrameOf(
        "name" to listOf("Bob", "Alice", "Charlie"),
        "city" to listOf("London", "Dubai", "Moscow"),
    ).cast<DfCities>()

    @DataSchema
    interface DfWithNameAndCity {
        val name: String
        val city: String?
    }

    @DataSchema
    interface DfLeft : DfWithNameAndCity {
        val age: Int
        override val city: String
        override val name: String
    }

    private val dfLeft = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie", "Charlie"),
        "age" to listOf(15, 45, 20, 40),
        "city" to listOf("London", "Dubai", "Moscow", "Tokyo"),
    ).cast<DfLeft>()

    @DataSchema
    interface DfRight : DfWithNameAndCity {
        override val city: String?
        val isBusy: Boolean
        override val name: String
    }

    private val dfRight = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Alice", "Charlie"),
        "isBusy" to listOf(true, false, true, true),
        "city" to listOf("London", "Tokyo", null, "Moscow"),
    ).cast<DfRight>()

    private fun nameToColor(name: String): RgbColor =
        when (name) {
            "Alice" -> RgbColor(189, 206, 233)
            "Bob" -> RgbColor(198, 224, 198)
            "Charlie" -> RgbColor(219, 198, 230)
            else -> RgbColor(255, 255, 255)
        }

    private fun nameAndCityToColor(name: String, city: String?): RgbColor =
        when (name to city) {
            "Alice" to "London" -> RgbColor(242, 210, 189)
            "Bob" to "Dubai" -> RgbColor(245, 226, 191)
            "Charlie" to "Moscow" -> RgbColor(210, 229, 199)
            "Charlie" to "Tokyo" -> RgbColor(191, 223, 232)
            "Bob" to "Tokyo" -> RgbColor(200, 200, 232)
            "Alice" to null -> RgbColor(233, 199, 220)
            else -> RgbColor(255, 255, 255)
        }

    fun <T : DfWithNameAndCity> DataFrame<T>.colorized() =
        format().perRowCol { row, _ ->
            val color = nameAndCityToColor(row.name, row.city)
            background(color) and textColor(black)
        }

    @Test
    fun notebook_test_join_3() {
        // SampleStart
        dfAges
            // SampleEnd
            .format().perRowCol { row, _ ->
                val color = nameToColor(row.firstName)
                background(color) and textColor(black)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_5() {
        // SampleStart
        dfCities
            // SampleEnd
            .format().perRowCol { row, _ ->
                val color = nameToColor(row.name)
                background(color) and textColor(black)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_6() {
        // SampleStart
        // INNER JOIN on differently named keys:
        // Merge a row when dfAges.firstName == dfCities.name.
        // With the given data all 3 names match → all rows merge.
        dfAges.join(dfCities) { firstName match right.name }
            // SampleEnd
            .format().perRowCol { row, _ ->
                val color = nameToColor(row.firstName)
                background(color) and textColor(black)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_8() {
        // SampleStart
        dfLeft
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_10() {
        // SampleStart
        dfRight
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_11() {
        // SampleStart
        // INNER JOIN on "name" only:
        // Merge when left.name == right.name.
        // Duplicate keys produce multiple merged rows (one per pairing).
        dfLeft.join(dfRight) { name }
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_12() {
        // SampleStart
        // INNER JOIN on all same-named columns ("name" and "city"):
        // Merge when BOTH name AND city are equal; otherwise the row is dropped.
        dfLeft.join(dfRight)
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_13() {
        // SampleStart
        dfLeft
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_14() {
        // SampleStart
        dfRight
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_15() {
        // SampleStart
        // INNER JOIN:
        // Keep only rows where (name, city) match on both sides.
        // In this dataset both Charlies match twice (Moscow, Milan) → 2 merged rows.
        dfLeft.innerJoin(dfRight) { name and city }
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_16() {
        // SampleStart
        // FILTER JOIN:
        // Keep ONLY left rows that have ANY match on (name, city).
        // No right-side columns are added.
        dfLeft.filterJoin(dfRight) { name and city }
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_17() {
        // SampleStart
        // LEFT JOIN:
        // Keep ALL left rows. If (name, city) matches, attach right columns;
        // if not, right columns are null (e.g., Alice–London has no right match).
        dfLeft.leftJoin(dfRight) { name and city }
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_18() {
        // SampleStart
        // RIGHT JOIN:
        // Keep ALL right rows. If no left match, left columns become null
        // (e.g., Alice with city=null exists only on the right).
        dfLeft.rightJoin(dfRight) { name and city }
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_19() {
        // SampleStart
        // FULL JOIN:
        // Keep ALL rows from both sides. Where there's no match on (name, city),
        // the other side is filled with nulls.
        dfLeft.fullJoin(dfRight) { name and city }
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_20() {
        // SampleStart
        // EXCLUDE JOIN:
        // Keep ONLY left rows that have NO match on (name, city).
        // Useful to find "unpaired" left rows.
        dfLeft.excludeJoin(dfRight) { name and city }
            // SampleEnd
            .colorized()
            .saveDfHtmlSample()
    }
}
