package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter

/**
 * The [predicate] is a [RowFilter] — a lambda that receives each [DataRow] both as `this` and `it`,
 * allowing you to define a [Boolean] condition using the row's values,
 * including through [extension properties][ExtensionPropertiesAPIDocs] for convenient access.
 */
internal interface RowFilterDescription
