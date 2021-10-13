package org.jetbrains.kotlinx.dataframe.test

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher

fun containNTimes(substring: String, n: Int): Matcher<String?> = neverNullMatcher { value ->
    var start = 0
    fun positiveMessage(actual: Int) = "String should contain `$substring` exactly $n times, but contains $actual times"
    val negativeMessage = "String should contain `$substring` more or less than $n times"
    fun result(passed: Boolean, actual: Int) = MatcherResult(passed, positiveMessage(actual), negativeMessage)

    if (substring.isEmpty()) return@neverNullMatcher result(false, 0)

    for (k in 0..value.length + 1) {
        val i = value.indexOf(substring, start)
        if (i == -1) return@neverNullMatcher result(k == n, k)
        start = i + substring.length
    }

    // Impossible situation
    result(false, value.length + 1)
}
