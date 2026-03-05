@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema>.`value`: DataColumn<Int> @JvmName("MySchema_value") get() = this["value"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema>.`value`: Int @JvmName("MySchema_value") get() = this["value"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema?>.`value`: DataColumn<Int?> @JvmName("NullableMySchema_value") get() = this["value"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema?>.`value`: Int? @JvmName("NullableMySchema_value") get() = this["value"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema>.key: DataColumn<String> @JvmName("MySchema_key") get() = this["key"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema>.key: String @JvmName("MySchema_key") get() = this["key"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema?>.key: DataColumn<String?> @JvmName("NullableMySchema_key") get() = this["key"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.MySchema?>.key: String? @JvmName("NullableMySchema_key") get() = this["key"] as String?
