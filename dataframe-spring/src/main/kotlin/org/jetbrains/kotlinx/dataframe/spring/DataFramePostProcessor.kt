package org.jetbrains.kotlinx.dataframe.spring

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.spring.annotations.DataSource
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.io.File
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * Spring BeanPostProcessor that automatically populates DataFrame fields
 * annotated with @DataSource by reading CSV files.
 * 
 * This processor scans all Spring beans for fields/properties annotated
 * with @DataSource and automatically loads the specified CSV files into
 * DataFrame instances.
 * 
 * Usage:
 * ```kotlin
 * @Component
 * class MyDataService {
 *     @DataSource(csvFile = "data.csv")
 *     lateinit var df: DataFrame<MyRow>
 * 
 *     fun process() {
 *         println(df.rowsCount())
 *     }
 * }
 * ```
 */
@Component
class DataFramePostProcessor : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        try {
            bean::class.memberProperties.forEach { prop ->
                processProperty(bean, prop)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to process @DataSource annotations for bean '$beanName'", e)
        }
        return bean
    }

    private fun processProperty(bean: Any, prop: KProperty1<out Any, *>) {
        val annotation = prop.findAnnotation<DataSource>() ?: return
        
        // Check if the property is a DataFrame type
        if (!isDataFrameProperty(prop)) {
            throw IllegalArgumentException(
                "Property '${prop.name}' is annotated with @DataSource but is not a DataFrame type"
            )
        }

        // Get the Java field for reflection access
        val field = prop.javaField ?: throw IllegalArgumentException(
            "Cannot access field '${prop.name}' for @DataSource processing"
        )

        // Read the CSV file
        val csvPath = annotation.csvFile
        val csvFile = File(csvPath)
        
        if (!csvFile.exists()) {
            throw IllegalArgumentException("CSV file not found: ${csvFile.absolutePath}")
        }

        try {
            val dataFrame = if (annotation.header) {
                DataFrame.readCsv(csvFile, delimiter = annotation.delimiter)
            } else {
                DataFrame.readCsv(csvFile, delimiter = annotation.delimiter, header = emptyList())
            }

            // Set the field value
            field.isAccessible = true
            field.set(bean, dataFrame)
        } catch (e: Exception) {
            throw RuntimeException("Failed to read CSV file '$csvPath' for property '${prop.name}'", e)
        }
    }

    private fun isDataFrameProperty(prop: KProperty1<out Any, *>): Boolean {
        val returnType = prop.returnType
        val classifier = returnType.classifier
        return classifier == DataFrame::class
    }
}