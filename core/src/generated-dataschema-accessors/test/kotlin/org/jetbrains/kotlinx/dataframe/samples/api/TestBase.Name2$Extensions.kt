@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2>.firstName: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames> @JvmName("Name2_firstName") get() = this["firstName"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2>.firstName: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames> @JvmName("Name2_firstName") get() = this["firstName"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?>.firstName: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?> @JvmName("NullableName2_firstName") get() = this["firstName"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?>.firstName: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?> @JvmName("NullableName2_firstName") get() = this["firstName"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2>.lastName: DataColumn<String> @JvmName("Name2_lastName") get() = this["lastName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2>.lastName: String @JvmName("Name2_lastName") get() = this["lastName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?>.lastName: DataColumn<String?> @JvmName("NullableName2_lastName") get() = this["lastName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?>.lastName: String? @JvmName("NullableName2_lastName") get() = this["lastName"] as String?
