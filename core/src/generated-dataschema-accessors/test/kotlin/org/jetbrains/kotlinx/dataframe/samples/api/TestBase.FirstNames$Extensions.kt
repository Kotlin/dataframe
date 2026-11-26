@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>.firstName: DataColumn<String> @JvmName("FirstNames_firstName") get() = this["firstName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>.firstName: String @JvmName("FirstNames_firstName") get() = this["firstName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>.firstName: DataColumn<String?> @JvmName("NullableFirstNames_firstName") get() = this["firstName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>.firstName: String? @JvmName("NullableFirstNames_firstName") get() = this["firstName"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>.secondName: DataColumn<String?> @JvmName("FirstNames_secondName") get() = this["secondName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>.secondName: String? @JvmName("FirstNames_secondName") get() = this["secondName"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>.secondName: DataColumn<String?> @JvmName("NullableFirstNames_secondName") get() = this["secondName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>.secondName: String? @JvmName("NullableFirstNames_secondName") get() = this["secondName"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>.thirdName: DataColumn<String?> @JvmName("FirstNames_thirdName") get() = this["thirdName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames>.thirdName: String? @JvmName("FirstNames_thirdName") get() = this["thirdName"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>.thirdName: DataColumn<String?> @JvmName("NullableFirstNames_thirdName") get() = this["thirdName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.FirstNames?>.thirdName: String? @JvmName("NullableFirstNames_thirdName") get() = this["thirdName"] as String?
