package org.jetbrains.kotlinx.dataframe.io

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.jupyter.api.Code
import java.io.BufferedInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL
import java.util.ServiceLoader
import kotlin.reflect.KType

public sealed interface SupportedFormat {

    public fun acceptsExtension(ext: String): Boolean

    public fun acceptsSample(sample: SupportedFormatSample): Boolean

    // `DataFrame.Companion.read` methods uses this to sort list of all supported formats in ascending order (-1, 2, 10)
    // sorted list is used to test if any format can read given input
    public val testOrder: Int

    public fun createDefaultReadMethod(pathRepresentation: String? = null): DefaultReadDfMethod
}

public sealed interface SupportedFormatSample {

    @JvmInline
    public value class File(public val sampleFile: java.io.File) : SupportedFormatSample

    @JvmInline
    public value class URL(public val sampleUrl: java.net.URL) : SupportedFormatSample

    @JvmInline
    public value class PathString(public val samplePath: String) : SupportedFormatSample

    @JvmInline
    public value class DataString(public val sampleData: String) : SupportedFormatSample
}

/**
 * Implement this interface to provide additional supported formats for DataFrames (such as JSON, XML, CSV, etc.).
 * A [SupportedDataFrameFormat] is read directly to a DataFrame. If specified using
 * [ImportDataSchema] or using the Gradle plugin, the read DataFrame will be used to
 * generate [DataSchema] interfaces.
 */
public interface SupportedDataFrameFormat : SupportedFormat {
    public fun readDataFrame(stream: InputStream, header: List<String> = emptyList()): DataFrame<*>

    public fun readDataFrame(file: File, header: List<String> = emptyList()): DataFrame<*>
}

/**
 * Implement this interface to provide additional [DataSchema] interface generation formats for DataFrames (such as OpenAPI).
 * Note, this doesn't add functionality to [DataFrame.Companion.read], just [ImportDataSchema] and Gradle plugin.
 *
 * Return type will be a [Code] which contains a generated `interface` with the given name containing generated
 * [DataSchema] interfaces and `enum`s, with `typealias`es (and optional extension functions in some integration)
 * outside the interface.
 */
public interface SupportedCodeGenerationFormat : SupportedFormat {

    /**
     * @param stream where to read the schema from
     * @param name the name of the top-level interface to generate
     * @param generateHelperCompanionObject whether to generate a helper companion object (only needed for Jupyter)
     */
    public fun readCodeForGeneration(
        stream: InputStream,
        name: String,
        generateHelperCompanionObject: Boolean = false,
    ): Code

    /**
     * @param file where to read the schema from
     * @param name the name of the top-level interface to generate
     * @param generateHelperCompanionObject whether to generate a helper companion object (only needed for Jupyter)
     */
    public fun readCodeForGeneration(
        file: File,
        name: String,
        generateHelperCompanionObject: Boolean = false,
    ): Code
}

public class MethodArguments {
    internal val defaultValues = mutableListOf<Argument>()

    public fun add(name: String, type: KType, initializerTemplate: String, vararg values: Any?): MethodArguments {
        val capitalizedName = name.replaceFirstChar { it.uppercaseChar() }
        val propertyName = "default$capitalizedName"

        val property = PropertySpec
            .builder(propertyName, type.asTypeName())
            .initializer(initializerTemplate, args = values)
            .build()

        defaultValues += Argument(name, property)
        return this
    }

    internal class Argument(val name: String, val property: PropertySpec)

    public companion object {
        public val EMPTY: MethodArguments get() = MethodArguments()
    }
}

/**
 * NOTE: Needs to have fully qualified name in
 * resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.SupportedFormat
 * to be detected here.
 */
internal val supportedFormats: List<SupportedFormat> by lazy {
    (
        ServiceLoader.load(SupportedDataFrameFormat::class.java).toList() +
            ServiceLoader.load(SupportedCodeGenerationFormat::class.java).toList() +
            ServiceLoader.load(SupportedFormat::class.java).toList()
        ).distinct()
        .sortedBy { it.testOrder }
}

internal fun guessFormatForExtension(
    ext: String,
    formats: List<SupportedFormat> = supportedFormats,
    sample: SupportedFormatSample? = null,
): SupportedFormat? = formats.firstOrNull { it.acceptsExtension(ext) && (sample == null || it.acceptsSample(sample)) }

internal fun guessFormat(
    file: File,
    formats: List<SupportedFormat> = supportedFormats,
    sample: SupportedFormatSample.File? = SupportedFormatSample.File(file),
): SupportedFormat? = guessFormatForExtension(file.extension.lowercase(), formats, sample = sample)

internal fun guessFormat(
    url: URL,
    formats: List<SupportedFormat> = supportedFormats,
    sample: SupportedFormatSample.URL? = SupportedFormatSample.URL(url),
): SupportedFormat? = guessFormatForExtension(url.path.substringAfterLast("."), formats, sample = sample)

internal fun guessFormat(
    path: String,
    formats: List<SupportedFormat> = supportedFormats,
    sample: SupportedFormatSample.PathString? = SupportedFormatSample.PathString(path),
): SupportedFormat? = guessFormatForExtension(path.substringAfterLast("."), formats, sample = sample)

private class NotCloseableStream(val src: InputStream) : InputStream() {
    override fun read(): Int = src.read()

    fun doClose() = src.close()

    override fun reset() = src.reset()
    override fun available() = src.available()
    override fun markSupported() = src.markSupported()
    override fun mark(readlimit: Int) = src.mark(readlimit)
}

/**
 * @param stream where to read the schema from
 * @param name the name of the top-level interface to generate
 * @param format the format to use
 * @param generateHelperCompanionObject whether to generate a helper companion object (only needed for Jupyter)
 * @param formats Optional list of supported formats to use. If not specified, all formats will be used.
 *
 * @return [GeneratedCode] with generated code
 */
internal fun readCodeForGeneration(
    stream: InputStream,
    name: String,
    format: SupportedCodeGenerationFormat? = null,
    generateHelperCompanionObject: Boolean = false,
    formats: List<SupportedCodeGenerationFormat> = supportedFormats.filterIsInstance<SupportedCodeGenerationFormat>(),
): GeneratedCode {
    if (format != null) return format to format.readCodeForGeneration(stream, name, generateHelperCompanionObject)
    val input = NotCloseableStream(if (stream.markSupported()) stream else BufferedInputStream(stream))
    try {
        val readLimit = 10000
        input.mark(readLimit)

        formats.sortedBy { it.testOrder }.forEach {
            try {
                input.reset()
                return it to it.readCodeForGeneration(input, name, generateHelperCompanionObject)
            } catch (_: Exception) {
            }
        }
        throw IllegalArgumentException("Unknown stream format")
    } finally {
        input.doClose()
    }
}

internal fun DataFrame.Companion.read(
    stream: InputStream,
    format: SupportedDataFrameFormat? = null,
    header: List<String> = emptyList(),
    formats: List<SupportedDataFrameFormat> = supportedFormats.filterIsInstance<SupportedDataFrameFormat>(),
): ReadAnyFrame {
    if (format != null) return format to format.readDataFrame(stream, header = header)
    val input = NotCloseableStream(if (stream.markSupported()) stream else BufferedInputStream(stream))
    try {
        val readLimit = 10000
        input.mark(readLimit)

        formats.sortedBy { it.testOrder }.forEach {
            try {
                input.reset()
                return it to it.readDataFrame(input, header = header)
            } catch (e: Exception) {
            }
        }
        throw IllegalArgumentException("Unknown stream format")
    } finally {
        input.doClose()
    }
}

internal fun DataFrame.Companion.read(
    file: File,
    format: SupportedDataFrameFormat? = null,
    header: List<String> = emptyList(),
    formats: List<SupportedDataFrameFormat> = supportedFormats.filterIsInstance<SupportedDataFrameFormat>(),
): ReadAnyFrame {
    if (format != null) return format to format.readDataFrame(file, header = header)
    formats.sortedBy { it.testOrder }.forEach {
        try {
            return it to it.readDataFrame(file, header = header)
        } catch (e: FileNotFoundException) {
            throw e
        } catch (e: Exception) {
        }
    }
    throw IllegalArgumentException("Unknown file format")
}

internal data class ReadAnyFrame(val format: SupportedDataFrameFormat, val df: AnyFrame)

internal infix fun SupportedDataFrameFormat.to(df: AnyFrame) = ReadAnyFrame(this, df)

internal data class GeneratedCode(
    val format: SupportedCodeGenerationFormat,
    val code: Code,
)

internal infix fun SupportedCodeGenerationFormat.to(code: Code) =
    GeneratedCode(this, code)

public fun DataFrame.Companion.read(file: File, header: List<String> = emptyList()): AnyFrame =
    read(
        file = file,
        format = guessFormat(file)?.also {
            if (it !is SupportedDataFrameFormat) error("Format $it does not support reading dataframes")
        } as SupportedDataFrameFormat?,
        header = header,
    ).df

public fun DataRow.Companion.read(file: File, header: List<String> = emptyList()): AnyRow =
    DataFrame.read(file, header).single()

public fun DataFrame.Companion.read(url: URL, header: List<String> = emptyList()): AnyFrame = when {
    isFile(url) -> read(urlAsFile(url), header)
    isProtocolSupported(url) -> catchHttpResponse(url) {
        read(
            stream = it,
            format = guessFormat(url)?.also {
                if (it !is SupportedDataFrameFormat) error("Format $it does not support reading dataframes")
            } as SupportedDataFrameFormat?,
            header = header,
        ).df
    }

    else -> throw IllegalArgumentException("Invalid protocol for url $url")
}

public fun DataRow.Companion.read(url: URL, header: List<String> = emptyList()): AnyRow =
    DataFrame.read(url, header).single()

public fun DataFrame.Companion.read(path: String, header: List<String> = emptyList()): AnyFrame =
    read(asURL(path), header)

public fun DataRow.Companion.read(path: String, header: List<String> = emptyList()): AnyRow =
    DataFrame.read(path, header).single()

public fun URL.readDataFrame(header: List<String> = emptyList()): AnyFrame = DataFrame.read(this, header)
public fun URL.readDataRow(header: List<String> = emptyList()): AnyRow = DataRow.read(this, header)

public fun File.readDataFrame(header: List<String> = emptyList()): AnyFrame = DataFrame.read(this, header)
public fun File.readDataRow(header: List<String> = emptyList()): AnyRow = DataRow.read(this, header)
