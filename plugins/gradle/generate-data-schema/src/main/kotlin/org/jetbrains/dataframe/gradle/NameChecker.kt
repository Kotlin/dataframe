package org.jetbrains.dataframe.gradle

internal object NameChecker {
    fun checkValidIdentifier(identifiers: String) {
        check(identifiers.none { char -> char in NAME_RESTRICTED_CHARS }) {
            "$identifiers contains illegal characters: ${NAME_RESTRICTED_CHARS.intersect(identifiers.toSet()).joinToString(",")}"
        }
    }

    fun checkValidPackageName(name: String) {
        val identifiers = name
            .split(PACKAGE_IDENTIFIER_DELIMITER)
            .joinToString("")
        checkValidIdentifier(identifiers)
    }

    // https://github.com/JetBrains/kotlin/blob/1.5.30/compiler/frontend.java/src/org/jetbrains/kotlin/resolve/jvm/checkers/JvmSimpleNameBacktickChecker.kt
    private val INVALID_CHARS = setOf('.', ';', '[', ']', '/', '<', '>', ':', '\\')
    private val DANGEROUS_CHARS = setOf('?', '*', '"', '|', '%')
    // QuotedSymbol https://kotlinlang.org/spec/syntax-and-grammar.html#identifiers
    private val RESTRICTED_CHARS = setOf('`', '\r', '\n')
    private val NAME_RESTRICTED_CHARS = INVALID_CHARS + DANGEROUS_CHARS + RESTRICTED_CHARS

    // https://kotlinlang.org/spec/syntax-and-grammar.html#grammar-rule-identifier
    val PACKAGE_IDENTIFIER_DELIMITER = "(\\n|\\r\\n)*\\.".toRegex()
}
