@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.person
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity>.city: DataColumn<String?> @JvmName("NameAndCity_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity>.city: String? @JvmName("NameAndCity_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?>.city: DataColumn<String?> @JvmName("NullableNameAndCity_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?>.city: String? @JvmName("NullableNameAndCity_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity>.name: DataColumn<String> @JvmName("NameAndCity_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity>.name: String @JvmName("NameAndCity_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?>.name: DataColumn<String?> @JvmName("NullableNameAndCity_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?>.name: String? @JvmName("NullableNameAndCity_name") get() = this["name"] as String?
