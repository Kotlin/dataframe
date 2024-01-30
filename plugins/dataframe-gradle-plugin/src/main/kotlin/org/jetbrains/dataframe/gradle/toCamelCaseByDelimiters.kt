package org.jetbrains.dataframe.gradle

import java.util.Locale

fun String.toCamelCaseByDelimiters(delimiters: Regex): String {
    return split(delimiters).joinToCamelCaseString().replaceFirstChar { it.lowercase(Locale.getDefault()) }
}

fun List<String>.joinToCamelCaseString(): String {
    return joinToString(separator = "") { s -> s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
}
