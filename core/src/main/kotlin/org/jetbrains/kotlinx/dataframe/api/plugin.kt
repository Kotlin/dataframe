package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.io.asURL
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.plugin.ReadCSV0
import org.jetbrains.kotlinx.dataframe.plugin.ReadJson0

@Refine("readJsonDefault_0")
@Interpretable(ReadJson0::class)
public fun DataFrame.Companion.readJsonDefault(path: String): AnyFrame = readJson(asURL(path))

// temporary hack to workaround backend codegen for default parameters
@Refine("readCSVDefault_0")
@Interpretable(ReadCSV0::class)
public fun DataFrame.Companion.readCSVDefault(fileOrUrl: String): AnyFrame = readCSV(fileOrUrl)
