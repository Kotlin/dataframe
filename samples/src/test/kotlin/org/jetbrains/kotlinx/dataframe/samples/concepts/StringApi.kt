package org.jetbrains.kotlinx.dataframe.samples.concepts

import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.select
import org.junit.Test

class StringApi {

    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Bob"),
        "info" to columnOf(
            "age" to columnOf(23, 27),
            "height" to columnOf(175.5, 160.2),
        ),
    )

    @Test
    fun simpleSelect() {
        // SampleStart
        // Select a sub-dataframe with the "name" and "info" columns
        df.select("name", "info")
        // SampleEnd
    }

    @Test
    fun getColumn() {
        // SampleStart
        df.getColumn { colGroup("info").col("height") }
        // SampleEnd
    }

    @Test
    fun selectSubcolumnAndColumn() {
        // SampleStart
        df.select { colGroup("info").col("age") and col("name") }
        // SampleEnd
    }

    @Test
    fun meanValueBySubcolumn() {
        // SampleStart
        df.mean { colGroup("info").col<Int>("age") }
        // SampleEnd
    }

    @Test
    fun combineExtensionsAndStrings() {
        run { df }
        // SampleStart
        df.select { "info".col("height") and name }
        // SampleEnd
    }

    @Test
    fun removeWithExcept() {
        // SampleStart
        df.remove {
            colsAtAnyDepth().colsOf<Number>() except
                colGroup("info").col("age")
        }
        // SampleEnd
    }

    @Test
    fun selectSubcolumns() {
        // SampleStart
        df.select { colGroup("info").select { col("age") and col("height") } }
        // or
        df.select { colGroup("info").allCols() }
        // SampleEnd
    }

    @Test
    fun addColumnFromSubcolumn() {
        // SampleStart
        df.add("heightInt") {
            "info"["height"]<Double>().toInt()
        }
        // SampleEnd
    }

    @Test
    fun filterBySubcolumn() {
        // SampleStart
        df.filter { "info"["age"]<Int>() >= 18 }
        // SampleEnd
    }

    @Test
    fun invocatedStringsApi() {
        // SampleStart
        // Columns Selection DSL

        // Get a single "height" subcolumn from the "info" column group
        df.getColumn { "info"["height"]<Double>() }

        // Select the "age" subcolumn of the "info" column group
        // and the "name" column
        df.select { "info"["age"] and "name"() }

        // Calculate the mean value of the ("info"->"age") column;
        // specify the column type as an invocation type argument
        df.mean { "info" { "age"<Int>() } }

        // Select all subcolumns from the "info" column group
        df.select { "info" { "age"() and "height"() } }
        // or
        df.select { "info".allCols() }

        // Row Expressions

        // Add a new "heightInt" column by
        // casting the "height" column values to `Int`
        df.add("heightInt") {
            "info"["height"]<Double>().toInt()
        }

        // Filter rows where the ("info"->"age") column value
        // is greater than or equal to 18
        df.filter { "info"["age"]<Int>() >= 18 }
        // SampleEnd
    }
}
