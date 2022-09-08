package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.ConvertApproximation
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation

public class Convert2 : AbstractInterpreter<ConvertApproximation>() {
    public val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    public val Arguments.columns: List<String> by varargString()

    override fun Arguments.interpret(): ConvertApproximation {
        return ConvertApproximation(receiver, columns.map { listOf(it) })
    }
}

public class With0 : AbstractSchemaModificationInterpreter() {
    public val Arguments.receiver: ConvertApproximation by arg()
    public val Arguments.type: TypeApproximation by type(name("rowConverter"))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return convertImpl(receiver, type)
    }
}

private fun convertImpl(receiver: ConvertApproximation, type: TypeApproximation): PluginDataFrameSchema {
    val names = receiver.columns.toSet()

    val newColumns = receiver.schema.columns().map {
        if (listOf(it.name()) in names) {
            it.changeType(type)
        } else {
            it
        }
    }

    return PluginDataFrameSchema(newColumns)
}

internal class To0 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.receiver: ConvertApproximation by arg()
    val Arguments.typeArg0: TypeApproximation by arg()
    override val Arguments.startingSchema get() = receiver.schema

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return convertImpl(receiver, typeArg0)
    }
}
