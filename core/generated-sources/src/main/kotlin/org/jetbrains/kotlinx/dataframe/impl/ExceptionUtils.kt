package org.jetbrains.kotlinx.dataframe.impl

internal fun <T> T?.throwIfNull(message: String): T = this ?: throw NoSuchElementException(message)

@PublishedApi
internal fun <T> T?.suggestIfNull(operation: String): T = throwIfNull("No elements for `$operation` operation. Use `${operation}OrNull` instead.")
