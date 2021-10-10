package docs.api

import org.jetbrains.dataframe.asComparable
import org.jetbrains.dataframe.asGrouped
import org.jetbrains.dataframe.asNumbers
import org.jetbrains.dataframe.column
import org.jetbrains.dataframe.columnGroup
import org.jetbrains.dataframe.columnOf
import org.jetbrains.dataframe.count
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.div
import org.jetbrains.dataframe.expr
import org.jetbrains.dataframe.groupBy
import org.jetbrains.dataframe.max
import org.jetbrains.dataframe.maxBy
import org.jetbrains.dataframe.maxFor
import org.jetbrains.dataframe.maxOf
import org.jetbrains.dataframe.mean
import org.jetbrains.dataframe.meanFor
import org.jetbrains.dataframe.meanOf
import org.jetbrains.dataframe.median
import org.jetbrains.dataframe.medianFor
import org.jetbrains.dataframe.medianOf
import org.jetbrains.dataframe.min
import org.jetbrains.dataframe.minFor
import org.jetbrains.dataframe.minOf
import org.jetbrains.dataframe.pivot
import org.jetbrains.dataframe.schema
import org.jetbrains.dataframe.sum
import org.jetbrains.dataframe.sumFor
import org.jetbrains.dataframe.sumOf
import org.jetbrains.dataframe.values
import org.junit.Test

class Analyze : TestBase() {

    @Test
    fun basicInfo() {
        // SampleStart
        df.nrow() // number of rows
        df.ncol() // number of columns
        df.schema() // schema of columns
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
        df.sum { "age"().asNumbers() and "weight"().asNumbers() }

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
        df.sumFor { "age"().asNumbers() and "weight"().asNumbers() }

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
        df.groupBy { "city"() and "name"["lastName"] }
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

        df.asGrouped { data } // convert dataframe to GroupedDataFrame by interpreting 'data' column as groups
        // SampleEnd
    }

    @Test
    fun groupedDataFrameToFrame() {
        // SampleStart
        df.groupBy { city }.asDataFrame()
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
        df.groupBy { city }.max { name.firstName.length() and name.lastName.length() } // maximum length of firstName or lastName into column "max"
        df.groupBy { city }.medianFor { age and weight } // median age into column "age", median weight into column "weight"
        df.groupBy { city }.minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
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
        df.groupBy { city }.max { firstName.length() and lastName.length() } // maximum length of firstName or lastName into column "max"
        df.groupBy { city }.medianFor { age and weight } // median age into column "age", median weight into column "weight"
        df.groupBy { city }.minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
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
        df.groupBy("city").max { "name"["firstName"].strings().length() and "name"["lastName"].strings().length() } // maximum length of firstName or lastName into column "max"
        df.groupBy("city").medianFor("age", "weight") // median age into column "age", median weight into column "weight"
        df.groupBy("city").minFor { ("age".ints() into "min age") and ("weight".intOrNulls() into "min weight") } // min age into column "min age", min weight into column "min weight"
        df.groupBy("city").meanOf("mean ratio") { "weight".intOrNull()?.div("age".int()) } // mean of weight/age into column "mean ratio"
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
        df.groupBy("city").values { "weight"() into "weights" }
        // SampleEnd
    }

    @Test
    fun groupByUnion() {
        // SampleStart
        df.groupBy { city }.union()
        // SampleEnd
    }

    @Test
    fun pivot() {
        // SampleStart
        df.pivot { city }
        // SampleEnd
    }
}
