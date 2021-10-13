package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrameBase
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.owner

public fun AnyFrame.schema(): String {
    val sb = StringBuilder()
    val indentSequence = "    "
    fun print(indent: Int, df: DataFrameBase<*>) {
        df.columns().forEach {
            sb.append(indentSequence.repeat(indent))
            sb.append(it.name + ":")
            when (it) {
                is ColumnGroup<*> -> {
                    sb.appendLine()
                    print(indent + 1, it.df)
                }
                is FrameColumn<*> -> {
                    sb.appendLine(" *")
                    val child = it.values.firstOrNull { it != null }
                    if (child != null) {
                        print(indent + 1, child)
                    }
                }
                is ValueColumn<*> -> {
                    sb.appendLine(" ${renderType(it.type)}")
                }
            }
        }
    }
    print(0, this)

    return sb.toString()
}

public fun AnyRow.schema(): String = owner.schema()
