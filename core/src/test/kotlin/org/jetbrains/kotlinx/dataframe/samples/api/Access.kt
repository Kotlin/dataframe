@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.distinctBy
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.mapToRows
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
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
    fun getColumnByIndex() {
        // SampleStart
        df.getColumn(2)
        df.getColumnGroup(0).getColumn(1)
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
