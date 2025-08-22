package org.jetbrains.kotlinx.dataframe.spring.annotations

import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.io.JSON

/**
 * Annotation to mark DataFrame fields/properties that should be automatically
 * populated with data from a JSON file using Spring's dependency injection.
 * 
 * This annotation is processed by [DataFramePostProcessor] during Spring
 * bean initialization.
 * 
 * @param file The path to the JSON file to read from
 * @param keyValuePaths List of JSON paths for key-value pair processing (comma-separated)
 * @param typeClashTactic How to handle type clashes when reading JSON (default: ARRAY_AND_VALUE_COLUMNS)
 * @param unifyNumbers Whether to unify numeric types (default: true)
 * 
 * @see DataFramePostProcessor
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JsonDataSource(
    val file: String,
    val keyValuePaths: Array<String> = [],
    val typeClashTactic: JSON.TypeClashTactic = JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS,
    val unifyNumbers: Boolean = true
)