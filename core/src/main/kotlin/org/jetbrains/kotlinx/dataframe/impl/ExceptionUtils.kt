package org.jetbrains.kotlinx.dataframe.impl

@PublishedApi
internal fun <T> T?.throwAndSuggestIfNull(operation: String): T = this ?: throwAndSuggestWhenNoElements(operation)

@PublishedApi
internal fun throwAndSuggestWhenNoElements(operation: String): Nothing =
    throw NoSuchElementException("No elements for the `$operation` operation. Use `${operation}OrNull` instead.")
