package org.jetbrains.kotlinx.dataframe.annotations

/**
 * Annotation preprocessing will generate a DataSchema interface from the data at `path`.
 * Data must be of supported format: CSV, JSON, Apache Arrow, Excel, OpenAPI (Swagger) in YAML/JSON.
 * Generated data schema has properties inferred from data and a companion object with `read method`.
 * `read method` is either `readCSV` or `readJson` that returns `DataFrame<name>`
 *
 * @param name name of the generated interface, irrelevant for OpenAPI
 * @param path URL or relative path to data.
 * if path starts with protocol (http, https, ftp), it's considered a URL. Otherwise, it's treated as relative path.
 * By default, it will be resolved relatively to project dir, i.e. File(projectDir, path)
 * You can configure it by passing `dataframe.resolutionDir` option to preprocessor, see https://kotlinlang.org/docs/ksp-quickstart.html#pass-options-to-processors
 * @param visibility visibility of the generated interface.
 * @param normalizationDelimiters if not empty, split property names by delimiters,
 * lowercase parts and join to camel case. Set empty list to disable normalization
 * @param withDefaultPath if `true`, generate `defaultPath` property to the data schema's companion object and make it default argument for a `read method`
 * @param csvOptions options to parse CSV data. Not used when data is JSON
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE)
@Repeatable
public annotation class ImportDataSchema(
    val name: String = "DataSchema",
    val path: String,
    val visibility: DataSchemaVisibility = DataSchemaVisibility.IMPLICIT_PUBLIC,
    val normalizationDelimiters: CharArray = ['\t', ' ', '_'],
    val withDefaultPath: Boolean = true,
    val csvOptions: CsvOptions = CsvOptions(',')
)

public enum class DataSchemaVisibility {
    INTERNAL, IMPLICIT_PUBLIC, EXPLICIT_PUBLIC
}

public annotation class CsvOptions(
    val delimiter: Char
)
