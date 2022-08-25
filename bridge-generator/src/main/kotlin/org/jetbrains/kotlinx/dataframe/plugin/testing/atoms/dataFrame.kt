package org.jetbrains.kotlinx.dataframe.plugin.testing.atoms

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.testing.test

@Interpretable(DataFrameIdentity::class)
public fun dataFrame(v: DataFrame<*>): DataFrame<*> {
    return v
}

public class DataFrameIdentity : AbstractInterpreter<PluginDataFrameSchema>() {
    public val Arguments.v: PluginDataFrameSchema by dataFrame()

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return v
    }
}

internal interface Schema1 {
    val i: Int
}

internal fun injectionScope() {
    val df = dataFrameOf("i")(1, 2, 3).cast<Schema1>()
    test(id = "dataFrame_1", call = dataFrame(df))
}
