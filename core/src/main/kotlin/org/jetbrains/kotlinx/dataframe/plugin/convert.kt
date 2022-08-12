package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.annotations.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.annotations.Arguments
import org.jetbrains.kotlinx.dataframe.annotations.ConvertApproximation
import org.jetbrains.kotlinx.dataframe.annotations.Present
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximation
import org.jetbrains.kotlinx.dataframe.api.Infer

internal class Convert0 : AbstractInterpreter<ConvertApproximation>() {
    val Arguments.columns: List<ColumnWithPathApproximation> by arg()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    override val Arguments.startingSchema get() = receiver

    override fun Arguments.interpret(): ConvertApproximation {
        return ConvertApproximation(receiver, columns.map { it.path.path })
    }
}

public class Convert2 : AbstractInterpreter<ConvertApproximation>() {
    public val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    public val Arguments.columns: List<String> by varargString()

    override fun Arguments.interpret(): ConvertApproximation {
        return ConvertApproximation(receiver, columns.map { listOf(it) })
    }
}

internal class Convert6 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.firstCol: String by string()
    val Arguments.cols: List<String> by varargString(defaultValue = Present(emptyList()))
    val Arguments.infer: Infer by enum(defaultValue = Present(Infer.Nulls))
    val Arguments.expression: TypeApproximation by type()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    override val Arguments.startingSchema get() = receiver

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val columns = (listOf(firstCol) + cols).map { listOf(it) }
        return convertImpl(ConvertApproximation(receiver, columns), expression)
    }
}

public class With0 : AbstractSchemaModificationInterpreter() {
    public val Arguments.receiver: ConvertApproximation by arg()
    public val Arguments.type: TypeApproximation by type(name("rowConverter"))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return convertImpl(receiver, type)
    }
}

internal fun convertImpl(receiver: ConvertApproximation, type: TypeApproximation): PluginDataFrameSchema {
    val columns = receiver.columns.toSet()

    fun simpleCol(it: SimpleCol, path: List<String>): SimpleCol = when (it) {
        is SimpleColumnGroup -> {
            val path1 = path + listOf(it.name)
            val newColumns = it.columns().map {
                simpleCol(it, path1)
            }
            SimpleColumnGroup(it.name, newColumns)
        }
        else -> if (path + listOf(it.name()) in columns) {
            it.changeType(type)
        } else {
            it
        }
    }

    val newColumns = receiver.schema.columns().map {
        simpleCol(it, emptyList())
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
