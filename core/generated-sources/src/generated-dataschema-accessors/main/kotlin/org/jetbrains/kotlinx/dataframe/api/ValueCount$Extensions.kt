@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ValueCount>.count: DataColumn<Int> @JvmName("ValueCount_count") get() = this["count"] as DataColumn<Int>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ValueCount>.count: Int @JvmName("ValueCount_count") get() = this["count"] as Int
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ValueCount?>.count: DataColumn<Int?> @JvmName("NullableValueCount_count") get() = this["count"] as DataColumn<Int?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ValueCount?>.count: Int? @JvmName("NullableValueCount_count") get() = this["count"] as Int?
