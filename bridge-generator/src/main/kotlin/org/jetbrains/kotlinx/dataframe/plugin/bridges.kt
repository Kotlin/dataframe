package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.plugin.model.Bridge

val bridges by lazy { DataFrame.readJson("/bridges.json".resource()).cast<Bridge>(verify = true) }

fun String.resource() = object {}.javaClass.getResource(this)!!
