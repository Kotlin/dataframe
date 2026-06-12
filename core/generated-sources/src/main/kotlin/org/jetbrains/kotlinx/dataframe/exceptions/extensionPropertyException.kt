package org.jetbrains.kotlinx.dataframe.exceptions

private const val TROUBLESHOOTING_LINK =
    "https://kotlin.github.io/dataframe/data-schemas-and-extension-properties-troubleshooting.html"

/**
 * Internal method for handling extension properties. Should only be used inside `catch` block
 * in the extension property code generation.
 *
 * Handle possible exceptions while accessing columns via extension properties.
 * Can be improved by throwing a custom "extension property" exception (#1872).
 *
 * 1) IllegalArgumentException -> most probably column not found. Should be replaced with a custom "column not found" exception (#1871)
 * 2) ClassCastException -> most probably incorrect column type. Should be replaced with a custom "wrong column type"  exception (#1871)
 * 3) else -> unexpected exception.
 *
 * @param [e] any [Exception] caught inside `catch(e: Exception)`.
 * @param [columnName] rendered column name used in the resulting exception message.
 * @throws [IllegalStateException] with a custom message depends on type of [e] and contating [e] as cause.
 */
public fun handleExtensionPropertyException(e: Exception, columnName: String) {
    val msg = when (e) {
        is IllegalArgumentException ->
            "Column not found exception in the generated DataFrame extension property '$columnName': ${e.localizedMessage}. See $$TROUBLESHOOTING_LINK for more information."

        is ClassCastException ->
            "Incorrect column type exception in generated DataFrame extension property '$columnName': ${e.localizedMessage}. See $$TROUBLESHOOTING_LINK for more information."

        else ->
            "Unexpected exception in generated DataFrame extension property '$columnName'. Please report it to https://github.com/Kotlin/dataframe/issues. See $$TROUBLESHOOTING_LINK for more information. Exception message: $e."
    }
    throw IllegalStateException(msg, e)
}
