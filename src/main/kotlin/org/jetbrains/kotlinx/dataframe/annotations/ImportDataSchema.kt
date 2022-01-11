package org.jetbrains.kotlinx.dataframe.annotations

/**
 * Annotation preprocessing will generate a DataSchema interface from the CSV or JSON at `url`.
 * Generated data schema has properties inferred from data and a companion object with `read method`.
 * `read method` is either `readCSV` or `readJson` that returns `DataFrame<name>`
 *
 * @param name name of the generated interface
 * @param url annotation preprocessor read data from this url
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
    val name: String,
    val url: String,
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

/**
 * Annotation preprocessing will generate a DataSchema interface from the CSV or JSON at `absolutePath`.
 * Generated data schema has properties inferred from data and a companion object with `read method`.
 * `read method` is either `readCSV` or `readJson` that returns `DataFrame<name>`
 *
 * @param name name of the generated interface
 * @param absolutePath annotation preprocessor read data from this file
 * @param visibility visibility of the generated interface.
 * @param normalizationDelimiters if not empty, split property names by delimiters,
 * lowercase parts and join to camel case. Set empty list to disable normalization
 * @param withDefaultPath if `true`, generate `defaultPath` property to the data schema's companion object and make it default argument for a `read method`
 * @param csvOptions options to parse CSV data. Not used when data is JSON
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE)
@Repeatable
public annotation class ImportDataSchemaByAbsolutePath(
    val name: String,
    val absolutePath: String,
    val visibility: DataSchemaVisibility = DataSchemaVisibility.IMPLICIT_PUBLIC,
    val normalizationDelimiters: CharArray = ['\t', ' ', '_'],
    val withDefaultPath: Boolean = true,
    val csvOptions: CsvOptions = CsvOptions(',')
)
