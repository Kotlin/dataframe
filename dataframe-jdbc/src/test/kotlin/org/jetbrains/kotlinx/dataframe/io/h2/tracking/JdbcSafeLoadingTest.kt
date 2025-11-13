package org.jetbrains.kotlinx.dataframe.io.h2.tracking

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.JdbcSafeDataLoading
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager

/**
 * Tests for JdbcSafeDataLoading functionality.
 */
class JdbcSafeLoadingTest {
    
    private lateinit var connection: Connection
    private val dbConfig = DbConnectionConfig("jdbc:h2:mem:test_safe;DB_CLOSE_DELAY=-1;MODE=MySQL")
    
    @Before
    fun setUp() {
        connection = DriverManager.getConnection(dbConfig.url)
        
        connection.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
                CREATE TABLE test_data (
                    id INT PRIMARY KEY,
                    data VARCHAR(1000)
                )
                """.trimIndent()
            )
            
            // Insert 10000 rows (~10KB each = ~100MB total)
            connection.autoCommit = false
            val insertStmt = connection.prepareStatement("INSERT INTO test_data VALUES (?, ?)")
            for (i in 1..10000) {
                insertStmt.setInt(1, i)
                insertStmt.setString(2, "x".repeat(1000)) // 1KB per row
                insertStmt.addBatch()
                
                if (i % 1000 == 0) {
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
        connection.createStatement().use { it.executeUpdate("DROP TABLE IF EXISTS test_data") }
        connection.close()
    }
    
    @Test
    fun `safe load with sufficient memory limit`() {
        val df = JdbcSafeDataLoading.load(maxMemoryGb = 1.0) {
            readSqlTable(connection, "test_data")
        }
        
        df.rowsCount() shouldBe 10000
    }
    
    @Test
    fun `safe load applies automatic limit when memory exceeded`() {
        var limitApplied = false
        var appliedLimitValue = 0
        
        val df = JdbcSafeDataLoading.load(
            configure = {
                maxMemoryGb = 0.001 // Very small limit
                onExceed = JdbcSafeDataLoading.ExceedAction.APPLY_LIMIT
        
                onLimitApplied = { _, limit ->
                    limitApplied = true
                    appliedLimitValue = limit
                }
            }
        ) {
            readSqlTable(connection, "test_data")
        }
        
        limitApplied shouldBe true
        appliedLimitValue shouldBeLessThan 10000
        df.rowsCount() shouldBe appliedLimitValue
    }
    
    @Test
    fun `safe load throws when configured`() {
        shouldThrow<JdbcSafeDataLoading.MemoryLimitExceededException> {
            JdbcSafeDataLoading.load(
                configure = {
                    maxMemoryGb = 0.001
                    onExceed = JdbcSafeDataLoading.ExceedAction.THROW
                }
            ) {
                readSqlTable(connection, "test_data")
            }
        }
    }
    
    @Test
    fun `safe load warns and proceeds when configured`() {
        val df = JdbcSafeDataLoading.load(
            configure = {
                maxMemoryGb = 0.001
                onExceed = JdbcSafeDataLoading.ExceedAction.WARN_AND_PROCEED
            }
        ) {
            readSqlTable(connection, "test_data")
        }
        
        df.rowsCount() shouldBe 10000
    }
    
    @Test
    fun `onEstimate callback is invoked`() {
        var estimateCalled = false
        var estimatedRows = 0L
        
        JdbcSafeDataLoading.load(
            configure = {
                maxMemoryGb = 1.0
        
                onEstimate = { estimate ->
                    estimateCalled = true
                    estimatedRows = estimate.estimatedRows
                }
            }
        ) {
            readSqlTable(connection, "test_data")
        }
        
        estimateCalled shouldBe true
        estimatedRows shouldBe 10000L
    }
    
    @Test
    fun `safe load works with DbConnectionConfig`() {
        val df = JdbcSafeDataLoading.load(maxMemoryGb = 1.0) {
            readSqlTable(dbConfig, "test_data")
        }
        
        df.rowsCount() shouldBe 10000
    }
    
    @Test
    fun `safe load works with query`() {
        val df = JdbcSafeDataLoading.load(maxMemoryGb = 1.0) {
            readSqlQuery(connection, "SELECT * FROM test_data WHERE id <= 100")
        }
        
        df.rowsCount() shouldBe 100
    }
    
    @Test
    fun `loadMultiple works with readAllSqlTables`() {
        // Create another table
        connection.createStatement().use { stmt ->
            stmt.executeUpdate("CREATE TABLE test_data2 (id INT PRIMARY KEY, amount INT)")
            stmt.executeUpdate("INSERT INTO test_data2 VALUES (1, 10), (2, 20)")
        }
        
        val tables = JdbcSafeDataLoading.loadMultiple(maxMemoryGb = 1.0) {
            readAllSqlTables(connection)
        }
        
        tables.keys shouldBe setOf("TEST_DATA", "TEST_DATA2")
        
        connection.createStatement().use { it.executeUpdate("DROP TABLE IF EXISTS test_data2") }
    }
}
