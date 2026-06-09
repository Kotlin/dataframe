package org.jetbrains.kotlinx.dataframe.codeGen

internal fun expectedExtensionProperty(
    receiverType: String,
    name: String,
    propertyType: String,
    jvmName: String,
    visibility: String = "",
    columnName: String = name.removeSurrounding("`"),
): String =
    buildString {
        val renderedColumnName = columnName
            .replace("\\", "\\\\")
            .replace("$", "\\\$")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

        appendLine("""${visibility}val $receiverType.$name: $propertyType @JvmName("$jvmName") get() = try {""")
        appendLine("""    this["$renderedColumnName"] as $propertyType""")
        appendLine("} catch (e: kotlin.Exception) {")
        appendLine("    val msg = when (e) {")
        appendLine("        is kotlin.IllegalArgumentException ->")
        appendLine(
            "            \"Column not found exception in the generated DataFrame extension property " +
                "'$renderedColumnName': \${e.localizedMessage}. See  for more information.\"",
        )
        appendLine("")
        appendLine("        is kotlin.ClassCastException ->")
        appendLine(
            "            \"Incorrect column type exception in generated DataFrame extension property " +
                $$"'$$renderedColumnName': ${e.localizedMessage}. See  for more information.\"",
        )
        appendLine("")
        appendLine("        else ->")
        appendLine(
            "            \"Unexpected exception in generated DataFrame extension property " +
                "'$renderedColumnName'. Please report it to https://github.com/Kotlin/dataframe/issues. " +
                $$"Exception message: $e.\"",
        )
        appendLine("    }")
        appendLine("    throw IllegalStateException(msg, e)")
        append("} ")
    }
