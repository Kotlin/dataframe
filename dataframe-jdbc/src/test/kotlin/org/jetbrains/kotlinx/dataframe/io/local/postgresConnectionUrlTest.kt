package org.jetbrains.kotlinx.dataframe.io.local

import org.jetbrains.kotlinx.dataframe.io.PostgresConnectionUrlTestBase

private const val URL = "jdbc:postgresql://localhost:5432/test"
private const val USER_NAME = "postgres"
private const val PASSWORD = "pass"

class PostgresConnectionUrlLocalTest : PostgresConnectionUrlTestBase() {
    override val baseUrl: String get() = URL

    override val userName: String get() = USER_NAME

    override val password: String get() = PASSWORD
}
