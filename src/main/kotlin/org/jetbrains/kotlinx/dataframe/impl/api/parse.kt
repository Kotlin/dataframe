package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.to
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.catchSilent
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.io.isURL
import org.jetbrains.kotlinx.dataframe.typeClass
import java.net.URL
import java.text.NumberFormat
import java.text.ParseException
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

internal interface StringParser<T> {
    fun toConverter(options: ParserOptions?): TypeConverter
    fun applyOptions(options: ParserOptions?): (String) -> T?
    val type: KType
}

internal open class DelegatedStringParser<T>(override val type: KType, val handle: (String) -> T?) : StringParser<T> {
    override fun toConverter(options: ParserOptions?): TypeConverter = {
        handle(it as String)
    }

    override fun applyOptions(options: ParserOptions?): (String) -> T? = handle
}

internal class StringParserWithFormat<T>(override val type: KType, val getParser: (ParserOptions?) -> ((String) -> T?)) :
    StringParser<T> {
    override fun toConverter(options: ParserOptions?): TypeConverter {
        val handler = getParser(options)
        return { handler(it as String) }
    }

    override fun applyOptions(options: ParserOptions?): (String) -> T? {
        val handler = getParser(options)
        return { handler(it) }
    }
}

internal object Parsers : GlobalParserOptions {

    private val formatters: MutableList<DateTimeFormatter> = mutableListOf()

    private val nullStrings: MutableList<String> = mutableListOf()

    public val nulls: List<String> get() = nullStrings

    override fun addDateTimeFormat(format: String) {
        formatters.add(DateTimeFormatter.ofPattern(format))
    }

    override fun addDateTimeFormatter(formatter: DateTimeFormatter) {
        formatters.add(formatter)
    }

    override fun addNullString(str: String) {
        nullStrings.add(str)
    }

    override var locale: Locale = Locale.getDefault()

    override fun resetToDefault() {
        formatters.clear()
        nullStrings.clear()

        formatters.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter()
            .let { formatters.add(it) }

        locale = Locale.getDefault()

        nullStrings.add("null")
        nullStrings.add("NULL")
    }

    init {
        resetToDefault()
    }

    private fun String.toLocalDateTimeOrNull(formatter: DateTimeFormatter? = null): LocalDateTime? {
        if (formatter != null) {
            return catchSilent { LocalDateTime.parse(this, formatter) }
        } else for (format in formatters) {
            catchSilent { LocalDateTime.parse(this, format) }?.let { return it }
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

    private fun String.toLocalDateOrNull(): LocalDate? =
        try {
            LocalDate.parse(this)
        } catch (_: Throwable) {
            null
        }

    private fun String.toLocalTimeOrNull(): LocalTime? =
        try {
            LocalTime.parse(this)
        } catch (_: Throwable) {
            null
        }

    private fun String.parseDouble(format: NumberFormat) =
        when (uppercase(Locale.getDefault())) {
            "NAN" -> Double.NaN
            "INF" -> Double.POSITIVE_INFINITY
            "-INF" -> Double.NEGATIVE_INFINITY
            "INFINITY" -> Double.POSITIVE_INFINITY
            "-INFINITY" -> Double.NEGATIVE_INFINITY
            else -> try {
                format.parse(this).toDouble()
            } catch (e: ParseException) {
                null
            }
        }

    inline fun <reified T : Any> stringParser(noinline body: (String) -> T?) = DelegatedStringParser(getType<T>(), body)

    inline fun <reified T : Any> stringParserWithOptions(noinline body: (ParserOptions?) -> ((String) -> T?)) =
        StringParserWithFormat(getType<T>(), body)

    val All = listOf(
        stringParser { it.toIntOrNull() },
        stringParser { it.toLongOrNull() },
        stringParser { it.toLocalDateOrNull() },
        stringParser { it.toLocalTimeOrNull() },

        stringParserWithOptions { options ->
            val formatter = options?.dateTimeFormatter
            val parser = { it: String -> it.toLocalDateTimeOrNull(formatter) }
            parser
        },

        stringParser { it.toUrlOrNull() },

        stringParserWithOptions { options ->
            val numberFormat = NumberFormat.getInstance(options?.locale ?: Locale.getDefault())
            val parser = { it: String -> it.parseDouble(numberFormat) }
            parser
        },
        stringParser { it.toBooleanOrNull() },
        stringParser { it.toBigDecimalOrNull() },

        stringParser { it } // must be last in the list of parsers to return original unparsed string
    )

    private val parsersMap = All.associateBy { it.type }

    val size: Int = All.size

    operator fun get(index: Int): StringParser<*> = All[index]

    operator fun get(type: KType): StringParser<*>? = parsersMap.get(type)

    operator fun <T : Any> get(type: KClass<T>): StringParser<*>? = parsersMap.get(type.createStarProjectedType(false))

    inline fun <reified T : Any> get(): StringParser<T>? = get(getType<T>()) as? StringParser<T>
}

internal fun DataColumn<String?>.tryParseImpl(options: ParserOptions?): DataColumn<*> {
    var parserId = 0
    val parsedValues = mutableListOf<Any?>()
    var hasNulls: Boolean
    var hasNotNulls: Boolean
    var nullStringParsed: Boolean
    val nulls = options?.nulls ?: Parsers.nulls
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
                    val res = parser(str)
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
    return DataColumn.createValueColumn(name(), parsedValues, type)
}

internal fun <T> DataColumn<String?>.parse(parser: StringParser<T>, options: ParserOptions?): DataColumn<T?> {
    val handler = parser.applyOptions(options)
    val parsedValues = values.map {
        it?.let {
            handler(it) ?: throw IllegalStateException("Couldn't parse '$it' into type ${parser.type}")
        }
    }
    return DataColumn.createValueColumn(name(), parsedValues, parser.type.withNullability(hasNulls)) as DataColumn<T?>
}

internal fun <T> DataFrame<T>.parseImpl(options: ParserOptions?, columns: ColumnsSelector<T, Any?>) =
    convert(columns).to {
        when {
            it.isFrameColumn() -> it.cast<AnyFrame?>().parse(options)
            it.typeClass == String::class -> it.cast<String?>().tryParse(options)
            else -> it
        }
    }
