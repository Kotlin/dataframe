package org.jetbrains.kotlinx.dataframe.spring.processors

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.springframework.context.ApplicationContext

/**
 * Strategy interface for processing different data source annotations.
 */
interface DataSourceProcessor {
    /**
     * Process the given annotation and return a DataFrame.
     * 
     * @param annotation The data source annotation
     * @param applicationContext The Spring application context for accessing beans
     * @return The loaded DataFrame
     */
    fun process(annotation: Annotation, applicationContext: ApplicationContext): AnyFrame
}