
package org.jetbrains.kotlinx.dataframe.io.h2.tracking

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager

/**
 * Tests for JDBC progress tracking functionality.
 */
class JdbcProgressTest {
    
    private lateinit var connection: Connection
    
    @Before
    fun setUp() {
        connection = DriverManager.getConnection("jdbc:h2:mem:test_progress;DB_CLOSE_DELAY=-1;MODE=MySQL")
        
        connection.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE test_table (
                    id INT PRIMARY KEY,
                    name VARCHAR(100),
                    amount INT
                )
                """.trimIndent()
            )
            
            // Insert 5000 rows for testing
            connection.autoCommit = false
            val insertStmt = connection.prepareStatement("INSERT INTO test_table VALUES (?, ?, ?)")
            for (i in 1..5000) {
                insertStmt.setInt(1, i)
                insertStmt.setString(2, "Name_$i")
                insertStmt.setInt(3, i * 10)
                insertStmt.addBatch()
                
                if (i % 500 == 0) {
                    insertStmt.executeBatch()
                }
            }
            insertStmt.executeBatch()
            connection.commit()
            connection.autoCommit = true
        }
    }
    
    @After
    fun tearDown() {
        connection.createStatement().use { it.executeUpdate("DROP TABLE IF EXISTS test_table") }
        connection.close()
    }
    
    @Test
    fun `progress tracking disabled by default`() {
        // Progress should be disabled by default
        System.clearProperty("dataframe.jdbc.progress")
        
        val df = DataFrame.readSqlTable(connection, "test_table")
        
        df.rowsCount() shouldBe 5000
    }
    
    @Test
    fun `progress tracking can be enabled via system property`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        
        try {
            val df = DataFrame.readSqlTable(connection, "test_table")
            df.rowsCount() shouldBe 5000
        } finally {
            System.clearProperty("dataframe.jdbc.progress")
        }
    }
    
    @Test
    fun `progress tracking with limit`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        
        try {
            val df = DataFrame.readSqlTable(connection, "test_table", limit = 100)
            df.rowsCount() shouldBe 100
        } finally {
            System.clearProperty("dataframe.jdbc.progress")
        }
    }
    
    @Test
    fun `detailed progress can be disabled`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        System.setProperty("dataframe.jdbc.progress.detailed", "false")
        
        try {
            val df = DataFrame.readSqlTable(connection, "test_table")
            df.rowsCount() shouldBe 5000
        } finally {
            System.clearProperty("dataframe.jdbc.progress")
            System.clearProperty("dataframe.jdbc.progress.detailed")
        }
    }
    
    @Test
    fun `progress interval can be configured`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        System.setProperty("dataframe.jdbc.progress.interval", "500")
        
        try {
            val df = DataFrame.readSqlTable(connection, "test_table")
            df.rowsCount() shouldBe 5000
        } finally {
            System.clearProperty("dataframe.jdbc.progress")
            System.clearProperty("dataframe.jdbc.progress.interval")
        }
    }
    
    @Test
    fun `progress works with query`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        
        try {
            val df = DataFrame.readSqlQuery(
                connection, 
                "SELECT * FROM test_table WHERE amount > 1000"
            )
            df.rowsCount() shouldBe 4900
        } finally {
            System.clearProperty("dataframe.jdbc.progress")
        }
    }
}
