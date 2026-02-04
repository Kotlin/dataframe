@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.age: DataColumn<Int> @JvmName("TitanicPassenger_age") get() = this["age"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.age: Int @JvmName("TitanicPassenger_age") get() = this["age"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.age: DataColumn<Int?> @JvmName("NullableTitanicPassenger_age") get() = this["age"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.age: Int? @JvmName("NullableTitanicPassenger_age") get() = this["age"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.home: DataColumn<String> @JvmName("TitanicPassenger_home") get() = this["home"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.home: String @JvmName("TitanicPassenger_home") get() = this["home"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.home: DataColumn<String?> @JvmName("NullableTitanicPassenger_home") get() = this["home"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.home: String? @JvmName("NullableTitanicPassenger_home") get() = this["home"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.name: DataColumn<String> @JvmName("TitanicPassenger_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.name: String @JvmName("TitanicPassenger_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.name: DataColumn<String?> @JvmName("NullableTitanicPassenger_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.name: String? @JvmName("NullableTitanicPassenger_name") get() = this["name"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.survived: DataColumn<Boolean> @JvmName("TitanicPassenger_survived") get() = this["survived"] as DataColumn<Boolean>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger>.survived: Boolean @JvmName("TitanicPassenger_survived") get() = this["survived"] as Boolean
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.survived: DataColumn<Boolean?> @JvmName("NullableTitanicPassenger_survived") get() = this["survived"] as DataColumn<Boolean?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels.TitanicPassenger?>.survived: Boolean? @JvmName("NullableTitanicPassenger_survived") get() = this["survived"] as Boolean?
