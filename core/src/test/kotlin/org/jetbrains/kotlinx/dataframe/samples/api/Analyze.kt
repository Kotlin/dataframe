@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.asComparable
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxFor
import org.jetbrains.kotlinx.dataframe.api.maxOf
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
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileBy
import org.jetbrains.kotlinx.dataframe.api.percentileFor
import org.jetbrains.kotlinx.dataframe.api.percentileOf
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.stdFor
import org.jetbrains.kotlinx.dataframe.api.stdOf
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.valueCounts
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
        df.min() // min of values for every comparable column with mutually comparable values
        df.min { age and weight } // min of all values in `age` and `weight`
        df.minFor(skipNaN = true) { age and name.firstName } // min of values per `age` and `firstName` separately
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
        df.median() // median of values for every column with mutually comparable values
        df.median { age and weight } // median of all values in `age` and `weight`
        df.medianFor(skipNaN = true) { age and name.firstName } // median of values per `age` and `firstName` separately
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
        df.percentile(25.0) // 25th percentile of values for every column with mutually comparable values
        df.percentile(75.0) { age and weight } // 75th percentile of all values in `age` and `weight`
        df.percentileFor(50.0, skipNaN = true) { age and name.firstName } // 50th percentile of values per `age` and `firstName` separately
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
        df.maxFor { name.firstName and age }
        df.sumFor { age and weight }
        df.meanFor { cols(1, 3).asNumbers() }
        df.medianFor { name.allCols().asComparable() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnsFor_strings() {
        // SampleStart
        df.minFor { colsOf<Int>() }
        df.maxFor { "name"["firstName"].asComparable() and "age"<Int>() }

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
