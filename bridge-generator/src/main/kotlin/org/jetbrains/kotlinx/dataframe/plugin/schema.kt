package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.plugin.codeGen.generateSchemaTestStub

object SchemaData {
    fun KotlinTypeFacade.schema1(): PluginDataFrameSchema = generateSchemaTestStub(
        name = "schema1"
    ) {
        DataFrame.readJson("/functions.json".resource())
    }

    fun KotlinTypeFacade.schema2(): PluginDataFrameSchema = generateSchemaTestStub(
        name = "schema2"
    ) {
        val name by columnOf("name")
        val returnType by columnOf("")
        val df = dataFrameOf(name, returnType)
        val functions by columnOf(df)
        val function by columnOf(name, returnType)
        val nestedGroup by columnOf(name)
        val group by columnOf(nestedGroup)
        dataFrameOf(name, functions, function, group)
    }
}
