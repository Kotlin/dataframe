@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord>.items: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>> @JvmName("DataRecord_items") get() = this["items"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord>.items: DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item> @JvmName("DataRecord_items") get() = this["items"] as DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord?>.items: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>> @JvmName("NullableDataRecord_items") get() = this["items"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord?>.items: DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?> @JvmName("NullableDataRecord_items") get() = this["items"] as DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord>.kind: DataColumn<String> @JvmName("DataRecord_kind") get() = this["kind"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord>.kind: String @JvmName("DataRecord_kind") get() = this["kind"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord?>.kind: DataColumn<String?> @JvmName("NullableDataRecord_kind") get() = this["kind"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord?>.kind: String? @JvmName("NullableDataRecord_kind") get() = this["kind"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord>.pageInfo: ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo> @JvmName("DataRecord_pageInfo") get() = this["pageInfo"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord>.pageInfo: DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo> @JvmName("DataRecord_pageInfo") get() = this["pageInfo"] as DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord?>.pageInfo: ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?> @JvmName("NullableDataRecord_pageInfo") get() = this["pageInfo"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.DataRecord?>.pageInfo: DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?> @JvmName("NullableDataRecord_pageInfo") get() = this["pageInfo"] as DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>
