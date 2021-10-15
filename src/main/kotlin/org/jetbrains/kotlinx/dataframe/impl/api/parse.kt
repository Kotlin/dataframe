package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.DataFrameParserOptions
import org.jetbrains.kotlinx.dataframe.api.allNulls
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.catchSilent
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.io.isURL
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal class StringParser<T : Any>(val type: KType, val parse: (String) -> T?) {
    fun toConverter(): TypeConverter = { parse(it as String) }
}

internal object Parsers : DataFrameParserOptions {

    private val formatterDateTime = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter()

    private val formatters = mutableListOf(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        formatterDateTime
    )

    override fun addDateTimeFormat(format: String) { formatters.add(DateTimeFormatter.ofPattern(format)) }

    private fun String.toLocalDateTimeOrNull(): LocalDateTime? {
        for (format in formatters) {
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

    private fun String.parseDouble() =
        when (uppercase(Locale.getDefault())) {
            "NAN" -> Double.NaN
            "INF" -> Double.POSITIVE_INFINITY
            "-INF" -> Double.NEGATIVE_INFINITY
            "INFINITY" -> Double.POSITIVE_INFINITY
            "-INFINITY" -> Double.NEGATIVE_INFINITY
            else -> toDoubleOrNull()
        }

    inline fun <reified T : Any> stringParser(noinline body: (String) -> T?) = StringParser(getType<T>(), body)

    val All = listOf(
        stringParser { it.toIntOrNull() },
        stringParser { it.toLongOrNull() },
        stringParser { it.parseDouble() },
        stringParser { it.toBooleanOrNull() },
        stringParser { it.toBigDecimalOrNull() },
        stringParser { it.toLocalDateOrNull() },
        stringParser { it.toLocalTimeOrNull() },
        stringParser { it.toLocalDateTimeOrNull() },
        stringParser { it.toUrlOrNull() }
    )

    private val parsersMap = All.associateBy { it.type }

    val size: Int = All.size

    operator fun get(index: Int): StringParser<*> = All[index]

    operator fun get(type: KType): StringParser<*>? = parsersMap.get(type)

    operator fun <T : Any> get(type: KClass<T>): StringParser<*>? = parsersMap.get(type.createStarProjectedType(false))

    inline fun <reified T : Any> get(): StringParser<T>? = get(getType<T>()) as? StringParser<T>
}

internal fun DataColumn<String?>.tryParseImpl(): DataColumn<*> {
    if (allNulls()) return this

    var parserId = 0
    val parsedValues = mutableListOf<Any?>()

    do {
        val parser = Parsers[parserId]
        parsedValues.clear()
        for (str in values) {
            if (str == null) parsedValues.add(null)
            else {
                val res = parser.parse(str)
                if (res == null) {
                    parserId++
                    break
                }
                parsedValues.add(res)
            }
        }
    } while (parserId < Parsers.size && parsedValues.size != size)
    if (parserId == Parsers.size) return this
    return DataColumn.create(name(), parsedValues, Parsers[parserId].type.withNullability(hasNulls))
}

internal fun <T : Any> DataColumn<String?>.parse(parser: StringParser<T>): DataColumn<T?> {
    val parsedValues = values.map {
        it?.let {
            parser.parse(it) ?: throw Exception("Couldn't parse '$it' to type ${parser.type}")
        }
    }
    return DataColumn.create(name(), parsedValues, parser.type.withNullability(hasNulls)) as DataColumn<T?>
}
