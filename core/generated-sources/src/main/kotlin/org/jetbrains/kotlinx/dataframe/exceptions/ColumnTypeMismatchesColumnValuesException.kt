package org.jetbrains.kotlinx.dataframe.exceptions

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.type

/**
 * Extension properties are generated according to [DataColumn.type] property
 * [DataColumn.type] must match types of [DataColumn.values], but it can fail to do so.
 * This causes [ClassCastException] or [NullPointerException] when you use extension property and actual value is of different type or is null.
 * If generated extension property causes this exception, this is a bug in the library
 * You can work around this problem by referring to [column] using String API
 */
public class ColumnTypeMismatchesColumnValuesException(public val column: AnyCol, cause: Throwable) :
    RuntimeException("Failed to convert column '${column.name()}'", cause)
