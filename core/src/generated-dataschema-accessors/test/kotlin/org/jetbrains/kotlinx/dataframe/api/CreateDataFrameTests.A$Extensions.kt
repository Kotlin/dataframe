@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>.v: DataColumn<Int> @JvmName("A_v") get() = this["v"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>.v: Int @JvmName("A_v") get() = this["v"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>.v: DataColumn<Int?> @JvmName("NullableA_v") get() = this["v"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>.v: Int? @JvmName("NullableA_v") get() = this["v"] as Int?
