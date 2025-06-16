package org.jetbrains.kotlinx.dataframe.impl.schema

import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.CompareResult
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.Equals
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsDerived
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsSuper
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.None
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.schema.plus
import kotlin.collections.forEach

public class DataFrameSchemaImpl(override val columns: Map<String, ColumnSchema>) : DataFrameSchema {

    override fun compare(other: DataFrameSchema, comparisonMode: ComparisonMode): CompareResult {
        require(other is DataFrameSchemaImpl)
        if (this === other) return Equals

        var result: CompareResult = Equals

        // check for each column in this schema if there is a column with the same name in the other schema
        // - if so, check those schemas for equality, taking comparisonMode into account
        // - if not, consider the other schema derived from this (or unrelated (None) if comparisonMode == STRICT)
        this.columns.forEach { (thisColName, thisSchema) ->
            val otherSchema = other.columns[thisColName]
            result += when {
                otherSchema != null -> {
                    // increase comparisonMode strictness when dealing with nested schemas of FrameColumns or ColumnGroups
                    val newComparisonMode =
                        if (comparisonMode == STRICT_FOR_NESTED_SCHEMAS && thisSchema !is ColumnSchema.Value) {
                            STRICT
                        } else {
                            comparisonMode
                        }

                    thisSchema.compare(other = otherSchema, comparisonMode = newComparisonMode)
                }

                else -> if (comparisonMode == STRICT) None else IsDerived
            }
            if (result == None) return None
        }
        // then check for each column in the other schema if there is a column with the same name in this schema
        // if not, consider the other schema as super to this (or unrelated (None) if comparisonMode == STRICT)
        other.columns.forEach { (otherColName, _) ->
            if (this.columns[otherColName] != null) return@forEach
            result += if (comparisonMode == STRICT) None else IsSuper
            if (result == None) return None
        }
        return result
    }

    override fun equals(other: Any?): Boolean = other is DataFrameSchema && this.compare(other).isEqual()

    override fun toString(): String = render()

    override fun hashCode(): Int = columns.hashCode()
}

internal fun DataFrameSchemaImpl.render(): String {
    val sb = StringBuilder()
    val indentSequence = "    "
    columns.render(0, sb, indentSequence)
    return sb.toString()
}

internal fun Map<String, ColumnSchema>.render(indent: Int, sb: StringBuilder, indentSequence: String): String {
    entries.forEachIndexed { i, (name, columnSchema) ->
        sb.append(indentSequence.repeat(indent))
        sb.append("$name:")
        when (columnSchema) {
            is ColumnSchema.Group -> {
                sb.appendLine()
                columnSchema.schema.columns.render(indent + 1, sb, indentSequence)
                sb.appendLine()
            }

            is ColumnSchema.Frame -> {
                sb.appendLine(" *")
                columnSchema.schema.columns.render(indent + 1, sb, indentSequence)
                sb.appendLine()
            }

            is ColumnSchema.Value -> {
                sb.append(" ${renderType(columnSchema.type)}")
                if (i != size - 1) {
                    sb.appendLine()
                }
            }

            else -> throw NotImplementedError(columnSchema::class.toString())
        }
    }
    return sb.toString()
}
