package org.jetbrains.kotlinx.dataframe.spring.processors

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.jetbrains.kotlinx.dataframe.spring.annotations.JdbcDataSource
import org.springframework.context.ApplicationContext
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource

/**
 * Processor for @JdbcDataSource annotations.
 */
class JdbcDataSourceProcessor : DataSourceProcessor {
    
    override fun process(annotation: Annotation, applicationContext: ApplicationContext): AnyFrame {
        require(annotation is JdbcDataSource) { 
            "Expected JdbcDataSource annotation, got ${annotation::class.simpleName}" 
        }
        
        val connection = getConnection(annotation, applicationContext)
        
        try {
            return when {
                annotation.query.isNotEmpty() -> {
                    // Execute custom query
                    DataFrame.readSqlQuery(connection, annotation.query, limit = annotation.limit)
                }
                annotation.tableName.isNotEmpty() -> {
                    // Query table
                    DataFrame.readSqlQuery(connection, annotation.query)
                }
                else -> {
                    throw IllegalArgumentException("Either 'tableName' or 'query' must be specified")
                }
            }
        } finally {
            // Only close if we created the connection ourselves
            if (annotation.connectionBean.isEmpty()) {
                connection.close()
            }
        }
    }
    
    private fun getConnection(annotation: JdbcDataSource, applicationContext: ApplicationContext): Connection {
        return when {
            annotation.connectionBean.isNotEmpty() -> {
                // Use connection from Spring context
                val bean = applicationContext.getBean(annotation.connectionBean)
                when (bean) {
                    is Connection -> bean
                    is DataSource -> bean.connection
                    else -> throw IllegalArgumentException(
                        "Bean '${annotation.connectionBean}' is not a Connection or DataSource, got ${bean::class.simpleName}"
                    )
                }
            }
            annotation.url.isNotEmpty() -> {
                // Create connection from URL
                if (annotation.username.isNotEmpty() && annotation.password.isNotEmpty()) {
                    DriverManager.getConnection(annotation.url, annotation.username, annotation.password)
                } else {
                    DriverManager.getConnection(annotation.url)
                }
            }
            else -> {
                throw IllegalArgumentException("Either 'connectionBean' or 'url' must be specified")
            }
        }
    }
}
