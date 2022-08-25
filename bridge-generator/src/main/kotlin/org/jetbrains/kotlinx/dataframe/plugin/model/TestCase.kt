package org.jetbrains.kotlinx.dataframe.plugin.model

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.GenerateConstructor
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema

@DataSchema
interface TestCase : DataRowSchema {
    val dfExpression: String
    val functionCalls: List<String>

    @GenerateConstructor
    companion object
}
