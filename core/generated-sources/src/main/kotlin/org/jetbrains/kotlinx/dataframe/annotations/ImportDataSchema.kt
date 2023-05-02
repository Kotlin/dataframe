package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.KeyValueProperty
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.io.JSON

/**
 * Annotation preprocessing will generate a DataSchema interface from the data at `path`.
 * Data must be of supported format: CSV, JSON, Apache Arrow, Excel, OpenAPI (Swagger) in YAML/JSON.
 * Generated data schema has properties inferred from data and a companion object with `read method`.
 * `read method` is either `readCSV` or `readJson` that returns `DataFrame<name>`
 *
 * @param name name of the generated interface
 * @param path URL or relative path to data.
 * if path starts with protocol (http, https, ftp), it's considered a URL. Otherwise, it's treated as relative path.
 * By default, it will be resolved relatively to project dir, i.e. File(projectDir, path)
 * You can configure it by passing `dataframe.resolutionDir` option to preprocessor, see https://kotlinlang.org/docs/ksp-quickstart.html#pass-options-to-processors
 * @param visibility visibility of the generated interface.
 * @param normalizationDelimiters if not empty, split property names by delimiters,
 * lowercase parts and join to camel case. Set empty list to disable normalization
 * @param withDefaultPath if `true`, generate `defaultPath` property to the data schema's companion object and make it default argument for a `read method`
 * @param csvOptions options to parse CSV data. Not used when data is not Csv
 * @param jsonOptions options to parse JSON data. Not used when data is not Json
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE)
@Repeatable
public annotation class ImportDataSchema(
    val name: String,
    val path: String,
    val visibility: DataSchemaVisibility = DataSchemaVisibility.IMPLICIT_PUBLIC,
    val normalizationDelimiters: CharArray = ['\t', ' ', '_'],
    val withDefaultPath: Boolean = true,
    val csvOptions: CsvOptions = CsvOptions(','),
    val jsonOptions: JsonOptions = JsonOptions(),
)

public enum class DataSchemaVisibility {
    INTERNAL, IMPLICIT_PUBLIC, EXPLICIT_PUBLIC
}

public annotation class CsvOptions(
    public val delimiter: Char,
)

public annotation class JsonOptions(

    /** Allows the choice of how to handle type clashes when reading a JSON file. */
    public val typeClashTactic: JSON.TypeClashTactic = JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS,

    /**
     * List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[KeyValueProperty]>
     *     will be created.
     *
     * Example:
     * `["""$["store"]["book"][*]["author"]"""]`
     */
    public val keyValuePaths: Array<String> = [],
)
