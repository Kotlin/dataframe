@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.C>.x: DataColumn<kotlin.collections.List<kotlin.Int>> @JvmName("C_x") get() = this["x"] as DataColumn<kotlin.collections.List<kotlin.Int>>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.C>.x: kotlin.collections.List<kotlin.Int> @JvmName("C_x") get() = this["x"] as kotlin.collections.List<kotlin.Int>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.C?>.x: DataColumn<kotlin.collections.List<kotlin.Int>?> @JvmName("NullableC_x") get() = this["x"] as DataColumn<kotlin.collections.List<kotlin.Int>?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.C?>.x: kotlin.collections.List<kotlin.Int>? @JvmName("NullableC_x") get() = this["x"] as kotlin.collections.List<kotlin.Int>?
