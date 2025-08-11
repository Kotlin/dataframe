package org.jetbrains.kotlinx.dataframe.spring

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.spring.annotations.DataSource
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DataSchema
interface TestRow {
    val name: String
    val age: Int
}

class TestDataService {
    @DataSource(csvFile = "test-data.csv")
    lateinit var df: DataFrame<TestRow>

    fun getRowCount(): Int = df.rowsCount()
    
    fun getFirstName(): String = df[0]["name"] as String
}

class DataFramePostProcessorTest {

    @Test
    fun `should populate DataFrame from CSV file`() {
        // Create test CSV file in working directory
        val csvFile = File("test-data.csv")
        csvFile.writeText("""
            name,age
            John,25
            Jane,30
            Bob,35
        """.trimIndent())
        
        try {
            val processor = DataFramePostProcessor()
            val testService = TestDataService()
            
            // Process the bean
            processor.postProcessBeforeInitialization(testService, "testService")
            
            // Verify the DataFrame was populated
            assertNotNull(testService.df)
            assertEquals(3, testService.getRowCount())
            assertEquals("John", testService.getFirstName())
        } finally {
            // Clean up
            csvFile.delete()
        }
    }
    
    @Test
    fun `should handle custom delimiter`() {
        val csvFile = File("test-data-pipe.csv")
        csvFile.writeText("""
            name|age
            John|25
            Jane|30
        """.trimIndent())
        
        try {
            class TestServiceWithPipe {
                @DataSource(csvFile = "test-data-pipe.csv", delimiter = '|')
                lateinit var df: DataFrame<TestRow>
            }
            
            val processor = DataFramePostProcessor()
            val testService = TestServiceWithPipe()
            
            processor.postProcessBeforeInitialization(testService, "testService")
            
            assertNotNull(testService.df)
            assertEquals(2, testService.df.rowsCount())
        } finally {
            csvFile.delete()
        }
    }
}