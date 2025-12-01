package org.jetbrains.kotlinx.dataframe.io.db

import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.Connection
import java.sql.SQLException
import java.util.Locale

private val logger = KotlinLogging.logger {}

/**
 * Extracts the database type from the given connection.
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

    return if (url.contains(H2().dbTypeInJdbcUrl)) {
        // works only for H2 version 2
        val modeQuery = "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME = 'MODE'"
        var mode = ""
        connection.prepareStatement(modeQuery).use { st ->
            st.executeQuery().use { rs ->
                if (rs.next()) {
                    mode = rs.getString("SETTING_VALUE")
                    logger.debug { "Fetched H2 DB mode: $mode" }
                } else {
                    throw IllegalStateException("The information about H2 mode is not found in the H2 meta-data!")
                }
            }
        }

        // H2 doesn't support MariaDB and SQLite
        when (mode.lowercase(Locale.getDefault())) {
            H2.MODE_MYSQL.lowercase(Locale.getDefault()) -> H2(MySql)

            H2.MODE_MSSQLSERVER.lowercase(Locale.getDefault()) -> H2(MsSql)

            H2.MODE_POSTGRESQL.lowercase(Locale.getDefault()) -> H2(PostgreSql)

            H2.MODE_MARIADB.lowercase(Locale.getDefault()) -> H2(MariaDb)

            else -> {
                val message = "Unsupported database type in the url: $url. " +
                    "Only MySQL, MariaDB, MSSQL and PostgreSQL are supported!"
                logger.error { message }

                throw IllegalArgumentException(message)
            }
        }
    } else {
        val dbType = extractDBTypeFromUrl(url)
        logger.info { "Identified DB type as $dbType from url: $url" }
        dbType
    }
}

/**
 * Extracts the database type from the given JDBC URL.
 *
 * @param [url] the JDBC URL.
 * @return the corresponding [DbType].
 * @throws [RuntimeException] if the url is null.
 */
public fun extractDBTypeFromUrl(url: String?): DbType {
    if (url != null) {
        val helperH2Instance = H2()
        return when {
            helperH2Instance.dbTypeInJdbcUrl in url -> createH2Instance(url)

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
    } else {
        throw SQLException("Database URL could not be null. The existing value is $url")
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
    val modePattern = "MODE=(.*?);".toRegex()
    val matchResult = modePattern.find(url)

    val mode: String = if (matchResult != null && matchResult.groupValues.size == 2) {
        matchResult.groupValues[1]
    } else {
        throw IllegalArgumentException("The provided URL `$url` does not contain a valid mode.")
    }

    // H2 doesn't support MariaDB and SQLite
    return when (mode.lowercase(Locale.getDefault())) {
        H2.MODE_MYSQL.lowercase(Locale.getDefault()) -> H2(MySql)

        H2.MODE_MSSQLSERVER.lowercase(Locale.getDefault()) -> H2(MsSql)

        H2.MODE_POSTGRESQL.lowercase(Locale.getDefault()) -> H2(PostgreSql)

        H2.MODE_MARIADB.lowercase(Locale.getDefault()) -> H2(MariaDb)

        else -> throw IllegalArgumentException(
            "Unsupported database mode: $mode. " +
                "Only MySQL, MariaDB, MSSQL, PostgreSQL modes are supported!",
        )
    }
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
