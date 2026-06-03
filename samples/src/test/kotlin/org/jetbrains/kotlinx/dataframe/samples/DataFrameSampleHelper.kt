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
        "../docs/StardustDocs/images",
        "../docs/StardustDocs/resources",
    ),
    TestBase {

    fun fqnTestName(): String = "${this::class.java.name}.${testName.methodName}"

    private val korroOutputLinesDir: File = File("build/korroOutputLines")

    fun String.saveSample(addOutputLine: Boolean = true) {
        val fqnName = fqnTestName().replace("_dataframe", "")
        val text = if (addOutputLine) {
            "\nOutput:\n\n$this\n"
        } else {
            "\n$this\n"
        }

        korroOutputLinesDir.mkdirs()
        File(korroOutputLinesDir, fqnName).writeText(text)
    }

    fun CodeString.saveSample(addOutputLine: Boolean = true, addCodeBlock: Boolean = true) {
        val text = if (addCodeBlock) {
            "```kotlin\n$value\n```"
        } else {
            value
        }
        text.saveSample(addOutputLine)
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

    fun DataFrame<*>.toExpandedHtml() =
        toStandaloneHtml(
            configuration = DisplayConfiguration(enableFallbackStaticTables = false),
            getFooter = { "" },
        ) + expandNestedFramesScript

    fun GroupBy<*, *>.toExpandedHtml() = toDataFrame().toExpandedHtml()
}
