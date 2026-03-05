@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>.resultsPerPage: DataColumn<Int> @JvmName("PageInfo_resultsPerPage") get() = this["resultsPerPage"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>.resultsPerPage: Int @JvmName("PageInfo_resultsPerPage") get() = this["resultsPerPage"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>.resultsPerPage: DataColumn<Int?> @JvmName("NullablePageInfo_resultsPerPage") get() = this["resultsPerPage"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>.resultsPerPage: Int? @JvmName("NullablePageInfo_resultsPerPage") get() = this["resultsPerPage"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>.snippets: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>> @JvmName("PageInfo_snippets") get() = this["snippets"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>.snippets: DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet> @JvmName("PageInfo_snippets") get() = this["snippets"] as DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>.snippets: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>> @JvmName("NullablePageInfo_snippets") get() = this["snippets"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>.snippets: DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?> @JvmName("NullablePageInfo_snippets") get() = this["snippets"] as DataFrame<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.Snippet?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>.totalResults: DataColumn<Int> @JvmName("PageInfo_totalResults") get() = this["totalResults"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo>.totalResults: Int @JvmName("PageInfo_totalResults") get() = this["totalResults"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>.totalResults: DataColumn<Int?> @JvmName("NullablePageInfo_totalResults") get() = this["totalResults"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.MatchSchemeTests.PageInfo?>.totalResults: Int? @JvmName("NullablePageInfo_totalResults") get() = this["totalResults"] as Int?
