package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter

/**
 * The [predicate] is a [RowFilter] — a lambda that receives each [DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][ExtensionPropertiesAPIDocs] for convenient and type-safe access.
 */
internal interface RowFilterDescription
