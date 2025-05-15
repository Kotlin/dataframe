package org.jetbrains.kotlinx.dataframe.samples

import org.jetbrains.kotlinx.kandy.letsplot.samples.SampleHelper

abstract class DataFrameSampleHelper(sampleName: String, subFolder: String = "samples") :
    SampleHelper(
        sampleName,
        subFolder,
        "../docs/StardustDocs/images",
        "../docs/StardustDocs/resources",
    )
