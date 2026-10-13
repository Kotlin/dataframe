package org.jetbrains.kotlinx.dataframe.samples

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.CodeString
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.kandy.letsplot.samples.SampleHelper
import java.io.File

abstract class DataFrameSampleHelper(sampleName: String, subFolder: String = "samples") :
    SampleHelper(
        sampleName,
        subFolder,
        imagesFolder = "../docs/StardustDocs/images",
        resourcesFolder = "../docs/StardustDocs/resources",
        namePartsToRemove = listOf("_properties", "_strings"),
    ),
    TestBase {

    private val korroOutputLinesDir: File = File("build/korroOutputLines")

    /**
     * Outputs saved by the current sample, in the order they were saved.
     * A set, so saving the same output twice (e.g. two dataframes into one HTML file) inserts it only once.
     */
    private val korroOutputs = LinkedHashSet<String>()

    /**
     * Writes all outputs of the current sample into the korro output file, which korro inserts
     * after the sample code inside the `<!---FUN-->`…`<!---END-->` block.
     *
     * The file is named after the fully qualified name of the sample function without the
     * `_properties`/`_strings` suffix — this is how korro looks up an output for a sample or a group of samples.
     */
    private fun saveKorroOutput(output: String) {
        korroOutputs += output
        korroOutputLinesDir.mkdirs()
        File(korroOutputLinesDir, "${this::class.java.name}.${sampleFileName()}")
            .writeText(korroOutputs.joinToString(""))
    }

    override fun onDfHtmlSampleSaved(name: String) {
        saveKorroOutput(
            "\n<inline-frame src=\"./resources/$name.html\" width=\"100%\" height=\"500px\"></inline-frame>\n",
        )
    }

    override fun onPlotSampleSaved(name: String) {
        saveKorroOutput("\n<img src=\"$name.svg\" alt=\"$name\" width=\"705\"/>\n")
    }

    fun String.saveSample(addOutputLine: Boolean = true) {
        val text = if (addOutputLine) {
            "\nOutput:\n\n$this\n"
        } else {
            "\n$this\n"
        }
        saveKorroOutput(text)
    }

    fun CodeString.saveSample(addOutputLine: Boolean = true, addCodeBlock: Boolean = true) {
        val text = if (addCodeBlock) {
            "```kotlin\n$value\n```"
        } else {
            value
        }
        text.saveSample(addOutputLine)
    }

    /**
     * Saves [this] as a plain-text sample output, the counterpart of [CodeString.saveSample]
     * for values that are not Kotlin code — the `toString()` of a `Map`, a number, a type.
     */
    fun String.saveTextSample(addOutputLine: Boolean = true) {
        "```text\n$this\n```".saveSample(addOutputLine)
    }

    fun DataColumn<*>.saveDfHtmlSample() {
        toDataFrame().saveDfHtmlSample()
    }

    // TODO: might be changed as #1887 is fixed
    private val expandNestedFramesScript = DataFrameHtmlData(
        script =
            """
            (function () {
                function expandColumnGroups(df) {
                    for (let col of df.cols) {
                        if (col.parent === undefined && col.children.length > 0) col.expanded = true;
                    }
                }

                function expandNestedFrames(df, rootDf) {
                    for (let col of df.cols) {
                        for (let value of col.values) {
                            if (value && value.frameId !== undefined) {
                                rootDf.expandedFrames.add(value.frameId);
                                let child = rootDf.childFrames[value.frameId];
                                if (child) {
                                    expandColumnGroups(child);
                                    expandNestedFrames(child, rootDf);
                                }
                            }
                        }
                    }
                }

                document.querySelectorAll("table.dataframe").forEach(function (table) {
                    if (table.df && table.df.id === table.df.rootId) {
                        let rootDf = table.df;
                        expandNestedFrames(rootDf, rootDf);
                        DataFrame.renderTable(rootDf.id);
                    }
                });
            })();
            """.trimIndent(),
    )

    fun DataFrame<*>.toHtmlWithOpenedNestedDfs() =
        toStandaloneHtml(
            configuration = DisplayConfiguration(enableFallbackStaticTables = false),
            getFooter = { "" },
        ) + expandNestedFramesScript

    fun GroupBy<*, *>.toHtmlWithOpenedNestedDfs() = toDataFrame().toHtmlWithOpenedNestedDfs()
}
