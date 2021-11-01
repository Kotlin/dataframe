package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.asComparable
import org.jetbrains.kotlinx.dataframe.api.asGroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.div
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.groupBy
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
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.stdFor
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.api.sumFor
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataRow
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.get
import org.junit.Test

class Analyze : TestBase() {

    @Test
    fun basicInfo() {
        // SampleStart
        df.nrow()
        df.ncol()
        df.columnNames()
        df.schema()
        df.describe()
        // SampleEnd
    }

    @Test
    fun count() {
        // SampleStart
        df.count { age > 15 }
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
    fun dataFrameToGrouped() {
        // SampleStart
        val key by columnOf(1, 2) // create int column with name "key"
        val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
        val df = dataFrameOf(key, data) // create dataframe with two columns

        df.asGroupedDataFrame { data } // convert dataframe to GroupedDataFrame by interpreting 'data' column as groups
        // SampleEnd
    }

    @Test
    fun groupedDataFrameToFrame() {
        // SampleStart
        df.groupBy { city }.toDataFrame()
        // SampleEnd
    }

    @Test
    fun groupByAggregations_properties() {
        // SampleStart
        df.groupBy { city }.aggregate {
            nrow() into "total"
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
            nrow() into "total"
            count { age() > 18 } into "adults"
            median { age } into "median age"
            min { age } into "min age"
            maxBy { age() }[name] into "name of oldest"
        }
        // or
        df.groupBy(city).aggregate {
            nrow() into "total"
            count { age > 18 } into "adults"
            median(age) into "median age"
            min(age) into "min age"
            maxBy(age)[name] into "name of oldest"
        }
        // SampleEnd
    }

    @Test
    fun groupByAggregations_strings() {
        // SampleStart
        df.groupBy("city").aggregate {
            nrow() into "total"
            count { "age"<Int>() > 18 } into "adults"
            median("age") into "median age"
            min("age") into "min age"
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
    fun groupByUnion() {
        // SampleStart
        df.groupBy { city }.union()
        // SampleEnd
    }

    @Test
    fun pivot_properties() {
        // SampleStart
        df.pivot { city }
        df.pivot { city and name.firstName }
        // SampleEnd
    }

    @Test
    fun pivot_accessors() {
        // SampleStart
        val city by column<String?>()
        val name by columnGroup()
        val firstName by name.column<String>()

        df.pivot { city }
        df.pivot { city and firstName }
        // SampleEnd
    }

    @Test
    fun pivot_strings() {
        // SampleStart
        df.pivot("city")
        df.pivot { "city" and "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun pivotAsDataRowOrFrame() {
        // SampleStart
        df.pivot { city }.toDataRow()
        df.pivot { city }.groupBy { name }.toDataFrame()
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
            maxByOrNull("weight")?.get("name")?.get("lastName") into "biggest"
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
}
