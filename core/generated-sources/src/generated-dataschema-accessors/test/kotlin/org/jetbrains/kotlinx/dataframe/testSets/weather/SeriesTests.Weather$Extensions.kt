@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.weather
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather>.city: DataColumn<String> @JvmName("Weather_city") get() = this["city"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather>.city: String @JvmName("Weather_city") get() = this["city"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather?>.city: DataColumn<String?> @JvmName("NullableWeather_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather?>.city: String? @JvmName("NullableWeather_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather>.day: DataColumn<Int> @JvmName("Weather_day") get() = this["day"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather>.day: Int @JvmName("Weather_day") get() = this["day"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather?>.day: DataColumn<Int?> @JvmName("NullableWeather_day") get() = this["day"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather?>.day: Int? @JvmName("NullableWeather_day") get() = this["day"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather>.temp: DataColumn<Int> @JvmName("Weather_temp") get() = this["temp"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather>.temp: Int @JvmName("Weather_temp") get() = this["temp"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather?>.temp: DataColumn<Int?> @JvmName("NullableWeather_temp") get() = this["temp"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.weather.SeriesTests.Weather?>.temp: Int? @JvmName("NullableWeather_temp") get() = this["temp"] as Int?
