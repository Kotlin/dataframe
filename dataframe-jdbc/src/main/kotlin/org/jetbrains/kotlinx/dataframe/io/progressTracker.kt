package org.jetbrains.kotlinx.dataframe.io

import java.sql.Date
import java.sql.Time
import java.sql.Types
import io.github.oshai.kotlinlogging.KLogger
import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Blob
import java.sql.Clob
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.UUID

/**
 * Strategy interface for tracking progress during data loading.
 */
internal interface ProgressTracker {
    fun onStart()
    fun onRowLoaded()
    fun onComplete(rowCount: Int)

    companion object {
        fun create(logger: KLogger): ProgressTracker {
            return when {
                JdbcConfig.PROGRESS_ENABLED -> {
                    if (JdbcConfig.PROGRESS_DETAILED) {
                        DetailedProgressTracker(logger)
                    } else {
                        SimpleProgressTracker(logger)
                    }
                }
                logger.isDebugEnabled -> {
                    DetailedProgressTracker(logger)
                }
                else -> NoOpProgressTracker
            }
        }
    }
}

/**
 * No-op implementation - zero overhead when disabled.
 */
private object NoOpProgressTracker : ProgressTracker {
    override fun onStart() {}
    override fun onRowLoaded() {}
    override fun onComplete(rowCount: Int) {}
}

/**
 * Simple progress tracker - shows only basic information.
 */
private class SimpleProgressTracker(private val logger: KLogger) : ProgressTracker {
    private var rowsLoaded = 0

    override fun onStart() {}

    override fun onRowLoaded() {
        rowsLoaded++
        if (rowsLoaded % JdbcConfig.PROGRESS_INTERVAL == 0) {
            logger.debug { "Loaded $rowsLoaded rows" }
        }
    }

    override fun onComplete(rowCount: Int) {
        if (rowCount > 0) {
            logger.debug { "Loading complete: $rowCount rows" }
        }
    }
}

/**
 * Detailed progress tracker with statistics and memory estimation.
 */
internal class DetailedProgressTracker(private val logger: KLogger) : ProgressTracker {
    private val startTime = System.currentTimeMillis()
    private var rowsLoaded = 0
    private var firstRowSize: Long? = null
    private var estimatedTotalRows: Long? = null
    private var estimatedMemoryBytes: Long? = null
    private var memoryWarningShown = false

    override fun onStart() {}

    override fun onRowLoaded() {
        rowsLoaded++

        if (rowsLoaded % JdbcConfig.PROGRESS_INTERVAL == 0) {
            logDetailedProgress()
        }
    }

    override fun onComplete(rowCount: Int) {
        if (rowCount == 0) return

        val totalMillis = System.currentTimeMillis() - startTime
        val rowsPerSecond = if (totalMillis > 0) {
            (rowCount.toDouble() / totalMillis * 1000).toInt()
        } else {
            0
        }

        val memoryInfo = estimatedMemoryBytes?.let {
            val actualMemory = firstRowSize?.let { size -> size * rowCount } ?: it
            " using ${formatBytes(actualMemory)}"
        } ?: ""

        if (totalMillis > 0) {
            logger.debug {
                "Loading complete: $rowCount rows in ${totalMillis}ms (~$rowsPerSecond rows/sec)$memoryInfo"
            }
        } else {
            logger.debug { "Loading complete: $rowCount rows$memoryInfo" }
        }
    }

    /**
     * Estimates memory on first row and shows warnings.
     */
    fun estimateMemoryOnFirstRow(
        columnData: List<MutableList<Any?>>,
        tableColumns: List<TableColumnMetadata>,
        rs: ResultSet,
    ) {
        if (memoryWarningShown) return

        try {
            firstRowSize = calculateRowSize(columnData, tableColumns)
            estimatedTotalRows = estimateTotalRows(rs)

            if (firstRowSize != null && estimatedTotalRows != null) {
                estimatedMemoryBytes = firstRowSize!! * estimatedTotalRows!!

                if (estimatedMemoryBytes!! > 100 * 1024 * 1024) {
                    logger.debug {
                        "Estimated memory: ${formatBytes(estimatedMemoryBytes!!)} for ~$estimatedTotalRows rows"
                    }

                    if (estimatedMemoryBytes!! > 1024L * 1024L * 1024L) {
                        logger.warn {
                            "Large dataset detected (${formatBytes(estimatedMemoryBytes!!)}). " +
                                "Consider using 'limit' parameter."
                        }
                        memoryWarningShown = true
                    }
                }
            }
        } catch (e: Exception) {
            logger.debug(e) { "Failed to estimate memory" }
        }
    }

    private fun logDetailedProgress() {
        val elapsedMillis = System.currentTimeMillis() - startTime
        if (elapsedMillis == 0L) return

        val rowsPerSecond = (rowsLoaded.toDouble() / elapsedMillis * 1000).toInt()

        val progressPercent = estimatedTotalRows?.let {
            " (${(rowsLoaded * 100.0 / it).toInt()}%)"
        } ?: ""

        logger.debug {
            "Loaded $rowsLoaded rows$progressPercent in ${elapsedMillis}ms (~$rowsPerSecond rows/sec)"
        }
    }

    private fun calculateRowSize(
        columnData: List<MutableList<Any?>>,
        tableColumns: List<TableColumnMetadata>,
    ): Long {
        var size = 16L // DataRow overhead

        columnData.forEachIndexed { index, values ->
            val value = values.firstOrNull()
            val columnMetadata = tableColumns.getOrNull(index)
            size += estimateValueSize(value, columnMetadata)
        }

        size += columnData.size * 64L // DataFrame column overhead
        return size
    }

    private fun estimateValueSize(value: Any?, metadata: TableColumnMetadata?): Long {
        if (value != null) {
            return estimateActualValueSize(value)
        }

        if (metadata != null) {
            return estimateValueSizeFromMetadata(metadata)
        }

        return 8L
    }

    private fun estimateActualValueSize(value: Any): Long {
        return when (value) {
            is Boolean, is Byte -> 16
            is Short -> 16
            is Int -> 16
            is Long, is Double -> 24
            is Float -> 16
            is Char -> 16
            is String -> 40 + (value.length * 2L)
            is ByteArray -> 16 + value.size
            is CharArray -> 16 + (value.size * 2L)
            is BigInteger -> 32 + (value.bitLength() / 8)
            is BigDecimal -> {
                val unscaledValue = value.unscaledValue()
                48 + (unscaledValue.bitLength() / 8)
            }

            is LocalDate -> 24
            is LocalTime -> 24
            is LocalDateTime -> 48
            is Instant -> 24
            is OffsetDateTime -> 56
            is ZonedDateTime -> 64
            is Timestamp -> 32
            is Date -> 32
            is Time -> 32
            is UUID -> 32
            is Blob -> 48 + estimateBlobSize(value)
            is Clob -> 48 + estimateClobSize(value)
            else -> 48
        } as Long
    }

    private fun estimateValueSizeFromMetadata(metadata: TableColumnMetadata): Long {
        return when (metadata.jdbcType) {
            Types.BIT, Types.BOOLEAN -> 16
            Types.TINYINT -> 16
            Types.SMALLINT -> 16
            Types.INTEGER -> 16
            Types.BIGINT -> 24
            Types.REAL, Types.FLOAT -> 16
            Types.DOUBLE -> 24
            Types.NUMERIC, Types.DECIMAL -> 48 + (metadata.size / 4)
            Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR ->
                40 + (metadata.size.coerceAtMost(1000) * 2L)

            Types.DATE -> 24
            Types.TIME -> 24
            Types.TIMESTAMP -> 32
            Types.TIMESTAMP_WITH_TIMEZONE -> 56
            Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY ->
                16 + metadata.size.coerceAtMost(1000)

            Types.BLOB -> 64
            Types.CLOB -> 64
            else -> 48
        } as Long
    }

    private fun estimateBlobSize(blob: Blob): Long {
        return try {
            blob.length().coerceAtMost(10 * 1024 * 1024)
        } catch (e: Exception) {
            1024L
        }
    }

    private fun estimateClobSize(clob: Clob): Long {
        return try {
            clob.length() * 2L
        } catch (e: Exception) {
            2048L
        }
    }

    private fun estimateTotalRows(rs: ResultSet): Long? {
        return try {
            if (rs.type != ResultSet.TYPE_FORWARD_ONLY) {
                val currentRow = rs.row
                rs.last()
                val total = rs.row.toLong()
                rs.absolute(currentRow)
                total
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes bytes"
    bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
    bytes < 1024 * 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    else -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
}
