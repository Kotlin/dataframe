@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>.id: DataColumn<String> @JvmName("Item_id") get() = this["id"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>.id: String @JvmName("Item_id") get() = this["id"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>.id: DataColumn<String?> @JvmName("NullableItem_id") get() = this["id"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>.id: String? @JvmName("NullableItem_id") get() = this["id"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>.kind: DataColumn<String> @JvmName("Item_kind") get() = this["kind"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>.kind: String @JvmName("Item_kind") get() = this["kind"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>.kind: DataColumn<String?> @JvmName("NullableItem_kind") get() = this["kind"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>.kind: String? @JvmName("NullableItem_kind") get() = this["kind"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>.snippet: ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet> @JvmName("Item_snippet") get() = this["snippet"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item>.snippet: DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet> @JvmName("Item_snippet") get() = this["snippet"] as DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>.snippet: ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?> @JvmName("NullableItem_snippet") get() = this["snippet"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Item?>.snippet: DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?> @JvmName("NullableItem_snippet") get() = this["snippet"] as DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>
