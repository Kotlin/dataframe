package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.io.db.MsSql
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.reflect.typeOf

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
        MsSql.sqlQueryLimit("SELECT * FROM TestTable1", 1) shouldBe "SELECT TOP 1 * FROM TestTable1"
    }
}
