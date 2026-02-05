@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person>.age: DataColumn<Int> @JvmName("Person_age") get() = this["age"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person>.age: Int @JvmName("Person_age") get() = this["age"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person?>.age: DataColumn<Int?> @JvmName("NullablePerson_age") get() = this["age"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person?>.age: Int? @JvmName("NullablePerson_age") get() = this["age"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person>.name: DataColumn<String> @JvmName("Person_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person>.name: String @JvmName("Person_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person?>.name: DataColumn<String?> @JvmName("NullablePerson_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Schemas.Person?>.name: String? @JvmName("NullablePerson_name") get() = this["name"] as String?
