package org.jetbrains.kotlinx.dataframe.spring

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.spring.annotations.*
import org.jetbrains.kotlinx.dataframe.spring.processors.*
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * Spring BeanPostProcessor that automatically populates DataFrame fields
 * annotated with data source annotations.
 * 
 * This processor scans all Spring beans for fields/properties annotated
 * with supported data source annotations and automatically loads the specified 
 * data into DataFrame instances.
 * 
 * Supported annotations:
 * - @CsvDataSource - for CSV files
 * - @JsonDataSource - for JSON files  
 * - @ArrowDataSource - for Arrow/Parquet/Feather files
 * - @JdbcDataSource - for database tables/queries
 * - @DataSource - legacy CSV annotation (deprecated)
 * 
 * Usage:
 * ```kotlin
 * @Component
 * class MyDataService {
 *     @CsvDataSource(file = "data.csv")
 *     lateinit var csvData: DataFrame<*>
 *     
 *     @JsonDataSource(file = "data.json")
 *     lateinit var jsonData: DataFrame<*>
 *     
 *     @ArrowDataSource(file = "data.feather")
 *     lateinit var arrowData: DataFrame<*>
 *     
 *     @JdbcDataSource(url = "jdbc:h2:mem:test", tableName = "users")
 *     lateinit var dbData: DataFrame<*>
 * }
 * ```
 */
@Component
class DataFramePostProcessor : BeanPostProcessor, ApplicationContextAware {
    
    private lateinit var applicationContext: ApplicationContext
    
    private val processors = mapOf<Class<out Annotation>, DataSourceProcessor>(
        CsvDataSource::class.java to CsvDataSourceProcessor(),
        JsonDataSource::class.java to JsonDataSourceProcessor(),
        ArrowDataSource::class.java to ArrowDataSourceProcessor(),
        JdbcDataSource::class.java to JdbcDataSourceProcessor(),
        DataSource::class.java to LegacyCsvDataSourceProcessor() // For backward compatibility
    )

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        try {
            bean::class.memberProperties.forEach { prop ->
                processProperty(bean, prop, beanName)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to process DataSource annotations for bean '$beanName'", e)
        }
        return bean
    }

    private fun processProperty(bean: Any, prop: KProperty1<out Any, *>, beanName: String) {
        // Check if the property is a DataFrame type
        if (!isDataFrameProperty(prop)) {
            return
        }

        // Get the Java field for reflection access
        val field = prop.javaField ?: return

        // Try each supported annotation type
        for ((annotationType, processor) in processors) {
            val annotation = prop.findAnnotation(annotationType) ?: continue
            
            try {
                val dataFrame = processor.process(annotation, applicationContext)
                field.isAccessible = true
                field.set(bean, dataFrame)
                return // Successfully processed, don't try other annotations
            } catch (e: Exception) {
                throw RuntimeException(
                    "Failed to process ${annotationType.simpleName} annotation for property '${prop.name}' in bean '$beanName'", 
                    e
                )
            }
        }
    }

    private fun isDataFrameProperty(prop: KProperty1<out Any, *>): Boolean {
        val returnType = prop.returnType
        val classifier = returnType.classifier
        return classifier == DataFrame::class
    }
}