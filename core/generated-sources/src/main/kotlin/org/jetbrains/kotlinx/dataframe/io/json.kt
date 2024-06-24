package org.jetbrains.kotlinx.dataframe.io

import com.beust.klaxon.Parser
import com.beust.klaxon.json
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.KeyValueProperty
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadJsonMethod
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.io.encodeDataFrameWithMetadata
import org.jetbrains.kotlinx.dataframe.impl.io.encodeFrame
import org.jetbrains.kotlinx.dataframe.impl.io.encodeRow
import org.jetbrains.kotlinx.dataframe.impl.io.readJson
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
) : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame =
        DataFrame.readJson(
            stream = stream,
            header = header,
            typeClashTactic = typeClashTactic,
            keyValuePaths = keyValuePaths,
        )

    override fun readDataFrame(file: File, header: List<String>): AnyFrame =
        DataFrame.readJson(
            file = file,
            header = header,
            typeClashTactic = typeClashTactic,
            keyValuePaths = keyValuePaths,
        )

    override fun acceptsExtension(ext: String): Boolean = ext == "json"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 10_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadJsonMethod(
            path = pathRepresentation,
            arguments =
                MethodArguments()
                    .add(
                        "keyValuePaths",
                        typeOf<List<JsonPath>>(),
                        "listOf(${keyValuePaths.joinToString {
                            "org.jetbrains.kotlinx.dataframe.api.JsonPath(\"\"\"${it.path}\"\"\")"
                        }})",
                    ).add(
                        "typeClashTactic",
                        typeOf<TypeClashTactic>(),
                        "org.jetbrains.kotlinx.dataframe.io.JSON.TypeClashTactic.${typeClashTactic.name}",
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

public const val ARRAY_COLUMN_NAME: String = "array"
public const val VALUE_COLUMN_NAME: String = "value"

/**
 * @param file Where to fetch the Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the file will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [file].
 */
public fun DataFrame.Companion.readJson(
    file: File,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(file.toURI().toURL(), header, keyValuePaths, typeClashTactic)

/**
 * @param file Where to fetch the Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the file will be read like an object with [header] being the keys.
 * @return [DataRow] from the given [file].
 */
public fun DataRow.Companion.readJson(
    file: File,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(file, header, keyValuePaths, typeClashTactic).single()

/**
 * @param path URL or file path from where to fetch the Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [path].
 */
public fun DataFrame.Companion.readJson(
    path: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(asURL(path), header, keyValuePaths, typeClashTactic)

/**
 * @param path URL or file path from where to fetch the Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @return [DataRow] from the given [path].
 */
public fun DataRow.Companion.readJson(
    path: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(path, header, keyValuePaths, typeClashTactic).single()

/**
 * @param url Where to fetch the Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [url].
 */
public fun DataFrame.Companion.readJson(
    url: URL,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = catchHttpResponse(url) { readJson(it, header, keyValuePaths, typeClashTactic) }

/**
 * @param url Where to fetch the Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, the stream will be read like an object with [header] being the keys.
 * @return [DataRow] from the given [url].
 */
public fun DataRow.Companion.readJson(
    url: URL,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(url, header, keyValuePaths, typeClashTactic).single()

/**
 * @param stream Json as [InputStream] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [stream] will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [stream].
 */
public fun DataFrame.Companion.readJson(
    stream: InputStream,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(Parser.default().parse(stream), header, keyValuePaths, typeClashTactic)

/**
 * @param stream Json as [InputStream] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [stream] will be read like an object with [header] being the keys.
 * @return [DataRow] from the given [stream].
 */
public fun DataRow.Companion.readJson(
    stream: InputStream,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJson(stream, header, keyValuePaths, typeClashTactic).single()

/**
 * @param text Json as [String] to be converted to a [DataFrame].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [text] will be read like an object with [header] being the keys.
 * @return [DataFrame] from the given [text].
 */
public fun DataFrame.Companion.readJsonStr(
    text: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyFrame = readJson(Parser.default().parse(StringBuilder(text)), header, keyValuePaths, typeClashTactic)

/**
 * @param text Json as [String] to be converted to a [DataRow].
 * @param keyValuePaths List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
 *     will be created.
 * @param typeClashTactic How to handle type clashes when reading a JSON file.
 * @param header Optional list of column names. If given, [text] will be read like an object with [header] being the keys.
 * @return [DataRow] from the given [text].
 */
public fun DataRow.Companion.readJsonStr(
    text: String,
    header: List<String> = emptyList(),
    keyValuePaths: List<JsonPath> = emptyList(),
    typeClashTactic: TypeClashTactic = ARRAY_AND_VALUE_COLUMNS,
): AnyRow = DataFrame.readJsonStr(text, header, keyValuePaths, typeClashTactic).single()

public fun AnyFrame.toJson(prettyPrint: Boolean = false, canonical: Boolean = false): String =
    json {
        encodeFrame(this@toJson)
    }.toJsonString(prettyPrint, canonical)

/**
 * Converts the DataFrame to a JSON string representation with additional metadata about serialized data.
 * It is heavily used to implement some integration features in Kotlin Notebook IntellJ IDEA plugin.
 *
 * @param rowLimit The maximum number of top-level dataframe rows to include in the output JSON.
 * @param nestedRowLimit The maximum number of nested frame rows to include in the output JSON.
 * If null, all rows are included.
 * Applied for each frame column recursively
 * @param prettyPrint Specifies whether the output JSON should be formatted with indentation and line breaks.
 * @param canonical Specifies whether the output JSON should be in a canonical form.
 * @param imageEncodingOptions The options for encoding images. The default is null, which indicates that the image is not encoded as Base64.
 *
 * @return The DataFrame converted to a JSON string with metadata.
 */
public fun AnyFrame.toJsonWithMetadata(
    rowLimit: Int,
    nestedRowLimit: Int? = null,
    prettyPrint: Boolean = false,
    canonical: Boolean = false,
    imageEncodingOptions: Base64ImageEncodingOptions? = null,
): String =
    json {
        encodeDataFrameWithMetadata(this@toJsonWithMetadata, rowLimit, nestedRowLimit, imageEncodingOptions)
    }.toJsonString(prettyPrint, canonical)

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

public fun AnyRow.toJson(prettyPrint: Boolean = false, canonical: Boolean = false): String =
    json {
        encodeRow(df(), index())
    }?.toJsonString(prettyPrint, canonical) ?: ""

public fun AnyFrame.writeJson(file: File, prettyPrint: Boolean = false, canonical: Boolean = false) {
    file.writeText(toJson(prettyPrint, canonical))
}

public fun AnyFrame.writeJson(path: String, prettyPrint: Boolean = false, canonical: Boolean = false): Unit =
    writeJson(File(path), prettyPrint, canonical)

public fun AnyFrame.writeJson(writer: Appendable, prettyPrint: Boolean = false, canonical: Boolean = false) {
    writer.append(toJson(prettyPrint, canonical))
}

public fun AnyRow.writeJson(file: File, prettyPrint: Boolean = false, canonical: Boolean = false) {
    file.writeText(toJson(prettyPrint, canonical))
}

public fun AnyRow.writeJson(path: String, prettyPrint: Boolean = false, canonical: Boolean = false) {
    writeJson(File(path), prettyPrint, canonical)
}

public fun AnyRow.writeJson(writer: Appendable, prettyPrint: Boolean = false, canonical: Boolean = false) {
    writer.append(toJson(prettyPrint, canonical))
}
