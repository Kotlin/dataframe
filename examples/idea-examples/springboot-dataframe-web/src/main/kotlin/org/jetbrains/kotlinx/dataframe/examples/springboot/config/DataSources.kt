package org.jetbrains.kotlinx.dataframe.examples.springboot.config

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.spring.annotations.CsvDataSource
import org.springframework.stereotype.Component

@Component
class DataSources {
    @CsvDataSource(file = "data/customers.csv")
    lateinit var customers: DataFrame<*>

    @CsvDataSource(file = "data/sales.csv")
    lateinit var sales: DataFrame<*>
}
