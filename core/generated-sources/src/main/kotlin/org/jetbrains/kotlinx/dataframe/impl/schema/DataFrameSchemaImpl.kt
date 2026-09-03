package org.jetbrains.kotlinx.dataframe.impl.schema

import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.CompareResult
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsDerived
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.IsSuper
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.Matches
import org.jetbrains.kotlinx.dataframe.schema.CompareResult.None
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT
import org.jetbrains.kotlinx.dataframe.schema.ComparisonMode.STRICT_FOR_NESTED_SCHEMAS
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchemaDoc
import org.jetbrains.kotlinx.dataframe.schema.plus

/**
 * [<code>DataFrameSchema</code>][DataFrameSchema] implementation.
 *
 * Represents the schema of a dataframe,
 * i.e., an ordered map of column names to their types.
 *
 * Column types are represented by [<code>ColumnSchema</code>][org.jetbrains.kotlinx.dataframe.schema.ColumnSchema]:
 *
 * - For value columns, it contains the [<code>type</code>][kotlin.reflect.KType] of the column.
 * - For column groups, it contains the [<code>DataFrameSchema</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema] of the nested columns.
 * - For frame columns, it contains the [<code>DataFrameSchema</code>][org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema] of the contained dataframes.
 *
 * Use [<code>compare</code>][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.compare]
 * to compare this schema with another schema using different [<code>comparison modes</code>][org.jetbrains.kotlinx.dataframe.schema.ComparisonMode].
 * The comparison ignores column order and can report how the schemas are related.
 *
 * Use [<code>equals</code>][org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl.equals]
 * to check whether two schemas are exactly equal, including column order.
 */
public class DataFrameSchemaImpl(override val columns: Map<String, ColumnSchema>) : DataFrameSchema {

    override fun compare(other: DataFrameSchema, comparisonMode: ComparisonMode): CompareResult {
        require(other is DataFrameSchemaImpl)
        if (this === other) return Matches

        var result: CompareResult = Matches

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

    /**
     * Returns `true` if, and only if,
     * this schema has the same columns **in the same order** as the [<code>other schema</code>][other].
     * The types must also match exactly.
     *
     * Each column is compared by [<code>ColumnSchema.equals</code>][ColumnSchema.equals], so nested schemas of
     * [<code>column group schemas</code>][ColumnSchema.Group] and [<code>frame column schemas</code>][ColumnSchema.Frame]
     * are compared by these same rules, recursively.
     *
     * Note that [<code>equals</code>][equals] and [<code>compare</code>][DataFrameSchema.compare] behave differently:
     * [<code>equals</code>][equals] requires schemas to match exactly, including column order,
     * while [<code>compare</code>][DataFrameSchema.compare] ignores column order and provides additional comparison options
     * (see [<code>ComparisonMode</code>][ComparisonMode]).
     *
     * Use [<code>compare</code>][DataFrameSchema.compare] when column order does not matter or
     * when you need additional [<code>comparison options</code>][ComparisonMode].
     *
     * @see [DataFrameSchema.compare]
     * @see [CompareResult.matches]
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataFrameSchema) return false
        if (columns.size != other.columns.size) return false

        val otherColumnsIterator = other.columns.entries.iterator()

        return columns.all { (name, schema) ->
            val (otherName, otherSchema) = otherColumnsIterator.next()
            name == otherName && schema == otherSchema
        }
    }

    override fun toString(): String = render()

    override fun hashCode(): Int = columns.toList().hashCode()
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
        }
    }
    return sb.toString()
}
