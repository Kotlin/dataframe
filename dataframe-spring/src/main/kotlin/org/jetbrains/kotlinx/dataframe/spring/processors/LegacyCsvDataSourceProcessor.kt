package org.jetbrains.kotlinx.dataframe.spring.processors

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.spring.annotations.DataSource
import org.springframework.context.ApplicationContext
import java.io.File

/**
 * Processor for legacy @DataSource annotations (for backward compatibility).
 * 
 * @deprecated Use @CsvDataSource instead
 */
class LegacyCsvDataSourceProcessor : DataSourceProcessor {
    
    override fun process(annotation: Annotation, applicationContext: ApplicationContext): AnyFrame {
        require(annotation is DataSource) { 
            "Expected DataSource annotation, got ${annotation::class.simpleName}" 
        }
        
        val csvFile = File(annotation.csvFile)
        
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