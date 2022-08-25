package org.jetbrains.kotlinx.dataframe.plugin.model

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema

@DataSchema
class Parameter(
    val name: String,
    val returnType: Type,
    val defaultValue: String?,
) : DataRowSchema
