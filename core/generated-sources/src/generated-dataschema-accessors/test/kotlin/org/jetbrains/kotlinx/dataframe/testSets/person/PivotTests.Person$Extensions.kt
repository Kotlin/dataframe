@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.person
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person>.`value`: DataColumn<Any?> @JvmName("Person_value") get() = this["value"] as DataColumn<Any?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person>.`value`: Any? @JvmName("Person_value") get() = this["value"] as Any?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person?>.`value`: DataColumn<Any?> @JvmName("NullablePerson_value") get() = this["value"] as DataColumn<Any?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person?>.`value`: Any? @JvmName("NullablePerson_value") get() = this["value"] as Any?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person>.key: DataColumn<String> @JvmName("Person_key") get() = this["key"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person>.key: String @JvmName("Person_key") get() = this["key"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person?>.key: DataColumn<String?> @JvmName("NullablePerson_key") get() = this["key"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person?>.key: String? @JvmName("NullablePerson_key") get() = this["key"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person>.name: DataColumn<String> @JvmName("Person_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person>.name: String @JvmName("Person_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person?>.name: DataColumn<String?> @JvmName("NullablePerson_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.PivotTests.Person?>.name: String? @JvmName("NullablePerson_name") get() = this["name"] as String?
