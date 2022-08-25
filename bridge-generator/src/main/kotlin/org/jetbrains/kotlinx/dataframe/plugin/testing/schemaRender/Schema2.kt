package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender

import kotlinx.serialization.decodeFromString
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.plugin.*
import org.jetbrains.kotlinx.dataframe.plugin.SchemaData.schema2

@Interpretable(Schema2::class)
public fun schema2(): DataFrame<*> {
    return TODO("won't run")
}

public class Schema2 : AbstractInterpreter<PluginDataFrameSchema>() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return schema2()
    }
}
