@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GetTests.Schema>.a: DataColumn<Int> @JvmName("Schema_a") get() = this["a"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GetTests.Schema>.a: Int @JvmName("Schema_a") get() = this["a"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GetTests.Schema?>.a: DataColumn<Int?> @JvmName("NullableSchema_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GetTests.Schema?>.a: Int? @JvmName("NullableSchema_a") get() = this["a"] as Int?
