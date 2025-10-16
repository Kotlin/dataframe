package org.jetbrains.kotlinx.dataframe.spring.processors

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.spring.annotations.CsvDataSource
import org.springframework.context.ApplicationContext
import java.io.File

/**
 * Processor for @CsvDataSource annotations.
 */
class CsvDataSourceProcessor : DataSourceProcessor {
    
    override fun process(annotation: Annotation, applicationContext: ApplicationContext): AnyFrame {
        require(annotation is CsvDataSource) { 
            "Expected CsvDataSource annotation, got ${annotation::class.simpleName}" 
        }
        
        val csvFile = File(annotation.file)
        
        if (!csvFile.exists()) {
            throw IllegalArgumentException("CSV file not found: ${csvFile.absolutePath}")
        }
        
        return if (annotation.header) {
            DataFrame.readCsv(csvFile, delimiter = annotation.delimiter)
        } else {
            DataFrame.readCsv(csvFile, delimiter = annotation.delimiter, header = emptyList())
        }
    }
}