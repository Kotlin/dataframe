package dfbuild

/*
 * Original version written in `core/src/main/kotlin/org/jetbrains/kotlinx/dataframe/impl/Utils.kt`
 */

// Single regex to split words by non-alphanumeric characters, camelCase, and numbers
private val CAMEL_DEFAULT_DELIMITERS_REGEX =
    Regex(
        "[^\\p{L}0-9]+|(?<=[\\p{Ll}])(?=[\\p{Lu}])|(?<=[\\p{Lu}])(?=[\\p{Lu}][\\p{Ll}])|(?<=\\d)(?=[\\p{L}])|(?<=[\\p{L}])(?=\\d)",
    )

fun String.toCamelCaseByDelimiters(
    delimiters: Regex = CAMEL_DEFAULT_DELIMITERS_REGEX,
    numberSeparator: String = "_",
): String =
    if (!this.any { it.isLetter() || it.isDigit() }) {
        this // If the string has no letters, return it unchanged
    } else {
        split(delimiters)
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
            .joinNumbers(numberSeparator)
            .joinToCamelCaseString()
    }

private fun List<String>.joinNumbers(separator: CharSequence): List<String> {
    val result = mutableListOf<String>()
    var i = 0

    while (i < this.size) {
        val current = this[i]
        if (current.all { it.isDigit() }) { // Check if the current element is a number
            val numberGroup = mutableListOf(current)
            while (i + 1 < this.size && this[i + 1].all { it.isDigit() }) {
                numberGroup.add(this[i + 1])
                i++
            }
            result.add(numberGroup.joinToString(separator)) // Join consecutive numbers with "_"
        } else {
            result.add(current)
        }
        i++
    }
    return result
}

private fun List<String>.joinToCamelCaseString(): String =
    mapIndexed { index, word ->
        if (index == 0) word.lowercase() else word.replaceFirstChar { it.uppercaseChar() }
    }.joinToString("")
