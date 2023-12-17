package org.jetbrains.kotlinx.dataframe.exceptions

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import kotlin.reflect.*
import kotlin.reflect.KType

public open class TypeConversionException(
    public val value: Any?,
    public val from: KType,
    public val to: KType,
    public val column: ColumnPath?
) : RuntimeException() {

    override val message: String
        get() = "Failed to convert '$value' from $from to $to" + (column?.let { " in column ${it.joinToString()}" } ?: "")
}
