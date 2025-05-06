package org.jetbrains.kotlinx.dataframe.plugin.impl.api

import org.jetbrains.kotlinx.dataframe.api.JoinType
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.plugin.impl.AbstractInterpreter
import org.jetbrains.kotlinx.dataframe.plugin.impl.Arguments
import org.jetbrains.kotlinx.dataframe.plugin.impl.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.impl.Present
import org.jetbrains.kotlinx.dataframe.plugin.impl.SimpleCol
import org.jetbrains.kotlinx.dataframe.plugin.impl.dataFrame
import org.jetbrains.kotlinx.dataframe.plugin.impl.enum
import org.jetbrains.kotlinx.dataframe.plugin.impl.ignore
import org.jetbrains.kotlinx.dataframe.plugin.impl.makeNullable

internal abstract class AbstractJoinWith() : AbstractInterpreter<PluginDataFrameSchema>() {
    val Arguments.receiver: PluginDataFrameSchema by dataFrame()
    val Arguments.right: PluginDataFrameSchema by dataFrame()
    val Arguments.joinExpression by ignore()

    fun Arguments.join(type: JoinType): PluginDataFrameSchema {
        val left = receiver.columns()
        val right = right.columns()

        val nameGenerator = ColumnNameGenerator()

        fun MutableList<SimpleCol>.addColumns(columns: List<SimpleCol>) {
            for (column in columns) {
                val uniqueName = nameGenerator.addUnique(column.name)
                add(column.rename(uniqueName))
            }
        }

        val result = buildList {
            when (type) {
                JoinType.Inner -> {
                    addColumns(left)
                    addColumns(right)
                }

                JoinType.Left -> {
                    addColumns(left)
                    addColumns(right.map { makeNullable(it) })
                }

                JoinType.Right -> {
                    addColumns(left.map { makeNullable(it) })
                    addColumns(right)
                }

                JoinType.Full -> {
                    addColumns(left.map { makeNullable(it) })
                    addColumns(right.map { makeNullable(it) })
                }

                JoinType.Filter -> addColumns(left)
                JoinType.Exclude -> addColumns(left)
            }
        }
        return PluginDataFrameSchema(result)
    }
}

internal class JoinWith : AbstractJoinWith() {
    val Arguments.type: JoinType by enum(defaultValue = Present(JoinType.Inner))

    override fun Arguments.interpret(): PluginDataFrameSchema {
        return join(type)
    }
}

internal class LeftJoinWith : AbstractJoinWith() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return join(JoinType.Left)
    }
}

internal class RightJoinWith : AbstractJoinWith() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return join(JoinType.Right)
    }
}

internal class FullJoinWith : AbstractJoinWith() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return join(JoinType.Full)
    }
}

internal class InnerJoinWith : AbstractJoinWith() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return join(JoinType.Inner)
    }
}

internal class FilterJoinWith : AbstractJoinWith() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return join(JoinType.Filter)
    }
}

internal class ExcludeJoinWith : AbstractJoinWith() {
    override fun Arguments.interpret(): PluginDataFrameSchema {
        return join(JoinType.Exclude)
    }
}
