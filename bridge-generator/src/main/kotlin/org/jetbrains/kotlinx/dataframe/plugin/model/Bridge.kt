package org.jetbrains.kotlinx.dataframe.plugin.model

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema

@DataSchema
class Bridge(val type: Type,
             val approximation: String,
             val converter: String,
             val lens: String,
             val supported: Boolean = false) : DataRowSchema
