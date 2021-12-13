package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.asComparable
import org.jetbrains.kotlinx.dataframe.api.asGroupBy
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.columnOf
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
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.medianOf
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.minFor
import org.jetbrains.kotlinx.dataframe.api.minOf
import org.jetbrains.kotlinx.dataframe.api.minOrNull
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
import org.junit.Test

class Analyze : TestBase() {

    @Test
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
    fun head() {
        // SampleStart
        df.head(3)
        // SampleEnd
    }

    @Test
    fun schema() {
        // SampleStart
        df.schema()
        // SampleEnd
    }

    @Test
    fun schemaGroupBy() {
        // SampleStart
        df.groupBy { city }.schema()
        // SampleEnd
    }

    @Test
    fun describe() {
        // SampleStart
        df.describe()
        // SampleEnd
    }

    @Test
    fun describeColumns_properties() {
        // SampleStart
        df.describe { age and name.all() }
        // SampleEnd
    }

    @Test
    fun describeColumns_accessors() {
        // SampleStart
        val age by column<Int>()
        val name by columnGroup()

        df.describe { age and name.all() }
        // SampleEnd
    }

    @Test
    fun describeColumns_strings() {
        // SampleStart
        df.describe { "age" and "name".all() }
        // SampleEnd
    }

    @Test
    fun countCondition() {
        // SampleStart
        df.count { age > 15 }
        // SampleEnd
    }

    @Test
    fun count() {
        // SampleStart
        df.count()
        // SampleEnd
    }

    @Test
    fun countAggregation() {
        // SampleStart
        df.groupBy { city }.count()
        df.pivot { city }.count { age > 18 }
        df.pivot { name.firstName }.groupBy { name.lastName }.count()
        // SampleEnd
    }

    @Test
    fun sumAggregations() {
        // SampleStart
        df.age.sum()
        df.groupBy { city }.sum()
        df.pivot { city }.sum()
        df.pivot { city }.groupBy { name.lastName }.sum()
        // SampleEnd
    }

    @Test
    fun statisticModes() {
        // SampleStart
        df.sum() // sum of values per every numeric column
        df.sum { age and weight } // sum of all values in `age` and `weight`
        df.sumFor { age and weight } // sum of values per `age` and `weight` separately
        df.sumOf { (weight ?: 0) / age } // sum of expression evaluated for every row
        // SampleEnd
    }

    @Test
    fun minmaxModes() {
        // SampleStart
        df.min() // min of values per every comparable column
        df.min { age and weight } // min of all values in `age` and `weight`
        df.minFor { age and weight } // min of values per `age` and `weight` separately
        df.minOf { (weight ?: 0) / age } // min of expression evaluated for every row
        df.minBy { age } // DataRow with minimal `age`
        // SampleEnd
    }

    @Test
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
    fun medianModes() {
        // SampleStart
        df.median() // median of values per every comparable column
        df.median { age and weight } // median of all values in `age` and `weight`
        df.medianFor { age and weight } // median of values per `age` and `weight` separately
        df.medianOf { (weight ?: 0) / age } // median of expression evaluated for every row
        // SampleEnd
    }

    @Test
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
    fun meanModes() {
        // SampleStart
        df.mean() // mean of values per every numeric column
        df.mean(skipNA = true) { age and weight } // mean of all values in `age` and `weight`, skips NA
        df.meanFor(skipNA = true) { age and weight } // mean of values per `age` and `weight` separately, skips NA
        df.meanOf { (weight ?: 0) / age } // median of expression evaluated for every row
        // SampleEnd
    }

    @Test
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
    fun stdModes() {
        // SampleStart
        df.std() // std of values per every numeric column
        df.std { age and weight } // std of all values in `age` and `weight`
        df.stdFor { age and weight } // std of values per `age` and `weight` separately, skips NA
        df.stdOf { (weight ?: 0) / age } // std of expression evaluated for every row
        // SampleEnd
    }

    @Test
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
    fun meanAggregationsSkipNA() {
        // SampleStart
        df.mean(skipNA = true)
        // SampleEnd
    }

    @Test
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
    fun statisticGroupBySingle() {
        // SampleStart
        df.groupBy { city }.mean { age } // [`city`, `mean`]
        df.groupBy { city }.meanOf { age / 2 } // [`city`, `mean`]
        // SampleEnd
    }

    @Test
    fun statisticGroupBySingleNamed() {
        // SampleStart
        df.groupBy { city }.mean("mean age") { age } // [`city`, `mean age`]
        df.groupBy { city }.meanOf("custom") { age / 2 } // [`city`, `custom`]
        // SampleEnd
    }

    @Test
    fun statisticGroupByMany() {
        // SampleStart
        df.groupBy { city }.meanFor { age and weight } // [`city`, `age`, `weight`]
        df.groupBy { city }.mean() // [`city`, `age`, `weight`, ...]
        // SampleEnd
    }

    @Test
    fun statisticPivotSingle_properties() {
        // SampleStart
        df.groupBy { city }.pivot { name.lastName }.mean { age }
        df.groupBy { city }.pivot { name.lastName }.meanOf { age / 2.0 }
        // SampleEnd
    }

    @Test
    fun statisticPivotSingle_accessors() {
        // SampleStart
        val city by column<String?>()
        val age by column<Int>()
        val name by columnGroup()
        val lastName by name.column<String>()

        df.groupBy { city }.pivot { lastName }.mean { age }
        df.groupBy { city }.pivot { lastName }.meanOf { age() / 2.0 }
        // SampleEnd
    }

    @Test
    fun statisticPivotSingle_strings() {
        // SampleStart
        df.groupBy("city").pivot { "name"["lastName"] }.mean("age")
        df.groupBy("city").pivot { "name"["lastName"] }.meanOf { "age"<Int>() / 2.0 }
        // SampleEnd
    }

    @Test
    fun statisticPivotMany() {
        // SampleStart
        df.groupBy { city }.pivot { name.lastName }.meanFor { age and weight }
        df.groupBy { city }.pivot { name.lastName }.mean()
        // SampleEnd
    }

    @Test
    fun statisticPivotManySeparate() {
        // SampleStart
        df.groupBy { city }.pivot { name.lastName }.meanFor(separate = true) { age and weight }
        df.groupBy { city }.pivot { name.lastName }.mean(separate = true)
        // SampleEnd
    }

    @Test
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
    fun columnStats_accessors() {
        // SampleStart
        val weight by column<Int?>()
        val age by column<Int>()

        df.sum { weight }
        df.min { age }
        df.mean { age }
        df.median { age }

        df.sum(weight)
        df.min(age)
        df.mean(age)
        df.median(age)

        df[weight].sum()
        df[age].mean()
        df[age].min()
        df[age].median()
        // SampleEnd
    }

    @Test
    fun columnStats_strings() {
        // SampleStart
        df.sum("weight")
        df.min("age")
        df.mean("age")
        df.median("age")
        // SampleEnd
    }

    @Test
    fun multipleColumnsStat_properties() {
        // SampleStart
        df.min { intCols() }
        df.max { name.firstName and name.lastName }
        df.sum { age and weight }
        df.mean { cols(1, 3).asNumbers() }
        df.median { name.cols().asComparable() }
        // SampleEnd
    }

    @Test
    fun multipleColumnsStat_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()
        val age by column<Int>()
        val weight by column<Int?>()

        df.min { intCols() }

        df.max { firstName and lastName }
        // or
        df.max(firstName, lastName)

        df.sum { age and weight }
        // or
        df.sum(age, weight)

        df.mean { cols(1, 3).asNumbers() }
        df.median { name.cols().asComparable() }
        // SampleEnd
    }

    @Test
    fun multipleColumnsStat_strings() {
        // SampleStart

        df.min { intCols() }

        df.max { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

        df.sum("age", "weight")
        // or
        df.sum { "age"<Int>() and "weight"<Int?>() }

        df.mean { cols(1, 3).asNumbers() }
        df.median { name.cols().asComparable() }
        // SampleEnd
    }

    @Test
    fun columnsFor_properties() {
        // SampleStart
        df.minFor { intCols() }
        df.maxFor { name.firstName and name.lastName }
        df.sumFor { age and weight }
        df.meanFor { cols(1, 3).asNumbers() }
        df.medianFor { name.cols().asComparable() }
        // SampleEnd
    }

    @Test
    fun columnsFor_aсcessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()
        val age by column<Int>()
        val weight by column<Int?>()

        df.minFor { intCols() }

        df.maxFor { firstName and lastName }
        // or
        df.maxFor(firstName, lastName)

        df.sumFor { age and weight }
        // or
        df.sum(age, weight)

        df.mean { cols(1, 3).asNumbers() }
        df.median { name.cols().asComparable() }
        // SampleEnd
    }

    @Test
    fun columnsFor_strings() {
        // SampleStart
        df.minFor { intCols() }
        df.maxFor { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

        df.sumFor("age", "weight")
        // or
        df.sumFor { "age"<Int>() and "weight"<Int?>() }

        df.meanFor { cols(1, 3).asNumbers() }
        df.medianFor { name.cols().asComparable() }
        // SampleEnd
    }

    @Test
    fun ofExpressions_properties() {
        // SampleStart
        df.minOf { 2021 - age }
        df.maxOf { name.firstName.length + name.lastName.length }
        df.sumOf { weight?.let { it - 50 } }
        df.meanOf { Math.log(age.toDouble()) }
        df.medianOf { city?.length }
        // SampleEnd
    }

    @Test
    fun ofExpressions_accessors() {
        // SampleStart
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()
        val age by column<Int>()
        val weight by column<Int?>()
        val city by column<String?>()

        df.minOf { 2021 - age() }
        df.maxOf { firstName().length + lastName().length }
        df.sumOf { weight()?.let { it - 50 } }
        df.meanOf { Math.log(age().toDouble()) }
        df.medianOf { city()?.length }
        // SampleEnd
    }

    @Test
    fun ofExpressions_strings() {
        // SampleStart
        df.minOf { 2021 - "age"<Int>() }
        df.maxOf { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length }
        df.sumOf { "weight"<Int?>()?.let { it - 50 } }
        df.meanOf { Math.log("age"<Int>().toDouble()) }
        df.medianOf { "city"<String?>()?.length }
        // SampleEnd
    }

    @Test
    fun groupBy_properties() {
        // SampleStart
        df.groupBy { name }
        df.groupBy { city and name.lastName }
        df.groupBy { age / 10 named "ageDecade" }
        df.groupBy { expr { name.firstName.length + name.lastName.length } named "nameLength" }
        // SampleEnd
    }

    @Test
    fun groupBy_accessors() {
        // SampleStart
        val name by columnGroup()
        val lastName by name.column<String>()
        val firstName by name.column<String>()
        val age by column<Int>()
        val city by column<String?>()

        df.groupBy { name }
        // or
        df.groupBy(name)

        df.groupBy { city and lastName }
        // or
        df.groupBy(city, lastName)

        df.groupBy { age / 10 named "ageDecade" }

        df.groupBy { expr { firstName().length + lastName().length } named "nameLength" }
        // SampleEnd
    }

    @Test
    fun groupBy_strings() {
        // SampleStart
        df.groupBy("name")
        df.groupBy { "city" and "name"["lastName"] }
        df.groupBy { "age".ints() / 10 named "ageDecade" }
        df.groupBy { expr { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length } named "nameLength" }
        // SampleEnd
    }

    @Test
    fun dataFrameToGroupBy() {
        // SampleStart
        val key by columnOf(1, 2) // create int column with name "key"
        val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
        val df = dataFrameOf(key, data) // create dataframe with two columns

        df.asGroupBy { data } // convert dataframe to GroupBy by interpreting 'data' column as groups
        // SampleEnd
    }

    @Test
    fun groupByToFrame() {
        // SampleStart
        df.groupBy { city }.toDataFrame()
        // SampleEnd
    }

    @Test
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
    fun groupByAggregations_accessors() {
        // SampleStart
        val city by column<String?>()
        val age by column<Int>()
        val name by columnGroup()

        df.groupBy { city }.aggregate {
            count() into "total"
            count { age() > 18 } into "adults"
            median { age } into "median age"
            min { age } into "min age"
            maxBy { age() }[name] into "name of oldest"
        }
        // or
        df.groupBy(city).aggregate {
            count() into "total"
            count { age > 18 } into "adults"
            median(age) into "median age"
            min(age) into "min age"
            maxBy(age)[name] into "name of oldest"
        }
        // or
        df.groupBy(city).aggregate {
            count() into "total"
            age().count { it > 18 } into "adults"
            age().median() into "median age"
            age().min() into "min age"
            maxBy(age)[name] into "name of oldest"
        }
        // SampleEnd
    }

    @Test
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
    fun groupByAggregateWithoutInto_properties() {
        // SampleStart
        df.groupBy { city }.aggregate { maxBy { age }.name }
        // SampleEnd
    }

    @Test
    fun groupByAggregateWithoutInto_accessors() {
        // SampleStart
        val city by column<String?>()
        val age by column<Int>()
        val name by columnGroup()

        df.groupBy { city }.aggregate { maxBy { age() }[name] }
        // or
        df.groupBy(city).aggregate { maxBy(age)[name] }
        // SampleEnd
    }

    @Test
    fun groupByAggregateWithoutInto_strings() {
        // SampleStart
        df.groupBy("city").aggregate { maxBy("age")["name"] }
        // SampleEnd
    }

    @Test
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
    fun groupByDirectAggregations_accessors() {
        // SampleStart
        val city by column<String?>()
        val age by column<Int>()
        val weight by column<Int?>()
        val name by columnGroup()
        val firstName by name.column<String>()
        val lastName by name.column<String>()

        df.groupBy { city }.max() // max for every comparable column
        df.groupBy { city }.mean() // mean for every numeric column
        df.groupBy { city }.max { age } // max age into column "age"
        df.groupBy { city }.sum("total weight") { weight } // sum of weights into column "total weight"
        df.groupBy { city }.count() // number of rows into column "count"
        df.groupBy { city }
            .max { firstName.length() and lastName.length() } // maximum length of firstName or lastName into column "max"
        df.groupBy { city }
            .medianFor { age and weight } // median age into column "age", median weight into column "weight"
        df.groupBy { city }
            .minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
        df.groupBy { city }.meanOf("mean ratio") { weight()?.div(age()) } // mean of weight/age into column "mean ratio"
        // SampleEnd
    }

    @Test
    fun groupByDirectAggregations_strings() {
        // SampleStart
        df.groupBy("city").max() // max for every comparable column
        df.groupBy("city").mean() // mean for every numeric column
        df.groupBy("city").max("age") // max age into column "age"
        df.groupBy("city").sum("weight", name = "total weight") // sum of weights into column "total weight"
        df.groupBy("city").count() // number of rows into column "count"
        df.groupBy("city").max {
            "name"["firstName"].strings().length() and "name"["lastName"].strings().length()
        } // maximum length of firstName or lastName into column "max"
        df.groupBy("city")
            .medianFor("age", "weight") // median age into column "age", median weight into column "weight"
        df.groupBy("city")
            .minFor { ("age".ints() into "min age") and ("weight".intOrNulls() into "min weight") } // min age into column "min age", min weight into column "min weight"
        df.groupBy("city").meanOf("mean ratio") {
            "weight".intOrNull()?.div("age".int())
        } // mean of weight/age into column "mean ratio"
        // SampleEnd
    }

    @Test
    fun groupByWithoutAggregation_properties() {
        // SampleStart
        df.groupBy { city }.values()
        df.groupBy { city }.values { name and age }
        df.groupBy { city }.values { weight into "weights" }
        // SampleEnd
    }

    @Test
    fun groupByWithoutAggregation_accessors() {
        // SampleStart
        val city by column<String?>()
        val age by column<Int>()
        val weight by column<Int?>()
        val name by columnGroup()

        df.groupBy(city).values()
        df.groupBy(city).values(name, age)
        df.groupBy(city).values { weight into "weights" }
        // SampleEnd
    }

    @Test
    fun groupByWithoutAggregation_strings() {
        // SampleStart
        df.groupBy("city").values()
        df.groupBy("city").values("name", "age")
        df.groupBy("city").values { "weight" into "weights" }
        // SampleEnd
    }

    @Test
    fun pivot_properties() {
        // SampleStart
        df.pivot { city }
        // SampleEnd
    }

    @Test
    fun pivot_accessors() {
        // SampleStart
        val city by column<String?>()

        df.pivot { city }
        // SampleEnd
    }

    @Test
    fun pivot_strings() {
        // SampleStart
        df.pivot("city")
        // SampleEnd
    }

    @Test
    fun pivot2_properties() {
        // SampleStart
        df.pivot { city and name.firstName }
        df.pivot { city then name.firstName }
        // SampleEnd
    }

    @Test
    fun pivot2_accessors() {
        // SampleStart
        val city by column<String?>()
        val name by columnGroup()
        val firstName by name.column<String>()

        df.pivot { city and firstName }
        df.pivot { city then firstName }
        // SampleEnd
    }

    @Test
    fun pivot2_strings() {
        // SampleStart
        df.pivot { "city" and "name"["firstName"] }
        df.pivot { "city" then "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun pivotInward_properties() {
        // SampleStart
        df.pivot(inward = true) { city }
        // SampleEnd
    }

    @Test
    fun pivotInward_accessors() {
        // SampleStart
        val city by column<String?>()

        df.pivot(inward = true) { city }
        // SampleEnd
    }

    @Test
    fun pivotInward_strings() {
        // SampleStart
        df.pivot("city", inward = true)
        // SampleEnd
    }

    @Test
    fun pivotAsDataRowOrFrame() {
        // SampleStart
        df.pivot { city }.frames()
        df.pivot { city }.groupBy { name }.frames()
        // SampleEnd
    }

    @Test
    fun pivotGroupBy_properties() {
        // SampleStart
        df.pivot { city }.groupBy { name }
        // same as
        df.groupBy { name }.pivot { city }
        // SampleEnd
    }

    @Test
    fun pivotGroupBy_accessors() {
        // SampleStart
        val city by column<String?>()
        val name by columnGroup()

        df.pivot { city }.groupBy { name }
        // same as
        df.groupBy { name }.pivot { city }
        // SampleEnd
    }

    @Test
    fun pivotGroupBy_strings() {
        // SampleStart
        df.pivot("city").groupBy("name")
        // same as
        df.groupBy("name").pivot("city")
        // SampleEnd
    }

    @Test
    fun pivotGroupByOther() {
        // SampleStart
        df.pivot { city }.groupByOther()
        // SampleEnd
    }

    @Test
    fun pivotAggregate_properties() {
        // SampleStart
        df.pivot { city }.aggregate { minBy { age }.name }
        df.pivot { city }.groupBy { name.firstName }.aggregate {
            meanFor { age and weight } into "means"
            stdFor { age and weight } into "stds"
            maxByOrNull { weight }?.name?.lastName into "biggest"
        }
        // SampleEnd
    }

    @Test
    fun pivotAggregate_accessors() {
        // SampleStart
        val city by column<String?>()
        val name by columnGroup()
        val firstName by name.column<String>()
        val age by column<Int>()
        val weight by column<Int?>()

        df.pivot { city }.aggregate { minBy(age).name }

        df.pivot { city }.groupBy { firstName }.aggregate {
            meanFor { age and weight } into "means"
            stdFor { age and weight } into "stds"
            maxByOrNull(weight)?.name?.lastName into "biggest"
        }
        // SampleEnd
    }

    @Test
    fun pivotAggregate_strings() {
        // SampleStart
        df.pivot("city").aggregate { minBy("age")["name"] }

        df.pivot("city").groupBy { "name"["firstName"] }.aggregate {
            meanFor("age", "weight") into "means"
            stdFor("age", "weight") into "stds"
            maxByOrNull("weight")?.getColumnGroup("name")?.get("lastName") into "biggest"
        }
        // SampleEnd
    }

    @Test
    fun pivotCommonAggregations_properties() {
        // SampleStart
        df.pivot { city }.maxFor { age and weight }
        df.groupBy { name }.pivot { city }.median { age }
        // SampleEnd
    }

    @Test
    fun pivotCommonAggregations_accessors() {
        // SampleStart
        val city by column<String?>()
        val name by columnGroup()
        val age by column<Int>()
        val weight by column<Int?>()

        df.pivot { city }.maxFor { age and weight }
        df.groupBy { name }.pivot { city }.median { age }
        // SampleEnd
    }

    @Test
    fun pivotCommonAggregations_strings() {
        // SampleStart
        df.pivot("city").maxFor("age", "weight")
        df.groupBy("name").pivot("city").median("age")
        // SampleEnd
    }

    @Test
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
    fun pivotSeparate_accessors() {
        // SampleStart
        val city by column<String?>()
        val age by column<Int>()
        val weight by column<Int?>()

        df.pivot { city }.maxFor(separate = true) { age and weight }
        df.pivot { city }.aggregate(separate = true) {
            min { age } into "min age"
            maxOrNull { weight } into "max weight"
        }
        // SampleEnd
    }

    @Test
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
    fun pivotDefault_properties() {
        // SampleStart
        df.pivot { city }.groupBy { name }.aggregate { min { age } default 0 }
        df.pivot { city }.groupBy { name }.aggregate {
            median { age } into "median age" default 0
            minOrNull { weight } into "min weight" default 100
        }
        df.pivot { city }.groupBy { name }.default(0).min()
        // SampleEnd
    }

    @Test
    fun pivotDefault_accessors() {
        // SampleStart
        val city by column<String?>()
        val age by column<Int>()
        val weight by column<Int?>()
        val name by columnGroup()

        df.pivot { city }.groupBy { name }.aggregate { min { age } default 0 }
        df.pivot { city }.groupBy { name }.aggregate {
            median { age } into "median age" default 0
            minOrNull { weight } into "min weight" default 100
        }
        df.pivot { city }.groupBy { name }.default(0).min()
        // SampleEnd
    }

    @Test
    fun pivotDefault_strings() {
        // SampleStart
        df.pivot("city").groupBy("name").aggregate { min("age") default 0 }
        df.pivot("city").groupBy("name").aggregate {
            median("age") into "median age" default 0
            minOrNull("weight") into "min weight" default 100
        }
        df.pivot("city").groupBy("name").default(0).min()
        // SampleEnd
    }

    @Test
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
    fun pivotInAggregate_accessors() {
        // SampleStart
        val city by column<String?>()
        val name by columnGroup()
        val firstName by name.column<String>()
        val age by column<Int>()

        df.groupBy { firstName }.aggregate {
            pivot { city }.aggregate(separate = true) {
                mean { age } into "mean age"
                count() into "count"
            }
            count() into "total"
        }
        // SampleEnd
    }

    @Test
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
    fun cumSum() {
        // SampleStart
        df.cumSum { weight }
        df.weight.cumSum()
        // SampleEnd
    }

    @Test
    fun valueCounts() {
        // SampleStart
        df.city.valueCounts()

        df.valueCounts { name and city }
        // SampleEnd
    }
}
