@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.ImplicitSchema>.perf: DataColumn<Double> @JvmName("ImplicitSchema_perf") get() = this["perf"] as DataColumn<Double>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.ImplicitSchema>.perf: Double @JvmName("ImplicitSchema_perf") get() = this["perf"] as Double
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.ImplicitSchema?>.perf: DataColumn<Double?> @JvmName("NullableImplicitSchema_perf") get() = this["perf"] as DataColumn<Double?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.ImplicitSchema?>.perf: Double? @JvmName("NullableImplicitSchema_perf") get() = this["perf"] as Double?
