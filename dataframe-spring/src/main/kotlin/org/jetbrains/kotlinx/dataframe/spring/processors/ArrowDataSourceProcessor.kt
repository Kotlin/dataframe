package org.jetbrains.kotlinx.dataframe.spring.processors

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readArrowIPC
import org.jetbrains.kotlinx.dataframe.spring.annotations.ArrowDataSource
import org.jetbrains.kotlinx.dataframe.spring.annotations.ArrowFormat
import org.springframework.context.ApplicationContext
import java.io.File

/**
 * Processor for @ArrowDataSource annotations.
 */
class ArrowDataSourceProcessor : DataSourceProcessor {
    
    override fun process(annotation: Annotation, applicationContext: ApplicationContext): AnyFrame {
        require(annotation is ArrowDataSource) { 
            "Expected ArrowDataSource annotation, got ${annotation::class.simpleName}" 
        }
        
        val arrowFile = File(annotation.file)
        
        if (!arrowFile.exists()) {
            throw IllegalArgumentException("Arrow file not found: ${arrowFile.absolutePath}")
        }
        
        val format = when (annotation.format) {
            ArrowFormat.AUTO -> determineFormatFromExtension(arrowFile)
            ArrowFormat.IPC -> ArrowFormat.IPC
            ArrowFormat.FEATHER -> ArrowFormat.FEATHER
        }
        
        return when (format) {
            ArrowFormat.IPC -> DataFrame.readArrowIPC(arrowFile, nullability = annotation.nullability)
            ArrowFormat.FEATHER -> DataFrame.readArrowFeather(arrowFile, nullability = annotation.nullability)
            else -> throw IllegalArgumentException("Unsupported Arrow format: $format")
        }
    }
    
    private fun determineFormatFromExtension(file: File): ArrowFormat {
        return when (file.extension.lowercase()) {
            "arrow" -> ArrowFormat.IPC
            "feather" -> ArrowFormat.FEATHER
            "parquet" -> ArrowFormat.FEATHER // Treat parquet as feather
            else -> throw IllegalArgumentException(
                "Cannot determine Arrow format from file extension: ${file.extension}. " +
                "Supported extensions: .arrow, .feather, .parquet"
            )
        }
    }
}