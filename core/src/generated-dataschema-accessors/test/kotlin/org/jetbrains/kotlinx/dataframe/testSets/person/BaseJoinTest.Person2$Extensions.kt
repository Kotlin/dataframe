@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.person
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2>.grade: DataColumn<Int> @JvmName("Person2_grade") get() = this["grade"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2>.grade: Int @JvmName("Person2_grade") get() = this["grade"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2?>.grade: DataColumn<Int?> @JvmName("NullablePerson2_grade") get() = this["grade"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2?>.grade: Int? @JvmName("NullablePerson2_grade") get() = this["grade"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2>.name: DataColumn<String> @JvmName("Person2_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2>.name: String @JvmName("Person2_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2?>.name: DataColumn<String?> @JvmName("NullablePerson2_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2?>.name: String? @JvmName("NullablePerson2_name") get() = this["name"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2>.origin: DataColumn<String?> @JvmName("Person2_origin") get() = this["origin"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2>.origin: String? @JvmName("Person2_origin") get() = this["origin"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2?>.origin: DataColumn<String?> @JvmName("NullablePerson2_origin") get() = this["origin"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.BaseJoinTest.Person2?>.origin: String? @JvmName("NullablePerson2_origin") get() = this["origin"] as String?
