@file:JvmName("TsvDeephavenKt")

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

public class TsvDeephaven(private val delimiter: Char = DelimParams.TSV_DELIMITER) : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): DataFrame<*> =
        DataFrame.readTsv(inputStream = stream, header = header, delimiter = delimiter)

    override fun readDataFrame(file: File, header: List<String>): DataFrame<*> =
        DataFrame.readTsv(file = file, header = header, delimiter = delimiter)

    override fun readDataFrame(path: Path, header: List<String>): DataFrame<*> =
        DataFrame.readTsv(path = path, header = header, delimiter = delimiter)

    override fun acceptsExtension(ext: String): Boolean = ext == "tsv"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 30_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        val arguments = MethodArguments().add("delimiter", typeOf<Char>(), "'%L'", delimiter)
        return DefaultReadTsvMethod(pathRepresentation, arguments)
    }
}

public class Tsv : DataFrameReadSource {

    public data class ReadOptions(
        val delimiter: Char = DelimParams.TSV_DELIMITER,
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

    override val supportedReadingTypes: Set<KType> =
        setOf(typeOf<URL>(), typeOf<Path>(), typeOf<File>(), typeOf<String>(), typeOf<InputStream>())

    public companion object {
        internal val EXTENSIONS = setOf("tsv", "zip", "gz")
        internal val MIME_TYPE = setOf(
            "text/tab-separated-values",
            "application/zip",
            "application/gzip",
        )
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is ReadOptions) return false
        if (sourceInfo.extension != null && sourceInfo.extension !in EXTENSIONS) return false
        if (sourceInfo.mimeType != null && sourceInfo.mimeType !in MIME_TYPE) return false
        return supportedReadingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

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
                return@runCatching DataFrame.readTsv(
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

            when {
                kType.isSubTypeOf<InputStream>() -> {
                    DataFrame.readTsv(
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

                    DataFrame.readTsvStr(
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

                else -> return Result.failure(IllegalStateException("Cannot read source of type $kType as TSV"))
            }
        }

    override val testOrder: Int = 30_000

    override fun toString(): String = "Tsv"
}

private inline fun <reified T> KType.isSubTypeOf(): Boolean = this.isSubtypeOf(typeOf<T>())

private const val READ_TSV = "readTsv"

internal class DefaultReadTsvMethod(path: String?, arguments: MethodArguments) :
    AbstractDefaultReadMethod(path, arguments, READ_TSV)
