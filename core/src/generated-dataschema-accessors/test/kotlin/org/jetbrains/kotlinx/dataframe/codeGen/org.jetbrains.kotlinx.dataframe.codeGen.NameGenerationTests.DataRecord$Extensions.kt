@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.codeGen
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord>.`first column`: DataColumn<Int> @JvmName("DataRecord_first column") get() = this["first column"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord>.`first column`: Int @JvmName("DataRecord_first column") get() = this["first column"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord?>.`first column`: DataColumn<Int?> @JvmName("NullableDataRecord_first column") get() = this["first column"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord?>.`first column`: Int? @JvmName("NullableDataRecord_first column") get() = this["first column"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord>.`second column`: DataColumn<Int> @JvmName("DataRecord_second column") get() = this["second column"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord>.`second column`: Int @JvmName("DataRecord_second column") get() = this["second column"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord?>.`second column`: DataColumn<Int?> @JvmName("NullableDataRecord_second column") get() = this["second column"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.codeGen.NameGenerationTests.DataRecord?>.`second column`: Int? @JvmName("NullableDataRecord_second column") get() = this["second column"] as Int?
