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
inline fun <reified N> D1Array<N>.convertToColumn(name: String = ""): DataColumn<N> {
    // we can simply convert the 1D array to a typed list and create a typed column from it
    // by using the reified type parameter, DataFrame needs to do no inference :)
    val values = this.toList()
    return column<N>(values) named name
}

/**
 * Converts a one-dimensional array ([D1Array]) of type [N] into a DataFrame.
 * The resulting DataFrame contains a single column named "value", where each element of the array becomes a row in the DataFrame.
 *
 * @return a DataFrame where each element of the source array is represented as a row in a column named "value" under the schema [ValueProperty].
 */
@JvmName("convert1dArrayToDataFrame")
inline fun <reified N> D1Array<N>.convertToDataFrame(): DataFrame<ValueProperty<N>> {
    // do the conversion like above, but name the column "value"...
    val column = this.convertToColumn(ValueProperty<*>::value.name)
    // ...so we can cast it to a ValueProperty DataFrame
    return dataFrameOf(column).cast<ValueProperty<N>>()
}

/** Converts a [DataColumn] to a one-dimensional array ([D1Array]). */
@JvmName("convertNumberColumnToMultik")
inline fun <reified N> DataColumn<N>.convertToMultik(): D1Array<N> where N : Number, N : Comparable<N> {
    // we can convert our column to a typed list again to convert it to a multik array
    val values = this.toList()
    return mk.ndarray(values)
}

/** Converts a [DataColumn] to a one-dimensional array ([D1Array]). */
@JvmName("convertComplexColumnToMultik")
inline fun <reified N : Complex> DataColumn<N>.convertToMultik(): D1Array<N> {
    // we can convert our column to a typed list again to convert it to a multik array
    val values = this.toList()
    return mk.ndarray(values)
}

/** Converts a [DataColumn] selected by [column] to a one-dimensional array ([D1Array]). */
@JvmName("convertNumberColumnFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N> DataFrame<T>.convertToMultik(
    crossinline column: ColumnSelector<T, N>,
): D1Array<N>
    where N : Number, N : Comparable<N> {
    // use the selector to get the column from this DataFrame and convert it
    val col = this.getColumn { column(it) }
    return col.convertToMultik()
}

/** Converts a [DataColumn] selected by [column] to a one-dimensional array ([D1Array]). */
@JvmName("convertComplexColumnFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N : Complex> DataFrame<T>.convertToMultik(crossinline column: ColumnSelector<T, N>): D1Array<N> {
    // use the selector to get the column from this DataFrame and convert it
    val col = this.getColumn { column(it) }
    return col.convertToMultik()
}

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
inline fun <reified N> D2Array<N>.convertToDataFrame(columnNameGenerator: (Int) -> String = { "col$it" }): AnyFrame {
    // Turning the 2D array into a list of typed columns first, no inference needed
    val columns: List<DataColumn<N>> = List(shape[1]) { i ->
        this[0..<shape[0], i] // get all cells of column i
            .toList()
            .toColumn<N>(name = columnNameGenerator(i))
    }
    // and make a DataFrame from it
    return columns.toDataFrame()
}

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]).
 * You'll need to specify which columns to convert using the [columns] selector.
 *
 * All columns need to be of the same type. If no columns are supplied, the function
 * will only succeed if all columns are of the same type.
 *
 * @see convertToMultikOf
 */
@JvmName("convertNumberColumnsFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N> DataFrame<T>.convertToMultik(
    crossinline columns: ColumnsSelector<T, N>,
): D2Array<N>
    where N : Number, N : Comparable<N> {
    // use the selector to get the columns from this DataFrame and convert them
    val cols = this.getColumns { columns(it) }
    return cols.convertToMultik()
}

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]).
 * You'll need to specify which columns to convert using the [columns] selector.
 *
 * All columns need to be of the same type. If no columns are supplied, the function
 * will only succeed if all columns are of the same type.
 *
 * @see convertToMultikOf
 */
@JvmName("convertComplexColumnsFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N : Complex> DataFrame<T>.convertToMultik(
    crossinline columns: ColumnsSelector<T, N>,
): D2Array<N> {
    // use the selector to get the columns from this DataFrame and convert them
    val cols = this.getColumns { columns(it) }
    return cols.convertToMultik()
}

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]).
 * You'll need to specify which columns to convert using the `columns` selector.
 *
 * All columns need to be of the same type. If no columns are supplied, the function
 * will only succeed if all columns in [this] are of the same type.
 *
 * @see convertToMultikOf
 */
@JvmName("convertToMultikGuess")
fun AnyFrame.convertToMultik(): D2Array<*> {
    val columnTypes = this.columnTypes().distinct()
    val type = columnTypes.singleOrNull() ?: error("found multiple column types: $columnTypes")
    return when {
        type == typeOf<Complex>() -> convertToMultik { colsOf<Complex>() }
        type.isSubtypeOf(typeOf<Byte>()) -> convertToMultik { colsOf<Byte>() }
        type.isSubtypeOf(typeOf<Short>()) -> convertToMultik { colsOf<Short>() }
        type.isSubtypeOf(typeOf<Int>()) -> convertToMultik { colsOf<Int>() }
        type.isSubtypeOf(typeOf<Long>()) -> convertToMultik { colsOf<Long>() }
        type.isSubtypeOf(typeOf<Float>()) -> convertToMultik { colsOf<Float>() }
        type.isSubtypeOf(typeOf<Double>()) -> convertToMultik { colsOf<Double>() }
        else -> error("found multiple column types: $columnTypes")
    }
}

/**
 * Converts a [DataFrame] to a two-dimensional array ([D2Array]) by taking all
 * columns of type [N].
 *
 * Allows you to write `df.convertToMultikOf<Complex>()`.
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
 * Allows you to write `df.convertToMultikOf<Int>()`.
 *
 * @see convertToMultik
 */
@JvmName("convertToMultikOfNumber")
@Suppress("LocalVariableName")
inline fun <reified N> AnyFrame.convertToMultikOf(
    // unused param to avoid overload resolution ambiguity
    _klass: KClass<Number> = Number::class,
): D2Array<N> where N : Number, N : Comparable<N> = convertToMultik { colsOf<N>() }

/**
 * Helper function to convert a list of same-typed [DataColumn]s to a two-dimensional array ([D2Array]).
 * We cannot enforce all columns have the same type if we require just a [DataFrame].
 */
@Suppress("UNCHECKED_CAST")
@JvmName("convertNumberColumnsToMultik")
inline fun <reified N> List<DataColumn<N>>.convertToMultik(): D2Array<N> where N : Number, N : Comparable<N> {
    // to get the list of columns as a list of rows, we need to convert them back to a dataframe first,
    // then we can get the values of each row
    val rows = this.toDataFrame().map { row -> row.values() as List<N> }
    return mk.ndarray(rows)
}

/**
 * Helper function to convert a list of same-typed [DataColumn]s to a two-dimensional array ([D2Array]).
 * We cannot enforce all columns have the same type if we require just a [DataFrame].
 */
@Suppress("UNCHECKED_CAST")
@JvmName("convertComplexColumnsToMultik")
inline fun <reified N : Complex> List<DataColumn<N>>.convertToMultik(): D2Array<N> {
    // to get the list of columns as a list of rows, we need to convert them back to a dataframe first,
    // then we can get the values of each row
    val rows = this.toDataFrame().map { row -> row.values() as List<N> }
    return mk.ndarray(rows)
}

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
): AnyFrame {
    val columns: List<DataColumn<List<N>>> = List(shape[1]) { y ->
        this[0..<shape[0], y, 0..<shape[2]] // get all cells of column y, each is a 2d array of size shape[0] x shape[2]
            .toListD2() // get a shape[0]-sized list/column filled with lists of size shape[2]
            .toColumn<List<N>>(name = columnNameGenerator(y))
    }
    return columns.toDataFrame()
}

/**
 * Converts a three-dimensional array ([D3Array]) to a DataFrame.
 * It will contain `shape[0]` rows and `shape[1]` column groups containing `shape[2]` columns each.
 *
 * Column names can be specified using the [columnNameGenerator] lambda.
 *
 * The conversion enforces that `multikArray[x][y][z] == dataframe[x][y][z]`
 */
@JvmName("convert3dArrayToDataFrame")
inline fun <reified N> D3Array<N>.convertToDataFrame(columnNameGenerator: (Int) -> String = { "col$it" }): AnyFrame {
    val columns: List<ColumnGroup<*>> = List(shape[1]) { y ->
        this[0..<shape[0], y, 0..<shape[2]] // get all cells of column i, each is a 2d array of size shape[0] x shape[2]
            .transpose(1, 0) // flip, so we get shape[2] x shape[0]
            .toListD2() // get a shape[2]-sized list filled with lists of size shape[0]
            .mapIndexed { z, list ->
                list.toColumn<N>(name = columnNameGenerator(z))
            } // we get shape[2] columns inside each column group
            .toColumnGroup(name = columnNameGenerator(y))
    }
    return columns.toDataFrame()
}

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
@Suppress("UNCHECKED_CAST")
inline fun <reified N> NDArray<N, *>.convertToDataFrameNestedGroups(
    noinline columnNameGenerator: (Int) -> String = { "col$it" },
): AnyFrame {
    if (shape.size == 1) return (this as D1Array<N>).convertToDataFrame()

    // push the first dimension to the end, because this represents the rows in DataFrame,
    // and they are accessed by []'s first
    return transpose(*(1..<dim.d).toList().toIntArray(), 0)
        .convertToDataFrameNestedGroupsRecursive(
            indices = intArrayOf(),
            type = typeOf<N>(), // cannot inline a recursive function, so pass the type explicitly
            columnNameGenerator = columnNameGenerator,
        ).let {
            // we could just cast this to a DataFrame<*>, because a ColumnGroup<*>: DataFrame
            // however, this can sometimes cause issues where instance checks are done at runtime
            // this converts it to an actual DataFrame instance
            dataFrameOf((it as ColumnGroup<*>).columns())
        }
}

/**
 * Recursive helper function to handle traversal across dimensions. Do not call directly,
 * use [convertToDataFrameNestedGroups] instead.
 */
@PublishedApi
internal fun NDArray<*, *>.convertToDataFrameNestedGroupsRecursive(
    indices: IntArray,
    type: KType,
    columnNameGenerator: (Int) -> String,
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
