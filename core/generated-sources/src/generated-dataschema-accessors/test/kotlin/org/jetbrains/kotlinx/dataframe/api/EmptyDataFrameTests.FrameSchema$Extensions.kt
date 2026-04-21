@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema>.e: DataColumn<Double> @JvmName("FrameSchema_e") get() = this["e"] as DataColumn<Double>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema>.e: Double @JvmName("FrameSchema_e") get() = this["e"] as Double
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema?>.e: DataColumn<Double?> @JvmName("NullableFrameSchema_e") get() = this["e"] as DataColumn<Double?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema?>.e: Double? @JvmName("NullableFrameSchema_e") get() = this["e"] as Double?
