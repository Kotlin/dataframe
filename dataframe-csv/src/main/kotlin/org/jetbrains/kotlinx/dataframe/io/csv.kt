@file:JvmName("CsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams
import org.jetbrains.kotlinx.dataframe.io.Csv.ReadOptions
import org.jetbrains.kotlinx.dataframe.io.Csv.WriteOptions
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

public class CsvDeephaven(private val delimiter: Char = DelimParams.CSV_DELIMITER) : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): DataFrame<*> =
        DataFrame.readCsv(inputStream = stream, header = header, delimiter = delimiter)

    override fun readDataFrame(file: File, header: List<String>): DataFrame<*> =
        DataFrame.readCsv(file = file, header = header, delimiter = delimiter)

    override fun readDataFrame(path: Path, header: List<String>): DataFrame<*> =
        DataFrame.readCsv(path = path, delimiter = delimiter, header = header)

    override fun acceptsExtension(ext: String): Boolean = ext == "csv"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 20_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        val arguments = MethodArguments().add("delimiter", typeOf<Char>(), "'%L'", delimiter)
        return DefaultReadCsvMethod(pathRepresentation, arguments)
    }
}

public class Csv :
    DataFrameReadSource,
    DataFrameWriteTarget {

    public data class ReadOptions(
        val delimiter: Char,
        val header: List<String>,
        val charset: Charset?,
        val colTypes: Map<String, ColType>,
        val skipLines: Long,
        val readLines: Long?,
        val parserOptions: ParserOptions?,
        val ignoreEmptyLines: Boolean,
        val allowMissingColumns: Boolean,
        val ignoreExcessColumns: Boolean,
        val quote: Char,
        val ignoreSurroundingSpaces: Boolean,
        val trimInsideQuoted: Boolean,
        val parseParallel: Boolean,
    ) : DataFrameReadOptions {
        public companion object {
            public operator fun invoke(
                delimiter: Char = DelimParams.CSV_DELIMITER,
                header: List<String> = DelimParams.HEADER,
                charset: Charset? = DelimParams.CHARSET,
                colTypes: Map<String, ColType> = DelimParams.COL_TYPES,
                skipLines: Long = DelimParams.SKIP_LINES,
                readLines: Long? = DelimParams.READ_LINES,
                parserOptions: ParserOptions? = DelimParams.PARSER_OPTIONS,
                ignoreEmptyLines: Boolean = DelimParams.IGNORE_EMPTY_LINES,
                allowMissingColumns: Boolean = DelimParams.ALLOW_MISSING_COLUMNS,
                ignoreExcessColumns: Boolean = DelimParams.IGNORE_EXCESS_COLUMNS,
                quote: Char = DelimParams.QUOTE,
                ignoreSurroundingSpaces: Boolean = DelimParams.IGNORE_SURROUNDING_SPACES,
                trimInsideQuoted: Boolean = DelimParams.TRIM_INSIDE_QUOTED,
                parseParallel: Boolean = DelimParams.PARSE_PARALLEL,
            ): ReadOptions =
                ReadOptions(
                    delimiter = delimiter,
                    header = header,
                    charset = charset,
                    colTypes = colTypes,
                    skipLines = skipLines,
                    readLines = readLines,
                    parserOptions = parserOptions,
                    ignoreEmptyLines = ignoreEmptyLines,
                    allowMissingColumns = allowMissingColumns,
                    ignoreExcessColumns = ignoreExcessColumns,
                    quote = quote,
                    ignoreSurroundingSpaces = ignoreSurroundingSpaces,
                    trimInsideQuoted = trimInsideQuoted,
                    parseParallel = parseParallel,
                )
        }
    }

    override val supportedReadingTypes: Set<KType> =
        setOf(typeOf<URL>(), typeOf<Path>(), typeOf<File>(), typeOf<String>(), typeOf<InputStream>())

    public data class WriteOptions(
        val delimiter: Char,
        val includeHeader: Boolean,
        val quote: Char?,
        val quoteMode: QuoteMode,
        val escapeChar: Char?,
        val commentChar: Char?,
        val headerComments: List<String>,
        val recordSeparator: String,
        val adjustCsvFormat: AdjustCSVFormat,
    ) : DataFrameWriteOptions {
        public companion object {
            public operator fun invoke(
                delimiter: Char = DelimParams.CSV_DELIMITER,
                includeHeader: Boolean = DelimParams.INCLUDE_HEADER,
                quote: Char? = DelimParams.QUOTE,
                quoteMode: QuoteMode = DelimParams.QUOTE_MODE,
                escapeChar: Char? = DelimParams.ESCAPE_CHAR,
                commentChar: Char? = DelimParams.COMMENT_CHAR,
                headerComments: List<String> = DelimParams.HEADER_COMMENTS,
                recordSeparator: String = DelimParams.RECORD_SEPARATOR,
                adjustCsvFormat: AdjustCSVFormat = DelimParams.ADJUST_CSV_FORMAT,
            ): WriteOptions =
                WriteOptions(
                    delimiter = delimiter,
                    includeHeader = includeHeader,
                    quote = quote,
                    quoteMode = quoteMode,
                    escapeChar = escapeChar,
                    commentChar = commentChar,
                    headerComments = headerComments,
                    recordSeparator = recordSeparator,
                    adjustCsvFormat = adjustCsvFormat,
                )
        }
    }

    override val supportedWritingTypes: Set<KType> =
        setOf(
            typeOf<Path>(),
            typeOf<File>(),
            typeOf<String>(),
            typeOf<Appendable>(),
            typeOf<OutputStream>(),
            typeOf<Function1<String, *>>(),
        )

    public companion object {
        internal val EXTENSIONS = setOf("csv", "zip", "gz")
        internal val WRITE_EXTENSIONS = setOf("csv")
        internal val MIME_TYPES = setOf(
            "text/csv",
            "application/zip",
            "application/gzip",
        )
        internal val WRITE_MIME_TYPES = setOf("text/csv")
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is ReadOptions) return false
        if (sourceInfo.extension != null && sourceInfo.extension !in EXTENSIONS) return false
        if (sourceInfo.mimeType != null && sourceInfo.mimeType !in MIME_TYPES) return false
        return supportedReadingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    override fun acceptsTarget(sourceInfo: DataSourceInfo, options: DataFrameWriteOptions?): Boolean {
        if (options != null && options !is WriteOptions) return false
        if (sourceInfo.extension != null && sourceInfo.extension !in WRITE_EXTENSIONS) return false
        if (sourceInfo.mimeType != null && sourceInfo.mimeType !in WRITE_MIME_TYPES) return false
        return supportedWritingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    override fun writeDataFrame(
        dataFrame: DataFrame<*>,
        target: Any,
        targetInfo: DataSourceInfo,
        options: DataFrameWriteOptions?,
    ): Result<Unit> =
        runCatching {
            val opts = (options ?: WriteOptions()) as WriteOptions
            val kType = targetInfo.kType

            @Suppress("RedundantReturnKeyword")
            return@runCatching when {
                kType.isSubTypeOf<Path>() ->
                    dataFrame.writeCsv(path = target as Path, options = opts)

                kType.isSubTypeOf<File>() ->
                    dataFrame.writeCsv(file = target as File, options = opts)

                kType.isSubTypeOf<String>() ->
                    dataFrame.writeCsv(path = target as String, options = opts)

                kType.isSubTypeOf<Appendable>() ->
                    dataFrame.writeCsv(writer = target as Appendable, options = opts)

                kType.isSubTypeOf<OutputStream>() ->
                    OutputStreamWriter(target as OutputStream).use { dataFrame.writeCsv(writer = it, options = opts) }

                kType.isSubTypeOf<Function1<String, *>>() -> {
                    (target as Function1<String, *>).invoke(dataFrame.toCsvStr(options = opts))
                    Unit
                }

                else -> return Result.failure(IllegalStateException("Unsupported target type for CSV writing: $kType"))
            }
        }

    override fun writeDataRow(
        dataRow: DataRow<*>,
        target: Any,
        targetInfo: DataSourceInfo,
        options: DataFrameWriteOptions?,
    ): Result<Unit> = writeDataFrame(dataRow.toDataFrame(), target, targetInfo, options)

    override fun readDataFrame(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrame<*>> =
        runCatching {
            val opts = (options ?: ReadOptions()) as ReadOptions
            val kType = sourceInfo.kType

            val url: URL? = when {
                kType.isSubTypeOf<URL>() -> source as? URL
                kType.isSubTypeOf<Path>() -> (source as? Path)?.toUri()?.toURL()
                kType.isSubTypeOf<File>() -> (source as? File)?.toPath()?.toUri()?.toURL()
                else -> null
            }
            if (url != null) {
                return@runCatching DataFrame.readCsv(
                    url = url,
                    delimiter = opts.delimiter,
                    header = opts.header,
                    charset = opts.charset,
                    colTypes = opts.colTypes,
                    skipLines = opts.skipLines,
                    readLines = opts.readLines,
                    parserOptions = opts.parserOptions,
                    ignoreEmptyLines = opts.ignoreEmptyLines,
                    allowMissingColumns = opts.allowMissingColumns,
                    ignoreExcessColumns = opts.ignoreExcessColumns,
                    quote = opts.quote,
                    ignoreSurroundingSpaces = opts.ignoreSurroundingSpaces,
                    trimInsideQuoted = opts.trimInsideQuoted,
                    parseParallel = opts.parseParallel,
                )
            }

            @Suppress("RedundantReturnKeyword")
            return@runCatching when {
                kType.isSubTypeOf<InputStream>() -> {
                    DataFrame.readCsv(
                        inputStream = source as InputStream,
                        delimiter = opts.delimiter,
                        header = opts.header,
                        charset = opts.charset,
                        colTypes = opts.colTypes,
                        skipLines = opts.skipLines,
                        readLines = opts.readLines,
                        parserOptions = opts.parserOptions,
                        ignoreEmptyLines = opts.ignoreEmptyLines,
                        allowMissingColumns = opts.allowMissingColumns,
                        ignoreExcessColumns = opts.ignoreExcessColumns,
                        quote = opts.quote,
                        ignoreSurroundingSpaces = opts.ignoreSurroundingSpaces,
                        trimInsideQuoted = opts.trimInsideQuoted,
                        parseParallel = opts.parseParallel,
                    )
                }

                kType.isSubTypeOf<String>() -> {
                    // early fail
                    if (opts.delimiter !in source as String) {
                        return Result.failure(
                            IllegalStateException("String does not contain delimiter '${opts.delimiter}'"),
                        )
                    }

                    DataFrame.readCsvStr(
                        text = source,
                        delimiter = opts.delimiter,
                        header = opts.header,
                        colTypes = opts.colTypes,
                        skipLines = opts.skipLines,
                        readLines = opts.readLines,
                        parserOptions = opts.parserOptions,
                        ignoreEmptyLines = opts.ignoreEmptyLines,
                        allowMissingColumns = opts.allowMissingColumns,
                        ignoreExcessColumns = opts.ignoreExcessColumns,
                        quote = opts.quote,
                        ignoreSurroundingSpaces = opts.ignoreSurroundingSpaces,
                        trimInsideQuoted = opts.trimInsideQuoted,
                        parseParallel = opts.parseParallel,
                    )
                }

                else -> return Result.failure(IllegalStateException("Cannot read source of type $kType as CSV"))
            }
        }

    override val testOrder: Int = 20_000

    override fun toString(): String = "Csv"
}

public val DataFrameReadOptions.Companion.Csv: ReadOptions.Companion
    get() = ReadOptions.Companion

public val DataFrameWriteOptions.Companion.Csv: WriteOptions.Companion
    get() = WriteOptions.Companion

private inline fun <reified T> KType.isSubTypeOf(): Boolean = this.isSubtypeOf(typeOf<T>())

private const val READ_CSV = "readCsv"

internal class DefaultReadCsvMethod(path: String?, arguments: MethodArguments) :
    AbstractDefaultReadMethod(path, arguments, READ_CSV)

private fun DataFrame<*>.writeCsv(path: Path, options: WriteOptions): Unit =
    writeCsv(
        path = path,
        delimiter = options.delimiter,
        includeHeader = options.includeHeader,
        quote = options.quote,
        quoteMode = options.quoteMode,
        escapeChar = options.escapeChar,
        commentChar = options.commentChar,
        headerComments = options.headerComments,
        recordSeparator = options.recordSeparator,
    )

private fun DataFrame<*>.writeCsv(file: File, options: WriteOptions): Unit =
    writeCsv(
        file = file,
        delimiter = options.delimiter,
        includeHeader = options.includeHeader,
        quote = options.quote,
        quoteMode = options.quoteMode,
        escapeChar = options.escapeChar,
        commentChar = options.commentChar,
        headerComments = options.headerComments,
        recordSeparator = options.recordSeparator,
    )

private fun DataFrame<*>.writeCsv(path: String, options: WriteOptions): Unit =
    writeCsv(
        path = path,
        delimiter = options.delimiter,
        includeHeader = options.includeHeader,
        quote = options.quote,
        quoteMode = options.quoteMode,
        escapeChar = options.escapeChar,
        commentChar = options.commentChar,
        headerComments = options.headerComments,
        recordSeparator = options.recordSeparator,
    )

private fun DataFrame<*>.writeCsv(writer: Appendable, options: WriteOptions): Unit =
    writeCsv(
        writer = writer,
        delimiter = options.delimiter,
        includeHeader = options.includeHeader,
        quote = options.quote,
        quoteMode = options.quoteMode,
        escapeChar = options.escapeChar,
        commentChar = options.commentChar,
        headerComments = options.headerComments,
        recordSeparator = options.recordSeparator,
        adjustCsvFormat = options.adjustCsvFormat,
    )

private fun DataFrame<*>.toCsvStr(options: WriteOptions): String =
    toCsvStr(
        delimiter = options.delimiter,
        includeHeader = options.includeHeader,
        quote = options.quote,
        quoteMode = options.quoteMode,
        escapeChar = options.escapeChar,
        commentChar = options.commentChar,
        headerComments = options.headerComments,
        recordSeparator = options.recordSeparator,
    )
