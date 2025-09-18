package org.jetbrains.kotlinx.dataframe.samples

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.kandy.letsplot.samples.SampleHelper

abstract class DataFrameSampleHelper(sampleName: String, subFolder: String = "samples") :
    SampleHelper(
        sampleName,
        subFolder,
        "../docs/StardustDocs/images",
        "../docs/StardustDocs/resources",
    ) {

    fun DataColumn<*>.saveDfHtmlSample() {
        toDataFrame().saveDfHtmlSample()
    }
}
