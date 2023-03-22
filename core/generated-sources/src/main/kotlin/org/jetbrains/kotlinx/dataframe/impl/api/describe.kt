package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnDescription
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asComparable
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.isComparable
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.maxOrNull
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.medianOrNull
import org.jetbrains.kotlinx.dataframe.api.minOrNull
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.jvm.jvmErasure

internal fun describeImpl(cols: List<AnyCol>): DataFrame<ColumnDescription> {
    fun List<AnyCol>.collectAll(dfs: Boolean): List<AnyCol> = flatMap { col ->
        when (col.kind) {
            ColumnKind.Frame -> col.asAnyFrameColumn().concat().columns().map {
                it.addPath(col.path() + it.name)
            }.collectAll(true)
            ColumnKind.Group -> if (dfs) col.asColumnGroup().columns().map { it.addPath(col.path() + it.name) }.collectAll(true) else listOf(col)
            ColumnKind.Value -> listOf(col)
        }
    }

    val all = cols.collectAll(false)

    val hasNumeric = all.any { it.isNumber() }
    val hasComparable = all.any { it.isComparable() }
    val hasLongPaths = all.any { it.path().size > 1 }
    var df = all.toDataFrame {
        ColumnDescription::name from { it.name() }
        if (hasLongPaths) {
            ColumnDescription::path from { it.path() }
        }
        ColumnDescription::type from { it.type.jvmErasure.simpleName }
        ColumnDescription::count from { it.size }
        ColumnDescription::unique from { it.countDistinct() }
        ColumnDescription::nulls from { it.values.count { it == null } }
        ColumnDescription::top from inferType { it.values.filterNotNull().groupBy { it }.maxByOrNull { it.value.size }?.key }
        if (hasNumeric) {
            ColumnDescription::mean from { if (it.isNumber()) it.asNumbers().mean() else null }
            ColumnDescription::std from { if (it.isNumber()) it.asNumbers().std() else null }
        }
        if (hasComparable) {
            ColumnDescription::min from inferType { if (it.isComparable()) it.asComparable().minOrNull() else null }
            ColumnDescription::median from inferType { if (it.isComparable()) it.asComparable().medianOrNull() else null }
            ColumnDescription::max from inferType { if (it.isComparable()) it.asComparable().maxOrNull() else null }
        }
    }
    df = df.add(ColumnDescription::freq) {
        val top = it[ColumnDescription::top]
        val data = all[index]
        data.values.count { it == top }
    }.move(ColumnDescription::freq).after(ColumnDescription::top)

    return df.cast()
}
