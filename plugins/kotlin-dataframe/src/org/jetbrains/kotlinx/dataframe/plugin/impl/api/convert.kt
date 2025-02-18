package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.plugin.extensions.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.plugin.impl.Absent
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractSchemaModificationInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.Present
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleColumnGroup
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleDataColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleFrameColumn
import org.jetbrains.kotlinx.dataframe.plugin.impl.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.impl.enum
import org.jetbrains.kotlinx.dataframe.plugin.impl.ignore
import org.jetbrains.kotlinx.dataframe.plugin.impl.simpleColumnOf
import org.jetbrains.kotlinx.dataframe.plugin.impl.type

internal class Convert0 : AbstractInterpreter<ConvertApproximation>() {
    val Arguments.columns: ColumnsResolver by arg()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    override val Arguments.startingSchema get() = receiver

    override fun Arguments.interpret(): ConvertApproximation {
        return ConvertApproximation(receiver, columns.resolve(receiver).map { it.path.path })
    }
}

class Convert2 : AbstractInterpreter<ConvertApproximation>() {
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    val Arguments.columns: List<String> by arg(defaultValue = Absent)

    override fun Arguments.interpret(): ConvertApproximation {
        return ConvertApproximation(receiver, columns.map { listOf(it) })
    }
}

class ConvertApproximation(val schema: PluginDataFrameSchema, val columns: List<List<String>>)

internal class Convert6 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.firstCol: String by arg()
    val Arguments.cols: List<String> by arg(defaultValue = Present(emptyList()))
    val Arguments.infer: Infer by enum(defaultValue = Present(Infer.Nulls))
    val Arguments.expression: TypeApproximation by type()
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    override val Arguments.startingSchema get() = receiver

    override fun Arguments.interpret(): PluginDataFrameSchema {
        val columns = (listOf(firstCol) + cols).map { listOf(it) }
        return convertImpl(receiver, columns, expression)
    }
}

class With0 : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver: ConvertApproximation by arg()
    val Arguments.infer by ignore()
    val Arguments.type: TypeApproximation by type(name("rowConverter"))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return convertImpl(receiver.schema, receiver.columns, type)
    }
}

class PerRowCol : AbstractSchemaModificationInterpreter() {
    val Arguments.receiver: ConvertApproximation by arg()
    val Arguments.infer by ignore()
    val Arguments.type: TypeApproximation by type(name("expression"))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return convertImpl(receiver.schema, receiver.columns, type)
    }
}

internal fun KotlinTypeFacade.convertImpl(
    pluginDataFrameSchema: PluginDataFrameSchema,
    columns: List<List<String>>,
    type: TypeApproximation
): PluginDataFrameSchema {
    return pluginDataFrameSchema.map(columns.toSet()) { path, column ->
        val unwrappedType = type.type
        simpleColumnOf(column.name, unwrappedType)
    }
}

internal fun PluginDataFrameSchema.map(selected: ColumnsSet, transform: ColumnMapper): PluginDataFrameSchema {
    return PluginDataFrameSchema(
        f(columns(), transform, selected, emptyList())
    )
}

internal typealias ColumnsSet = Set<List<String>>

internal typealias ColumnMapper = (List<String>, SimpleCol) -> SimpleCol

internal fun f(columns: List<SimpleCol>, transform: ColumnMapper, selected: ColumnsSet, path: List<String>): List<SimpleCol> {
    return columns.map {
        val fullPath = path + listOf(it.name)
        when (it) {
            is SimpleColumnGroup -> if (fullPath in selected) {
                transform(fullPath, it)
            } else {
                it.map(transform, selected, fullPath)
            }
            is SimpleFrameColumn -> if (fullPath in selected) {
                transform(fullPath, it)
            } else {
                it.map(transform, selected, fullPath)
            }
            is SimpleDataColumn -> if (fullPath in selected) {
                transform(path, it)
            } else {
                it
            }
        }
    }
}

internal fun SimpleColumnGroup.map(transform: ColumnMapper, selected: ColumnsSet, path: List<String>): SimpleColumnGroup {
    return SimpleColumnGroup(
        name,
        f(columns(), transform, selected, path)
    )
}

internal fun SimpleFrameColumn.map(transform: ColumnMapper, selected: ColumnsSet, path: List<String>): SimpleFrameColumn {
    return SimpleFrameColumn(
        name,
        f(columns(), transform, selected, path)
    )
}

internal class To0 : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.receiver: ConvertApproximation by arg()
    val Arguments.typeArg0: TypeApproximation by arg()
    override val Arguments.startingSchema get() = receiver.schema

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return convertImpl(receiver.schema, receiver.columns, typeArg0)
    }
}
