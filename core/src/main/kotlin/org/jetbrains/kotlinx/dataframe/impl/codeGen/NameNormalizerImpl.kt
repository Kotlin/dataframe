package org.jetbrains.kotlinx.dataframe.impl.codeGen

import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import java.util.Locale

public companion fun NameNormalizer.toCamelCaseFrom(normalizationDelimiters: Set<Char>): NameNormalizer {
    val delimitersSet = normalizationDelimiters.joinToString("", "[", "]")
    val delimitedStringRegex by lazy {
        ".+$delimitersSet.+".toRegex()
    }
    return NameNormalizer {
        when {
            normalizationDelimiters.isEmpty() -> it

            it matches delimitedStringRegex -> {
                it.lowercase(Locale.getDefault()).toCamelCaseByDelimiters(delimitersSet.toRegex())
            }

            else -> it
        }
    }
}

public companion fun NameNormalizer.id(): NameNormalizer = NameNormalizer { it }
