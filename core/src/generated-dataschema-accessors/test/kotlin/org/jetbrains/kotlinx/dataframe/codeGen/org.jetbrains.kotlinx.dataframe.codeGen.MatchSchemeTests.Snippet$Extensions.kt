@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>.info: DataColumn<String> @JvmName("Snippet_info") get() = this["info"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>.info: String @JvmName("Snippet_info") get() = this["info"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>.info: DataColumn<String?> @JvmName("NullableSnippet_info") get() = this["info"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>.info: String? @JvmName("NullableSnippet_info") get() = this["info"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>.position: DataColumn<Int> @JvmName("Snippet_position") get() = this["position"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>.position: Int @JvmName("Snippet_position") get() = this["position"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>.position: DataColumn<Int?> @JvmName("NullableSnippet_position") get() = this["position"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>.position: Int? @JvmName("NullableSnippet_position") get() = this["position"] as Int?
