package org.jetbrains.kotlinx.dataframe.exceptions

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import kotlin.reflect.KType

public open class TypeConversionException(
    public val value: Any?,
    public val from: KType,
    public val to: KType,
    public val column: ColumnPath?,
    public val extraInformation: String? = null,
) : RuntimeException() {

    override val message: String
        get() = buildString {
            append("Failed to convert '$value' from $from to $to")
            if (column != null) {
                append(" in column '${column.joinToString()}'")
            }
            if (extraInformation != null) {
                append(": $extraInformation")
            }
        }
}
