package org.jetbrains.kotlinx.dataframe.io.db

import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.Connection
import java.sql.SQLException
import java.util.Locale

private val logger = KotlinLogging.logger {}

private const val UNSUPPORTED_H2_MODE_MESSAGE =
    "Unsupported H2 MODE: %s. Supported: MySQL, PostgreSQL, MSSQLServer, MariaDB, REGULAR/H2-Regular (or omit MODE)."

private const val H2_MODE_QUERY = "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME = 'MODE'"

private val H2_MODE_URL_PATTERN = "MODE=([^;:&]+)".toRegex(RegexOption.IGNORE_CASE)

/**
 * Extracts the database type from the given connection.
 * For H2, fetches the actual MODE from the active connection settings.
 * For other databases, extracts type from URL.
 *
 * @param [connection] the database connection.
 * @return the corresponding [DbType].
 * @throws [IllegalStateException] if URL information is missing in connection meta-data.
 * @throws [IllegalArgumentException] if the URL specifies an unsupported database type.
 * @throws [SQLException] if the URL is null.
 */
public fun extractDBTypeFromConnection(connection: Connection): DbType {
    val url = connection.metaData?.url
        ?: throw IllegalStateException("URL information is missing in connection meta data!")
    logger.info { "Processing DB type extraction for connection url: $url" }

    // First, determine the base database type from URL
    val baseDbType = extractDBTypeFromUrl(url)

    // For H2, refine the mode by querying the active connection settings
    // This handles cases where MODE is not specified in URL, but H2 returns "Regular" from settings
    return if (baseDbType is H2) {
        val mode = fetchH2ModeFromConnection(connection)
        parseH2ModeOrThrow(mode)
    } else {
        logger.info { "Identified DB type as $baseDbType from url: $url" }
        baseDbType
    }
}

/**
 * Fetches H2 database mode from an active connection.
 * Works only for H2 version 2.
 *
 * @param [connection] the database connection.
 * @return the mode string or null if not set.
 */
private fun fetchH2ModeFromConnection(connection: Connection): String? {
    var mode: String? = null
    connection.prepareStatement(H2_MODE_QUERY).use { st ->
        st.executeQuery().use { rs ->
            if (rs.next()) {
                mode = rs.getString("SETTING_VALUE")
                logger.debug { "Fetched H2 DB mode: $mode" }
            }
        }
    }

    return mode?.trim()?.takeIf { it.isNotEmpty() }
}

/**
 * Parses H2 mode string and returns the corresponding H2 DbType instance.
 *
 * @param [mode] the mode string (maybe null or empty for Regular mode).
 * @return H2 instance with the appropriate mode.
 * @throws [IllegalArgumentException] if the mode is not supported.
 */
private fun parseH2ModeOrThrow(mode: String?): H2 {
    if (mode.isNullOrEmpty()) {
        return H2(H2.Mode.Regular)
    }
    return H2.Mode.fromValue(mode)?.let { H2(it) }
        ?: throw IllegalArgumentException(UNSUPPORTED_H2_MODE_MESSAGE.format(mode)).also {
            logger.error { it.message }
        }
}

/**
 * Extracts the database type from the given JDBC URL.
 *
 * @param [url] the JDBC URL.
 * @return the corresponding [DbType].
 * @throws [SQLException] if the url is null.
 * @throws [IllegalArgumentException] if the URL specifies an unsupported database type.
 */
public fun extractDBTypeFromUrl(url: String?): DbType {
    url ?: throw SQLException("Database URL could not be null.")

    return when {
        H2().dbTypeInJdbcUrl in url -> createH2Instance(url)
        MariaDb.dbTypeInJdbcUrl in url -> MariaDb
        MySql.dbTypeInJdbcUrl in url -> MySql
        Sqlite.dbTypeInJdbcUrl in url -> Sqlite
        PostgreSql.dbTypeInJdbcUrl in url -> PostgreSql
        MsSql.dbTypeInJdbcUrl in url -> MsSql
        DuckDb.dbTypeInJdbcUrl in url -> DuckDb
        else -> throw IllegalArgumentException(
            "Unsupported database type in the url: $url. " +
                    "Only H2, MariaDB, MySQL, MSSQL, SQLite, PostgreSQL, and DuckDB are supported!",
        )
    }
}

/**
 * Creates an instance of DbType based on the provided JDBC URL.
 *
 * @param [url] The JDBC URL representing the database connection.
 * @return The corresponding [DbType] instance.
 * @throws [IllegalArgumentException] if the provided URL does not contain a valid mode.
 */
private fun createH2Instance(url: String): DbType {
    val mode = H2_MODE_URL_PATTERN.find(url)?.groupValues?.getOrNull(1)
    return parseH2ModeOrThrow(mode?.takeIf { it.isNotBlank() })
}

/**
 * Retrieves the driver class name from the given JDBC URL.
 *
 * @param [url] The JDBC URL to extract the driver class name from.
 * @return The driver class name as a [String].
 */
public fun driverClassNameFromUrl(url: String): String {
    val dbType = extractDBTypeFromUrl(url)
    return dbType.driverClassName
}
