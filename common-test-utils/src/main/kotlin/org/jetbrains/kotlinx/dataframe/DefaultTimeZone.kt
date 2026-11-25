package org.jetbrains.kotlinx.dataframe

import java.time.ZoneId
import java.util.TimeZone as JavaTimeZone

/**
 * Runs [block] with the JVM default time zone set to [zoneId], restoring the previous one afterwards.
 *
 * Use it to pin how a conversion behaves under a specific default time zone:
 * `withDefaultTimeZone("Europe/Berlin") { columnOf(millis).convertToLocalDateTime()[0] }`.
 */
public fun <R> withDefaultTimeZone(zoneId: String, block: () -> R): R {
    val previous = JavaTimeZone.getDefault()
    // ZoneId.of fails fast on an unknown id; TimeZone.getTimeZone(String) would silently fall back to GMT.
    JavaTimeZone.setDefault(JavaTimeZone.getTimeZone(ZoneId.of(zoneId)))
    try {
        return block()
    } finally {
        JavaTimeZone.setDefault(previous)
    }
}
