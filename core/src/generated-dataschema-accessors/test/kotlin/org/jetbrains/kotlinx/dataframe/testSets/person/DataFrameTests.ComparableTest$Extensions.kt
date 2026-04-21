@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.testSets.person
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableInt: DataColumn<kotlin.Comparable<kotlin.Int>> @JvmName("ComparableTest_comparableInt") get() = this["comparableInt"] as DataColumn<kotlin.Comparable<kotlin.Int>>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableInt: kotlin.Comparable<kotlin.Int> @JvmName("ComparableTest_comparableInt") get() = this["comparableInt"] as kotlin.Comparable<kotlin.Int>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableInt: DataColumn<kotlin.Comparable<kotlin.Int>?> @JvmName("NullableComparableTest_comparableInt") get() = this["comparableInt"] as DataColumn<kotlin.Comparable<kotlin.Int>?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableInt: kotlin.Comparable<kotlin.Int>? @JvmName("NullableComparableTest_comparableInt") get() = this["comparableInt"] as kotlin.Comparable<kotlin.Int>?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableNothing: DataColumn<kotlin.Comparable<kotlin.Nothing>> @JvmName("ComparableTest_comparableNothing") get() = this["comparableNothing"] as DataColumn<kotlin.Comparable<kotlin.Nothing>>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableNothing: kotlin.Comparable<kotlin.Nothing> @JvmName("ComparableTest_comparableNothing") get() = this["comparableNothing"] as kotlin.Comparable<kotlin.Nothing>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableNothing: DataColumn<kotlin.Comparable<kotlin.Nothing>?> @JvmName("NullableComparableTest_comparableNothing") get() = this["comparableNothing"] as DataColumn<kotlin.Comparable<kotlin.Nothing>?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableNothing: kotlin.Comparable<kotlin.Nothing>? @JvmName("NullableComparableTest_comparableNothing") get() = this["comparableNothing"] as kotlin.Comparable<kotlin.Nothing>?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableStar: DataColumn<Comparable<*>> @JvmName("ComparableTest_comparableStar") get() = this["comparableStar"] as DataColumn<Comparable<*>>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableStar: Comparable<*> @JvmName("ComparableTest_comparableStar") get() = this["comparableStar"] as Comparable<*>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableStar: DataColumn<Comparable<*>?> @JvmName("NullableComparableTest_comparableStar") get() = this["comparableStar"] as DataColumn<Comparable<*>?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableStar: Comparable<*>? @JvmName("NullableComparableTest_comparableStar") get() = this["comparableStar"] as Comparable<*>?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableString: DataColumn<kotlin.Comparable<kotlin.String>> @JvmName("ComparableTest_comparableString") get() = this["comparableString"] as DataColumn<kotlin.Comparable<kotlin.String>>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.comparableString: kotlin.Comparable<kotlin.String> @JvmName("ComparableTest_comparableString") get() = this["comparableString"] as kotlin.Comparable<kotlin.String>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableString: DataColumn<kotlin.Comparable<kotlin.String>?> @JvmName("NullableComparableTest_comparableString") get() = this["comparableString"] as DataColumn<kotlin.Comparable<kotlin.String>?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.comparableString: kotlin.Comparable<kotlin.String>? @JvmName("NullableComparableTest_comparableString") get() = this["comparableString"] as kotlin.Comparable<kotlin.String>?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.int: DataColumn<Int> @JvmName("ComparableTest_int") get() = this["int"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.int: Int @JvmName("ComparableTest_int") get() = this["int"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.int: DataColumn<Int?> @JvmName("NullableComparableTest_int") get() = this["int"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.int: Int? @JvmName("NullableComparableTest_int") get() = this["int"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.string: DataColumn<String> @JvmName("ComparableTest_string") get() = this["string"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest>.string: String @JvmName("ComparableTest_string") get() = this["string"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.string: DataColumn<String?> @JvmName("NullableComparableTest_string") get() = this["string"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.testSets.person.DataFrameTests.ComparableTest?>.string: String? @JvmName("NullableComparableTest_string") get() = this["string"] as String?
