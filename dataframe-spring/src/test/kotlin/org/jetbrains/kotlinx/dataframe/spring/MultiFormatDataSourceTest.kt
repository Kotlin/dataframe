package org.jetbrains.kotlinx.dataframe.spring

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.io.JSON
import org.jetbrains.kotlinx.dataframe.spring.annotations.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.context.support.GenericApplicationContext
import java.io.File
import java.nio.file.Path
import kotlin.test.*

/**
 * Comprehensive test suite for multi-format DataSource annotations.
 */
class MultiFormatDataSourceTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var processor: DataFramePostProcessor
    private lateinit var applicationContext: GenericApplicationContext

    @BeforeEach
    fun setUp() {
        processor = DataFramePostProcessor()
        applicationContext = GenericApplicationContext()
        processor.setApplicationContext(applicationContext)
        
        // Create test data files
        createTestFiles()
    }

    private fun createTestFiles() {
        // CSV test file
        File(tempDir.toFile(), "test.csv").writeText("""
            name,age,city
            Alice,25,New York
            Bob,30,Los Angeles
            Charlie,35,Chicago
        """.trimIndent())
        
        // TSV test file  
        File(tempDir.toFile(), "test.tsv").writeText("""
            name	age	city
            David	28	Seattle
            Eve	32	Portland
        """.trimIndent())
        
        // JSON test file
        File(tempDir.toFile(), "test.json").writeText("""
            [
                {"name": "Alice", "age": 25, "city": "New York"},
                {"name": "Bob", "age": 30, "city": "Los Angeles"}
            ]
        """.trimIndent())
        
        // Complex JSON with type clashes
        File(tempDir.toFile(), "complex.json").writeText("""
            [
                {"value": "text"},
                {"value": 123},
                {"value": [1, 2, 3]}
            ]
        """.trimIndent())
    }

    @Test
    fun testCsvDataSourceAnnotation() {
        class TestBean {
            @CsvDataSource(file = "${tempDir}/test.csv")
            lateinit var data: DataFrame<*>
        }
        
        val bean = TestBean()
        processor.postProcessBeforeInitialization(bean, "testBean")
        
        assertNotNull(bean.data)
        assertEquals(3, bean.data.rowsCount())
        assertEquals(3, bean.data.columnsCount())
    }

    @Test
    fun testCsvDataSourceWithCustomDelimiter() {
        class TestBean {
            @CsvDataSource(file = "${tempDir}/test.tsv", delimiter = '\t')
            lateinit var data: DataFrame<*>
        }
        
        val bean = TestBean()
        processor.postProcessBeforeInitialization(bean, "testBean")
        
        assertNotNull(bean.data)
        assertEquals(2, bean.data.rowsCount())
        assertEquals(3, bean.data.columnsCount())
    }

    @Test
    fun testJsonDataSourceAnnotation() {
        class TestBean {
            @JsonDataSource(file = "${tempDir}/test.json")
            lateinit var data: DataFrame<*>
        }
        
        val bean = TestBean()
        processor.postProcessBeforeInitialization(bean, "testBean")
        
        assertNotNull(bean.data)
        assertEquals(2, bean.data.rowsCount())
        assertTrue(bean.data.columnsCount() >= 3)
    }

    @Test
    fun testJsonDataSourceWithTypeClashTactic() {
        class TestBean {
            @JsonDataSource(
                file = "${tempDir}/complex.json",
                typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS
            )
            lateinit var data: DataFrame<*>
        }
        
        val bean = TestBean()
        processor.postProcessBeforeInitialization(bean, "testBean")
        
        assertNotNull(bean.data)
        assertEquals(3, bean.data.rowsCount())
    }

    @Test
    fun testCsvDataSourceAnnotation_legacyReplacement() {
        class TestBean {
            @CsvDataSource(file = "${tempDir}/test.csv")
            lateinit var data: DataFrame<*>
        }
        
        val bean = TestBean()
        processor.postProcessBeforeInitialization(bean, "testBean")
        
        assertNotNull(bean.data)
        assertEquals(3, bean.data.rowsCount())
    }

    @Test
    fun testFileNotFound() {
        class TestBean {
            @CsvDataSource(file = "${tempDir}/nonexistent.csv")
            lateinit var data: DataFrame<*>
        }
        
        val bean = TestBean()
        
        val exception = assertFailsWith<RuntimeException> {
            processor.postProcessBeforeInitialization(bean, "testBean")
        }
        assertTrue(exception.message!!.contains("CSV file not found"))
    }

    @Test
    fun testNonDataFrameField() {
        class TestBean {
            @CsvDataSource(file = "${tempDir}/test.csv")
            lateinit var data: String // Wrong type - should be DataFrame
        }
        
        val bean = TestBean()
        
        // Should not throw - processor only processes DataFrame fields
        assertDoesNotThrow {
            processor.postProcessBeforeInitialization(bean, "testBean")
        }
        
        // Field should remain uninitialized
        assertFailsWith<UninitializedPropertyAccessException> {
            bean.data
        }
    }

    @Test
    fun testMultipleAnnotationsOnSameField() {
        class TestBean {
            @CsvDataSource(file = "${tempDir}/test.csv")
            @JsonDataSource(file = "${tempDir}/test.json")
            lateinit var data: DataFrame<*>
        }
        
        val bean = TestBean()
        processor.postProcessBeforeInitialization(bean, "testBean")
        
        // Should process the first annotation it finds and skip the rest
        assertNotNull(bean.data)
    }
}
