@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

public val <V : kotlin.Any?> ColumnsScope<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>>.`value`: DataColumn<V> @JvmName("NameValuePair_value") get() = this["value"] as DataColumn<V>
public val <V : kotlin.Any?> DataRow<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>>.`value`: V @JvmName("NameValuePair_value") get() = this["value"] as V
public val <V : kotlin.Any?> ColumnsScope<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>?>.`value`: DataColumn<V?> @JvmName("NullableNameValuePair_value") get() = this["value"] as DataColumn<V?>
public val <V : kotlin.Any?> DataRow<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>?>.`value`: V? @JvmName("NullableNameValuePair_value") get() = this["value"] as V?
public val <V : kotlin.Any?> ColumnsScope<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>>.name: DataColumn<String> @JvmName("NameValuePair_name") get() = this["name"] as DataColumn<String>
public val <V : kotlin.Any?> DataRow<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>>.name: String @JvmName("NameValuePair_name") get() = this["name"] as String
public val <V : kotlin.Any?> ColumnsScope<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>?>.name: DataColumn<String?> @JvmName("NullableNameValuePair_name") get() = this["name"] as DataColumn<String?>
public val <V : kotlin.Any?> DataRow<org.jetbrains.kotlinx.dataframe.api.NameValuePair<V>?>.name: String? @JvmName("NullableNameValuePair_name") get() = this["name"] as String?
