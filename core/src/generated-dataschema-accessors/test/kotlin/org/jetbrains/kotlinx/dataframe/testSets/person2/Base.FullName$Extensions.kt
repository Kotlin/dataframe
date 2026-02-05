@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.person2
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName>.firstName: DataColumn<String> @JvmName("FullName_firstName") get() = this["firstName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName>.firstName: String @JvmName("FullName_firstName") get() = this["firstName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName?>.firstName: DataColumn<String?> @JvmName("NullableFullName_firstName") get() = this["firstName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName?>.firstName: String? @JvmName("NullableFullName_firstName") get() = this["firstName"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName>.lastName: DataColumn<String> @JvmName("FullName_lastName") get() = this["lastName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName>.lastName: String @JvmName("FullName_lastName") get() = this["lastName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName?>.lastName: DataColumn<String?> @JvmName("NullableFullName_lastName") get() = this["lastName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person2.Base.FullName?>.lastName: String? @JvmName("NullableFullName_lastName") get() = this["lastName"] as String?
