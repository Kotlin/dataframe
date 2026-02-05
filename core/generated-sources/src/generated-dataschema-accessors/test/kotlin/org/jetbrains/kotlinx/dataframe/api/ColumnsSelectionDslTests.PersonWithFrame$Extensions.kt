@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.age: DataColumn<Int> @JvmName("PersonWithFrame_age") get() = this["age"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.age: Int @JvmName("PersonWithFrame_age") get() = this["age"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.age: DataColumn<Int?> @JvmName("NullablePersonWithFrame_age") get() = this["age"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.age: Int? @JvmName("NullablePersonWithFrame_age") get() = this["age"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.city: DataColumn<String?> @JvmName("PersonWithFrame_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.city: String? @JvmName("PersonWithFrame_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.city: DataColumn<String?> @JvmName("NullablePersonWithFrame_city") get() = this["city"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.city: String? @JvmName("NullablePersonWithFrame_city") get() = this["city"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.frameCol: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person>> @JvmName("PersonWithFrame_frameCol") get() = this["frameCol"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.frameCol: DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person> @JvmName("PersonWithFrame_frameCol") get() = this["frameCol"] as DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.frameCol: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person?>> @JvmName("NullablePersonWithFrame_frameCol") get() = this["frameCol"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person?>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.frameCol: DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person?> @JvmName("NullablePersonWithFrame_frameCol") get() = this["frameCol"] as DataFrame<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.isHappy: DataColumn<Boolean> @JvmName("PersonWithFrame_isHappy") get() = this["isHappy"] as DataColumn<Boolean>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.isHappy: Boolean @JvmName("PersonWithFrame_isHappy") get() = this["isHappy"] as Boolean
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.isHappy: DataColumn<Boolean?> @JvmName("NullablePersonWithFrame_isHappy") get() = this["isHappy"] as DataColumn<Boolean?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.isHappy: Boolean? @JvmName("NullablePersonWithFrame_isHappy") get() = this["isHappy"] as Boolean?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.name: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name> @JvmName("PersonWithFrame_name") get() = this["name"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.name: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name> @JvmName("PersonWithFrame_name") get() = this["name"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.name: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?> @JvmName("NullablePersonWithFrame_name") get() = this["name"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.name: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?> @JvmName("NullablePersonWithFrame_name") get() = this["name"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Name?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.weight: DataColumn<Int?> @JvmName("PersonWithFrame_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame>.weight: Int? @JvmName("PersonWithFrame_weight") get() = this["weight"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.weight: DataColumn<Int?> @JvmName("NullablePersonWithFrame_weight") get() = this["weight"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslTests.PersonWithFrame?>.weight: Int? @JvmName("NullablePersonWithFrame_weight") get() = this["weight"] as Int?
