@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Ignore
import org.junit.Test

class MariaDb {

    @Ignore
    @Test
    fun readSqlTable() {
        // SampleStart
        val url = "jdbc:mariadb://localhost:3306/testDatabase"
        val username = "root"
        val password = "password"

        val dbConfig = DbConnectionConfig(url, username, password)

        val tableName = "Customer"

        val df = DataFrame.readSqlTable(dbConfig, tableName)
        // SampleEnd
    }
}
