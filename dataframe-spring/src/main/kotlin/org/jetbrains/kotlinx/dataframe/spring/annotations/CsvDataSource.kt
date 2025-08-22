package org.jetbrains.kotlinx.dataframe.spring.annotations

/**
 * Annotation to mark DataFrame fields/properties that should be automatically
 * populated with data from a CSV file using Spring's dependency injection.
 * 
 * This annotation is processed by [DataFramePostProcessor] during Spring
 * bean initialization.
 * 
 * @param file The path to the CSV file to read from
 * @param delimiter The delimiter character to use for CSV parsing (default: ',')
 * @param header Whether the first row contains column headers (default: true)
 * 
 * @see DataFramePostProcessor
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CsvDataSource(
    val file: String,
    val delimiter: Char = ',',
    val header: Boolean = true
)
