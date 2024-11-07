package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.math.cumSum
import org.jetbrains.kotlinx.dataframe.math.defaultCumSumSkipNA
import org.jetbrains.kotlinx.dataframe.typeClass
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region DataColumn

public fun <T : Number?> DataColumn<T>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataColumn<T> =
    when (type()) {
        typeOf<Double>() -> cast<Double>().cumSum(skipNA).cast()

        typeOf<Double?>() -> cast<Double?>().cumSum(skipNA).cast()

        typeOf<Float>() -> cast<Float>().cumSum(skipNA).cast()

        typeOf<Float?>() -> cast<Float?>().cumSum(skipNA).cast()

        // careful, cast to Int can occur! TODO
        typeOf<Int>(), typeOf<Byte>(), typeOf<Short>() -> cast<Int>().cumSum().cast()

        // careful, cast to Int can occur! TODO
        typeOf<Int?>(), typeOf<Byte?>(), typeOf<Short?>() -> cast<Int?>().cumSum(skipNA).cast()

        typeOf<Long>() -> cast<Long>().cumSum().cast()

        typeOf<Long?>() -> cast<Long?>().cumSum(skipNA).cast()

        typeOf<BigInteger>() -> cast<BigInteger>().cumSum().cast()

        typeOf<BigInteger?>() -> cast<BigInteger?>().cumSum(skipNA).cast()

        typeOf<BigDecimal>() -> cast<BigDecimal>().cumSum().cast()

        typeOf<BigDecimal?>() -> cast<BigDecimal?>().cumSum(skipNA).cast()

        typeOf<Number?>(), typeOf<Number>() -> convertToDouble().cumSum(skipNA).cast()

        else -> error("Cumsum for type ${type()} is not supported")
    }

private val supportedClasses = setOf(
    Double::class,
    Float::class,
    Int::class,
    Byte::class,
    Short::class,
    Long::class,
    BigInteger::class,
    BigDecimal::class,
)

// endregion

// region DataFrame

public fun <T, C> DataFrame<T>.cumSum(
    skipNA: Boolean = defaultCumSumSkipNA,
    columns: ColumnsSelector<T, C>,
): DataFrame<T> =
    convert(columns).to { if (it.typeClass in supportedClasses) it.cast<Number?>().cumSum(skipNA) else it }

public fun <T> DataFrame<T>.cumSum(vararg columns: String, skipNA: Boolean = defaultCumSumSkipNA): DataFrame<T> =
    cumSum(skipNA) { columns.toColumnSet() }

public fun <T> DataFrame<T>.cumSum(
    vararg columns: AnyColumnReference,
    skipNA: Boolean = defaultCumSumSkipNA,
): DataFrame<T> = cumSum(skipNA) { columns.toColumnSet() }

public fun <T> DataFrame<T>.cumSum(vararg columns: KProperty<*>, skipNA: Boolean = defaultCumSumSkipNA): DataFrame<T> =
    cumSum(skipNA) { columns.toColumnSet() }

public fun <T> DataFrame<T>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): DataFrame<T> =
    cumSum(skipNA) {
        colsAtAnyDepth { !it.isColumnGroup() }
    }

// endregion

// region GroupBy

public fun <T, G, C> GroupBy<T, G>.cumSum(
    skipNA: Boolean = defaultCumSumSkipNA,
    columns: ColumnsSelector<G, C>,
): GroupBy<T, G> = updateGroups { cumSum(skipNA, columns) }

public fun <T, G> GroupBy<T, G>.cumSum(vararg columns: String, skipNA: Boolean = defaultCumSumSkipNA): GroupBy<T, G> =
    cumSum(skipNA) { columns.toColumnSet() }

public fun <T, G> GroupBy<T, G>.cumSum(
    vararg columns: AnyColumnReference,
    skipNA: Boolean = defaultCumSumSkipNA,
): GroupBy<T, G> = cumSum(skipNA) { columns.toColumnSet() }

public fun <T, G> GroupBy<T, G>.cumSum(
    vararg columns: KProperty<*>,
    skipNA: Boolean = defaultCumSumSkipNA,
): GroupBy<T, G> = cumSum(skipNA) { columns.toColumnSet() }

public fun <T, G> GroupBy<T, G>.cumSum(skipNA: Boolean = defaultCumSumSkipNA): GroupBy<T, G> =
    cumSum(skipNA) {
        colsAtAnyDepth { !it.isColumnGroup() }
    }

// endregion
