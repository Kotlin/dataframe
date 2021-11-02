package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnDescriptionSchema
import org.jetbrains.kotlinx.dataframe.api.GeneralColumnDescriptionSchema
import org.jetbrains.kotlinx.dataframe.api.NumberColumnDescriptionSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.createDataFrame
import org.jetbrains.kotlinx.dataframe.api.isComparable
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.move
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.ndistinct
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.type

internal fun describeImpl(cols: List<AnyCol>): DataFrame<ColumnDescriptionSchema> {
    val hasNumeric = cols.any { it.isNumber() }
    val hasCategorical = cols.any { !it.isNumber() }
    var df = cols.createDataFrame {
        GeneralColumnDescriptionSchema::column from { it.name }
        GeneralColumnDescriptionSchema::count from { it.size }
        GeneralColumnDescriptionSchema::nulls from { it.values.count { it == null } }

        if (hasCategorical) {
            ColumnDescriptionSchema::unique from { it.ndistinct }
            ColumnDescriptionSchema::top from { it.values.groupBy { it }.maxByOrNull { it.value.size }?.key }
            ColumnDescriptionSchema::type from { it.type.toString() }
        }
        if (hasNumeric) {
            NumberColumnDescriptionSchema::mean from { if (it.isNumber()) (it as DataColumn<Number?>).mean() else null }
            NumberColumnDescriptionSchema::min from { if (it.isComparable()) (it as DataColumn<Comparable<Any?>>).min() else null }
            NumberColumnDescriptionSchema::max from { if (it.isComparable()) (it as DataColumn<Comparable<Any?>>).max() else null }
        }
    }
    if (hasCategorical) df = df.add(ColumnDescriptionSchema::freq) {
        val top = it[ColumnDescriptionSchema::top]
        val data = cols[index]
        data.values.count { it == top }
    }.move(ColumnDescriptionSchema::freq).after(ColumnDescriptionSchema::top)

    return df.cast()
}
