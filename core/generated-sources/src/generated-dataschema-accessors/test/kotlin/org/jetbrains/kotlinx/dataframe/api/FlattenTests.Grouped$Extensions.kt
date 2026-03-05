@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.Grouped>.d: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow> @JvmName("Grouped_d") get() = this["d"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.Grouped>.d: DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow> @JvmName("Grouped_d") get() = this["d"] as DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.Grouped?>.d: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?> @JvmName("NullableGrouped_d") get() = this["d"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.Grouped?>.d: DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?> @JvmName("NullableGrouped_d") get() = this["d"] as DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>
