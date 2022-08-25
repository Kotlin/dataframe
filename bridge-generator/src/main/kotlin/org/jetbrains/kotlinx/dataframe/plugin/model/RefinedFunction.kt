package org.jetbrains.kotlinx.dataframe.plugin.model

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema

@DataSchema
class RefinedFunction(
    val receiverType: String,
    val function: String,
    val functionReturnType: Type,
    val parameters: List<Parameter>,
    val startingSchema: Parameter
) : DataRowSchema
