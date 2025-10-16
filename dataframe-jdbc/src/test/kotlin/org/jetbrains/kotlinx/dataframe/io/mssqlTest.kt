package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.io.db.MsSql
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class MsSqlTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpClass() {
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
        }
    }

    @Test
    fun `test SQL Server TOP limit functionality`() {
        MsSql.buildSqlQueryWithLimit("SELECT * FROM TestTable1", 1) shouldBe "SELECT TOP 1 * FROM TestTable1"
    }
}
