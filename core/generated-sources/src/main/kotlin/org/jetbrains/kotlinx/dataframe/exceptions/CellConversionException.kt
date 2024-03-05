package org.jetbrains.kotlinx.dataframe.exceptions

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import kotlin.reflect.KType

public class CellConversionException(
    value: Any?,
    from: KType,
    to: KType,
    column: ColumnPath,
    public val row: Int?,
    override val cause: Throwable?
) : TypeConversionException(value, from, to, column) {
    override val message: String
        get() = "${super.message} in column $column, row $row"
}
