@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SchemaWithNullableEnum>.a: DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum?> @JvmName("SchemaWithNullableEnum_a") get() = this["a"] as DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SchemaWithNullableEnum>.a: org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum? @JvmName("SchemaWithNullableEnum_a") get() = this["a"] as org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SchemaWithNullableEnum?>.a: DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum?> @JvmName("NullableSchemaWithNullableEnum_a") get() = this["a"] as DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SchemaWithNullableEnum?>.a: org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum? @JvmName("NullableSchemaWithNullableEnum_a") get() = this["a"] as org.jetbrains.kotlinx.dataframe.api.ConvertToTests.SimpleEnum?
