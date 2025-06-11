@file:OptIn(ExperimentalTypeInference::class)

package org.jetbrains.kotlinx.dataframe.examples.multik

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ValueProperty
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toColumnGroup
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.complex.Complex
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
import org.jetbrains.kotlinx.multik.ndarray.operations.toListD2
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

// region 1D

/** Converts a one-dimensional array ([D1Array]) to a [DataColumn] with optional [name]. */
inline fun <reified N> D1Array<N>.convertToColumn(name: String = ""): DataColumn<N> = column(toList()) named name

/**
 * Converts a one-dimensional array ([D1Array]) of type [N] into a DataFrame.
 * The resulting DataFrame contains a single column named "value", where each element of the array becomes a row in the DataFrame.
 *
 * @return a DataFrame where each element of the source array is represented as a row in a column named "value" under the schema [ValueProperty].
 */
@JvmName("convert1dArrayToDataFrame")
inline fun <reified N> D1Array<N>.convertToDataFrame(): DataFrame<ValueProperty<N>> =
    dataFrameOf(ValueProperty<*>::value.name to column(toList()))
        .cast()

/** Converts a [DataColumn] to a one-dimensional array ([D1Array]). */
@JvmName("convertNumberColumnToMultik")
inline fun <reified N> DataColumn<N>.convertToMultik(): D1Array<N> where N : Number, N : Comparable<N> =
    mk.ndarray(toList())

/** Converts a [DataColumn] to a one-dimensional array ([D1Array]). */
@JvmName("convertComplexColumnToMultik")
inline fun <reified N : Complex> DataColumn<N>.convertToMultik(): D1Array<N> = mk.ndarray(toList())

/** Converts a [DataColumn] selected by [column] to a one-dimensional array ([D1Array]). */
@JvmName("convertNumberColumnFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N> DataFrame<T>.convertToMultik(
    crossinline column: ColumnSelector<T, N>,
): D1Array<N>
    where N : Number, N : Comparable<N> = getColumn { column(it) }.convertToMultik()

/** Converts a [DataColumn] selected by [column] to a one-dimensional array ([D1Array]). */
@JvmName("convertComplexColumnFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N : Complex> DataFrame<T>.convertToMultik(crossinline column: ColumnSelector<T, N>): D1Array<N> =
    getColumn { column(it) }.convertToMultik()

// endregion

// region 2D

/**
 * Converts a two-dimensional array ([D2Array]) to a DataFrame.
 * It will contain `shape[0]` rows and `shape[1]` columns.
 *
 * Column names can be specified using the [columnNameGenerator] lambda.
 *
 * The conversion enforces that `multikArray[x][y] == dataframe[x][y]`
 */
@JvmName("convert2dArrayToDataFrame")
inline fun <reified N> D2Array<N>.convertToDataFrame(columnNameGenerator: (Int) -> String = { "col$it" }): AnyFrame =
    List(shape[1]) { i ->
        this[0..<shape[0], i] // get all cells of column i
            .toList()
            .toColumn(columnNameGenerator(i))
    }.toDataFrame()

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]).
 * You'll need to specify which columns to convert using the [columns] selector.
 *
 * All column need to be of the same type. If no column are supplied, the function
 * will only succeed if all columns are of the same type.
 *
 * @see convertToMultikOf
 */
@JvmName("convertNumberColumnsFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N> DataFrame<T>.convertToMultik(
    crossinline columns: ColumnsSelector<T, N>,
): D2Array<N>
    where N : Number, N : Comparable<N> = getColumns { columns(it) }.convertToMultik()

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]).
 * You'll need to specify which columns to convert using the [columns] selector.
 *
 * All column need to be of the same type. If no column are supplied, the function
 * will only succeed if all columns are of the same type.
 *
 * @see convertToMultikOf
 */
@JvmName("convertComplexColumnsFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N : Complex> DataFrame<T>.convertToMultik(
    crossinline columns: ColumnsSelector<T, N>,
): D2Array<N> = getColumns { columns(it) }.convertToMultik()

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]).
 * You'll need to specify which columns to convert using the `columns` selector.
 *
 * All column need to be of the same type. If no column are supplied, the function
 * will only succeed if all columns are of the same type.
 *
 * @see convertToMultikOf
 */
@JvmName("convertToMultikGuess")
fun AnyFrame.convertToMultik(): D2Array<*> {
    val columnTypes = columnTypes().distinct()
    val type = columnTypes.singleOrNull() ?: error("found column types: $columnTypes")
    return when {
        type == typeOf<Complex>() -> convertToMultik { colsOf<Complex>() }
        type.isSubtypeOf(typeOf<Byte>()) -> convertToMultik { colsOf<Byte>() }
        type.isSubtypeOf(typeOf<Short>()) -> convertToMultik { colsOf<Short>() }
        type.isSubtypeOf(typeOf<Int>()) -> convertToMultik { colsOf<Int>() }
        type.isSubtypeOf(typeOf<Long>()) -> convertToMultik { colsOf<Long>() }
        type.isSubtypeOf(typeOf<Float>()) -> convertToMultik { colsOf<Float>() }
        type.isSubtypeOf(typeOf<Double>()) -> convertToMultik { colsOf<Double>() }
        else -> error("found column types: $columnTypes")
    }
}

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]) by taking all
 * columns of type [N].
 *
 * @see convertToMultik
 */
@JvmName("convertToMultikOfComplex")
@Suppress("LocalVariableName")
inline fun <reified N : Complex> AnyFrame.convertToMultikOf(
    // unused param to avoid overload resolution ambiguity
    _klass: KClass<Complex> = Complex::class,
): D2Array<N> =
    convertToMultik { colsOf<N>() }

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]) by taking all
 * columns of type [N].
 *
 * @see convertToMultik
 */
@JvmName("convertToMultikOfNumber")
@Suppress("LocalVariableName")
inline fun <reified N> AnyFrame.convertToMultikOf(
    // unused param to avoid overload resolution ambiguity
    _klass: KClass<Number> = Number::class,
): D2Array<N> where N : Number, N : Comparable<N> = convertToMultik { colsOf<N>() }

@JvmName("convertNumberColumnsToMultik")
inline fun <reified N> List<DataColumn<N>>.convertToMultik(): D2Array<N> where N : Number, N : Comparable<N> =
    mk.ndarray(toDataFrame().map { it.values() as List<N> })

@JvmName("convertComplexColumnsToMultik")
inline fun <reified N : Complex> List<DataColumn<N>>.convertToMultik(): D2Array<N> =
    mk.ndarray(toDataFrame().map { it.values() as List<N> })

// endregion

// region higher dimensions

/**
 * Converts a three-dimensional array ([D3Array]) to a DataFrame.
 * It will contain `shape[0]` rows and `shape[1]` columns containing lists of size `shape[2]`.
 *
 * Column names can be specified using the [columnNameGenerator] lambda.
 *
 * The conversion enforces that `multikArray[x][y][z] == dataframe[x][y][z]`
 */
inline fun <reified N> D3Array<N>.convertToDataFrameWithLists(
    columnNameGenerator: (Int) -> String = { "col$it" },
): AnyFrame =
    List(shape[1]) { y ->
        this[0..<shape[0], y, 0..<shape[2]] // get all cells of column y, each is a 2d array of size shape[0] x shape[2]
            .toListD2() // get a shape[0]-sized list/column filled with lists of size shape[2]
            .toColumn(columnNameGenerator(y))
    }.toDataFrame()

/**
 * Converts a three-dimensional array ([D3Array]) to a DataFrame.
 * It will contain `shape[0]` rows and `shape[1]` column groups containing `shape[2]` columns each.
 *
 * Column names can be specified using the [columnNameGenerator] lambda.
 *
 * The conversion enforces that `multikArray[x][y][z] == dataframe[x][y][z]`
 */
@JvmName("convert3dArrayToDataFrame")
inline fun <reified N> D3Array<N>.convertToDataFrame(columnNameGenerator: (Int) -> String = { "col$it" }): AnyFrame =
    List(shape[1]) { y ->
        this[0..<shape[0], y, 0..<shape[2]] // get all cells of column i, each is a 2d array of size shape[0] x shape[2]
            .transpose(1, 0) // flip, so we get shape[2] x shape[0]
            .toListD2() // get a shape[2]-sized list filled with lists of size shape[0]
            .mapIndexed { z, list ->
                list.toColumn(columnNameGenerator(z))
            } // we get shape[2] columns inside each column group
            .toColumnGroup(columnNameGenerator(y))
    }.toDataFrame()

/**
 * Exploratory recursive function to convert a [MultiArray] of any number of dimensions
 * to a `List<List<...>>` of the same number of dimensions.
 */
fun <T> MultiArray<T, *>.toListDn(): List<*> {
    // Recursive helper function to handle traversal across dimensions
    fun toListRecursive(indices: IntArray): List<*> {
        // If we are at the last dimension (1D case)
        if (indices.size == shape.lastIndex) {
            return List(shape[indices.size]) { i ->
                this[intArrayOf(*indices, i)] // Collect values for this dimension
            }
        }

        // For higher dimensions, recursively process smaller dimensions
        return List(shape[indices.size]) { i ->
            toListRecursive(indices + i) // Add `i` to the current index array
        }
    }
    return toListRecursive(intArrayOf())
}

/**
 * Converts a multidimensional array ([NDArray]) to a DataFrame.
 * Inspired by [toListDn].
 *
 * For a single-dimensional array, it will call [D1Array.convertToDataFrame].
 *
 * Column names can be specified using the [columnNameGenerator] lambda.
 *
 * The conversion enforces that `multikArray[a][b][c][d]... == dataframe[a][b][c][d]...`
 */
inline fun <reified N> NDArray<N, *>.convertToDataFrameNestedGroups(
    noinline columnNameGenerator: (Int) -> String = { "col$it" },
): AnyFrame {
    if (shape.size == 1) return (this as D1Array<N>).convertToDataFrame()

    // push the first dimension to the end, because this represents the rows in DataFrame,
    // and they are accessed by []'s first
    return transpose(*(1..<dim.d).toList().toIntArray(), 0)
        .convertToDataFrameNestedGroupsRecursive(
            indices = intArrayOf(),
            type = typeOf<N>(),
            columnNameGenerator = columnNameGenerator,
        ).let { dataFrameOf((it as ColumnGroup<*>).columns()) }
}

// Recursive helper function to handle traversal across dimensions
@PublishedApi
internal fun NDArray<*, *>.convertToDataFrameNestedGroupsRecursive(
    indices: IntArray,
    type: KType,
    columnNameGenerator: (Int) -> String = { "col$it" },
): BaseColumn<*> {
    // If we are at the last dimension (1D case)
    if (indices.size == shape.lastIndex) {
        return List(shape[indices.size]) { i ->
            this[intArrayOf(*indices, i)] // Collect values for this dimension
        }.let {
            DataColumn.createByType(name = "", values = it, type = type)
        }
    }

    // For higher dimensions, recursively process smaller dimensions
    return List(shape[indices.size]) { i ->
        convertToDataFrameNestedGroupsRecursive(
            indices = indices + i, // Add `i` to the current index array
            type = type,
            columnNameGenerator = columnNameGenerator,
        ).rename(columnNameGenerator(i))
    }.toColumnGroup("")
}

// endregion
