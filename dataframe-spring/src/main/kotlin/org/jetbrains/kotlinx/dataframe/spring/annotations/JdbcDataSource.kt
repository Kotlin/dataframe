package org.jetbrains.kotlinx.dataframe.spring.annotations

/**
 * Annotation to mark DataFrame fields/properties that should be automatically
 * populated with data from a JDBC database using Spring's dependency injection.
 * 
 * This annotation is processed by [DataFramePostProcessor] during Spring
 * bean initialization.
 * 
 * @param url The JDBC URL to connect to (if not using existing connection)
 * @param connectionBean Spring bean name containing a javax.sql.Connection or javax.sql.DataSource (optional)
 * @param tableName The name of the table to query
 * @param query Custom SQL query to execute (overrides tableName if provided)  
 * @param limit Maximum number of records to fetch (default: no limit)
 * @param username Database username (if not using connectionBean)
 * @param password Database password (if not using connectionBean)
 * 
 * @see DataFramePostProcessor
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JdbcDataSource(
    val url: String = "",
    val connectionBean: String = "",
    val tableName: String = "",
    val query: String = "",
    val limit: Int = -1,
    val username: String = "",
    val password: String = ""
)