package docs.api

import org.jetbrains.dataframe.asComparable
import org.jetbrains.dataframe.asNumbers
import org.jetbrains.dataframe.column
import org.jetbrains.dataframe.columnGroup
import org.jetbrains.dataframe.max
import org.jetbrains.dataframe.mean
import org.jetbrains.dataframe.meanOf
import org.jetbrains.dataframe.median
import org.jetbrains.dataframe.medianOf
import org.jetbrains.dataframe.min
import org.jetbrains.dataframe.schema
import org.jetbrains.dataframe.sum
import org.jetbrains.dataframe.sumOf
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
    fun columnSum_properties() {
        df.sum { weight }
        df.weight.sum()
    }

    @Test
    fun columnSum_accessors() {
        val weight by column<Int?>()

        df.sum { weight }
        df.sum(weight)
        df[weight].sum()
    }

    @Test
    fun columnSum_strings() {
        df.sum("weight")
        df["weight"].asNumbers().sum()
        df.getColumn<Int?>("weight").sum()
    }

    @Test
    fun columnMinMax_properties() {
        // SampleStart
        df.min { age }
        df.age.min()
        // SampleEnd
    }

    @Test
    fun columnMinMax_accessors() {
        // SampleStart
        val age by column<Int>()

        df.min(age)
        df.min { age }
        df[age].min()
        // SampleEnd
    }

    @Test
    fun columnMinMax_strings() {
        // SampleStart
        df.min("age")
        df["age"].asComparable().min()
        df.getColumn<Int>("age").min()
        // SampleEnd
    }

    @Test
    fun columnMean_properties() {
        // SampleStart
        df.mean { age }
        df.age.mean()
        // SampleEnd
    }

    @Test
    fun columnMean_accessors() {
        // SampleStart
        val age by column<Int>()

        df.mean(age)
        df.mean { age }
        df[age].mean()
        // SampleEnd
    }

    @Test
    fun columnMean_strings() {
        // SampleStart
        df.mean("age")
        df["age"].asNumbers().mean()
        df.getColumn<Int>("age").mean()
        // SampleEnd
    }

    @Test
    fun columnMedian_properties() {
        // SampleStart
        df.median { age }
        df.age.median()
        // SampleEnd
    }

    @Test
    fun columnMedian_accessors() {
        // SampleStart
        val age by column<Int>()

        df.median(age)
        df.median { age }
        df[age].median()
        // SampleEnd
    }

    @Test
    fun columnMedian_strings() {
        // SampleStart
        df.median("age")
        df["age"].asComparable().median()
        df.getColumn<Int>("age").median()
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
    fun columnsFor_acessors() {
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

        df.minOf { 2021 - age }
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
}
