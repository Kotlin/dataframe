@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.chunked
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.distinctBy
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.dropNaNs
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.dropWhile
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.mapToRows
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.maxByOrNull
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.minus
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.takeLast
import org.jetbrains.kotlinx.dataframe.api.takeWhile
import org.jetbrains.kotlinx.dataframe.api.values
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
    fun getColumnsByName() {
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
    fun getRowsColumns() {
        // SampleStart
        df.columns() // List<DataColumn>
        df.rows() // Iterable<DataRow>
        df.values() // Sequence<Any?>
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
