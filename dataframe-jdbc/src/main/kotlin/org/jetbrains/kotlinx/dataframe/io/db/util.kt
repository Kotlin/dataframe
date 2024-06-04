package org.jetbrains.kotlinx.dataframe.io.db

import java.sql.SQLException
import java.util.*

/**
 * Extracts the database type from the given JDBC URL.
 *
 * @param [url] the JDBC URL.
 * @return the corresponding [DbType].
 * @throws RuntimeException if the url is null.
 */
public fun extractDBTypeFromUrl(url: String?): DbType {
    if (url != null) {
        val helperH2Instance = H2(MySql)
        return when {
            helperH2Instance.dbTypeInJdbcUrl in url -> createH2Instance(url)
            MariaDb.dbTypeInJdbcUrl in url -> MariaDb
            MySql.dbTypeInJdbcUrl in url -> MySql
            Sqlite.dbTypeInJdbcUrl in url -> Sqlite
            PostgreSql.dbTypeInJdbcUrl in url -> PostgreSql
            MsSql.dbTypeInJdbcUrl in url -> MsSql
            else -> throw IllegalArgumentException(
                "Unsupported database type in the url: $url. " +
                    "Only H2, MariaDB, MySQL, MSSQL, SQLite and PostgreSQL are supported!"
            )
        }
    } else {
        throw SQLException("Database URL could not be null. The existing value is $url")
    }
}

private fun createH2Instance(url: String): DbType {
    val modePattern = "MODE=(.*?);".toRegex()
    val matchResult = modePattern.find(url)

    val mode: String = if (matchResult != null && matchResult.groupValues.size == 2) {
        matchResult.groupValues[1]
    } else {
        throw IllegalArgumentException("The provided URL does not contain a valid mode.")
    }

    return when(mode.uppercase(Locale.getDefault())) {
        "MYSQL" -> MySql
        "MARIADB" -> MariaDb
        "SQLITE" -> Sqlite
        "POSTGRESQL" -> PostgreSql
        "MSSQL" -> MsSql
        else -> throw IllegalArgumentException(
            "Unsupported database mode: $mode. " +
                "Only H2, MariaDB, MySQL, MSSQL, SQLite and PostgreSQL modes are supported!"
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
