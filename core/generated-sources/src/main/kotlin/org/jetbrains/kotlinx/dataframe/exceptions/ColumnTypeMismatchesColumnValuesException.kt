package org.jetbrains.kotlinx.dataframe.exceptions

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.type

/**
 * Extension properties are generated according to [<code>DataColumn.type</code>][DataColumn.type] property
 * [<code>DataColumn.type</code>][DataColumn.type] must match types of [<code>DataColumn.values</code>][DataColumn.values], but it can fail to do so.
 * This causes [<code>ClassCastException</code>][ClassCastException] or [<code>NullPointerException</code>][NullPointerException] when you use extension property and actual value is of different type or is null.
 * If generated extension property causes this exception, this is a bug in the library
 * You can work around this problem by referring to [<code>column</code>][column] using String API
 */
public class ColumnTypeMismatchesColumnValuesException(public val column: AnyCol, cause: Throwable) :
    RuntimeException("Failed to convert column '${column.name()}'", cause)
