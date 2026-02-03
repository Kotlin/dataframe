@file:Suppress("unused")

package org.jetbrains.dataframe.gradle

import groovy.lang.Closure
import org.gradle.api.Project
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.io.JSON
import java.io.File
import java.io.Serializable
import java.net.URL

public open class SchemaGeneratorExtension {
    public lateinit var project: Project
    internal val schemas: MutableList<Schema> = mutableListOf()
    public var packageName: String? = null
    public var sourceSet: String? = null
    public var visibility: DataSchemaVisibility? = null
    internal var defaultPath: Boolean? = null
    internal var withNormalizationBy: Set<Char>? = null

    /** Can be set to `true` to enable experimental OpenAPI 3.0.0 types support */
    public var enableExperimentalOpenApi: Boolean = false

    public fun schema(config: Schema.() -> Unit) {
        val schema = Schema(project).apply(config)
        schemas.add(schema)
    }

    public fun schema(config: Closure<*>) {
        val schema = Schema(project)
        project.configure(schema, config)
        schemas.add(schema)
    }

    public fun withoutDefaultPath() {
        defaultPath = false
    }

    public fun withNormalizationBy(vararg delimiter: Char) {
        withNormalizationBy = delimiter.toSet()
    }

    // Overload for Groovy.
    // It's impossible to call a method with char argument without type cast in groovy, because it only has string literals
    public fun withNormalizationBy(delimiter: String) {
        withNormalizationBy = delimiter.toSet()
    }

    public fun withoutNormalization() {
        withNormalizationBy = emptySet()
    }

    public fun enableExperimentalOpenApi(enable: Boolean) {
        enableExperimentalOpenApi = enable
    }
}

public class Schema(
    private val project: Project,
    public var data: Any? = null,
    public var src: File? = null,
    public var name: String? = null,
    public var packageName: String? = null,
    public var sourceSet: String? = null,
    public var visibility: DataSchemaVisibility? = null,
    internal var defaultPath: Boolean? = null,
    internal var withNormalizationBy: Set<Char>? = null,
    public val csvOptions: CsvOptionsDsl = CsvOptionsDsl(),
    public val jsonOptions: JsonOptionsDsl = JsonOptionsDsl(),
    public val jdbcOptions: JdbcOptionsDsl = JdbcOptionsDsl(),
) {
    public fun setData(file: File) {
        data = file
    }

    public fun setData(path: String) {
        data = path
    }

    public fun setData(url: URL) {
        data = url
    }

    public fun csvOptions(config: CsvOptionsDsl.() -> Unit) {
        csvOptions.apply(config)
    }

    public fun csvOptions(config: Closure<*>) {
        project.configure(csvOptions, config)
    }

    public fun jsonOptions(config: JsonOptionsDsl.() -> Unit) {
        jsonOptions.apply(config)
    }

    public fun jsonOptions(config: Closure<*>) {
        project.configure(jsonOptions, config)
    }

    public fun jdbcOptions(config: JdbcOptionsDsl.() -> Unit) {
        jdbcOptions.apply(config)
    }

    public fun jdbcOptions(config: Closure<*>) {
        project.configure(jdbcOptions, config)
    }

    public fun withoutDefaultPath() {
        defaultPath = false
    }

    public fun withDefaultPath() {
        defaultPath = true
    }

    public fun withNormalizationBy(vararg delimiter: Char) {
        withNormalizationBy = delimiter.toSet()
    }

    // Overload for Groovy.
    // It's impossible to call a method with char argument without type cast in groovy, because it only has string literals
    public fun withNormalizationBy(delimiter: String) {
        withNormalizationBy = delimiter.toSet()
    }

    public fun withoutNormalization() {
        withNormalizationBy = emptySet()
    }
}

// Without Serializable GradleRunner tests fail
// TODO add more options
public data class CsvOptionsDsl(var delimiter: Char = ',') : Serializable

public data class JsonOptionsDsl(
    var typeClashTactic: JSON.TypeClashTactic = JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS,
    var keyValuePaths: List<JsonPath> = emptyList(),
    var unifyNumbers: Boolean = true,
) : Serializable

/**
 * Represents the configuration options for JDBC data source.
 *
 * @property [user] The username used to authenticate with the database. Default is an empty string.
 * @property [password] The password used to authenticate with the database. Default is an empty string.
 * @property [tableName] The name of the table to generate schema for. Default is an empty string.
 * @property [sqlQuery] The SQL query used to generate schema. Default is an empty string.
 */
public data class JdbcOptionsDsl(
    var user: String = "",
    var password: String = "",
    var tableName: String = "",
    var sqlQuery: String = "",
) : Serializable
