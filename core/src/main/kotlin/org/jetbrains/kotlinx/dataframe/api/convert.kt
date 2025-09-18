package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toStdlibInstant
import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Converter
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.convertToDeprecatedInstant
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.convertRowColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.api.convertToDoubleImpl
import org.jetbrains.kotlinx.dataframe.impl.api.convertToTypeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalTime
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.jetbrains.kotlinx.dataframe.io.toDataFrame
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_DEPRECATED_INSTANT
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_DEPRECATED_INSTANT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_INSTANT
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_INSTANT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_URL
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_URL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.TO_DEPRECATED_INSTANT
import org.jetbrains.kotlinx.dataframe.util.TO_DEPRECATED_INSTANT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.TO_INSTANT
import org.jetbrains.kotlinx.dataframe.util.TO_INSTANT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.TO_URL
import org.jetbrains.kotlinx.dataframe.util.TO_URL_REPLACE
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.util.Locale
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

/**
 * See also [parse] — a specialized form of the [convert] operation that parses [String] columns
 * into other types without requiring explicit type specification.
 */
internal interface SeeAlsoParse

/**
 * Converts the values in the specified [columns\] either to a supported target type
 * or using a custom converter, keeping their original names and positions within the [DataFrame].
 *
 * This function does not immediately convert the columns but instead selects columns to convert and
 * returns a [Convert],
 * which serves as an intermediate step.
 * The [Convert] object provides methods to transform selected columns using:
 * - [to][Convert.to]
 * - [with][Convert.with]
 * - [asFrame][Convert.asFrame]
 * - [perRowCol][Convert.perRowCol]
 * - [notNull][Convert.notNull]
 * - [toDataFrames][Convert.toDataFrames]
 *
 * Additionally, it offers a wide range of methods for converting to specific types,
 * such as [toStr][Convert.toStr], [toDouble][Convert.toDouble], and many others.
 *
 * For the full list of supported types, see [SupportedTypes].
 *
 * Each method returns a new [DataFrame] with the updated columns.
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][ConvertSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * @include [SeeAlsoParse]
 */
internal interface ConvertDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetConvertOperationArg]}
     */
    interface ConvertSelectingOptions

    /**
     * List of types, supported in [convert to][Convert.to] operation:
     * * [String] (uses parse to convert from String to other types);
     * * [Boolean];
     * * [Byte], [Short], [Char];
     * * [Int], [Long], [Float], [Double];
     * * [BigDecimal], [BigInteger];
     * * [LocalDateTime], [LocalDate], [LocalTime],
     *   `Instant` ([kotlinx.datetime][DeprecatedInstant], [kotlin.time][StdlibInstant], and [java.time]),
     * * [URL], [IMG], [IFRAME].
     */
    interface SupportedTypes

    /**
     * ## Convert Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * **[`convert`][DataFrame.convert]**`  { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * {@include [Indent]}
     * __`.`__[**`with`**][Convert.with]`(infer: `[`Infer`][Infer]`, rowExpression: `[`RowValueExpression`][RowValueExpression]`)`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`notNull`**][Convert.notNull]`  { rowExpression: `[`RowValueExpression`][RowValueExpression]`  }`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`to`**][Convert.to]`<T>()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`to`**][Convert.to]`(type: `[`KType`][KType]`)`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`perRowCol`**][Convert.perRowCol]`  { expression: `[`RowColumnExpression`][RowColumnExpression]`  }`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`asFrame`**][Convert.asFrame]`  { builder: `[`ColumnsContainer`](ColumnsContainer)`.(`[`ColumnConvert`](ColumnConvert)`) -> `[`DataFrame`](DataFrame)`  }`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toStr`**][Convert.toStr]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toInt`**][Convert.toInt]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toLong`**][Convert.toLong]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toDouble`**][Convert.toDouble]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toFloat`**][Convert.toFloat]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toBigDecimal`**][Convert.toBigDecimal]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toBigInteger`**][Convert.toBigInteger]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toBoolean`**][Convert.toBoolean]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toLocalDateTime`**][Convert.toLocalDateTime]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toLocalDate`**][Convert.toLocalDate]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toLocalTime`**][Convert.toLocalTime]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toInstant`**][Convert.toInstant]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toUrl`**][Convert.toUrl]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toIFrame`**][Convert.toIFrame]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toImg`**][Convert.toImg]`()`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toDataFrames`**][Convert.toDataFrames]`()`
     */
    interface Grammar
}

/** {@set [SelectingColumns.OPERATION] [convert][convert]} */
@ExcludeFromSources
private interface SetConvertOperationArg

/**
 * {@include [ConvertDocs]}
 * ### This Convert Overload
 */
@ExcludeFromSources
private interface CommonConvertDocs

/**
 * @include [CommonConvertDocs]
 * @include [SelectingColumns.Dsl] {@include [SetConvertOperationArg]}
 * ### Examples:
 * ```kotlin
 * df.convert { columnA and columnB }.with { it.toString().lowercase() }
 * df.convert { colsOf<String>() }.to<Double>()
 * df.convert { colsAtAnyDepth().colGroups() }.asFrame { it.add("nan") { Double.NaN } }
 * ```
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to group.
 */
@Interpretable("Convert0")
public fun <T, C> DataFrame<T>.convert(columns: ColumnsSelector<T, C>): Convert<T, C> = Convert(this, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.convert(vararg columns: KProperty<C>): Convert<T, C> = convert { columns.toColumnSet() }

/**
 * @include [CommonConvertDocs]
 * @include [SelectingColumns.ColumnNames] {@include [SetConvertOperationArg]}
 * ### Example:
 * ```kotlin
 * df.convert("person", "position").toStr()
 * df.convert("value").with { (it as Number).toDouble() }
 * ```
 * @param [columns\] The [Column Names][String] used to select the columns of this [DataFrame] to group.
 */
@Interpretable("Convert2")
public fun <T> DataFrame<T>.convert(vararg columns: String): Convert<T, Any?> = convert { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.convert(vararg columns: ColumnReference<C>): Convert<T, C> =
    convert { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    infer: Infer = Infer.Nulls,
    noinline expression: RowValueExpression<T, C, R>,
): DataFrame<T> = convert(*headPlusArray(firstCol, cols)).with(infer, expression)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    infer: Infer = Infer.Nulls,
    noinline expression: RowValueExpression<T, C, R>,
): DataFrame<T> = convert(*headPlusArray(firstCol, cols)).with(infer, expression)

/**
 * Converts specified columns
 * using row converter [expression] within the [DataFrame].
 * {@include [ExpressionsGivenRow.RowValueExpression]}
 *
 * ## Note
 * @include [ExpressionsGivenRow.AddDataRowNote]
 * ## See Also
 * - {@include [SeeAlsoConvertPerRowCol]}
 *
 * ### Example:
 * ```kotlin
 * // Convert values in selected column to a trimmed `String`.
 * df.convert("valueA", "valueB") { it.toString().trim() }
 * ```
 * @param [expression] The {@include [ExpressionsGivenRow.RowValueExpressionLink]} to update the rows with.
 * @return A new [DataFrame] with the converted values.
 */
@Refine
@Interpretable("Convert6")
public inline fun <T, reified R> DataFrame<T>.convert(
    firstCol: String,
    vararg cols: String,
    infer: Infer = Infer.Nulls,
    noinline expression: RowValueExpression<T, Any?, R>,
): DataFrame<T> = convert(*headPlusArray(firstCol, cols)).with(infer, expression)

@Refine
@Interpretable("ConvertNotNull")
public inline fun <T, C, reified R> Convert<T, C?>.notNull(
    crossinline expression: RowValueExpression<T, C, R>,
): DataFrame<T> =
    with {
        if (it == null) {
            null
        } else {
            expression(this, it)
        }
    }

/**
 * An intermediate class used in the [convert] operation.
 *
 * This class itself does not perform any conversion — it is a transitional step
 * before specifying how to convert the selected columns.
 * It must be followed by one of the conversion methods
 * to produce a new [DataFrame] with updated column values and types.
 *
 * The resulting columns will keep their original names and positions
 * in the [DataFrame], but their values will be transformed.
 *
 * Use the following methods to perform the conversion:
 * - [to(kType)][to]/[to`<Type`>()][to] – converts columns to a specific type.
 * - [asColumn { columnConverter }][asColumn] - converts columns using column converter expression.
 * - [with][Convert.with] – applies a custom row-wise conversion expression.
 * - [notNull][Convert.notNull] – like [with], but only for non-null values.
 * - [perRowCol][Convert.perRowCol] – applies a conversion that uses both column and row information.
 * - [asFrame][Convert.asFrame] – converts [column groups][ColumnGroup] as a [DataFrame] with the given expression.
 * - [toStr], [toInt], [toLong], [toDouble], [toFloat], [toBigDecimal],
 *   [toBigInteger], [toBoolean] – convert to standard types.
 * - [toLocalDateTime], [toLocalDate], [toLocalTime] – convert to kotlinx.datetime types.
 * - [toInstant] (temporarily deprecated), [toStdlibInstant], [toDeprecatedInstant] – convert to `Instant`
 * - [toUrl], [toIFrame], [toImg] – convert to special types.
 * - [toDataFrames] – converts a column of lists into separate DataFrames.
 *
 * See [Grammar][ConvertDocs.Grammar] for more details.
 */
@HasSchema(schemaArg = 0)
public class Convert<T, out C>(
    @PublishedApi internal val df: DataFrame<T>,
    @PublishedApi internal val columns: ColumnsSelector<T, C>,
) {
    /**
     * Casts the type parameter of the columns previously selected with [convert][convert] to a new type [R],
     * without performing any actual data transformation.
     *
     * This operation updates the static type of the selected columns for further type-safe conversions.
     */
    public fun <R> cast(): Convert<T, R> = Convert(df, columns as ColumnsSelector<T, R>)

    /**
     * Converts values in the columns previously selected with [convert] to the specified type [D],
     * preserving their original names and positions within the [DataFrame].
     *
     * The target type is provided as a reified type argument.
     * For the full list of supported types, see [ConvertDocs.SupportedTypes].
     *
     * For more information: {@include [DocumentationUrls.Convert]}
     *
     * ### Examples:
     * ```kotlin
     * // Convert selected columns to Int:
     * df.convert("year", "count").to<Int>()
     *
     * // Convert all String columns to LocalDate:
     * df.convert { colsOf<String>() }.to<LocalDate>()
     * ```
     *
     * @param D The target type, provided as a reified type argument, to convert values to.
     * @return A new [DataFrame] with the values converted to type [D].
     */
    @Refine
    @Interpretable("To0")
    public inline fun <reified D> to(): DataFrame<T> = to(typeOf<D>())

    override fun toString(): String = "Convert(df=$df, columns=$columns)"
}

/**
 * Converts values in the columns previously selected with [convert] to the specified [type],
 * preserving their original names and positions within the [DataFrame].
 *
 * The target type is provided as a [KType].
 * For the full list of supported types, see [ConvertDocs.SupportedTypes].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * // Convert selected columns to String:
 * df.convert("year", "count").to(typeOf<String>())
 * df.convert { year and count }.to(typeOf<String>())
 * // Convert all `Int` columns to `Double`
 * df.convert { colsOf<Int>() }.to(typeOf<Double>())
 * ```
 *
 * @param type The target type, provided as a [KType], to convert values to.
 * @return A new [DataFrame] with the values converted to [type].
 */
public fun <T> Convert<T, *>.to(type: KType): DataFrame<T> = asColumn { it.convertTo(type) }

@Deprecated(CONVERT_TO, ReplaceWith(CONVERT_TO_REPLACE), DeprecationLevel.ERROR)
public fun <T, C> Convert<T, C>.to(columnConverter: DataFrame<T>.(DataColumn<C>) -> AnyBaseCol): DataFrame<T> =
    df.replace(columns).with { columnConverter(df, it) }

/** [Convert per row col][Convert.perRowCol] to provide a new value for every selected cell giving its column. */
@ExcludeFromSources
private interface SeeAlsoConvertPerRowCol

/** [Convert as column][Convert.asColumn] to convert using a column converter */
@ExcludeFromSources
private interface SeeAlsoConvertAsColumn

/**
 * Converts values in columns previously selected by [convert] using the specified [rowConverter],
 * a [row value expression][RowValueExpression] applied to each row in the [DataFrame].
 *
 * A [row value expression][RowValueExpression] allows to provide a new value for every selected cell
 * given its row (as a receiver) and its previous value (as a lambda argument).
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ## Note
 * @include [ExpressionsGivenRow.AddDataRowNote]
 * ## See Also
 * - {@include [SeeAlsoConvertPerRowCol]}
 * - {@include [SeeAlsoConvertAsColumn]}
 *
 * ### Examples:
 * ```kotlin
 * // Select columns with json values and convert it to decoded `String`.
 * df.convert { valueJson }.with { Json.decode(it) }
 * // Convert all `Int` columns to `Duration`, multiplying each value by the corresponding value from the "coeff" `Double` column before conversion
 * df.convert { colsOf<Int>() }.with { baseValue -> (baseValue * coeff).seconds }
 * ```
 *
 * @param infer [Infer] strategy that defines how the [type][DataColumn.type] of the resulting column should be determined.
 * Defaults to [Infer.Nulls].
 * @param [rowConverter] The [RowValueExpression] to provide a new value for every selected cell giving its row and previous value.
 * @return A new [DataFrame] with the converted values.
 */
@Refine
@Interpretable("With0")
public inline fun <T, C, reified R> Convert<T, C>.with(
    infer: Infer = Infer.Nulls,
    noinline rowConverter: RowValueExpression<T, C, R>,
): DataFrame<T> = withRowCellImpl(typeOf<R>(), infer, rowConverter)

/**
 * Converts [column groups][ColumnGroup] previously selected with [convert]
 * as a [DataFrame] using a [dataframe expression][DataFrameExpression].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Example:
 * ```kotlin
 * // Add a column to selected column group "name".
 * df.convert { name }.asFrame { it.add("fullName") { "\$firstName \$lastName" } }
 * ```
 *
 * @param [expression] The {@include [ExpressionsGivenDataFrame.DataFrameExpressionLink]} to replace the selected column group with.
 */
@Refine
@Interpretable("ConvertAsFrame")
public fun <T, C, R> Convert<T, DataRow<C>>.asFrame(
    expression: ColumnsContainer<T>.(ColumnGroup<C>) -> DataFrame<R>,
): DataFrame<T> = asColumn { expression(this, it.asColumnGroup()).asColumnGroup(it.name()) }

/**
 * Converts values in the columns previously selected with [convert]
 * using [columnConverter] expression within the [DataFrame].
 *
 * The [columnConverter] is a lambda with the current [DataFrame] as receiver and the selected column as argument.
 * It returns a new column that will replace the original column.
 * **Preserves original column name for each column (even it was explicitly changed in [columnConverter] expression).**
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * It's a compiler plugin-friendly variant of [ReplaceClause.with].
 * [ReplaceClause.with] allows to change both column types and names.
 * Tracking of column name changes in arbitrary lambda expression is unreliable and generally impossible
 * to do statically.
 * This function ensures that all column names remain as is and only their type changes to [R]
 *
 * ## See Also
 *  - {@include [SeeAlsoConvertWith]}
 *  - [Replace with][ReplaceClause.with] to replace columns using a column converter,
 * allowing both column names and types to be changed.
 *
 * ### Examples:
 * ```kotlin
 * // Convert all columns into column groups, each containing the original column
 * df.convert { all() }.asColumn { listOf(it).toColumnGroup(it.name) }
 * // Converts all `String` columns by applying heavyIO to each element in parallel and assembling results back into columns.
 * df.convert { colsOf<String>() }.asColumn { it.asList().parallelStream().map { heavyIO(it) }.toList().toColumn() }`
 * ```
 *
 * @return A new [DataFrame] with the values converted to [type].
 */
@Refine
@Interpretable("ConvertAsColumn")
public inline fun <T, C, R> Convert<T, C>.asColumn(
    crossinline columnConverter: DataFrame<T>.(DataColumn<C>) -> BaseColumn<R>,
): DataFrame<T> = df.replace(columns).with { columnConverter(df, it).rename(it.name()) }

/** [Convert with][Convert.with] to provide a new value for every selected cell
 * giving its row and its previous value. */
@ExcludeFromSources
private interface SeeAlsoConvertWith

/**
 * Converts values in the columns previously selected with [convert]
 * using [row column][RowColumnExpression] [expression] within the [DataFrame].
 *
 * A [row column expression][RowColumnExpression] allows to provide a new value for every selected cell
 * given its row and column (as lambda arguments).
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ## See Also
 *  - {@include [SeeAlsoConvertWith]}
 *  - {@include [SeeAlsoConvertAsColumn]}
 *
 * ### Example:
 * ```kotlin
 * // Convert values in all columns to `String` and add their column name to the end
 * df.convert { all() }.perRowCol { row, col ->
 *    col[row].toString() + col.name()
 * }
 * ```
 *
 * @param infer [Infer] strategy that defines how the [type][DataColumn.type] of the resulting column should be determined.
 * Defaults to [Infer.Nulls].
 * @param [expression] The [RowColumnExpression] to provide a new value for every selected cell giving its row and column.
 */
@Refine
@Interpretable("PerRowCol")
public inline fun <T, C, reified R> Convert<T, C>.perRowCol(
    infer: Infer = Infer.Nulls,
    noinline expression: RowColumnExpression<T, C, R>,
): DataFrame<T> = convertRowColumnImpl(typeOf<R>(), infer, expression)

/**
 * Converts values in this column to the specified type [C].
 *
 * The target type is provided as a reified type argument.
 *
 * For the full list of supported types, see [ConvertDocs.SupportedTypes].
 *
 * @param [C] The target type to convert values to.
 * @return A new [DataColumn] with the values converted to type [C].
 */
public inline fun <reified C> AnyCol.convertTo(): DataColumn<C> = convertTo(typeOf<C>()) as DataColumn<C>

/**
 * Converts values in this column to the specified [type].
 *
 * For the full list of supported types, see [ConvertDocs.SupportedTypes].
 *
 * @param type The target type, provided as a [KType], to convert values to.
 * @return A new [DataColumn] with the values converted to [type].
 */
@Suppress("UNCHECKED_CAST")
public fun AnyCol.convertTo(newType: KType): AnyCol =
    when {
        type().isSubtypeOf(typeOf<String?>()) ->
            (this as DataColumn<String?>).convertTo(newType)

        else -> convertToTypeImpl(newType, null)
    }

/**
 * Converts values in this `String` column to the specified type [C].
 *
 * The target type is provided as a reified type argument.
 *
 * @include [SeeAlsoParse]
 *
 * @param [C] The target type to convert values to.
 * @param [parserOptions] Optional [ParserOptions] to customize parsing behavior (e.g., locale, null strings).
 * @return A new [DataColumn] with the values converted to type [C].
 */
public inline fun <reified C> DataColumn<String?>.convertTo(parserOptions: ParserOptions? = null): DataColumn<C> =
    convertTo(typeOf<C>(), parserOptions) as DataColumn<C>

/**
 * Converts values in this `String` column to the specified [type][newType].
 *
 * The target type is provided as a [KType].
 *
 * @include [SeeAlsoParse]
 *
 * @param [newType] The target type to convert values to.
 * @param [parserOptions] Optional [ParserOptions] to customize parsing behavior (e.g., locale, null strings).
 * @return A new [DataColumn] with the values converted to [type].
 */
public fun DataColumn<String?>.convertTo(newType: KType, parserOptions: ParserOptions? = null): AnyCol =
    when {
        newType.isSubtypeOf(typeOf<Double?>()) ->
            convertToDoubleImpl(
                locale = parserOptions?.locale,
                nullStrings = parserOptions?.nullStrings,
                useFastDoubleParser = parserOptions?.useFastDoubleParser,
            ).setNullable(newType.isMarkedNullable)

        else -> convertToTypeImpl(newType, parserOptions)
    }

/**
 * Converts values in this column to [LocalDateTime].
 *
 * @return A new [DataColumn] with the [LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromT")
public fun <T : Any> DataColumn<T>.convertToLocalDateTime(): DataColumn<LocalDateTime> = convertTo()

/**
 * Converts values in this column to [LocalDateTime]. Preserves null values.
 *
 * @return A new [DataColumn] with the [LocalDateTime] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLocalDateTime(): DataColumn<LocalDateTime?> = convertTo()

/**
 * Converts values in this column to [LocalDate].
 *
 * @return A new [DataColumn] with the [LocalDate] values.
 */
@JvmName("convertToLocalDateFromT")
public fun <T : Any> DataColumn<T>.convertToLocalDate(): DataColumn<LocalDate> = convertTo()

/**
 * Converts values in this column to [LocalDate]. Preserves null values.
 *
 * @return A new [DataColumn] with the [LocalDate] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLocalDate(): DataColumn<LocalDate?> = convertTo()

/**
 * Converts values in this column to [LocalTime].
 *
 * @return A new [DataColumn] with the [LocalTime] values.
 */
@JvmName("convertToLocalTimeFromT")
public fun <T : Any> DataColumn<T>.convertToLocalTime(): DataColumn<LocalTime> = convertTo()

/**
 * Converts values in this column to [LocalTime]. Preserves null values.
 *
 * @return A new [DataColumn] with the [LocalTime] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLocalTime(): DataColumn<LocalTime?> = convertTo()

/**
 * Converts values in this column to [Byte].
 *
 * @return A new [DataColumn] with the [Byte] values.
 */
@JvmName("convertToByteFromT")
public fun <T : Any> DataColumn<T>.convertToByte(): DataColumn<Byte> = convertTo()

/**
 * Converts values in this column to [Byte]. Preserves null values.
 *
 * @return A new [DataColumn] with the [Byte] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToByte(): DataColumn<Byte?> = convertTo()

/**
 * Converts values in this column to [Short].
 *
 * @return A new [DataColumn] with the [Short] values.
 */
@JvmName("convertToShortFromT")
public fun <T : Any> DataColumn<T>.convertToShort(): DataColumn<Short> = convertTo()

/**
 * Converts values in this column to [Short]. Preserves null values.
 *
 * @return A new [DataColumn] with the [Short] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToShort(): DataColumn<Short?> = convertTo()

/**
 * Converts values in this column to [Int].
 *
 * @return A new [DataColumn] with the [Int] values.
 */
@JvmName("convertToIntFromT")
public fun <T : Any> DataColumn<T>.convertToInt(): DataColumn<Int> = convertTo()

/**
 * Converts values in this column to [Int]. Preserves null values.
 *
 * @return A new [DataColumn] with the [Int] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToInt(): DataColumn<Int?> = convertTo()

/**
 * Converts values in this column to [Long].
 *
 * @return A new [DataColumn] with the [Long] values.
 */
@JvmName("convertToLongFromT")
public fun <T : Any> DataColumn<T>.convertToLong(): DataColumn<Long> = convertTo()

/**
 * Converts values in this column to [Long]. Preserves null values.
 *
 * @return A new [DataColumn] with the [Long] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLong(): DataColumn<Long?> = convertTo()

/**
 * Converts values in this column to [String].
 *
 * @return A new [DataColumn] with the [String] values.
 */
@JvmName("convertToStringFromT")
public fun <T : Any> DataColumn<T>.convertToString(): DataColumn<String> = convertTo()

/**
 * Converts values in this column to [String]. Preserves null values.
 *
 * @return A new [DataColumn] with the [String] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToString(): DataColumn<String?> = convertTo()

/**
 * Converts values in this column to [Double].
 *
 * @return A new [DataColumn] with the [Double] values.
 */
@JvmName("convertToDoubleFromT")
public fun <T : Any> DataColumn<T>.convertToDouble(): DataColumn<Double> = convertTo()

/**
 * Converts values in this column to [Double]. Preserves null values.
 *
 * @return A new [DataColumn] with the [Double] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToDouble(): DataColumn<Double?> = convertTo()

/**
 * Converts values in this [String] column to [Double] considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [DataFrame.parser][DataFrame.Companion.parser]) is used.
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [DataFrame.parser][DataFrame.Companion.parser] is the system locale.
 *   If the column cannot be parsed, the POSIX format is used.
 *
 * @return A new [DataColumn] with the [Double] values.
 */
@ExcludeFromSources
private interface DataColumnStringConvertToDoubleDoc

/** @include [DataColumnStringConvertToDoubleDoc] */
@JvmName("convertToDoubleFromString")
public fun DataColumn<String>.convertToDouble(locale: Locale? = null): DataColumn<Double> =
    convertToDouble(locale = locale, nullStrings = null, useFastDoubleParser = null)

/**
 * @include [DataColumnStringConvertToDoubleDoc]
 * @param nullStrings a set of strings that should be treated as `null` values.
 *   The default in [DataFrame.parser][DataFrame.Companion.parser] is ["null", "NULL", "NA", "N/A"].
 * @param useFastDoubleParser whether to use [FastDoubleParser].
 *   The default in [DataFrame.parser][DataFrame.Companion.parser] is `true`.
 */
@JvmName("convertToDoubleFromString")
public fun DataColumn<String>.convertToDouble(
    locale: Locale? = null,
    nullStrings: Set<String>?,
    useFastDoubleParser: Boolean?,
): DataColumn<Double> =
    this.castToNullable().convertToDouble(locale, nullStrings, useFastDoubleParser).castToNotNullable()

/** @include [DataColumnStringConvertToDoubleDoc] */
@JvmName("convertToDoubleFromStringNullable")
public fun DataColumn<String?>.convertToDouble(locale: Locale? = null): DataColumn<Double?> =
    convertToDouble(locale = locale, nullStrings = null, useFastDoubleParser = null)

/**
 * @include [DataColumnStringConvertToDoubleDoc]
 * @param nullStrings a set of strings that should be treated as `null` values.
 *   The default in [DataFrame.parser][DataFrame.Companion.parser] is ["null", "NULL", "NA", "N/A"].
 * @param useFastDoubleParser whether to use [FastDoubleParser].
 *   The default in [DataFrame.parser][DataFrame.Companion.parser] is `true`.
 */
@JvmName("convertToDoubleFromStringNullable")
public fun DataColumn<String?>.convertToDouble(
    locale: Locale? = null,
    nullStrings: Set<String>?,
    useFastDoubleParser: Boolean?,
): DataColumn<Double?> =
    convertToDoubleImpl(
        locale = locale,
        nullStrings = nullStrings,
        useFastDoubleParser = useFastDoubleParser,
    )

/**
 * Converts values in this column to [Float].
 *
 * @return A new [DataColumn] with the [Float] values.
 */
@JvmName("convertToFloatFromT")
public fun <T : Any> DataColumn<T>.convertToFloat(): DataColumn<Float> = convertTo()

/**
 * Converts values in this column to [Float]. Preserves null values.
 *
 * @return A new [DataColumn] with the [Float] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToFloat(): DataColumn<Float?> = convertTo()

/**
 * Converts values in this column to [BigDecimal].
 *
 * @return A new [DataColumn] with the [BigDecimal] values.
 */
@JvmName("convertToBigDecimalFromT")
public fun <T : Any> DataColumn<T>.convertToBigDecimal(): DataColumn<BigDecimal> = convertTo()

/**
 * Converts values in this column to [BigDecimal]. Preserves null values.
 *
 * @return A new [DataColumn] with the [BigDecimal] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToBigDecimal(): DataColumn<BigDecimal?> = convertTo()

/**
 * Converts values in this column to [BigInteger].
 *
 * @return A new [DataColumn] with the [BigInteger] values.
 */
@JvmName("convertToBigIntegerFromT")
public fun <T : Any> DataColumn<T>.convertToBigInteger(): DataColumn<BigInteger> = convertTo()

/**
 * Converts values in this column to [BigInteger]. Preserves null values.
 *
 * @return A new [DataColumn] with the [BigInteger] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToBigInteger(): DataColumn<BigInteger?> = convertTo()

/**
 * Converts values in this column to [Boolean].
 *
 * @return A new [DataColumn] with the [Boolean] values.
 */
@JvmName("convertToBooleanFromT")
public fun <T : Any> DataColumn<T>.convertToBoolean(): DataColumn<Boolean> = convertTo()

/**
 * Converts values in this column to [Boolean]. Preserves null values.
 *
 * @return A new [DataColumn] with the [Boolean] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToBoolean(): DataColumn<Boolean?> = convertTo()

// region convert URL

/**
 * Converts values in an [URL] columns previously selected with [convert] to an [IFRAME],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { imgUrl }.toIFrame()
 * ```
 *
 * @param border Whether the iframe should have a border. Defaults to `false`.
 * @param width Optional width of the iframe in pixels.
 * @param height Optional height of the iframe in pixels.
 * @return A new [DataFrame] with the values converted to an [IFRAME].
 */
@JvmName("toIframeFromUrlNullable")
@Refine
@Converter(IFRAME::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, URL?>.toIFrame(
    border: Boolean = false,
    width: Int? = null,
    height: Int? = null,
): DataFrame<T> = asColumn { it.map { url -> url?.let { IFRAME(url.toString(), border, width, height) } } }

/**
 * Converts values in an [URL] columns previously selected with [convert] to an [IFRAME],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { imgUrl }.toIFrame()
 * ```
 *
 * @param border Whether the iframe should have a border. Defaults to `false`.
 * @param width Optional width of the iframe in pixels.
 * @param height Optional height of the iframe in pixels.
 * @return A new [DataFrame] with the values converted to an [IFRAME].
 */
@JvmName("toIframeFromUrl")
@Refine
@Converter(IFRAME::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, URL>.toIFrame(
    border: Boolean = false,
    width: Int? = null,
    height: Int? = null,
): DataFrame<T> = asColumn { it.map { IFRAME(it.toString(), border, width, height) } }

/**
 * Converts values in an [URL] columns previously selected with [convert] to an [IMG],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { avatarUrl }.toImg()
 * ```
 *
 * @param width Optional width of the image in pixels.
 * @param height Optional height of the image in pixels.
 * @return A new [DataFrame] with the values converted to an [IMG].
 */
@JvmName("toImgFromUrlNullable")
@Refine
@Converter(IMG::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T, R : URL?> Convert<T, URL?>.toImg(width: Int? = null, height: Int? = null): DataFrame<T> =
    asColumn { it.map { url -> url?.let { IMG(url.toString(), width, height) } } }

/**
 * Converts values in an [URL] columns previously selected with [convert] to an [IMG],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { avatarUrl }.toImg()
 * ```
 *
 * @param width Optional width of the image in pixels.
 * @param height Optional height of the image in pixels.
 * @return A new [DataFrame] with the values converted to an [IMG].
 */
@JvmName("toImgFromUrl")
@Refine
@Converter(IMG::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T, R : URL?> Convert<T, URL>.toImg(width: Int? = null, height: Int? = null): DataFrame<T> =
    asColumn { it.map { IMG(it.toString(), width, height) } }

// endregion

// region toURL

@Deprecated(CONVERT_TO_URL, ReplaceWith(CONVERT_TO_URL_REPLACE), DeprecationLevel.ERROR)
public fun DataColumn<String>.convertToURL(): DataColumn<URL> = convertToUrl()

/**
 * Converts values in this [String] column to an [URL].
 *
 * @return A new [DataColumn] with an [URL] values.
 */
public fun DataColumn<String>.convertToUrl(): DataColumn<URL> = map { URI(it).toURL() }

@Deprecated(CONVERT_TO_URL, ReplaceWith(CONVERT_TO_URL_REPLACE), DeprecationLevel.ERROR)
@JvmName("convertToURLFromStringNullable")
public fun DataColumn<String?>.convertToURL(): DataColumn<URL?> = convertToUrl()

/**
 * Converts values in this [String] column to an [URL]. Preserves null values.
 *
 * @return A new [DataColumn] with an [URL] nullable values.
 */
@JvmName("convertToUrlFromStringNullable")
public fun DataColumn<String?>.convertToUrl(): DataColumn<URL?> = map { it?.let { URI(it).toURL() } }

@Deprecated(TO_URL, ReplaceWith(TO_URL_REPLACE), DeprecationLevel.ERROR)
@JvmName("toURLFromStringNullable")
@Refine
@Converter(URL::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String?>.toURL(): DataFrame<T> = asColumn { it.convertToUrl() }

/**
 * Converts values in the [String] columns previously selected with [convert] to an [URL],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { webAddress }.toUrl()
 * ```
 *
 * @return A new [DataFrame] with the values converted to an [URL].
 */
@JvmName("toUrlFromStringNullable")
@Refine
@Converter(URL::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String?>.toUrl(): DataFrame<T> = asColumn { it.convertToUrl() }

@Deprecated(TO_URL, ReplaceWith(TO_URL_REPLACE), DeprecationLevel.ERROR)
@JvmName("toURLFromString")
@Refine
@Converter(URL::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String>.toURL(): DataFrame<T> = toUrl()

/**
 * Converts values in the [String] columns previously selected with [convert] to an [URL],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { webAddress }.toUrl()
 * ```
 *
 * @return A new [DataFrame] with the values converted to an [URL].
 */
@JvmName("toUrlFromString")
@Refine
@Converter(URL::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String>.toUrl(): DataFrame<T> = asColumn { it.convertToUrl() }

// endregion

// region toInstant

/**
 * __Deprecated__:
 *
 * [kotlinx.datetime.Instant] is deprecated in favor of [kotlin.time.Instant].
 * Either migrate to [kotlin.time.Instant] and use [convertToStdlibInstant] or use [convertToDeprecatedInstant].
 * This function will be migrated to [kotlin.time.Instant] in 1.1.
 */
@Deprecated(
    message = CONVERT_TO_INSTANT,
    replaceWith = ReplaceWith(CONVERT_TO_INSTANT_REPLACE),
    level = DeprecationLevel.ERROR,
)
public fun DataColumn<String>.convertToInstant(): DataColumn<DeprecatedInstant> = map { DeprecatedInstant.parse(it) }

/**
 * __Deprecated__:
 *
 * [kotlinx.datetime.Instant] is deprecated in favor of [kotlin.time.Instant].
 * Either migrate to [kotlin.time.Instant] and use [convertToStdlibInstant] or use [convertToDeprecatedInstant].
 * This function will be migrated to [kotlin.time.Instant] in 1.1.
 */
@JvmName("convertToInstantFromStringNullable")
@Deprecated(
    message = CONVERT_TO_INSTANT,
    replaceWith = ReplaceWith(CONVERT_TO_INSTANT_REPLACE),
    level = DeprecationLevel.ERROR,
)
public fun DataColumn<String?>.convertToInstant(): DataColumn<DeprecatedInstant?> =
    map { it?.let { DeprecatedInstant.parse(it) } }

/**
 * Converts values in this [String] column to the deprecated [kotlinx.datetime.Instant].
 *
 * Migrate to [kotlin.time.Instant] and use [convertToStdlibInstant] at your own pace.
 *
 * @return A new [DataColumn] with the [kotlinx.datetime.Instant] values.
 */
@Deprecated(
    message = CONVERT_TO_DEPRECATED_INSTANT,
    replaceWith = ReplaceWith(CONVERT_TO_DEPRECATED_INSTANT_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun DataColumn<String>.convertToDeprecatedInstant(): DataColumn<DeprecatedInstant> =
    map { DeprecatedInstant.parse(it) }

/**
 * Converts values in this [String] column to the deprecated [kotlinx.datetime.Instant]. Preserves null values.
 *
 * Migrate to [kotlin.time.Instant] and use [convertToStdlibInstant] at your own pace.
 *
 * @return A new [DataColumn] with the nullable [kotlinx.datetime.Instant] values.
 */
@Deprecated(
    message = CONVERT_TO_DEPRECATED_INSTANT,
    replaceWith = ReplaceWith(CONVERT_TO_DEPRECATED_INSTANT_REPLACE),
    level = DeprecationLevel.WARNING,
)
@JvmName("convertToDeprecatedInstantFromStringNullable")
public fun DataColumn<String?>.convertToDeprecatedInstant(): DataColumn<DeprecatedInstant?> =
    map { it?.let { DeprecatedInstant.parse(it) } }

/**
 * Converts values in this [String] column to [kotlin.time.Instant].
 *
 * This function will be renamed to `.convertToInstant()` in 1.1.
 *
 * @return A new [DataColumn] with the [kotlin.time.Instant] values.
 */
public fun DataColumn<String>.convertToStdlibInstant(): DataColumn<StdlibInstant> = map { StdlibInstant.parse(it) }

/**
 * Converts values in this [String] column to [kotlin.time.Instant]. Preserves null values.
 *
 * This function will be renamed to `.convertToInstant()` in 1.1.
 *
 * @return A new [DataColumn] with the [kotlin.time.Instant] nullable values.
 */
@JvmName("convertToStdlibInstantFromStringNullable")
public fun DataColumn<String?>.convertToStdlibInstant(): DataColumn<StdlibInstant?> =
    map { it?.let { StdlibInstant.parse(it) } }

/**
 * Converts values in this [kotlinx.datetime.Instant] column to [kotlin.time.Instant].
 *
 * @return A new [DataColumn] with the [kotlin.time.Instant] values.
 */
@JvmName("convertToStdlibInstantFromDeprecatedInstant")
public fun DataColumn<DeprecatedInstant>.convertToStdlibInstant(): DataColumn<StdlibInstant> =
    map { it.toStdlibInstant() }

/**
 * Converts values in this [kotlinx.datetime.Instant] column to [kotlin.time.Instant]. Preserves null values.
 *
 * @return A new [DataColumn] with the [kotlin.time.Instant] nullable values.
 */
@JvmName("convertToStdlibInstantFromDeprecatedInstantNullable")
public fun DataColumn<DeprecatedInstant?>.convertToStdlibInstant(): DataColumn<StdlibInstant?> =
    map { it?.toStdlibInstant() }

/**
 * __Deprecated__:
 *
 * [kotlinx.datetime.Instant] is deprecated in favor of [kotlin.time.Instant].
 * Either migrate to [kotlin.time.Instant] and use [toStdlibInstant] or use [toDeprecatedInstant].
 * This function will be migrated to [kotlin.time.Instant] in 1.1.
 */

@JvmName("toInstantFromStringNullable")
@Refine
@Converter(DeprecatedInstant::class, nullable = true)
@Interpretable("ToSpecificType")
@Deprecated(message = TO_INSTANT, replaceWith = ReplaceWith(TO_INSTANT_REPLACE), level = DeprecationLevel.ERROR)
public fun <T> Convert<T, String?>.toInstant(): DataFrame<T> = asColumn { it.convertToDeprecatedInstant() }

/**
 * __Deprecated__:
 *
 * [kotlinx.datetime.Instant] is deprecated in favor of [kotlin.time.Instant].
 * Either migrate to [kotlin.time.Instant] and use [toStdlibInstant] or use [toDeprecatedInstant].
 * This function will be migrated to [kotlin.time.Instant] in 1.1.
 */
@JvmName("toInstantFromString")
@Refine
@Converter(DeprecatedInstant::class, nullable = false)
@Interpretable("ToSpecificType")
@Deprecated(message = TO_INSTANT, replaceWith = ReplaceWith(TO_INSTANT_REPLACE), level = DeprecationLevel.ERROR)
public fun <T> Convert<T, String>.toInstant(): DataFrame<T> = asColumn { it.convertToDeprecatedInstant() }

/**
 * Converts values in the [String] columns previously selected with [convert] to [kotlinx.datetime.Instant],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toDeprecatedInstant()
 * ```
 *
 * Migrate to [kotlin.time.Instant] and use [convertToStdlibInstant] at your own pace.
 *
 * @return A new [DataFrame] with the values converted to [kotlinx.datetime.Instant].
 */
@JvmName("toDeprecatedInstantFromStringNullable")
@Refine
@Converter(DeprecatedInstant::class, nullable = true)
@Interpretable("ToSpecificType")
@Deprecated(
    message = TO_DEPRECATED_INSTANT,
    replaceWith = ReplaceWith(TO_DEPRECATED_INSTANT_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun <T> Convert<T, String?>.toDeprecatedInstant(): DataFrame<T> = asColumn { it.convertToDeprecatedInstant() }

/**
 * Converts values in the [String] columns previously selected with [convert] to [kotlinx.datetime.Instant],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toDeprecatedInstant()
 * ```
 *
 * Migrate to [kotlin.time.Instant] and use [convertToStdlibInstant] at your own pace.
 *
 * @return A new [DataFrame] with the values converted to [kotlinx.datetime.Instant].
 */
@JvmName("toDeprecatedInstantFromString")
@Refine
@Converter(DeprecatedInstant::class, nullable = false)
@Interpretable("ToSpecificType")
@Deprecated(
    message = TO_DEPRECATED_INSTANT,
    replaceWith = ReplaceWith(TO_DEPRECATED_INSTANT_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun <T> Convert<T, String>.toDeprecatedInstant(): DataFrame<T> = asColumn { it.convertToDeprecatedInstant() }

/**
 * Converts values in the [String] columns previously selected with [convert] to [kotlin.time.Instant],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toStdlibInstant()
 * ```
 *
 * This function will be renamed to `.toInstant()` in 1.1.
 *
 * @return A new [DataFrame] with the values converted to [kotlin.time.Instant].
 */
@JvmName("toStdlibInstantFromStringNullable")
@Refine
@Converter(StdlibInstant::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String?>.toStdlibInstant(): DataFrame<T> = asColumn { it.convertToStdlibInstant() }

/**
 * Converts values in the [String] columns previously selected with [convert] to [kotlin.time.Instant],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toStdlibInstant()
 * ```
 *
 * This function will be renamed to `.toInstant()` in 1.1.
 *
 * @return A new [DataFrame] with the values converted to [kotlin.time.Instant].
 */
@JvmName("toStdlibInstantFromString")
@Refine
@Converter(StdlibInstant::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String>.toStdlibInstant(): DataFrame<T> = asColumn { it.convertToStdlibInstant() }

/**
 * Converts values in the [kotlinx.datetime.Instant] columns previously selected with [convert] to [kotlin.time.Instant],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toStdlibInstant()
 * ```
 *
 * @return A new [DataFrame] with the values converted to [kotlin.time.Instant].
 */
@JvmName("toStdlibInstantFromDeprecatedInstantNullable")
@Refine
@Converter(StdlibInstant::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DeprecatedInstant?>.toStdlibInstant(): DataFrame<T> = asColumn { it.convertToStdlibInstant() }

/**
 * Converts values in the [kotlinx.datetime.Instant] columns previously selected with [convert] to the [kotlin.time.Instant],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toStdlibInstant()
 * ```
 *
 * @return A new [DataFrame] with the values converted to [kotlin.time.Instant].
 */
@JvmName("toStdlibInstantFromDeprecatedInstant")
@Refine
@Converter(StdlibInstant::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DeprecatedInstant>.toStdlibInstant(): DataFrame<T> = asColumn { it.convertToStdlibInstant() }
// endregion

// region toLocalDate

/**
 * Converts values in this [Long] column to [LocalDate].
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDate] values.
 */
@JvmName("convertToLocalDateFromLong")
public fun DataColumn<Long>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> =
    map { it.toLocalDate(zone) }

/**
 * Converts values in this [Long] column to [LocalDate]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDate] nullable values.
 */
public fun DataColumn<Long?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> =
    map { it?.toLocalDate(zone) }

/**
 * Converts values in this [Int] column to [LocalDate].
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDate] values.
 */
@JvmName("convertToLocalDateFromInt")
public fun DataColumn<Int>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> =
    map { it.toLong().toLocalDate(zone) }

/**
 * Converts values in this [Int] column to [LocalDate]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDate] nullable values.
 */
@JvmName("convertToLocalDateFromIntNullable")
public fun DataColumn<Int?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> =
    map { it?.toLong()?.toLocalDate(zone) }

/**
 * Converts values in this [String] column to [LocalDate].
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataColumn] with the [LocalDate] values.
 */
@JvmName("convertToLocalDateFromString")
public fun DataColumn<String>.convertToLocalDate(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDate> {
    val converter = Parsers.getDateTimeConverter(LocalDate::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalDate") }
}

/**
 * Converts values in this [String] column to [LocalDate].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataColumn] with the [LocalDate] nullable values.
 */
@JvmName("convertToLocalDateFromStringNullable")
public fun DataColumn<String?>.convertToLocalDate(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDate?> {
    val converter = Parsers.getDateTimeConverter(LocalDate::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalDate") } }
}

/**
 * Converts values in the [Long] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDate].
 */
@JvmName("toLocalDateFromTLongNullable")
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDate(zone) }

/**
 * Converts values in the [Long] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDate].
 */
@JvmName("toLocalDateFromTLong")
@Refine
@Converter(LocalDate::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDate(zone) }

/**
 * Converts values in the [Int] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDate].
 */
@JvmName("toLocalDateFromTInt")
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDate(zone) }

/**
 * Converts values in the [Int] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a date. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDate].
 */
@JvmName("toLocalDateFromTIntNullable")
@Refine
@Converter(LocalDate::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDate(zone) }

/**
 * Converts values in the [String] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataFrame] with the values converted to [LocalDate].
 */
@JvmName("toLocalDateFromStringNullable")
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalDate(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToLocalDate(pattern, locale) }

/**
 * Converts values in the [String] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataFrame] with the values converted to [LocalDate].
 */
@JvmName("toLocalDateFromString")
@Refine
@Converter(LocalDate::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toLocalDate(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToLocalDate(pattern, locale) }

/**
 * Converts values in the columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @return A new [DataFrame] with the values converted to [LocalDate].
 */
@Refine
@Converter(LocalDate::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalDate(): DataFrame<T> = asColumn { it.convertTo<LocalDate>() }

// endregion

// region toLocalTime

/**
 * Converts values in this [Long] column to [LocalTime].
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalTime] values.
 */
@JvmName("convertToLocalTimeFromLong")
public fun DataColumn<Long>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> =
    map { it.toLocalTime(zone) }

/**
 * Converts values in this [Long] column to [LocalTime]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalTime] nullable values.
 */
public fun DataColumn<Long?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> =
    map { it?.toLocalTime(zone) }

/**
 * Converts values in this [Int] column to [LocalTime].
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalTime] values.
 */
@JvmName("convertToLocalTimeFromInt")
public fun DataColumn<Int>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> =
    map { it.toLong().toLocalTime(zone) }

/**
 * Converts values in this [Int] column to [LocalTime]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalTime] nullable values.
 */
@JvmName("convertToLocalTimeIntNullable")
public fun DataColumn<Int?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> =
    map { it?.toLong()?.toLocalTime(zone) }

/**
 * Converts values in this [String] column to [LocalTime].
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataColumn] with the [LocalTime] values.
 */
@JvmName("convertToLocalTimeFromString")
public fun DataColumn<String>.convertToLocalTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalTime> {
    val converter = Parsers.getDateTimeConverter(LocalTime::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalTime") }
}

/**
 * Converts values in this [String] column to [LocalTime].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataColumn] with the [LocalTime] nullable values.
 */
@JvmName("convertToLocalTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalTime?> {
    val converter = Parsers.getDateTimeConverter(LocalTime::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalTime") } }
}

/**
 * Converts values in the [Long] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalTime].
 */
@JvmName("toLocalTimeFromTLongNullable")
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalTime(zone) }

/**
 * Converts values in the [Long] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalTime].
 */
@JvmName("toLocalTimeFromTLong")
@Refine
@Converter(LocalTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalTime(zone) }

/**
 * Converts values in the [Int] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalTime].
 */
@JvmName("toLocalTimeFromTIntNullable")
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalTime(zone) }

/**
 * Converts values in the [Int] columns previously selected with [convert] to the [LocalDate],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalTime].
 */
@JvmName("toLocalTimeFromTInt")
@Refine
@Converter(LocalTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalTime(zone) }

/**
 * Converts values in the [String] columns previously selected with [convert] to the [LocalTime],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataFrame] with the values converted to [LocalTime].
 */
@JvmName("toLocalTimeFromStringNullable")
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToLocalTime(pattern, locale) }

/**
 * Converts values in the [String] columns previously selected with [convert] to the [LocalTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataFrame] with the values converted to [LocalTime].
 */
@JvmName("toLocalTimeFromString")
@Refine
@Converter(LocalTime::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toLocalTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToLocalTime(pattern, locale) }

/**
 * Converts values in the columns previously selected with [convert] to the [LocalTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @return A new [DataFrame] with the values converted to [LocalTime].
 */
@Refine
@Converter(LocalTime::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalTime(): DataFrame<T> = asColumn { it.convertTo<LocalTime>() }

// endregion

// region toLocalDateTime

/**
 * Converts values in this [Long] column to [LocalDateTime].
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDate] values.
 */
@JvmName("convertToLocalDateTimeFromLong")
public fun DataColumn<Long>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> =
    map { it.toLocalDateTime(zone) }

/**
 * Converts values in this [Long] column to [LocalDateTime]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDateTime] nullable values.
 */
public fun DataColumn<Long?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> =
    map { it?.toLocalDateTime(zone) }

/**
 * Converts values in this [kotlinx.datetime.Instant] column to [LocalDateTime].
 *
 * @param zone The [TimeZone] used to interpret the [kotlinx.datetime.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromDeprecatedInstant")
public fun DataColumn<DeprecatedInstant>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime> = map { it.toLocalDateTime(zone) }

/**
 * Converts values in this [kotlinx.datetime.Instant] column to [LocalDateTime]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [kotlinx.datetime.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromDeprecatedInstantNullable")
public fun DataColumn<DeprecatedInstant?>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime?> = map { it?.toLocalDateTime(zone) }

/**
 * Converts values in this [kotlin.time.Instant] column to [LocalDateTime].
 *
 * @param zone The [TimeZone] used to interpret the [kotlin.time.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromStdlibInstant")
public fun DataColumn<StdlibInstant>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime> = map { it.toLocalDateTime(zone) }

/**
 * Converts values in this [kotlin.time.Instant] column to [LocalDateTime]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [kotlin.time.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromStdlibInstantNullable")
public fun DataColumn<StdlibInstant?>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime?> = map { it?.toLocalDateTime(zone) }

/**
 * Converts values in this [Int] column to [LocalDateTime].
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromInt")
public fun DataColumn<Int>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> =
    map { it.toLong().toLocalDateTime(zone) }

/**
 * Converts values in this [Int] column to [LocalDateTime]. Preserves null values.
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [DataColumn] with the [LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromIntNullable")
public fun DataColumn<Int?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> =
    map { it?.toLong()?.toLocalDateTime(zone) }

/**
 * Converts values in this [String] column to [LocalDateTime].
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataColumn] with the [LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromString")
public fun DataColumn<String>.convertToLocalDateTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDateTime> {
    val converter = Parsers.getDateTimeConverter(LocalDateTime::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalDateTime") }
}

/**
 * Converts values in this [String] column to [LocalDateTime].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date pattern. If `null`, the system locale is used.
 * @return A new [DataColumn] with the [LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalDateTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDateTime?> {
    val converter = Parsers.getDateTimeConverter(LocalDateTime::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalDateTime") } }
}

/**
 * Converts values in the [Long] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTLongNullable")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [Long] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTLong")
@Refine
@Converter(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [kotlinx.datetime.Instant] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [kotlinx.datetime.Instant] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTDeprecatedInstantNullable")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, DeprecatedInstant?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [kotlinx.datetime.Instant] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [kotlinx.datetime.Instant] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTDeprecatedInstant")
@Refine
@Converter(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, DeprecatedInstant>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [kotlin.time.Instant] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [kotlin.time.Instant] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTStdlibInstantNullable")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, StdlibInstant?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [kotlin.time.Instant] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [kotlin.time.Instant] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTStdlibInstant")
@Refine
@Converter(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, StdlibInstant>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [Int] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTIntNullable")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [Int] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [TimeZone] used to interpret the [Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTInt")
@Refine
@Converter(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [String] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date-time pattern. If `null`, the system locale is used.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromStringNullable")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalDateTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(pattern, locale) }

/**
 * Converts values in the [String] columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * Trims each string and attempts to parse it using the specified [pattern] and [locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @param locale An optional [Locale] to interpret the date-time pattern. If `null`, the system locale is used.
 * @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@JvmName("toLocalDateTimeFromString")
@Refine
@Converter(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toLocalDateTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(pattern, locale) }

/**
 * Converts values in the columns previously selected with [convert] to the [LocalDateTime],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [LocalDateTime].
 */
@Refine
@Converter(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalDateTime(): DataFrame<T> = asColumn { it.convertTo<LocalDateTime>() }

// endregion

/**
 * Converts values in the columns previously selected with [convert] to the [Int],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toInt()
 * df.convert { colsOf<Double>() }.toInt()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Int].
 */
@JvmName("toIntTAny")
@Refine
@Converter(Int::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toInt(): DataFrame<T> = to<Int>()

/**
 * Converts values in the columns previously selected with [convert] to the [Int],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toInt()
 * df.convert { colsOf<Double>() }.toInt()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Int].
 */
@Refine
@Converter(Int::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toInt(): DataFrame<T> = to<Int?>()

/**
 * Converts values in the columns previously selected with [convert] to the [Long],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toLong()
 * df.convert { colsOf<Double>() }.toLong()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Long].
 */
@JvmName("toLongTAny")
@Refine
@Converter(Long::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toLong(): DataFrame<T> = to<Long>()

/**
 * Converts values in the columns previously selected with [convert] to the [Long],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toLong()
 * df.convert { colsOf<Double>() }.toLong()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Long].
 */
@Refine
@Converter(Long::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toLong(): DataFrame<T> = to<Long?>()

/**
 * Converts values in the columns previously selected with [convert] to the [String],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toStr()
 * df.convert { colsOf<Double>() }.toStr()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [String].
 */
@JvmName("toStrTAny")
@Refine
@Converter(String::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toStr(): DataFrame<T> = to<String>()

/**
 * Converts values in the columns previously selected with [convert] to the [String],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toStr()
 * df.convert { colsOf<Double>() }.toStr()
 * ```
 *
 * @return A new [DataFrame] with the values converted to [String].
 */
@Refine
@Converter(String::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toStr(): DataFrame<T> = to<String?>()

/**
 * Converts values in the columns previously selected with [convert] to the [Double],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toDouble()
 * df.convert { colsOf<Int>() }.toDouble()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Double].
 */
@JvmName("toDoubleTAny")
@Refine
@Converter(Double::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toDouble(): DataFrame<T> = to<Double>()

/**
 * Converts values in the columns previously selected with [convert] to the [Double],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toDouble()
 * df.convert { colsOf<Number?>() }.toDouble()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Double].
 */
@Refine
@Converter(Double::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toDouble(): DataFrame<T> = to<Double?>()

/**
 * Converts values in the columns previously selected with [convert] to the [Float],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toFloat()
 * df.convert { colsOf<Double>() }.toFloat()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Float].
 */
@JvmName("toFloatTAny")
@Refine
@Converter(Float::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toFloat(): DataFrame<T> = to<Float>()

/**
 * Converts values in the columns previously selected with [convert] to the [Float],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toFloat()
 * df.convert { colsOf<Double>() }.toFloat()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Float].
 */
@Refine
@Converter(Float::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toFloat(): DataFrame<T> = to<Float?>()

/**
 * Converts values in the columns previously selected with [convert] to the [BigDecimal],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toBigDecimal()
 * df.convert { colsOf<Double>() }.toBigDecimal()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [BigDecimal].
 */
@JvmName("toBigDecimalTAny")
@Refine
@Converter(BigDecimal::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toBigDecimal(): DataFrame<T> = to<BigDecimal>()

/**
 * Converts values in the columns previously selected with [convert] to the [BigDecimal],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toBigDecimal()
 * df.convert { colsOf<Double>() }.toBigDecimal()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [BigDecimal].
 */
@Refine
@Converter(BigDecimal::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBigDecimal(): DataFrame<T> = to<BigDecimal?>()

/**
 * Converts values in the columns previously selected with [convert] to the [BigInteger],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toBigInteger()
 * df.convert { colsOf<Double>() }.toBigInteger()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [BigInteger].
 */
@JvmName("toBigIntegerTAny")
@Refine
@Converter(BigInteger::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toBigInteger(): DataFrame<T> = to<BigInteger>()

/**
 * Converts values in the columns previously selected with [convert] to the [BigInteger],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toBigInteger()
 * df.convert { colsOf<Double?>() }.toBigInteger()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [BigInteger].
 */
@Refine
@Converter(BigInteger::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBigInteger(): DataFrame<T> = to<BigInteger?>()

/**
 * Converts values in the columns previously selected with [convert] to the [Boolean],
 * preserving their original names and positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { isMarked and isFinished }.toBoolean()
 * df.convert { colsOf<String> { it.name.startsWith("it") } }.toBoolean()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Boolean].
 */
@JvmName("toBooleanTAny")
@Refine
@Converter(Boolean::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toBoolean(): DataFrame<T> = to<Boolean>()

/**
 * Converts values in the columns previously selected with [convert] to the [Boolean],
 * preserving their original names and positions within the [DataFrame].
 * Preserves null values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { isMarked and isFinished }.toBoolean()
 * df.convert { colsOf<String?> { it.name.startsWith("it") } }.toBoolean()
 * ```
 *
 *  @return A new [DataFrame] with the values converted to [Boolean].
 */
@Refine
@Converter(Boolean::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBoolean(): DataFrame<T> = to<Boolean?>()

/**
 * Converts a list of lists values in the columns previously selected with [convert] to the [DataFrame],
 * preserving their original names and positions within the [DataFrame].
 *
 * By default, treats the first inner list as a header (column names), and the remaining lists as rows.
 * If [containsColumns] is `true`, interprets each inner list as a column,
 * where the first element is used as the column name, and the remaining elements as values.
 *
 * For more information: {@include [DocumentationUrls.Convert]}
 *
 * ### Examples:
 * ```kotlin
 * df.convert { userData }.toDataFrames()
 * df.convert { colsOf<List<List<*>>>() }.toDataFrames(containsColumns = true)
 * ```
 *
 * @param containsColumns If `true`, treats each nested list as a column with its first element as the column name.
 *                        Otherwise, the first list is treated as the header.
 *                        Defaults to `false`.
 *  @return A new [DataFrame] with the values converted to [DataFrame].
 */
public fun <T, C> Convert<T, List<List<C>>>.toDataFrames(containsColumns: Boolean = false): DataFrame<T> =
    asColumn { it.toDataFrames(containsColumns) }

/**
 * Converts a list of lists values in this [DataColumn] to the [DataFrame].
 *
 * By default, treats the first inner list as a header (column names), and the remaining lists as rows.
 * If [containsColumns] is `true`, interprets each inner list as a column,
 * where the first element is used as the column name, and the remaining elements as values.
 *
 * @param containsColumns If `true`, treats each nested list as a column with its first element as the column name.
 *                        Otherwise, the first list is treated as the header.
 *                        Defaults to `false`.
 *  @return A new [DataColumn] with the values converted to [DataFrame].
 */
public fun <T> DataColumn<List<List<T>>>.toDataFrames(containsColumns: Boolean = false): DataColumn<AnyFrame> =
    map { it.toDataFrame(containsColumns) }
