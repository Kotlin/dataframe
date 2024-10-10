package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.parseImpl
import org.jetbrains.kotlinx.dataframe.impl.api.tryParseImpl
import org.jetbrains.kotlinx.dataframe.typeClass
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.reflect.KProperty
import kotlin.reflect.KType

public val DataFrame.Companion.parser: GlobalParserOptions get() = Parsers

public fun <T> DataFrame<T>.parse(options: ParserOptions? = null, columns: ColumnsSelector<T, Any?>): DataFrame<T> =
    parseImpl(options, columns)

public fun <T> DataFrame<T>.parse(vararg columns: String, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.parse(vararg columns: ColumnReference<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

public fun <T, C> DataFrame<T>.parse(vararg columns: KProperty<C>, options: ParserOptions? = null): DataFrame<T> =
    parse(options) { columns.toColumnSet() }

public interface GlobalParserOptions {

    public fun addDateTimePattern(pattern: String)

    public fun addNullString(str: String)

    public fun resetToDefault()

    public var locale: Locale
}

/**
 * ### Options for parsing [String]`?` columns
 *
 * @param locale locale to use for parsing dates and numbers, defaults to the System default locale.
 *   If specified instead of [dateTimeFormatter], it will be used in combination with [dateTimePattern]
 *   to create a [DateTimeFormatter]. Just providing [locale] will not allow you to parse
 *   locale-specific dates!
 * @param dateTimeFormatter a [DateTimeFormatter] to use for parsing dates, if not specified, it will be created
 *   from [dateTimePattern] and [locale]. If neither [dateTimeFormatter] nor [dateTimePattern] are specified,
 *   [DateTimeFormatter.ISO_LOCAL_DATE_TIME] will be used.
 * @param dateTimePattern a pattern to use for parsing dates. If specified instead of [dateTimeFormatter],
 *   it will be used to create a [DateTimeFormatter].
 * @param nullStrings a set of strings that should be treated as `null` values. By default, it's
 *   ["null", "NULL", "NA", "N/A"].
 * @param useFastDoubleParser whether to use FastDoubleParser, defaults to `true`, but it can be disabled
 *   if it works unreliably on, for instance, [locale]-dependent data.
 * @param skipTypes a set of types that should be skipped during parsing. Parsing will be attempted for all other types.
 *   By default, it's an empty set. To skip all types except some specified ones, use [allTypesExcept].
 */
public data class ParserOptions(
    val locale: Locale? = null,
    // TODO, migrate to kotlinx.datetime.format.DateTimeFormat? https://github.com/Kotlin/dataframe/issues/876
    val dateTimeFormatter: DateTimeFormatter? = null,
    val dateTimePattern: String? = null,
    val nullStrings: Set<String>? = null,
    val useFastDoubleParser: Boolean = true,
    val skipTypes: Set<KType> = emptySet(),
) {
    public companion object {
        /**
         * Small helper function to get all types except the ones specified.
         * Useful in combination with the [skipTypes] parameter.
         */
        public fun allTypesExcept(vararg types: KType): Set<KType> =
            Parsers.parsersOrder.map { it.type }.toSet() - types.toSet()
    }

    internal fun getDateTimeFormatter(): DateTimeFormatter? =
        when {
            dateTimeFormatter != null -> dateTimeFormatter
            dateTimePattern != null && locale != null -> DateTimeFormatter.ofPattern(dateTimePattern, locale)
            dateTimePattern != null -> DateTimeFormatter.ofPattern(dateTimePattern)
            else -> null
        }
}

public fun DataColumn<String?>.tryParse(options: ParserOptions? = null): DataColumn<*> = tryParseImpl(options)

public fun <T> DataFrame<T>.parse(options: ParserOptions? = null): DataFrame<T> =
    parse(options) {
        colsAtAnyDepth { !it.isColumnGroup() }
    }

public fun DataColumn<String?>.parse(options: ParserOptions? = null): DataColumn<*> =
    tryParse(options).also { if (it.typeClass == String::class) error("Can't guess column type") }

@JvmName("parseAnyFrameNullable")
public fun DataColumn<AnyFrame?>.parse(options: ParserOptions? = null): DataColumn<AnyFrame?> =
    map { it?.parse(options) }
