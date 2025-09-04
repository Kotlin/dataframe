package org.jetbrains.kotlinx.dataframe.io

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromStream
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.KeyValueProperty
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.io.encodeDataFrameWithMetadata
import org.jetbrains.kotlinx.dataframe.impl.io.encodeFrame
import org.jetbrains.kotlinx.dataframe.impl.io.encodeRow
import org.jetbrains.kotlinx.dataframe.impl.io.readJsonImpl
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ANY_COLUMNS
import org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.reflect.typeOf

public class JSON(
    private val typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    private val keyValuePaths: List<JsonPath> = emptyList(),
    private val unifyNumbers: Boolean = true,
) : SupportedDataFrameFormat {

    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame =
        DataFrame.readJson(
            stream = stream,
            header = header,
            typeClashTactic = typeClashTactic,
            keyValuePaths = keyValuePaths,
            unifyNumbers = unifyNumbers,
        )

    override fun readDataFrame(file: File, header: List<String>): AnyFrame =
        DataFrame.readJson(
            file = file,
            header = header,
            typeClashTactic = typeClashTactic,
            keyValuePaths = keyValuePaths,
            unifyNumbers = unifyNumbers,
        )

    override fun acceptsExtension(ext: String): Boolean = ext == "json"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 10_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadJsonMethod(
            path = pathRepresentation,
            arguments = MethodArguments()
                .add(
                    "keyValuePaths",
                    typeOf<List<JsonPath>>(),
                    "listOf(${
                        keyValuePaths.joinToString {
                            "org.jetbrains.kotlinx.dataframe.api.JsonPath(\"\"\"${it.path}\"\"\")"
                        }
                    })",
                )
                .add(
                    "typeClashTactic",
                    typeOf<TypeClashTactic>(),
                    "org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.${typeClashTactic.name}",
                )
                .add(
                    "unifyNumbers",
                    typeOf<Boolean>(),
                    unifyNumbers.toString(),
                ),
        )

    /**
     * Allows the choice of how to handle type clashes when reading a JSON file.
     * Such as:
     * ```json
     * [
     *     { "a": "text" },
     *     { "a": { "b": 2 } },
     *     { "a": [6, 7, 8] }
     *  ]
     * ```
     *
     * [ARRAY_AND_VALUE_COLUMNS] (default) will create a [DataFrame] looking like (including `null` and `[]` values):
     * ```
     * ⌌----------------------------------------------⌍
     * |  | a:{b:Int?, value:String?, array:List<Int>}|
     * |--|-------------------------------------------|
     * | 0|         { b:null, value:"text", array:[] }|
     * | 1|              { b:2, value:null, array:[] }|
     * | 2|    { b:null, value:null, array:[6, 7, 8] }|
     * ⌎----------------------------------------------⌏
     * ```
     * So, for the type clashing argument it will create a [ColumnGroup] with the properties `value`, `array`,
     * and the unwrapped properties of the objects the property can be.
     *
     * [ANY_COLUMNS] will create a [DataFrame] looking like:
     * ```
     * ⌌-------------⌍
     * |  |     a:Any|
     * |--|----------|
     * | 0|    "text"|
     * | 1|   { b:2 }|
     * | 2| [6, 7, 8]|
     * ⌎-------------⌏
     * ```
     */
    public enum class TypeClashTactic {
        ARRAY_AND_VALUE_COLUMNS,
        ANY_COLUMNS,
    }
}

public class JsonSchemaReader : SchemaReader {
    override fun accepts(path: String, qualifier: String): Boolean =
        qualifier == SchemaReader.DEFAULT_QUALIFIER && path.endsWith(".json")

    override fun read(path: String): DataFrame<*> = DataFrame.readJson(path)
}

internal const val ARRAY_COLUMN_NAME: String = "array"
internal const val VALUE_COLUMN_NAME: String = "value"

/**
 * @param file Where to fetch the Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the file will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataFrame] from the given [file].
 */
public fun DataFrame.Companion.readJson(
    file: File,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyFrame = DataFrame.readJson(file.toURI().toURL(), header, keyValuePaths, typeClashTactic, unifyNumbers)

/**
 * @param file Where to fetch the Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the file will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataRow] from the given [file].
 */
public fun DataRow.Companion.readJson(
    file: File,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyRow = DataFrame.readJson(file, header, keyValuePaths, typeClashTactic, unifyNumbers).single()

/**
 * @param path URL or file path from where to fetch the Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataFrame] from the given [path].
 */
public fun DataFrame.Companion.readJson(
    path: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyFrame = DataFrame.readJson(asUrl(path), header, keyValuePaths, typeClashTactic, unifyNumbers)

/**
 * @param path URL or file path from where to fetch the Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataRow] from the given [path].
 */
public fun DataRow.Companion.readJson(
    path: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyRow = DataFrame.readJson(path, header, keyValuePaths, typeClashTactic, unifyNumbers).single()

/**
 * @param url Where to fetch the Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataFrame] from the given [url].
 */
public fun DataFrame.Companion.readJson(
    url: URL,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyFrame = catchHttpResponse(url) { DataFrame.readJson(it, header, keyValuePaths, typeClashTactic, unifyNumbers) }

/**
 * @param url Where to fetch the Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataRow] from the given [url].
 */
public fun DataRow.Companion.readJson(
    url: URL,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyRow = DataFrame.readJson(url, header, keyValuePaths, typeClashTactic, unifyNumbers).single()

/**
 * @param stream Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [stream] will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataFrame] from the given [stream].
 */
@OptIn(ExperimentalSerializationApi::class)
public fun DataFrame.Companion.readJson(
    stream: InputStream,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyFrame =
    readJsonImpl(Json.decodeFromStream<JsonElement>(stream), unifyNumbers, header, keyValuePaths, typeClashTactic)

/**
 * @param stream Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [stream] will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataRow] from the given [stream].
 */
public fun DataRow.Companion.readJson(
    stream: InputStream,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyRow = DataFrame.readJson(stream, header, keyValuePaths, typeClashTactic, unifyNumbers).single()

/**
 * @param text Json as [String] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [text] will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataFrame] from the given [text].
 */
public fun DataFrame.Companion.readJsonStr(
    @Language("json") text: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyFrame = readJsonImpl(Json.parseToJsonElement(text), unifyNumbers, header, keyValuePaths, typeClashTactic)

/**
 * @param text Json as [String] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [text] will be read like an object with [header] being the keys.
 * @param unifyNumbers Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default.
 * @return [DataRow] from the given [text].
 */
public fun DataRow.Companion.readJsonStr(
    @Language("json") text: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
    unifyNumbers: Boolean = true,
): AnyRow = DataFrame.readJsonStr(text, header, keyValuePaths, typeClashTactic, unifyNumbers).single()

public fun AnyFrame.toJson(prettyPrint: Boolean = false): String {
    val json = Json {
        this.prettyPrint = prettyPrint
        isLenient = true
        allowSpecialFloatingPointValues = true
    }
    return json.encodeToString(JsonElement.serializer(), encodeFrame(this@toJson))
}

/**
 * Converts the DataFrame to a JSON string representation with additional metadata about serialized data.
 * It is heavily used to implement some integration features in Kotlin Notebook IntelliJ IDEA plugin.
 *
 * @param rowLimit The maximum number of top-level dataframe rows to include in the output JSON.
 * @param nestedRowLimit The maximum number of nested frame rows to include in the output JSON.
 * If null, all rows are included.
 * Applied for each frame column recursively
 * @param prettyPrint Specifies whether the output JSON should be formatted with indentation and line breaks.
 * @param customEncoders The options for encoding things like images.
 *   The default is empty list, which indicates that the image is not encoded as Base64.
 * @param isFormatted Specifies whether the DataFrame should be formatted,
 *   a.k.a. it comes from [FormattedFrame.df] or it contains a
 *   [DataColumn][DataColumn]`<`[FormattedFrame][FormattedFrame]`<*>>` at any depth.
 *   This is just a marker; formatting is applied by the renderer. Defaults to `false`.
 *
 * @return The DataFrame converted to a JSON string with metadata.
 */
public fun AnyFrame.toJsonWithMetadata(
    rowLimit: Int,
    nestedRowLimit: Int? = null,
    prettyPrint: Boolean = false,
    customEncoders: List<CustomEncoder> = emptyList(),
    isFormatted: Boolean = false,
): String {
    val json = Json {
        this.prettyPrint = prettyPrint
        isLenient = true
        allowSpecialFloatingPointValues = true
    }
    return json.encodeToString(
        JsonElement.serializer(),
        encodeDataFrameWithMetadata(
            frame = this@toJsonWithMetadata,
            rowLimit = rowLimit,
            nestedRowLimit = nestedRowLimit,
            customEncoders = customEncoders,
            isFormatted = isFormatted,
        ),
    )
}

/**
 * Interface for defining a custom encoder. That applied to the value during dataframe JSON serialization
 */
public interface CustomEncoder {
    /**
     * Determines whether this encoder can encode the given input.
     *
     * @param input The input object to be checked for suitability.
     * @return `true` if the input can be encoded, otherwise `false`.
     */
    public fun canEncode(input: Any?): Boolean

    /**
     * Encodes the provided input into a JSON element.
     *
     * @param input The input object to be encoded.
     * @return A JsonElement representing the encoded input.
     */
    public fun encode(input: Any?): JsonElement
}

internal const val DEFAULT_IMG_SIZE = 600

/**
 * Class representing the options for encoding images.
 *
 * @property imageSizeLimit The maximum size to which images should be resized. Defaults to the value of DEFAULT_IMG_SIZE.
 * @property options Bitwise-OR of the [GZIP_ON] and [LIMIT_SIZE_ON] constants. Defaults to [GZIP_ON] or [LIMIT_SIZE_ON].
 */
public class Base64ImageEncodingOptions(
    public val imageSizeLimit: Int = DEFAULT_IMG_SIZE,
    private val options: Int = GZIP_ON or LIMIT_SIZE_ON,
) {
    public val isGzipOn: Boolean
        get() = options and GZIP_ON == GZIP_ON

    public val isLimitSizeOn: Boolean
        get() = options and LIMIT_SIZE_ON == LIMIT_SIZE_ON

    public companion object {
        public const val ALL_OFF: Int = 0
        public const val GZIP_ON: Int = 1 // 2^0
        public const val LIMIT_SIZE_ON: Int = 2 // 2^1
    }
}

public fun AnyRow.toJson(prettyPrint: Boolean = false): String {
    val json = Json {
        this.prettyPrint = prettyPrint
        isLenient = true
        allowSpecialFloatingPointValues = true
    }
    return json.encodeToString(JsonElement.serializer(), encodeRow(df(), index()))
}

public fun AnyFrame.writeJson(file: File, prettyPrint: Boolean = false) {
    file.writeText(toJson(prettyPrint))
}

public fun AnyFrame.writeJson(path: String, prettyPrint: Boolean = false): Unit = writeJson(File(path), prettyPrint)

public fun AnyFrame.writeJson(writer: Appendable, prettyPrint: Boolean = false) {
    writer.append(toJson(prettyPrint))
}

public fun AnyRow.writeJson(file: File, prettyPrint: Boolean = false) {
    file.writeText(toJson(prettyPrint))
}

public fun AnyRow.writeJson(path: String, prettyPrint: Boolean = false) {
    writeJson(File(path), prettyPrint)
}

public fun AnyRow.writeJson(writer: Appendable, prettyPrint: Boolean = false) {
    writer.append(toJson(prettyPrint))
}

private const val READ_JSON = "readJson"

internal class DefaultReadJsonMethod(path: String?, arguments: MethodArguments) :
    AbstractDefaultReadMethod(
        path = path,
        arguments = arguments,
        methodName = READ_JSON,
    )
