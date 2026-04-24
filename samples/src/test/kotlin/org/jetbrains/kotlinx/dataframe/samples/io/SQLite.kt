@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Ignore
import org.junit.Test

class SQLite {

    @Ignore
    @Test
    fun readSqlTable() {
        // SampleStart
        val url = "jdbc:sqlite:testDatabase.db"

        val dbConfig = DbConnectionConfig(url)

        val tableName = "Customer"

        val df = DataFrame.readSqlTable(dbConfig, tableName)
        // SampleEnd
    }
}
