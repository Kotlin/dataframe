@file:Suppress("PropertyName", "UNUSED_VARIABLE", "UNUSED_EXPRESSION", "UNCHECKED_CAST")

package org.jetbrains.kotlinx.dataframe.samples.guides

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.maxOf
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.writeExcel
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.junit.Ignore
import org.junit.Test

class QuickStartGuide : DataFrameSampleHelper("quickstart", "guides") {
    private val df = DataFrame.readCsv(
        "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
    )

    private val full_name by column<String>()
    private val name by column<String>()
    private val stargazers_count by column<Int>()
    private val starsCount by column<Int>()
    private val topics by column<String>()

    interface DFUpdatedSchema

    // TODO remove, use plugin
    val DataRow<DFUpdatedSchema>.name: String
        get() = get("name") as String
    val ColumnsContainer<DFUpdatedSchema>.topics: DataColumn<String>
        get() = get("topics") as DataColumn<String>
    val DataRow<DFUpdatedSchema>.topics: List<String>
        get() = get("topics") as List<String>
    val ColumnsContainer<DFUpdatedSchema>.isIntellij: DataColumn<Boolean>
        get() = get("isIntellij") as DataColumn<Boolean>
    val DataRow<DFUpdatedSchema>.starsCount: Int
        get() = get("starsCount") as Int

    private val dfSelected = df.select { full_name and stargazers_count and topics }
    private val dfFiltered = dfSelected.filter { stargazers_count >= 1000 }
    private val dfRenamed = dfFiltered.rename { full_name }.into("name")
        // And "stargazers_count" into "starsCount"
        .rename { stargazers_count }.into("starsCount")
    private val dfUpdated = dfRenamed
        // Update "name" values with only its second part (after '/')
        .update { name }.with { it.split("/")[1] }
        // Convert "topics" `String` values into `List<String>` by splitting:
        .convert { topics }.with { it.removePrefix("[").removeSuffix("]").split(", ") } as DataFrame<DFUpdatedSchema>
    private val dfWithIsIntellij = dfUpdated.add("isIntellij") {
        name.contains("intellij") || "intellij" in topics
    }
    private val groupedByIsIntellij = dfWithIsIntellij.groupBy { isIntellij }
    private val dfTop10 = dfWithIsIntellij
        // Sort by "starsCount" value descending
        .sortByDesc { starsCount }.take(10)

    @Test
    fun notebook_test_quickstart_2() {
        // SampleStart
        val df = DataFrame.readCsv(
            "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
        )
        // SampleEnd
    }

    @Test
    fun notebook_test_quickstart_3() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_4() {
        // SampleStart
        df.describe()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_5() {
        // SampleStart
        // Select "full_name", "stargazers_count" and "topics" columns
        val dfSelected = df.select { full_name and stargazers_count and topics }
        dfSelected
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_6() {
        // SampleStart
        // Keep only rows where "stargazers_count" value is more than 1000
        val dfFiltered = dfSelected.filter { stargazers_count >= 1000 }
        dfFiltered
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_7() {
        // SampleStart
        // Rename "full_name" column into "name"
        val dfRenamed = dfFiltered.rename { full_name }.into("name")
            // And "stargazers_count" into "starsCount"
            .rename { stargazers_count }.into("starsCount")
        dfRenamed
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_8() {
        // SampleStart
        val dfUpdated = dfRenamed
            // Update "name" values with only its second part (after '/')
            .update { name }.with { it.split("/")[1] }
            // Convert "topics" `String` values into `List<String>` by splitting:
            .convert { topics }.with { it.removePrefix("[").removeSuffix("]").split(", ") }
        dfUpdated
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_9() {
        // SampleStart
        dfUpdated.topics.type()
        // SampleEnd
    }

    @Test
    fun notebook_test_quickstart_10() {
        // SampleStart
        // Add a `Boolean` column indicating whether the `name` contains the "intellij" substring
        // or the topics include "intellij".
        val dfWithIsIntellij = dfUpdated.add("isIntellij") {
            name.contains("intellij") || "intellij" in topics
        }
        dfWithIsIntellij
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_11() {
        // SampleStart
        val groupedByIsIntellij = dfWithIsIntellij.groupBy { isIntellij }
        groupedByIsIntellij
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_12() {
        // SampleStart
        groupedByIsIntellij.count()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_13() {
        // SampleStart
        groupedByIsIntellij.aggregate {
            // Compute sum and max of "starsCount" within each group into "sumStars" and "maxStars" columns
            sumOf { starsCount } into "sumStars"
            maxOf { starsCount } into "maxStars"
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_14() {
        // SampleStart
        val dfTop10 = dfWithIsIntellij
            // Sort by "starsCount" value descending
            .sortByDesc { starsCount }.take(10)
        dfTop10
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_16() {
        // SampleStart
        dfTop10.plot {
            bars {
                x(name)
                y(starsCount)
            }

            layout.title = "Top 10 JetBrains repositories by stars count"
        }
            // SampleEnd
            .savePlotSVGSample()
    }

    @Ignore
    @Test
    fun notebook_test_quickstart_17() {
        // SampleStart
        dfWithIsIntellij.writeExcel("jb_repos.xlsx")
        // SampleEnd
    }
}
