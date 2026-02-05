@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.SchemaA>.i: DataColumn<Int?> @JvmName("SchemaA_i") get() = this["i"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.SchemaA>.i: Int? @JvmName("SchemaA_i") get() = this["i"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.SchemaA?>.i: DataColumn<Int?> @JvmName("NullableSchemaA_i") get() = this["i"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.SchemaA?>.i: Int? @JvmName("NullableSchemaA_i") get() = this["i"] as Int?
