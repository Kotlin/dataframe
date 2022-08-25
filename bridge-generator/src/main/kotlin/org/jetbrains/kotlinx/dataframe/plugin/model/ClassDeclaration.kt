package org.jetbrains.kotlinx.dataframe.plugin.model

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.GenerateConstructor
import org.jetbrains.kotlinx.dataframe.api.DataRowSchema
import org.jetbrains.kotlinx.dataframe.plugin.model.Parameter

@DataSchema
interface ClassDeclaration : DataRowSchema {
    val name: String
    val parameters: List<Parameter>

    @GenerateConstructor
    companion object
}
