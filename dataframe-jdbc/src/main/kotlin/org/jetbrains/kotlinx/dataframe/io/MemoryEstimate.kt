package org.jetbrains.kotlinx.dataframe.io

/**
 * Result of memory estimation for a database query.
 */
public data class MemoryEstimate(
    /** Estimated number of rows */
    val estimatedRows: Long,

    /** Estimated bytes per row (including DataFrame overhead) */
    val bytesPerRow: Long,

    /** Total estimated memory in bytes */
    val totalBytes: Long,

    /** Human-readable size */
    val humanReadable: String,

    /** Whether this is an exact count or estimate */
    val isExact: Boolean,
) {
    /** Total estimated memory in megabytes */
    val megabytes: Double get() = totalBytes / (1024.0 * 1024.0)

    /** Total estimated memory in gigabytes */
    val gigabytes: Double get() = totalBytes / (1024.0 * 1024.0 * 1024.0)

    /**
     * Returns true if estimated memory exceeds the given threshold.
     */
    public fun exceeds(thresholdBytes: Long): Boolean = totalBytes > thresholdBytes

    /**
     * Returns true if estimated memory exceeds the given threshold in gigabytes.
     */
    public fun exceedsGb(thresholdGb: Double): Boolean = gigabytes > thresholdGb

    /**
     * Calculates recommended limit to stay under the given memory threshold.
     */
    public fun recommendedLimit(maxBytes: Long): Int {
        if (bytesPerRow == 0L) return Int.MAX_VALUE
        val recommendedRows = maxBytes / bytesPerRow
        return recommendedRows.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    override fun toString(): String = buildString {
        append("Memory Estimate: $humanReadable")
        append(" (~$estimatedRows rows × $bytesPerRow bytes/row)")
        if (!isExact) append(" [approximate]")
    }
}
