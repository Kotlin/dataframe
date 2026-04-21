@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>.v: DataColumn<Int> @JvmName("A_v") get() = this["v"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>.v: Int @JvmName("A_v") get() = this["v"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>.v: DataColumn<Int?> @JvmName("NullableA_v") get() = this["v"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>.v: Int? @JvmName("NullableA_v") get() = this["v"] as Int?
