@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.DataSchemaWithAnyFrame>.dfs: DataColumn<org.jetbrains.kotlinx.dataframe.AnyFrame?> @JvmName("DataSchemaWithAnyFrame_dfs") get() = this["dfs"] as DataColumn<org.jetbrains.kotlinx.dataframe.AnyFrame?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.DataSchemaWithAnyFrame>.dfs: org.jetbrains.kotlinx.dataframe.AnyFrame? @JvmName("DataSchemaWithAnyFrame_dfs") get() = this["dfs"] as org.jetbrains.kotlinx.dataframe.AnyFrame?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.DataSchemaWithAnyFrame?>.dfs: DataColumn<org.jetbrains.kotlinx.dataframe.AnyFrame?> @JvmName("NullableDataSchemaWithAnyFrame_dfs") get() = this["dfs"] as DataColumn<org.jetbrains.kotlinx.dataframe.AnyFrame?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.DataSchemaWithAnyFrame?>.dfs: org.jetbrains.kotlinx.dataframe.AnyFrame? @JvmName("NullableDataSchemaWithAnyFrame_dfs") get() = this["dfs"] as org.jetbrains.kotlinx.dataframe.AnyFrame?
