package org.jetbrains.kotlinx.dataframe.io.db

public fun extractDBTypeFromURL(url: String?): DbType {
    if (url != null) {
        return when {
            H2.jdbcUrlDatabaseName in url -> H2
            MariaDb.jdbcUrlDatabaseName in url -> MariaDb
            MySql.jdbcUrlDatabaseName in url -> MySql
            Sqlite.jdbcUrlDatabaseName in url -> Sqlite
            PostgreSql.jdbcUrlDatabaseName in url -> PostgreSql
            else -> MariaDb // probably better to add default SQL databases without vendor name
        }
    } else {
        throw RuntimeException("Database URL could not be null. The existing value is $url")
    }
}
