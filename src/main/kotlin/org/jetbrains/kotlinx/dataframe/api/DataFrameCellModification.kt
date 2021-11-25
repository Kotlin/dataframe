package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueExpression
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.convertRowColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.api.convertToTypeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone
import org.jetbrains.kotlinx.dataframe.impl.api.explodeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.implodeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.splitDefault
import org.jetbrains.kotlinx.dataframe.impl.api.splitImpl
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWithValuePerColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.api.withRowCellImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.createTypeWithArgument
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.io.toDataFrame
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.typeClass
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.KType

// region update

public fun <T, C> DataFrame<T>.update(columns: ColumnsSelector<T, C>): UpdateClause<T, C> =
    UpdateClause(this, null, columns)

public fun <T, C> DataFrame<T>.update(columns: Iterable<ColumnReference<C>>): UpdateClause<T, C> =
    update { columns.toColumnSet() }

public fun <T> DataFrame<T>.update(vararg columns: String): UpdateClause<T, Any?> = update { columns.toColumns() }
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): UpdateClause<T, C> = update { columns.toColumns() }
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): UpdateClause<T, C> =
    update { columns.toColumns() }

public data class UpdateClause<T, C>(
    val df: DataFrame<T>,
    val filter: RowValueFilter<T, C>?,
    val columns: ColumnsSelector<T, C>
) {
    public fun <R : C> cast(): UpdateClause<T, R> =
        UpdateClause(df, filter as RowValueFilter<T, R>?, columns as ColumnsSelector<T, R>)
}

public fun <T, C> UpdateClause<T, C>.where(predicate: RowValueFilter<T, C>): UpdateClause<T, C> =
    copy(filter = filter and predicate)

public fun <T, C> UpdateClause<T, C>.at(rowIndices: Collection<Int>): UpdateClause<T, C> = where { index in rowIndices }
public fun <T, C> UpdateClause<T, C>.at(vararg rowIndices: Int): UpdateClause<T, C> = at(rowIndices.toSet())
public fun <T, C> UpdateClause<T, C>.at(rowRange: IntRange): UpdateClause<T, C> = where { index in rowRange }

public infix fun <T, C> UpdateClause<T, C>.perRowCol(expression: RowColumnExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, column, _ -> expression(row, column) }

public infix fun <T, C> UpdateClause<T, C>.with(expression: RowValueExpression<T, C, C>): DataFrame<T> =
    withExpression(expression)

public fun <T, C> UpdateClause<T, C>.asNullable(): UpdateClause<T, C?> = this as UpdateClause<T, C?>

public fun <T, C> UpdateClause<T, C>.perCol(values: Map<String, C>): DataFrame<T> = updateWithValuePerColumnImpl {
    values[it.name()] ?: throw IllegalArgumentException("Update value for column ${it.name()} is not defined")
}

public fun <T, C> UpdateClause<T, C>.perCol(values: AnyRow): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

public fun <T, C> UpdateClause<T, C>.perCol(valueSelector: Selector<DataColumn<C>, C>): DataFrame<T> =
    updateWithValuePerColumnImpl(valueSelector)

public fun <T, C> UpdateClause<T, C>.withExpression(expression: RowValueExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, _, value ->
        expression(row, value)
    }

internal infix fun <T, C> RowValueFilter<T, C>?.and(other: RowValueFilter<T, C>): RowValueFilter<T, C> {
    if (this == null) return other
    val thisExp = this
    return { thisExp(this, it) && other(this, it) }
}

public fun <T, C> UpdateClause<T, C?>.notNull(): UpdateClause<T, C> =
    copy(filter = filter and { it != null }) as UpdateClause<T, C>

public fun <T, C> UpdateClause<T, C?>.notNull(expression: RowValueExpression<T, C, C>): DataFrame<T> =
    notNull().updateImpl { row, column, value ->
        expression(row, value)
    }

public fun <T, C> DataFrame<T>.update(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    expression: RowValueExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public fun <T, C> DataFrame<T>.update(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    expression: RowValueExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public fun <T> DataFrame<T>.update(
    firstCol: String,
    vararg cols: String,
    expression: RowValueExpression<T, Any?, Any?>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).withExpression(expression)

public fun <T, C> UpdateClause<T, C>.withNull(): DataFrame<T> = asNullable().withValue(null)

public fun <T, C> UpdateClause<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }

public infix fun <T, C> UpdateClause<T, C>.withValue(value: C): DataFrame<T> = withExpression { value }

// endregion

// region convert

public fun <T, C> DataFrame<T>.convert(columns: ColumnsSelector<T, C>): ConvertClause<T, C> =
    ConvertClause(this, columns)

public fun <T, C> DataFrame<T>.convert(vararg columns: KProperty<C>): ConvertClause<T, C> =
    convert { columns.toColumns() }

public fun <T> DataFrame<T>.convert(vararg columns: String): ConvertClause<T, Any?> = convert { columns.toColumns() }
public fun <T, C> DataFrame<T>.convert(vararg columns: ColumnReference<C>): ConvertClause<T, C> =
    convert { columns.toColumns() }

public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    noinline expression: RowValueExpression<T, C, R>
): DataFrame<T> =
    convert(*headPlusArray(firstCol, cols)).with(inferType = false, expression)

public inline fun <T, C, reified R> DataFrame<T>.convert(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    noinline expression: RowValueExpression<T, C, R>
): DataFrame<T> =
    convert(*headPlusArray(firstCol, cols)).with(inferType = false, expression)

public inline fun <T, reified R> DataFrame<T>.convert(
    firstCol: String,
    vararg cols: String,
    noinline expression: RowValueExpression<T, Any?, R>
): DataFrame<T> =
    convert(*headPlusArray(firstCol, cols)).with(inferType = false, expression)

public inline fun <T, C, reified R> ConvertClause<T, C?>.notNull(crossinline expression: RowValueExpression<T, C, R>): DataFrame<T> =
    with {
        if (it == null) null
        else expression(this, it)
    }

public data class ConvertClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>) {
    public fun <R> cast(): ConvertClause<T, R> = ConvertClause(df, columns as ColumnsSelector<T, R>)

    public inline fun <reified D> to(): DataFrame<T> = to(getType<D>())
}

public fun <T> ConvertClause<T, *>.to(type: KType): DataFrame<T> = to { it.convertTo(type) }

public inline fun <T, C, reified R> ConvertClause<T, C>.with(
    inferType: Boolean = false,
    noinline rowConverter: RowValueExpression<T, C, R>
): DataFrame<T> =
    withRowCellImpl(if (inferType) null else getType<R>(), rowConverter)

public inline fun <T, C, reified R> ConvertClause<T, C>.perRowCol(
    inferType: Boolean = false,
    noinline expression: RowColumnExpression<T, C, R>
): DataFrame<T> =
    convertRowColumnImpl(if (inferType) null else getType<R>(), expression)

public fun <T, C> ConvertClause<T, C>.to(columnConverter: DataFrame<T>.(DataColumn<C>) -> AnyCol): DataFrame<T> =
    df.replace(columns).with { columnConverter(df, it) }

public inline fun <reified C> AnyCol.convertTo(): DataColumn<C> = convertTo(getType<C>()) as DataColumn<C>

public fun AnyCol.toDateTime(): DataColumn<LocalDateTime> = convertTo()
public fun AnyCol.toDate(): DataColumn<LocalDate> = convertTo()
public fun AnyCol.toTime(): DataColumn<LocalTime> = convertTo()
public fun AnyCol.toInt(): DataColumn<Int> = convertTo()
public fun AnyCol.toStr(): DataColumn<String> = convertTo()
public fun AnyCol.toDouble(): DataColumn<Double> = convertTo()

public fun AnyCol.convertTo(newType: KType): AnyCol = convertToTypeImpl(newType)

public fun <T> ConvertClause<T, *>.toInt(): DataFrame<T> = to<Int>()
public fun <T> ConvertClause<T, *>.toDouble(): DataFrame<T> = to<Double>()
public fun <T> ConvertClause<T, *>.toFloat(): DataFrame<T> = to<Float>()
public fun <T> ConvertClause<T, *>.toStr(): DataFrame<T> = to<String>()
public fun <T> ConvertClause<T, *>.toLong(): DataFrame<T> = to<Long>()
public fun <T> ConvertClause<T, *>.toBigDecimal(): DataFrame<T> = to<BigDecimal>()

public fun <T> ConvertClause<T, *>.toDate(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.toLocalDate(zone) }
public fun <T> ConvertClause<T, *>.toTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.toLocalTime(zone) }
public fun <T> ConvertClause<T, *>.toDateTime(zone: ZoneId = defaultTimeZone): DataFrame<T> =
    to { it.toLocalDateTime(zone) }

public fun <T, C> ConvertClause<T, List<List<C>>>.toDataFrames(containsColumns: Boolean = false): DataFrame<T> =
    to { it.toDataFrames(containsColumns) }

public fun AnyCol.toLocalDate(zone: ZoneId = defaultTimeZone): DataColumn<LocalDate> = when (typeClass) {
    Long::class -> cast<Long>().map { it.toLocalDate(zone) }
    Int::class -> cast<Int>().map { it.toLong().toLocalDate(zone) }
    else -> convertTo(getType<LocalDate>()).cast()
}

public fun AnyCol.toLocalDateTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalDateTime> = when (typeClass) {
    Long::class -> cast<Long>().map { it.toLocalDateTime(zone) }
    Int::class -> cast<Int>().map { it.toLong().toLocalDateTime(zone) }
    else -> convertTo(getType<LocalDateTime>()).cast()
}

public fun AnyCol.toLocalTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalTime> = when (typeClass) {
    Long::class -> cast<Long>().map { it.toLocalDateTime(zone).toLocalTime() }
    Int::class -> cast<Int>().map { it.toLong().toLocalDateTime(zone).toLocalTime() }
    else -> convertTo(getType<LocalTime>()).cast()
}

public fun <T> DataColumn<List<List<T>>>.toDataFrames(containsColumns: Boolean = false): DataColumn<AnyFrame> =
    map { it.toDataFrame(containsColumns) }

// endregion

// region parse

public val DataFrame.Companion.parser: GlobalParserOptions get() = Parsers

public fun <T> DataFrame<T>.parse(options: ParserOptions? = null, columns: ColumnsSelector<T, Any?>): DataFrame<T> =
    parseImpl(options, columns)

public fun <T> DataFrame<T>.parse(vararg columns: String, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumns() }

public fun <T, C> DataFrame<T>.parse(vararg columns: ColumnReference<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumns() }

public fun <T, C> DataFrame<T>.parse(vararg columns: KProperty<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumns() }

public interface GlobalParserOptions {

    public fun addDateTimeFormat(format: String)

    public fun addDateTimeFormatter(formatter: DateTimeFormatter)

    public fun addNullString(str: String)

    public fun resetToDefault()

    public var locale: Locale
}

public data class ParserOptions(
    val locale: Locale? = null,
    val dateTimeFormatter: DateTimeFormatter? = null,
    val nulls: Set<String>? = null
)

public fun DataColumn<String?>.tryParse(options: ParserOptions? = null): DataColumn<*> = tryParseImpl(options)

public fun <T> DataFrame<T>.parse(options: ParserOptions? = null): DataFrame<T> = parse(options) { dfsLeafs() }

public fun DataColumn<String?>.parse(options: ParserOptions? = null): DataColumn<*> =
    tryParse(options).also { if (it.typeClass == String::class) error("Can't guess column type") }

@JvmName("parseAnyFrame?")
public fun DataColumn<AnyFrame?>.parse(options: ParserOptions? = null): DataColumn<AnyFrame?> =
    map { it?.parse(options) }

// endregion

// region split

public fun <T, C> DataFrame<T>.split(columns: ColumnsSelector<T, C?>): Split<T, C> =
    Split(this, columns)

public fun <T> DataFrame<T>.split(vararg columns: String): Split<T, Any> = split { columns.toColumns() }
public fun <T, C> DataFrame<T>.split(vararg columns: ColumnReference<C?>): Split<T, C> = split { columns.toColumns() }
public fun <T, C> DataFrame<T>.split(vararg columns: KProperty<C?>): Split<T, C> = split { columns.toColumns() }

public data class Split<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C?>,
) {
    public fun <P> cast(): Split<T, P> = this as Split<T, P>
}

public data class SplitWithTransform<T, C, R>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C?>,
    internal val inward: Boolean,
    internal val targetType: KType,
    internal val default: R? = null,
    internal val transform: DataRow<T>.(C) -> Iterable<R>
)

public typealias ColumnNamesGenerator<C> = ColumnWithPath<C>.(extraColumnIndex: Int) -> String

// region default

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.default(value: R?): SplitWithTransform<T, C, R> =
    by { it }.default(value)

public fun <T> Split<T, String>.default(value: String?): SplitWithTransform<T, String, String> =
    by { it.splitDefault() }.default(value)

public fun <T, C, R> SplitWithTransform<T, C, R>.default(value: R?): SplitWithTransform<T, C, R> = copy(default = value)

// endregion

// region by

public inline fun <T, C, reified R> Split<T, C>.by(noinline splitter: DataRow<T>.(C) -> Iterable<R>): SplitWithTransform<T, C, R> =
    by(getType<R>(), splitter)

public fun <T, C> Split<T, C>.by(
    vararg delimiters: Char,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0
): SplitWithTransform<T, C, String> = by {
    it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
        if (trim) it.map { it.trim() }
        else it
    }
}

public fun <T, C> Split<T, C>.by(
    regex: Regex,
    trim: Boolean = true,
    limit: Int = 0
): SplitWithTransform<T, C, String> = by {
    it.toString().split(regex, limit = limit).let {
        if (trim) it.map { it.trim() }
        else it
    }
}

public fun <T, C> Split<T, C>.by(
    vararg delimiters: String,
    trim: Boolean = true,
    ignoreCase: Boolean = false,
    limit: Int = 0
): SplitWithTransform<T, C, String> = by {
    it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
        if (trim) it.map { it.trim() }
        else it
    }
}

@PublishedApi
internal fun <T, C, R> Split<T, C>.by(
    type: KType,
    splitter: DataRow<T>.(C) -> Iterable<R>
): SplitWithTransform<T, C, R> {
    return SplitWithTransform(df, columns, false, type) {
        if (it == null) emptyList() else splitter(it).asList()
    }
}

// endregion

// region match

public fun <T, C : String?> Split<T, C>.match(regex: String): SplitWithTransform<T, C, String?> = match(regex.toRegex())

public fun <T, C : String?> Split<T, C>.match(regex: Regex): SplitWithTransform<T, C, String?> = by {
    it?.let { regex.matchEntire(it)?.groups?.drop(1)?.map { it?.value } } ?: emptyList<String>()
}

// endregion

internal fun <T, C> Split<T, C>.toDataFrame(): DataFrame<T> = by {
    when (it) {
        is List<*> -> it
        is AnyFrame -> it.rows()
        else -> listOf(it)
    }
}.into()

// region into

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>
): DataFrame<T> =
    into(listOf(firstName.name()) + otherNames.map { it.name() })

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = into(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    names: List<String>,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = splitImpl(this) { numberOfNewCols ->
    if (extraNamesGenerator != null && names.size < numberOfNewCols) {
        names + (1..(numberOfNewCols - names.size)).map { extraNamesGenerator(this, it) }
    } else names
}

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.into(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    by { it }.into(names.toList(), extraNamesGenerator)

@JvmName("splitDataFrameInto")
public fun <T, C> Split<T, DataFrame<C>>.into(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<DataFrame<C>>? = null
): DataFrame<T> =
    by { it.rows() }.into(names.toList(), extraNamesGenerator)

public fun <T, A, B> Split<T, Pair<A, B>>.into(
    firstCol: String,
    secondCol: String
): DataFrame<T> =
    by { listOf(it.first, it.second) }.into(firstCol, secondCol)

public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.into(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>
): DataFrame<T> =
    by { listOf(it.first, it.second) }.into(firstCol, secondCol)

@JvmName("intoTC")
public fun <T> Split<T, String>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> =
    by { it.splitDefault() }.into(names.toList(), extraNamesGenerator)

// endregion

// region inward

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    names: Iterable<String>,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    copy(inward = true).into(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> = inward(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.inward(
    firstName: ColumnAccessor<*>,
    vararg otherNames: ColumnAccessor<*>
): DataFrame<T> =
    inward(listOf(firstName.name()) + otherNames.map { it.name() })

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inward(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    by { it }.inward(names.toList(), extraNamesGenerator)

@JvmName("splitDataFrameInward")
public fun <T, C : DataFrame<R>, R> Split<T, C>.inward(
    vararg names: String,
    extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    by { it.rows() }.inward(names.toList(), extraNamesGenerator)

public fun <T, A, B> Split<T, Pair<A, B>>.inward(
    firstCol: String,
    secondCol: String
): DataFrame<T> =
    by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

public inline fun <T, reified A, reified B> Split<T, Pair<A, B>>.inward(
    firstCol: ColumnAccessor<A>,
    secondCol: ColumnAccessor<B>
): DataFrame<T> =
    by { listOf(it.first, it.second) }.inward(firstCol, secondCol)

@JvmName("inwardTC")
public fun <T> Split<T, String>.inward(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> =
    by { it.splitDefault() }.inward(names.toList(), extraNamesGenerator)

// endregion

// region intoColumns

public fun <T, C : AnyFrame> Split<T, C>.intoColumns(): DataFrame<T> {
    return df.convert(columns).with {
        it?.implode { all() }?.get(0)
    }
}

// endregion

// region intoRows

@JvmName("intoRowsTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it }
        .intoRows(dropEmpty)

@JvmName("intoRowsFrame")
public fun <T, C : AnyFrame> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> =
    by { it.rows() }.intoRows(dropEmpty)

internal fun <T, C, R> ConvertClause<T, C?>.splitInplace(targetType: KType, transform: DataRow<T>.(C) -> Iterable<R>) =
    withRowCellImpl(List::class.createTypeWithArgument(targetType)) { if (it == null) emptyList() else transform(it).asList() }

public fun <T, C, R> SplitWithTransform<T, C, R>.intoRows(dropEmpty: Boolean = true): DataFrame<T> {
    val paths = df.getColumnPaths(columns).toColumnSet()
    return df.convert { paths as ColumnSet<C?> }.splitInplace(targetType, transform).explode(dropEmpty) { paths }
}

// endregion

// region inplace

@JvmName("inplaceTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inplace(): DataFrame<T> = by { it }.inplace()

public fun <T, C, R> SplitWithTransform<T, C, R>.inplace(): DataFrame<T> = df.convert(columns).splitInplace(targetType, transform)

// endregion

// endregion

// region merge

public fun <T, C> DataFrame<T>.merge(selector: ColumnsSelector<T, C>): MergeClause<T, C, List<C>> =
    MergeClause(this, selector, false, { it })

public fun <T> DataFrame<T>.merge(vararg columns: String): MergeClause<T, Any?, List<Any?>> =
    merge { columns.toColumns() }

public fun <T, C> DataFrame<T>.merge(vararg columns: ColumnReference<C>): MergeClause<T, C, List<C>> =
    merge { columns.toColumns() }

public fun <T, C> DataFrame<T>.merge(vararg columns: KProperty<C>): MergeClause<T, C, List<C>> =
    merge { columns.toColumns() }

public data class MergeClause<T, C, R>(
    @PublishedApi
    internal val df: DataFrame<T>,
    @PublishedApi
    internal val selector: ColumnsSelector<T, C>,
    @PublishedApi
    internal val notNull: Boolean,
    @PublishedApi
    internal val transform: DataRow<T>.(List<C>) -> R,
)

public fun <T, C, R> MergeClause<T, C, R>.notNull(): MergeClause<T, C, R> = copy(notNull = true)

public fun <T, C, R> MergeClause<T, C, R>.into(columnName: String): DataFrame<T> = into(pathOf(columnName))
public fun <T, C, R> MergeClause<T, C, R>.into(column: ColumnAccessor<R>): DataFrame<T> = into(column.path())

public fun <T, C, R> MergeClause<T, C, R>.intoList(): List<R> =
    df.select(selector).rows().map { transform(it, it.values() as List<C>) }

public fun <T, C, R> MergeClause<T, C, R>.into(columnPath: ColumnPath): DataFrame<T> {
    val grouped = df.move(selector).under { columnPath }
    val res = grouped.convert { getColumnGroup(columnPath) }.withRowCellImpl(null) {
        val srcRow = df[index()]
        var values = it.values() as List<C>
        if (notNull) {
            values = values.filter {
                it != null && (it !is AnyRow || !it.isEmpty())
            }
        }
        transform(srcRow, values)
    }
    return res
}

public fun <T, C, R> MergeClause<T, C, R>.asStrings(): MergeClause<T, C, String> = by(", ")
public fun <T, C, R> MergeClause<T, C, R>.by(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "..."
): MergeClause<T, C, String> =
    MergeClause(df, selector, notNull) {
        it.joinToString(
            separator = separator,
            prefix = prefix,
            postfix = postfix,
            limit = limit,
            truncated = truncated
        )
    }

public inline fun <T, C, R, reified V> MergeClause<T, C, R>.by(crossinline transform: DataRow<T>.(R) -> V): MergeClause<T, C, V> =
    MergeClause(df, selector, notNull) { transform(this@by.transform(this, it)) }

// endregion

// region explode

public fun <T> DataFrame<T>.explode(
    dropEmpty: Boolean = true,
    selector: ColumnsSelector<T, *> = { all() }
): DataFrame<T> = explodeImpl(dropEmpty, selector)

public fun <T> DataFrame<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

public fun <T, C> DataFrame<T>.explode(vararg columns: ColumnReference<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

public fun <T, C> DataFrame<T>.explode(vararg columns: KProperty<C>, dropEmpty: Boolean = true): DataFrame<T> =
    explode(dropEmpty) { columns.toColumns() }

@JvmName("explodeList")
public fun <T> DataColumn<Collection<T>>.explode(): DataColumn<T> = explodeImpl() as DataColumn<T>

@JvmName("explodeFrames")
public fun <T> DataColumn<DataFrame<T>>.explode(): ColumnGroup<T> = concat().toColumnGroup(name())

// endregion

// region implode

public fun <T, C> DataFrame<T>.implode(dropNulls: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> =
    implodeImpl(dropNulls, columns)

public fun <T> DataFrame<T>.implode(vararg columns: String, dropNulls: Boolean = false): DataFrame<T> =
    implode(dropNulls) { columns.toColumns() }

public fun <T, C> DataFrame<T>.implode(vararg columns: ColumnReference<C>, dropNulls: Boolean = false): DataFrame<T> =
    implode(dropNulls) { columns.toColumns() }

public fun <T, C> DataFrame<T>.implode(vararg columns: KProperty<C>, dropNulls: Boolean = false): DataFrame<T> =
    implode(dropNulls) { columns.toColumns() }

// endregion
