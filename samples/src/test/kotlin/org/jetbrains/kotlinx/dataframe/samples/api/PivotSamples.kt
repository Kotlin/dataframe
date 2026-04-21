package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.any
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.groupByOther
import org.jetbrains.kotlinx.dataframe.api.last
import org.jetbrains.kotlinx.dataframe.api.matches
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.maxFor
import org.jetbrains.kotlinx.dataframe.api.maxOf
import org.jetbrains.kotlinx.dataframe.api.maxOrNull
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.pivotCounts
import org.jetbrains.kotlinx.dataframe.api.pivotMatches
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

class PivotSamples : DataFrameSampleHelper("pivot", "api") {
    val df = peopleDf

    private fun isHappyToColor(isHappy: Boolean): RgbColor =
        when (isHappy) {
            true -> RgbColor(189, 206, 233)
            false -> RgbColor(198, 224, 198)
        }

    private fun lastNameToColor(name: String): RgbColor =
        when (name) {
            "Marley" -> RgbColor(242, 210, 189)
            "Smith" -> RgbColor(245, 226, 191)
            "Byrd" -> RgbColor(210, 229, 199)
            "Daniels" -> RgbColor(191, 223, 232)
            "Cooper" -> RgbColor(200, 200, 232)
            "Dylan" -> RgbColor(233, 199, 220)
            "Wolf" -> RgbColor(232, 220, 220)
            "Johnson" -> RgbColor(220, 220, 232)
            else -> RgbColor(255, 255, 255)
        }

    private fun <T> DataFrame<T>.colorByLastName(): FormattedFrame<T> {
        val lastNameHappyCol = this["true"]["name"]["lastName"]
        val lastNameNotHappyCol = this["false"]["name"]["lastName"]
        return format().perRowCol { row, col ->
            val topKey = col.path().firstOrNull()
            when (topKey) {
                "true" -> {
                    val lastName = lastNameHappyCol[row.index()] as String
                    background(lastNameToColor(lastName)) and textColor(black)
                }

                "false" -> {
                    val lastName = lastNameNotHappyCol[row.index()] as String
                    background(lastNameToColor(lastName)) and textColor(black)
                }

                else -> null
            }
        }
    }

    @Test
    fun df() {
        // SampleStart
        df
            // SampleEnd
            .format().perRowCol { row, _ ->
                val isHappy = df[row.index()].isHappy
                background(isHappyToColor(isHappy)) and textColor(black)
            }
            .saveDfHtmlSample()
    }

    @Test
    fun pivot_properties() {
        // SampleStart
        df.pivot { isHappy }
            // SampleEnd
            .frames()
            .toDataFrame()
            .convert { "true"() }.with {
                (it as DataFrame<Any?>).format().perRowCol { _, _ ->
                    background(isHappyToColor(true)) and textColor(black)
                }
            }
            .convert { "false"() }.with {
                (it as DataFrame<Any?>).format().perRowCol { _, _ ->
                    background(isHappyToColor(false)) and textColor(black)
                }
            }
            .defaultHeaderFormatting { "true"() and "false"() }
            .saveDfHtmlSample()
    }

    @Test
    fun pivot_strings() {
        // SampleStart
        df.pivot("isHappy")
        // SampleEnd
    }

    @Test
    fun pivotInward_properties() {
        // SampleStart
        df.pivot(inward = true) { isHappy }
            // SampleEnd
            .frames()
            .toDataFrame()
            .convert { "isHappy"["true"] }.with {
                (it as DataFrame<Any?>).format().perRowCol { _, _ ->
                    background(isHappyToColor(true)) and textColor(black)
                }
            }
            .convert { "isHappy"["false"] }.with {
                (it as DataFrame<Any?>).format().perRowCol { _, _ ->
                    background(isHappyToColor(false)) and textColor(black)
                }
            }
            .defaultHeaderFormatting { "isHappy"() }
            .saveDfHtmlSample()
    }

    @Test
    fun pivotInward_strings() {
        // SampleStart
        df.pivot("isHappy", inward = true)
        // SampleEnd
    }

    @Test
    fun pivotAnd_properties() {
        // SampleStart
        df.pivot { isHappy and name.firstName }
            // SampleEnd
            .frames()
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotAnd_strings() {
        // SampleStart
        df.pivot { "isHappy" and "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun pivotThen_properties() {
        // SampleStart
        df.pivot { isHappy then name.firstName }
            // SampleEnd
            .frames()
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotThen_strings() {
        // SampleStart
        df.pivot { "isHappy" then "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun pivotReducing_properties() {
        // SampleStart
        df.pivot { isHappy }
            // SampleEnd
            .frames()
            .toDataFrame()
            .convert { "true"() }.with {
                val lastNameCol = (it as DataFrame<Any?>)["name"]["lastName"]
                it.format().perRowCol { row, _ ->
                    val lastName = lastNameCol[row.index()] as String
                    background(lastNameToColor(lastName)) and textColor(black)
                }
            }
            .convert { "false"() }.with {
                val lastNameCol = (it as DataFrame<Any?>)["name"]["lastName"]
                it.format().perRowCol { row, _ ->
                    val lastName = lastNameCol[row.index()] as String
                    background(lastNameToColor(lastName)) and textColor(black)
                }
            }
            .defaultHeaderFormatting { "true"() and "false"() }
            .saveDfHtmlSample()
    }

    @Test
    fun pivotReducing_strings() {
        // SampleStart
        df.pivot("isHappy")
        // SampleEnd
    }

    // region first / last

    @Test
    fun pivotFirst_properties() {
        // SampleStart
        df.pivot { isHappy }.first()
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotFirst_strings() {
        // SampleStart
        df.pivot("isHappy").first()
        // SampleEnd
    }

    @Test
    fun pivotFirstWithPredicate_properties() {
        // SampleStart
        df.pivot { isHappy }.first { age == 30 }
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotFirstWithPredicate_strings() {
        // SampleStart
        df.pivot("isHappy").first { "age"<Int>() == 30 }
        // SampleEnd
    }

    @Test
    fun pivotLast_properties() {
        // SampleStart
        df.pivot { isHappy }.last()
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotLast_strings() {
        // SampleStart
        df.pivot("isHappy").last()
        // SampleEnd
    }

    @Test
    fun pivotLastWithPredicate_properties() {
        // SampleStart
        df.pivot { isHappy }.last { age > 20 }
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotLastWithPredicate_strings() {
        // SampleStart
        df.pivot("isHappy").last { "age"<Int>() > 20 }
        // SampleEnd
    }

    // endregion

    // region minBy / maxBy

    @Test
    fun pivotMinBy_properties() {
        // SampleStart
        df.pivot { isHappy }.minBy { weight }
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMinBy_strings() {
        // SampleStart
        df.pivot("isHappy").minBy { "weight"<Int>() }
        // SampleEnd
    }

    @Test
    fun pivotMaxBy_properties() {
        // SampleStart
        df.pivot { isHappy }.maxBy { age }
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMaxBy_strings() {
        // SampleStart
        df.pivot("isHappy").maxBy { "age"<Int>() }
        // SampleEnd
    }

    // endregion

    // region medianBy / percentileBy

    @Test
    fun pivotMedianBy_properties() {
        // SampleStart
        df.pivot { isHappy }.medianBy { weight }
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMedianBy_strings() {
        // SampleStart
        df.pivot("isHappy").medianBy { "weight"<Int>() }
        // SampleEnd
    }

    @Test
    fun pivotPercentileBy_properties() {
        // SampleStart
        df.pivot { isHappy }.percentileBy(25.0) { weight }
            // SampleEnd
            .values()
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotPercentileBy_strings() {
        // SampleStart
        df.pivot("isHappy").percentileBy(25.0) { "weight"<Int>() }
        // SampleEnd
    }

    // endregion

    // region values, with

    @Test
    fun pivotValues_properties() {
        // SampleStart
        df.pivot { isHappy }.first().values()
            // SampleEnd
            .toDataFrame()
            .colorByLastName()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotValues_strings() {
        // SampleStart
        df.pivot("isHappy").first().values()
        // SampleEnd
    }

    @Test
    fun pivotWith_properties() {
        // SampleStart
        df.pivot { isHappy }
            .maxBy { age }
            .with { name.firstName + " " + name.lastName }
            // SampleEnd
            .toDataFrame()
            .format().perRowCol { row, col ->
                val fullName = row[col.name()] as String
                val lastName = fullName.substringAfterLast(" ")
                background(lastNameToColor(lastName)) and textColor(black)
            }
            .defaultHeaderFormatting { "true"() and "false"() }
            .saveDfHtmlSample()
    }

    @Test
    fun pivotWith_strings() {
        // SampleStart
        df.pivot("isHappy")
            .maxBy("age")
            .with { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
        // SampleEnd
    }

    // endregion

    // region frames

    @Test
    fun pivotFrames_properties() {
        // SampleStart
        df.pivot { isHappy }.frames()
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotFrames_strings() {
        // SampleStart
        df.pivot("isHappy").frames()
        // SampleEnd
    }

    // endregion

    // region values (aggregate)

    @Test
    fun pivotValuesAggregate_properties() {
        // SampleStart
        df.pivot { isHappy }.values { name and age }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotValuesAggregate_strings() {
        // SampleStart
        df.pivot("isHappy").values("name", "age")
        // SampleEnd
    }

    @Test
    fun pivotValuesAggregateAll_properties() {
        // SampleStart
        df.pivot { isHappy }.values()
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotValuesAggregateAll_strings() {
        // SampleStart
        df.pivot("isHappy").values()
        // SampleEnd
    }

    // endregion

    // region count (aggregate)

    @Test
    fun pivotCount_properties() {
        // SampleStart
        df.pivot { isHappy }.count()
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotCount_strings() {
        // SampleStart
        df.pivot("isHappy").count()
        // SampleEnd
    }

    // endregion

    // region with (aggregate, without reducer)

    @Test
    fun withOnPivot_properties() {
        // SampleStart
        df.pivot { isHappy }.with { name.lastName }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun withOnPivot_strings() {
        // SampleStart
        df.pivot("isHappy").with { "name"["lastName"]<String>() }
        // SampleEnd
    }

    // endregion

    // region aggregate

    @Test
    fun pivotAggregateMultiple_properties() {
        // SampleStart
        df.pivot { isHappy }.aggregate {
            count() into "total"
            count { age >= 18 } into "adults"
            median { age } into "median age"
            min { age } into "min age"
            maxBy { age }.name into "oldest"
        }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotAggregateMultiple_strings() {
        // SampleStart
        df.pivot("isHappy").aggregate {
            count() into "total"
            count { "age"<Int>() > 18 } into "adults"
            median("age") into "median age"
            min("age") into "min age"
            maxBy("age")["name"] into "oldest"
        }
        // SampleEnd
    }

    @Test
    fun pivotAggregate_properties() {
        // SampleStart
        df.pivot { isHappy }.aggregate { minBy { age }.name }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotAggregate_strings() {
        // SampleStart
        df.pivot("isHappy").aggregate { minBy("age")["name"] }
        // SampleEnd
    }

    // endregion

    // region statistics

    @Test
    fun pivotMax_properties() {
        // SampleStart
        df.pivot { isHappy }.max { age }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMax_strings() {
        // SampleStart
        df.pivot("isHappy").max("age")
        // SampleEnd
    }

    @Test
    fun pivotMaxMultiple_properties() {
        // SampleStart
        df.pivot { isHappy }.max { age and weight }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMaxMultiple_strings() {
        // SampleStart
        df.pivot("isHappy").max("age", "weight")
        // SampleEnd
    }

    @Test
    fun pivotMaxForMultiple_properties() {
        // SampleStart
        df.pivot { isHappy }.maxFor { age and weight }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMaxForMultiple_strings() {
        // SampleStart
        df.pivot("isHappy").maxFor("age", "weight")
        // SampleEnd
    }

    @Test
    fun pivotMaxOf_properties() {
        // SampleStart
        df.pivot { isHappy }.maxOf { if (age < 30) weight else null }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMaxOf_strings() {
        // SampleStart
        df.pivot("isHappy").maxOf { if ("age"<Int>() < 30) "weight"<Int>() else null }
        // SampleEnd
    }

    @Test
    fun pivotMin_properties() {
        // SampleStart
        df.pivot { isHappy }.min { age }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMin_strings() {
        // SampleStart
        df.pivot("isHappy").min("age")
        // SampleEnd
    }

    @Test
    fun pivotSum_properties() {
        // SampleStart
        df.pivot { isHappy }.sum { weight }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotSum_strings() {
        // SampleStart
        df.pivot("isHappy").sum("weight")
        // SampleEnd
    }

    @Test
    fun pivotMeanAll_properties() {
        // SampleStart
        df.pivot { isHappy }.mean()
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMeanAll_strings() {
        // SampleStart
        df.pivot("isHappy").mean()
        // SampleEnd
    }

    @Test
    fun pivotMean_properties() {
        // SampleStart
        df.pivot { isHappy }.mean { age }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMean_strings() {
        // SampleStart
        df.pivot("isHappy").mean { "age"<Int>() }
        // SampleEnd
    }

    @Test
    fun pivotStd_properties() {
        // SampleStart
        df.pivot { isHappy }.std { age }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotStd_strings() {
        // SampleStart
        df.pivot("isHappy").std { "age"<Int>() }
        // SampleEnd
    }

    @Test
    fun pivotMedian_properties() {
        // SampleStart
        df.pivot { isHappy }.median { age }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMedian_strings() {
        // SampleStart
        df.pivot("isHappy").median("age")
        // SampleEnd
    }

    @Test
    fun pivotPercentile_properties() {
        // SampleStart
        df.pivot { isHappy }.percentile(25.0) { age }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotPercentile_strings() {
        // SampleStart
        df.pivot("isHappy").percentile(25.0) { "age"<Int>() }
        // SampleEnd
    }

    // endregion

    // region separate

    @Test
    fun pivotSeparate_properties() {
        // SampleStart
        df.pivot { isHappy }.maxFor(separate = true) { age and weight }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotSeparate_strings() {
        // SampleStart
        df.pivot("isHappy").maxFor("age", "weight", separate = true)
        // SampleEnd
    }

    @Test
    fun pivotAggregateSeparate_properties() {
        // SampleStart
        df.pivot { isHappy }.aggregate(separate = true) {
            min { age } into "min age"
            maxOrNull { weight } into "max weight"
        }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotAggregateSeparate_strings() {
        // SampleStart
        df.pivot("isHappy").aggregate(separate = true) {
            min("age") into "min age"
            maxOrNull("weight") into "max weight"
        }
        // SampleEnd
    }

    // endregion

    // region default

    @Test
    fun pivotDefault_properties() {
        // SampleStart
        df.pivot { isHappy }.groupBy { city }.aggregate { min { age } default 0 }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotDefault_strings() {
        // SampleStart
        df.pivot("isHappy").groupBy("city").aggregate { min("age") default 0 }
        // SampleEnd
    }

    @Test
    fun pivotDefaultDirect_properties() {
        // SampleStart
        df.pivot { isHappy }.groupBy { city }.default(0).min()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotDefaultDirect_strings() {
        // SampleStart
        df.pivot("isHappy").groupBy("city").default(0).min()
        // SampleEnd
    }

    @Test
    fun pivotDefaultMultiple_properties() {
        // SampleStart
        df.pivot { isHappy }.groupBy { city }.aggregate {
            count() into "people" default 0
            any { age < 18 } into "hasMinors" default false
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotDefaultMultiple_strings() {
        // SampleStart
        df.pivot("isHappy").groupBy("city").aggregate {
            count() into "people" default 0
            any { "age"<Int>() < 18 } into "hasMinors" default false
        }
        // SampleEnd
    }

    // endregion

    // region pivot inside aggregate

    @Test
    fun pivotInAggregate_properties() {
        // SampleStart
        df.groupBy { name.firstName }.aggregate {
            pivot { isHappy }.aggregate(separate = true) {
                mean { age } into "mean age"
                count() into "count"
            }
            count() into "total"
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotInAggregate_strings() {
        // SampleStart
        df.groupBy { "name"["firstName"] }.aggregate {
            pivot("isHappy").aggregate(separate = true) {
                mean("age") into "mean age"
                count() into "count"
            }
            count() into "total"
        }
        // SampleEnd
    }

    // endregion

    // region pivotCounts

    @Test
    fun pivotCountsOnDf_properties() {
        // SampleStart
        df.pivotCounts { isHappy }
        // same as
        df.pivot { isHappy }.groupByOther().count()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotCountsOnDf_strings() {
        // SampleStart
        df.pivotCounts("isHappy")
        // same as
        df.pivot("isHappy").groupByOther().count()
        // SampleEnd
    }

    @Test
    fun pivotCountsOnGroupBy_properties() {
        // SampleStart
        df.groupBy { name }.pivotCounts { city }
        // same as
        df.groupBy { name }.pivot { city }.count()
        // same as
        df.groupBy { name }.aggregate {
            pivotCounts { city }
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotCountsOnGroupBy_strings() {
        // SampleStart
        df.groupBy("name").pivotCounts("city")
        // same as
        df.groupBy("name").pivot("city").count()
        // same as
        df.groupBy("name").aggregate {
            pivotCounts("city")
        }
        // SampleEnd
    }

    // endregion

    // region pivotMatches

    @Test
    fun pivotMatchesGroupByOther_properties() {
        // SampleStart
        df.pivotMatches { city }
        // same as
        df.pivot { city }.groupByOther().matches()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMatchesGroupByOther_strings() {
        // SampleStart
        df.pivotMatches("city")
        // same as
        df.pivot("city").groupByOther().matches()
        // SampleEnd
    }

    @Test
    fun pivotMatches_properties() {
        // SampleStart
        df.groupBy { name }.pivotMatches { city }
        // same as
        df.groupBy { name }.pivot { city }.matches()
        // same as
        df.groupBy { name }.aggregate {
            pivotMatches { city }
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun pivotMatches_strings() {
        // SampleStart
        df.groupBy("name").pivotMatches("city")
        // same as
        df.groupBy("name").pivot("city").matches()
        // same as
        df.groupBy("name").aggregate {
            pivotMatches("city")
        }
        // SampleEnd
    }

    // endregion

    // region pivot + groupBy

    @Test
    fun pivotGroupBy_properties() {
        // SampleStart
        df.pivot { isHappy }.groupBy { name.firstName }
        // same as
        df.groupBy { name.firstName }.pivot { isHappy }
            // SampleEnd
            .frames()
            .defaultHeaderFormatting { "firstName"<String>() }
            .saveDfHtmlSample()
    }

    @Test
    fun pivotGroupBy_strings() {
        // SampleStart
        df.pivot("isHappy").groupBy { "name"["firstName"] }
        // same as
        df.groupBy { "name"["firstName"] }.pivot("isHappy")
        // SampleEnd
    }

    @Test
    fun pivotGroupByOther_properties() {
        // SampleStart
        df.pivot { isHappy }.groupByOther()
            // SampleEnd
            .frames()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotGroupByOther_strings() {
        // SampleStart
        df.pivot("isHappy").groupByOther()
        // SampleEnd
    }

    @Test
    fun pivotGroupByAggregation_properties() {
        // SampleStart
        df.groupBy { name.firstName }.pivot { isHappy }.max { age }
            // SampleEnd
            .defaultHeaderFormatting { "firstName"<String>() }
            .saveDfHtmlSample()
    }

    @Test
    fun pivotGroupByAggregation_strings() {
        // SampleStart
        df.groupBy { "name"["firstName"] }.pivot("isHappy").max("age")
        // SampleEnd
    }

    @Test
    fun pivotGroupByFrames_properties() {
        // SampleStart
        df.pivot { isHappy }.groupBy { name.firstName }.frames()
            // SampleEnd
            .defaultHeaderFormatting { "firstName"<String>() }
            .saveDfHtmlSample()
    }

    @Test
    fun pivotGroupByFrames_strings() {
        // SampleStart
        df.pivot("isHappy").groupBy { "name"["firstName"] }.frames()
        // SampleEnd
    }

    // endregion
}
