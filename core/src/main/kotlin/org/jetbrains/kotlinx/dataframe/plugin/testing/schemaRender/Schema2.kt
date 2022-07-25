package org.jetbrains.kotlinx.dataframe.plugin.testing.schemaRender

import kotlinx.serialization.decodeFromString
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.plugin.*

@Interpretable(Schema2::class)
public fun schema2(): DataFrame<*> {
    return TODO("won't run")
}

public class Schema2 : AbstractInterpreter<PluginDataFrameSchema>() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return pluginJsonFormat.decodeFromString<PluginDataFrameSchema>(
            """{"columns":[{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"name","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleFrameColumn","name":"functions","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.FrameColumnTypeApproximation"},"name1":"functions","columns":[{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"name","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"returnType","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}}],"nullable":false},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleColumnGroup","name":"function","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.ColumnGroupTypeApproximation"},"name1":"function","columns":[{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"name","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"returnType","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}}]},{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleColumnGroup","name":"group","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.ColumnGroupTypeApproximation"},"name1":"group","columns":[{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleColumnGroup","name":"nestedGroup","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.ColumnGroupTypeApproximation"},"name1":"nestedGroup","columns":[{"type":"org.jetbrains.kotlinx.dataframe.plugin.SimpleCol","name":"name","valuesType":{"type":"org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl","fqName":"kotlin.String","nullable":false}}]}]}]}"""
        )
    }
}
