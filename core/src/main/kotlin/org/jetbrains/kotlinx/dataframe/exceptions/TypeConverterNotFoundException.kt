package org.jetbrains.kotlinx.dataframe.exceptions

import kotlin.reflect.*

public class TypeConverterNotFoundException(public val from: KType, public val to: KType) : IllegalArgumentException() {

    override val message: String
        get() = "Type converter from $from to $to is not found"
}
