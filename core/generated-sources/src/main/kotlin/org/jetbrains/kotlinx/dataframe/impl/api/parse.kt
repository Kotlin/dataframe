package org.jetbrains.kotlinx.dataframe.impl.api

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toDeprecatedInstant
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinLocalTime
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.GlobalParserOptions
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isSubtypeOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConversionException
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers.resetToDefault
import org.jetbrains.kotlinx.dataframe.impl.canParse
import org.jetbrains.kotlinx.dataframe.impl.catchSilent
import org.jetbrains.kotlinx.dataframe.impl.createStarProjectedType
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.jetbrains.kotlinx.dataframe.impl.javaDurationCanParse
import org.jetbrains.kotlinx.dataframe.io.isUrl
import org.jetbrains.kotlinx.dataframe.values
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.text.ParsePosition
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.Temporal
import java.time.temporal.TemporalQuery
import java.util.Locale
import kotlin.properties.Delegates
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.time.Duration as JavaDuration
import java.time.Instant as JavaInstant
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.LocalTime as JavaLocalTime
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

private val logger = KotlinLogging.logger { }

internal interface StringParser<T> {
    fun toConverter(options: ParserOptions?): TypeConverter

    fun applyOptions(options: ParserOptions?): (String) -> T?

    /** If a parser with one of these types is run, this parser can be skipped. */
    val coveredBy: Collection<KType>

    val type: KType
}

internal open class DelegatedStringParser<T>(
    override val type: KType,
    override val coveredBy: Collection<KType>,
    val handle: (String) -> T?,
) : StringParser<T> {
    override fun toConverter(options: ParserOptions?): TypeConverter {
        val nulls = options?.nullStrings ?: Parsers.nulls
        return {
            val str = it as String
            if (str in nulls) {
                null
            } else {
                handle(str) ?: throw TypeConversionException(it, typeOf<String>(), type, null)
            }
        }
    }

    override fun applyOptions(options: ParserOptions?): (String) -> T? = handle
}

internal class StringParserWithFormat<T>(
    override val type: KType,
    override val coveredBy: Collection<KType>,
    val getParser: (ParserOptions?) -> ((String) -> T?),
) : StringParser<T> {
    override fun toConverter(options: ParserOptions?): TypeConverter {
        val handler = getParser(options)
        val nulls = options?.nullStrings ?: Parsers.nulls
        return {
            val str = it as String
            if (str in nulls) {
                null
            } else {
                handler(str) ?: throw TypeConversionException(it, typeOf<String>(), type, null)
            }
        }
    }

    override fun applyOptions(options: ParserOptions?): (String) -> T? {
        val handler = getParser(options)
        return { handler(it) }
    }
}

/**
 * Central implementation for [GlobalParserOptions].
 *
 * Can be obtained by a user by calling [DataFrame.parser][DataFrame.Companion.parser].
 *
 * Defaults are set by [resetToDefault].
 */
internal object Parsers : GlobalParserOptions {

    private val formatters: MutableList<DateTimeFormatter> = mutableListOf()

    private val nullStrings: MutableSet<String> = mutableSetOf()

    internal val skipTypesSet = mutableSetOf<KType>()

    override val nulls: Set<String>
        get() = nullStrings

    override val skipTypes: Set<KType>
        get() = skipTypesSet

    override var parseExperimentalUuid by Delegates.notNull<Boolean>()

    override var parseExperimentalInstant by Delegates.notNull<Boolean>()

    override fun addDateTimePattern(pattern: String) {
        formatters.add(DateTimeFormatter.ofPattern(pattern))
    }

    override fun addNullString(str: String) {
        nullStrings.add(str)
    }

    override fun addSkipType(type: KType) {
        skipTypesSet.add(type)
    }

    override var useFastDoubleParser by Delegates.notNull<Boolean>()

    private var _locale: Locale? = null

    override var locale: Locale
        get() = _locale ?: Locale.getDefault()
        set(value) {
            _locale = value
        }

    override fun resetToDefault() {
        formatters.clear()
        nullStrings.clear()
        skipTypesSet.clear()
        formatters.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        formatters.add(DateTimeFormatter.ISO_DATE_TIME)

        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter()
            .let { formatters.add(it) }

        useFastDoubleParser = true
        parseExperimentalUuid = false
        parseExperimentalInstant = false
        _locale = null
        nullStrings.addAll(listOf("null", "NULL", "NA", "N/A"))
    }

    init {
        resetToDefault()
    }

    /**
     * Parses a [string][str] using the given [java formatter][DateTimeFormatter] and [query]
     * while avoiding exceptions. This avoidance is achieved by first trying to parse the string _unresovled_.
     * If this is unsuccessful, we can simply return `null` without throwing an exception. Only if the string can
     * successfully be parsed unresolved, we try to parse it _resolved_.
     *
     * See more about resolved and unresolved parsing in the [DateTimeFormatter] documentation.
     */
    private fun <T : Temporal> DateTimeFormatter.parseOrNull(str: String, query: TemporalQuery<T>): T? =
        catchSilent {
            // first try to parse unresolved, since it doesn't throw exceptions on invalid values
            val parsePosition = ParsePosition(0)
            if (parseUnresolved(str, parsePosition) != null && parsePosition.errorIndex == -1) {
                // do the parsing again, but now resolved, since the chance of exception is low
                parse(str, query)
            } else {
                null
            }
        }

    private fun String.toInstantOrNull(): StdlibInstant? =
        StdlibInstant.parseOrNull(this)
            // fallback on the java instant to catch things like "2022-01-23T04:29:60", a.k.a. leap seconds
            ?: toJavaInstantOrNull()?.toKotlinInstant()

    private fun String.toJavaInstantOrNull(): JavaInstant? =
        // Default format used by java.time.Instant.parse
        DateTimeFormatter.ISO_INSTANT
            .parseOrNull(this, JavaInstant::from)

    private fun String.toJavaLocalDateTimeOrNull(formatter: DateTimeFormatter?): JavaLocalDateTime? {
        if (formatter != null) {
            return formatter.parseOrNull(this, JavaLocalDateTime::from)
        } else {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                .parseOrNull(this, JavaLocalDateTime::from)
                ?.let { return it }
            for (format in formatters) {
                format.parseOrNull(this, JavaLocalDateTime::from)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun String.toLocalDateTimeOrNull(formatter: DateTimeFormatter?): LocalDateTime? =
        toJavaLocalDateTimeOrNull(formatter) // since we accept a Java DateTimeFormatter
            ?.toKotlinLocalDateTime()

    private fun String.toUrlOrNull(): URL? = if (isUrl(this)) catchSilent { URI(this).toURL() } else null

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

    private fun String.toJavaLocalDateOrNull(formatter: DateTimeFormatter?): JavaLocalDate? {
        if (formatter != null) {
            return formatter.parseOrNull(this, JavaLocalDate::from)
        } else {
            DateTimeFormatter.ISO_LOCAL_DATE
                .parseOrNull(this, JavaLocalDate::from)
                ?.let { return it }
            for (format in formatters) {
                format.parseOrNull(this, JavaLocalDate::from)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun String.toLocalDateOrNull(formatter: DateTimeFormatter?): LocalDate? =
        toJavaLocalDateOrNull(formatter) // since we accept a Java DateTimeFormatter
            ?.toKotlinLocalDate()

    private fun String.toJavaLocalTimeOrNull(formatter: DateTimeFormatter?): JavaLocalTime? {
        if (formatter != null) {
            return formatter.parseOrNull(this, JavaLocalTime::from)
        } else {
            DateTimeFormatter.ISO_LOCAL_TIME
                .parseOrNull(this, JavaLocalTime::from)
                ?.let { return it }
            for (format in formatters) {
                format.parseOrNull(this, JavaLocalTime::from)
                    ?.let { return it }
            }
        }
        return null
    }

    private fun String.toLocalTimeOrNull(formatter: DateTimeFormatter?): LocalTime? =
        toJavaLocalTimeOrNull(formatter) // since we accept a Java DateTimeFormatter
            ?.toKotlinLocalTime()

    private fun String.toJavaDurationOrNull(): JavaDuration? =
        if (javaDurationCanParse(this)) {
            catchSilent { JavaDuration.parse(this) } // will likely succeed
        } else {
            null
        }

    private fun String.toDurationOrNull(): Duration? =
        if (Duration.canParse(this)) { // TODO, migrate to `Duration.parseOrNull()` in Kotlin 2.3.0+
            catchSilent { Duration.parse(this) } // will likely succeed
        } else {
            null
        }

    inline fun <reified T : Any> stringParser(
        catch: Boolean = false,
        coveredBy: Set<KType> = emptySet(),
        noinline body: (String) -> T?,
    ): StringParser<T> =
        if (catch) {
            DelegatedStringParser(typeOf<T>(), coveredBy) {
                catchSilent { body(it) }
            }
        } else {
            DelegatedStringParser(typeOf<T>(), coveredBy, body)
        }

    inline fun <reified T : Any> stringParserWithOptions(
        coveredBy: Set<KType> = emptySet(),
        noinline body: (ParserOptions?) -> ((String) -> T?),
    ): StringParserWithFormat<T> = StringParserWithFormat(typeOf<T>(), coveredBy, body)

    private val parserToDoubleWithOptions = stringParserWithOptions { options ->
        val fastDoubleParser = FastDoubleParser(options)
        val parser = { it: String -> fastDoubleParser.parseOrNull(it) }
        parser
    }

    // same as parserToDoubleWithOptions, but overrides the locale to C.UTF-8
    private val posixParserToDoubleWithOptions = stringParserWithOptions { options ->
        val parserOptions = (options ?: ParserOptions()).copy(locale = Locale.forLanguageTag("C.UTF-8"))
        val fastDoubleParser = FastDoubleParser(parserOptions)
        val parser = { it: String -> fastDoubleParser.parseOrNull(it) }
        parser
    }

    // TODO rewrite using parser service later https://github.com/Kotlin/dataframe/issues/962
    // null when dataframe-json is not present
    private val readJsonStrAnyFrame: ((text: String) -> AnyFrame)? by lazy {
        try {
            val klass = Class.forName("org.jetbrains.kotlinx.dataframe.io.JsonKt")
            val typeClashTactic = Class.forName($$"org.jetbrains.kotlinx.dataframe.io.JSON$TypeClashTactic")
            val readJsonStr = klass.getMethod(
                "readJsonStr",
                // this =
                DataFrame.Companion::class.java,
                // text =
                String::class.java,
                // header =
                List::class.java,
                // keyValuePaths =
                List::class.java,
                // typeClashTactic =
                typeClashTactic,
                // unifyNumbers =
                Boolean::class.java,
            )

            return@lazy { text: String ->
                readJsonStr.invoke(
                    null,
                    // this =
                    DataFrame.Companion,
                    // text =
                    text,
                    // header =
                    emptyList<Any>(),
                    // keyValuePaths =
                    emptyList<Any>(),
                    // typeClashTactic =
                    typeClashTactic.enumConstants[0],
                    // unifyNumbers =
                    true,
                ) as AnyFrame
            }
        } catch (_: ClassNotFoundException) {
            return@lazy null
        }
    }

    // TODO rewrite using parser service later https://github.com/Kotlin/dataframe/issues/962
    // null when dataframe-json is not present
    private val readJsonStrAnyRow: ((text: String) -> AnyRow)? by lazy {
        try {
            val klass = Class.forName("org.jetbrains.kotlinx.dataframe.io.JsonKt")
            val typeClashTactic = Class.forName("org.jetbrains.kotlinx.dataframe.io.JSON\$TypeClashTactic")
            val readJsonStr = klass.getMethod(
                "readJsonStr",
                // this =
                DataRow.Companion::class.java,
                // text =
                String::class.java,
                // header =
                List::class.java,
                // keyValuePaths =
                List::class.java,
                // typeClashTactic =
                typeClashTactic,
                // unifyNumbers =
                Boolean::class.java,
            )

            return@lazy { text: String ->
                readJsonStr.invoke(
                    null,
                    // this =
                    DataRow.Companion,
                    // text =
                    text,
                    // header =
                    emptyList<Any>(),
                    // keyValuePaths =
                    emptyList<Any>(),
                    // typeClashTactic =
                    typeClashTactic.enumConstants[0],
                    // unifyNumbers =
                    true,
                ) as AnyRow
            }
        } catch (_: ClassNotFoundException) {
            return@lazy null
        }
    }

    private val uuidRegex = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")

    @OptIn(ExperimentalUuidApi::class)
    internal val parsersOrder = listOf(
        // Int
        stringParser<Int> { it.toIntOrNull() },
        // Long
        stringParser<Long> { it.toLongOrNull() },
        // kotlin.time.Instant
        stringParserWithOptions<StdlibInstant> {
            val parseExperimentalInstant = it?.parseExperimentalInstant ?: this.parseExperimentalInstant
            val parser = { it: String ->
                if (parseExperimentalInstant) {
                    it.toInstantOrNull()
                } else {
                    null
                }
            }
            parser
        },
        // kotlinx.datetime.Instant
        stringParser<DeprecatedInstant> {
            it.toInstantOrNull()?.toDeprecatedInstant()
        },
        // java.time.Instant, will be skipped if kotlinx.datetime.Instant is already checked
        stringParser<JavaInstant>(coveredBy = setOf(typeOf<DeprecatedInstant>())) {
            it.toJavaInstantOrNull()
        },
        // kotlinx.datetime.LocalDateTime
        stringParserWithOptions<LocalDateTime> { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalDateTimeOrNull(formatter) }
            parser
        },
        // java.time.LocalDateTime, will be skipped if kotlinx.datetime.LocalDateTime is already checked
        stringParserWithOptions<JavaLocalDateTime>(coveredBy = setOf(typeOf<LocalDateTime>())) { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toJavaLocalDateTimeOrNull(formatter) }
            parser
        },
        // kotlinx.datetime.LocalDate
        stringParserWithOptions<LocalDate> { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalDateOrNull(formatter) }
            parser
        },
        // java.time.LocalDate, will be skipped if kotlinx.datetime.LocalDate is already checked
        stringParserWithOptions<JavaLocalDate>(coveredBy = setOf(typeOf<LocalDate>())) { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toJavaLocalDateOrNull(formatter) }
            parser
        },
        // kotlin.time.Duration
        stringParser<Duration> {
            it.toDurationOrNull()
        },
        // java.time.Duration, will be skipped if kotlin.time.Duration is already checked
        stringParser<JavaDuration>(coveredBy = setOf(typeOf<Duration>())) {
            it.toJavaDurationOrNull()
        },
        // kotlinx.datetime.LocalTime
        stringParserWithOptions<LocalTime> { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toLocalTimeOrNull(formatter) }
            parser
        },
        // java.time.LocalTime, will be skipped if kotlinx.datetime.LocalTime is already checked
        stringParserWithOptions<JavaLocalTime>(coveredBy = setOf(typeOf<LocalTime>())) { options ->
            val formatter = options?.getDateTimeFormatter()
            val parser = { it: String -> it.toJavaLocalTimeOrNull(formatter) }
            parser
        },
        // java.net.URL
        stringParser<URL> { it.toUrlOrNull() },
        // Double, with explicit number format or taken from current locale
        parserToDoubleWithOptions,
        // Double, with POSIX format
        posixParserToDoubleWithOptions,
        // Boolean
        stringParser<Boolean> { it.toBooleanOrNull() },
        // Uuid
        stringParserWithOptions<Uuid> { options ->
            val parser = { str: String ->
                val parseExperimentalUuid = options?.parseExperimentalUuid ?: this.parseExperimentalUuid
                when {
                    !parseExperimentalUuid -> null

                    uuidRegex.matches(str) -> {
                        try {
                            Uuid.parse(str)
                        } catch (_: IllegalArgumentException) {
                            null
                        }
                    }

                    else -> null
                }
            }
            parser
        },
        // BigInteger
        stringParser<BigInteger> { it.toBigIntegerOrNull() },
        // BigDecimal
        stringParser<BigDecimal> { it.toBigDecimalOrNull() },
        // JSON array as DataFrame<*>
        stringParser<AnyFrame>(catch = true) {
            val trimmed = it.trim()
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                if (readJsonStrAnyFrame == null) {
                    logger.warn {
                        "parse() encountered a string that looks like a JSON array, but the dataframe-json dependency was not detected. Skipping for now."
                    }
                    null
                } else {
                    readJsonStrAnyFrame!!(trimmed)
                }
            } else {
                null
            }
        },
        // JSON object as DataRow<*>
        stringParser<AnyRow>(catch = true) {
            val trimmed = it.trim()
            if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                if (readJsonStrAnyRow == null) {
                    logger.warn {
                        "parse() encountered a string that looks like a JSON object, but the dataframe-json dependency was not detected. Skipping for now."
                    }
                    null
                } else {
                    readJsonStrAnyRow!!(trimmed)
                }
            } else {
                null
            }
        },
        // Char
        stringParser<Char> { it.singleOrNull() },
        // No parser found, return as String
        // must be last in the list of parsers to return original unparsed string
        stringParser<String> { it },
    )

    private val parsersMap = parsersOrder.associateBy { it.type }

    val size: Int = parsersOrder.size

    operator fun get(index: Int): StringParser<*> = parsersOrder[index]

    operator fun get(type: KType): StringParser<*>? = parsersMap[type]

    operator fun <T : Any> get(type: KClass<T>): StringParser<T>? =
        parsersMap[type.createStarProjectedType(false)] as? StringParser<T>

    inline fun <reified T : Any> get(): StringParser<T>? = get(typeOf<T>()) as? StringParser<T>

    internal fun <R : Any> getDateTimeConverter(
        clazz: KClass<R>,
        pattern: String? = null,
        locale: Locale? = null,
    ): (String) -> R? {
        val parser = get(clazz) ?: error("Can not convert String to $clazz")
        val formatter = pattern?.let {
            if (locale == null) {
                DateTimeFormatter.ofPattern(it)
            } else {
                DateTimeFormatter.ofPattern(it, locale)
            }
        }
        val options = if (formatter != null || locale != null) {
            ParserOptions(
                dateTimeFormatter = formatter,
                locale = locale,
            )
        } else {
            null
        }
        return parser.applyOptions(options)
    }

    internal fun getDoubleParser(locale: Locale?, useFastDoubleParser: Boolean): (String) -> Double? =
        parserToDoubleWithOptions
            .applyOptions(ParserOptions(locale = locale, useFastDoubleParser = useFastDoubleParser))

    internal fun getPosixDoubleParser(useFastDoubleParser: Boolean): (String) -> Double? =
        posixParserToDoubleWithOptions
            .applyOptions(ParserOptions(useFastDoubleParser = useFastDoubleParser))
}

/**
 * Tries to parse a column of strings into a column of a different type.
 * Each parser in [Parsers] is run in order until a valid parser is found,
 * a.k.a. that parser was able to parse all values in the column successfully. If a parser
 * fails to parse any value, the next parser is tried. If all the others fail, the final parser
 * simply returns the original string, leaving the column unchanged.
 *
 * Parsers that are [covered by][StringParser.coveredBy] other parsers are skipped.
 *
 * @param options options for parsing, like providing a locale or a custom date-time formatter
 * @throws IllegalStateException if no valid parser is found (unlikely, unless the `String` parser is disabled)
 * @return a new column with parsed values
 */
internal fun DataColumn<String?>.tryParseImpl(options: ParserOptions?): DataColumn<*> {
    val columnSize = size
    val parsedValues = ArrayList<Any?>(columnSize)
    var hasNulls = false
    var hasNotNulls = false
    var nullStringParsed = false
    val nulls = options?.nullStrings ?: Parsers.nulls

    val parserTypesToSkip = options?.skipTypes ?: Parsers.skipTypesSet
    val parsersToCheck = Parsers.parsersOrder
        .filterNot { it.type in parserTypesToSkip }
    val parserTypesToCheck = parsersToCheck.map { it.type }.toSet()

    var correctParser: StringParser<*>? = null
    for (parser in parsersToCheck) {
        if (parser.coveredBy.any { it in parserTypesToCheck }) continue

        val parserWithOptions = parser.applyOptions(options)
        parsedValues.clear()
        hasNulls = false
        hasNotNulls = false
        nullStringParsed = false
        for (str in this) {
            when (str) {
                null -> {
                    parsedValues += null
                    hasNulls = true
                }

                in nulls -> {
                    parsedValues += null
                    hasNulls = true
                    nullStringParsed = true
                }

                else -> {
                    val trimmed = str.trim()
                    val res = parserWithOptions(trimmed) ?: break
                    parsedValues += res
                    hasNotNulls = true
                }
            }
        }

        // break when everything is parsed
        if (parsedValues.size >= columnSize) {
            correctParser = parser
            break
        }
    }
    check(correctParser != null) { "Valid parser not found" }

    val type = (if (hasNotNulls) correctParser.type else this.type()).withNullability(hasNulls)
    if (type.jvmErasure == String::class && !nullStringParsed) {
        return this // nothing parsed
    }

    // Create a new column with the parsed values,
    // createColumnGuessingType is used to handle unifying values if needed
    return DataColumn.createByInference(
        name = name(),
        values = parsedValues,
        suggestedType = TypeSuggestion.Use(type),
    )
}

internal fun <T> DataColumn<String?>.parse(parser: StringParser<T>, options: ParserOptions?): DataColumn<T?> {
    val handler = parser.applyOptions(options)
    val parsedValues = values.map {
        it?.let {
            handler(it.trim()) ?: throw IllegalStateException("Couldn't parse '$it' into type ${parser.type}")
        }
    }
    return DataColumn.createByInference(
        name = name(),
        values = parsedValues,
        suggestedType = TypeSuggestion.Use(parser.type.withNullability(hasNulls)),
    )
}

internal fun <T> DataFrame<T>.parseImpl(options: ParserOptions?, columns: ColumnsSelector<T, Any?>): DataFrame<T> =
    convert(columns).asColumn { col ->
        when {
            // when a frame column is requested to be parsed,
            // parse each value/frame column at any depth inside each DataFrame in the frame column
            col.isFrameColumn() ->
                col.map {
                    it.parseImpl(options) {
                        colsAtAnyDepth().filter { !it.isColumnGroup() }
                    }
                }

            // when a column group is requested to be parsed,
            // parse each column in the group
            col.isColumnGroup() ->
                col.parseImpl(options) { all() }
                    .asColumnGroup(col.name())
                    .asDataColumn()

            // Base case, parse the column if it's a `Char?` column
            col.isSubtypeOf<Char?>() ->
                col.map { it?.toString() }.tryParseImpl(options)

            // Base case, parse the column if it's a `String?` column
            col.isSubtypeOf<String?>() ->
                col.tryParseImpl(options)

            else -> col
        }
    }
