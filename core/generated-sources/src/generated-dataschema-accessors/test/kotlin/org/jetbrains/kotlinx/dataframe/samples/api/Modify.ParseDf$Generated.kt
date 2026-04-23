package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
interface ParseDf {
    val date: String
    val value: String
}
