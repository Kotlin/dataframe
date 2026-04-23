package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<ParseDf>.date: DataColumn<String> @JvmName("ParseDf_date") get() = this["date"] as DataColumn<String>
val DataRow<ParseDf>.date: String @JvmName("ParseDf_date") get() = this["date"] as String
val ColumnsScope<ParseDf>.value: DataColumn<String> @JvmName("ParseDf_value") get() = this["value"] as DataColumn<String>
val DataRow<ParseDf>.value: String @JvmName("ParseDf_value") get() = this["value"] as String
