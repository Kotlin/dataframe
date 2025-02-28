package org.jetbrains.kotlinx.dataframe.impl

import java.math.BigDecimal
import java.math.BigInteger

internal fun <T> T?.throwIfNull(message: String): T = this ?: throw NoSuchElementException(message)

@PublishedApi
internal fun <T> T?.suggestIfNull(operation: String): T =
    throwIfNull("No elements for `$operation` operation. Use `${operation}OrNull` instead.")

@PublishedApi
internal fun BigInteger?.suggestIfNull(operation: String): BigInteger =
    throwIfNull(
        "The `$operation` operation either had no elements, or the result is NaN. Use `${operation}OrNull` instead.",
    )

@PublishedApi
internal fun BigDecimal?.suggestIfNull(operation: String): BigDecimal =
    throwIfNull(
        "The `$operation` operation either had no elements, or the result is NaN. Use `${operation}OrNull` instead.",
    )
