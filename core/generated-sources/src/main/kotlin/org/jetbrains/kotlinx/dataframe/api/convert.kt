package org.jetbrains.kotlinx.dataframe.api

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Converter
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.dataTypes.IFRAME
import org.jetbrains.kotlinx.dataframe.dataTypes.IMG
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
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.util.Locale
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@Interpretable("Convert0")
public fun <T, C> DataFrame<T>.convert(columns: ColumnsSelector<T, C>): Convert<T, C> = Convert(this, columns)

@AccessApiOverload
public fun <T, C> DataFrame<T>.convert(vararg columns: KProperty<C>): Convert<T, C> = convert { columns.toColumnSet() }

@Interpretable("Convert2")
public fun <T> DataFrame<T>.convert(vararg columns: String): Convert<T, Any?> = convert { columns.toColumnSet() }

@AccessApiOverload
public fun <T, C> DataFrame<T>.convert(vararg columns: ColumnReference<C>): Convert<T, C> =
    convert { columns.toColumnSet() }

@AccessApiOverload
public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    infer: Infer = Infer.Nulls,
    noinline expression: RowValueExpression<T, C, R>,
): DataFrame<T> = convert(*headPlusArray(firstCol, cols)).with(infer, expression)

@AccessApiOverload
public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    infer: Infer = Infer.Nulls,
    noinline expression: RowValueExpression<T, C, R>,
): DataFrame<T> = convert(*headPlusArray(firstCol, cols)).with(infer, expression)

@Interpretable("Convert6")
public inline fun <T, reified R> DataFrame<T>.convert(
    firstCol: String,
    vararg cols: String,
    infer: Infer = Infer.Nulls,
    noinline expression: RowValueExpression<T, Any?, R>,
): DataFrame<T> = convert(*headPlusArray(firstCol, cols)).with(infer, expression)

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

@HasSchema(schemaArg = 0)
public class Convert<T, out C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    public fun <R> cast(): Convert<T, R> = Convert(df, columns as ColumnsSelector<T, R>)

    @Refine
    @Interpretable("To0")
    public inline fun <reified D> to(): DataFrame<T> = to(typeOf<D>())

    override fun toString(): String = "Convert(df=$df, columns=$columns)"
}

public fun <T> Convert<T, *>.to(type: KType): DataFrame<T> = to { it.convertTo(type) }

public fun <T, C> Convert<T, C>.to(columnConverter: DataFrame<T>.(DataColumn<C>) -> AnyBaseCol): DataFrame<T> =
    df.replace(columns).with { columnConverter(df, it) }

@Refine
@Interpretable("With0")
public inline fun <T, C, reified R> Convert<T, C>.with(
    infer: Infer = Infer.Nulls,
    noinline rowConverter: RowValueExpression<T, C, R>,
): DataFrame<T> = withRowCellImpl(typeOf<R>(), infer, rowConverter)

@Refine
@Interpretable("With0")
public inline fun <T, C, reified R> Convert<T, C>.with(
    noinline rowConverter: RowValueExpression<T, C, R>,
): DataFrame<T> = with(Infer.Nulls, rowConverter)

public fun <T, C, R> Convert<T, DataRow<C>>.asFrame(
    body: ColumnsContainer<T>.(ColumnGroup<C>) -> DataFrame<R>,
): DataFrame<T> = to { body(this, it.asColumnGroup()).asColumnGroup(it.name()) }

@Refine
@Interpretable("PerRowCol")
public inline fun <T, C, reified R> Convert<T, C>.perRowCol(
    infer: Infer = Infer.Nulls,
    noinline expression: RowColumnExpression<T, C, R>,
): DataFrame<T> = convertRowColumnImpl(typeOf<R>(), infer, expression)

public inline fun <reified C> AnyCol.convertTo(): DataColumn<C> = convertTo(typeOf<C>()) as DataColumn<C>

@Suppress("UNCHECKED_CAST")
public fun AnyCol.convertTo(newType: KType): AnyCol =
    when {
        type().isSubtypeOf(typeOf<String?>()) ->
            (this as DataColumn<String?>).convertTo(newType)

        else -> convertToTypeImpl(newType, null)
    }

public inline fun <reified C> DataColumn<String?>.convertTo(parserOptions: ParserOptions? = null): DataColumn<C> =
    convertTo(typeOf<C>(), parserOptions) as DataColumn<C>

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

@JvmName("convertToLocalDateTimeFromT")
public fun <T : Any> DataColumn<T>.convertToLocalDateTime(): DataColumn<LocalDateTime> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToLocalDateTime(): DataColumn<LocalDateTime?> = convertTo()

@JvmName("convertToLocalDateFromT")
public fun <T : Any> DataColumn<T>.convertToLocalDate(): DataColumn<LocalDate> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToLocalDate(): DataColumn<LocalDate?> = convertTo()

@JvmName("convertToLocalTimeFromT")
public fun <T : Any> DataColumn<T>.convertToLocalTime(): DataColumn<LocalTime> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToLocalTime(): DataColumn<LocalTime?> = convertTo()

@JvmName("convertToByteFromT")
public fun <T : Any> DataColumn<T>.convertToByte(): DataColumn<Byte> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToByte(): DataColumn<Byte?> = convertTo()

@JvmName("convertToShortFromT")
public fun <T : Any> DataColumn<T>.convertToShort(): DataColumn<Short> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToShort(): DataColumn<Short?> = convertTo()

@JvmName("convertToIntFromT")
public fun <T : Any> DataColumn<T>.convertToInt(): DataColumn<Int> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToInt(): DataColumn<Int?> = convertTo()

@JvmName("convertToLongFromT")
public fun <T : Any> DataColumn<T>.convertToLong(): DataColumn<Long> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToLong(): DataColumn<Long?> = convertTo()

@JvmName("convertToStringFromT")
public fun <T : Any> DataColumn<T>.convertToString(): DataColumn<String> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToString(): DataColumn<String?> = convertTo()

@JvmName("convertToDoubleFromT")
public fun <T : Any> DataColumn<T>.convertToDouble(): DataColumn<Double> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToDouble(): DataColumn<Double?> = convertTo()

/** Parses a String column to Double considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 * If the column cannot be parsed, the POSIX format is used. */
@JvmName("convertToDoubleFromString")
public fun DataColumn<String>.convertToDouble(locale: Locale? = null): DataColumn<Double> =
    convertToDouble(locale = locale, nullStrings = null, useFastDoubleParser = null)

/**
 * Parses a String column to Double considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 *   If the column cannot be parsed, the POSIX format is used.
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

/** Parses a String column to Double considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 * If the column cannot be parsed, the POSIX format is used. */
@JvmName("convertToDoubleFromStringNullable")
public fun DataColumn<String?>.convertToDouble(locale: Locale? = null): DataColumn<Double?> =
    convertToDouble(locale = locale, nullStrings = null, useFastDoubleParser = null)

/**
 * Parses a String column to Double considering locale (number format).
 *
 * If any of the parameters is `null`, the global default (in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser]) is used.
 *
 * @param locale If defined, its number format is used for parsing.
 *   The default in [DataFrame.parser][org.jetbrains.kotlinx.dataframe.DataFrame.Companion.parser] is the system locale.
 *   If the column cannot be parsed, the POSIX format is used.
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

@JvmName("convertToFloatFromT")
public fun <T : Any> DataColumn<T>.convertToFloat(): DataColumn<Float> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToFloat(): DataColumn<Float?> = convertTo()

@JvmName("convertToBigDecimalFromT")
public fun <T : Any> DataColumn<T>.convertToBigDecimal(): DataColumn<BigDecimal> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToBigDecimal(): DataColumn<BigDecimal?> = convertTo()

@JvmName("convertToBigIntegerFromT")
public fun <T : Any> DataColumn<T>.convertToBigInteger(): DataColumn<BigInteger> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToBigInteger(): DataColumn<BigInteger?> = convertTo()

@JvmName("convertToBooleanFromT")
public fun <T : Any> DataColumn<T>.convertToBoolean(): DataColumn<Boolean> = convertTo()

public fun <T : Any> DataColumn<T?>.convertToBoolean(): DataColumn<Boolean?> = convertTo()

// region convert URL

@JvmName("toIframeFromUrlNullable")
@Refine
@Converter<IFRAME>(IFRAME::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, URL?>.toIFrame(
    border: Boolean = false,
    width: Int? = null,
    height: Int? = null,
): DataFrame<T> = to { it.map { url -> url?.let { IFRAME(url.toString(), border, width, height) } } }

@JvmName("toIframeFromUrl")
@Refine
@Converter<IFRAME>(IFRAME::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, URL>.toIFrame(
    border: Boolean = false,
    width: Int? = null,
    height: Int? = null,
): DataFrame<T> = to { it.map { IFRAME(it.toString(), border, width, height) } }

@JvmName("toImgFromUrlNullable")
@Refine
@Converter<IMG>(IMG::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T, R : URL?> Convert<T, URL?>.toImg(width: Int? = null, height: Int? = null): DataFrame<T> =
    to { it.map { url -> url?.let { IMG(url.toString(), width, height) } } }

@JvmName("toImgFromUrl")
@Refine
@Converter<IMG>(IMG::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T, R : URL?> Convert<T, URL>.toImg(width: Int? = null, height: Int? = null): DataFrame<T> =
    to { it.map { IMG(it.toString(), width, height) } }

// endregion

// region toURL

public fun DataColumn<String>.convertToURL(): DataColumn<URL> = map { URL(it) }

@JvmName("convertToURLFromStringNullable")
public fun DataColumn<String?>.convertToURL(): DataColumn<URL?> = map { it?.let { URL(it) } }

@JvmName("toUrlFromStringNullable")
@Refine
@Converter<URL>(URL::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String?>.toURL(): DataFrame<T> = to { it.convertToURL() }

@JvmName("toUrlFromString")
@Refine
@Converter<URL>(URL::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String>.toURL(): DataFrame<T> = to { it.convertToURL() }

// endregion

// region toInstant

public fun DataColumn<String>.convertToInstant(): DataColumn<Instant> = map { Instant.parse(it) }

@JvmName("convertToInstantFromStringNullable")
public fun DataColumn<String?>.convertToInstant(): DataColumn<Instant?> = map { it?.let { Instant.parse(it) } }

@JvmName("toInstantFromStringNullable")
@Refine
@Converter<Instant>(Instant::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String?>.toInstant(): DataFrame<T> = to { it.convertToInstant() }

@JvmName("toInstantFromString")
@Refine
@Converter<Instant>(Instant::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, String>.toInstant(): DataFrame<T> = to { it.convertToInstant() }

// endregion

// region toLocalDate

@JvmName("convertToLocalDateFromLong")
public fun DataColumn<Long>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> =
    map { it.toLocalDate(zone) }

public fun DataColumn<Long?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> =
    map { it?.toLocalDate(zone) }

@JvmName("convertToLocalDateFromInt")
public fun DataColumn<Int>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate> =
    map { it.toLong().toLocalDate(zone) }

@JvmName("convertToLocalDateFromIntNullable")
public fun DataColumn<Int?>.convertToLocalDate(zone: TimeZone = defaultTimeZone): DataColumn<LocalDate?> =
    map { it?.toLong()?.toLocalDate(zone) }

@JvmName("convertToLocalDateFromString")
public fun DataColumn<String>.convertToLocalDate(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDate> {
    val converter = Parsers.getDateTimeConverter(LocalDate::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalDate") }
}

@JvmName("convertToLocalDateFromStringNullable")
public fun DataColumn<String?>.convertToLocalDate(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDate?> {
    val converter = Parsers.getDateTimeConverter(LocalDate::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalDate") } }
}

@JvmName("toLocalDateFromTLongNullable")
@Refine
@Converter<LocalDate>(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDate(zone) }

@JvmName("toLocalDateFromTLong")
@Refine
@Converter<LocalDate>(LocalDate::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDate(zone) }

@JvmName("toLocalDateFromTInt")
@Refine
@Converter<LocalDate>(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDate(zone) }

@JvmName("toLocalDateFromTIntNullable")
@Refine
@Converter<LocalDate>(LocalDate::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int>.toLocalDate(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDate(zone) }

@JvmName("toLocalDateFromStringNullable")
@Refine
@Converter<LocalDate>(LocalDate::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalDate(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    to { it.convertToLocalDate(pattern, locale) }

@JvmName("toLocalDateFromString")
@Refine
@Converter<LocalDate>(LocalDate::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toLocalDate(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    to { it.convertToLocalDate(pattern, locale) }

@Refine
@Converter<LocalDate>(LocalDate::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalDate(): DataFrame<T> = to { it.convertTo<LocalDate>() }

// endregion

// region toLocalTime

@JvmName("convertToLocalTimeFromLong")
public fun DataColumn<Long>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> =
    map { it.toLocalTime(zone) }

public fun DataColumn<Long?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> =
    map { it?.toLocalTime(zone) }

@JvmName("convertToLocalTimeFromInt")
public fun DataColumn<Int>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime> =
    map { it.toLong().toLocalTime(zone) }

@JvmName("convertToLocalTimeIntNullable")
public fun DataColumn<Int?>.convertToLocalTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalTime?> =
    map { it?.toLong()?.toLocalTime(zone) }

@JvmName("convertToLocalTimeFromString")
public fun DataColumn<String>.convertToLocalTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalTime> {
    val converter = Parsers.getDateTimeConverter(LocalTime::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalTime") }
}

@JvmName("convertToLocalTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalTime?> {
    val converter = Parsers.getDateTimeConverter(LocalTime::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalTime") } }
}

@JvmName("toLocalTimeFromTLongNullable")
@Refine
@Converter<LocalTime>(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalTime(zone) }

@JvmName("toLocalTimeFromTLong")
@Refine
@Converter<LocalTime>(LocalTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalTime(zone) }

@JvmName("toLocalTimeFromTIntNullable")
@Refine
@Converter<LocalTime>(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalTime(zone) }

@JvmName("toLocalTimeFromTInt")
@Refine
@Converter<LocalTime>(LocalTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int>.toLocalTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalTime(zone) }

@JvmName("toLocalTimeFromStringNullable")
@Refine
@Converter<LocalTime>(LocalTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    to { it.convertToLocalTime(pattern, locale) }

@JvmName("toLocalTimeFromString")
@Refine
@Converter<LocalTime>(LocalTime::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toLocalTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    to { it.convertToLocalTime(pattern, locale) }

@Refine
@Converter<LocalTime>(LocalTime::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalTime(): DataFrame<T> = to { it.convertTo<LocalTime>() }

// endregion

// region toLocalDateTime

@JvmName("convertToLocalDateTimeFromLong")
public fun DataColumn<Long>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> =
    map { it.toLocalDateTime(zone) }

public fun DataColumn<Long?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> =
    map { it?.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeFromInstant")
public fun DataColumn<Instant>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> =
    map { it.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeFromInstantNullable")
public fun DataColumn<Instant?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> =
    map { it?.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeFromInt")
public fun DataColumn<Int>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime> =
    map { it.toLong().toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeFromIntNullable")
public fun DataColumn<Int?>.convertToLocalDateTime(zone: TimeZone = defaultTimeZone): DataColumn<LocalDateTime?> =
    map { it?.toLong()?.toLocalDateTime(zone) }

@JvmName("convertToLocalDateTimeFromString")
public fun DataColumn<String>.convertToLocalDateTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDateTime> {
    val converter = Parsers.getDateTimeConverter(LocalDateTime::class, pattern, locale)
    return map { converter(it.trim()) ?: error("Can't convert `$it` to LocalDateTime") }
}

@JvmName("convertToLocalDateTimeFromStringNullable")
public fun DataColumn<String?>.convertToLocalDateTime(
    pattern: String? = null,
    locale: Locale? = null,
): DataColumn<LocalDateTime?> {
    val converter = Parsers.getDateTimeConverter(LocalDateTime::class, pattern, locale)
    return map { it?.let { converter(it.trim()) ?: error("Can't convert `$it` to LocalDateTime") } }
}

@JvmName("toLocalDateTimeFromTLongNullable")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDateTime(zone) }

@JvmName("toLocalDateTimeFromTLong")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Long>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDateTime(zone) }

@JvmName("toLocalDateTimeFromTInstantNullable")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Instant?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDateTime(zone) }

@JvmName("toLocalDateTimeFromTInstant")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Instant>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDateTime(zone) }

@JvmName("toLocalDateTimeFromTIntNullable")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int?>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDateTime(zone) }

@JvmName("toLocalDateTimeFromTInt")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypeZone")
public fun <T> Convert<T, Int>.toLocalDateTime(zone: TimeZone = defaultTimeZone): DataFrame<T> =
    to { it.convertToLocalDateTime(zone) }

@JvmName("toLocalDateTimeFromStringNullable")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = true)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String?>.toLocalDateTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    to { it.convertToLocalDateTime(pattern, locale) }

@JvmName("toLocalDateTimeFromString")
@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificTypePattern")
public fun <T> Convert<T, String>.toLocalDateTime(pattern: String? = null, locale: Locale? = null): DataFrame<T> =
    to { it.convertToLocalDateTime(pattern, locale) }

@Refine
@Converter<LocalDateTime>(LocalDateTime::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, *>.toLocalDateTime(): DataFrame<T> = to { it.convertTo<LocalDateTime>() }

// endregion

@JvmName("toIntTAny")
@Refine
@Converter<Int>(Int::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toInt(): DataFrame<T> = to<Int>()

@Refine
@Converter<Int>(Int::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toInt(): DataFrame<T> = to<Int?>()

@JvmName("toLongTAny")
@Refine
@Converter<Long>(Long::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toLong(): DataFrame<T> = to<Long>()

@Refine
@Converter<Long>(Long::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toLong(): DataFrame<T> = to<Long?>()

@JvmName("toStrTAny")
@Refine
@Converter<String>(String::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toStr(): DataFrame<T> = to<String>()

@Refine
@Converter<String>(String::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toStr(): DataFrame<T> = to<String?>()

@JvmName("toDoubleTAny")
@Refine
@Converter<Double>(Double::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toDouble(): DataFrame<T> = to<Double>()

@Refine
@Converter<Double>(Double::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toDouble(): DataFrame<T> = to<Double?>()

@JvmName("toFloatTAny")
@Refine
@Converter<Float>(Float::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toFloat(): DataFrame<T> = to<Float>()

@Refine
@Converter<Float>(Float::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toFloat(): DataFrame<T> = to<Float?>()

@JvmName("toBigDecimalTAny")
@Refine
@Converter<BigDecimal>(BigDecimal::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toBigDecimal(): DataFrame<T> = to<BigDecimal>()

@Refine
@Converter<BigDecimal>(BigDecimal::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBigDecimal(): DataFrame<T> = to<BigDecimal?>()

@JvmName("toBigIntegerTAny")
@Refine
@Converter<BigInteger>(BigInteger::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toBigInteger(): DataFrame<T> = to<BigInteger>()

@Refine
@Converter<BigInteger>(BigInteger::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBigInteger(): DataFrame<T> = to<BigInteger?>()

@JvmName("toBooleanTAny")
@Refine
@Converter<Boolean>(Boolean::class, nullable = false)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any>.toBoolean(): DataFrame<T> = to<Boolean>()

@Refine
@Converter<Boolean>(Boolean::class, nullable = true)
@Interpretable("ToSpecificType")
public fun <T> Convert<T, Any?>.toBoolean(): DataFrame<T> = to<Boolean?>()

public fun <T, C> Convert<T, List<List<C>>>.toDataFrames(containsColumns: Boolean = false): DataFrame<T> =
    to { it.toDataFrames(containsColumns) }

public fun <T> DataColumn<List<List<T>>>.toDataFrames(containsColumns: Boolean = false): DataColumn<AnyFrame> =
    map { it.toDataFrame(containsColumns) }
