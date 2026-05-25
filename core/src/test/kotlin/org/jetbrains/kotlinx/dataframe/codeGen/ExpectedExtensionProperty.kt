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
        appendLine("""    this["$columnName"] as $propertyType""")
        appendLine("} catch (e: Exception) {")
        appendLine("     when (e) {")
        appendLine(
            """        is IllegalArgumentException -> error(message = "Column not found exception in the generated DataFrame extension property '$renderedColumnName': " + e.getLocalizedMessage() + ". See  for more information.")""",
        )
        appendLine(
            """        is ClassCastException -> error(message = "Incorrect column type exception in generated DataFrame extension property '$renderedColumnName': " + e.getLocalizedMessage() + " See  for more information.")""",
        )
        appendLine(
            """        else -> error(message = "Unexpected exception in generated DataFrame extension property '$renderedColumnName'. Please report it to https://github.com/Kotlin/dataframe/issues." + "Exception message: " + e.toString())""",
        )
        appendLine("    }")
        append("} ")
    }
