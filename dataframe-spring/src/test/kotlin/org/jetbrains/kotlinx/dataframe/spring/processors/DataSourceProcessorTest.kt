package org.jetbrains.kotlinx.dataframe.spring.processors

import org.jetbrains.kotlinx.dataframe.spring.annotations.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.context.support.GenericApplicationContext
import java.io.File
import java.nio.file.Path
import kotlin.test.*

/**
 * Unit tests for individual DataSource processors.
 */
class DataSourceProcessorTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var applicationContext: GenericApplicationContext

    @BeforeEach
    fun setUp() {
        applicationContext = GenericApplicationContext()
        
        // Create test CSV file
        File(tempDir.toFile(), "test.csv").writeText("""
            name,age,city
            Alice,25,New York
            Bob,30,Los Angeles
        """.trimIndent())
        
        // Create test JSON file
        File(tempDir.toFile(), "test.json").writeText("""
            [
                {"name": "Alice", "age": 25},
                {"name": "Bob", "age": 30}
            ]
        """.trimIndent())
    }
/*
    @Test
    fun testCsvDataSourceProcessor() {
        val processor = CsvDataSourceProcessor()
        val annotation = object : CsvDataSource {
            override val file: String = "${tempDir}/test.csv"
            override val delimiter: Char = ','
            override val header: Boolean = true
            fun annotationClass() = CsvDataSource::class
        }
        
        val dataFrame = processor.process(annotation, applicationContext)
        
        assertEquals(2, dataFrame.rowsCount())
        assertEquals(3, dataFrame.columnsCount())
    }

    @Test
    fun testJsonDataSourceProcessor() {
        val processor = JsonDataSourceProcessor()
        val annotation = object : JsonDataSource {
            override val file: String = "${tempDir}/test.json"
            override val keyValuePaths: Array<String> = emptyArray()
            override val typeClashTactic = org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
            override val unifyNumbers: Boolean = true
            fun annotationClass() = JsonDataSource::class
        }
        
        val dataFrame = processor.process(annotation, applicationContext)
        
        assertEquals(2, dataFrame.rowsCount())
    }

    @Test
    fun testCsvProcessorWithWrongAnnotationType() {
        val processor = CsvDataSourceProcessor()
        val wrongAnnotation = object : JsonDataSource {
            override val file: String = "${tempDir}/test.json"
            override val keyValuePaths: Array<String> = emptyArray()
            override val typeClashTactic = org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
            override val unifyNumbers: Boolean = true
            fun annotationClass() = JsonDataSource::class
        }
        
        assertFailsWith<IllegalArgumentException> {
            processor.process(wrongAnnotation, applicationContext)
        }
    }

    @Test
    fun testCsvProcessorWithMissingFile() {
        val processor = CsvDataSourceProcessor()
        val annotation = object : CsvDataSource {
            override val file: String = "${tempDir}/missing.csv"
            override val delimiter: Char = ','
            override val header: Boolean = true
            fun annotationClass() = CsvDataSource::class
        }
        
        assertFailsWith<IllegalArgumentException> {
            processor.process(annotation, applicationContext)
        }
    }*/
}
