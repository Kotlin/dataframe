package org.jetbrains.kotlinx.dataframe.impl

/**
 * Checks if the string can be parsed as a java duration without throwing an exception.
 */
internal fun javaDurationCanParse(value: String): Boolean = isoDurationRegex.matches(value)

/**
 * regex from [java.time.Duration.Lazy.PATTERN], it represents the ISO-8601 duration format.
 */
private val isoDurationRegex = Regex(
    """([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?""",
    RegexOption.IGNORE_CASE,
)
