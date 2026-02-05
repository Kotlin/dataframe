@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.A>.x: DataColumn<kotlin.collections.List<*>> @JvmName("A_x") get() = this["x"] as DataColumn<kotlin.collections.List<*>>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.A>.x: kotlin.collections.List<*> @JvmName("A_x") get() = this["x"] as kotlin.collections.List<*>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.A?>.x: DataColumn<kotlin.collections.List<*>?> @JvmName("NullableA_x") get() = this["x"] as DataColumn<kotlin.collections.List<*>?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.ReplCodeGenTests.Test3.A?>.x: kotlin.collections.List<*>? @JvmName("NullableA_x") get() = this["x"] as kotlin.collections.List<*>?
