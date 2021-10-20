package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.RowCellFilter
import org.jetbrains.kotlinx.dataframe.RowCellSelector
import org.jetbrains.kotlinx.dataframe.RowColumnSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.emptyMany
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.convertToImpl
import org.jetbrains.kotlinx.dataframe.impl.api.defaultTimeZone
import org.jetbrains.kotlinx.dataframe.impl.api.explodeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.mergeRowsImpl
import org.jetbrains.kotlinx.dataframe.impl.api.splitDefault
import org.jetbrains.kotlinx.dataframe.impl.api.splitImpl
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDate
import org.jetbrains.kotlinx.dataframe.impl.api.toLocalDateTime
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.with
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
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
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

// region update

public fun <T, C> DataFrame<T>.update(selector: ColumnsSelector<T, C>): UpdateClause<T, C> = UpdateClause(this, null, selector, null, false, null)
public fun <T, C> DataFrame<T>.update(cols: Iterable<ColumnReference<C>>): UpdateClause<T, C> = update { cols.toColumnSet() }
public fun <T> DataFrame<T>.update(vararg cols: String): UpdateClause<T, Any?> = update { cols.toColumns() }
public fun <T, C> DataFrame<T>.update(vararg cols: KProperty<C>): UpdateClause<T, C> = update { cols.toColumns() }
public fun <T, C> DataFrame<T>.update(vararg cols: ColumnReference<C>): UpdateClause<T, C> = update { cols.toColumns() }

public data class UpdateClause<T, C>(
    val df: DataFrame<T>,
    val filter: RowCellFilter<T, C>?,
    val selector: ColumnsSelector<T, C>,
    val targetType: KType?,
    val toNull: Boolean = false,
    val typeSuggestions: ((KClass<*>) -> KType)?
) {
    public fun <R> cast(): UpdateClause<T, R> = UpdateClause(df, filter as RowCellFilter<T, R>?, selector as ColumnsSelector<T, R>, targetType, toNull, typeSuggestions)

    public inline fun <reified R> toType(): UpdateClause<T, C> = UpdateClause(df, filter, selector, getType<R>(), toNull, typeSuggestions)
}

public fun <T, C> UpdateClause<T, C>.where(predicate: RowCellFilter<T, C>): UpdateClause<T, C> = copy(filter = predicate)

public fun <T, C> UpdateClause<T, C>.at(rowIndices: Collection<Int>): UpdateClause<T, C> = where { index in rowIndices }
public fun <T, C> UpdateClause<T, C>.at(vararg rowIndices: Int): UpdateClause<T, C> = at(rowIndices.toList())
public fun <T, C> UpdateClause<T, C>.at(rowRange: IntRange): UpdateClause<T, C> = where { index in rowRange }

public fun <T, C> UpdateClause<T, C>.guessTypes(): UpdateClause<T, C> = copy(targetType = null) { it.createStarProjectedType(false) }

public fun <T, C> UpdateClause<T, C>.toType(type: KType): UpdateClause<T, C> = copy(targetType = type)

public fun <T, C> UpdateClause<T, C>.suggestTypes(vararg suggestions: Pair<KClass<*>, KType>): UpdateClause<T, C> {
    val map = suggestions.toMap()
    return copy(targetType = null) { map[it] ?: it.createStarProjectedType(false) }
}

public inline infix fun <T, C, reified R> UpdateClause<T, C>.withRowCol(noinline expression: RowColumnSelector<T, C, R>): DataFrame<T> = updateImpl(copy(targetType = targetType ?: getType<R>()), expression)

public fun <T, C, R> UpdateClause<T, C>.with(targetType: KType?, expression: RowCellSelector<T, C, R>): DataFrame<T> = updateImpl(copy(filter = null, targetType = targetType)) { row, column ->
    val currentValue = column[row.index]
    if (filter?.invoke(row, currentValue) == false) {
        currentValue as R
    } else expression(row, currentValue)
}

public inline infix fun <T, C, reified R> UpdateClause<T, C>.with(noinline expression: RowCellSelector<T, C, R>): DataFrame<T> = copy(targetType = targetType ?: getType<R>()).withExpression(expression)

public fun <T, C, R> UpdateClause<T, C>.withExpression(expression: RowCellSelector<T, C, R>): DataFrame<T> = updateImpl(copy(filter = null)) { row, column ->
    val currentValue = column[row.index]
    if (filter?.invoke(row, currentValue) == false) {
        currentValue
    } else expression(row, currentValue)
}

internal infix fun <T, C> RowCellFilter<T, C>?.and(other: RowCellFilter<T, C>): RowCellFilter<T, C> {
    if (this == null) return other
    val thisExp = this
    return { thisExp(this, it) && other(this, it) }
}

public fun <T, C> UpdateClause<T, C?>.notNull(): UpdateClause<T, C> = copy(filter = filter and { it != null }) as UpdateClause<T, C>

public inline fun <T, C, reified R> UpdateClause<T, C?>.notNull(noinline expression: RowCellSelector<T, C, R>): DataFrame<T> = updateImpl(copy(filter = null, targetType = targetType ?: getType<R>())) { row, column ->
    val currentValue = column[row.index]
    if (currentValue == null) {
        null
    } else expression(row, currentValue)
}

public inline fun <T, C, reified R> DataFrame<T>.update(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    noinline expression: RowCellSelector<T, C, R>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public inline fun <T, C, reified R> DataFrame<T>.update(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    noinline expression: RowCellSelector<T, C, R>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public inline fun <T, reified R> DataFrame<T>.update(
    firstCol: String,
    vararg cols: String,
    noinline expression: RowCellSelector<T, Any?, R>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

public fun <T, C> UpdateClause<T, C>.withNull(): DataFrame<T> = updateImpl(copy(filter = null, targetType = null, typeSuggestions = null, toNull = true)) { row, column ->
    if (filter != null) {
        val currentValue = column[row.index]
        if (!filter.invoke(row, currentValue)) {
            currentValue
        } else null
    } else null
}

public inline infix fun <T, C, reified R> UpdateClause<T, C>.with(value: R): DataFrame<T> = with { value }

// endregion

// region convert

public fun <T, C> DataFrame<T>.convert(selector: ColumnsSelector<T, C>): ConvertClause<T, C> = ConvertClause(this, selector)
public fun <T, C> DataFrame<T>.convert(vararg columns: KProperty<C>): ConvertClause<T, C> = convert { columns.toColumns() }
public fun <T> DataFrame<T>.convert(vararg columns: String): ConvertClause<T, Any?> = convert { columns.toColumns() }
public fun <T, C> DataFrame<T>.convert(vararg columns: ColumnReference<C>): ConvertClause<T, C> = convert { columns.toColumns() }

public data class ConvertClause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>) {
    public inline fun <reified D> to(): DataFrame<T> = to(getType<D>())
}

public fun <T> ConvertClause<T, *>.to(type: KType): DataFrame<T> = to { it.convertTo(type) }

public inline fun <T, C, reified R> ConvertClause<T, C>.with(noinline rowConverter: DataRow<T>.(C) -> R): DataFrame<T> =
    with(getType<R>(), rowConverter)

public fun <T, C> ConvertClause<T, C>.to(columnConverter: (DataColumn<C>) -> AnyCol): DataFrame<T> =
    df.replace(selector).with { columnConverter(it) }

public inline fun <reified C> AnyCol.convertTo(): DataColumn<C> = convertTo(getType<C>()) as DataColumn<C>

public fun AnyCol.convertToDateTime(): DataColumn<LocalDateTime> = convertTo()
public fun AnyCol.convertToDate(): DataColumn<LocalDate> = convertTo()
public fun AnyCol.convertToTime(): DataColumn<LocalTime> = convertTo()
public fun AnyCol.convertToInt(): DataColumn<Int> = convertTo()
public fun AnyCol.convertToString(): DataColumn<String> = convertTo()
public fun AnyCol.convertToDouble(): DataColumn<Double> = convertTo()

public fun AnyCol.convertTo(newType: KType): AnyCol = convertToImpl(newType)

public fun <T> ConvertClause<T, *>.toInt(): DataFrame<T> = to<Int>()
public fun <T> ConvertClause<T, *>.toDouble(): DataFrame<T> = to<Double>()
public fun <T> ConvertClause<T, *>.toFloat(): DataFrame<T> = to<Float>()
public fun <T> ConvertClause<T, *>.toStr(): DataFrame<T> = to<String>()
public fun <T> ConvertClause<T, *>.toLong(): DataFrame<T> = to<Long>()
public fun <T> ConvertClause<T, *>.toBigDecimal(): DataFrame<T> = to<BigDecimal>()

public fun <T> ConvertClause<T, *>.toDate(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.toLocalDate(zone) }
public fun <T> ConvertClause<T, *>.toTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.toLocalTime(zone) }
public fun <T> ConvertClause<T, *>.toDateTime(zone: ZoneId = defaultTimeZone): DataFrame<T> = to { it.toLocalDateTime(zone) }

public fun <T, C> ConvertClause<T, Many<Many<C>>>.toDataFrames(containsColumns: Boolean = false): DataFrame<T> =
    to { it.toDataFrames(containsColumns) }

public fun AnyCol.toLocalDate(zone: ZoneId = defaultTimeZone): DataColumn<LocalDate> = when (typeClass) {
    Long::class -> typed<Long>().map { it.toLocalDate(zone) }
    Int::class -> typed<Int>().map { it.toLong().toLocalDate(zone) }
    else -> convertTo(getType<LocalDate>()).typed()
}

public fun AnyCol.toLocalDateTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalDateTime> = when (typeClass) {
    Long::class -> typed<Long>().map { it.toLocalDateTime(zone) }
    Int::class -> typed<Int>().map { it.toLong().toLocalDateTime(zone) }
    else -> convertTo(getType<LocalDateTime>()).typed()
}

public fun AnyCol.toLocalTime(zone: ZoneId = defaultTimeZone): DataColumn<LocalTime> = when (typeClass) {
    Long::class -> typed<Long>().map { it.toLocalDateTime(zone).toLocalTime() }
    Int::class -> typed<Int>().map { it.toLong().toLocalDateTime(zone).toLocalTime() }
    else -> convertTo(getType<LocalTime>()).typed()
}

public fun <T> DataColumn<Many<Many<T>>>.toDataFrames(containsColumns: Boolean = false): DataColumn<AnyFrame> =
    map { it.toDataFrame(containsColumns) }

// endregion

// region parse

public val DataFrame.Companion.parser: DataFrameParserOptions get() = Parsers

public interface DataFrameParserOptions {

    public fun addDateTimeFormat(format: String)
}

public fun DataColumn<String?>.tryParse(): DataColumn<*> = tryParseImpl()

public fun <T> DataFrame<T>.parse(): DataFrame<T> = parse { dfs() }

public fun <T> DataFrame<T>.parse(columns: ColumnsSelector<T, Any?>): DataFrame<T> = convert(columns).to {
    when {
        it.isFrameColumn() -> it.castTo<AnyFrame?>().parse()
        it.typeClass == String::class -> it.castTo<String?>().tryParse()
        else -> it
    }
}

public fun DataColumn<String?>.parse(): DataColumn<*> = tryParse().also { if (it.typeClass == String::class) error("Can't guess column type") }

@JvmName("tryParseAnyFrame?")
public fun DataColumn<AnyFrame?>.parse(): DataColumn<AnyFrame?> = map { it?.parse() }

// endregion

// region split

public fun <T, C> DataFrame<T>.split(selector: ColumnsSelector<T, C?>): Split<T, C> =
    SplitClause(this, selector)

public fun <T> DataFrame<T>.split(column: String): Split<T, Any> = split { column.toColumnAccessor() }
public fun <T, C> DataFrame<T>.split(column: ColumnReference<C?>): Split<T, C> = split { column }
public fun <T, C> DataFrame<T>.split(column: KProperty<C?>): Split<T, C> = split { column.toColumnAccessor() }

public interface Split<out T, out C> {

    public fun by(vararg delimiters: String, trim: Boolean = true, ignoreCase: Boolean = false, limit: Int = 0): SplitWithTransform<T, C, String> = with {
        it.toString().split(*delimiters, ignoreCase = ignoreCase, limit = limit).let {
            if (trim) it.map { it.trim() }
            else it
        }
    }
}

public typealias ColumnNamesGenerator<C> = ColumnWithPath<C>.(extraColumnIndex: Int) -> String

public interface SplitWithTransform<out T, out C, in R> {

    public fun intoRows(dropEmpty: Boolean = true): DataFrame<T>

    public fun inplace(): DataFrame<T>

    public fun inward(vararg names: String, extraNamesGenerator: ColumnNamesGenerator<C>? = null): DataFrame<T> = inward(names.toList(), extraNamesGenerator)

    public fun inward(names: Iterable<String>, extraNamesGenerator: ColumnNamesGenerator<C>? = null): DataFrame<T>
}

public class SplitClause<T, C>(
    public val df: DataFrame<T>,
    public val columns: ColumnsSelector<T, C?>
) : Split<T, C>

public inline fun <T, C, reified R> Split<T, C>.with(noinline splitter: (C) -> Iterable<R>): SplitWithTransform<T, C, R> = with(
    getType<R>(), splitter
)

@PublishedApi
internal fun <T, C, R> Split<T, C>.with(type: KType, splitter: (C) -> Iterable<R>): SplitWithTransform<T, C, R> {
    require(this is SplitClause<T, C>)
    return SplitClauseWithTransform(df, columns, false, type) {
        if (it == null) emptyMany() else splitter(it).toMany()
    }
}

public data class SplitClauseWithTransform<T, C, R>(
    val df: DataFrame<T>,
    val columns: ColumnsSelector<T, C?>,
    val inward: Boolean,
    val targetType: KType,
    val transform: (C) -> Iterable<R>
) : SplitWithTransform<T, C, R> {

    override fun intoRows(dropEmpty: Boolean): DataFrame<T> = df.explode(dropEmpty, columns)

    override fun inplace(): DataFrame<T> = df.convert(columns).with(Many::class.createTypeWithArgument(targetType)) { if (it == null) emptyMany() else transform(it).toMany() }

    override fun inward(names: Iterable<String>, extraNamesGenerator: ColumnNamesGenerator<C>?): DataFrame<T> = copy(inward = true).into(names.toList(), extraNamesGenerator)
}

public class FrameSplit<T, C>(
    public val df: DataFrame<T>,
    public val columns: ColumnSelector<T, DataFrame<C>?>
)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    firstName: ColumnReference<*>,
    vararg otherNames: ColumnReference<*>
): DataFrame<T> =
    into(listOf(firstName.name()) + otherNames.map { it.name() })

public fun <T, C, R> SplitWithTransform<T, C, R>.intoMany(
    namesProvider: (ColumnWithPath<C>, numberOfNewColumns: Int) -> List<String>
): DataFrame<T> =
    splitImpl(this as SplitClauseWithTransform<T, C, R>, namesProvider)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = into(names.toList(), extraNamesGenerator)

public fun <T, C, R> SplitWithTransform<T, C, R>.into(
    names: List<String>,
    extraNamesGenerator: (ColumnWithPath<C>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> = intoMany { col, numberOfNewCols ->
    if (extraNamesGenerator != null && names.size < numberOfNewCols) {
        names + (1..(numberOfNewCols - names.size)).map { extraNamesGenerator(col, it) }
    } else names
}

@JvmName("intoRowsTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.intoRows(dropEmpty: Boolean = true): DataFrame<T> = with { it }.intoRows(dropEmpty)

@JvmName("intoRowsFrame")
public fun <T> Split<T, AnyFrame>.intoRows(dropEmpty: Boolean = true): DataFrame<T> = with { it.rows() }.intoRows(dropEmpty)

@JvmName("inplaceTC")
public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inplace(): DataFrame<T> = with { it }.inplace()

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.inward(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    with { it }.inward(names.toList(), extraNamesGenerator)

public inline fun <T, C : Iterable<R>, reified R> Split<T, C>.into(
    vararg names: String,
    noinline extraNamesGenerator: ColumnNamesGenerator<C>? = null
): DataFrame<T> =
    with { it }.into(names.toList(), extraNamesGenerator)

@JvmName("intoTC")
public fun <T> Split<T, String>.into(
    vararg names: String,
    extraNamesGenerator: (ColumnWithPath<String>.(extraColumnIndex: Int) -> String)? = null
): DataFrame<T> =
    with { it.splitDefault() }.into(names.toList(), extraNamesGenerator)

// endregion

// region merge

public class MergeClause<T, C, R>(
    public val df: DataFrame<T>,
    public val selector: ColumnsSelector<T, C>,
    public val transform: (Iterable<C>) -> R
)

public fun <T, C> DataFrame<T>.merge(selector: ColumnsSelector<T, C>): MergeClause<T, C, Iterable<C>> = MergeClause(this, selector, { it })

public inline fun <T, C, reified R> MergeClause<T, C, R>.into(columnName: String): DataFrame<T> = into(pathOf(columnName))

public inline fun <T, C, reified R> MergeClause<T, C, R>.into(columnPath: ColumnPath): DataFrame<T> {
    val grouped = df.move(selector).under(columnPath)
    val res = grouped.update { getColumnGroup(columnPath) }.with {
        transform(it.values().toMany() as Iterable<C>)
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
    MergeClause(df, selector) { it.joinToString(separator = separator, prefix = prefix, postfix = postfix, limit = limit, truncated = truncated) }

public inline fun <T, C, R, reified V> MergeClause<T, C, R>.by(crossinline transform: (R) -> V): MergeClause<T, C, V> = MergeClause(df, selector) { transform(this@by.transform(it)) }

// endregion

// region explode

public fun <T> DataFrame<T>.explode(dropEmpty: Boolean = true): DataFrame<T> = explode(dropEmpty) { all() }
public fun <T> DataFrame<T>.explode(vararg columns: Column, dropEmpty: Boolean = true): DataFrame<T> = explode(dropEmpty) { columns.toColumns() }
public fun <T> DataFrame<T>.explode(vararg columns: String, dropEmpty: Boolean = true): DataFrame<T> = explode(dropEmpty) { columns.toColumns() }
public fun <T> DataFrame<T>.explode(dropEmpty: Boolean = true, selector: ColumnsSelector<T, *>): DataFrame<T> = explodeImpl(dropEmpty, selector)

// endregion

// region mergeRows

public fun <T> DataFrame<T>.mergeRows(vararg columns: String, dropNulls: Boolean = false): DataFrame<T> = mergeRows(dropNulls) { columns.toColumns() }
public fun <T> DataFrame<T>.mergeRows(vararg columns: Column, dropNulls: Boolean = false): DataFrame<T> = mergeRows(dropNulls) { columns.toColumns() }
public fun <T, C> DataFrame<T>.mergeRows(vararg columns: KProperty<C>, dropNulls: Boolean = false): DataFrame<T> = mergeRows(dropNulls) { columns.toColumns() }
public fun <T, C> DataFrame<T>.mergeRows(dropNulls: Boolean = false, columns: ColumnsSelector<T, C>): DataFrame<T> = mergeRowsImpl(dropNulls, columns)

// endregion
