@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.age: DataColumn<Int> @JvmName("Person_age") get() = this["age"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.age: Int @JvmName("Person_age") get() = this["age"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.age: DataColumn<Int?> @JvmName("NullablePerson_age") get() = this["age"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.age: Int? @JvmName("NullablePerson_age") get() = this["age"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.city: DataColumn<String?> @JvmName("Person_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.city: String? @JvmName("Person_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.city: DataColumn<String?> @JvmName("NullablePerson_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.city: String? @JvmName("NullablePerson_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.firstName: DataColumn<String> @JvmName("Person_firstName") get() = this["firstName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.firstName: String @JvmName("Person_firstName") get() = this["firstName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.firstName: DataColumn<String?> @JvmName("NullablePerson_firstName") get() = this["firstName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.firstName: String? @JvmName("NullablePerson_firstName") get() = this["firstName"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.lastName: DataColumn<String> @JvmName("Person_lastName") get() = this["lastName"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person>.lastName: String @JvmName("Person_lastName") get() = this["lastName"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.lastName: DataColumn<String?> @JvmName("NullablePerson_lastName") get() = this["lastName"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.Person?>.lastName: String? @JvmName("NullablePerson_lastName") get() = this["lastName"] as String?
