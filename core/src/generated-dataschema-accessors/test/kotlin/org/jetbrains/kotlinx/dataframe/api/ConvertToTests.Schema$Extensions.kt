@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Schema>.a: DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A> @JvmName("Schema_a") get() = this["a"] as DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Schema>.a: org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A @JvmName("Schema_a") get() = this["a"] as org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Schema?>.a: DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A?> @JvmName("NullableSchema_a") get() = this["a"] as DataColumn<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Schema?>.a: org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A? @JvmName("NullableSchema_a") get() = this["a"] as org.jetbrains.kotlinx.dataframe.api.ConvertToTests.A?
