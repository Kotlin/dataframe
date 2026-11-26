@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name>.firstName: DataColumn<String> @JvmName("Name_firstName") get() = this["firstName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name>.firstName: String @JvmName("Name_firstName") get() = this["firstName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?>.firstName: DataColumn<String?> @JvmName("NullableName_firstName") get() = this["firstName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?>.firstName: String? @JvmName("NullableName_firstName") get() = this["firstName"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name>.lastName: DataColumn<String> @JvmName("Name_lastName") get() = this["lastName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name>.lastName: String @JvmName("Name_lastName") get() = this["lastName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?>.lastName: DataColumn<String?> @JvmName("NullableName_lastName") get() = this["lastName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?>.lastName: String? @JvmName("NullableName_lastName") get() = this["lastName"] as String?
