@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.chunked
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.countDistinct
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.distinctBy
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.dropNaNs
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.dropWhile
import org.jetbrains.kotlinx.dataframe.api.fillNaNs
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.gather
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.mapToRows
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.maxByOrNull
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.takeLast
import org.jetbrains.kotlinx.dataframe.api.takeWhile
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.withZero
import org.jetbrains.kotlinx.dataframe.api.xs
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.jetbrains.kotlinx.dataframe.get
import org.junit.Test

class Access : TestBase() {

    @Test
    @TransformDataFrameExpressions
    fun getColumnByName_properties() {
        // SampleStart
        df.age
        df.name.lastName
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnByName_strings() {
        // SampleStart
        df["age"]
        df["name"]["firstName"]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumn_properties() {
        // SampleStart
        df.getColumn { age }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumn_strings() {
        // SampleStart
        df.getColumn("age")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnOrNull_properties() {
        // SampleStart
        df.getColumnOrNull { age }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnOrNull_strings() {
        // SampleStart
        df.getColumnOrNull("age")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumns_properties() {
        // SampleStart
        df.getColumns { age and name }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumns_strings() {
        // SampleStart
        df.getColumns("age", "name")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnGroup_properties() {
        // SampleStart
        df.getColumnGroup { name }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnGroup_strings() {
        // SampleStart
        df.getColumnGroup("name")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnByIndex() {
        // SampleStart
        df.getColumn(2)
        df.getColumnGroup(0).getColumn(1)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getRowByIndex() {
        // SampleStart
        df[2]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
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
    @TransformDataFrameExpressions
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
    @TransformDataFrameExpressions
    fun getCell_strings() {
        // SampleStart
        df["age"][1]
        df[1]["age"]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getCell_properties() {
        // SampleStart
        df.age[1]
        df[1].age
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnsByName_properties() {
        // SampleStart
        df[df.age, df.weight]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getColumnsByName_strings() {
        // SampleStart
        df["age", "weight"]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun select_properties() {
        // SampleStart
        df.select { age and weight }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun select_strings() {
        // SampleStart
        df.select { "age" and "weight" }
        df.select("age", "weight")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getSeveralRowsByIndices() {
        // SampleStart
        df[0, 3, 4]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getSeveralRowsByRanges() {
        // SampleStart
        df[1..2]
        df[0..2, 4..5]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun getRowsColumns() {
        // SampleStart
        df.columns() // List<DataColumn>
        df.rows() // Iterable<DataRow>
        df.values() // Sequence<Any?>
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun take() {
        // SampleStart
        df.take(5)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun takeLast() {
        // SampleStart
        df.takeLast(5)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun takeWhile() {
        // SampleStart
        df.takeWhile { isHappy }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun drop() {
        // SampleStart
        df.drop(5)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun dropLast() {
        // SampleStart
        df.dropLast() // default 1
        df.dropLast(5)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun dropWhile() {
        // SampleStart
        df.dropWhile { !isHappy }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun filter_properties() {
        // SampleStart
        df.filter { age > 18 && name.firstName.startsWith("A") }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun filter_strings() {
        // SampleStart
        df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun dropWhere_properties() {
        // SampleStart
        df.drop { weight == null || city == null }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun dropWhere_strings() {
        // SampleStart
        df.drop { it["weight"] == null || it["city"] == null }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
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
    @TransformDataFrameExpressions
    fun dropNaNs() {
        // SampleStart
        df.dropNaNs() // remove rows containing NaN in any column
        df.dropNaNs(whereAllNaN = true) // remove rows with NaN in all columns
        df.dropNaNs { weight } // remove rows where 'weight' is NaN
        df.dropNaNs { age and weight } // remove rows where either 'age' or 'weight' is NaN
        df.dropNaNs(whereAllNaN = true) { age and weight } // remove rows where both 'age' and 'weight' are NaN
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun dropNA() {
        // SampleStart
        df.dropNA() // remove rows containing null or NaN in any column
        df.dropNA(whereAllNA = true) // remove rows with null or NaN in all columns
        df.dropNA { weight } // remove rows where 'weight' is null or NaN
        df.dropNA { age and weight } // remove rows where either 'age' or 'weight' is null or NaN
        df.dropNA(whereAllNA = true) { age and weight } // remove rows where both 'age' and 'weight' are null or NaN
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun byColumn_strings() {
        // SampleStart
        df["name"][0]
        df["name", "age"][3, 5, 6]
        // SampleEnd
        // TODO: df["age"][2..4]
    }

    @Test
    @TransformDataFrameExpressions
    fun byColumn_properties() {
        // SampleStart
        df.name[0]
        df.select { name and age }[3, 5, 6]
        df.age[2..4]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun byRow_strings() {
        // SampleStart
        df[0]["name"]
        df[3, 5, 6]["name", "age"]
        df[3..5]["age"]
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun byRow_properties() {
        // SampleStart
        df[0].name
        df[3, 5, 6].select { name and age }
        df[3..5].age
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun namedAndRenameCol() {
        // SampleStart
        val unnamedCol = columnOf("Alice", "Bob")
        val colRename = unnamedCol.rename("name")
        val colNamed = columnOf("Alice", "Bob") named "name"
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun namedColumnWithoutValues() {
        // SampleStart
        val name by column<String>()
        val col = column<String>("name")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
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
    @TransformDataFrameExpressions
    fun iterableApi() {
        // SampleStart
        df.forEach { println(it) }
        df.take(5)
        df.drop(2)
        df.chunked(10)
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun distinct() {
        // SampleStart
        df.distinct()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun distinctColumns_properties() {
        // SampleStart
        df.distinct { age and name }
        // same as
        df.select { age and name }.distinct()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun countDistinct() {
        // SampleStart
        df.countDistinct()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun countDistinctColumns_properties() {
        // SampleStart
        df.countDistinct { age and name }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun countDistinctColumns_strings() {
        // SampleStart
        df.countDistinct("age", "name")
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun distinctColumns_strings() {
        // SampleStart
        df.distinct("age", "name")
        // same as
        df.select("age", "name").distinct()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun distinctBy_properties() {
        // SampleStart
        df.distinctBy { age and name }
        // same as
        df.groupBy { age and name }.mapToRows { group.first() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun distinctBy_strings() {
        // SampleStart
        df.distinctBy("age", "name")
        // same as
        df.groupBy("age", "name").mapToRows { group.first() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnSelectorsUsages() {
        // SampleStart
        df.select { age and name }
        df.fillNaNs { colsAtAnyDepth().colsOf<Double>() }.withZero()
        df.remove { cols { it.hasNulls() } }
        df.group { cols { it.data != name } }.into { "nameless" }
        df.update { city }.notNull { it.lowercase() }
        df.gather { colsOf<Number>() }.into("key", "value")
        df.move { name.firstName and name.lastName }.after { city }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
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

        // all columns of ColumnGroup
        df.select { name.allCols() }

        // traversal of columns at any depth from here excluding ColumnGroups
        df.select { name.colsAtAnyDepth().filter { !it.isColumnGroup() } }

        // SampleEnd
    }

    @Test
    fun columnSelectors_kproperties() {
        // SampleStart
        // by column name
        df.select { it[Person::name] }
        df.select { (Person::name)() }
        df.select { col(Person::name) }

        // by column path
        df.select { it[Person::name][Name::firstName] }
        df.select { Person::name[Name::firstName] }

        // with a new name
        df.select { Person::name named "Full Name" }

        // converted
        df.select { Person::name[Name::firstName].map { it.lowercase() } }

        // column arithmetics
        df.select { 2021 - (Person::age)() }

        // two columns
        df.select { Person::name and Person::age }

        // range of columns
        df.select { Person::name..Person::age }

        // all columns of ColumnGroup
        df.select { Person::name.allCols() }

        // traversal of columns at any depth from here excluding ColumnGroups
        df.select { Person::name.colsAtAnyDepth().filter { !it.isColumnGroup() } }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnSelectors_strings() {
        // SampleStart
        // by column name
        df.select { it["name"] }

        // by column path
        df.select { it["name"]["firstName"] }
        df.select { "name"["firstName"] }

        // with a new name
        df.select { "name" named "Full Name" }

        // converted
        df.select { "name"["firstName"]<String>().map { it.uppercase() } }

        // column arithmetics
        df.select { 2021 - "age"<Int>() }

        // two columns
        df.select { "name" and "age" }

        // by range of names
        df.select { "name".."age" }

        // all columns of ColumnGroup
        df.select { "name".allCols() }

        // traversal of columns at any depth from here excluding ColumnGroups
        df.select { "name".colsAtAnyDepth().filter { !it.isColumnGroup() } }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
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
    @TransformDataFrameExpressions
    fun columnSelectorsMisc() {
        val df = df.add { "year" from { 0 } }
        // SampleStart
        // by condition
        df.select { cols { it.name().startsWith("year") } }
        df.select { nameStartsWith("year") }

        // by type
        df.select { colsOf<String>() }

        // by type with condition
        df.select { colsOf<String?> { it.countDistinct() > 5 } }

        // all top-level columns
        df.select { all() }

        // first/last n columns
        df.select { take(2) }
        df.select { takeLast(2) }

        // all except first/last n columns
        df.select { drop(2) }
        df.select { dropLast(2) }

        // find the first column satisfying the condition
        df.select { first { it.name.startsWith("year") } }

        // find the last column inside a column group satisfying the condition
        df.select {
            colGroup("name").lastCol { it.name().endsWith("Name") }
        }

        // find the single column inside a column group satisfying the condition
        df.select {
            Person::name.singleCol { it.name().startsWith("first") }
        }

        // traversal of columns at any depth from here excluding ColumnGroups
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() } }

        // traversal of columns at any depth from here including ColumnGroups
        df.select { colsAtAnyDepth() }

        // traversal of columns at any depth with condition
        df.select { colsAtAnyDepth().filter() { it.name().contains(":") } }

        // traversal of columns at any depth to find columns of given type
        df.select { colsAtAnyDepth().colsOf<String>() }

        // all columns except given column set
        df.select { allExcept { colsOf<String>() } }

        // union of column sets
        df.select { take(2) and col(3) }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun columnSelectorsModifySet() {
        // SampleStart
        // first/last n value- and frame columns in column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.take(3) }
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.takeLast(3) }

        // all except first/last n value- and frame columns in column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.drop(3) }
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.dropLast(3) }

        // filter column set by condition
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() && it.name().startsWith("year") } }

        // exclude columns from column set
        df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.except { age } }

        // keep only unique columns
        df.select { (colsOf<Int>() and age).distinct() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun forRows_properties() {
        // SampleStart
        for (row in df) {
            println(row.age)
        }

        df.forEach {
            println(it.age)
        }

        df.rows().forEach {
            println(it.age)
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun forRows_strings() {
        // SampleStart
        for (row in df) {
            println(row["age"])
        }

        df.forEach {
            println(it["age"])
        }

        df.rows().forEach {
            println(it["age"])
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun forColumn() {
        // SampleStart
        df.columns().forEach {
            println(it.name())
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun forCells() {
        // SampleStart
        // from top to bottom, then from left to right
        df.values().forEach {
            println(it)
        }

        // from left to right, then from top to bottom
        df.values(byRows = true).forEach {
            println(it)
        }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun xs() {
        // SampleStart
        df.xs("Charlie", "Chaplin")

        df.xs("Moscow", true) { city and isHappy }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun values() {
        // SampleStart
        df.values()
        df.values(byRows = true)
        df.values { age and weight }
        // SampleEnd
    }
}
