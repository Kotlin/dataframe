package org.jetbrains.kotlinx.dataframe.annotations

import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.api.NameValueProperty
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.io.SchemaReader

/**
 * Annotation preprocessing will generate a DataSchema interface from the data at `path`.
 * Data must be of supported format: CSV, JSON, Apache Arrow, Excel, OpenAPI (Swagger) in YAML/JSON, JDBC.
 * Generated data schema has properties inferred from data and a companion object with `read method`.
 * `read method` is either `readCSV` or `readJson` that returns `DataFrame<name>`
 *
 * @param name name of the generated interface
 * @param path URL or relative path to data.
 * If a path starts with protocol (http, https, ftp, jdbc), it's considered a URL.
 * Otherwise, it's treated as a relative path.
 * By default, it will be resolved relatively to project dir, i.e. File(projectDir, path)
 * You can configure it by passing `dataframe.resolutionDir` option to preprocessor,
 * see https://kotlinlang.org/docs/ksp-quickstart.html#pass-options-to-processors
 * @param visibility visibility of the generated interface.
 * @param normalizationDelimiters if not empty, split property names by delimiters,
 * lowercase parts and join to camel case. Set empty list to disable normalization
 * @param withDefaultPath if `true`, generate `defaultPath` property to the data schema's companion object and make it default argument for a `read method`
 * @param csvOptions options to parse CSV data. Not used when data is not Csv
 * @param jsonOptions options to parse JSON data. Not used when data is not Json
 * @param jdbcOptions options to parse data from a database via JDBC. Not used when data is not stored in the database
 * @param enableExperimentalOpenApi Can be set to `true` to enable experimental OpenAPI 3.0.0 types support
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
    val jdbcOptions: JdbcOptions = JdbcOptions(),
    val enableExperimentalOpenApi: Boolean = false,
)

@Target(AnnotationTarget.CLASS)
public annotation class DataSchemaSource(val source: String, val qualifier: String = SchemaReader.DEFAULT_QUALIFIER)

public enum class DataSchemaVisibility {
    INTERNAL,
    IMPLICIT_PUBLIC,
    EXPLICIT_PUBLIC,
}

// TODO add more options
public annotation class CsvOptions(public val delimiter: Char)

/**
 * An annotation class that represents options for JDBC connection.
 *
 * @property [user] The username for the JDBC connection. Default value is an empty string.
 * If [extractCredFromEnv] is true, the [user] value will be interpreted as key for system environment variable.
 * @property [password] The password for the JDBC connection. Default value is an empty string.
 * If [extractCredFromEnv] is true, the [password] value will be interpreted as key for system environment variable.
 * @property [extractCredFromEnv] Whether to extract the JDBC credentials from environment variables. Default value is false.
 * @property [tableName] The name of the table for the JDBC connection. Default value is an empty string.
 * @property [sqlQuery] The SQL query to be executed in the JDBC connection. Default value is an empty string.
 */
public annotation class JdbcOptions(
    public val user: String = "",
    public val password: String = "",
    public val extractCredFromEnv: Boolean = false,
    public val tableName: String = "",
    public val sqlQuery: String = "",
)

public annotation class JsonOptions(
    /**
     * Allows the choice of how to handle type clashes when reading a JSON file.
     * Must be either [JsonOptions.TypeClashTactics.ARRAY_AND_VALUE_COLUMNS] or [JsonOptions.TypeClashTactics.ANY_COLUMNS]
     * */
    public val typeClashTactic: String = TypeClashTactics.ARRAY_AND_VALUE_COLUMNS,
    /**
     * List of [JsonPath]s where instead of a [ColumnGroup], a [FrameColumn]<[NameValueProperty]>
     *     will be created.
     *
     * Example:
     * `["""\$["store"]["book"][*]["author"]"""]`
     */
    public val keyValuePaths: Array<String> = [],
    /** Whether to [unify the numbers that are read][UnifyingNumbers]. `true` by default. */
    public val unifyNumbers: Boolean = true,
) {
    public object TypeClashTactics {
        public const val ARRAY_AND_VALUE_COLUMNS: String = "ARRAY_AND_VALUE_COLUMNS"
        public const val ANY_COLUMNS: String = "ANY_COLUMNS"
    }
}
