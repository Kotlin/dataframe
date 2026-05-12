package org.jetbrains.kotlinx.dataframe.io

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.util.ServiceLoader
import kotlin.reflect.KType

public sealed interface SupportedFormatSample {

    @JvmInline
    public value class DataFile(public val sampleFile: File) : SupportedFormatSample

    @JvmInline
    public value class DataPath(public val samplePath: Path) : SupportedFormatSample

    @JvmInline
    public value class DataUrl(public val sampleUrl: URL) : SupportedFormatSample

    @JvmInline
    public value class PathString(public val samplePath: String) : SupportedFormatSample

    @JvmInline
    public value class DataString(public val sampleData: String) : SupportedFormatSample
}

/**
 * Implement this interface to provide additional [DataSchema] interface generation formats for DataFrames (such as OpenAPI).
 * Note, this doesn't add functionality to [DataFrame.Companion.read], just [ImportDataSchema] and Gradle plugin.
 *
 * Return type will be a [Code] which contains a generated `interface` with the given name containing generated
 * [DataSchema] interfaces and `enum`s, with `typealias`es (and optional extension functions in some integration)
 * outside the interface.
 */
public interface SupportedCodeGenerationFormat {

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
    public fun readCodeForGeneration(file: File, name: String, generateHelperCompanionObject: Boolean = false): Code

    public fun acceptsExtension(ext: String): Boolean

    public fun acceptsSample(sample: SupportedFormatSample): Boolean

    // `DataFrame.Companion.read` methods uses this to sort list of all supported formats in ascending order (-1, 2, 10)
    // sorted list is used to test if any format can read given input
    public val testOrder: Int

    public fun createDefaultReadMethod(pathRepresentation: String? = null): DefaultReadDfMethod
}

public class MethodArguments {
    internal val defaultValues = mutableListOf<Argument>()

    public fun add(
        name: String,
        type: KType,
        initializerTemplate: String,
        vararg values: Any?,
    ): MethodArguments {
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
internal val supportedFormats: List<SupportedCodeGenerationFormat> by lazy {
    ServiceLoader.load(SupportedCodeGenerationFormat::class.java).toList()
        .distinct()
        .sortedBy { it.testOrder }
}

internal fun guessFormatForExtension(
    ext: String,
    formats: List<SupportedCodeGenerationFormat> = supportedFormats,
    sample: SupportedFormatSample? = null,
): SupportedCodeGenerationFormat? =
    formats.firstOrNull {
        it.acceptsExtension(ext) &&
            (sample == null || it.acceptsSample(sample))
    }

internal fun guessFormat(
    url: URL,
    formats: List<SupportedCodeGenerationFormat> = supportedFormats,
    sample: SupportedFormatSample.DataUrl? = SupportedFormatSample.DataUrl(url),
): SupportedCodeGenerationFormat? = guessFormatForExtension(url.path.substringAfterLast("."), formats, sample = sample)

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

internal data class GeneratedCode(val format: SupportedCodeGenerationFormat, val code: Code)

internal infix fun SupportedCodeGenerationFormat.to(code: Code) = GeneratedCode(this, code)
