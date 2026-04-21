@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType>.col: DataColumn<String> @JvmName("_DataFrameType_col") get() = this["col"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType>.col: String @JvmName("_DataFrameType_col") get() = this["col"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType?>.col: DataColumn<String?> @JvmName("Nullable_DataFrameType_col") get() = this["col"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType?>.col: String? @JvmName("Nullable_DataFrameType_col") get() = this["col"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType>.leaf: ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1> @JvmName("_DataFrameType_leaf") get() = this["leaf"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType>.leaf: DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1> @JvmName("_DataFrameType_leaf") get() = this["leaf"] as DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType?>.leaf: ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?> @JvmName("Nullable_DataFrameType_leaf") get() = this["leaf"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType?>.leaf: DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?> @JvmName("Nullable_DataFrameType_leaf") get() = this["leaf"] as DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test5._DataFrameType1?>
