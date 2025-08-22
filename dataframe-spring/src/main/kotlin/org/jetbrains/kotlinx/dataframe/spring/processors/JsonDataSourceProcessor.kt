package org.jetbrains.kotlinx.dataframe.spring.processors

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.spring.annotations.JsonDataSource
import org.springframework.context.ApplicationContext
import java.io.File

/**
 * Processor for @JsonDataSource annotations.
 */
class JsonDataSourceProcessor : DataSourceProcessor {
    
    override fun process(annotation: Annotation, applicationContext: ApplicationContext): AnyFrame {
        require(annotation is JsonDataSource) { 
            "Expected JsonDataSource annotation, got ${annotation::class.simpleName}" 
        }
        
        val jsonFile = File(annotation.file)
        
        if (!jsonFile.exists()) {
            throw IllegalArgumentException("JSON file not found: ${jsonFile.absolutePath}")
        }
        
        val keyValuePaths = annotation.keyValuePaths.map { JsonPath(it) }
        
        return DataFrame.readJson(
            file = jsonFile,
            keyValuePaths = keyValuePaths,
            typeClashTactic = annotation.typeClashTactic,
            unifyNumbers = annotation.unifyNumbers
        )
    }
}