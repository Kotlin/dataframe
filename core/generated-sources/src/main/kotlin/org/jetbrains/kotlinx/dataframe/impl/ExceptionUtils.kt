package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.util.SUGGEST_IF_NULL

@PublishedApi
internal fun <T> T?.throwAndSuggestIfNull(operation: String): T = this ?: throwAndSuggestWhenNoElements(operation)

@PublishedApi
internal fun throwAndSuggestWhenNoElements(operation: String): Nothing =
    throw NoSuchElementException("No elements for the `$operation` operation. Use `${operation}OrNull` instead.")

@Deprecated(SUGGEST_IF_NULL, level = DeprecationLevel.HIDDEN)
@PublishedApi
internal fun <T> T?.suggestIfNull(operation: String): T = throwAndSuggestIfNull(operation)
