package org.jetbrains.kotlinx.dataframe.core.format

import org.jetbrains.kotlinx.dataframe.core.Column
import org.jetbrains.kotlinx.dataframe.core.ColumnGroup
import org.jetbrains.kotlinx.dataframe.core.DataFrame
import org.jetbrains.kotlinx.dataframe.core.DataFrameBuilder

class Format internal constructor(
    private val column: Column,
    private val condition: (Any?) -> Boolean,
    private val style: (Any?) -> Style
) {

    internal fun apply(df: DataFrame): DataFrame {
        val newDf = DataFrameBuilder(df)
        for (col in df.columns) {
            if (col is ColumnGroup) {
                val newCol = col.map { column ->
                    if (column.name == column.name) {
                        column
                    } else {
                        Column(column.name, column.values.map { value ->
                            if (condition(value)) {
                                style(value)(value)
                            } else {
                                value
                            }
                        })
                    }
                }
                newDf.add(newCol)
            } else {
                newDf.add(col)
            }
        }
        return newDf.build()
    }
}
