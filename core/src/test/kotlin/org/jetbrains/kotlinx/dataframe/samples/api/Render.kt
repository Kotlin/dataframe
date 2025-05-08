@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.reorderColumnsByName
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.toHtml
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.junit.Ignore
import org.junit.Test
import java.io.File
import kotlin.io.path.Path

class Render : TestBase() {
    @Test
    @TransformDataFrameExpressions
    @Ignore
    fun useRenderingResult() {
        // SampleStart
        df.toStandaloneHtml(DisplayConfiguration(rowsLimit = null)).openInBrowser()
        df.toStandaloneHtml(DisplayConfiguration(rowsLimit = null)).writeHtml(File("/path/to/file"))
        df.toStandaloneHtml(DisplayConfiguration(rowsLimit = null)).writeHtml(Path("/path/to/file"))
        // SampleEnd
    }

    @Test
    fun composeTables() {
        // SampleStart
        val df1 = df.reorderColumnsByName()
        val df2 = df.sortBy { age }
        val df3 = df.sortByDesc { age }

        listOf(df1, df2, df3).fold(DataFrameHtmlData.tableDefinitions()) { acc, df -> acc + df.toHtml() }
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun configureCellOutput() {
        // SampleStart
        df.toHtml(DisplayConfiguration(cellContentLimit = -1))
        // SampleEnd
    }
}
