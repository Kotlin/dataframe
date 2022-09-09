package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.SchemaData.schema1

@Interpretable(Schema1::class)
public fun schema1(): DataFrame<*> {
    return TODO("won't run")
}

public class Schema1 : AbstractInterpreter<PluginDataFrameSchema>() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return schema1()
    }
}
