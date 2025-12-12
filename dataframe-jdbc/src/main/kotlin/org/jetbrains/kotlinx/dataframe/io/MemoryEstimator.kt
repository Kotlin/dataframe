package org.jetbrains.kotlinx.dataframe.io

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import java.sql.Connection
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

/**
 * Estimates memory usage for database queries WITHOUT loading all data.
 *
 * Strategy:
 * 1. Load 10 sample rows to calculate average row size
 * 2. Use COUNT(*) or database statistics for total row count
 * 3. Multiply to get total memory estimate
 */
internal object MemoryEstimator {

    private const val SAMPLE_SIZE = 10
    private const val COUNT_TIMEOUT_SECONDS = 5

    /**
     * Estimates memory for a table.
     */
    fun estimateTable(
        connection: Connection,
        tableName: String,
        dbType: DbType,
        limit: Int?,
    ): MemoryEstimate {
        val bytesPerRow = estimateAverageRowSize(connection, tableName, dbType)
        val totalRows = limit?.toLong() ?: estimateTableRowCount(connection, tableName, dbType)
        val totalBytes = bytesPerRow * totalRows

        return MemoryEstimate(
            estimatedRows = totalRows,
            bytesPerRow = bytesPerRow,
            totalBytes = totalBytes,
            humanReadable = formatBytes(totalBytes),
            isExact = limit != null,
        )
    }

    /**
     * Estimates memory for a custom SQL query.
     */
    fun estimateQuery(
        connection: Connection,
        sqlQuery: String,
        dbType: DbType,
        limit: Int?,
    ): MemoryEstimate {
        val sampleQuery = dbType.buildSqlQueryWithLimit(sqlQuery, SAMPLE_SIZE)

        var totalSize = 0L
        var rowCount = 0

        connection.prepareStatement(sampleQuery).use { stmt ->
            stmt.executeQuery().use { rs ->
                val tableColumns = getTableColumnsMetadata(rs)

                while (rs.next()) {
                    totalSize += calculateRowSizeFromResultSet(rs, tableColumns)
                    rowCount++
                }

                if (rowCount == 0) {
                    return MemoryEstimate(0, 0, 0, "0 bytes", true)
                }

                val bytesPerRow = totalSize / rowCount
                val totalRows = limit?.toLong() ?: estimateQueryRowCount(connection, sqlQuery)
                val totalBytes = bytesPerRow * totalRows

                return MemoryEstimate(
                    estimatedRows = totalRows,
                    bytesPerRow = bytesPerRow,
                    totalBytes = totalBytes,
                    humanReadable = formatBytes(totalBytes),
                    isExact = limit != null,
                )
            }
        }
    }

    /**
     * Calculates average row size from sample rows.
     */
    private fun estimateAverageRowSize(
        connection: Connection,
        tableName: String,
        dbType: DbType,
    ): Long {
        val sampleQuery = dbType.buildSelectTableQueryWithLimit(tableName, SAMPLE_SIZE)

        var totalSize = 0L
        var rowCount = 0

        connection.prepareStatement(sampleQuery).use { stmt ->
            stmt.executeQuery().use { rs ->
                val tableColumns = getTableColumnsMetadata(rs)

                while (rs.next()) {
                    totalSize += calculateRowSizeFromResultSet(rs, tableColumns)
                    rowCount++
                }
            }
        }

        return if (rowCount > 0) totalSize / rowCount else 64L
    }

    /**
     * Calculates the size of a single row from ResultSet.
     */
    private fun calculateRowSizeFromResultSet(
        rs: ResultSet,
        tableColumns: List<TableColumnMetadata>,
    ): Long {
        var size = 16L // DataRow overhead

        for (i in tableColumns.indices) {
            val value = rs.getObject(i + 1)
            val metadata = tableColumns[i]
            size += estimateValueSize(value, metadata)
        }

        size += tableColumns.size * 64L // DataFrame column overhead
        return size
    }

    /**
     * Estimates value size based on actual value or metadata.
     */
    private fun estimateValueSize(value: Any?, metadata: TableColumnMetadata): Long {
        if (value != null) {
            return when (value) {
                is Boolean, is Byte -> 16
                is Short -> 16
                is Int -> 16
                is Long, is Double -> 24
                is Float -> 16
                is String -> 40 + (value.length * 2L)
                is ByteArray -> 16 + value.size
                else -> 48
            } as Long
        }

        // Fallback to metadata
        return when (metadata.jdbcType) {
            java.sql.Types.BIT, java.sql.Types.BOOLEAN, java.sql.Types.TINYINT -> 16
            java.sql.Types.SMALLINT -> 16
            java.sql.Types.INTEGER -> 16
            java.sql.Types.BIGINT, java.sql.Types.DOUBLE -> 24
            java.sql.Types.REAL, java.sql.Types.FLOAT -> 16
            java.sql.Types.CHAR, java.sql.Types.VARCHAR -> 40 + (metadata.size.coerceAtMost(100) * 2L)
            else -> 48
        }
    }

    /**
     * Estimates total row count using COUNT(*) with timeout.
     */
    private fun estimateTableRowCount(
        connection: Connection,
        tableName: String,
        dbType: DbType,
    ): Long {
        return try {
            connection.prepareStatement("SELECT COUNT(*) FROM $tableName").use { stmt ->
                stmt.queryTimeout = COUNT_TIMEOUT_SECONDS
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getLong(1) else 0L
                }
            }
        } catch (e: Exception) {
            logger.warn(e) { "COUNT(*) failed for $tableName, using fallback" }
            1000L // Conservative fallback
        }
    }

    /**
     * Estimates query row count using COUNT(*) wrapper.
     */
    private fun estimateQueryRowCount(connection: Connection, sqlQuery: String): Long {
        return try {
            val countQuery = "SELECT COUNT(*) FROM ($sqlQuery) AS temp_count"
            connection.prepareStatement(countQuery).use { stmt ->
                stmt.queryTimeout = COUNT_TIMEOUT_SECONDS
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getLong(1) else 0L
                }
            }
        } catch (e: Exception) {
            logger.warn(e) { "Cannot wrap query with COUNT(*), using sample-based estimate" }
            1000L // Conservative fallback
        }
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes bytes"
    bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
    bytes < 1024 * 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    else -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
}
