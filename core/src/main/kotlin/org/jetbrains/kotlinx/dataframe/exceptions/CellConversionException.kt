package org.jetbrains.kotlinx.dataframe.exceptions

import kotlin.reflect.KType

public class CellConversionException(
    value: Any?,
    from: KType,
    to: KType,
    public val column: String,
    public val row: Int?,
    override val cause: Throwable?
) : TypeConversionException(value, from, to) {
    override val message: String
        get() = "${super.message} in column $column, row $row"
}
