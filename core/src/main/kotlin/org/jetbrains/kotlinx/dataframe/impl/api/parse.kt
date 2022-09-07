package org.jetbrains.kotlinx.dataframe.impl.api

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConversionException
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.catchSilent
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.io.isURL
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.typeClass
import java.net.URL
import java.text.NumberFormat
import java.text.ParsePosition
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf
import kotlin.time.Duration

internal interface StringParser<T> {
    fun toConverter(options: ParserOptions?): TypeConverter
    fun applyOptions(options: ParserOptions?): (String) -> T?
    val type: KType
}

internal open class DelegatedStringParser<T>(override val type: KType, val handle: (String) -> T?) : StringParser<T> {
    override fun toConverter(options: ParserOptions?): TypeConverter {
        val nulls = options?.nullStrings ?: Parsers.nulls
        return {
            val str = it as String
            if (str in nulls) null
            else handle(str) ?: throw TypeConversionException(it, typeOf<String>(), type)
        }
    }

    override fun applyOptions(options: ParserOptions?): (String) -> T? = handle
}

internal class StringParserWithFormat<T>(override val type: KType, val getParser: (ParserOptions?) -> ((String) -> T?)) :
    StringParser<T> {
    override fun toConverter(options: ParserOptions?): TypeConverter {
        val handler = getParser(options)
        val nulls = options?.nullStrings ?: Parsers.nulls
        return {
            val str = it as String
            if (str in nulls) null
            else handler(str) ?: throw TypeConversionException(it, typeOf<String>(), type)
        }
    }

    override fun applyOptions(options: ParserOptions?): (String) -> T? {
        val handler = getParser(options)
        return { handler(it) }
    }
}

internal object Parsers : GlobalParserOptions {

    private val formatters: MutableList<DateTimeFormatter> = mutableListOf()

    private val nullStrings: MutableSet<String> = mutableSetOf()

    public val nulls: Set<String> get() = nullStrings

    override fun addDateTimePattern(pattern: String) {
        formatters.add(DateTimeFormatter.ofPattern(pattern))
    }

    override fun addNullString(str: String) {
        nullStrings.add(str)
    }

    override var locale: Locale = Locale.getDefault()

    override fun resetToDefault() {
        formatters.clear()
        nullStrings.clear()
        formatters.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        formatters.add(DateTimeFormatter.ISO_DATE_TIME)

        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter()
            .let { formatters.add(it) }

        locale = Locale.getDefault()

        nullStrings.addAll(listOf("null", "NULL", "NA", "N/A"))
    }

    init {
        resetToDefault()
    }

    private fun String.toLocalDateTimeOrNull(formatter: DateTimeFormatter?): LocalDateTime? {
        if (formatter != null) {
            return catchSilent { java.time.LocalDateTime.parse(this, formatter) }
        } else {
            catchSilent { LocalDateTime.parse(this) }?.let { return it }
            for (format in formatters) {
                catchSilent { java.time.LocalDateTime.parse(this, format) }?.let { return it }
            }
        }
        return null
    }

    private fun String.toUrlOrNull(): URL? {
        return if (isURL(this)) catchSilent { URL(this) } else null
    }

    private fun String.toBooleanOrNull() =
        when (uppercase(Locale.getDefault())) {
            "T" -> true
            "TRUE" -> true
            "YES" -> true
            "F" -> false
            "FALSE" -> false
            "NO" -> false
            else -> null
        }

    private fun String.toLocalDateOrNull(formatter: DateTimeFormatter?): LocalDate? {
        if (formatter != null) {
            return catchSilent { java.time.LocalDate.parse(this, formatter) }
        } else {
            catchSilent { LocalDate.parse(this) }?.let { return it }
            for (format in formatters) {
                catchSilent { java.time.LocalDate.parse(this, format) }?.let { return it }
            }
        }
        return null
    }

    private fun String.toLocalTimeOrNull(formatter: DateTimeFormatter?): LocalTime? {
        if (formatter != null) {
            return catchSilent { LocalTime.parse(this, formatter) }
        } else {
            catchSilent { LocalTime.parse(this) }?.let { return it }
            for (format in formatters) {
                catchSilent { LocalTime.parse(this, format) }?.let { return it }
            }
        }
        return null
    }

    private fun String.parseDouble(format: NumberFormat) =
        when (uppercase(Locale.getDefault())) {
            "NAN" -> Double.NaN
            "INF" -> Double.POSITIVE_INFINITY
            "-INF" -> Double.NEGATIVE_INFINITY
            "INFINITY" -> Double.POSITIVE_INFINITY
            "-INFINITY" -> Double.NEGATIVE_INFINITY
            else -> {
                val parsePosition = ParsePosition(0)
                val result: Double? = format.parse(this, parsePosition)?.toDouble()
                if (parsePosition.index != this.length) null
                else result
            }
        }

    inline fun <reified T : Any> stringParser(catch: Boolean = false, noinline body: (String) -> T?): StringParser<T> {
        return if (catch) DelegatedStringParser(typeOf<T>()) {
            try {
                body(it)
            } catch (e: Throwable) {
                null
            }
        }
        else DelegatedStringParser(typeOf<T>(), body)
    }

    inline fun <reified T : Any> stringParserWithOptions(noinline body: (ParserOptions?) -> ((String) -> T?)) =
        StringParserWithFormat(typeOf<T>(), body)

    private val parserToDoubleWithOptions = stringParserWithOptions { options ->
        val numberFormat = NumberFormat.getInstance(options?.locale ?: Locale.getDefault())
        val parser = { it: String -> it.parseDouble(numberFormat) }
        parser
    }

    private val parsersOrder = listOf(
        stringParser { it.toIntOrNull() },
        stringParser { it.toLongOrNull() },

        // kotlinx.datetime.Instant
        stringParser { catchSilent { Instant.parse(it) } },

        // java.time.Instant
        stringParser { catchSilent { java.time.Instant.parse(it) } },

        // kotlinx.datetime.LocalDateTime
        stringParserWithOptions { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalDateTimeOrNull(formatter)?.toKotlinLocalDateTime() }
            parser
        },

        // java.time.LocalDateTime
        stringParserWithOptions { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalDateTimeOrNull(formatter) }
            parser
        },

        // kotlinx.datetime.LocalDate
        stringParserWithOptions { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalDateOrNull(formatter)?.toKotlinLocalDate() }
            parser
        },

        // java.time.LocalDate
        stringParserWithOptions { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalDateOrNull(formatter) }
            parser
        },

        // kotlin.time.duration
        stringParser { catchSilent { Duration.parse(it) } },

        // java.time.duration
        stringParser { catchSilent { java.time.Duration.parse(it) } },

        // java.time.LocalTime
        stringParserWithOptions { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalTimeOrNull(formatter) }
            parser
        },

        // java.net.URL
        stringParser { it.toUrlOrNull() },

        // Double, with explicit number format or taken from current locale
        parserToDoubleWithOptions,

        // Double, with POSIX format
        stringParser { it.parseDouble(NumberFormat.getInstance(Locale.forLanguageTag("C.UTF-8"))) },

        stringParser { it.toBooleanOrNull() },
        stringParser { it.toBigDecimalOrNull() },

        stringParser(catch = true) { if (it.startsWith("[")) DataFrame.readJsonStr(it) else null },
        stringParser(catch = true) { if (it.startsWith("{")) DataFrame.readJsonStr(it).single() else null },

        stringParser { it } // must be last in the list of parsers to return original unparsed string
    )

    private val parsersMap = parsersOrder.associateBy { it.type }

    val size: Int = parsersOrder.size

    operator fun get(index: Int): StringParser<*> = parsersOrder[index]

    operator fun get(type: KType): StringParser<*>? = parsersMap[type]

    operator fun <T : Any> get(type: KClass<T>): StringParser<T>? = parsersMap.get(type.createStarProjectedType(false)) as? StringParser<T>

    inline fun <reified T : Any> get(): StringParser<T>? = get(typeOf<T>()) as? StringParser<T>

    internal fun <R : Any> getDateTimeConverter(clazz: KClass<R>, pattern: String? = null, locale: Locale? = null):
        (String) -> R? {
        val parser = get(clazz) ?: error("Can not convert String to $clazz")
        val formatter = pattern?.let {
            if (locale == null) DateTimeFormatter.ofPattern(it)
            else DateTimeFormatter.ofPattern(it, locale)
        }
        val options = if (formatter != null || locale != null) ParserOptions(
            dateTimeFormatter = formatter,
            locale = locale
        ) else null
        return parser.applyOptions(options)
    }

    internal fun getDoubleParser(locale: Locale? = null): (String) -> Double? {
        val options = if (locale != null) ParserOptions(
            locale = locale
        ) else null
        return parserToDoubleWithOptions.applyOptions(options)
    }
}

internal fun DataColumn<String?>.tryParseImpl(options: ParserOptions?): DataColumn<*> {
    var parserId = 0
    val parsedValues = mutableListOf<Any?>()
    var hasNulls: Boolean
    var hasNotNulls: Boolean
    var nullStringParsed: Boolean
    val nulls = options?.nullStrings ?: Parsers.nulls
    do {
        val parser = Parsers[parserId].applyOptions(options)
        parsedValues.clear()
        hasNulls = false
        hasNotNulls = false
        nullStringParsed = false
        for (str in values) {
            when {
                str == null -> {
                    parsedValues.add(null)
                    hasNulls = true
                }

                nulls.contains(str) -> {
                    parsedValues.add(null)
                    hasNulls = true
                    nullStringParsed = true
                }

                else -> {
                    val trimmed = str.trim()
                    val res = parser(trimmed)
                    if (res == null) {
                        parserId++
                        break
                    }
                    parsedValues.add(res)
                    hasNotNulls = true
                }
            }
        }
    } while (parserId < Parsers.size && parsedValues.size != size)
    check(parserId < Parsers.size) { "Valid parser not found" }

    val type = (if (hasNotNulls) Parsers[parserId].type else this.type()).withNullability(hasNulls)
    if (type.jvmErasure == String::class && !nullStringParsed) return this // nothing parsed
    return DataColumn.create(name(), parsedValues, type)
}

internal fun <T> DataColumn<String?>.parse(parser: StringParser<T>, options: ParserOptions?): DataColumn<T?> {
    val handler = parser.applyOptions(options)
    val parsedValues = values.map {
        it?.let {
            handler(it.trim()) ?: throw IllegalStateException("Couldn't parse '$it' into type ${parser.type}")
        }
    }
    return DataColumn.createValueColumn(name(), parsedValues, parser.type.withNullability(hasNulls)) as DataColumn<T?>
}

internal fun <T> DataFrame<T>.parseImpl(options: ParserOptions?, columns: ColumnsSelector<T, Any?>) =
    convert(columns).to {
        when {
            it.isFrameColumn() -> it.cast<AnyFrame?>().parse(options)
            it.isColumnGroup() -> it.asColumnGroup().parse { all() }.asColumnGroup(it.name()).asDataColumn()
            it.typeClass == String::class -> it.cast<String?>().tryParse(options)
            else -> it
        }
    }
