package org.jetbrains.kotlinx.dataframe.plugin.model

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema

@DataSchema
data class Type(val name: String, val vararg: Boolean)
