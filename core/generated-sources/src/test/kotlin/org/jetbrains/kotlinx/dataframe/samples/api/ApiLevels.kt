@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.jetbrains.kotlinx.dataframe.io.read
import org.junit.Ignore
import org.junit.Test

@Ignore
class ApiLevels {

    @Test
    @TransformDataFrameExpressions
    fun strings() {
        // SampleStart
        DataFrame.read("titanic.csv")
            .add("lastName") { "name"<String>().split(",").last() }
            .dropNulls("age")
            .filter {
                "survived"<Boolean>() &&
                    "home"<String>().endsWith("NY") &&
                    "age"<Int>() in 10..20
            }
        // SampleEnd
    }

    @DataSchema
    interface TitanicPassenger {
        val survived: Boolean
        val home: String
        val age: Int
        val name: String
    }

    @Test
    @TransformDataFrameExpressions
    fun extensionProperties2() {
        val df = DataFrame.read("titanic.csv").cast<TitanicPassenger>()
        // SampleStart
        df.add("lastName") { name.split(",").last() }
            .dropNulls { age }
            .filter { survived && home.endsWith("NY") && age in 10..20 }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun extensionProperties1() {
        // SampleStart
        val df /* : AnyFrame */ = DataFrame.read("titanic.csv")
        // SampleEnd
    }
}
