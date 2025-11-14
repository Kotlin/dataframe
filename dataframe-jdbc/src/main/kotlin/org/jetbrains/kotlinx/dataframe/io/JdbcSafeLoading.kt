package org.jetbrains.kotlinx.dataframe.io

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.extractDBTypeFromConnection
import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

private val logger = KotlinLogging.logger {}

/**
 * Safe JDBC data loading with automatic memory estimation and limit management.
 *
 * Uses the same method names as the main API but adds automatic memory checking:
 *
 * ```kotlin
 * // Instead of:
 * DataFrame.readSqlTable(config, "table")
 *
 * // Use:
 * JdbcSafeDataLoading.load(maxMemoryGb = 1.0) {
 *     readSqlTable(config, "table")
 * }
 * ```
 *
 * ## Features
 * - Automatic memory estimation before loading (10 sample rows + COUNT(*))
 * - Automatic limit application when memory threshold exceeded
 * - Configurable behavior: throw, warn, or auto-limit
 * - Callbacks for estimates and limit events
 *
 * ## Examples
 *
 * Basic usage:
 * ```kotlin
 * val df = JdbcSafeDataLoading.load(maxMemoryGb = 1.0) {
 *     readSqlTable(config, "large_table")
 * }
 * ```
 *
 * Advanced configuration:
 * ```kotlin
 * val df = JdbcSafeDataLoading.load {
 *     maxMemoryGb = 2.0
 *     onExceed = ExceedAction.THROW
 *
 *     onEstimate = { estimate ->
 *         println("Estimated: ${estimate.humanReadable}")
 *     }
 *
 *     readSqlTable(config, "huge_table")
 * }
 * ```
 */
public object JdbcSafeDataLoading {

    /**
     * Loads JDBC data with automatic memory checking.
     */
    public fun <T : AnyFrame> load(
        maxMemoryGb: Double = 1.0,
        block: SafeLoadContext.() -> T,
    ): T {
        val config = LoadConfig().apply { this.maxMemoryGb = maxMemoryGb }
        return SafeLoadContext(config).block()
    }

    /**
     * Advanced version with full configuration.
     */
    public fun <T : AnyFrame> load(
        configure: LoadConfig.() -> Unit,
        block: SafeLoadContext.() -> T,
    ): T {
        val config = LoadConfig().apply(configure)
        return SafeLoadContext(config).block()
    }

    /**
     * For loading multiple results (like Map<String, AnyFrame>).
     */
    public fun <T> loadMultiple(
        maxMemoryGb: Double = 1.0,
        block: SafeLoadContext.() -> T,
    ): T {
        val config = LoadConfig().apply { this.maxMemoryGb = maxMemoryGb }
        return SafeLoadContext(config).block()
    }

    /**
     * Advanced version for multiple results.
     */
    public fun <T> loadMultiple(
        configure: LoadConfig.() -> Unit,
        block: SafeLoadContext.() -> T,
    ): T {
        val config = LoadConfig().apply(configure)
        return SafeLoadContext(config).block()
    }

    /**
     * Configuration for safe JDBC loading.
     */
    public class LoadConfig {
        /** Maximum allowed memory in bytes. */
        public var maxMemoryBytes: Long = 1024L * 1024L * 1024L

        /** Maximum allowed memory in gigabytes (convenience setter). */
        public var maxMemoryGb: Double
            get() = maxMemoryBytes / (1024.0 * 1024.0 * 1024.0)
            set(value) { maxMemoryBytes = (value * 1024 * 1024 * 1024).toLong() }

        /** What to do when memory limit is exceeded. */
        public var onExceed: ExceedAction = ExceedAction.APPLY_LIMIT

        /** Callback invoked when estimate is available (before loading). */
        public var onEstimate: ((MemoryEstimate) -> Unit)? = null

        /** Callback invoked when limit is automatically applied. */
        public var onLimitApplied: ((estimate: MemoryEstimate, appliedLimit: Int) -> Unit)? = null

        /** For loadAllTables: callback invoked for each table's estimate. */
        public var onTableEstimate: ((tableName: String, estimate: MemoryEstimate) -> Unit)? = null
    }

    /** Action to take when estimated memory exceeds the limit. */
    public enum class ExceedAction {
        /** Automatically apply limit to stay under threshold */
        APPLY_LIMIT,

        /** Throw MemoryLimitExceededException and don't load */
        THROW,

        /** Log warning but proceed with full load */
        WARN_AND_PROCEED,
    }

    /** Exception thrown when memory limit exceeded and action is THROW. */
    public class MemoryLimitExceededException(
        message: String,
        public val estimate: MemoryEstimate,
    ) : IllegalStateException(message)

    /**
     * Context with the same method names as DataFrame.Companion.
     */
    public class SafeLoadContext internal constructor(
        private val config: LoadConfig,
    ) {
        // region readSqlTable

        public fun readSqlTable(
            dbConfig: DbConnectionConfig,
            tableName: String,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            strictValidation: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): AnyFrame = estimateAndLoad(
            estimate = { estimateSqlTable(dbConfig, tableName, null, dbType) },
            load = { limit ->
                DataFrame.readSqlTable(dbConfig, tableName, limit, inferNullability, dbType, strictValidation, configureStatement)
            }
        )

        public fun readSqlTable(
            connection: Connection,
            tableName: String,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            strictValidation: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): AnyFrame = estimateAndLoad(
            estimate = { estimateSqlTable(connection, tableName, null, dbType) },
            load = { limit ->
                DataFrame.readSqlTable(connection, tableName, limit, inferNullability, dbType, strictValidation, configureStatement)
            }
        )

        public fun readSqlTable(
            dataSource: DataSource,
            tableName: String,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            strictValidation: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): AnyFrame = dataSource.connection.use { conn ->
            readSqlTable(conn, tableName, dbType, inferNullability, strictValidation, configureStatement)
        }

        // endregion

        // region readSqlQuery

        public fun readSqlQuery(
            dbConfig: DbConnectionConfig,
            sqlQuery: String,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            strictValidation: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): AnyFrame = estimateAndLoad(
            estimate = { estimateSqlQuery(dbConfig, sqlQuery, null, dbType) },
            load = { limit ->
                DataFrame.readSqlQuery(dbConfig, sqlQuery, limit, inferNullability, dbType, strictValidation, configureStatement)
            }
        )

        public fun readSqlQuery(
            connection: Connection,
            sqlQuery: String,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            strictValidation: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): AnyFrame = estimateAndLoad(
            estimate = { estimateSqlQuery(connection, sqlQuery, null, dbType) },
            load = { limit ->
                DataFrame.readSqlQuery(connection, sqlQuery, limit, inferNullability, dbType, strictValidation, configureStatement)
            }
        )

        public fun readSqlQuery(
            dataSource: DataSource,
            sqlQuery: String,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            strictValidation: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): AnyFrame = dataSource.connection.use { conn ->
            readSqlQuery(conn, sqlQuery, dbType, inferNullability, strictValidation, configureStatement)
        }

        // endregion

        // region readAllSqlTables

        public fun readAllSqlTables(
            dbConfig: DbConnectionConfig,
            catalogue: String? = null,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): Map<String, AnyFrame> = withReadOnlyConnection(dbConfig, dbType) { connection ->
            readAllSqlTables(connection, catalogue, dbType, inferNullability, configureStatement)
        }

        public fun readAllSqlTables(
            connection: Connection,
            catalogue: String? = null,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): Map<String, AnyFrame> {
            val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)
            val metaData = connection.metaData
            val tablesResultSet = retrieveTableMetadata(metaData, catalogue, determinedDbType)

            return buildMap {
                while (tablesResultSet.next()) {
                    val tableMetadata = determinedDbType.buildTableMetadata(tablesResultSet)

                    if (determinedDbType.isSystemTable(tableMetadata)) continue

                    val fullTableName = buildFullTableName(catalogue, tableMetadata.schemaName, tableMetadata.name)

                    val dataFrame = estimateAndLoad(
                        estimate = {
                            val est = estimateSqlTable(connection, fullTableName, null, dbType)
                            config.onTableEstimate?.invoke(fullTableName, est)
                            est
                        },
                        load = { limit ->
                            DataFrame.readSqlTable(connection, fullTableName, limit, inferNullability, determinedDbType, true, configureStatement)
                        }
                    )

                    put(fullTableName, dataFrame)
                }
            }
        }

        public fun readAllSqlTables(
            dataSource: DataSource,
            catalogue: String? = null,
            dbType: DbType? = null,
            inferNullability: Boolean = true,
            configureStatement: (PreparedStatement) -> Unit = {},
        ): Map<String, AnyFrame> = dataSource.connection.use { conn ->
            readAllSqlTables(conn, catalogue, dbType, inferNullability, configureStatement)
        }

        // endregion

        private fun estimateAndLoad(
            estimate: () -> MemoryEstimate,
            load: (limit: Int?) -> AnyFrame,
        ): AnyFrame {
            val memoryEstimate = estimate()
            config.onEstimate?.invoke(memoryEstimate)
            val finalLimit = handleMemoryLimit(memoryEstimate, null)
            return load(finalLimit)
        }

        private fun handleMemoryLimit(estimate: MemoryEstimate, originalLimit: Int?): Int? {
            if (!estimate.exceeds(config.maxMemoryBytes)) return originalLimit

            return when (config.onExceed) {
                ExceedAction.APPLY_LIMIT -> {
                    if (originalLimit != null) {
                        originalLimit
                    } else {
                        val recommendedLimit = estimate.recommendedLimit(config.maxMemoryBytes)
                        logger.warn {
                            "Estimated memory ${estimate.humanReadable} exceeds limit " +
                                "${formatBytes(config.maxMemoryBytes)}. Applying limit: $recommendedLimit rows"
                        }
                        config.onLimitApplied?.invoke(estimate, recommendedLimit)
                        recommendedLimit
                    }
                }

                ExceedAction.THROW -> throw MemoryLimitExceededException(
                    "Estimated memory ${estimate.humanReadable} exceeds limit ${formatBytes(config.maxMemoryBytes)}",
                    estimate
                )

                ExceedAction.WARN_AND_PROCEED -> {
                    logger.warn { "Memory ${estimate.humanReadable} exceeds limit, but proceeding" }
                    originalLimit
                }
            }
        }
    }
}

// Helper functions for estimation

private fun estimateSqlTable(
    dbConfig: DbConnectionConfig,
    tableName: String,
    limit: Int?,
    dbType: DbType?,
): MemoryEstimate {
    validateLimit(limit)
    return withReadOnlyConnection(dbConfig, dbType) { conn ->
        val determinedDbType = dbType ?: extractDBTypeFromConnection(conn)
        MemoryEstimator.estimateTable(conn, tableName, determinedDbType, limit)
    }
}

private fun estimateSqlQuery(
    dbConfig: DbConnectionConfig,
    sqlQuery: String,
    limit: Int?,
    dbType: DbType?,
): MemoryEstimate {
    validateLimit(limit)
    require(isValidSqlQuery(sqlQuery)) {
        "SQL query should start from SELECT and be a valid query"
    }

    return withReadOnlyConnection(dbConfig, dbType) { conn ->
        val determinedDbType = dbType ?: extractDBTypeFromConnection(conn)
        MemoryEstimator.estimateQuery(conn, sqlQuery, determinedDbType, limit)
    }
}

private fun estimateSqlTable(
    connection: Connection,
    tableName: String,
    limit: Int?,
    dbType: DbType?,
): MemoryEstimate {
    validateLimit(limit)
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)
    return MemoryEstimator.estimateTable(connection, tableName, determinedDbType, limit)
}

private fun estimateSqlQuery(
    connection: Connection,
    sqlQuery: String,
    limit: Int?,
    dbType: DbType?,
): MemoryEstimate {
    validateLimit(limit)
    require(isValidSqlQuery(sqlQuery)) {
        "SQL query should start from SELECT and be a valid query"
    }
    val determinedDbType = dbType ?: extractDBTypeFromConnection(connection)
    return MemoryEstimator.estimateQuery(connection, sqlQuery, determinedDbType, limit)
}

private fun buildFullTableName(catalogue: String?, schemaName: String?, tableName: String): String {
    return when {
        catalogue != null && schemaName != null -> "$catalogue.$schemaName.$tableName"
        catalogue != null -> "$catalogue.$tableName"
        else -> tableName
    }
}

private fun retrieveTableMetadata(
    metaData: java.sql.DatabaseMetaData,
    catalogue: String?,
    dbType: DbType,
): java.sql.ResultSet {
    val tableTypes = dbType.tableTypes?.toTypedArray()
    return metaData.getTables(catalogue, null, null, tableTypes)
}

private fun formatBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes bytes"
    bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
    bytes < 1024 * 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    else -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
}
