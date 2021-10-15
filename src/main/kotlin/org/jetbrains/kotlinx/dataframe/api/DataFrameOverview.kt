package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameBase
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.api.describeImpl
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType

// region describe

public fun <T> DataFrame<T>.describe(columns: ColumnsSelector<T, *> = { numberCols() }): DataFrame<ColumnDescriptionSchema> = describeImpl(this[columns])
public fun <T> DataColumn<T>.describe(): DataFrame<ColumnDescriptionSchema> = describeImpl(listOf(this))

@DataSchema
public interface GeneralColumnDescriptionSchema {
    public val column: String
    public val count: Int
    public val nulls: Int
}

@DataSchema
public interface ColumnDescriptionSchema : GeneralColumnDescriptionSchema {
    public val unique: Int
    public val top: Any
    public val freq: Int
    public val type: KType
}

@DataSchema
public interface NumberColumnDescriptionSchema : GeneralColumnDescriptionSchema {
    public val mean: Double
    public val min: Any
    public val max: KType
}

// endregion

// schema

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

// endregion
