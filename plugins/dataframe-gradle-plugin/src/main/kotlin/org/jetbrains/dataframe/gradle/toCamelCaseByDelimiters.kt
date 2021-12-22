package org.jetbrains.dataframe.gradle

fun String.toCamelCaseByDelimiters(delimiters: Regex): String {
    return split(delimiters).joinToCamelCaseString().decapitalize()
}

fun List<String>.joinToCamelCaseString(): String {
    return joinToString(separator = "") { it.capitalize() }
}
