@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.person
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson>.age: DataColumn<Int> @JvmName("GroupedPerson_age") get() = this["age"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson>.age: Int @JvmName("GroupedPerson_age") get() = this["age"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson?>.age: DataColumn<Int?> @JvmName("NullableGroupedPerson_age") get() = this["age"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson?>.age: Int? @JvmName("NullableGroupedPerson_age") get() = this["age"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson>.nameAndCity: ColumnGroup<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity> @JvmName("GroupedPerson_nameAndCity") get() = this["nameAndCity"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson>.nameAndCity: DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity> @JvmName("GroupedPerson_nameAndCity") get() = this["nameAndCity"] as DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson?>.nameAndCity: ColumnGroup<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?> @JvmName("NullableGroupedPerson_nameAndCity") get() = this["nameAndCity"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson?>.nameAndCity: DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?> @JvmName("NullableGroupedPerson_nameAndCity") get() = this["nameAndCity"] as DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.NameAndCity?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson>.weight: DataColumn<Int?> @JvmName("GroupedPerson_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson>.weight: Int? @JvmName("GroupedPerson_weight") get() = this["weight"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson?>.weight: DataColumn<Int?> @JvmName("NullableGroupedPerson_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTreeTests.GroupedPerson?>.weight: Int? @JvmName("NullableGroupedPerson_weight") get() = this["weight"] as Int?
