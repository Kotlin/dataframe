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
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.complex.Complex
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

// region 1D

/** Converts a one-dimensional array ([D1Array]) to a [DataColumn] with optional [name]. */
inline fun <reified N> D1Array<N>.convertToColumn(name: String = ""): DataColumn<N> = column(toList()) named name

/** Converts a [DataColumn] to a one-dimensional array ([D1Array]). */
@JvmName("convertNumberColumnToMultik")
inline fun <reified N> DataColumn<N>.convertToMultik(): D1Array<N> where N : Number, N : Comparable<N> =
    mk.ndarray(toList())

/** Converts a [DataColumn] to a one-dimensional array ([D1Array]). */
@JvmName("convertComplexColumnToMultik")
inline fun <reified N : Complex> DataColumn<N>.convertToMultik(): D1Array<N> = mk.ndarray(toList())

@JvmName("convertNumberColumnFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N> DataFrame<T>.convertToMultik(
    crossinline column: ColumnSelector<T, N>,
): D1Array<N>
    where N : Number, N : Comparable<N> = getColumn { column(it) }.convertToMultik()

@JvmName("convertComplexColumnFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N : Complex> DataFrame<T>.convertToMultik(crossinline column: ColumnSelector<T, N>): D1Array<N> =
    getColumn { column(it) }.convertToMultik()

/**
 * Converts a one-dimensional array ([D1Array]) of type [N] into a DataFrame.
 * The resulting DataFrame contains a single column named "value", where each element of the array becomes a row in the DataFrame.
 *
 * @return a DataFrame where each element of the source array is represented as a row in a column named "value" under the schema [ValueProperty].
 */
inline fun <reified N> D1Array<N>.convertToDataFrame(): DataFrame<ValueProperty<N>> =
    dataFrameOf(ValueProperty<*>::value.name to column(toList()))
        .cast()

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
inline fun <reified N> D2Array<N>.convertToDataFrame(columnNameGenerator: (Int) -> String = { "col$it" }): AnyFrame =
    (0..<shape[1]).map { col ->
        this[0..<shape[0], col]
            .toList()
            .toColumn(columnNameGenerator(col))
    }.toDataFrame()

@JvmName("convertToMultikOfComplex")
inline fun <reified N : Complex> AnyFrame.convertToMultikOf(_klass: KClass<Complex> = Complex::class): D2Array<N> =
    convertToMultik { colsOf<N>() }

@JvmName("convertToMultikOfNumber")
inline fun <reified N> AnyFrame.convertToMultikOf(
    _klass: KClass<Number> = Number::class,
): D2Array<N> where N : Number, N : Comparable<N> = convertToMultik { colsOf<N>() }

@JvmName("convertToMultikGuess")
fun AnyFrame.convertToMultik(): D2Array<*> {
    val columnTypes = columnTypes().distinct()
    return when {
        columnTypes.size != 1 -> error("found column types: $columnTypes")
        columnTypes.single() == typeOf<Complex>() -> convertToMultik { colsOf<Complex>() }
        columnTypes.single().isSubtypeOf(typeOf<Byte>()) -> convertToMultik { colsOf<Byte>() }
        columnTypes.single().isSubtypeOf(typeOf<Short>()) -> convertToMultik { colsOf<Short>() }
        columnTypes.single().isSubtypeOf(typeOf<Int>()) -> convertToMultik { colsOf<Int>() }
        columnTypes.single().isSubtypeOf(typeOf<Long>()) -> convertToMultik { colsOf<Long>() }
        columnTypes.single().isSubtypeOf(typeOf<Float>()) -> convertToMultik { colsOf<Float>() }
        columnTypes.single().isSubtypeOf(typeOf<Double>()) -> convertToMultik { colsOf<Double>() }
        else -> error("found column types: $columnTypes")
    }
}

@JvmName("convertNumberColumnsFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N> DataFrame<T>.convertToMultik(
    crossinline columns: ColumnsSelector<T, N>,
): D2Array<N>
    where N : Number, N : Comparable<N> = getColumns { columns(it) }.convertToMultik()

@JvmName("convertComplexColumnsFromDfToMultik")
@OverloadResolutionByLambdaReturnType
inline fun <T, reified N : Complex> DataFrame<T>.convertToMultik(
    crossinline columns: ColumnsSelector<T, N>,
): D2Array<N> = getColumns { columns(it) }.convertToMultik()

@JvmName("convertNumberColumnsToMultik")
inline fun <reified N> List<DataColumn<N>>.convertToMultik(): D2Array<N> where N : Number, N : Comparable<N> =
    mk.ndarray(
        toDataFrame().map { it.values() as List<N> },
    )

@JvmName("convertComplexColumnsToMultik")
inline fun <reified N : Complex> List<DataColumn<N>>.convertToMultik(): D2Array<N> =
    mk.ndarray(
        toDataFrame().map { it.values() as List<N> },
    )

// endregion
