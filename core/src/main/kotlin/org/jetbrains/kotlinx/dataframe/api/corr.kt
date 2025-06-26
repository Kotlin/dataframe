package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls.Convert
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnGroupsAndNestedColumnsMention
import org.jetbrains.kotlinx.dataframe.impl.api.corrImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Calculates the correlation between values in the specified [columns\].
 *
 * This function does not perform the calculation immediately. Instead, it selects a primary set of columns
 * and returns a [Corr] object, which serves as an intermediate step in the correlation analysis.
 *
 * The [Corr] object provides two methods to perform correlation computations:
 * - [with][Corr.with] — allows you to specify a second set of columns and computes correlations between
 *   the initially selected columns and this second set.
 * - [withItself][Corr.withItself] — computes correlations within the initially selected columns.
 *
 * Each of these methods returns a [DataFrame] where rows correspond to one set of columns, columns to the other set,
 * and each cell contains the correlation coefficient between the corresponding pair of columns.
 *
 * If you need to compute correlations between all columns in a DataFrame, use [DataFrame.corr()][DataFrame.corr].
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][ConvertSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Corr]}
 */
internal interface CorrDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetCorrOperationArg]}
     */
    interface ConvertSelectingOptions

    /**
     * ## Corr Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * **[`corr`][convert]**`  { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * {@include [Indent]}
     * __`.`__[**`with`**][Corr.with]` { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * {@include [Indent]}
     *`| `__`.`__[**`withItself`**][Corr.withItself]`()`
     */
    interface Grammar
}

/** {@set [SelectingColumns.OPERATION] [corr][corr]} */
@ExcludeFromSources
private interface SetCorrOperationArg

/**
 * {@include [CorrDocs]}
 * ### This Corr Overload
 */
@ExcludeFromSources
private interface CommonCorrDocs

internal fun AnyCol.isSuitableForCorr() = isSubtypeOf<Number>() || type() == typeOf<Boolean>()

// region DataFrame

/**
 * An intermediate class used in the [corr] operation.
 *
 * This class itself does not perform any computations — it is a transitional step
 * before specifying how to compute correlation.
 * It must be followed by one of the methods specifying correlation
 * computation to produce a new correlation [DataFrame].
 *
 * Each of these methods returns a [DataFrame] where rows correspond to one set of columns, columns to the other set,
 * and each cell contains the correlation coefficient between the corresponding pair of columns.
 *
 * Use the following methods to perform the computation:
 * - [with { columnsSelector }][with] – selects a second set of columns and computes correlations between
 *  the initially selected columns and this second set.
 * - [withItself()][withItself] - computes correlations within the initially selected columns.
 *
 * See [Grammar][CorrDocs.Grammar] for more details.
 */
public data class Corr<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>)

public fun <T> DataFrame<T>.corr(): DataFrame<T> =
    corr {
        colsAtAnyDepth().filter { it.isSuitableForCorr() }
    }.withItself()

public fun <T, C> DataFrame<T>.corr(columns: ColumnsSelector<T, C>): Corr<T, C> = Corr(this, columns)

public fun <T> DataFrame<T>.corr(vararg columns: String): Corr<T, Any?> = corr { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.corr(vararg columns: KProperty<C>): Corr<T, C> = corr { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.corr(vararg columns: ColumnReference<C>): Corr<T, C> = corr { columns.toColumnSet() }

public fun <T, C, R> Corr<T, C>.with(otherColumns: ColumnsSelector<T, R>): DataFrame<T> = corrImpl(otherColumns)

public fun <T, C> Corr<T, C>.with(vararg otherColumns: String): DataFrame<T> = with { otherColumns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: KProperty<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: ColumnReference<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

public fun <T, C> Corr<T, C>.withItself(): DataFrame<T> = with(columns)

// endregion
