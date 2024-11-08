package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnDescription
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.after
import org.jetbrains.kotlinx.dataframe.api.any
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asComparable
import org.jetbrains.kotlinx.dataframe.api.asNumbers
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.isInterComparable
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.map
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
import org.jetbrains.kotlinx.dataframe.impl.isBigNumber
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.toBigDecimal
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.type

internal fun describeImpl(cols: List<AnyCol>): DataFrame<ColumnDescription> {
    fun List<AnyCol>.collectAll(atAnyDepth: Boolean): List<AnyCol> =
        flatMap { col ->
            when (col.kind) {
                ColumnKind.Frame ->
                    col.asAnyFrameColumn()
                        .concat()
                        .columns()
                        .map { it.addPath(col.path() + it.name) }
                        .collectAll(true)

                ColumnKind.Group ->
                    if (atAnyDepth) {
                        col.asColumnGroup()
                            .columns()
                            .map { it.addPath(col.path() + it.name) }
                            .collectAll(true)
                    } else {
                        listOf(col)
                    }

                ColumnKind.Value -> listOf(col)
            }
        }

    val allCols = cols.collectAll(false)

    val hasNumericCols = allCols.any { it.isNumber() }
    val hasInterComparableCols = allCols.any { it.isInterComparable() }
    val hasLongPaths = allCols.any { it.path().size > 1 }
    var df = allCols.toDataFrame {
        ColumnDescription::name from { it.name() }
        if (hasLongPaths) {
            ColumnDescription::path from { it.path() }
        }
        ColumnDescription::type from { renderType(it.type) }
        ColumnDescription::count from { it.size }
        ColumnDescription::unique from { it.countDistinct() }
        ColumnDescription::nulls from { it.values.count { it == null } }
        ColumnDescription::top from inferType {
            it.values.filterNotNull()
                .groupBy { it }.maxByOrNull { it.value.size }
                ?.key
        }
        if (hasNumericCols) {
            ColumnDescription::mean from { if (it.isNumber()) it.asNumbers().mean() else null }
            ColumnDescription::std from { if (it.isNumber()) it.asNumbers().std() else null }
        }
        if (hasInterComparableCols || hasNumericCols) {
            ColumnDescription::min from inferType {
                when {
                    it.isInterComparable() ->
                        it.asComparable().minOrNull()

                    // Found incomparable number types, convert all to Double or BigDecimal first
                    it.isNumber() ->
                        if (it.any { it?.isBigNumber() == true }) {
                            it.map { (it as Number?)?.toBigDecimal() }.minOrNull()
                        } else {
                            it.map { (it as Number?)?.toDouble() }.minOrNull()
                        }

                    else -> null
                }
            }
            ColumnDescription::median from inferType {
                when {
                    it.isInterComparable() ->
                        it.asComparable().medianOrNull()

                    // Found incomparable number types, convert all to Double or BigDecimal first
                    it.isNumber() ->
                        if (it.any { it?.isBigNumber() == true }) {
                            it.map { (it as Number?)?.toBigDecimal() }.medianOrNull()
                        } else {
                            it.map { (it as Number?)?.toDouble() }.medianOrNull()
                        }

                    else -> null
                }
            }
            ColumnDescription::max from inferType {
                when {
                    it.isInterComparable() -> it.asComparable().maxOrNull()

                    // Found incomparable number types, convert all to Double or BigDecimal first
                    it.isNumber() ->
                        if (it.any { it?.isBigNumber() == true }) {
                            it.map { (it as Number?)?.toBigDecimal() }.maxOrNull()
                        } else {
                            it.map { (it as Number?)?.toDouble() }.maxOrNull()
                        }

                    else -> null
                }
            }
        }
    }
    df = df.add(ColumnDescription::freq) {
        val top = it[ColumnDescription::top]
        val data = allCols[index]
        data.values.count { it == top }
    }.move(ColumnDescription::freq).after(ColumnDescription::top)

    return df.cast()
}
