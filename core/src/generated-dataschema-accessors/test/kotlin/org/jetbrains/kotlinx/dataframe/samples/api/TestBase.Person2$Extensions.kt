@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.age: DataColumn<Int> @JvmName("Person2_age") get() = this["age"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.age: Int @JvmName("Person2_age") get() = this["age"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.age: DataColumn<Int?> @JvmName("NullablePerson2_age") get() = this["age"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.age: Int? @JvmName("NullablePerson2_age") get() = this["age"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.city: DataColumn<String?> @JvmName("Person2_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.city: String? @JvmName("Person2_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.city: DataColumn<String?> @JvmName("NullablePerson2_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.city: String? @JvmName("NullablePerson2_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.isHappy: DataColumn<Boolean> @JvmName("Person2_isHappy") get() = this["isHappy"] as DataColumn<Boolean>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.isHappy: Boolean @JvmName("Person2_isHappy") get() = this["isHappy"] as Boolean
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.isHappy: DataColumn<Boolean?> @JvmName("NullablePerson2_isHappy") get() = this["isHappy"] as DataColumn<Boolean?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.isHappy: Boolean? @JvmName("NullablePerson2_isHappy") get() = this["isHappy"] as Boolean?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.name: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2> @JvmName("Person2_name") get() = this["name"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.name: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2> @JvmName("Person2_name") get() = this["name"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.name: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?> @JvmName("NullablePerson2_name") get() = this["name"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.name: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?> @JvmName("NullablePerson2_name") get() = this["name"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name2?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.weight: DataColumn<Int?> @JvmName("Person2_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2>.weight: Int? @JvmName("Person2_weight") get() = this["weight"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.weight: DataColumn<Int?> @JvmName("NullablePerson2_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person2?>.weight: Int? @JvmName("NullablePerson2_weight") get() = this["weight"] as Int?
