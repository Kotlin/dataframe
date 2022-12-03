package org.jetbrains.kotlinx.dataframe.exceptions

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.path
import kotlin.reflect.*
import kotlin.reflect.KType

public class TypeConverterNotFoundException(public val from: KType, public val to: KType, public val column: AnyCol?) : IllegalArgumentException() {

    override val message: String
        get() = "Type converter from $from to $to is not found" + (column?.let { " for column ${it.path.joinToString()}" } ?: "")
}
