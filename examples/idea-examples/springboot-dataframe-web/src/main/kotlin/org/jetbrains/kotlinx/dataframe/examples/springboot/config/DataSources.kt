package org.jetbrains.kotlinx.dataframe.examples.springboot.config

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.jetbrains.kotlinx.dataframe.spring.annotations.CsvDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class DataSources {
    @CsvDataSource(file = "data/spring/customers.csv")
    lateinit var customers: DataFrame<*>

    @CsvDataSource(file = "data/spring/sales.csv")
    lateinit var sales: DataFrame<*>
}



