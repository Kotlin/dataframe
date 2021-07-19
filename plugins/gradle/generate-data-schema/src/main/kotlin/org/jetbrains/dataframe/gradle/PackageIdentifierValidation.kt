package org.jetbrains.dataframe.gradle

import org.jetbrains.dataframe.keywords.SoftKeywords

// https://kotlinlang.org/spec/syntax-and-grammar.html#grammar-rule-identifier
internal fun String.isPackageJvmIdentifier(): Boolean {
    return split("(\\n|\\r\\n)*\\.".toRegex()).all { it.isSimpleJvmIdentifier() }
}

internal fun String.isSimpleJvmIdentifier(): Boolean {
    return isJvmIdentifier() || SoftKeywords.VALUES.contains(this)
}

internal fun String.isJvmIdentifier(): Boolean {
    return isValidKotlinIdentifier() && !contains("""[.\[\](){}]""".toRegex())
}

private fun String.isValidKotlinIdentifier(): Boolean {
    if (isEmpty()) return false
    if (all { it == '_' }) return false
    return asSequence()
        .runningFold(State.START) { state, char ->
            when (state) {
                State.START -> when {
                    char.isLetterOrUnderscore() -> State.SECOND_CHAR
                    char.isQuotes() -> State.QUOTES_STARTED
                    else -> State.ERROR
                }
                State.QUOTES_STARTED -> when {
                    char.isQuotedSymbol() -> State.QUOTES_STARTED
                    char.isQuotes() -> State.QUOTES_ENDED
                    else -> State.ERROR
                }
                State.SECOND_CHAR -> when {
                    char.isLetterOrUnderscore() || char.isDigit() -> State.SECOND_CHAR
                    else -> State.ERROR
                }
                State.QUOTES_ENDED, State.ERROR -> State.ERROR
            }
        }
        .none { it == State.ERROR }
}

private enum class State {
    START, QUOTES_STARTED, SECOND_CHAR, QUOTES_ENDED, ERROR
}

private fun Char.isLetterOrUnderscore(): Boolean {
    return category in letterCategories || this == '_'
}

private val letterCategories = setOf(
    CharCategory.LETTER_NUMBER,
    CharCategory.UPPERCASE_LETTER,
    CharCategory.TITLECASE_LETTER,
    CharCategory.OTHER_LETTER,
    CharCategory.MODIFIER_LETTER,
    CharCategory.LOWERCASE_LETTER
)

private fun Char.isQuotes() = this == '`'

private fun Char.isQuotedSymbol() = this != '\n' && this != '\r'
