package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnDescriptionSchema
import org.jetbrains.kotlinx.dataframe.api.GeneralColumnDescriptionSchema
import org.jetbrains.kotlinx.dataframe.api.NumberColumnDescriptionSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.isComparable
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ndistinct
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.typed

internal fun describeImpl(cols: List<AnyCol>): DataFrame<ColumnDescriptionSchema> {
    val hasNumeric = cols.any { it.isNumber() }
    val hasCategorical = cols.any { !it.isNumber() }
    var df = cols.toDataFrame {
        GeneralColumnDescriptionSchema::column { name }
        GeneralColumnDescriptionSchema::count { size }
        GeneralColumnDescriptionSchema::nulls { values.count { it == null } }

        if (hasCategorical) {
            ColumnDescriptionSchema::unique { ndistinct }
            ColumnDescriptionSchema::top { values.groupBy { it }.maxByOrNull { it.value.size }?.key }
            ColumnDescriptionSchema::type { type.toString() }
        }
        if (hasNumeric) {
            NumberColumnDescriptionSchema::mean { if (it.isNumber()) (it as DataColumn<Number?>).mean() else null }
            NumberColumnDescriptionSchema::min { if (it.isComparable()) (it as DataColumn<Comparable<Any?>>).min() else null }
            NumberColumnDescriptionSchema::max { if (it.isComparable()) (it as DataColumn<Comparable<Any?>>).max() else null }
        }
    }
    if (hasCategorical) df = df.add(ColumnDescriptionSchema::freq) {
        val top = it[ColumnDescriptionSchema::top]
        val data = cols[index]
        data.values.count { it == top }
    }.move(ColumnDescriptionSchema::freq).after(ColumnDescriptionSchema::top)

    return df.typed()
}
