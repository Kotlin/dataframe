package org.jetbrains.kotlinx.dataframe.examples.springboot.config

import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DataFrameConfiguration {
    @Bean
    open fun dataFramePostProcessor(): DataFramePostProcessor = DataFramePostProcessor()
}
