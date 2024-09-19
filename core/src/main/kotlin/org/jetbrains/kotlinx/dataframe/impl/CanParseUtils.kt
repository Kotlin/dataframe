package org.jetbrains.kotlinx.dataframe.impl

import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Checks if the string can be parsed as a duration without throwing an exception.
 *
 * The logic is taken from [Duration.parse] (Kotlin version 2.0.20),
 * so it should return the same result.
 *
 * @param value the string to check
 */
internal fun Duration.Companion.canParse(value: String): Boolean {
    var length = value.length
    if (length == 0) return false
    var index = 0
    val infinityString = "Infinity"
    when (value[index]) {
        '+', '-' -> index++
    }
    val hasSign = index > 0
    when {
        length <= index -> return false

        value[index] == 'P' -> {
            if (++index == length) return false
            val nonDigitSymbols = "+-."
            var isTimeComponent = false
            var prevUnit: DurationUnit? = null
            while (index < length) {
                if (value[index] == 'T') {
                    if (isTimeComponent || ++index == length) return false
                    isTimeComponent = true
                    continue
                }
                val component = value.substringWhile(index) { it in '0'..'9' || it in nonDigitSymbols }
                if (component.isEmpty()) return false
                index += component.length
                val unitChar = value.getOrElse(index) { return false }
                index++
                val unit = durationUnitByIsoChar(unitChar, isTimeComponent)
                if (prevUnit != null && prevUnit <= unit) return false
                prevUnit = unit
            }
        }

        value.regionMatches(
            thisOffset = index,
            other = infinityString,
            otherOffset = 0,
            length = maxOf(length - index, infinityString.length),
            ignoreCase = true,
        ) -> return true

        else -> {
            // parse default string format
            var prevUnit: DurationUnit? = null
            var afterFirst = false
            var allowSpaces = !hasSign
            if (hasSign && value[index] == '(' && value.last() == ')') {
                allowSpaces = true
                if (++index == --length) return false
            }
            while (index < length) {
                if (afterFirst && allowSpaces) {
                    index = value.skipWhile(index) { it == ' ' }
                }
                afterFirst = true
                val component = value.substringWhile(index) { it in '0'..'9' || it == '.' }
                if (component.isEmpty()) return false
                index += component.length
                val unitName = value.substringWhile(index) { it in 'a'..'z' }
                index += unitName.length
                val unit = durationUnitByShortName(unitName)
                if (prevUnit != null && prevUnit <= unit) return false
                prevUnit = unit
                val dotIndex = component.indexOf('.')
                if (dotIndex > 0) {
                    if (index < length) return false
                }
            }
        }
    }
    return true
}

/**
 * Checks if the string can be parsed as a java duration without throwing an exception.
 */
internal fun javaDurationCanParse(value: String): Boolean = durationRegex.matches(value)

// regex from java.time.Duration.Lazy.PATTERN
private val durationRegex = Regex(
    pattern = "[-+]?P?:[-+]?[0-9]+D?T?:[-+]?[0-9]+H??:[-+]?[0-9]+M??:[-+]?[0-9]+?:[.,][0-9]{0,9}?S??",
    option = RegexOption.IGNORE_CASE,
)

/**
 * Copy of [kotlin.time.substringWhile] (Kotlin version 2.0.20).
 */
private inline fun String.substringWhile(startIndex: Int, predicate: (Char) -> Boolean): String =
    substring(startIndex, skipWhile(startIndex, predicate))

/**
 * Copy of [kotlin.time.skipWhile] (Kotlin version 2.0.20).
 */
private inline fun String.skipWhile(startIndex: Int, predicate: (Char) -> Boolean): Int {
    var i = startIndex
    while (i < length && predicate(this[i])) i++
    return i
}

/**
 * Copy of [kotlin.time.durationUnitByIsoChar] (Kotlin version 2.0.20).
 */
private fun durationUnitByIsoChar(isoChar: Char, isTimeComponent: Boolean): DurationUnit =
    when {
        !isTimeComponent -> {
            when (isoChar) {
                'D' -> DurationUnit.DAYS

                else -> throw IllegalArgumentException(
                    "Invalid or unsupported duration ISO non-time unit: $isoChar",
                )
            }
        }

        else -> {
            when (isoChar) {
                'H' -> DurationUnit.HOURS
                'M' -> DurationUnit.MINUTES
                'S' -> DurationUnit.SECONDS
                else -> throw IllegalArgumentException("Invalid duration ISO time unit: $isoChar")
            }
        }
    }

/**
 * Copy of [kotlin.time.durationUnitByShortName] (Kotlin version 2.0.20).
 */
private fun durationUnitByShortName(shortName: String): DurationUnit =
    when (shortName) {
        "ns" -> DurationUnit.NANOSECONDS
        "us" -> DurationUnit.MICROSECONDS
        "ms" -> DurationUnit.MILLISECONDS
        "s" -> DurationUnit.SECONDS
        "m" -> DurationUnit.MINUTES
        "h" -> DurationUnit.HOURS
        "d" -> DurationUnit.DAYS
        else -> throw IllegalArgumentException("Unknown duration unit short name: $shortName")
    }
