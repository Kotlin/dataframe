@file:JvmName("CsvDeephavenKt")

package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.documentationCsv.DelimParams
import java.io.File
import java.io.InputStream
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

public class Csv : DataFrameReadSource {

    public data class Options(
        val delimiter: Char = DelimParams.CSV_DELIMITER,
        val header: List<String> = DelimParams.HEADER,
        val charset: Charset? = DelimParams.CHARSET,
        val colTypes: Map<String, ColType> = DelimParams.COL_TYPES,
        val skipLines: Long = DelimParams.SKIP_LINES,
        val readLines: Long? = DelimParams.READ_LINES,
        val parserOptions: ParserOptions? = DelimParams.PARSER_OPTIONS,
        val ignoreEmptyLines: Boolean = DelimParams.IGNORE_EMPTY_LINES,
        val allowMissingColumns: Boolean = DelimParams.ALLOW_MISSING_COLUMNS,
        val ignoreExcessColumns: Boolean = DelimParams.IGNORE_EXCESS_COLUMNS,
        val quote: Char = DelimParams.QUOTE,
        val ignoreSurroundingSpaces: Boolean = DelimParams.IGNORE_SURROUNDING_SPACES,
        val trimInsideQuoted: Boolean = DelimParams.TRIM_INSIDE_QUOTED,
        val parseParallel: Boolean = DelimParams.PARSE_PARALLEL,
    ) : DataFrameReadOptions

    override val supportedTypes: Set<KType> =
        setOf(typeOf<URL>(), typeOf<Path>(), typeOf<File>(), typeOf<String>(), typeOf<InputStream>())

    public companion object {
        internal val EXTENSIONS = setOf("csv", "zip", "gz")
        internal val MIME_TYPES = setOf(
            "text/csv",
            "application/zip",
            "application/gzip",
        )
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is Options) return false
        if (sourceInfo.extension != null && sourceInfo.extension !in EXTENSIONS) return false
        if (sourceInfo.mimeType != null && sourceInfo.mimeType !in MIME_TYPES) return false
        return supportedTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    override fun readDataFrameOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): DataFrame<*>? {
        val opts = (options ?: Options()) as Options
        val kType = sourceInfo.kType

        val url: URL? = when {
            kType.isSubTypeOf<URL>() -> source as? URL
            kType.isSubTypeOf<Path>() -> (source as? Path)?.toUri()?.toURL()
            kType.isSubTypeOf<File>() -> (source as? File)?.toPath()?.toUri()?.toURL()
            else -> null
        }
        if (url != null) {
            return DataFrame.readCsv(
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

        return when {
            kType.isSubTypeOf<InputStream>() ->
                (source as? InputStream)?.let { stream ->
                    DataFrame.readCsv(
                        inputStream = stream,
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

            kType.isSubTypeOf<String>() ->
                (source as? String)?.let { text ->
                    // early fail
                    if (opts.delimiter !in text) return null

                    DataFrame.readCsvStr(
                        text = text,
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

            else -> null
        }
    }

    override val testOrder: Int = 20_000

    override fun toString(): String = "Csv"
}

private inline fun <reified T> KType.isSubTypeOf(): Boolean = this.isSubtypeOf(typeOf<T>())

private const val READ_CSV = "readCsv"

internal class DefaultReadCsvMethod(path: String?, arguments: MethodArguments) :
    AbstractDefaultReadMethod(path, arguments, READ_CSV)
