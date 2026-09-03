package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toStdlibInstant
import org.jetbrains.kotlinx.dataframe.AnyCol
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
import org.jetbrains.kotlinx.dataframe.impl.api.convertRowColumnImpl
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
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_KOTLIN_DATETIME_LOCALE
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_URL
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_URL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.CONVERT_TO_WITHOUT_PARSER_OPTIONS
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
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import java.time.Duration as JavaDuration
import java.time.Instant as JavaInstant
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

/**
 * See also [<code>parse</code>][parse] — a specialized form of the [<code>convert</code>][convert] operation that parses [<code>String</code>][String] columns
 * into other types without requiring explicit type specification.
 */
internal typealias SeeAlsoParse = Nothing

/**
 * Converts the values in the specified [columns] either to a supported target type
 * or using a custom converter, keeping their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * This function does not immediately convert the columns but instead selects columns to convert and
 * returns a [<code>Convert</code>][Convert],
 * which serves as an intermediate step.
 * The [<code>Convert</code>][Convert] object provides methods to transform selected columns using:
 * - [<code>to</code>][Convert.to]
 * - [<code>with</code>][Convert.with]
 * - [<code>asFrame</code>][Convert.asFrame]
 * - [<code>perRowCol</code>][Convert.perRowCol]
 * - [<code>notNull</code>][Convert.notNull]
 * - [<code>toDataFrames</code>][Convert.toDataFrames]
 *
 * Additionally, it offers a wide range of methods for converting to specific types,
 * such as [<code>toStr</code>][Convert.toStr], [<code>toDouble</code>][Convert.toDouble], and many others.
 *
 * For the full list of supported types, see [<code>SupportedTypes</code>][SupportedTypes].
 *
 * Each method returns a new [<code>DataFrame</code>][DataFrame] with the updated columns.
 *
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][ConvertSelectingOptions].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * See also [<code>parse</code>][org.jetbrains.kotlinx.dataframe.api.parse] — a specialized form of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation that parses [<code>String</code>][String] columns
 * into other types without requiring explicit type specification.
 */
internal interface ConvertDocs {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert]`("length", "age")`
     *
     *
     *
     */
    typealias ConvertSelectingOptions = Nothing

    /**
     * List of types, supported in [<code>convert to</code>][Convert.to] operation:
     * * [<code>String</code>][String] (uses parse to convert from String to other types);
     * * [<code>Boolean</code>][Boolean];
     * * [<code>Byte</code>][Byte], [<code>Short</code>][Short], [<code>Char</code>][Char];
     * * [<code>Int</code>][Int], [<code>Long</code>][Long], [<code>Float</code>][Float], [<code>Double</code>][Double];
     * * [<code>BigDecimal</code>][BigDecimal], [<code>BigInteger</code>][BigInteger];
     * * [<code>LocalDateTime</code>][LocalDateTime], [<code>LocalDate</code>][LocalDate], [<code>LocalTime</code>][LocalTime],
     *   `Instant` ([<code>kotlinx.datetime</code>][DeprecatedInstant], [<code>kotlin.time</code>][StdlibInstant], and [<code>java.time</code>][java.time]),
     * * [<code>URL</code>][URL], [<code>IMG</code>][IMG], [<code>IFRAME</code>][IFRAME].
     *
     * __NOTE__: Conversion between [<code>Int</code>][Int] and [<code>Char</code>][Char] is done by UTF-16 [<code>Char.code</code>][Char.code].
     *   To convert [<code>Char</code>][Char]->[<code>Int</code>][Int] the way it is written, use [<code>parse()</code>][parse] instead, or,
     *   in either case, use [<code>String</code>][String] as intermediary type.
     */
    typealias SupportedTypes = Nothing

    /**
     * ## Convert Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * **[<code>`convert`</code>][DataFrame.convert]**`  { columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`with`**</code>][Convert.with]`(infer: `[<code>`Infer`</code>][Infer]`, rowExpression: `[<code>`RowValueExpression`</code>][RowValueExpression]`)`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`notNull`**</code>][Convert.notNull]`  { rowExpression: `[<code>`RowValueExpression`</code>][RowValueExpression]`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`to`**</code>][Convert.to]`<T>()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`to`**</code>][Convert.to]`(type: `[<code>`KType`</code>][KType]`)`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perRowCol`**</code>][Convert.perRowCol]`  { expression: `[<code>`RowColumnExpression`</code>][RowColumnExpression]`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`asFrame`**</code>][Convert.asFrame]`  { builder: `[`ColumnsContainer`](ColumnsContainer)`.(`[`ColumnConvert`](ColumnConvert)`) -> `[`DataFrame`](DataFrame)`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toStr`**</code>][Convert.toStr]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toInt`**</code>][Convert.toInt]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toLong`**</code>][Convert.toLong]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toDouble`**</code>][Convert.toDouble]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toFloat`**</code>][Convert.toFloat]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toBigDecimal`**</code>][Convert.toBigDecimal]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toBigInteger`**</code>][Convert.toBigInteger]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toBoolean`**</code>][Convert.toBoolean]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toLocalDateTime`**</code>][Convert.toLocalDateTime]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toLocalDate`**</code>][Convert.toLocalDate]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toLocalTime`**</code>][Convert.toLocalTime]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toInstant`**</code>][Convert.toInstant]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toUtcOffset`**</code>][Convert.toUtcOffset]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toYearMonth`**</code>][Convert.toYearMonth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toDateTimeComponents`**</code>][Convert.toDateTimeComponents]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toDuration`**</code>][Convert.toDuration]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toJavaLocalDateTime`**</code>][Convert.toJavaLocalDateTime]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toJavaLocalDate`**</code>][Convert.toJavaLocalDate]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toJavaLocalTime`**</code>][Convert.toJavaLocalTime]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toJavaInstant`**</code>][Convert.toJavaInstant]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toJavaDuration`**</code>][Convert.toJavaDuration]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toUrl`**</code>][Convert.toUrl]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toIFrame`**</code>][Convert.toIFrame]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toImg`**</code>][Convert.toImg]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toDataFrames`**</code>][Convert.toDataFrames]`()`
     */
    typealias Grammar = Nothing
}

/**
 * Converts the values in the specified [columns] either to a supported target type
 * or using a custom converter, keeping their original names and positions within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately convert the columns but instead selects columns to convert and
 * returns a [<code>Convert</code>][org.jetbrains.kotlinx.dataframe.api.Convert],
 * which serves as an intermediate step.
 * The [<code>Convert</code>][org.jetbrains.kotlinx.dataframe.api.Convert] object provides methods to transform selected columns using:
 * - [<code>to</code>][org.jetbrains.kotlinx.dataframe.api.Convert.to]
 * - [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with]
 * - [<code>asFrame</code>][org.jetbrains.kotlinx.dataframe.api.Convert.asFrame]
 * - [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.Convert.perRowCol]
 * - [<code>notNull</code>][org.jetbrains.kotlinx.dataframe.api.Convert.notNull]
 * - [<code>toDataFrames</code>][org.jetbrains.kotlinx.dataframe.api.Convert.toDataFrames]
 *
 * Additionally, it offers a wide range of methods for converting to specific types,
 * such as [<code>toStr</code>][org.jetbrains.kotlinx.dataframe.api.Convert.toStr], [<code>toDouble</code>][org.jetbrains.kotlinx.dataframe.api.Convert.toDouble], and many others.
 *
 * For the full list of supported types, see [<code>SupportedTypes</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.SupportedTypes].
 *
 * Each method returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the updated columns.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.ConvertSelectingOptions].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * See also [<code>parse</code>][org.jetbrains.kotlinx.dataframe.api.parse] — a specialized form of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation that parses [<code>String</code>][String] columns
 * into other types without requiring explicit type specification.
 * ### This Convert Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Examples:
 * ```kotlin
 * df.convert { columnA and columnB }.with { it.toString().lowercase() }
 * df.convert { colsOf<String>() }.to<Double>()
 * df.convert { colsAtAnyDepth().colGroups() }.asFrame { it.add("nan") { Double.NaN } }
 * ```
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to convert.
 */
@Interpretable("Convert0")
public fun <T, C> DataFrame<T>.convert(columns: ColumnsSelector<T, C>): Convert<T, C> = Convert(this, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.convert(vararg columns: KProperty<C>): Convert<T, C> = convert { columns.toColumnSet() }

/**
 * Converts the values in the specified [columns] either to a supported target type
 * or using a custom converter, keeping their original names and positions within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately convert the columns but instead selects columns to convert and
 * returns a [<code>Convert</code>][org.jetbrains.kotlinx.dataframe.api.Convert],
 * which serves as an intermediate step.
 * The [<code>Convert</code>][org.jetbrains.kotlinx.dataframe.api.Convert] object provides methods to transform selected columns using:
 * - [<code>to</code>][org.jetbrains.kotlinx.dataframe.api.Convert.to]
 * - [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with]
 * - [<code>asFrame</code>][org.jetbrains.kotlinx.dataframe.api.Convert.asFrame]
 * - [<code>perRowCol</code>][org.jetbrains.kotlinx.dataframe.api.Convert.perRowCol]
 * - [<code>notNull</code>][org.jetbrains.kotlinx.dataframe.api.Convert.notNull]
 * - [<code>toDataFrames</code>][org.jetbrains.kotlinx.dataframe.api.Convert.toDataFrames]
 *
 * Additionally, it offers a wide range of methods for converting to specific types,
 * such as [<code>toStr</code>][org.jetbrains.kotlinx.dataframe.api.Convert.toStr], [<code>toDouble</code>][org.jetbrains.kotlinx.dataframe.api.Convert.toDouble], and many others.
 *
 * For the full list of supported types, see [<code>SupportedTypes</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.SupportedTypes].
 *
 * Each method returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the updated columns.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.ConvertSelectingOptions].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * See also [<code>parse</code>][org.jetbrains.kotlinx.dataframe.api.parse] — a specialized form of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation that parses [<code>String</code>][String] columns
 * into other types without requiring explicit type specification.
 * ### This Convert Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example:
 * ```kotlin
 * df.convert("person", "position").toStr()
 * df.convert("value").with { (it as Number).toDouble() }
 * ```
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to convert.
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
 * using row converter [<code>expression</code>][expression] within the [<code>DataFrame</code>][DataFrame].
 * Provide a new value for every selected cell given its row and its previous value using a
 * [<code>row value expression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * Fore more information, [See RowValueExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowvalueexpression)
 * [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ## Note
 * [<code>update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]-,
 * [<code>convert with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with]-
 * and [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [<code>AddDataRow</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [<code>RowValueExpression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([<code>AddDataRow.newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 * ## See Also
 * - [<code>Convert per row col</code>][org.jetbrains.kotlinx.dataframe.api.Convert.perRowCol] to provide a new value for every selected cell giving its column.
 *
 * ### Example:
 * ```kotlin
 * // Convert values in selected column to a trimmed `String`.
 * df.convert("valueA", "valueB") { it.toString().trim() }
 * ```
 * @param [expression] The [<code>Row Value Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 * @return A new [<code>DataFrame</code>][DataFrame] with the converted values.
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
 * An intermediate class used in the [<code>convert</code>][convert] operation.
 *
 * This class itself does not perform any conversion — it is a transitional step
 * before specifying how to convert the selected columns.
 * It must be followed by one of the conversion methods
 * to produce a new [<code>DataFrame</code>][DataFrame] with updated column values and types.
 *
 * The resulting columns will keep their original names and positions
 * in the [<code>DataFrame</code>][DataFrame], but their values will be transformed.
 *
 * Use the following methods to perform the conversion:
 * - [<code>to(kType)</code>][to]/[<code>to`<Type`>()</code>][to] – converts columns to a specific type.
 * - [<code>asColumn { columnConverter }</code>][asColumn] - converts columns using column converter expression.
 * - [<code>with</code>][Convert.with] – applies a custom row-wise conversion expression.
 * - [<code>notNull</code>][Convert.notNull] – like [<code>with</code>][with], but only for non-null values.
 * - [<code>perRowCol</code>][Convert.perRowCol] – applies a conversion that uses both column and row information.
 * - [<code>asFrame</code>][Convert.asFrame] – converts [<code>column groups</code>][ColumnGroup] as a [<code>DataFrame</code>][DataFrame] with the given expression.
 * - [<code>toStr</code>][toStr], [<code>toInt</code>][toInt], [<code>toLong</code>][toLong], [<code>toDouble</code>][toDouble], [<code>toFloat</code>][toFloat], [<code>toBigDecimal</code>][toBigDecimal],
 *   [<code>toBigInteger</code>][toBigInteger], [<code>toBoolean</code>][toBoolean] – convert to standard types.
 * - [<code>toLocalDateTime</code>][toLocalDateTime], [<code>toLocalDate</code>][toLocalDate], [<code>toLocalTime</code>][toLocalTime] – convert to kotlinx.datetime types.
 * - [<code>toInstant</code>][toInstant] (temporarily deprecated), [<code>toStdlibInstant</code>][toStdlibInstant], [<code>toDeprecatedInstant</code>][toDeprecatedInstant] – convert to `Instant`
 * - [<code>toUrl</code>][toUrl], [<code>toIFrame</code>][toIFrame], [<code>toImg</code>][toImg] – convert to special types.
 * - [<code>toDataFrames</code>][toDataFrames] – converts a column of lists into separate DataFrames.
 *
 * See [<code>Grammar</code>][ConvertDocs.Grammar] for more details.
 *
 * [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 */
@HasSchema(schemaArg = 0)
public class Convert<T, out C>(
    @PublishedApi internal val df: DataFrame<T>,
    @PublishedApi internal val columns: ColumnsSelector<T, C>,
) {
    /**
     * Casts the type parameter of the columns previously selected with [<code>convert</code>][convert] to a new type [<code>R</code>][R],
     * without performing any actual data transformation.
     *
     * This operation updates the static type of the selected columns for further type-safe conversions.
     *
     * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
     */
    public fun <R> cast(): Convert<T, R> = Convert(df, columns as ColumnsSelector<T, R>)

    @Deprecated(CONVERT_TO_WITHOUT_PARSER_OPTIONS, level = DeprecationLevel.HIDDEN)
    @Refine
    @Interpretable("To0")
    public inline fun <reified D> to(): DataFrame<T> = to(typeOf<D>())

    /**
     * Converts the values in the columns previously selected with [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] to the specified [<code>type</code>][org.jetbrains.kotlinx.dataframe.api.type],
     * preserving their original names and positions within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * The target type is provided as reified argument.
     * For the full list of supported types, see [<code>SupportedTypes</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.SupportedTypes].
     *
     * Converting from [<code>String(?)</code>][String] columns is considered "parsing".
     * You can also provide [parserOptions] to customize the [<code>Locale</code>][Locale], date-time options, etc.
     * See [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions]. This argument is ignored for non-`String` columns.
     *
     * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
     *
     * ### Examples:
     * ```kotlin
     * // Convert selected columns to Int:
     * df.convert("year", "count").to<Int>()
     *
     * // Convert all String columns to LocalDate:
     * df.convert { colsOf<String>() }.to<LocalDate>()
     *
     * // Convert selected columns to Double with parser options:
     * df.convert("year", "count").to<Double>(ParserOptions(locale = Locale.GERMAN))
     *
     * // Converted selected column to Java LocalDate with custom date format:
     * df.convert { dates }.to<java.time.LocalDate>(ParserOptions(dateTime = JavaDateTimeParserOptions.withPattern("yyyy-MM-dd")))
     * ```
     *
     * @param [D] The target type, provided as a [<code>KType</code>][KType], to convert values to.
     * @param [parserOptions] The optional [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions] for parsing the [<code>String</code>][String] values.
     *   Will be ignored if provided for non-String columns.
     * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the values converted to [<code>type</code>][org.jetbrains.kotlinx.dataframe.api.type].
     *
     */
    @Suppress("UNCHECKED_CAST")
    @Refine
    @Interpretable("To0")
    public inline fun <reified D> to(parserOptions: ParserOptions? = null): DataFrame<T> =
        to(typeOf<D>(), parserOptions)

    override fun toString(): String = "Convert(df=$df, columns=$columns)"
}

/**
 * Converts the values in the columns previously selected with [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] to the specified [<code>type</code>][org.jetbrains.kotlinx.dataframe.api.type],
 * preserving their original names and positions within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * The target type is provided as reified argument.
 * For the full list of supported types, see [<code>SupportedTypes</code>][org.jetbrains.kotlinx.dataframe.api.ConvertDocs.SupportedTypes].
 *
 * Converting from [<code>String(?)</code>][String] columns is considered "parsing".
 * You can also provide [parserOptions] to customize the [<code>Locale</code>][Locale], date-time options, etc.
 * See [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions]. This argument is ignored for non-`String` columns.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * // Convert selected columns to Int:
 * df.convert("year", "count").to(typeOf<Int>())
 *
 * // Convert all String columns to LocalDate:
 * df.convert { colsOf<String>() }.to(typeOf<LocalDate>())
 *
 * // Convert selected columns to Double with parser options:
 * df.convert("year", "count").to(typeOf<Double>(), ParserOptions(locale = Locale.GERMAN))
 *
 * // Converted selected column to Java LocalDate with custom date format:
 * df.convert { dates }.to(typeOf<java.time.LocalDate>(), ParserOptions(dateTime = JavaDateTimeParserOptions.withPattern("yyyy-MM-dd")))
 * ```
 *
 * @param [type] The target type, provided as a [<code>KType</code>][KType], to convert values to.
 * @param [parserOptions] The optional [<code>ParserOptions</code>][org.jetbrains.kotlinx.dataframe.api.ParserOptions] for parsing the [<code>String</code>][String] values.
 *   Will be ignored if provided for non-String columns.
 * @return A new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the values converted to [<code>type</code>][org.jetbrains.kotlinx.dataframe.api.type].
 *
 */
public fun <T> Convert<T, *>.to(type: KType, parserOptions: ParserOptions? = null): DataFrame<T> =
    asColumn { it.convertToTypeImpl(type, parserOptions) }

@Deprecated(CONVERT_TO, ReplaceWith(CONVERT_TO_REPLACE), DeprecationLevel.ERROR)
public fun <T, C> Convert<T, C>.to(columnConverter: DataFrame<T>.(DataColumn<C>) -> BaseColumn<*>): DataFrame<T> =
    df.replace(columns).with { columnConverter(df, it) }

/**
 * Converts values in columns previously selected by [<code>convert</code>][convert] using the specified [<code>rowConverter</code>][rowConverter],
 * a [<code>row value expression</code>][RowValueExpression] applied to each row in the [<code>DataFrame</code>][DataFrame].
 *
 * A [<code>row value expression</code>][RowValueExpression] allows you to provide a new value for every selected cell
 * given its row (as a receiver) and its previous value (as a lambda argument).
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ## Note
 * [<code>update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]-,
 * [<code>convert with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with]-
 * and [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [<code>AddDataRow</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [<code>RowValueExpression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([<code>AddDataRow.newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 * ## See Also
 * - [<code>Convert per row col</code>][org.jetbrains.kotlinx.dataframe.api.Convert.perRowCol] to provide a new value for every selected cell giving its column.
 * - [<code>Convert as column</code>][org.jetbrains.kotlinx.dataframe.api.Convert.asColumn] to convert using a column converter
 *
 * ### Examples:
 * ```kotlin
 * // Select columns with json values and convert it to decoded `String`.
 * df.convert { valueJson }.with { Json.decode(it) }
 * // Convert all `Int` columns to `Duration`, multiplying each value by the corresponding value from the "coeff" `Double` column before conversion
 * df.convert { colsOf<Int>() }.with { baseValue -> (baseValue * coeff).seconds }
 * ```
 *
 * @param infer [<code>Infer</code>][Infer] strategy that defines how the [<code>type</code>][DataColumn.type] of the resulting column should be determined.
 * Defaults to [<code>Infer.Nulls</code>][Infer.Nulls].
 * @param [rowConverter] The [<code>RowValueExpression</code>][RowValueExpression] to provide a new value for every selected cell giving its row and previous value.
 * @return A new [<code>DataFrame</code>][DataFrame] with the converted values.
 */
@Refine
@Interpretable("With0")
public inline fun <T, C, reified R> Convert<T, C>.with(
    infer: Infer = Infer.Nulls,
    noinline rowConverter: RowValueExpression<T, C, R>,
): DataFrame<T> = withRowCellImpl(typeOf<R>(), infer, rowConverter)

/**
 * Converts [<code>column groups</code>][ColumnGroup] previously selected with [<code>convert</code>][convert]
 * as a [<code>DataFrame</code>][DataFrame] using a [<code>dataframe expression</code>][DataFrameExpression].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Example:
 * ```kotlin
 * // Add a column to selected column group "name".
 * df.convert { name }.asFrame { it.add("fullName") { "$firstName $lastName" } }
 * ```
 *
 * @param [expression] The [<code>DataFrame Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression] to replace the selected column group with.
 */
@Refine
@Interpretable("ConvertAsFrame")
public fun <T, C, R> Convert<T, DataRow<C>>.asFrame(
    expression: ColumnsContainer<T>.(ColumnGroup<C>) -> DataFrame<R>,
): DataFrame<T> = asColumn { expression(this, it.asColumnGroup()).asColumnGroup(it.name()) }

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert]
 * using [<code>columnConverter</code>][columnConverter] expression within the [<code>DataFrame</code>][DataFrame].
 *
 * The [<code>columnConverter</code>][columnConverter] is a lambda with the current [<code>DataFrame</code>][DataFrame] as receiver and the selected column as argument.
 * It returns a new column that will replace the original column.
 * **Preserves original column name for each column (even it was explicitly changed in [<code>columnConverter</code>][columnConverter] expression).**
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * It's a compiler plugin-friendly variant of [<code>ReplaceClause.with</code>][ReplaceClause.with].
 * [<code>ReplaceClause.with</code>][ReplaceClause.with] allows you to change both column types and names.
 * Tracking of column name changes in arbitrary lambda expression is unreliable and generally impossible
 * to do statically.
 * This function ensures that all column names remain as is and only their type changes to [<code>R</code>][R]
 *
 * ## See Also
 *  - [<code>Convert with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with] to provide a new value for every selected cell
 * giving its row and its previous value.
 *  - [<code>Replace with</code>][ReplaceClause.with] to replace columns using a column converter,
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
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>type</code>][type].
 */
@Refine
@Interpretable("ConvertAsColumn")
public inline fun <T, C, R> Convert<T, C>.asColumn(
    crossinline columnConverter: DataFrame<T>.(DataColumn<C>) -> BaseColumn<R>,
): DataFrame<T> = df.replace(columns).with { columnConverter(df, it).rename(it.name()) }

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert]
 * using [<code>row column</code>][RowColumnExpression] [<code>expression</code>][expression] within the [<code>DataFrame</code>][DataFrame].
 *
 * A [<code>row column expression</code>][RowColumnExpression] allows you to provide a new value for every selected cell
 * given its row and column (as lambda arguments).
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ## See Also
 *  - [<code>Convert with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with] to provide a new value for every selected cell
 * giving its row and its previous value.
 *  - [<code>Convert as column</code>][org.jetbrains.kotlinx.dataframe.api.Convert.asColumn] to convert using a column converter
 *
 * ### Example:
 * ```kotlin
 * // Convert values in all columns to `String` and add their column name to the end
 * df.convert { all() }.perRowCol { row, col ->
 *    col[row].toString() + col.name()
 * }
 * ```
 *
 * @param infer [<code>Infer</code>][Infer] strategy that defines how the [<code>type</code>][DataColumn.type] of the resulting column should be determined.
 * Defaults to [<code>Infer.Nulls</code>][Infer.Nulls].
 * @param [expression] The [<code>RowColumnExpression</code>][RowColumnExpression] to provide a new value for every selected cell giving its row and column.
 */
@Refine
@Interpretable("PerRowCol")
public inline fun <T, C, reified R> Convert<T, C>.perRowCol(
    infer: Infer = Infer.Nulls,
    noinline expression: RowColumnExpression<T, C, R>,
): DataFrame<T> = convertRowColumnImpl(typeOf<R>(), infer, expression)

/**
 * Converts values in this column to the specified type [<code>C</code>][C].
 *
 * The target type is provided as a reified type argument.
 *
 * For the full list of supported types, see [<code>SupportedTypes</code>][ConvertDocs.SupportedTypes].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [C] The target type to convert values to.
 * @return A new [<code>DataColumn</code>][DataColumn] with the values converted to type [<code>C</code>][C].
 */
public inline fun <reified C> AnyCol.convertTo(): DataColumn<C> = convertTo(typeOf<C>()) as DataColumn<C>

/**
 * Converts values in this column to the specified [<code>type</code>][newType].
 *
 * For the full list of supported types, see [<code>SupportedTypes</code>][ConvertDocs.SupportedTypes].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [newType] The target type, provided as a [<code>KType</code>][KType], to convert values to.
 * @return A new [<code>DataColumn</code>][DataColumn] with the values converted to [<code>type</code>][type].
 */
@Suppress("UNCHECKED_CAST")
public fun AnyCol.convertTo(newType: KType): AnyCol = convertToTypeImpl(newType, null)

/**
 * Converts values in this `String` column to the specified type [<code>C</code>][C].
 *
 * The target type is provided as a reified type argument.
 *
 * See also [<code>parse</code>][org.jetbrains.kotlinx.dataframe.api.parse] — a specialized form of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation that parses [<code>String</code>][String] columns
 * into other types without requiring explicit type specification.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [C] The target type to convert values to.
 * @param [parserOptions] Optional [<code>ParserOptions</code>][ParserOptions] to customize parsing behavior (e.g., locale, null strings).
 * @return A new [<code>DataColumn</code>][DataColumn] with the values converted to type [<code>C</code>][C].
 */
public inline fun <reified C> DataColumn<String?>.convertTo(parserOptions: ParserOptions? = null): DataColumn<C> =
    convertTo(typeOf<C>(), parserOptions) as DataColumn<C>

/**
 * Converts values in this `String` column to the specified [<code>type</code>][newType].
 *
 * The target type is provided as a [<code>KType</code>][KType].
 *
 * See also [<code>parse</code>][org.jetbrains.kotlinx.dataframe.api.parse] — a specialized form of the [<code>convert</code>][org.jetbrains.kotlinx.dataframe.api.convert] operation that parses [<code>String</code>][String] columns
 * into other types without requiring explicit type specification.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [newType] The target type to convert values to.
 * @param [parserOptions] Optional [<code>ParserOptions</code>][ParserOptions] to customize parsing behavior (e.g., locale, null strings).
 * @return A new [<code>DataColumn</code>][DataColumn] with the values converted to [<code>type</code>][type].
 */
public fun DataColumn<String?>.convertTo(newType: KType, parserOptions: ParserOptions? = null): AnyCol =
    convertToTypeImpl(newType, parserOptions)

/**
 * Converts values in this column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromT")
public fun <T : Any> DataColumn<T>.convertToLocalDateTime(): DataColumn<LocalDateTime> = convertTo()

/**
 * Converts values in this column to [<code>LocalDateTime</code>][LocalDateTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLocalDateTime(): DataColumn<LocalDateTime?> = convertTo()

/**
 * Converts values in this column to [<code>LocalDate</code>][LocalDate].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] values.
 */
@JvmName("convertToLocalDateFromT")
public fun <T : Any> DataColumn<T>.convertToLocalDate(): DataColumn<LocalDate> = convertTo()

/**
 * Converts values in this column to [<code>LocalDate</code>][LocalDate]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLocalDate(): DataColumn<LocalDate?> = convertTo()

/**
 * Converts values in this column to [<code>LocalTime</code>][LocalTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] values.
 */
@JvmName("convertToLocalTimeFromT")
public fun <T : Any> DataColumn<T>.convertToLocalTime(): DataColumn<LocalTime> = convertTo()

/**
 * Converts values in this column to [<code>LocalTime</code>][LocalTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLocalTime(): DataColumn<LocalTime?> = convertTo()

/**
 * Converts values in this column to [<code>Byte</code>][Byte].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Byte</code>][Byte] values.
 */
@JvmName("convertToByteFromT")
public fun <T : Any> DataColumn<T>.convertToByte(): DataColumn<Byte> = convertTo()

/**
 * Converts values in this column to [<code>Byte</code>][Byte]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Byte</code>][Byte] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToByte(): DataColumn<Byte?> = convertTo()

/**
 * Converts values in this column to [<code>Short</code>][Short].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Short</code>][Short] values.
 */
@JvmName("convertToShortFromT")
public fun <T : Any> DataColumn<T>.convertToShort(): DataColumn<Short> = convertTo()

/**
 * Converts values in this column to [<code>Short</code>][Short]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Short</code>][Short] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToShort(): DataColumn<Short?> = convertTo()

/**
 * Converts values in this column to [<code>Int</code>][Int].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Int</code>][Int] values.
 */
@JvmName("convertToIntFromT")
public fun <T : Any> DataColumn<T>.convertToInt(): DataColumn<Int> = convertTo()

/**
 * Converts values in this column to [<code>Int</code>][Int]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Int</code>][Int] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToInt(): DataColumn<Int?> = convertTo()

/**
 * Converts values in this column to [<code>Long</code>][Long].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Long</code>][Long] values.
 */
@JvmName("convertToLongFromT")
public fun <T : Any> DataColumn<T>.convertToLong(): DataColumn<Long> = convertTo()

/**
 * Converts values in this column to [<code>Long</code>][Long]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Long</code>][Long] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToLong(): DataColumn<Long?> = convertTo()

/**
 * Converts values in this column to [<code>String</code>][String].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>String</code>][String] values.
 */
@JvmName("convertToStringFromT")
public fun <T : Any> DataColumn<T>.convertToString(): DataColumn<String> = convertTo()

/**
 * Converts values in this column to [<code>String</code>][String]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>String</code>][String] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToString(): DataColumn<String?> = convertTo()

/**
 * Converts values in this column to [<code>Double</code>][Double].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Double</code>][Double] values.
 */
@JvmName("convertToDoubleFromT")
public fun <T : Any> DataColumn<T>.convertToDouble(): DataColumn<Double> = convertTo()

/**
 * Converts values in this column to [<code>Double</code>][Double]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Double</code>][Double] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToDouble(): DataColumn<Double?> = convertTo()

/** Converts values in this [<code>String</code>][String] column to [<code>Double</code>][Double] considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 *   If the column cannot be parsed, the POSIX format is used.
 *
 * @return A new [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with the [<code>Double</code>][Double] values. */
@JvmName("convertToDoubleFromString")
public fun DataColumn<String>.convertToDouble(locale: Locale? = null): DataColumn<Double> =
    convertToDouble(locale = locale, nullStrings = null, useFastDoubleParser = null)

/**
 * Converts values in this [<code>String</code>][String] column to [<code>Double</code>][Double] considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 *   If the column cannot be parsed, the POSIX format is used.
 *
 * @return A new [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with the [<code>Double</code>][Double] values.
 * @param nullStrings a set of strings that should be treated as `null` values.
 *   The default in [<code>DataFrame.parser</code>][DataFrame.Companion.parser] is [<code>"null", "NULL", "NA", "N/A"</code>]["null", "NULL", "NA", "N/A"].
 * @param useFastDoubleParser whether to use [<code>FastDoubleParser</code>][FastDoubleParser].
 *   The default in [<code>DataFrame.parser</code>][DataFrame.Companion.parser] is `true`.
 */
@JvmName("convertToDoubleFromString")
public fun DataColumn<String>.convertToDouble(
    locale: Locale? = null,
    nullStrings: Set<String>?,
    useFastDoubleParser: Boolean?,
): DataColumn<Double> =
    this.castToNullable().convertToDouble(locale, nullStrings, useFastDoubleParser).castToNotNullable()

/** Converts values in this [<code>String</code>][String] column to [<code>Double</code>][Double] considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 *   If the column cannot be parsed, the POSIX format is used.
 *
 * @return A new [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with the [<code>Double</code>][Double] values. */
@JvmName("convertToDoubleFromStringNullable")
public fun DataColumn<String?>.convertToDouble(locale: Locale? = null): DataColumn<Double?> =
    convertToDouble(locale = locale, nullStrings = null, useFastDoubleParser = null)

/**
 * Converts values in this [<code>String</code>][String] column to [<code>Double</code>][Double] considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [<code>DataFrame.parser</code>][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 *   If the column cannot be parsed, the POSIX format is used.
 *
 * @return A new [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] with the [<code>Double</code>][Double] values.
 * @param nullStrings a set of strings that should be treated as `null` values.
 *   The default in [<code>DataFrame.parser</code>][DataFrame.Companion.parser] is [<code>"null", "NULL", "NA", "N/A"</code>]["null", "NULL", "NA", "N/A"].
 * @param useFastDoubleParser whether to use [<code>FastDoubleParser</code>][FastDoubleParser].
 *   The default in [<code>DataFrame.parser</code>][DataFrame.Companion.parser] is `true`.
 */
@JvmName("convertToDoubleFromStringNullable")
public fun DataColumn<String?>.convertToDouble(
    locale: Locale? = null,
    nullStrings: Set<String>?,
    useFastDoubleParser: Boolean?,
): DataColumn<Double?> =
    convertTo<Double?>(
        parserOptions = ParserOptions(
            locale = locale,
            nullStrings = nullStrings,
            useFastDoubleParser = useFastDoubleParser,
        ),
    )

/**
 * Converts values in this column to [<code>Float</code>][Float].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Float</code>][Float] values.
 */
@JvmName("convertToFloatFromT")
public fun <T : Any> DataColumn<T>.convertToFloat(): DataColumn<Float> = convertTo()

/**
 * Converts values in this column to [<code>Float</code>][Float]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Float</code>][Float] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToFloat(): DataColumn<Float?> = convertTo()

/**
 * Converts values in this column to [<code>BigDecimal</code>][BigDecimal].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>BigDecimal</code>][BigDecimal] values.
 */
@JvmName("convertToBigDecimalFromT")
public fun <T : Any> DataColumn<T>.convertToBigDecimal(): DataColumn<BigDecimal> = convertTo()

/**
 * Converts values in this column to [<code>BigDecimal</code>][BigDecimal]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>BigDecimal</code>][BigDecimal] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToBigDecimal(): DataColumn<BigDecimal?> = convertTo()

/**
 * Converts values in this column to [<code>BigInteger</code>][BigInteger].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>BigInteger</code>][BigInteger] values.
 */
@JvmName("convertToBigIntegerFromT")
public fun <T : Any> DataColumn<T>.convertToBigInteger(): DataColumn<BigInteger> = convertTo()

/**
 * Converts values in this column to [<code>BigInteger</code>][BigInteger]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>BigInteger</code>][BigInteger] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToBigInteger(): DataColumn<BigInteger?> = convertTo()

/**
 * Converts values in this column to [<code>Boolean</code>][Boolean].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Boolean</code>][Boolean] values.
 */
@JvmName("convertToBooleanFromT")
public fun <T : Any> DataColumn<T>.convertToBoolean(): DataColumn<Boolean> = convertTo()

/**
 * Converts values in this column to [<code>Boolean</code>][Boolean]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Boolean</code>][Boolean] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToBoolean(): DataColumn<Boolean?> = convertTo()

// region convert URL

/**
 * Converts values in the [<code>URL</code>][URL] columns previously selected with [<code>convert</code>][convert] to [<code>IFRAME</code>][IFRAME],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { imgUrl }.toIFrame()
 * ```
 *
 * @param border Whether the iframe should have a border. Defaults to `false`.
 * @param width Optional width of the iframe in pixels.
 * @param height Optional height of the iframe in pixels.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to an [<code>IFRAME</code>][IFRAME].
 */
@Refine
@Converter(IFRAME::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, URL?>.toIFrame(
    border: Boolean = false,
    width: Int? = null,
    height: Int? = null,
): DataFrame<T> = asColumn { it.map { url -> url?.let { IFRAME(url.toString(), border, width, height) } } }

/**
 * Converts values in the [<code>URL</code>][URL] columns previously selected with [<code>convert</code>][convert] to [<code>IMG</code>][IMG],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { avatarUrl }.toImg()
 * ```
 *
 * @param width Optional width of the image in pixels.
 * @param height Optional height of the image in pixels.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to an [<code>IMG</code>][IMG].
 */
@Refine
@Converter(IMG::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, URL?>.toImg(width: Int? = null, height: Int? = null): DataFrame<T> =
    asColumn { it.map { url -> url?.let { IMG(url.toString(), width, height) } } }

// endregion

// region toURL

@Deprecated(CONVERT_TO_URL, ReplaceWith(CONVERT_TO_URL_REPLACE), DeprecationLevel.ERROR)
public fun DataColumn<String>.convertToURL(): DataColumn<URL> = convertToUrl()

/**
 * Converts values in this [<code>String</code>][String] column to an [<code>URL</code>][URL].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with an [<code>URL</code>][URL] values.
 */
public fun DataColumn<String>.convertToUrl(): DataColumn<URL> = map { URI(it).toURL() }

@Deprecated(CONVERT_TO_URL, ReplaceWith(CONVERT_TO_URL_REPLACE), DeprecationLevel.ERROR)
@JvmName("convertToURLFromStringNullable")
public fun DataColumn<String?>.convertToURL(): DataColumn<URL?> = convertToUrl()

/**
 * Converts values in this [<code>String</code>][String] column to an [<code>URL</code>][URL]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with an [<code>URL</code>][URL] nullable values.
 */
@JvmName("convertToUrlFromStringNullable")
public fun DataColumn<String?>.convertToUrl(): DataColumn<URL?> = map { it?.let { URI(it).toURL() } }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to an [<code>URL</code>][URL],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { webAddress }.toUrl()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to an [<code>URL</code>][URL].
 */
@Refine
@Converter(URL::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String?>.toUrl(): DataFrame<T> = asColumn { it.convertToUrl() }

// endregion

// region toInstant

/**
 * __Deprecated__:
 *
 * [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] is deprecated in favor of [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 * Either migrate to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] and use [<code>convertToStdlibInstant</code>][convertToStdlibInstant] or use [<code>convertToDeprecatedInstant</code>][convertToDeprecatedInstant].
 * This function will be migrated to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] in 1.1.
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
 * [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] is deprecated in favor of [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 * Either migrate to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] and use [<code>convertToStdlibInstant</code>][convertToStdlibInstant] or use [<code>convertToDeprecatedInstant</code>][convertToDeprecatedInstant].
 * This function will be migrated to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] in 1.1.
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
 * Converts values in this [<code>String</code>][String] column to the deprecated [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant].
 *
 * Migrate to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] and use [<code>convertToStdlibInstant</code>][convertToStdlibInstant] at your own pace.
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] values.
 */
@Deprecated(
    message = CONVERT_TO_DEPRECATED_INSTANT,
    replaceWith = ReplaceWith(CONVERT_TO_DEPRECATED_INSTANT_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun DataColumn<String>.convertToDeprecatedInstant(): DataColumn<DeprecatedInstant> =
    map { DeprecatedInstant.parse(it) }

/**
 * Converts values in this [<code>String</code>][String] column to the deprecated [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant]. Preserves null values.
 *
 * Migrate to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] and use [<code>convertToStdlibInstant</code>][convertToStdlibInstant] at your own pace.
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the nullable [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] values.
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
 * Converts values in this [<code>String</code>][String] column to [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 *
 * This function will be renamed to `.convertToInstant()` in 1.1.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] values.
 */
public fun DataColumn<String>.convertToStdlibInstant(): DataColumn<StdlibInstant> = map { StdlibInstant.parse(it) }

/**
 * Converts values in this [<code>String</code>][String] column to [<code>kotlin.time.Instant</code>][kotlin.time.Instant]. Preserves null values.
 *
 * This function will be renamed to `.convertToInstant()` in 1.1.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] nullable values.
 */
@JvmName("convertToStdlibInstantFromStringNullable")
public fun DataColumn<String?>.convertToStdlibInstant(): DataColumn<StdlibInstant?> =
    map { it?.let { StdlibInstant.parse(it) } }

/**
 * Converts values in this [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] column to [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] values.
 */
@JvmName("convertToStdlibInstantFromDeprecatedInstant")
public fun DataColumn<DeprecatedInstant>.convertToStdlibInstant(): DataColumn<StdlibInstant> =
    map { it.toStdlibInstant() }

/**
 * Converts values in this [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] column to [<code>kotlin.time.Instant</code>][kotlin.time.Instant]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] nullable values.
 */
@JvmName("convertToStdlibInstantFromDeprecatedInstantNullable")
public fun DataColumn<DeprecatedInstant?>.convertToStdlibInstant(): DataColumn<StdlibInstant?> =
    map { it?.toStdlibInstant() }

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 *
 * This function will be renamed to `.convertToInstant()` in 1.1.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] values.
 */
@JvmName("convertToStdlibInstantFromDateTimeComponents")
public fun DataColumn<DateTimeComponents>.convertToStdlibInstant(): DataColumn<StdlibInstant> =
    convertTo<StdlibInstant>()

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>kotlin.time.Instant</code>][kotlin.time.Instant]. Preserves null values.
 *
 * This function will be renamed to `.convertToInstant()` in 1.1.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] nullable values.
 */
@JvmName("convertToStdlibInstantFromDateTimeComponentsNullable")
public fun DataColumn<DateTimeComponents?>.convertToStdlibInstant(): DataColumn<StdlibInstant?> =
    convertTo<StdlibInstant?>()

/**
 * __Deprecated__:
 *
 * [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] is deprecated in favor of [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 * Either migrate to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] and use [<code>toStdlibInstant</code>][toStdlibInstant] or use [<code>toDeprecatedInstant</code>][toDeprecatedInstant].
 * This function will be migrated to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] in 1.1.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 */

@Refine
@Converter(DeprecatedInstant::class, nullable = true)
@Interpretable("ToSpecificType")
@Deprecated(message = TO_INSTANT, replaceWith = ReplaceWith(TO_INSTANT_REPLACE), level = DeprecationLevel.ERROR)
public fun <T> Convert<T, String?>.toInstant(): DataFrame<T> = asColumn { it.convertToDeprecatedInstant() }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toDeprecatedInstant()
 * ```
 *
 * Migrate to [<code>kotlin.time.Instant</code>][kotlin.time.Instant] and use [<code>convertToStdlibInstant</code>][convertToStdlibInstant] at your own pace.
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant].
 */
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
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>kotlin.time.Instant</code>][kotlin.time.Instant],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toStdlibInstant()
 * ```
 *
 * This function will be renamed to `.toInstant()` in 1.1.
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 */
@JvmName("toStdlibInstantFromString")
@Refine
@Converter(StdlibInstant::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String?>.toStdlibInstant(): DataFrame<T> = asColumn { it.convertToStdlibInstant() }

/**
 * Converts values in the [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] columns previously selected with [<code>convert</code>][convert] to [<code>kotlin.time.Instant</code>][kotlin.time.Instant],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toStdlibInstant()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 */
@JvmName("toStdlibInstantFromDeprecatedInstant")
@Refine
@Converter(StdlibInstant::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DeprecatedInstant?>.toStdlibInstant(): DataFrame<T> = asColumn { it.convertToStdlibInstant() }

/**
 * Converts values in the [<code>DateTimeComponents</code>][DateTimeComponents] columns previously selected with [<code>convert</code>][convert] to [<code>kotlin.time.Instant</code>][kotlin.time.Instant],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toStdlibInstant()
 * ```
 *
 * This function will be renamed to `.toInstant()` in 1.1.
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>kotlin.time.Instant</code>][kotlin.time.Instant].
 */
@JvmName("toStdlibInstantFromDateTimeComponents")
@Refine
@Converter(StdlibInstant::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DateTimeComponents?>.toStdlibInstant(): DataFrame<T> =
    asColumn { it.convertToStdlibInstant() }

// endregion

// region toUtcOffset

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>UtcOffset</code>][UtcOffset].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>UtcOffset</code>][UtcOffset] values.
 */
public fun DataColumn<DateTimeComponents>.convertToUtcOffset(): DataColumn<UtcOffset> = convertTo<UtcOffset>()

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>UtcOffset</code>][UtcOffset]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>UtcOffset</code>][UtcOffset] nullable values.
 */
@JvmName("convertToUtcOffsetNullable")
public fun DataColumn<DateTimeComponents?>.convertToUtcOffset(): DataColumn<UtcOffset?> = convertTo<UtcOffset?>()

/**
 * Converts values in the [<code>DateTimeComponents</code>][DateTimeComponents] columns previously selected with [<code>convert</code>][convert] to [<code>UtcOffset</code>][UtcOffset],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>UtcOffset</code>][UtcOffset].
 */
@Refine
@Converter(UtcOffset::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DateTimeComponents?>.toUtcOffset(): DataFrame<T> = asColumn { it.convertToUtcOffset() }

// endregion

// region toYearMonth

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>YearMonth</code>][YearMonth].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>YearMonth</code>][YearMonth] values.
 */
public fun DataColumn<DateTimeComponents>.convertToYearMonth(): DataColumn<YearMonth> = convertTo<YearMonth>()

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>YearMonth</code>][YearMonth]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>YearMonth</code>][YearMonth] nullable values.
 */
@JvmName("convertToYearMonthNullable")
public fun DataColumn<DateTimeComponents?>.convertToYearMonth(): DataColumn<YearMonth?> = convertTo<YearMonth?>()

/**
 * Converts values in the [<code>DateTimeComponents</code>][DateTimeComponents] columns previously selected with [<code>convert</code>][convert] to [<code>YearMonth</code>][YearMonth],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>YearMonth</code>][YearMonth].
 */
@Refine
@Converter(YearMonth::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DateTimeComponents?>.toYearMonth(): DataFrame<T> = asColumn { it.convertToYearMonth() }

// endregion

// region toLocalDate

/**
 * Converts values in this [<code>Long</code>][Long] column to [<code>LocalDate</code>][LocalDate].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a date. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] values.
 */
@JvmName("convertToLocalDateFromLong")
public fun DataColumn<Long>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> =
    map { it.toLocalDate(zone) }

/**
 * Converts values in this [<code>Long</code>][Long] column to [<code>LocalDate</code>][LocalDate]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a date. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] nullable values.
 */
public fun DataColumn<Long?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> =
    map { it?.toLocalDate(zone) }

/**
 * Converts values in this [<code>Int</code>][Int] column to [<code>LocalDate</code>][LocalDate].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a date. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] values.
 */
@JvmName("convertToLocalDateFromInt")
public fun DataColumn<Int>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> =
    map { it.toLong().toLocalDate(zone) }

/**
 * Converts values in this [<code>Int</code>][Int] column to [<code>LocalDate</code>][LocalDate]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a date. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] nullable values.
 */
@JvmName("convertToLocalDateFromIntNullable")
public fun DataColumn<Int?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> =
    map { it?.toLong()?.toLocalDate(zone) }

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDate</code>][LocalDate].
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] values.
 */
@JvmName("convertToLocalDateFromString")
public fun DataColumn<String>.convertToLocalDate(format: DateTimeFormat<LocalDate>? = null): DataColumn<LocalDate> =
    convertTo<LocalDate>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDate</code>][LocalDate].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] values.
 */
@JvmName("convertToLocalDateFromStringPattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String>.convertToLocalDate(pattern: String): DataColumn<LocalDate> =
    convertToLocalDate(LocalDate.Format { byUnicodePattern(pattern) })

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDate</code>][LocalDate].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] nullable values.
 */
@JvmName("convertToLocalDateFromStringNullable")
public fun DataColumn<String?>.convertToLocalDate(format: DateTimeFormat<LocalDate>? = null): DataColumn<LocalDate?> =
    convertTo<LocalDate?>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDate</code>][LocalDate].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date pattern to use for parsing. If `null`, a default parser is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] nullable values.
 */
@JvmName("convertToLocalDateFromStringNullablePattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String?>.convertToLocalDate(pattern: String): DataColumn<LocalDate?> =
    convertToLocalDate(LocalDate.Format { byUnicodePattern(pattern) })

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>LocalDate</code>][LocalDate].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] values.
 */
@JvmName("convertToLocalDateFromDateTimeComponents")
public fun DataColumn<DateTimeComponents>.convertToLocalDate(): DataColumn<LocalDate> = convertTo<LocalDate>()

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>LocalDate</code>][LocalDate]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] nullable values.
 */
@JvmName("convertToLocalDateFromDateTimeComponentsNullable")
public fun DataColumn<DateTimeComponents?>.convertToLocalDate(): DataColumn<LocalDate?> = convertTo<LocalDate?>()

/**
 * Converts values in the [<code>Long</code>][Long] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDate</code>][LocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a date. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDate</code>][LocalDate].
 */
@JvmName("toLocalDateFromTLong")
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDate(zone) }

/**
 * Converts values in the [<code>Int</code>][Int] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDate</code>][LocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a date. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDate</code>][LocalDate].
 */
@JvmName("toLocalDateFromTInt")
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDate(zone) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDate</code>][LocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDate</code>][LocalDate].
 */
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalDate(format: DateTimeFormat<LocalDate>? = null): DataFrame<T> =
    asColumn { it.convertToLocalDate(format) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDate</code>][LocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDate</code>][LocalDate].
 */
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
@FormatStringsInDatetimeFormats
public fun <T> Convert<T, String?>.toLocalDate(pattern: String): DataFrame<T> =
    asColumn { it.convertToLocalDate(pattern) }

/**
 * Converts values in the [<code>DateTimeComponents</code>][DateTimeComponents] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDate</code>][LocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDate</code>][LocalDate].
 */
@JvmName("toLocalDateFromDateTimeComponents")
@Refine
@Converter(LocalDate::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DateTimeComponents?>.toLocalDate(): DataFrame<T> = asColumn { it.convertToLocalDate() }

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>LocalDate</code>][LocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDate()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDate</code>][LocalDate].
 */
@Refine
@Converter(LocalDate::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalDate(): DataFrame<T> = asColumn { it.convertTo<LocalDate>() }

// endregion

// region toLocalTime

/**
 * Converts values in this [<code>Long</code>][Long] column to [<code>LocalTime</code>][LocalTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] values.
 */
@JvmName("convertToLocalTimeFromLong")
public fun DataColumn<Long>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> =
    map { it.toLocalTime(zone) }

/**
 * Converts values in this [<code>Long</code>][Long] column to [<code>LocalTime</code>][LocalTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] nullable values.
 */
public fun DataColumn<Long?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> =
    map { it?.toLocalTime(zone) }

/**
 * Converts values in this [<code>Int</code>][Int] column to [<code>LocalTime</code>][LocalTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] values.
 */
@JvmName("convertToLocalTimeFromInt")
public fun DataColumn<Int>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> =
    map { it.toLong().toLocalTime(zone) }

/**
 * Converts values in this [<code>Int</code>][Int] column to [<code>LocalTime</code>][LocalTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] nullable values.
 */
@JvmName("convertToLocalTimeIntNullable")
public fun DataColumn<Int?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> =
    map { it?.toLong()?.toLocalTime(zone) }

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalTime</code>][LocalTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] values.
 */
@JvmName("convertToLocalTimeFromString")
public fun DataColumn<String>.convertToLocalTime(format: DateTimeFormat<LocalTime>? = null): DataColumn<LocalTime> =
    convertTo<LocalTime>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalTime</code>][LocalTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] values.
 */
@JvmName("convertToLocalTimeFromStringPattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String>.convertToLocalTime(pattern: String): DataColumn<LocalTime> =
    convertToLocalTime(LocalTime.Format { byUnicodePattern(pattern) })

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalTime</code>][LocalTime].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] nullable values.
 */
@JvmName("convertToLocalTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalTime(format: DateTimeFormat<LocalTime>? = null): DataColumn<LocalTime?> =
    convertTo<LocalTime?>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalTime</code>][LocalTime].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] nullable values.
 */
@JvmName("convertToLocalTimeFromStringNullablePattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String?>.convertToLocalTime(pattern: String): DataColumn<LocalTime?> =
    convertToLocalTime(LocalTime.Format { byUnicodePattern(pattern) })

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>LocalTime</code>][LocalTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] values.
 */
@JvmName("convertToLocalTimeFromDateTimeComponents")
public fun DataColumn<DateTimeComponents>.convertToLocalTime(): DataColumn<LocalTime> = convertTo<LocalTime>()

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>LocalTime</code>][LocalTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalTime</code>][LocalTime] nullable values.
 */
@JvmName("convertToLocalTimeFromDateTimeComponentsNullable")
public fun DataColumn<DateTimeComponents?>.convertToLocalTime(): DataColumn<LocalTime?> = convertTo<LocalTime?>()

/**
 * Converts values in the [<code>Long</code>][Long] columns previously selected with [<code>convert</code>][convert] to [<code>LocalTime</code>][LocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalTime</code>][LocalTime].
 */
@JvmName("toLocalTimeFromTLong")
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalTime(zone) }

/**
 * Converts values in the [<code>Int</code>][Int] columns previously selected with [<code>convert</code>][convert] to [<code>LocalTime</code>][LocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalTime</code>][LocalTime].
 */
@JvmName("toLocalTimeFromTInt")
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalTime(zone) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>LocalTime</code>][LocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalTime</code>][LocalTime].
 */
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalTime(format: DateTimeFormat<LocalTime>? = null): DataFrame<T> =
    asColumn { it.convertToLocalTime(format) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>LocalTime</code>][LocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalTime</code>][LocalTime].
 */
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
@FormatStringsInDatetimeFormats
public fun <T> Convert<T, String?>.toLocalTime(pattern: String): DataFrame<T> =
    asColumn { it.convertToLocalTime(pattern) }

/**
 * Converts values in the [<code>DateTimeComponents</code>][DateTimeComponents] columns previously selected with [<code>convert</code>][convert] to [<code>LocalTime</code>][LocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalTime</code>][LocalTime].
 */
@JvmName("toLocalTimeFromDateTimeComponents")
@Refine
@Converter(LocalTime::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DateTimeComponents?>.toLocalTime(): DataFrame<T> = asColumn { it.convertToLocalTime() }

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>LocalTime</code>][LocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalTime</code>][LocalTime].
 */
@Refine
@Converter(LocalTime::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalTime(): DataFrame<T> = asColumn { it.convertTo<LocalTime>() }

// endregion

// region toLocalDateTime

/**
 * Converts values in this [<code>Long</code>][Long] column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDate</code>][LocalDate] values.
 */
@JvmName("convertToLocalDateTimeFromLong")
public fun DataColumn<Long>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> =
    map { it.toLocalDateTime(zone) }

/**
 * Converts values in this [<code>Long</code>][Long] column to [<code>LocalDateTime</code>][LocalDateTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
public fun DataColumn<Long?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> =
    map { it?.toLocalDateTime(zone) }

/**
 * Converts values in this [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromDeprecatedInstant")
public fun DataColumn<DeprecatedInstant>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime> = map { it.toLocalDateTime(zone) }

/**
 * Converts values in this [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] column to [<code>LocalDateTime</code>][LocalDateTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromDeprecatedInstantNullable")
public fun DataColumn<DeprecatedInstant?>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime?> = map { it?.toLocalDateTime(zone) }

/**
 * Converts values in this [<code>kotlin.time.Instant</code>][kotlin.time.Instant] column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromStdlibInstant")
public fun DataColumn<StdlibInstant>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime> = map { it.toLocalDateTime(zone) }

/**
 * Converts values in this [<code>kotlin.time.Instant</code>][kotlin.time.Instant] column to [<code>LocalDateTime</code>][LocalDateTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromStdlibInstantNullable")
public fun DataColumn<StdlibInstant?>.convertToLocalDateTime(
    zone: TimeZone = defaultTimeZone,
): DataColumn<LocalDateTime?> = map { it?.toLocalDateTime(zone) }

/**
 * Converts values in this [<code>Int</code>][Int] column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromInt")
public fun DataColumn<Int>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> =
    map { it.toLong().toLocalDateTime(zone) }

/**
 * Converts values in this [<code>Int</code>][Int] column to [<code>LocalDateTime</code>][LocalDateTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a date-time.
 * Defaults to the system current time zone.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromIntNullable")
public fun DataColumn<Int?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> =
    map { it?.toLong()?.toLocalDateTime(zone) }

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromString")
public fun DataColumn<String>.convertToLocalDateTime(
    format: DateTimeFormat<LocalDateTime>? = null,
): DataColumn<LocalDateTime> =
    convertTo<LocalDateTime>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromStringPattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String>.convertToLocalDateTime(pattern: String): DataColumn<LocalDateTime> =
    convertToLocalDateTime(LocalDateTime.Format { byUnicodePattern(pattern) })

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDateTime</code>][LocalDateTime].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalDateTime(
    format: DateTimeFormat<LocalDateTime>? = null,
): DataColumn<LocalDateTime?> =
    convertTo<LocalDateTime?>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>LocalDateTime</code>][LocalDateTime].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromStringNullablePattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String?>.convertToLocalDateTime(pattern: String): DataColumn<LocalDateTime?> =
    convertToLocalDateTime(LocalDateTime.Format { byUnicodePattern(pattern) })

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>LocalDateTime</code>][LocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] values.
 */
@JvmName("convertToLocalDateTimeFromDateTimeComponents")
public fun DataColumn<DateTimeComponents>.convertToLocalDateTime(): DataColumn<LocalDateTime> =
    convertTo<LocalDateTime>()

/**
 * Converts values in this [<code>DateTimeComponents</code>][DateTimeComponents] column to [<code>LocalDateTime</code>][LocalDateTime]. Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>LocalDateTime</code>][LocalDateTime] nullable values.
 */
@JvmName("convertToLocalDateTimeFromDateTimeComponentsNullable")
public fun DataColumn<DateTimeComponents?>.convertToLocalDateTime(): DataColumn<LocalDateTime?> =
    convertTo<LocalDateTime?>()

/**
 * Converts values in the [<code>Long</code>][Long] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Long</code>][Long] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTLong")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>kotlinx.datetime.Instant</code>][kotlinx.datetime.Instant] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTDeprecatedInstant")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, DeprecatedInstant?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>kotlin.time.Instant</code>][kotlin.time.Instant] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTStdlibInstant")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, StdlibInstant?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [<code>Int</code>][Int] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalDateTime()
 * ```
 *
 * @param zone The [<code>TimeZone</code>][TimeZone] used to interpret the [<code>Int</code>][Int] timestamp as a time. Defaults to the system current time zone.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@JvmName("toLocalDateTimeFromTInt")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(zone) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalDateTime(format: DateTimeFormat<LocalDateTime>? = null): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(format) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 * @param pattern An optional date pattern to use for parsing.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
@FormatStringsInDatetimeFormats
public fun <T> Convert<T, String?>.toLocalDateTime(pattern: String): DataFrame<T> =
    asColumn { it.convertToLocalDateTime(pattern) }

/**
 * Converts values in the [<code>DateTimeComponents</code>][DateTimeComponents] columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@JvmName("toLocalDateTimeFromDateTimeComponents")
@Refine
@Converter(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, DateTimeComponents?>.toLocalDateTime(): DataFrame<T> =
    asColumn { it.convertToLocalDateTime() }

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>LocalDateTime</code>][LocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toLocalTime()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>LocalDateTime</code>][LocalDateTime].
 */
@Refine
@Converter(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalDateTime(): DataFrame<T> = asColumn { it.convertTo<LocalDateTime>() }

// endregion

// region toDateTimeComponents

/**
 * Converts values in this [<code>String</code>][String] column to [<code>DateTimeComponents</code>][DateTimeComponents].
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>DateTimeComponents</code>][DateTimeComponents] values.
 */
@JvmName("convertToDateTimeComponentsFromString")
public fun DataColumn<String>.convertToDateTimeComponents(
    format: DateTimeFormat<DateTimeComponents>? = null,
): DataColumn<DateTimeComponents> =
    convertTo<DateTimeComponents>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>DateTimeComponents</code>][DateTimeComponents].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date-time pattern to use for parsing.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>DateTimeComponents</code>][DateTimeComponents] values.
 */
@JvmName("convertToDateTimeComponentsFromStringPattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String>.convertToDateTimeComponents(pattern: String): DataColumn<DateTimeComponents> =
    convertToDateTimeComponents(DateTimeComponents.Format { byUnicodePattern(pattern) })

/**
 * Converts values in this [<code>String</code>][String] column to [<code>DateTimeComponents</code>][DateTimeComponents].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>DateTimeComponents</code>][DateTimeComponents] nullable values.
 */
@JvmName("convertToDateTimeComponentsFromStringNullable")
public fun DataColumn<String?>.convertToDateTimeComponents(
    format: DateTimeFormat<DateTimeComponents>? = null,
): DataColumn<DateTimeComponents?> =
    convertTo<DateTimeComponents?>(
        parserOptions = ParserOptions(dateTime = DateTimeParserOptions.Kotlin.withFormat(format)),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>DateTimeComponents</code>][DateTimeComponents].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern An optional date-time pattern to use for parsing.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>DateTimeComponents</code>][DateTimeComponents] nullable values.
 */
@JvmName("convertToDateTimeComponentsFromStringNullablePattern")
@FormatStringsInDatetimeFormats
public fun DataColumn<String?>.convertToDateTimeComponents(pattern: String): DataColumn<DateTimeComponents?> =
    convertToDateTimeComponents(DateTimeComponents.Format { byUnicodePattern(pattern) })

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>DateTimeComponents</code>][DateTimeComponents],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>format</code>][format].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toDateTimeComponents()
 * ```
 *
 * @param [format] An optional [<code>DateTimeFormat</code>][DateTimeFormat] to use when parsing. If `null`, the defaults will be used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>DateTimeComponents</code>][DateTimeComponents].
 */
@Refine
@Converter(DateTimeComponents::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toDateTimeComponents(
    format: DateTimeFormat<DateTimeComponents>? = null,
): DataFrame<T> = asColumn { it.convertToDateTimeComponents(format) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>DateTimeComponents</code>][DateTimeComponents],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toDateTimeComponents()
 * ```
 *
 * @param pattern An optional date-time pattern to use for parsing.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>DateTimeComponents</code>][DateTimeComponents].
 */
@Refine
@Converter(DateTimeComponents::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
@FormatStringsInDatetimeFormats
public fun <T> Convert<T, String?>.toDateTimeComponents(pattern: String): DataFrame<T> =
    asColumn { it.convertToDateTimeComponents(pattern) }

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>DateTimeComponents</code>][DateTimeComponents],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toDateTimeComponents()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>DateTimeComponents</code>][DateTimeComponents].
 */
@Refine
@Converter(DateTimeComponents::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toDateTimeComponents(): DataFrame<T> = asColumn { it.convertTo<DateTimeComponents>() }

// endregion

// region toDuration

/**
 * Converts values in this column to [<code>Duration</code>][Duration].
 *
 * Supported source types: [<code>String</code>][String] (parsed via [<code>Duration.parse</code>][Duration.parse]),
 * [<code>JavaDuration</code>][JavaDuration], [<code>Long</code>][Long] and [<code>Int</code>][Int] (interpreted as milliseconds).
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Duration</code>][Duration] values.
 */
@JvmName("convertToDurationFromT")
public fun <T : Any> DataColumn<T>.convertToDuration(): DataColumn<Duration> = convertTo()

/**
 * Converts values in this column to [<code>Duration</code>][Duration]. Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed via [<code>Duration.parse</code>][Duration.parse]),
 * [<code>JavaDuration</code>][JavaDuration], [<code>Long</code>][Long] and [<code>Int</code>][Int] (interpreted as milliseconds).
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>Duration</code>][Duration] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToDuration(): DataColumn<Duration?> = convertTo()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>Duration</code>][Duration],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed via [<code>Duration.parse</code>][Duration.parse]),
 * [<code>JavaDuration</code>][JavaDuration], [<code>Long</code>][Long] and [<code>Int</code>][Int] (interpreted as milliseconds).
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { duration }.toDuration()
 * df.convert { colsOf<String?>() }.toDuration()
 * df.convert { colsOf<Long?>() }.toDuration()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>Duration</code>][Duration].
 */
@Refine
@Converter(Duration::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toDuration(): DataFrame<T> = to<Duration?>()

// endregion

// region toJavaInstant

/**
 * Converts values in this column to [<code>JavaInstant</code>][JavaInstant].
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaInstant</code>][JavaInstant] values.
 */
@JvmName("convertToJavaInstantFromT")
public fun <T : Any> DataColumn<T>.convertToJavaInstant(): DataColumn<JavaInstant> = convertTo()

/**
 * Converts values in this column to [<code>JavaInstant</code>][JavaInstant]. Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaInstant</code>][JavaInstant] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToJavaInstant(): DataColumn<JavaInstant?> = convertTo()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>JavaInstant</code>][JavaInstant],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { timestamp }.toJavaInstant()
 * df.convert { colsOf<Long?>() }.toJavaInstant()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaInstant</code>][JavaInstant].
 */
@Refine
@Converter(JavaInstant::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toJavaInstant(): DataFrame<T> = to<JavaInstant?>()

// endregion

// region toJavaDuration

/**
 * Converts values in this column to [<code>JavaDuration</code>][JavaDuration].
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Duration</code>][Duration].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaDuration</code>][JavaDuration] values.
 */
@JvmName("convertToJavaDurationFromT")
public fun <T : Any> DataColumn<T>.convertToJavaDuration(): DataColumn<JavaDuration> = convertTo()

/**
 * Converts values in this column to [<code>JavaDuration</code>][JavaDuration]. Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Duration</code>][Duration].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaDuration</code>][JavaDuration] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToJavaDuration(): DataColumn<JavaDuration?> = convertTo()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>JavaDuration</code>][JavaDuration],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Duration</code>][Duration].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { duration }.toJavaDuration()
 * df.convert { colsOf<Duration?>() }.toJavaDuration()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaDuration</code>][JavaDuration].
 */
@Refine
@Converter(JavaDuration::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toJavaDuration(): DataFrame<T> = to<JavaDuration?>()

// endregion

// region toJavaLocalDate

/**
 * Converts values in this column to [<code>JavaLocalDate</code>][JavaLocalDate].
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalDate</code>][LocalDate], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDate</code>][JavaLocalDate] values.
 */
@JvmName("convertToJavaLocalDateFromT")
public fun <T : Any> DataColumn<T>.convertToJavaLocalDate(): DataColumn<JavaLocalDate> = convertTo()

/**
 * Converts values in this column to [<code>JavaLocalDate</code>][JavaLocalDate]. Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalDate</code>][LocalDate], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDate</code>][JavaLocalDate] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToJavaLocalDate(): DataColumn<JavaLocalDate?> = convertTo()

/**
 * Converts values in this [<code>String</code>][String] column to [<code>JavaLocalDate</code>][JavaLocalDate].
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDate</code>][JavaLocalDate] values.
 */
@JvmName("convertToJavaLocalDateFromString")
public fun DataColumn<String>.convertToJavaLocalDate(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataColumn<JavaLocalDate> =
    convertTo<JavaLocalDate>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withFormatter<JavaLocalDate>(formatter)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>JavaLocalDate</code>][JavaLocalDate].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A date pattern to use for parsing (e.g., `"yyyy-MM-dd"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDate</code>][JavaLocalDate] values.
 */
@JvmName("convertToJavaLocalDateFromStringPattern")
public fun DataColumn<String>.convertToJavaLocalDate(
    pattern: String,
    locale: Locale? = null,
): DataColumn<JavaLocalDate> =
    convertTo<JavaLocalDate>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withPattern<JavaLocalDate>(pattern)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this nullable [<code>String</code>][String] column to [<code>JavaLocalDate</code>][JavaLocalDate]. Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDate</code>][JavaLocalDate] nullable values.
 */
@JvmName("convertToJavaLocalDateFromStringNullable")
public fun DataColumn<String?>.convertToJavaLocalDate(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataColumn<JavaLocalDate?> =
    convertTo<JavaLocalDate?>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withFormatter<JavaLocalDate>(formatter)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this nullable [<code>String</code>][String] column to [<code>JavaLocalDate</code>][JavaLocalDate]. Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A date pattern to use for parsing (e.g., `"yyyy-MM-dd"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDate</code>][JavaLocalDate] nullable values.
 */
@JvmName("convertToJavaLocalDateFromStringNullablePattern")
public fun DataColumn<String?>.convertToJavaLocalDate(
    pattern: String,
    locale: Locale? = null,
): DataColumn<JavaLocalDate?> =
    convertTo<JavaLocalDate?>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withPattern<JavaLocalDate>(pattern)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDate</code>][JavaLocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalDate</code>][LocalDate], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { date }.toJavaLocalDate()
 * df.convert { colsOf<LocalDate?>() }.toJavaLocalDate()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDate</code>][JavaLocalDate].
 */
@Refine
@Converter(JavaLocalDate::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toJavaLocalDate(): DataFrame<T> = to<JavaLocalDate?>()

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDate</code>][JavaLocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDate</code>][JavaLocalDate].
 */
@JvmName("toJavaLocalDateFromString")
@Refine
@Converter(JavaLocalDate::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toJavaLocalDate(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataFrame<T> = asColumn { it.convertToJavaLocalDate(formatter, locale) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDate</code>][JavaLocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A date pattern to use for parsing (e.g., `"yyyy-MM-dd"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDate</code>][JavaLocalDate].
 */
@JvmName("toJavaLocalDateFromStringPattern")
@Refine
@Converter(JavaLocalDate::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toJavaLocalDate(pattern: String, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToJavaLocalDate(pattern, locale) }

/**
 * Converts values in the nullable [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDate</code>][JavaLocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDate</code>][JavaLocalDate].
 */
@JvmName("toJavaLocalDateFromStringNullable")
@Refine
@Converter(JavaLocalDate::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toJavaLocalDate(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataFrame<T> = asColumn { it.convertToJavaLocalDate(formatter, locale) }

/**
 * Converts values in the nullable [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDate</code>][JavaLocalDate],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A date pattern to use for parsing (e.g., `"yyyy-MM-dd"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDate</code>][JavaLocalDate].
 */
@JvmName("toJavaLocalDateFromStringNullablePattern")
@Refine
@Converter(JavaLocalDate::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toJavaLocalDate(pattern: String, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToJavaLocalDate(pattern, locale) }

// endregion

// region toJavaLocalTime

/**
 * Converts values in this column to [<code>JavaLocalTime</code>][JavaLocalTime].
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalTime</code>][LocalTime], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>StdlibInstant</code>][StdlibInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalTime</code>][JavaLocalTime] values.
 */
@JvmName("convertToJavaLocalTimeFromT")
public fun <T : Any> DataColumn<T>.convertToJavaLocalTime(): DataColumn<JavaLocalTime> = convertTo()

/**
 * Converts values in this column to [<code>JavaLocalTime</code>][JavaLocalTime]. Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalTime</code>][LocalTime], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>StdlibInstant</code>][StdlibInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalTime</code>][JavaLocalTime] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToJavaLocalTime(): DataColumn<JavaLocalTime?> = convertTo()

/**
 * Converts values in this [<code>String</code>][String] column to [<code>JavaLocalTime</code>][JavaLocalTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalTime</code>][JavaLocalTime] values.
 */
@JvmName("convertToJavaLocalTimeFromString")
public fun DataColumn<String>.convertToJavaLocalTime(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataColumn<JavaLocalTime> =
    convertTo<JavaLocalTime>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withFormatter<JavaLocalTime>(formatter)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>JavaLocalTime</code>][JavaLocalTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A time pattern to use for parsing (e.g., `"HH:mm:ss"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalTime</code>][JavaLocalTime] values.
 */
@JvmName("convertToJavaLocalTimeFromStringPattern")
public fun DataColumn<String>.convertToJavaLocalTime(
    pattern: String,
    locale: Locale? = null,
): DataColumn<JavaLocalTime> =
    convertTo<JavaLocalTime>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withPattern<JavaLocalTime>(pattern)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this nullable [<code>String</code>][String] column to [<code>JavaLocalTime</code>][JavaLocalTime]. Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalTime</code>][JavaLocalTime] nullable values.
 */
@JvmName("convertToJavaLocalTimeFromStringNullable")
public fun DataColumn<String?>.convertToJavaLocalTime(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataColumn<JavaLocalTime?> =
    convertTo<JavaLocalTime?>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withFormatter<JavaLocalTime>(formatter)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this nullable [<code>String</code>][String] column to [<code>JavaLocalTime</code>][JavaLocalTime]. Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A time pattern to use for parsing (e.g., `"HH:mm:ss"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalTime</code>][JavaLocalTime] nullable values.
 */
@JvmName("convertToJavaLocalTimeFromStringNullablePattern")
public fun DataColumn<String?>.convertToJavaLocalTime(
    pattern: String,
    locale: Locale? = null,
): DataColumn<JavaLocalTime?> =
    convertTo<JavaLocalTime?>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withPattern<JavaLocalTime>(pattern)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalTime</code>][JavaLocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalTime</code>][LocalTime], [<code>LocalDateTime</code>][LocalDateTime], [<code>JavaLocalDateTime</code>][JavaLocalDateTime], [<code>StdlibInstant</code>][StdlibInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { time }.toJavaLocalTime()
 * df.convert { colsOf<LocalTime?>() }.toJavaLocalTime()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalTime</code>][JavaLocalTime].
 */
@Refine
@Converter(JavaLocalTime::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toJavaLocalTime(): DataFrame<T> = to<JavaLocalTime?>()

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalTime</code>][JavaLocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalTime</code>][JavaLocalTime].
 */
@JvmName("toJavaLocalTimeFromString")
@Refine
@Converter(JavaLocalTime::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toJavaLocalTime(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataFrame<T> = asColumn { it.convertToJavaLocalTime(formatter, locale) }

/**
 * Converts values in the [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalTime</code>][JavaLocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A time pattern to use for parsing (e.g., `"HH:mm:ss"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalTime</code>][JavaLocalTime].
 */
@JvmName("toJavaLocalTimeFromStringPattern")
@Refine
@Converter(JavaLocalTime::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toJavaLocalTime(pattern: String, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToJavaLocalTime(pattern, locale) }

/**
 * Converts values in the nullable [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalTime</code>][JavaLocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalTime</code>][JavaLocalTime].
 */
@JvmName("toJavaLocalTimeFromStringNullable")
@Refine
@Converter(JavaLocalTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toJavaLocalTime(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataFrame<T> = asColumn { it.convertToJavaLocalTime(formatter, locale) }

/**
 * Converts values in the nullable [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalTime</code>][JavaLocalTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A time pattern to use for parsing (e.g., `"HH:mm:ss"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalTime</code>][JavaLocalTime].
 */
@JvmName("toJavaLocalTimeFromStringNullablePattern")
@Refine
@Converter(JavaLocalTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toJavaLocalTime(pattern: String, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToJavaLocalTime(pattern, locale) }

// endregion

// region toJavaLocalDateTime

/**
 * Converts values in this column to [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalDateTime</code>][LocalDateTime], [<code>LocalDate</code>][LocalDate], [<code>JavaLocalDate</code>][JavaLocalDate], [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDateTime</code>][JavaLocalDateTime] values.
 */
@JvmName("convertToJavaLocalDateTimeFromT")
public fun <T : Any> DataColumn<T>.convertToJavaLocalDateTime(): DataColumn<JavaLocalDateTime> = convertTo()

/**
 * Converts values in this column to [<code>JavaLocalDateTime</code>][JavaLocalDateTime]. Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalDateTime</code>][LocalDateTime], [<code>LocalDate</code>][LocalDate], [<code>JavaLocalDate</code>][JavaLocalDate], [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDateTime</code>][JavaLocalDateTime] nullable values.
 */
public fun <T : Any> DataColumn<T?>.convertToJavaLocalDateTime(): DataColumn<JavaLocalDateTime?> = convertTo()

/**
 * Converts values in this [<code>String</code>][String] column to [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDateTime</code>][JavaLocalDateTime] values.
 */
@JvmName("convertToJavaLocalDateTimeFromString")
public fun DataColumn<String>.convertToJavaLocalDateTime(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataColumn<JavaLocalDateTime> =
    convertTo<JavaLocalDateTime>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withFormatter<JavaLocalDateTime>(formatter)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this [<code>String</code>][String] column to [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A date-time pattern to use for parsing (e.g., `"yyyy-MM-dd HH:mm:ss"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDateTime</code>][JavaLocalDateTime] values.
 */
@JvmName("convertToJavaLocalDateTimeFromStringPattern")
public fun DataColumn<String>.convertToJavaLocalDateTime(
    pattern: String,
    locale: Locale? = null,
): DataColumn<JavaLocalDateTime> =
    convertTo<JavaLocalDateTime>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withPattern<JavaLocalDateTime>(pattern)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this nullable [<code>String</code>][String] column to [<code>JavaLocalDateTime</code>][JavaLocalDateTime]. Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDateTime</code>][JavaLocalDateTime] nullable values.
 */
@JvmName("convertToJavaLocalDateTimeFromStringNullable")
public fun DataColumn<String?>.convertToJavaLocalDateTime(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataColumn<JavaLocalDateTime?> =
    convertTo<JavaLocalDateTime?>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withFormatter<JavaLocalDateTime>(formatter)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in this nullable [<code>String</code>][String] column to [<code>JavaLocalDateTime</code>][JavaLocalDateTime]. Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A date-time pattern to use for parsing (e.g., `"yyyy-MM-dd HH:mm:ss"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataColumn</code>][DataColumn] with the [<code>JavaLocalDateTime</code>][JavaLocalDateTime] nullable values.
 */
@JvmName("convertToJavaLocalDateTimeFromStringNullablePattern")
public fun DataColumn<String?>.convertToJavaLocalDateTime(
    pattern: String,
    locale: Locale? = null,
): DataColumn<JavaLocalDateTime?> =
    convertTo<JavaLocalDateTime?>(
        parserOptions = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                .withPattern<JavaLocalDateTime>(pattern)
                .withLocale(locale),
        ),
    )

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDateTime</code>][JavaLocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Supported source types: [<code>String</code>][String] (parsed), [<code>Long</code>][Long] and [<code>Int</code>][Int] (epoch milliseconds),
 * [<code>LocalDateTime</code>][LocalDateTime], [<code>LocalDate</code>][LocalDate], [<code>JavaLocalDate</code>][JavaLocalDate], [<code>StdlibInstant</code>][StdlibInstant], [<code>DeprecatedInstant</code>][DeprecatedInstant], [<code>JavaInstant</code>][JavaInstant].
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { dateTime }.toJavaLocalDateTime()
 * df.convert { colsOf<LocalDateTime?>() }.toJavaLocalDateTime()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 */
@Refine
@Converter(JavaLocalDateTime::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toJavaLocalDateTime(): DataFrame<T> = to<JavaLocalDateTime?>()

/**
 * Converts values in the nullable [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDateTime</code>][JavaLocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>formatter</code>][formatter] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param formatter An optional [<code>DateTimeFormatter</code>][DateTimeFormatter] to use for parsing. If `null`, default parsers are used.
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 */
@Refine
@Converter(JavaLocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toJavaLocalDateTime(
    formatter: DateTimeFormatter? = null,
    locale: Locale? = null,
): DataFrame<T> = asColumn { it.convertToJavaLocalDateTime(formatter, locale) }

/**
 * Converts values in the nullable [<code>String</code>][String] columns previously selected with [<code>convert</code>][convert] to [<code>JavaLocalDateTime</code>][JavaLocalDateTime],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * Trims each string and attempts to parse it using the specified [<code>pattern</code>][pattern] and [<code>locale</code>][locale].
 * Fails with an exception if a value cannot be parsed.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param pattern A date-time pattern to use for parsing (e.g., `"yyyy-MM-dd HH:mm:ss"`).
 * @param locale An optional [<code>Locale</code>][Locale] for parsing. If `null`, the default locale is used.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>JavaLocalDateTime</code>][JavaLocalDateTime].
 */
@Refine
@Converter(JavaLocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toJavaLocalDateTime(pattern: String, locale: Locale? = null): DataFrame<T> =
    asColumn { it.convertToJavaLocalDateTime(pattern, locale) }

// endregion

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>Int</code>][Int],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toInt()
 * df.convert { colsOf<Double>() }.toInt()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>Int</code>][Int].
 */
@Refine
@Converter(Int::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toInt(): DataFrame<T> = to<Int?>()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>Long</code>][Long],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toLong()
 * df.convert { colsOf<Double>() }.toLong()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>Long</code>][Long].
 */
@Refine
@Converter(Long::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toLong(): DataFrame<T> = to<Long?>()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>String</code>][String],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toStr()
 * df.convert { colsOf<Double>() }.toStr()
 * ```
 *
 * @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>String</code>][String].
 */
@Refine
@Converter(String::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toStr(): DataFrame<T> = to<String?>()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>Double</code>][Double],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toDouble()
 * df.convert { colsOf<Number?>() }.toDouble()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>Double</code>][Double].
 */
@Refine
@Converter(Double::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toDouble(): DataFrame<T> = to<Double?>()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>Float</code>][Float],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toFloat()
 * df.convert { colsOf<Double>() }.toFloat()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>Float</code>][Float].
 */
@Refine
@Converter(Float::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toFloat(): DataFrame<T> = to<Float?>()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>BigDecimal</code>][BigDecimal],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toBigDecimal()
 * df.convert { colsOf<Double>() }.toBigDecimal()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>BigDecimal</code>][BigDecimal].
 */
@Refine
@Converter(BigDecimal::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBigDecimal(): DataFrame<T> = to<BigDecimal?>()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>BigInteger</code>][BigInteger],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { age and year }.toBigInteger()
 * df.convert { colsOf<Double?>() }.toBigInteger()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>BigInteger</code>][BigInteger].
 */
@Refine
@Converter(BigInteger::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBigInteger(): DataFrame<T> = to<BigInteger?>()

/**
 * Converts values in the columns previously selected with [<code>convert</code>][convert] to [<code>Boolean</code>][Boolean],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 * Preserves null values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * ### Examples:
 * ```kotlin
 * df.convert { isMarked and isFinished }.toBoolean()
 * df.convert { colsOf<String?> { it.name.startsWith("it") } }.toBoolean()
 * ```
 *
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>Boolean</code>][Boolean].
 */
@Refine
@Converter(Boolean::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBoolean(): DataFrame<T> = to<Boolean?>()

/**
 * Converts a list of lists values in the columns previously selected with [<code>convert</code>][convert] to [<code>DataFrame</code>][DataFrame],
 * preserving their original names and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * By default, treats the first inner list as a header (column names), and the remaining lists as rows.
 * If [<code>containsColumns</code>][containsColumns] is `true`, interprets each inner list as a column,
 * where the first element is used as the column name, and the remaining elements as values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
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
 *  @return A new [<code>DataFrame</code>][DataFrame] with the values converted to [<code>DataFrame</code>][DataFrame].
 */
public fun <T, C> Convert<T, List<List<C>>>.toDataFrames(containsColumns: Boolean = false): DataFrame<T> =
    asColumn { it.toDataFrames(containsColumns) }

/**
 * Converts a list of lists values in this [<code>DataColumn</code>][DataColumn] to [<code>DataFrame</code>][DataFrame].
 *
 * By default, treats the first inner list as a header (column names), and the remaining lists as rows.
 * If [<code>containsColumns</code>][containsColumns] is `true`, interprets each inner list as a column,
 * where the first element is used as the column name, and the remaining elements as values.
 *
 * For more information: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/convert.html)
 *
 * @param containsColumns If `true`, treats each nested list as a column with its first element as the column name.
 *                        Otherwise, the first list is treated as the header.
 *                        Defaults to `false`.
 *  @return A new [<code>DataColumn</code>][DataColumn] with the values converted to [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataColumn<List<List<T>>>.toDataFrames(containsColumns: Boolean = false): DataColumn<DataFrame<*>> =
    map { it.toDataFrame(containsColumns = containsColumns) }

// region deprecated

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.toJavaLocalDate(pattern, locale)"),
    level = DeprecationLevel.ERROR,
)
public fun <T> Convert<T, String?>.toLocalDate(pattern: String? = null, locale: Locale?): DataFrame<T> =
    toJavaLocalDate(formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) }, locale = locale)
        .convert(this.columns).toLocalDate()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.toJavaLocalTime(pattern, locale)"),
    level = DeprecationLevel.ERROR,
)
public fun <T> Convert<T, String?>.toLocalTime(pattern: String? = null, locale: Locale?): DataFrame<T> =
    toJavaLocalTime(formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) }, locale = locale)
        .convert(this.columns).toLocalTime()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.toJavaLocalDateTime(pattern, locale)"),
    level = DeprecationLevel.ERROR,
)
public fun <T> Convert<T, String?>.toLocalDateTime(pattern: String? = null, locale: Locale?): DataFrame<T> =
    toJavaLocalDateTime(formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) }, locale = locale)
        .convert(this.columns).toLocalDateTime()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.convertToJavaLocalDate(pattern, locale).convertToLocalDate()"),
    level = DeprecationLevel.ERROR,
)
@JvmName("convertToLocalDateFromString")
public fun DataColumn<String>.convertToLocalDate(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDate> =
    convertToJavaLocalDate(
        formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) },
        locale = locale,
    ).convertToLocalDate()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.convertToJavaLocalDate(pattern, locale).convertToLocalDate()"),
    level = DeprecationLevel.ERROR,
)
@JvmName("convertToLocalDateFromStringNullable")
public fun DataColumn<String?>.convertToLocalDate(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDate?> =
    convertToJavaLocalDate(
        formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) },
        locale = locale,
    ).convertToLocalDate()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.convertToJavaLocalTime(pattern, locale).convertToLocalTime()"),
    level = DeprecationLevel.ERROR,
)
@JvmName("convertToLocalTimeFromString")
public fun DataColumn<String>.convertToLocalTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalTime> =
    convertToJavaLocalTime(
        formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) },
        locale = locale,
    ).convertToLocalTime()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.convertToJavaLocalTime(pattern, locale).convertToLocalTime()"),
    level = DeprecationLevel.ERROR,
)
@JvmName("convertToLocalTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalTime?> =
    convertToJavaLocalTime(
        formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) },
        locale = locale,
    ).convertToLocalTime()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.convertToJavaLocalDateTime(pattern, locale).convertToLocalDateTime()"),
    level = DeprecationLevel.ERROR,
)
@JvmName("convertToLocalDateTimeFromString")
public fun DataColumn<String>.convertToLocalDateTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDateTime> =
    convertToJavaLocalDateTime(
        formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) },
        locale = locale,
    ).convertToLocalDateTime()

@Deprecated(
    message = CONVERT_TO_KOTLIN_DATETIME_LOCALE,
    replaceWith = ReplaceWith("this.convertToJavaLocalDateTime(pattern, locale).convertToLocalDateTime()"),
    level = DeprecationLevel.ERROR,
)
@JvmName("convertToLocalDateTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalDateTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDateTime?> =
    convertToJavaLocalDateTime(
        formatter = pattern?.let { DateTimeFormatter.ofPattern(pattern) },
        locale = locale,
    ).convertToLocalDateTime()

// endregion
