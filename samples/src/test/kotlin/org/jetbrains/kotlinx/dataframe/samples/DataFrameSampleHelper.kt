package org.jetbrains.kotlinx.dataframe.samples

import java.io.File
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.CodeString
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.kandy.letsplot.samples.SampleHelper

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
        val text =
            if (addOutputLine) {
                "\nOutput:\n\n$this\n"
            } else {
                "\n$this\n"
            }

        korroOutputLinesDir.mkdirs()
        File(korroOutputLinesDir, fqnName).writeText(text)
    }

    fun CodeString.saveSample(addOutputLine: Boolean = true, addCodeBlock: Boolean = true) {
        val text =
            if (addCodeBlock) {
                "```kotlin\n$value\n```"
            } else {
                value
            }
        text.saveSample(addOutputLine)
    }

    fun DataColumn<*>.saveDfHtmlSample() {
        toDataFrame().saveDfHtmlSample()
    }
}
