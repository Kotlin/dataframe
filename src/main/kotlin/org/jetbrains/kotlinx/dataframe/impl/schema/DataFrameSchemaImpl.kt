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
        val sb = StringBuilder()
        val indentSequence = "    "
        fun print(indent: Int, schema: DataFrameSchema) {
            schema.columns.forEach { (name, columnSchema) ->
                sb.append(indentSequence.repeat(indent))
                sb.append(name + ":")
                when (columnSchema) {
                    is ColumnSchema.Group -> {
                        sb.appendLine()
                        print(indent + 1, columnSchema.schema)
                    }
                    is ColumnSchema.Frame -> {
                        sb.appendLine(" *")
                        print(indent + 1, columnSchema.schema)
                    }
                    is ColumnSchema.Value -> {
                        sb.appendLine(" ${renderType(columnSchema.type)}")
                    }
                }
            }
        }
        print(0, this)

        return sb.toString()
    }

    override fun hashCode(): Int {
        return columns.hashCode()
    }
}
