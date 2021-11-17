package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.chunked
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.distinctBy
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.mapToRows
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.singleColumn
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.takeLast
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.getColumnGroup
import org.junit.Test

class Access : TestBase() {

    @Test
    fun getColumnByName_strings() {
        // SampleStart
        df["age"]
        df["name"]["firstName"]
        // SampleEnd
    }

    @Test
    fun getColumnByName_properties() {
        // SampleStart
        df.age
        df.name.lastName
        // SampleEnd
    }

    @Test
    fun getColumnByName_accessors() {
        // SampleStart
        val age by column<Int>()
        val name by columnGroup()
        val lastName by name.column<String>()

        df[age]
        df[lastName]
        // SampleEnd
    }

    @Test
    fun getColumnByIndex() {
        // SampleStart
        df.getColumn(2)
        df.getColumnGroup(0).getColumn(1)
        // SampleEnd
    }

    @Test
    fun getColumnByCondition() {
        // SampleStart
        df.singleColumn { it.isNumber() && it.hasNulls() }
        // SampleEnd
    }

    @Test
    fun getRowByIndex() {
        // SampleStart
        df[2]
        // SampleEnd
    }

    @Test
    fun getRowByCondition_properties() {
        // SampleStart
        df.single { age == 45 }
        df.first { weight != null }
        df.minBy { age }
        df.maxBy { name.firstName.length }
        df.maxByOrNull { weight }
        // SampleEnd
    }

    @Test
    fun getRowByCondition_accessors() {
        // SampleStart
        val age by column<Int>()
        val weight by column<Int?>()
        val name by columnGroup()
        val firstName by name.column<String>()

        df.single { age() == 45 }
        df.first { weight() != null }
        df.minBy(age)
        df.maxBy { firstName().length }
        df.maxByOrNull { weight() }
        // SampleEnd
    }

    @Test
    fun getRowByCondition_strings() {
        // SampleStart
        df.single { "age"<Int>() == 45 }
        df.first { it["weight"] != null }
        df.minBy("weight")
        df.maxBy { "name"["firstName"]<String>().length }
        df.maxByOrNull("weight")
        // SampleEnd
    }

    @Test
    fun getCell_strings() {
        // SampleStart
        df["age"][1]
        df[1]["age"]
        // SampleEnd
    }

    @Test
    fun getCell_properties() {
        // SampleStart
        df.age[1]
        df[1].age
        // SampleEnd
    }

    @Test
    fun getCell_accessors() {
        // SampleStart
        val age by column<String>()

        df[age][1]
        df[1][age]
        // SampleEnd
    }

    @Test
    fun getColumnsByName_strings() {
        // SampleStart
        df.select { "age" and "weight" }
        df.select("age", "weight")
        df["age", "weight"]
        // SampleEnd
    }

    @Test
    fun getColumnsByName_properties() {
        // SampleStart
        df.select { age and weight }
        df[df.age, df.weight]
        // SampleEnd
    }

    @Test
    fun getColumnsByName_accessors() {
        // SampleStart
        val age by column<Int>()
        val weight by column<Int?>()

        df.select { age and weight }
        df.select(age, weight)
        df[age, weight]
        // SampleEnd
    }

    @Test
    fun getSeveralRowsByIndices() {
        // SampleStart
        df[0, 3, 4]
        // SampleEnd
    }

    @Test
    fun getSeveralRowsByRanges() {
        // SampleStart
        df[1..2]
        df[0..2, 4..5]
        // SampleEnd
    }

    @Test
    fun take() {
        // SampleStart
        df.take(5)
        // SampleEnd
    }

    @Test
    fun takeLast() {
        // SampleStart
        df.takeLast(5)
        // SampleEnd
    }

    @Test
    fun drop() {
        // SampleStart
        df.drop(5)
        // SampleEnd
    }

    @Test
    fun dropLast() {
        // SampleStart
        df.dropLast(5)
        // SampleEnd
    }

    @Test
    fun filter_properties() {
        // SampleStart
        df.filter { age > 18 && name.firstName.startsWith("A") }
        // SampleEnd
    }

    @Test
    fun filter_accessors() {
        // SampleStart
        val age by column<Int>()
        val name by columnGroup()
        val firstName by name.column<String>()

        df.filter { age() > 18 && firstName().startsWith("A") }
        // or
        df.filter { it[age] > 18 && it[firstName].startsWith("A") }
        // SampleEnd
    }

    @Test
    fun filter_strings() {
        // SampleStart
        df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }
        // SampleEnd
    }

    @Test
    fun filterBy_properties() {
        // SampleStart
        df.filterBy { isHappy }
        // SampleEnd
    }

    @Test
    fun filterBy_accessors() {
        // SampleStart
        val isHappy by column<Boolean>()
        df.filterBy { isHappy }
        // SampleEnd
    }

    @Test
    fun filterBy_strings() {
        // SampleStart
        df.filterBy("isHappy")
        // SampleEnd
    }

    @Test
    fun dropWhere_properties() {
        // SampleStart
        df.drop { weight == null || city == null }
        // SampleEnd
    }

    @Test
    fun dropWhere_accessors() {
        // SampleStart
        val name by columnGroup()
        val weight by column<Int?>()
        val city by column<String?>()

        df.drop { weight() == null || city() == null }
        // or
        df.drop { it[weight] == null || it[city] == null }
        // SampleEnd
    }

    @Test
    fun dropWhere_strings() {
        // SampleStart
        df.drop { it["weight"] == null || it["city"] == null }
        // SampleEnd
    }

    @Test
    fun dropNulls() {
        // SampleStart
        df.dropNulls() // remove rows with null value in any column
        df.dropNulls(whereAllNull = true) // remove rows with null values in all columns
        df.dropNulls { city } // remove rows with null value in 'city' column
        df.dropNulls { city and weight } // remove rows with null value in 'city' OR 'weight' columns
        df.dropNulls(whereAllNull = true) { city and weight } // remove rows with null value in 'city' AND 'weight' columns
        // SampleEnd
    }

    @Test
    fun dropNA() {
        // SampleStart
        df.dropNA() // remove rows containing null or Double.NaN in any column
        df.dropNA(whereAllNA = true) // remove rows with null or Double.NaN in all columns
        df.dropNA { weight } // remove rows where 'weight' is null or Double.NaN
        df.dropNA { age and weight } // remove rows where either 'age' or 'weight' is null or Double.NaN
        df.dropNA(whereAllNA = true) { age and weight } // remove rows where both 'age' and 'weight' are null or Double.NaN
        // SampleEnd
    }

    @Test
    fun byColumn_strings() {
        // SampleStart
        df["name"][0]
        df["name", "age"][3, 5, 6]
        // SampleEnd
        // TODO: df["age"][2..4]
    }

    @Test
    fun byColumn_accessors() {
        // SampleStart
        val name by column<String>()
        val age by column<Int>()
        df[name][0]
        df[name, age][3, 5, 6]
        // SampleEnd
        // TODO: df[age][2..4]
    }

    @Test
    fun byColumn_properties() {
        // SampleStart
        df.name[0]
        df.select { name and age }[3, 5, 6]
        df.age[2..4]
        // SampleEnd
    }

    @Test
    fun byRow_strings() {
        // SampleStart
        df[0]["name"]
        df[3, 5, 6]["name", "age"]
        df[3..5]["age"]
        // SampleEnd
    }

    @Test
    fun byRow_accessors() {
        // SampleStart
        val name by column<String>()
        val age by column<Int>()
        df[0][name]
        df[3, 5, 6][name, age]
        df[3..5][age]
        // SampleEnd
    }

    @Test
    fun byRow_properties() {
        // SampleStart
        df[0].name
        df[3, 5, 6].select { name and age }
        df[3..5].age
        // SampleEnd
    }

    @Test
    fun namedAndRenameCol() {
        // SampleStart
        val unnamedCol = columnOf("Alice", "Bob")
        val colRename = unnamedCol.rename("name")
        val colNamed = columnOf("Alice", "Bob") named "name"
        // SampleEnd
    }

    @Test
    fun namedColumnWithoutValues() {
        // SampleStart
        val name by column<String>()
        val col = column<String>("name")
        // SampleEnd
    }

    @Test
    fun colRefForTypedAccess() {
        val df = dataFrameOf("name")("Alice", "Bob")
        val name by column<String>()
        val col = column<String>("name")
        // SampleStart
        df.filter { it[name].startsWith("A") }
        df.sortBy { col }
        // SampleEnd
    }

    @Test
    fun iterableApi() {
        // SampleStart
        df.forEach { println(it) }
        df.take(5)
        df.drop(2)
        df.chunked(10)
        // SampleEnd
    }

    @Test
    fun asIterableOrSequence() {
        // SampleStart
        df.asIterable()
        df.asSequence()
        // SampleEnd
    }

    @Test
    fun distinct() {
        // SampleStart
        df.distinct()
        // SampleEnd
    }

    @Test
    fun distinctColumns_properties() {
        // SampleStart
        df.distinct { age and name }
        // same as
        df.select { age and name }.distinct()
        // SampleEnd
    }

    @Test
    fun distinctColumns_accessors() {
        // SampleStart
        val age by column<Int>()
        val name by columnGroup()
        df.distinct { age and name }
        // same as
        df.select { age and name }.distinct()
        // SampleEnd
    }

    @Test
    fun distinctColumns_strings() {
        // SampleStart
        df.distinct("age", "name")
        // same as
        df.select("age", "name").distinct()
        // SampleEnd
    }

    @Test
    fun distinctBy_properties() {
        // SampleStart
        df.distinctBy { age and name }
        // same as
        df.groupBy { age and name }.mapToRows { group.first() }
        // SampleEnd
    }

    @Test
    fun distinctBy_accessors() {
        // SampleStart
        val age by column<Int>()
        val name by columnGroup()
        val firstName by name.column<String>()

        df.distinctBy { age and name }
        // same as
        df.groupBy { age and name }.mapToRows { group.first() }
        // SampleEnd
    }

    @Test
    fun distinctBy_strings() {
        // SampleStart
        df.distinctBy("age", "name")
        // same as
        df.groupBy("age", "name").mapToRows { group.first() }
        // SampleEnd
    }

    @Test
    fun columnSelectorsUsages() {
        // SampleStart
        df.select { age and name }
        df.fillNaNs { dfsOf<Double>() }.withZero()
        df.remove { cols { it.hasNulls() } }
        df.update { city }.notNull { it.lowercase() }
        df.gather { numberCols() }.into("key", "value")
        df.move { name.firstName and name.lastName }.after { city }
        // SampleEnd
    }

    @Test
    fun columnSelectors_properties() {
        // SampleStart
        // by column name
        df.select { it.name }
        df.select { name }

        // by column path
        df.select { name.firstName }

        // with a new name
        df.select { name named "Full Name" }

        // converted
        df.select { name.firstName.map { it.lowercase() } }

        // column arithmetics
        df.select { 2021 - age }

        // two columns
        df.select { name and age }

        // range of columns
        df.select { name..age }

        // all children of ColumnGroup
        df.select { name.all() }

        // dfs traversal of children columns
        df.select { name.dfs() }

        // SampleEnd
    }

    @Test
    fun columnSelectors_accessors() {
        // SampleStart
        // by column name
        val name by columnGroup()
        df.select { it[name] }
        df.select { name }

        // by column path
        val firstName by name.column<String>()
        df.select { firstName }

        // with a new name
        df.select { name named "First Name" }

        // converted
        df.select { firstName.map { it.lowercase() } }

        // column arithmetics
        val age by column<Int>()
        df.select { 2021 - age }

        // two columns
        df.select { name and age }

        // range of columns
        df.select { name..age }

        // all children of ColumnGroup
        df.select { name.all() }

        // dfs traversal of children columns
        df.select { name.dfs() }
        // SampleEnd
    }

    @Test
    fun columnSelectors_strings() {
        // SampleStart
        // by column name
        df.select { it["name"] }

        // by column path
        df.select { it["name"]["firstName"] }
        df.select { "name"["firstName"] }

        // with a new name
        df.select { "name" named "First Name" }

        // converted
        df.select { "name"["firstName"]<String>().map { it.uppercase() } }

        // column arithmetics
        df.select { 2021 - "age"() }

        // two columns
        df.select { "name" and "age" }

        // by range of names
        df.select { "name".."age" }

        // all children of ColumnGroup
        df.select { "name".all() }

        // dfs traversal of children columns
        df.select { "name".dfs() }
        // SampleEnd
    }

    @Test
    fun columnsSelectorByIndices() {
        // SampleStart
        // by index
        df.select { col(2) }

        // by several indices
        df.select { cols(0, 1, 3) }

        // by range of indices
        df.select { cols(1..4) }
        // SampleEnd
    }

    @Test
    fun columnSelectorsMisc() {
        // SampleStart
        // by condition
        df.select { cols { it.name.startsWith("year") } }

        // by type
        df.select { colsOf<String>() }
        df.select { stringCols() }

        // by type with condition
        df.select { colsOf<String> { !it.hasNulls() } }
        df.select { stringCols { !it.hasNulls() } }

        // all top-level columns
        df.select { all() }

        // first/last n columns
        df.select { take(2) }
        df.select { takeLast(2) }

        // all except first/last n columns
        df.select { drop(2) }
        df.select { dropLast(2) }

        // dfs traversal of columns
        df.select { dfs() }

        // dfs traversal with condition
        df.select { dfs { it.name.contains(":") } }

        // columns of given type in dfs traversal
        df.select { dfsOf<String>() }

        // all columns except given column set
        df.select { except { colsOf<String>() } }

        // union of column sets
        df.select { take(2) and col(3) }
        // SampleEnd
    }

    @Test
    fun columnSelectorsModifySet() {
        // SampleStart
        // first/last n columns in column set
        df.select { dfs().take(3) }
        df.select { dfs().takeLast(3) }

        // all except first/last n columns in column set
        df.select { dfs().drop(3) }
        df.select { dfs().dropLast(3) }

        // filter column set by condition
        df.select { dfs().filter { it.name.startsWith("year") } }

        // exclude columns from column set
        df.select { dfs().except { age } }
        // SampleEnd
    }
}
