@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

public val <T : kotlin.Any?> ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ValueProperty<T>>.`value`: DataColumn<T> @JvmName("ValueProperty_value") get() = this["value"] as DataColumn<T>
public val <T : kotlin.Any?> DataRow<org.jetbrains.kotlinx.dataframe.api.ValueProperty<T>>.`value`: T @JvmName("ValueProperty_value") get() = this["value"] as T
public val <T : kotlin.Any?> ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ValueProperty<T>?>.`value`: DataColumn<T?> @JvmName("NullableValueProperty_value") get() = this["value"] as DataColumn<T?>
public val <T : kotlin.Any?> DataRow<org.jetbrains.kotlinx.dataframe.api.ValueProperty<T>?>.`value`: T? @JvmName("NullableValueProperty_value") get() = this["value"] as T?
