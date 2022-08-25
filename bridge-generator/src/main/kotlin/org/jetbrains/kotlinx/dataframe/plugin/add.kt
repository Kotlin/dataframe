package org.jetbrains.kotlinx.dataframe.plugin

import generateDfFunctionTestStub
import org.jetbrains.kotlinx.dataframe.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf

object AddData {
    fun KotlinTypeFacade.test0(): Pair<PluginDataFrameSchema, PluginDataFrameSchema> {
        val schemaName = "Add0"
        val modify = "add(\"\") { 42 }"
        val id = "add0"
        return generateDfFunctionTestStub(
            expression = { dataFrameOf("a")(1) },
            schemaName = schemaName,
            modify = { it.add("") { 42 } },
            modifyRepr = modify,
            id = id,
            file = "add.kt"
        )
    }
    private val KotlinTypeFacade.test0Data get() = test0()
    val KotlinTypeFacade.test0Schema get() = test0Data.first
    val KotlinTypeFacade.test0After get() = test0Data.second
}
