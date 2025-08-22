package org.jetbrains.kotlinx.dataframe.spring

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.spring.annotations.*
import org.jetbrains.kotlinx.dataframe.spring.processors.*
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import org.springframework.context.support.StaticApplicationContext

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
    
    // Make context optional to support both Spring-managed and manual usage
    private var applicationContext: ApplicationContext? = null
    
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
        // Skip non-DataFrame properties
        if (!isDataFrameProperty(prop)) {
            return
        }

        // Obtain reflection handles
        val field = prop.javaField
        val getter = prop.javaGetter

        // Try each supported annotation and search on property/getter/field
        for ((annotationType, processor) in processors) {
            val fromProperty = prop.annotations.firstOrNull { annotationType.isInstance(it) }
            val fromGetter = getter?.getAnnotation(annotationType)
            val fromField = field?.getAnnotation(annotationType)

            val annotation = (fromProperty ?: fromGetter ?: fromField) ?: continue

            try {
                // Use provided ApplicationContext if available; otherwise fallback to a lightweight static context
                val ctx = applicationContext ?: StaticApplicationContext()
                val dataFrame = processor.process(annotation, ctx)

                // Inject into backing field
                val targetField = field ?: prop.javaField
                    ?: throw IllegalStateException(
                        "No backing field found for property '${prop.name}' in bean '$beanName' to inject DataFrame"
                    )
                targetField.isAccessible = true
                targetField.set(bean, dataFrame)
                return // Successfully processed, stop trying other annotations
            } catch (e: Exception) {
                throw RuntimeException(
                    "Failed to process ${annotationType.simpleName} annotation for property '${prop.name}' in bean '$beanName'", 
                    e
                )
            }
        }
    }

    private fun isDataFrameProperty(prop: KProperty1<out Any, *>): Boolean {
        // Robust check that works for parameterized DataFrame<T>
        val classifier = prop.returnType.classifier as? kotlin.reflect.KClass<*> ?: return false
        return DataFrame::class.java.isAssignableFrom(classifier.java)
    }
}
