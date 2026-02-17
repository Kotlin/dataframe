package org.jetbrains.kotlinx.dataframe.io

/**
 * Internal configuration for JDBC module behavior.
 * Controlled via system properties or environment variables.
 *
 * Example usage:
 * ```kotlin
 * // Enable progress with detailed statistics
 * System.setProperty("dataframe.jdbc.progress", "true")
 * System.setProperty("dataframe.jdbc.progress.detailed", "true")
 *
 * // Or via environment variables
 * export DATAFRAME_JDBC_PROGRESS=true
 * export DATAFRAME_JDBC_PROGRESS_DETAILED=true
 * ```
 */
internal object JdbcConfig {
    /**
     * Enable progress logging during data loading.
     *
     * When `true`, enables DEBUG-level progress messages and memory estimates.
     * When `false` (default), only respects logger level (e.g., DEBUG).
     *
     * System property: `-Ddataframe.jdbc.progress=true`
     * Environment variable: `DATAFRAME_JDBC_PROGRESS=true`
     * Default: `false`
     */
    @JvmStatic
    val PROGRESS_ENABLED: Boolean by lazy {
        System.getProperty("dataframe.jdbc.progress")?.toBoolean()
            ?: System.getenv("DATAFRAME_JDBC_PROGRESS")?.toBoolean()
            ?: false
    }

    /**
     * Enable detailed progress logging with statistics.
     *
     * When `true`: Shows row count, percentage, speed (rows/sec), memory usage
     * When `false`: Shows only basic "Loaded X rows" messages
     *
     * Only takes effect when progress is enabled.
     *
     * System property: `-Ddataframe.jdbc.progress.detailed=true`
     * Environment variable: `DATAFRAME_JDBC_PROGRESS_DETAILED=true`
     * Default: `true`
     */
    @JvmStatic
    val PROGRESS_DETAILED: Boolean by lazy {
        System.getProperty("dataframe.jdbc.progress.detailed")?.toBoolean()
            ?: System.getenv("DATAFRAME_JDBC_PROGRESS_DETAILED")?.toBoolean()
            ?: true
    }

    /**
     * Report progress every N rows.
     *
     * System property: `-Ddataframe.jdbc.progress.interval=5000`
     * Environment variable: `DATAFRAME_JDBC_PROGRESS_INTERVAL=5000`
     * Default: `1000`
     */
    @JvmStatic
    val PROGRESS_INTERVAL: Int by lazy {
        System.getProperty("dataframe.jdbc.progress.interval")?.toIntOrNull()
            ?: System.getenv("DATAFRAME_JDBC_PROGRESS_INTERVAL")?.toIntOrNull()
            ?: 1000
    }
}
