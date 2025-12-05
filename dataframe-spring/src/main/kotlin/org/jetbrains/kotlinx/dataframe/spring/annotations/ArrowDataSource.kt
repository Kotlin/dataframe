package org.jetbrains.kotlinx.dataframe.spring.annotations

import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions

/**
 * Annotation to mark DataFrame fields/properties that should be automatically
 * populated with data from an Arrow/Parquet file using Spring's dependency injection.
 * 
 * This annotation is processed by [DataFramePostProcessor] during Spring
 * bean initialization. Supports both Arrow IPC (.arrow) and Feather (.feather) formats.
 * 
 * @param file The path to the Arrow/Parquet/Feather file to read from
 * @param format The file format to use (AUTO, IPC, FEATHER)
 * @param nullability How to handle nullable types (default: Infer)
 * 
 * @see org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ArrowDataSource(
    val file: String,
    val format: ArrowFormat = ArrowFormat.AUTO,
    val nullability: NullabilityOptions = NullabilityOptions.Infer
)

enum class ArrowFormat {
    /**
     * Automatically detect format based on file extension
     */
    AUTO,
    
    /**
     * Arrow Interprocess Communication format (.arrow)
     */
    IPC,
    
    /**
     * Arrow Feather format (.feather)
     */
    FEATHER
}
