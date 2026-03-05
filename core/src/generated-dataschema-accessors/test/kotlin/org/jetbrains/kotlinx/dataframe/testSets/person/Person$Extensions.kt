@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.person
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.age: DataColumn<Int> @JvmName("Person_age") get() = this["age"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.age: Int @JvmName("Person_age") get() = this["age"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.age: DataColumn<Int?> @JvmName("NullablePerson_age") get() = this["age"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.age: Int? @JvmName("NullablePerson_age") get() = this["age"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.city: DataColumn<String?> @JvmName("Person_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.city: String? @JvmName("Person_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.city: DataColumn<String?> @JvmName("NullablePerson_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.city: String? @JvmName("NullablePerson_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.name: DataColumn<String> @JvmName("Person_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.name: String @JvmName("Person_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.name: DataColumn<String?> @JvmName("NullablePerson_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.name: String? @JvmName("NullablePerson_name") get() = this["name"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.weight: DataColumn<Int?> @JvmName("Person_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person>.weight: Int? @JvmName("Person_weight") get() = this["weight"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.weight: DataColumn<Int?> @JvmName("NullablePerson_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.Person?>.weight: Int? @JvmName("NullablePerson_weight") get() = this["weight"] as Int?
