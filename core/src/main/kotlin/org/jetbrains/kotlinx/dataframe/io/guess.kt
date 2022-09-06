package org.jetbrains.kotlinx.dataframe.io

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.codeGen.CodeWithConverter
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import java.io.BufferedInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL
import java.util.ServiceLoader
import kotlin.reflect.KType

public interface SupportedFormat {

    public fun acceptsExtension(ext: String): Boolean

    // `DataFrame.Companion.read` methods uses this to sort list of all supported formats in ascending order (-1, 2, 10)
    // sorted list is used to test if any format can read given input
    public val testOrder: Int

    public fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod
}

public interface SupportedDataFrameFormat : SupportedFormat {
    public fun readDataFrame(stream: InputStream, header: List<String> = emptyList()): DataFrame<*>

    public fun readDataFrame(file: File, header: List<String> = emptyList()): DataFrame<*>
}

public interface SupportedCodeGenerationFormat : SupportedFormat {

    public fun readCodeForGeneration(stream: InputStream): CodeWithConverter

    public fun readCodeForGeneration(file: File): CodeWithConverter
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

internal val supportedFormats: List<SupportedFormat> by lazy {
    ServiceLoader.load(SupportedDataFrameFormat::class.java).toList() +
        ServiceLoader.load(SupportedCodeGenerationFormat::class.java).toList()
}

internal fun guessFormatForExtension(
    ext: String,
    formats: List<SupportedFormat> = supportedFormats,
    sample: Any? = null
): SupportedFormat? =
    formats.firstOrNull { it.acceptsExtension(ext) }
        ?.let {
            // a json file can be read both for openApi as for JSON try to make the distinction
            if (it is JSON || it is OpenApi) {
                val isOpenApi = when (sample) {
                    is File -> isOpenApi(sample)
                    is URL -> isOpenApi(sample)
                    is String -> isOpenApi(sample)
                    else -> false
                }

                if (isOpenApi) {
                    supportedFormats.firstOrNull { it is OpenApi }
                        ?: supportedFormats.firstOrNull { it is JSON }
                } else {
                    it
                }
            } else it
        }

internal fun guessFormat(
    file: File,
    formats: List<SupportedFormat> = supportedFormats,
    sample: Any? = file,
): SupportedFormat? =
    guessFormatForExtension(file.extension.lowercase(), formats, sample = sample)

internal fun guessFormat(
    url: URL,
    formats: List<SupportedFormat> = supportedFormats,
    sample: Any? = url,
): SupportedFormat? =
    guessFormat(url.path, formats, sample = sample)

internal fun guessFormat(
    url: String,
    formats: List<SupportedFormat> = supportedFormats,
    sample: Any? = url,
): SupportedFormat? =
    guessFormatForExtension(url.substringAfterLast("."), formats, sample = sample)

private class NotCloseableStream(val src: InputStream) : InputStream() {
    override fun read(): Int = src.read()

    fun doClose() = src.close()

    override fun reset() = src.reset()
    override fun available() = src.available()
    override fun markSupported() = src.markSupported()
    override fun mark(readlimit: Int) = src.mark(readlimit)
}

internal fun readCodeForGeneration(
    stream: InputStream,
    format: SupportedCodeGenerationFormat? = null,
    formats: List<SupportedCodeGenerationFormat> = supportedFormats.filterIsInstance<SupportedCodeGenerationFormat>(),
): ReadCodeWithConverter {
    if (format != null) return format to format.readCodeForGeneration(stream)
    val input = NotCloseableStream(if (stream.markSupported()) stream else BufferedInputStream(stream))
    try {
        val readLimit = 10000
        input.mark(readLimit)

        formats.sortedBy { it.testOrder }.forEach {
            try {
                input.reset()
                return it to it.readCodeForGeneration(input)
            } catch (e: Exception) {
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
    formats: List<SupportedDataFrameFormat> = supportedFormats.filterIsInstance<SupportedDataFrameFormat>()
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

internal data class ReadCodeWithConverter(
    val format: SupportedCodeGenerationFormat,
    val codeWithConverter: CodeWithConverter,
)

internal infix fun SupportedCodeGenerationFormat.to(codeWithConverter: CodeWithConverter) =
    ReadCodeWithConverter(this, codeWithConverter)

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
