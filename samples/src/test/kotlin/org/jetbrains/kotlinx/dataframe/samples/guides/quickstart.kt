@file:Suppress("PropertyName", "UNUSED_VARIABLE", "UNUSED_EXPRESSION", "UNCHECKED_CAST")

package org.jetbrains.kotlinx.dataframe.samples.guides

import io.kotest.assertions.print.print
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.CodeString
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.generateInterfaces
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.maxOf
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.id
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.toHtml
import org.jetbrains.kotlinx.dataframe.io.writeExcel
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.junit.AfterClass
import org.junit.Ignore
import org.junit.Test
import java.io.File
import kotlin.io.path.Path

class QuickStartGuide : DataFrameSampleHelper("quickstart", "guides") {

    @DataSchema
    interface Repository {
        val full_name: String
        val html_url: java.net.URL
        val stargazers_count: Int
        val topics: String
        val watchers: Int
    }

    private val df = DataFrame.readCsv(
        "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
    )

    private val dfRepository = df.cast<Repository>()

    private fun getDfSelected() = dfRepository.select { full_name and stargazers_count and topics }

    @Test
    fun t() {
        println(
            dfRepository.cast<Repository>().select { full_name and stargazers_count and topics }
                .select { full_name },
        )
    }

    private fun getDfFiltered() =
        getDfSelected()
            .filter { stargazers_count >= 1000 }

    private fun getDfRenamed() =
        getDfFiltered()
            .rename { full_name }.to("name")
            // And "stargazers_count" to "starsCount"
            .rename { stargazers_count }.to("starsCount")

    private fun getDfUpdated() =
        getDfRenamed()
            // Update "name" values with only its second part (after '/')
            .update { name }.with { it.split("/")[1] }
            // Convert "topics" `String` values into `List<String>` by splitting:
            .convert { topics }.with { it.removePrefix("[").removeSuffix("]").split(", ") }

    private fun getDfWithIsIntellij() =
        getDfUpdated()
            .add("isIntellij") {
                name.contains("intellij") || "intellij" in topics
            }

    private fun getGroupedByIsIntellij() =
        getDfWithIsIntellij()
            .groupBy { isIntellij }

    private fun getDfTop10() =
        getDfWithIsIntellij()
            // Sort by "starsCount" value descending
            .sortByDesc { starsCount }.take(10)

    @Test
    fun notebook_test_quickstart_2() {
        // SampleStart
        // Read a csv file from the given URL string
        val df = DataFrame.readCsv(
            "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
        )
        // SampleEnd
    }

    @Test
    fun dfPrint() {
        // SampleStart
        df.print()
        // SampleEnd
    }

    @Test
    fun dfToHtml() {
        // SampleStart
        df.toHtml().writeHtml("df.html")
        // SampleEnd
        df.saveDfHtmlSample()
    }

    @Test
    fun dfToHtmlOpenInBrowser() {
        // SampleStart
        df.toHtml().openInBrowser()
        // SampleEnd
    }

    @Test
    fun dfDescribe() {
        // SampleStart
        df.describe()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dfGenerateInterfaces() {
        // SampleStart
        df.generateInterfaces("Repository", nameNormalizer = NameNormalizer.id()).print()
        // SampleEnd
        df.generateInterfaces("Repository", nameNormalizer = NameNormalizer.id()).saveSample()
    }

    @Test
    fun dfCast() {
        // SampleStart
        val dfRepository = df.cast<Repository>()
        // SampleEnd
    }

    @Test
    fun dfRepositoryGetFullName() {
        // SampleStart
        val fullNameColumn: DataColumn<String> = dfRepository.full_name
        // SampleEnd
        fullNameColumn.saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_5() {
        // SampleStart
        // Select "full_name", "stargazers_count" and "topics" columns
        val dfSelected = dfRepository.select { full_name and stargazers_count and topics }
        // SampleEnd
        dfSelected.saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_6() {
        val dfSelected = getDfSelected()
        // SampleStart
        // Keep only rows where "stargazers_count" value is more than 1000
        val dfFiltered = dfSelected.filter { stargazers_count >= 1000 }
        // SampleEnd
        dfFiltered.saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_7() {
        val dfFiltered = getDfFiltered()
        // SampleStart
        val dfRenamed = dfFiltered
            // Rename "full_name" column to "name"
            .rename { full_name }.to("name")
            // and "stargazers_count" to "starsCount"
            .rename { stargazers_count }.to("starsCount")
        // SampleEnd
        dfRenamed.saveDfHtmlSample()
    }

    @Test
    fun dfRenamedSelectName() {
        val dfRenamed = getDfRenamed()
        // SampleStart
        dfRenamed.select { name }
        // SampleEnd
    }

    @Test
    fun notebook_test_quickstart_8() {
        val dfRenamed = getDfRenamed()
        // SampleStart
        val dfUpdated = dfRenamed
            // Update "name" values with only its second part (after '/')
            .update { name }.with { it.split("/")[1] }
            // Convert "topics" `String` values into `List<String>` by splitting:
            .convert { topics }.with { it.removePrefix("[").removeSuffix("]").split(", ") }
            // SampleEnd
        dfUpdated.saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_9() {
        val dfUpdated = getDfUpdated()
        // SampleStart
        println(dfUpdated.topics.type())
        // SampleEnd
        CodeString(dfUpdated.topics.type().toString()).saveSample()
    }

    @Test
    fun notebook_test_quickstart_10() {
        val dfUpdated = getDfUpdated()
        // SampleStart
        // Add a `Boolean` column indicating whether the `name` contains the "intellij" substring
        // or the topics include "intellij".
        val dfWithIsIntellij = dfUpdated.add("isIntellij") {
            name.lowercase().contains("intellij") || "intellij" in topics
        }
            // SampleEnd
        dfWithIsIntellij.saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_11() {
        val dfWithIsIntellij = getDfWithIsIntellij()
        // SampleStart
        val groupedByIsIntellij = dfWithIsIntellij.groupBy { isIntellij }
            // SampleEnd
        groupedByIsIntellij.saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_12() {
        val groupedByIsIntellij = getGroupedByIsIntellij()
        // SampleStart
        groupedByIsIntellij.count()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_13() {
        val groupedByIsIntellij = getGroupedByIsIntellij()
        // SampleStart
        groupedByIsIntellij.aggregate {
            // Compute sum and max of "starsCount" within each group
            // into "sumStars" and "maxStars" columns
            sumOf { starsCount } into "sumStars"
            maxOf { starsCount } into "maxStars"
        }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_14() {
        val dfWithIsIntellij = getDfWithIsIntellij()
        // SampleStart
        val dfTop10 = dfWithIsIntellij
            // Sort by "starsCount" value descending
            .sortByDesc { starsCount }.take(10)
            // SampleEnd
        dfTop10.saveDfHtmlSample()
    }

    @Test
    fun notebook_test_quickstart_16() {
        val dfTop10 = getDfTop10()
        // SampleStart
        dfTop10.plot {
            // Create a bar layer
            bars {
                // Use values from "name" as bars categories
                x(name)
                // Use values from "starsCount" as bars heights
                y(starsCount)
            }

            layout.title = "Top 10 JetBrains repositories by stars count"
        }
            // save the plot as an SVG image
            .save("top_10_repos.svg", path = "plots/")
        // SampleEnd

        dfTop10.plot {
            bars {
                x(name)
                y(starsCount)
            }

            layout.title = "Top 10 JetBrains repositories by stars count"
        }
            .savePlotSVGSample()
    }

    @Test
    fun notebook_test_quickstart_17() {
        val dfWithIsIntellij = getDfWithIsIntellij()
        // SampleStart
        dfWithIsIntellij.writeExcel("jb_repos.xlsx")
        // SampleEnd
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun removeGeneratedFiles() {
            File("plots").deleteRecursively()
            File("jb_repos.xlsx").deleteRecursively()
            File("df.html").deleteRecursively()
        }
    }
}
