@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test4.B>.a: DataColumn<Int?> @JvmName("B_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test4.B>.a: Int? @JvmName("B_a") get() = this["a"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test4.B?>.a: DataColumn<Int?> @JvmName("NullableB_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test4.B?>.a: Int? @JvmName("NullableB_a") get() = this["a"] as Int?
