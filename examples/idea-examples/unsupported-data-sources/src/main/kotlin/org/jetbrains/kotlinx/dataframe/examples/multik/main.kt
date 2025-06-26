package org.jetbrains.kotlinx.dataframe.examples.multik

import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanFor
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.value
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.rand
import org.jetbrains.kotlinx.multik.ndarray.data.get

/**
 * Let's explore some ways we can combine Multik with Kotlin DataFrame.
 *
 * We will use compatibilityLayer.kt for the conversions.
 * Take a look at that file for the implementation details!
 */
fun main() {
    oneDimension()
    twoDimensions()
    higherDimensions()
}

fun oneDimension() {
    // we can convert a 1D ndarray to a column of a DataFrame:
    val mk1 = mk.rand<Double>(50)
    val col1 by mk1.convertToColumn()
    println(col1)

    // or straight to a DataFrame. It will become the `value` column.
    val df1 = mk1.convertToDataFrame()
    println(df1)

    // this allows us to perform any DF operation:
    println(df1.mean { value })
    df1.describe().print(borders = true)

    // we can convert back to Multik:
    val mk2 = df1.convertToMultik { value }
    // or
    df1.value.convertToMultik()

    println(mk2)
}

fun twoDimensions() {
    // we can also convert a 2D ndarray to a DataFrame
    // This conversion will create columns like "col0", "col1", etc.
    // (careful, when the number of columns is too large, this can cause problems)
    // but will allow for similar access like in multik
    // aka: `multikArray[x][y] == dataframe[x][y]`
    val mk1 = mk.rand<Int>(5, 10)
    println(mk1)
    val df = mk1.convertToDataFrame()
    df.print()

    // this allows us to perform any DF operation:
    val means = df.meanFor { ("col0".."col9").cast<Int>() }
    means.print()

    // we can convert back to Multik in multiple ways.
    // Multik can only store one type of data, so we need to specify the type or select
    // only the columns we want:
    val mk2 = df.convertToMultik { colsOf<Int>() }
    // or
    df.convertToMultikOf<Int>()
    // or if all columns are of the same type:
    df.convertToMultik()

    println(mk2)
}

fun higherDimensions() {
    // Multik can store higher dimensions as well
    // however; to convert this to a DataFrame, we need to specify how to do a particular conversion
    // for instance, for 3d, we could store a list in each cell of the DF to represent the extra dimension:
    val mk1 = mk.rand<Int>(5, 4, 3)

    println(mk1)

    val df1 = mk1.convertToDataFrameWithLists()
    df1.print()

    // Alternatively, this could be solved using column groups.
    // This subdivides each column into more columns, while ensuring `multikArray[x][y][z] == dataframe[x][y][z]`
    val df2 = mk1.convertToDataFrame()
    df2.print()

    // For even higher dimensions, we can keep adding more column groups
    val mk2 = mk.rand<Int>(5, 4, 3, 2)
    val df3 = mk2.convertToDataFrameNestedGroups()
    df3.print()

    // ...or use nested DataFrames (in FrameColumns)
    // (for instance, a 4D matrix could be stored in a 2D DataFrame where each cell is another DataFrame)
    // but, we'll leave that as an exercise for the reader :)
}
