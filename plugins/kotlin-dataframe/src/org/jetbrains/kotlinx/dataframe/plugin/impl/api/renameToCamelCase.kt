package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlinx.dataframe.api.renameToCamelCase
import org.jetbrains.kotlinx.dataframe.impl.DELIMITED_STRING_REGEX
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleColumnGroup
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleDataColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleFrameColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.impl.processAsPluginDataFrame

class RenameToCamelCase : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return receiver.processAsPluginDataFrame {
            renameToCamelCase()
        }
//        return PluginDataFrameSchema(receiver.columns().renameToCamelCase())
    }
}

private fun String.toCamelCase() =
    if (this matches DELIMITED_STRING_REGEX || this[0].isUpperCase()) {
        this
            .toCamelCaseByDelimiters(DELIMITERS_REGEX)
            .replaceFirstChar { it.lowercaseChar() }
    } else {
        this
    }

private fun <T : SimpleCol> List<T>.renameToCamelCase(): List<T> = map { it.renameToCamelCase() }

private fun <T : SimpleCol> T.renameToCamelCase(): T =
    when (this) {
        is SimpleDataColumn -> copy(name.toCamelCase())
        is SimpleColumnGroup -> copy(name.toCamelCase(), columns().renameToCamelCase())
        is SimpleFrameColumn -> copy(name.toCamelCase(), columns().renameToCamelCase())
        else -> error("Unknown column type")
    } as T
