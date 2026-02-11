package org.jetbrains.dataframe.gradle

import java.util.Locale

public fun String.toCamelCaseByDelimiters(delimiters: Regex): String =
    split(delimiters)
        .joinToCamelCaseString()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }

public fun List<String>.joinToCamelCaseString(): String =
    joinToString(separator = "") { s ->
        s.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
