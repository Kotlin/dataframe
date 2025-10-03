package org.jetbrains.kotlinx.dataframe.examples.springboot.service

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.examples.springboot.config.DataSources
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val dataSources: DataSources
) {
    fun customersSortedByName(): DataFrame<*> =
        dataSources.customers.sortBy("name")

    fun customersFilteredByCountry(country: String): DataFrame<*> =
        dataSources.customers.filter { it["country"].toString().equals(country, ignoreCase = true) }

    fun salesSortedByValueDesc(): DataFrame<*> =
        dataSources.sales.sortByDesc("value")
}
