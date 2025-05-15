@file:Suppress("PropertyName", "unused", "ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.renameToCamelCase
import org.jetbrains.kotlinx.dataframe.api.toCamelCase
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class RenameToCamelCase : DataFrameSampleHelper("rename", "api") {
    private val df = dataFrameOf("ColumnA", "column_b", "COLUMN-C")(1, "a", true, 2, "b", false)

    val ColumnA by column<String>()
    val `COLUMN-C` by column<String>()

    @Test
    fun notebook_test_rename_3() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_rename_4() {
        // SampleStart
        df.rename { ColumnA and `COLUMN-C` }.toCamelCase()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_rename_5() {
        // SampleStart
        df.renameToCamelCase()
            // SampleEnd
            .saveDfHtmlSample()
    }
}
