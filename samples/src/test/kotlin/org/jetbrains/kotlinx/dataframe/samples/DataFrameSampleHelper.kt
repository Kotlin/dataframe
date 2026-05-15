package org.jetbrains.kotlinx.dataframe.samples

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.CodeString
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
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

    private val korroOutputLinesDir: File
        get() = File(System.getProperty("dataframe.samples.korroOutputLinesDir", "build/korroOutputLines"))

    fun String.saveSample(addOutputLine: Boolean = true) {
        val fqnName = fqnTestName().replace("_dataframe", "")
        val shortName = testName.methodName.replace("_dataframe", "")
        val text = if (addOutputLine) {
            "\nOutput:\n\n$this\n"
        } else {
            "\n$this\n"
        }

        korroOutputLinesDir.mkdirs()
        listOf(fqnName, shortName).distinct().forEach { name ->
            File(korroOutputLinesDir, name).writeText(text)
        }
    }

    fun CodeString.saveSample(addOutputLine: Boolean = true) {
        "```kotlin\n$value\n```".saveSample(addOutputLine)
    }

    fun DataColumn<*>.saveDfHtmlSample() {
        toDataFrame().saveDfHtmlSample()
    }
}
