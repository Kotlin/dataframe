package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender

import kotlinx.serialization.decodeFromString
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.pluginJsonFormat

@Interpretable(Schema1::class)
public fun schema1(): DataFrame<*> {
    return TODO("won't run")
}

public class Schema1 : AbstractInterpreter<PluginDataFrameSchema>() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(
            """{"columns":[{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"id","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.Int","nullable":false}},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"function","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"functionReturnType","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleFrameColumn","name":"parameters","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.FrameColumnTypeApproximation"},"name1":"parameters","columns":[{"name":"name","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}},{"name":"returnType","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}},{"name":"defaultValue","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":true}}],"nullable":false},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"receiverType","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}}]}"""
        )
    }
}
