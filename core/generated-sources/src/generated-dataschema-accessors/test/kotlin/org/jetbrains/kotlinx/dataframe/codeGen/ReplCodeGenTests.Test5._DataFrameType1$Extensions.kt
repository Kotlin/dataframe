@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1>.a: DataColumn<Int> @JvmName("_DataFrameType1_a") get() = this["a"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1>.a: Int @JvmName("_DataFrameType1_a") get() = this["a"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?>.a: DataColumn<Int?> @JvmName("Nullable_DataFrameType1_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?>.a: Int? @JvmName("Nullable_DataFrameType1_a") get() = this["a"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1>.b: DataColumn<Int> @JvmName("_DataFrameType1_b") get() = this["b"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1>.b: Int @JvmName("_DataFrameType1_b") get() = this["b"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?>.b: DataColumn<Int?> @JvmName("Nullable_DataFrameType1_b") get() = this["b"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?>.b: Int? @JvmName("Nullable_DataFrameType1_b") get() = this["b"] as Int?
