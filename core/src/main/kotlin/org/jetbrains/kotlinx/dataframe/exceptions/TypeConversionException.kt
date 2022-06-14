package org.jetbrains.kotlinx.dataframe.exceptions

import kotlin.reflect.*

public class TypeConversionException(public val value: Any?, public val from: KType, public val to: KType) : RuntimeException() {

    override val message: String
        get() = "Failed to convert '$value' from $from to $to"
}
