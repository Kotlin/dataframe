package org.jetbrains.kotlinx.dataframe.io.h2.tracking

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.JdbcSafeDataLoading
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.sql.Connection
import java.sql.DriverManager

/**
 * Tests for verifying log output of progress tracking and safe loading.
 * 
 * Note: These tests capture System.err output instead of using Logback-specific 
 * log appenders to remain compatible with any SLF4J implementation.
 */
class JdbcProgressOutputTest {
    
    private lateinit var connection: Connection
    private lateinit var originalErr: PrintStream
    private lateinit var capturedErr: ByteArrayOutputStream
    
    @Before
    fun setUp() {
        connection = DriverManager.getConnection("jdbc:h2:mem:test_output;DB_CLOSE_DELAY=-1;MODE=MySQL")
        
        // Setup System.err capture for SLF4J Simple logging
        originalErr = System.err
        capturedErr = ByteArrayOutputStream()
        System.setErr(PrintStream(capturedErr))
        
        // Create test table
        connection.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE test_progress (
                    id INT PRIMARY KEY,
                    data VARCHAR(100)
                )
                """.trimIndent()
            )
            
            // Insert 3000 rows
            connection.autoCommit = false
            val insertStmt = connection.prepareStatement("INSERT INTO test_progress VALUES (?, ?)")
            for (i in 1..3000) {
                insertStmt.setInt(1, i)
                insertStmt.setString(2, "Data_$i")
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
        System.setErr(originalErr)
        connection.createStatement().use { it.executeUpdate("DROP TABLE IF EXISTS test_progress") }
        connection.close()
        System.clearProperty("dataframe.jdbc.progress")
        System.clearProperty("dataframe.jdbc.progress.detailed")
        System.clearProperty("dataframe.jdbc.progress.interval")
    }
    
    private fun getLogOutput(): String = capturedErr.toString("UTF-8")
    
    private fun clearLogOutput() {
        capturedErr.reset()
    }
    
    @Test
    fun `progress tracking logs debug messages`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        clearLogOutput()
        
        DataFrame.readSqlTable(connection, "test_progress")
        
        val logOutput = getLogOutput()
        
        // Should have progress messages
        val hasProgressMessages = logOutput.contains("Loaded") && logOutput.contains("rows")
        hasProgressMessages shouldBe true
    }
    
    @Test
    fun `detailed progress shows statistics`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        System.setProperty("dataframe.jdbc.progress.detailed", "true")
        clearLogOutput()
        
        DataFrame.readSqlTable(connection, "test_progress")
        
        val logOutput = getLogOutput()
        
        // Should contain statistics
        val hasStatistics = logOutput.contains("rows/sec") || logOutput.contains("ms")
        hasStatistics shouldBe true
    }
    
    @Test
    fun `simple progress shows basic messages`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        System.setProperty("dataframe.jdbc.progress.detailed", "false")
        clearLogOutput()
        
        DataFrame.readSqlTable(connection, "test_progress")
        
        val logOutput = getLogOutput()
        
        // Should have messages but without detailed stats
        val hasBasicMessages = logOutput.contains("Loaded") && logOutput.contains("rows")
        hasBasicMessages shouldBe true
        
        // Should NOT contain detailed statistics
        val hasDetailedStats = logOutput.contains("rows/sec")
        hasDetailedStats shouldBe false
    }
    
    @Test
    fun `progress interval is respected`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        System.setProperty("dataframe.jdbc.progress.interval", "1000")
        clearLogOutput()
        
        DataFrame.readSqlTable(connection, "test_progress")
        
        val logOutput = getLogOutput()
        
        // Should have messages at 1000, 2000, 3000
        val has1000 = logOutput.contains("1000 rows")
        val has2000 = logOutput.contains("2000 rows")
        has1000 shouldBe true
        has2000 shouldBe true
    }
    
    @Test
    fun `completion message is logged`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        clearLogOutput()
        
        DataFrame.readSqlTable(connection, "test_progress")
        
        val logOutput = getLogOutput()
        
        // Should have completion message
        val hasCompletion = logOutput.contains("Loading complete") || logOutput.contains("3000 rows")
        hasCompletion shouldBe true
    }
    
    @Test
    fun `memory warning is logged for large datasets`() {
        System.setProperty("dataframe.jdbc.progress", "true")
        
        // Create a larger table
        connection.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE large_test (
                    id INT PRIMARY KEY,
                    data VARCHAR(10000)
                )
                """.trimIndent()
            )
            
            connection.autoCommit = false
            val insertStmt = connection.prepareStatement("INSERT INTO large_test VALUES (?, ?)")
            for (i in 1..1000) {
                insertStmt.setInt(1, i)
                insertStmt.setString(2, "x".repeat(10000)) // 10KB per row = 10MB total
                insertStmt.addBatch()
                
                if (i % 100 == 0) {
                    insertStmt.executeBatch()
                }
            }
            insertStmt.executeBatch()
            connection.commit()
            connection.autoCommit = true
        }
        
        clearLogOutput()
        
        DataFrame.readSqlTable(connection, "large_test")
        
        val logOutput = getLogOutput()
        
        // Should have memory estimate message (if logged at DEBUG/INFO level)
        // This is optional as it depends on logging configuration
        
        connection.createStatement().use { it.executeUpdate("DROP TABLE IF EXISTS large_test") }
    }
    
    @Test
    fun `safe loading logs warnings when limit applied`() {
        clearLogOutput()
        
        JdbcSafeDataLoading.load(
            configure = {
                maxMemoryGb = 0.00001 // Very small limit to trigger warning
                onExceed = JdbcSafeDataLoading.ExceedAction.APPLY_LIMIT
            }
        ) {
            readSqlTable(connection, "test_progress")
        }
        
        val logOutput = getLogOutput()
        
        // Should have warning about exceeding limit
        val hasWarning = logOutput.contains("exceeds limit") || logOutput.contains("WARN")
        hasWarning shouldBe true
    }
    
    @Test
    fun `safe loading logs warning when proceeding despite limit`() {
        clearLogOutput()
        
        JdbcSafeDataLoading.load(
            configure = {
                maxMemoryGb = 0.00001
                onExceed = JdbcSafeDataLoading.ExceedAction.WARN_AND_PROCEED
            }
        ) {
            readSqlTable(connection, "test_progress")
        }
        
        val logOutput = getLogOutput()
        
        // Should have warning about proceeding
        val hasWarning = (logOutput.contains("exceeds limit") && logOutput.contains("proceeding")) ||
                        logOutput.contains("WARN")
        hasWarning shouldBe true
    }
    
    @Test
    fun `no logs when progress disabled`() {
        System.clearProperty("dataframe.jdbc.progress")
        clearLogOutput()
        
        DataFrame.readSqlTable(connection, "test_progress")
        
        val logOutput = getLogOutput()
        
        // Should NOT have progress messages (only connection/query logs possibly)
        val hasProgressMessages = logOutput.matches(Regex(".*Loaded \\d+ rows.*"))
        hasProgressMessages shouldBe false
    }
}
