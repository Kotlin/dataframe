package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.apache.commons.csv.CSVFormat
import org.apache.commons.io.input.BOMInputStream
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadCsvMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.impl.api.parse
import org.jetbrains.kotlinx.dataframe.impl.io.readDelimImpl
import org.jetbrains.kotlinx.dataframe.util.APACHE_CSV
import org.jetbrains.kotlinx.dataframe.util.AS_URL
import org.jetbrains.kotlinx.dataframe.util.AS_URL_IMPORT
import org.jetbrains.kotlinx.dataframe.util.AS_URL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_TYPE_DEPRECATED_INSTANT
import org.jetbrains.kotlinx.dataframe.util.COL_TYPE_DEPRECATED_INSTANT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_TYPE_INSTANT
import org.jetbrains.kotlinx.dataframe.util.COL_TYPE_INSTANT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DF_READ_NO_CSV
import org.jetbrains.kotlinx.dataframe.util.DF_READ_NO_CSV_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_CSV
import org.jetbrains.kotlinx.dataframe.util.READ_CSV_FILE_OR_URL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_CSV_FILE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_CSV_IMPORT
import org.jetbrains.kotlinx.dataframe.util.READ_CSV_STREAM_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_CSV_URL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_DELIM
import org.jetbrains.kotlinx.dataframe.util.READ_DELIM_READER_REPLACE
import org.jetbrains.kotlinx.dataframe.util.READ_DELIM_STREAM_REPLACE
import org.jetbrains.kotlinx.dataframe.util.TO_CSV
import org.jetbrains.kotlinx.dataframe.util.TO_CSV_IMPORT
import org.jetbrains.kotlinx.dataframe.util.TO_CSV_REPLACE
import org.jetbrains.kotlinx.dataframe.util.WRITE_CSV
import org.jetbrains.kotlinx.dataframe.util.WRITE_CSV_FILE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.WRITE_CSV_IMPORT
import org.jetbrains.kotlinx.dataframe.util.WRITE_CSV_PATH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.WRITE_CSV_WRITER_REPLACE
import org.jetbrains.kotlinx.dataframe.values
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

@Deprecated(message = APACHE_CSV, level = DeprecationLevel.WARNING)
public class CSV(private val delimiter: Char = ',') : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame =
        DataFrame.readCSV(stream = stream, delimiter = delimiter, header = header)

    override fun readDataFrame(file: File, header: List<String>): AnyFrame =
        DataFrame.readCSV(file = file, delimiter = delimiter, header = header)

    override fun readDataFrame(path: Path, header: List<String>): AnyFrame =
        // core CSV impl is deprecated, delegate via File to preserve module boundaries
        DataFrame.readCSV(file = path.toFile(), delimiter = delimiter, header = header)

    override fun acceptsExtension(ext: String): Boolean = ext == "csv"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 20_001

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        val arguments = MethodArguments().add("delimiter", typeOf<Char>(), "'%L'", delimiter)
        return DefaultReadCsvMethod(pathRepresentation, arguments)
    }
}

@Deprecated(
    message = APACHE_CSV,
    level = DeprecationLevel.WARNING,
)
public enum class CSVType(public val format: CSVFormat) {
    DEFAULT(
        CSVFormat.DEFAULT.builder()
            .setAllowMissingColumnNames(true)
            .setIgnoreSurroundingSpaces(true)
            .build(),
    ),
    TDF(
        CSVFormat.TDF.builder()
            .setAllowMissingColumnNames(true)
            .build(),
    ),
}

private val defaultCharset = Charsets.UTF_8

@Deprecated("", level = DeprecationLevel.WARNING)
internal fun isCompressed(fileOrUrl: String) = listOf("gz", "zip").contains(fileOrUrl.split(".").last())

@Deprecated("", level = DeprecationLevel.WARNING)
internal fun isCompressed(file: File) = listOf("gz", "zip").contains(file.extension)

@Deprecated("", level = DeprecationLevel.WARNING)
internal fun isCompressed(url: URL) = isCompressed(url.path)

@Deprecated(
    message = APACHE_CSV,
    level = DeprecationLevel.HIDDEN, // clashes with the new readDelim
)
public fun DataFrame.Companion.readDelimStr(
    text: String,
    delimiter: Char = ',',
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
): DataFrame<*> =
    StringReader(text).use {
        val format = CSVType.DEFAULT.format.builder()
            .setHeader()
            .setDelimiter(delimiter)
            .build()
        readDelim(it, format, colTypes, skipLines, readLines)
    }

@Deprecated(
    message = DF_READ_NO_CSV,
    replaceWith = ReplaceWith(DF_READ_NO_CSV_REPLACE, READ_CSV_IMPORT),
    level = DeprecationLevel.ERROR,
)
public fun DataFrame.Companion.read(
    fileOrUrl: String,
    delimiter: Char,
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
): DataFrame<*> = error(DF_READ_NO_CSV)

@Deprecated(
    message = READ_CSV,
    replaceWith = ReplaceWith(READ_CSV_FILE_OR_URL_REPLACE, READ_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun DataFrame.Companion.readCSV(
    fileOrUrl: String,
    delimiter: Char = ',',
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    catchHttpResponse(asUrl(fileOrUrl)) {
        readDelim(
            it,
            delimiter,
            header,
            isCompressed(fileOrUrl),
            CSVType.DEFAULT,
            colTypes,
            skipLines,
            readLines,
            duplicate,
            charset,
            parserOptions,
        )
    }

@Deprecated(
    message = READ_CSV,
    replaceWith = ReplaceWith(READ_CSV_FILE_REPLACE, READ_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun DataFrame.Companion.readCSV(
    file: File,
    delimiter: Char = ',',
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    readDelim(
        FileInputStream(file),
        delimiter,
        header,
        isCompressed(file),
        CSVType.DEFAULT,
        colTypes,
        skipLines,
        readLines,
        duplicate,
        charset,
        parserOptions,
    )

@Deprecated(
    message = READ_CSV,
    replaceWith = ReplaceWith(READ_CSV_URL_REPLACE, READ_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun DataFrame.Companion.readCSV(
    url: URL,
    delimiter: Char = ',',
    header: List<String> = listOf(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    readCSV(
        url.openStream(),
        delimiter,
        header,
        isCompressed(url),
        colTypes,
        skipLines,
        readLines,
        duplicate,
        charset,
        parserOptions,
    )

@Deprecated(
    message = READ_CSV,
    replaceWith = ReplaceWith(READ_CSV_STREAM_REPLACE, READ_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun DataFrame.Companion.readCSV(
    stream: InputStream,
    delimiter: Char = ',',
    header: List<String> = listOf(),
    isCompressed: Boolean = false,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = Charsets.UTF_8,
    parserOptions: ParserOptions? = null,
): DataFrame<*> =
    readDelim(
        stream,
        delimiter,
        header,
        isCompressed,
        CSVType.DEFAULT,
        colTypes,
        skipLines,
        readLines,
        duplicate,
        charset,
        parserOptions,
    )

@Deprecated(
    message = AS_URL,
    replaceWith = ReplaceWith(AS_URL_REPLACE, AS_URL_IMPORT),
    level = DeprecationLevel.ERROR,
)
public fun asURL(fileOrUrl: String): URL = asUrl(fileOrUrl)

private fun getFormat(
    type: CSVType,
    delimiter: Char,
    header: List<String>,
    duplicate: Boolean,
): CSVFormat =
    type.format.builder()
        .setDelimiter(delimiter)
        .setHeader(*header.toTypedArray())
        .setAllowMissingColumnNames(duplicate)
        .build()

@Deprecated(
    message = READ_DELIM,
    replaceWith = ReplaceWith(READ_DELIM_STREAM_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun DataFrame.Companion.readDelim(
    inStream: InputStream,
    delimiter: Char = ',',
    header: List<String> = listOf(),
    isCompressed: Boolean = false,
    csvType: CSVType,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    duplicate: Boolean = true,
    charset: Charset = defaultCharset,
    parserOptions: ParserOptions? = null,
): AnyFrame {
    val bufferedInStream = BufferedInputStream(if (isCompressed) GZIPInputStream(inStream) else inStream)
    val bomIn = BOMInputStream.builder().setInputStream(bufferedInStream).get()
    val bufferedReader = BufferedReader(InputStreamReader(bomIn, charset))

    return readDelim(
        reader = bufferedReader,
        format = getFormat(csvType, delimiter, header, duplicate),
        colTypes = colTypes,
        skipLines = skipLines,
        readLines = readLines,
        parserOptions = parserOptions,
    )
}

/** Column types that DataFrame can [parse] from a [String]. */
public enum class ColType {
    Int,
    Long,
    Double,
    Boolean,
    BigDecimal,
    BigInteger,
    LocalDate,
    LocalTime,
    LocalDateTime,
    String,

    @Deprecated(
        message = COL_TYPE_INSTANT,
        replaceWith = ReplaceWith(COL_TYPE_INSTANT_REPLACE),
        level = DeprecationLevel.ERROR,
    )
    Instant,
    Duration,
    Url,
    JsonArray,
    JsonObject,
    Char,

    @Deprecated(
        message = COL_TYPE_DEPRECATED_INSTANT,
        replaceWith = ReplaceWith(COL_TYPE_DEPRECATED_INSTANT_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    DeprecatedInstant,

    /** Temporary, will be renamed to [Instant] in 1.1+ */
    StdlibInstant,
    ;

    public companion object {

        /**
         * You can add a default column type to the `colTypes` parameter
         * by setting the key to [ColType.DEFAULT] and the value to the desired type.
         */
        public const val DEFAULT: kotlin.String = ".default"
    }
}

public fun ColType.toType(): KClass<*> = toKType().classifier as KClass<*>

public fun ColType.toKType(): KType =
    when (this) {
        ColType.Int -> typeOf<Int>()

        ColType.Long -> typeOf<Long>()

        ColType.Double -> typeOf<Double>()

        ColType.Boolean -> typeOf<Boolean>()

        ColType.BigDecimal -> typeOf<BigDecimal>()

        ColType.BigInteger -> typeOf<BigInteger>()

        ColType.LocalDate -> typeOf<LocalDate>()

        ColType.LocalTime -> typeOf<LocalTime>()

        ColType.LocalDateTime -> typeOf<LocalDateTime>()

        ColType.String -> typeOf<String>()

        ColType.DeprecatedInstant -> typeOf<DeprecatedInstant>()

        ColType.StdlibInstant -> typeOf<StdlibInstant>()

        ColType.Duration -> typeOf<Duration>()

        ColType.Url -> typeOf<URL>()

        ColType.JsonArray -> typeOf<DataFrame<*>>()

        ColType.JsonObject -> typeOf<DataRow<*>>()

        ColType.Char -> typeOf<Char>()

        else -> {
            // can't reference ColType.Instant directly because of the deprecation cycle
            require(this.name == "Instant") { "Unknown column type: $this" }
            typeOf<DeprecatedInstant>()
        }
    }

@Deprecated(
    message = READ_DELIM,
    replaceWith = ReplaceWith(READ_DELIM_READER_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun DataFrame.Companion.readDelim(
    reader: Reader,
    format: CSVFormat = CSVFormat.DEFAULT.builder()
        .setHeader()
        .build(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    readLines: Int? = null,
    parserOptions: ParserOptions? = null,
): AnyFrame =
    try {
        readDelimImpl(
            reader = reader,
            format = format,
            colTypes = colTypes,
            skipLines = skipLines,
            readLines = readLines,
            parserOptions = parserOptions,
        )
    } catch (_: OutOfMemoryError) {
        throw OutOfMemoryError(
            "Ran out of memory reading this CSV-like file. " +
                "You can try our new experimental CSV reader by adding the dependency " +
                "\"org.jetbrains.kotlinx:dataframe-csv:{VERSION}\" and using `DataFrame.readCsv()` instead of " +
                "`DataFrame.readCSV()`.",
        )
    }

@Deprecated(
    message = WRITE_CSV,
    replaceWith = ReplaceWith(WRITE_CSV_FILE_REPLACE, WRITE_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun AnyFrame.writeCSV(file: File, format: CSVFormat = CSVFormat.DEFAULT): Unit =
    writeCSV(FileWriter(file), format)

@Deprecated(
    message = WRITE_CSV,
    replaceWith = ReplaceWith(WRITE_CSV_PATH_REPLACE, WRITE_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun AnyFrame.writeCSV(path: String, format: CSVFormat = CSVFormat.DEFAULT): Unit =
    writeCSV(FileWriter(path), format)

@Deprecated(
    message = WRITE_CSV,
    replaceWith = ReplaceWith(WRITE_CSV_WRITER_REPLACE, WRITE_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun AnyFrame.writeCSV(writer: Appendable, format: CSVFormat = CSVFormat.DEFAULT) {
    format.print(writer).use { printer ->
        if (!format.skipHeaderRecord) {
            printer.printRecord(columnNames())
        }
        forEach {
            val values = it.values.map {
                when (it) { // todo use compileOnly?
                    is AnyRow ->
                        error(
                            "Encountered a DataRow when writing CSV. This needs to be converted to JSON, which is not supported by `writeCSV` anymore. Please use `df.writeCsv()` instead.",
                        )

                    is AnyFrame ->
                        error(
                            "Encountered a DataFrame when writing CSV. This needs to be converted to JSON, which is not supported by `writeCSV` anymore. Please use `df.writeCsv()` instead.",
                        )

                    else -> it
                }
            }
            printer.printRecord(values)
        }
    }
}

@Deprecated(
    message = TO_CSV,
    replaceWith = ReplaceWith(TO_CSV_REPLACE, TO_CSV_IMPORT),
    level = DeprecationLevel.WARNING,
)
public fun AnyFrame.toCsv(format: CSVFormat = CSVFormat.DEFAULT): String =
    StringWriter().use {
        this.writeCSV(it, format)
        it
    }.toString()
