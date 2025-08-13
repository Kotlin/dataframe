@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.asComparable
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.div
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.groupByOther
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.length
import org.jetbrains.kotlinx.dataframe.api.matches
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.maxByOrNull
import org.jetbrains.kotlinx.dataframe.api.maxFor
import org.jetbrains.kotlinx.dataframe.api.maxOf
import org.jetbrains.kotlinx.dataframe.api.maxOrNull
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanFor
import org.jetbrains.kotlinx.dataframe.api.meanOf
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.medianOf
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.minFor
import org.jetbrains.kotlinx.dataframe.api.minOf
import org.jetbrains.kotlinx.dataframe.api.minOrNull
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.api.percentileFor
import org.jetbrains.kotlinx.dataframe.api.percentileOf
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.pivotCounts
import org.jetbrains.kotlinx.dataframe.api.pivotMatches
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.stdFor
import org.jetbrains.kotlinx.dataframe.api.stdOf
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.junit.Test
import kotlin.math.ln

class Analyze : TestBase() {

    @Test
    @TransformDataFrameExpressions
    fun basicInfo() {
        // SampleStart
        df.count() // same as df.rowsCount()
        df.indices() // 0 until count()
        df.columnsCount()
        df.columnNames()
        df.head()
        df.schema()
        df.describe()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun head() {
        // SampleStart
        df.head(3)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun schema() {
        // SampleStart
        df.schema()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun schemaGroupBy() {
        // SampleStart
        df.groupBy { city }.schema()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun describe() {
        // SampleStart
        df.describe()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun describeColumns_properties() {
        // SampleStart
        df.describe { age and name.allCols() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun describeColumns_strings() {
        // SampleStart
        df.describe { "age" and "name".allCols() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun countCondition() {
        // SampleStart
        df.count { age > 15 }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun count() {
        // SampleStart
        df.count()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun countAggregation() {
        // SampleStart
        df.groupBy { city }.count()
        df.pivot { city }.count { age > 18 }
        df.pivot { name.firstName }.groupBy { name.lastName }.count()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun sumAggregations() {
        // SampleStart
        df.age.sum()
        df.groupBy { city }.sum()
        df.pivot { city }.sum()
        df.pivot { city }.groupBy { name.lastName }.sum()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticModes() {
        // SampleStart
        df.sum() // sum of values per every numeric column
        df.sum { age and weight } // sum of all values in `age` and `weight`
        df.sumFor(skipNaN = true) { age and weight } // sum of values per `age` and `weight` separately
        df.sumOf { (weight ?: 0) / age } // sum of expression evaluated for every row
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun minmaxModes() {
        // SampleStart
        df.min() // min of values per every comparable column
        df.min { age and weight } // min of all values in `age` and `weight`
        df.minFor(skipNaN = true) { age and weight } // min of values per `age` and `weight` separately
        df.minOf { (weight ?: 0) / age } // min of expression evaluated for every row
        df.minBy { age } // DataRow with minimal `age`
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun minmaxAggregations() {
        // SampleStart
        df.min()
        df.age.min()
        df.groupBy { city }.min()
        df.pivot { city }.min()
        df.pivot { city }.groupBy { name.lastName }.min()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun medianModes() {
        // SampleStart
        df.median() // median of values per every comparable column
        df.median { age and weight } // median of all values in `age` and `weight`
        df.medianFor(skipNaN = true) { age and weight } // median of values per `age` and `weight` separately
        df.medianOf { (weight ?: 0) / age } // median of expression evaluated for every row
        df.medianBy { age } // DataRow where the median age lies (lower-median for an even number of values)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun medianAggregations() {
        // SampleStart
        df.median()
        df.age.median()
        df.groupBy { city }.median()
        df.pivot { city }.median()
        df.pivot { city }.groupBy { name.lastName }.median()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun percentileModes() {
        // SampleStart
        df.percentile(25.0) // 25th percentile of values per every comparable column
        df.percentile(75.0) { age and weight } // 75th percentile of all values in `age` and `weight`
        df.percentileFor(50.0, skipNaN = true) { age and weight } // 50th percentile of values per `age` and `weight` separately
        df.percentileOf(75.0) { (weight ?: 0) / age } // 75th percentile of expression evaluated for every row
        df.percentileBy(25.0) { age } // DataRow where the 25th percentile of `age` lies (index rounded using R3)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun percentileAggregations() {
        // SampleStart
        df.percentile(25.0)
        df.age.percentile(75.0)
        df.groupBy { city }.percentile(50.0)
        df.pivot { city }.percentile(75.0)
        df.pivot { city }.groupBy { name.lastName }.percentile(25.0)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun meanModes() {
        // SampleStart
        df.mean() // mean of values per every numeric column
        df.mean { age and weight } // mean of all values in `age` and `weight`
        df.meanFor(skipNaN = true) { age and weight } // mean of values per `age` and `weight` separately, skips NaN
        df.meanOf { (weight ?: 0) / age } // median of expression evaluated for every row
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun meanAggregations() {
        // SampleStart
        df.mean()
        df.age.mean()
        df.groupBy { city }.mean()
        df.pivot { city }.mean()
        df.pivot { city }.groupBy { name.lastName }.mean()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun stdModes() {
        // SampleStart
        df.std() // std of values per every numeric column
        df.std { age and weight } // std of all values in `age` and `weight`
        df.stdFor(skipNaN = true) { age and weight } // std of values per `age` and `weight` separately, skips NA
        df.stdOf { (weight ?: 0) / age } // std of expression evaluated for every row
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun stdAggregations() {
        // SampleStart
        df.std()
        df.age.std()
        df.groupBy { city }.std()
        df.pivot { city }.std()
        df.pivot { city }.groupBy { name.lastName }.std()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun meanAggregationsSkipNA() {
        // SampleStart
        df.mean(skipNaN = true)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticAggregations() {
        // SampleStart
        df.mean()
        df.age.sum()
        df.groupBy { city }.mean()
        df.pivot { city }.median()
        df.pivot { city }.groupBy { name.lastName }.std()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticGroupBySingle() {
        // SampleStart
        df.groupBy { city }.mean { age } // [`city`, `mean`]
        df.groupBy { city }.meanOf { age / 2 } // [`city`, `mean`]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticGroupBySingleNamed() {
        // SampleStart
        df.groupBy { city }.mean("mean age") { age } // [`city`, `mean age`]
        df.groupBy { city }.meanOf("custom") { age / 2 } // [`city`, `custom`]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticGroupByMany() {
        // SampleStart
        df.groupBy { city }.meanFor { age and weight } // [`city`, `age`, `weight`]
        df.groupBy { city }.mean() // [`city`, `age`, `weight`, ...]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticPivotSingle_properties() {
        // SampleStart
        df.groupBy { city }.pivot { name.lastName }.mean { age }
        df.groupBy { city }.pivot { name.lastName }.meanOf { age / 2.0 }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticPivotSingle_strings() {
        // SampleStart
        df.groupBy("city").pivot { "name"["lastName"] }.mean("age")
        df.groupBy("city").pivot { "name"["lastName"] }.meanOf { "age"<Int>() / 2.0 }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticPivotMany() {
        // SampleStart
        df.groupBy { city }.pivot { name.lastName }.meanFor { age and weight }
        df.groupBy { city }.pivot { name.lastName }.mean()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun statisticPivotManySeparate() {
        // SampleStart
        df.groupBy { city }.pivot { name.lastName }.meanFor(separate = true) { age and weight }
        df.groupBy { city }.pivot { name.lastName }.mean(separate = true)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnStats_properties() {
        // SampleStart
        df.sum { weight }
        df.min { age }
        df.mean { age }
        df.median { age }

        df.weight.sum()
        df.age.max()
        df.age.mean()
        df.age.median()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnStats_strings() {
        // SampleStart
        df.sum("weight")
        df.min("age")
        df.mean("age")
        df.median("age")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun multipleColumnsStat_properties() {
        // SampleStart
        df.min { colsOf<Int>() }
        df.max { name.firstName and name.lastName }
        df.sum { age and weight }
        df.mean { cols(1, 3).asNumbers() }
        df.median<_, String> { name.allCols().cast() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun multipleColumnsStat_strings() {
        // SampleStart

        df.min { colsOf<Int>() }

        df.max { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

        df.sum("age", "weight")
        // or
        df.sum { "age"<Int>() and "weight"<Int?>() }

        df.mean { cols(1, 3).asNumbers() }
        df.median<_, String> { name.allCols().cast() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnsFor_properties() {
        // SampleStart
        df.minFor { colsOf<Int>() }
        df.maxFor { name.firstName and name.lastName }
        df.sumFor { age and weight }
        df.meanFor { cols(1, 3).asNumbers() }
        df.medianFor { name.allCols().asComparable() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnsFor_aсcessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()
        val age by column<Int>()
        val weight by column<Int?>()

        df.minFor { colsOf<Int>() }

        df.maxFor { firstName and lastName }
        // or
        df.maxFor(firstName, lastName)

        df.sumFor { age and weight }
        // or
        df.sum(age, weight)

        df.mean { cols(1, 3).asNumbers() }
        df.median<_, String> { name.allCols().cast() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnsFor_strings() {
        // SampleStart
        df.minFor { colsOf<Int>() }
        df.maxFor { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

        df.sumFor("age", "weight")
        // or
        df.sumFor { "age"<Int>() and "weight"<Int?>() }

        df.meanFor { cols(1, 3).asNumbers() }
        df.medianFor { name.allCols().asComparable() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun ofExpressions_properties() {
        // SampleStart
        df.minOf { 2021 - age }
        df.maxOf { name.firstName.length + name.lastName.length }
        df.sumOf { weight?.let { it - 50 } }
        df.meanOf { ln(age.toDouble()) }
        df.medianOf { city?.length }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun ofExpressions_strings() {
        // SampleStart
        df.minOf { 2021 - "age"<Int>() }
        df.maxOf { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length }
        df.sumOf { "weight"<Int?>()?.let { it - 50 } }
        df.meanOf { ln("age"<Int>().toDouble()) }
        df.medianOf { "city"<String?>()?.length }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupBy_properties() {
        // SampleStart
        df.groupBy { name }
        df.groupBy { city and name.lastName }
        df.groupBy { age / 10 named "ageDecade" }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupBy_strings() {
        // SampleStart
        df.groupBy("name")
        df.groupBy { "city" and "name"["lastName"] }
        df.groupBy { "age"<Int>() / 10 named "ageDecade" }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByExpr_properties() {
        // SampleStart
        df.groupBy { expr { name.firstName.length + name.lastName.length } named "nameLength" }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByExpr_strings() {
        // SampleStart
        df.groupBy { expr { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length } named "nameLength" }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByMoveToTop() {
        // SampleStart
        df.groupBy(moveToTop = true) { name.lastName }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByMoveToTopFalse() {
        // SampleStart
        df.groupBy(moveToTop = false) { name.lastName }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun dataFrameToGroupBy() {
        // SampleStart
        val key by columnOf(1, 2) // create int column with name "key"
        val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
        val df = dataFrameOf(key, data) // create dataframe with two columns

        df.asGroupBy { data } // convert dataframe to GroupBy by interpreting 'data' column as groups
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByToFrame() {
        // SampleStart
        df.groupBy { city }.toDataFrame()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByAggregations_properties() {
        // SampleStart
        df.groupBy { city }.aggregate {
            count() into "total"
            count { age > 18 } into "adults"
            median { age } into "median age"
            min { age } into "min age"
            maxBy { age }.name into "oldest"
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByAggregations_strings() {
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
    @TransformDataFrameExpressions
    fun groupByAggregateWithoutInto_properties() {
        // SampleStart
        df.groupBy { city }.aggregate { maxBy { age }.name }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByAggregateWithoutInto_strings() {
        // SampleStart
        df.groupBy("city").aggregate { maxBy("age")["name"] }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByDirectAggregations_properties() {
        // SampleStart
        df.groupBy { city }.max() // max for every comparable column
        df.groupBy { city }.mean() // mean for every numeric column
        df.groupBy { city }.max { age } // max age into column "age"
        df.groupBy { city }.sum("total weight") { weight } // sum of weights into column "total weight"
        df.groupBy { city }.count() // number of rows into column "count"
        df.groupBy { city }
            .max { name.firstName.length() and name.lastName.length() } // maximum length of firstName or lastName into column "max"
        df.groupBy { city }
            .medianFor { age and weight } // median age into column "age", median weight into column "weight"
        df.groupBy { city }
            .minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
        df.groupBy { city }.meanOf("mean ratio") { weight?.div(age) } // mean of weight/age into column "mean ratio"
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByDirectAggregations_strings() {
        // SampleStart
        df.groupBy("city").max() // max for every comparable column
        df.groupBy("city").mean() // mean for every numeric column
        df.groupBy("city").max("age") // max age into column "age"
        df.groupBy("city").sum("weight", name = "total weight") // sum of weights into column "total weight"
        df.groupBy("city").count() // number of rows into column "count"
        df.groupBy("city").max {
            "name"["firstName"]<String>().length() and "name"["lastName"]<String>().length()
        } // maximum length of firstName or lastName into column "max"
        df.groupBy("city")
            .medianFor("age", "weight") // median age into column "age", median weight into column "weight"
        df.groupBy("city")
            .minFor { ("age"<Int>() into "min age") and ("weight"<Int?>() into "min weight") } // min age into column "min age", min weight into column "min weight"
        df.groupBy("city").meanOf("mean ratio") {
            "weight"<Int?>()?.div("age"<Int>())
        } // mean of weight/age into column "mean ratio"
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByWithoutAggregation_properties() {
        // SampleStart
        df.groupBy { city }.values()
        df.groupBy { city }.values { name and age }
        df.groupBy { city }.values { weight into "weights" }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun groupByWithoutAggregation_strings() {
        // SampleStart
        df.groupBy("city").values()
        df.groupBy("city").values("name", "age")
        df.groupBy("city").values { "weight" into "weights" }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivot_properties() {
        // SampleStart
        df.pivot { city }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivot_strings() {
        // SampleStart
        df.pivot("city")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivot2_properties() {
        // SampleStart
        df.pivot { city and name.firstName }
        df.pivot { city then name.firstName }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivot2_strings() {
        // SampleStart
        df.pivot { "city" and "name"["firstName"] }
        df.pivot { "city" then "name"["firstName"] }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotInward_properties() {
        // SampleStart
        df.pivot(inward = true) { city }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotInward_strings() {
        // SampleStart
        df.pivot("city", inward = true)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotAsDataRowOrFrame() {
        // SampleStart
        df.pivot { city }.frames()
        df.pivot { city }.groupBy { name }.frames()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotGroupBy_properties() {
        // SampleStart
        df.pivot { city }.groupBy { name }
        // same as
        df.groupBy { name }.pivot { city }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotGroupBy_strings() {
        // SampleStart
        df.pivot("city").groupBy("name")
        // same as
        df.groupBy("name").pivot("city")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotGroupByOther() {
        // SampleStart
        df.pivot { city }.groupByOther()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotAggregate_properties() {
        // SampleStart
        df.pivot { city }.aggregate { minBy { age }.name }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotAggregate1_properties() {
        // SampleStart
        df.pivot { city }.groupBy { name.firstName }.aggregate {
            meanFor { age and weight } into "means"
            stdFor { age and weight } into "stds"
            maxByOrNull { weight }?.name?.lastName into "biggest"
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotAggregate_strings() {
        // SampleStart
        df.pivot("city").aggregate { minBy("age")["name"] }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotAggregate1_strings() {
        // SampleStart
        df.pivot("city").groupBy { "name"["firstName"] }.aggregate {
            meanFor("age", "weight") into "means"
            stdFor("age", "weight") into "stds"
            maxByOrNull("weight")?.getColumnGroup("name")?.get("lastName") into "biggest"
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotCommonAggregations_properties() {
        // SampleStart
        df.pivot { city }.maxFor { age and weight }
        df.groupBy { name }.pivot { city }.median { age }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotCommonAggregations_strings() {
        // SampleStart
        df.pivot("city").maxFor("age", "weight")
        df.groupBy("name").pivot("city").median("age")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotSeparate_properties() {
        // SampleStart
        df.pivot { city }.maxFor(separate = true) { age and weight }
        df.pivot { city }.aggregate(separate = true) {
            min { age } into "min age"
            maxOrNull { weight } into "max weight"
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotSeparate_strings() {
        // SampleStart
        df.pivot("city").maxFor("age", "weight", separate = true)
        df.pivot("city").aggregate(separate = true) {
            min("age") into "min age"
            maxOrNull("weight") into "max weight"
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotDefault_properties() {
        // SampleStart
        df.pivot { city }.groupBy { name }.aggregate { min { age } default 0 }
        df.pivot { city }.groupBy { name }.default(0).min()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotDefault1_properties() {
        // SampleStart
        df.pivot { city }.groupBy { name }.aggregate {
            median { age } into "median age" default 0
            minOrNull { weight } into "min weight" default 100
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotDefault_strings() {
        // SampleStart
        df.pivot("city").groupBy("name").aggregate { min("age") default 0 }
        df.pivot("city").groupBy("name").default(0).min()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotDefault1_strings() {
        // SampleStart
        df.pivot("city").groupBy("name").aggregate {
            median("age") into "median age" default 0
            minOrNull("weight") into "min weight" default 100
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotInAggregate_properties() {
        // SampleStart
        df.groupBy { name.firstName }.aggregate {
            pivot { city }.aggregate(separate = true) {
                mean { age } into "mean age"
                count() into "count"
            }
            count() into "total"
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotInAggregate_strings() {
        // SampleStart
        df.groupBy { "name"["firstName"] }.aggregate {
            pivot("city").aggregate(separate = true) {
                mean("age") into "mean age"
                count() into "count"
            }
            count() into "total"
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotCounts() {
        // SampleStart
        df.pivotCounts { city }
        // same as
        df.pivot { city }.groupByOther().count()

        df.groupBy { name }.pivotCounts { city }
        // same as
        df.groupBy { name }.pivot { city }.count()
        // same as
        df.groupBy { name }.aggregate {
            pivotCounts { city }
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun pivotMatches() {
        // SampleStart
        df.pivotMatches { city }
        // same as
        df.pivot { city }.groupByOther().matches()

        df.groupBy { name }.pivotMatches { city }
        // same as
        df.groupBy { name }.pivot { city }.matches()
        // same as
        df.groupBy { name }.aggregate {
            pivotMatches { city }
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun cumSum() {
        // SampleStart
        df.cumSum { weight }
        df.weight.cumSum()
        df.groupBy { city }.cumSum { weight }.concat()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun valueCounts() {
        // SampleStart
        df.city.valueCounts()

        df.valueCounts { name and city }
        // SampleEnd
    }
}
