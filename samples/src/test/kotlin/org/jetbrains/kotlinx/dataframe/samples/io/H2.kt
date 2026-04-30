@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Ignore
import org.junit.Test

class H2 {
    @Ignore
    @Test
    fun readSqlTableRegularMode() {
        // SampleStart
        val url = "jdbc:h2:mem:testDatabase"
        val username = "sa"
        val password = ""

        val dbConfig = DbConnectionConfig(url, username, password)

        val tableName = "Customer"

        val df = DataFrame.readSqlTable(dbConfig, tableName)
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqlTablePostgreSqlMode() {
        // SampleStart
        val postgresUrl = "jdbc:h2:mem:testDatabase;MODE=PostgreSQL"
        val username = "sa"
        val password = ""

        val postgresConfig = DbConnectionConfig(postgresUrl, username, password)

        val tableName = "Customer"

        val dfPostgres = DataFrame.readSqlTable(postgresConfig, tableName)
        // SampleEnd
    }
}
