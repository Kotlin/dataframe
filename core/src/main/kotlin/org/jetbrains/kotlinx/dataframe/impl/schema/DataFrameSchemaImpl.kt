package org.jetbrains.kotlinx.dataframe.impl.schema

import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.CompareResult
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

internal class DataFrameSchemaImpl(override val columns: Map<String, ColumnSchema>) : DataFrameSchema {

    override fun compare(other: DataFrameSchema): CompareResult {
        require(other is DataFrameSchemaImpl)
        if (this === other) return CompareResult.Equals
        var result = CompareResult.Equals
        columns.forEach {
            val otherColumn = other.columns[it.key]
            if (otherColumn == null) {
                result = result.combine(CompareResult.IsDerived)
            } else {
                result = result.combine(it.value.compare(otherColumn))
            }
            if (result == CompareResult.None) return CompareResult.None
        }
        other.columns.forEach {
            val thisField = columns[it.key]
            if (thisField == null) {
                result = result.combine(CompareResult.IsSuper)
                if (result == CompareResult.None) return CompareResult.None
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        return other is DataFrameSchema && compare(other).isEqual()
    }

    override fun toString(): String {
        return render()
    }

    override fun hashCode(): Int {
        return columns.hashCode()
    }
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
        sb.append(name + ":")
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
