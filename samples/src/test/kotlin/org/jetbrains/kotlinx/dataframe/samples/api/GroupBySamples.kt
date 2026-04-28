package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.concatWithKeys
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.div
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.last
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.maxFor
import org.jetbrains.kotlinx.dataframe.api.maxOf
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanOf
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.minFor
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByCount
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sortByGroup
import org.jetbrains.kotlinx.dataframe.api.sortByKey
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

class GroupBySamples : DataFrameSampleHelper("groupBy", "api") {
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
            "Chaplin" -> RgbColor(210, 229, 199)
            "Daniels" -> RgbColor(191, 223, 232)
            "Cooper" -> RgbColor(200, 200, 232)
            "Dylan" -> RgbColor(233, 199, 220)
            "Wolf" -> RgbColor(232, 220, 220)
            else -> RgbColor(255, 255, 255)
        }

    private fun <T> DataFrame<T>.colorByLastName(): FormattedFrame<T> {
        val lastNameCol = this["name"]["lastName"]
        return format().perRowCol { row, _ ->
            val lastName = lastNameCol[row.index()] as String
            val color = lastNameToColor(lastName)
            background(color) and textColor(black)
        }
    }

    // region df and groupBy

    @Test
    fun groupByDf() {
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
    fun groupByDfGrouped_properties() {
        // SampleStart
        df.groupBy { isHappy }
            // SampleEnd
            .toDataFrame()
            .convert { group }.with {
                it.format().perRowCol { _, _ ->
                    background(isHappyToColor(isHappy)) and textColor(black)
                }
            }
            .format().perRowCol { row, _ ->
                val color = isHappyToColor(row.isHappy)
                background(color) and textColor(black)
            }
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByDfGrouped_strings() {
        // SampleStart
        df.groupBy("isHappy")
        // SampleEnd
    }

    @Test
    fun groupByTwoColumns_properties() {
        // SampleStart
        df.groupBy { name.firstName and isHappy }
            // SampleEnd
            .toDataFrame()
            .defaultHeaderFormatting { firstName and isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByTwoColumns_strings() {
        // SampleStart
        df.groupBy { "name"["firstName"]<String>() and "isHappy" }
        // SampleEnd
    }

    @Test
    fun groupByNewColumn_properties() {
        // SampleStart
        df.groupBy { age / 10 named "ageDecade" }
            // SampleEnd
            .toDataFrame()
            .defaultHeaderFormatting { "ageDecade"<Int>() }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByNewColumn_strings() {
        // SampleStart
        df.groupBy { "age"<Int>() / 10 named "ageDecade" }
        // SampleEnd
    }

    @Test
    fun groupByExpr_properties() {
        // SampleStart
        df.groupBy { expr { name.firstName.length + name.lastName.length } named "nameLength" }
            // SampleEnd
            .toDataFrame()
            .defaultHeaderFormatting { nameLength }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByExpr_strings() {
        // SampleStart
        df.groupBy {
            expr { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length } named
                "nameLength"
        }
        // SampleEnd
    }

    @Test
    fun groupByMoveToTop_properties() {
        // SampleStart
        df.groupBy(moveToTop = true) { name.firstName }
            // SampleEnd
            .toDataFrame()
            .defaultHeaderFormatting { firstName }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByMoveToTop_strings() {
        // SampleStart
        df.groupBy(moveToTop = true) { "name"["firstName"]<String>() }
        // SampleEnd
    }

    @Test
    fun groupByMoveToTopFalse_properties() {
        // SampleStart
        df.groupBy(moveToTop = false) { name.firstName }
            // SampleEnd
            .toDataFrame()
            .defaultHeaderFormatting { name.firstName }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByMoveToTopFalse_strings() {
        // SampleStart
        df.groupBy(moveToTop = false) { "name"["firstName"]<String>() }
        // SampleEnd
    }

    @Test
    fun dataFrameToGroupBy_properties() {
        // SampleStart
        val df = dataFrameOf(
            "key" to columnOf(1, 2),
            "data" to columnOf(df[0..3], df[4..6]),
        ) // create dataframe with two columns

        df.asGroupBy { data } // convert dataframe to GroupBy by interpreting 'data' column as groups
        // SampleEnd
    }

    @Test
    fun dataFrameToGroupBy_strings() {
        // SampleStart
        val df = dataFrameOf(
            "key" to columnOf(1, 2),
            "data" to columnOf(df[0..3], df[4..6]),
        ) // create dataframe with two columns

        df.asGroupBy("data") // convert dataframe to GroupBy by interpreting 'data' column as groups
        // SampleEnd
    }

    // endregion

    // region transformation

    @Test
    fun sortByOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.sortBy { age }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun sortByOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").sortBy("age")
        // SampleEnd
    }

    @Test
    fun sortByGroupOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.sortByGroup { mean { age } }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun sortByGroupOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").sortByGroup { mean("age") }
        // SampleEnd
    }

    @Test
    fun sortByCountOnGroupBy_properties() {
        // SampleStart
        df.groupBy { age }.sortByCount()
            // SampleEnd
            .toDataFrame()
            .defaultHeaderFormatting { group }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByCountOnGroupBy_strings() {
        // SampleStart
        df.groupBy("age").sortByCount()
        // SampleEnd
    }

    @Test
    fun sortByKeyOnGroupBy_properties() {
        // SampleStart
        df.groupBy { age }.sortByKey()
            // SampleEnd
            .toDataFrame()
            .defaultHeaderFormatting { age }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByKeyOnGroupBy_strings() {
        // SampleStart
        df.groupBy { age }.sortByKey()
        // SampleEnd
    }

    @Test
    fun updateGroupsOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.updateGroups { sortByDesc { age }.take(2) }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun updateGroupsOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").updateGroups { sortByDesc("age").take(2) }
        // SampleEnd
    }

    @Test
    fun filterOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.filter { group.median { age } > 20 }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun filterOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").filter { group.median { "age"<Int>() } > 20 }
        // SampleEnd
    }

    @Test
    fun addOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.add("isAdult") { age >= 18 }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun addOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").add("isAdult") { "age"<Int>() >= 18 }
        // SampleEnd
    }

    // endregion

    // region reducing (step 1)

    @Test
    fun groupByDfGroupedReducing_properties() {
        // SampleStart
        df.groupBy { isHappy }
            // SampleEnd
            .toDataFrame()
            .convert { group }.with { group ->
                val lastNameCol = group["name"]["lastName"]
                group.format().perRowCol { row, _ ->
                    val lastName = lastNameCol[row.index()] as String
                    background(lastNameToColor(lastName)) and textColor(black)
                }
            }
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByDfGroupedReducing_strings() {
        // SampleStart
        df.groupBy("isHappy")
        // SampleEnd
    }

    @Test
    fun groupByFirst_properties() {
        // SampleStart
        df.groupBy { isHappy }.first { age == 30 }
            // SampleEnd
            .values()
            .colorByLastName()
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByFirst_strings() {
        // SampleStart
        df.groupBy("isHappy").first { it["age"] == 30 }
        // SampleEnd
    }

    @Test
    fun groupByLast_properties() {
        // SampleStart
        df.groupBy { isHappy }.last { weight == null }
            // SampleEnd
            .values()
            .colorByLastName()
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByLast_strings() {
        // SampleStart
        df.groupBy("isHappy").last { it["weight"] == null }
        // SampleEnd
    }

    @Test
    fun groupByMinBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.minBy { weight }
            // SampleEnd
            .values()
            .colorByLastName()
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByMinBy_strings() {
        // SampleStart
        df.groupBy("isHappy").minBy("weight")
        // SampleEnd
    }

    @Test
    fun groupByMaxBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.maxBy { age }
            // SampleEnd
            .values()
            .colorByLastName()
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByMaxBy_strings() {
        // SampleStart
        df.groupBy("isHappy").maxBy("age")
        // SampleEnd
    }

    @Test
    fun groupByMedianBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.medianBy { weight }
            // SampleEnd
            .values()
            .colorByLastName()
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByMedianBy_strings() {
        // SampleStart
        df.groupBy("isHappy").medianBy("weight")
        // SampleEnd
    }

    @Test
    fun groupByPercentileBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.percentileBy(25.0) { weight }
            // SampleEnd
            .values()
            .colorByLastName()
            .defaultHeaderFormatting { isHappy }
            .saveDfHtmlSample()
    }

    @Test
    fun groupByPercentileBy_strings() {
        // SampleStart
        df.groupBy("isHappy").percentileBy(25.0, "weight")
        // SampleEnd
    }

    // endregion

    // region reducing (step 2)

    @Test
    fun groupByConcat_properties() {
        // SampleStart
        df.groupBy { isHappy }.minBy { age }.concat()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun groupByConcat_strings() {
        // SampleStart
        df.groupBy("isHappy").minBy("age").concat()
        // SampleEnd
    }

    @Test
    fun groupByValues_properties() {
        // SampleStart
        df.groupBy { isHappy }.minBy { age }.values { name and age and city }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun groupByValues_strings() {
        // SampleStart
        df.groupBy("isHappy").minBy("age").values("name", "age", "city")
        // SampleEnd
    }

    @Test
    fun groupByInto_properties() {
        // SampleStart
        df.groupBy { isHappy }.minBy { age }.into("youngest") { name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun groupByInto_strings() {
        // SampleStart
        df.groupBy("isHappy").minBy("age").into("youngest") { getColumnGroup("name") }
        // SampleEnd
    }

    // endregion

    // region aggregation

    @Test
    fun concatOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.concat()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun concatOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").concat()
        // SampleEnd
    }

    @Test
    fun toDfOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.toDataFrame()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun toDfOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").toDataFrame()
        // SampleEnd
    }

    @Test
    fun concatWithKeysOnGroupBy_properties() {
        // SampleStart
        df.groupBy { expr { age >= 18 } named "isAdult" }.concatWithKeys()
            // SampleEnd
            .defaultHeaderFormatting { isAdult }
            .saveDfHtmlSample()
    }

    @Test
    fun concatWithKeysOnGroupBy_strings() {
        // SampleStart
        df.groupBy { expr { "age"<Int>() >= 18 } named "isAdult" }.concatWithKeys()
        // SampleEnd
    }

    @Test
    fun intoOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.into("ages") { age }
            // SampleEnd
            .defaultHeaderFormatting { "ages"<List<String>>() }
            .saveDfHtmlSample()
    }

    @Test
    fun intoOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").into("ages") { "age"<Int>() }
        // SampleEnd
    }

    @Test
    fun valuesOnGroupBySelectedColumns_properties() {
        // SampleStart
        df.groupBy { isHappy }.values { name and age }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun valuesOnGroupBySelectedColumns_strings() {
        // SampleStart
        df.groupBy("isHappy").values("name", "age")
        // SampleEnd
    }

    @Test
    fun valuesOnGroupByAllColumns_properties() {
        // SampleStart
        df.groupBy { isHappy }.values()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun valuesOnGroupByAllColumns_strings() {
        // SampleStart
        df.groupBy("isHappy").values()
        // SampleEnd
    }

    @Test
    fun valuesOnGroupByRenameColumns_properties() {
        // SampleStart
        df.groupBy { isHappy }.values { age into "ages" }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun valuesOnGroupByRenameColumns_strings() {
        // SampleStart
        df.groupBy("isHappy").values { "age" into "ages" }
        // SampleEnd
    }

    @Test
    fun countOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.count()
            // SampleEnd
            .defaultHeaderFormatting { count }
            .saveDfHtmlSample()
    }

    @Test
    fun countOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").count()
        // SampleEnd
    }

    @Test
    fun aggregateOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.aggregate {
            count() into "total"
            count { age > 18 } into "adults"
            median { age } into "median age"
            min { age } into "min age"
            maxBy { age }.name into "oldest"
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun aggregateOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").aggregate {
            count() into "total"
            count { "age"<Int>() > 18 } into "adults"
            median("age") into "median age"
            min("age") into "min age"
            maxBy("age")["name"] into "oldest"
        }
        // or
        df.groupBy("city").aggregate {
            count() into "total"
            count { "age"<Int>() > 18 } into "adults"
            "age"<Int>().median() into "median age"
            "age"<Int>().min() into "min age"
            maxBy("age")["name"] into "oldest"
        }
        // SampleEnd
    }

    @Test
    fun aggregateOnGroupByWithoutInto_properties() {
        // SampleStart
        df.groupBy { city }.aggregate { maxBy { age }.name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun aggregateOnGroupByWithoutInto_strings() {
        // SampleStart
        df.groupBy("city").aggregate { maxBy("age")["name"] }
        // SampleEnd
    }

    // endregion

    // region aggregation statistics

    @Test
    fun maxOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.max() // max for every column with mutually comparable values
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun maxOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").max() // max for every column with mutually comparable values
        // SampleEnd
    }

    @Test
    fun maxSelectedOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.max { age and weight }
            // SampleEnd
            .defaultHeaderFormatting { "max"<Int>() }
            .saveDfHtmlSample()
    }

    @Test
    fun maxSelectedOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").max("age", "weight")
        // SampleEnd
    }

    @Test
    fun maxForOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.maxFor { age and weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun maxForOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").maxFor("age", "weight")
        // SampleEnd
    }

    @Test
    fun maxOfOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.maxOf { if (age < 30) weight else null }
            // SampleEnd
            .defaultHeaderFormatting { max }
            .saveDfHtmlSample()
    }

    @Test
    fun maxOfOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").maxOf { if ("age"<Int>() < 30) "weight"<Int>() else null }
        // SampleEnd
    }

    @Test
    fun maxOnGroupByNameLength_properties() {
        // SampleStart
        df.groupBy { city }
            .max {
                name.firstName.map {
                    it.length
                } and name.lastName.map { it.length }
            } // maximum length of firstName or lastName into column "max"
            // SampleEnd
            .defaultHeaderFormatting { "max"<Int>() }
            .saveDfHtmlSample()
    }

    @Test
    fun maxOnGroupByNameLength_strings() {
        // SampleStart
        df.groupBy("city").max {
            "name"["firstName"]<String>().map { it.length } and "name"["lastName"]<String>().map { it.length }
        } // maximum length of firstName or lastName into column "max"
        // SampleEnd
    }

    @Test
    fun minOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.min { age }
            // SampleEnd
            .defaultHeaderFormatting { "age"<Int>() }
            .saveDfHtmlSample()
    }

    @Test
    fun minOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").min("age")
        // SampleEnd
    }

    @Test
    fun minForOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }
            .minFor {
                (age into "minAge") and (weight into "minWeight")
            } // min age into column "min age", min weight into column "min weight"
            // SampleEnd
            .defaultHeaderFormatting { minAge and minWeight }
            .saveDfHtmlSample()
    }

    @Test
    fun minForOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city")
            .minFor {
                ("age"<Int>() into "minAge") and ("weight"<Int?>() into "minWeight")
            } // min age into column "min age", min weight into column "min weight"
        // SampleEnd
    }

    @Test
    fun sumOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.sum("totalWeight") { weight } // sum of weights into column "total weight"
            // SampleEnd
            .defaultHeaderFormatting { "totalWeight"<Int?>() }
            .saveDfHtmlSample()
    }

    @Test
    fun sumOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").sum("weight", name = "totalWeight") // sum of weights into column "total weight"
        // SampleEnd
    }

    @Test
    fun meanOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.mean() // mean for every numeric column
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun meanOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").mean() // mean for every numeric column
        // SampleEnd
    }

    @Test
    fun meanOfOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.meanOf("meanRatio") { weight?.div(age) } // mean of weight/age into column "mean ratio"
            // SampleEnd
            .defaultHeaderFormatting { meanRatio }
            .saveDfHtmlSample()
    }

    @Test
    fun meanOfOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").meanOf("meanRatio") {
            "weight"<Int?>()?.div("age"<Int>())
        } // mean of weight/age into column "mean ratio"
        // SampleEnd
    }

    @Test
    fun stdOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.std { age }
            // SampleEnd
            .defaultHeaderFormatting { "age"() }
            .saveDfHtmlSample()
    }

    @Test
    fun stdOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").std("age")
        // SampleEnd
    }

    @Test
    fun medianOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.median { age }
            // SampleEnd
            .defaultHeaderFormatting { "age"() }
            .saveDfHtmlSample()
    }

    @Test
    fun medianOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").median("age")
        // SampleEnd
    }

    @Test
    fun medianForOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }
            .medianFor { age and weight } // median age into column "age", median weight into column "weight"
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun medianForOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city")
            .medianFor("age", "weight") // median age into column "age", median weight into column "weight"
        // SampleEnd
    }

    @Test
    fun percentileOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.percentile(25.0) { age }
            // SampleEnd
            .defaultHeaderFormatting { "age"() }
            .saveDfHtmlSample()
    }

    @Test
    fun percentileOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").percentile(25.0) { "age"<Int>() }
        // SampleEnd
    }

    // endregion

    // region pivot + groupBy

    @Test
    fun pivotOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.pivot { name.firstName }
            // SampleEnd
            .frames()
            .saveDfHtmlSample()
    }

    @Test
    fun pivotOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").pivot { "name"["firstName"]<String>() }
        // SampleEnd
    }

    // endregion
}
