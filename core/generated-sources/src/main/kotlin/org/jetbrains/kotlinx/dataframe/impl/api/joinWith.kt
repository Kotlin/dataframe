package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.JoinExpression
import org.jetbrains.kotlinx.dataframe.api.JoinType
import org.jetbrains.kotlinx.dataframe.api.JoinedDataRow
import org.jetbrains.kotlinx.dataframe.api.allowLeftNulls
import org.jetbrains.kotlinx.dataframe.api.allowRightNulls
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl

internal class JoinedDataRowImpl<A, B>(
    leftOwner: DataFrame<A>,
    val index: Int,
    rightOwner: DataFrame<B>,
    index1: Int,
) : DataRowImpl<A>(index, leftOwner),
    JoinedDataRow<A, B> {
    override val right: DataRow<B> = DataRowImpl(index1, rightOwner)
}

internal fun <A, B> DataFrame<A>.joinWithImpl(
    right: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    addNewColumns: Boolean,
    joinExpression: JoinExpression<A, B>,
): DataFrame<A> {
    val generator = ColumnNameGenerator(columnNames())
    if (addNewColumns) {
        right.columnNames().forEach { generator.addUnique(it) }
    }
    val rightColumnsCount = if (addNewColumns) right.columnsCount() else 0
    val outputData = List(columnsCount() + rightColumnsCount) { mutableListOf<Any?>() }
    val rightMatched = BooleanArray(right.count()) { false }
    for (l in indices()) {
        var leftMatched = false
        for (r in right.indices()) {
            val joined = JoinedDataRowImpl(this, l, right, r)
            val matched = joinExpression(joined, joined)
            if (matched && type == JoinType.Exclude) {
                leftMatched = true
                break
            }
            if (matched) {
                rightMatched[r] = true
                leftMatched = true
                val left = get(l).values()
                for (col in left.indices) {
                    outputData[col].add(left[col])
                }
                if (addNewColumns) {
                    val offset = left.size
                    val row = right.get(r).values()
                    for (col in row.indices) {
                        outputData[col + offset].add(row[col])
                    }
                }
            }
        }
        if (!leftMatched && type.allowRightNulls) {
            val left = get(l).values()
            for (col in left.indices) {
                outputData[col].add(left[col])
            }
            if (addNewColumns) {
                for (col in left.size..outputData.lastIndex) {
                    outputData[col].add(null)
                }
            }
        }
    }

    if (type.allowLeftNulls) {
        rightMatched.forEachIndexed { row, matched ->
            if (!matched) {
                repeat(columnsCount()) { col ->
                    outputData[col].add(null)
                }
                val offset = columnsCount()
                val rowData = right[row].values()
                for (col in rowData.indices) {
                    outputData[offset + col].add(rowData[col])
                }
            }
        }
    }

    val leftColumns = columns()
    val rightColumns = if (addNewColumns) right.columns() else emptyList()
    val df: DataFrame<*> = outputData.mapIndexed { index, values ->
        val srcColumn = if (index < leftColumns.size) {
            leftColumns[index]
        } else {
            rightColumns[index - leftColumns.size]
        }
        // let's optimize an easy case.
        // handling introduction of nulls into ColumnGroup and FrameColumn is not straightforward
        when (srcColumn.kind()) {
            ColumnKind.Value -> DataColumn.createByType(
                name = generator.names[index],
                values = values,
                type = srcColumn.type(),
                infer = Infer.Nulls,
            )

            else -> DataColumn.createByInference(generator.names[index], values)
        }
    }.toDataFrame()

    return df.cast()
}
