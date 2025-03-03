@file:Suppress("unused")

package org.jetbrains.dataframe.gradle

import groovy.lang.Closure
import org.gradle.api.Project
import org.jetbrains.kotlinx.dataframe.api.JsonPath
import org.jetbrains.kotlinx.dataframe.io.JSON
import java.io.File
import java.io.Serializable
import java.net.URL

open class SchemaGeneratorExtension {
    lateinit var project: Project
    internal val schemas: MutableList<Schema> = mutableListOf()
    var packageName: String? = null
    var sourceSet: String? = null
    var visibility: DataSchemaVisibility? = null
    internal var defaultPath: Boolean? = null
    internal var withNormalizationBy: Set<Char>? = null

    fun schema(config: Schema.() -> Unit) {
        val schema = Schema(project).apply(config)
        schemas.add(schema)
    }

    fun schema(config: Closure<*>) {
        val schema = Schema(project)
        project.configure(schema, config)
        schemas.add(schema)
    }

    fun withoutDefaultPath() {
        defaultPath = false
    }

    fun withNormalizationBy(vararg delimiter: Char) {
        withNormalizationBy = delimiter.toSet()
    }

    // Overload for Groovy.
    // It's impossible to call a method with char argument without type cast in groovy, because it only has string literals
    fun withNormalizationBy(delimiter: String) {
        withNormalizationBy = delimiter.toSet()
    }

    fun withoutNormalization() {
        withNormalizationBy = emptySet()
    }
}

class Schema(
    private val project: Project,
    var data: Any? = null,
    var src: File? = null,
    var name: String? = null,
    var packageName: String? = null,
    var sourceSet: String? = null,
    var visibility: DataSchemaVisibility? = null,
    internal var defaultPath: Boolean? = null,
    internal var withNormalizationBy: Set<Char>? = null,
    val csvOptions: CsvOptionsDsl = CsvOptionsDsl(),
    val jsonOptions: JsonOptionsDsl = JsonOptionsDsl(),
    val jdbcOptions: JdbcOptionsDsl = JdbcOptionsDsl(),
) {
    fun setData(file: File) {
        data = file
    }

    fun setData(path: String) {
        data = path
    }

    fun setData(url: URL) {
        data = url
    }

    fun csvOptions(config: CsvOptionsDsl.() -> Unit) {
        csvOptions.apply(config)
    }

    fun csvOptions(config: Closure<*>) {
        project.configure(csvOptions, config)
    }

    fun jsonOptions(config: JsonOptionsDsl.() -> Unit) {
        jsonOptions.apply(config)
    }

    fun jsonOptions(config: Closure<*>) {
        project.configure(jsonOptions, config)
    }

    fun jdbcOptions(config: JdbcOptionsDsl.() -> Unit) {
        jdbcOptions.apply(config)
    }

    fun jdbcOptions(config: Closure<*>) {
        project.configure(jdbcOptions, config)
    }

    fun withoutDefaultPath() {
        defaultPath = false
    }

    fun withDefaultPath() {
        defaultPath = true
    }

    fun withNormalizationBy(vararg delimiter: Char) {
        withNormalizationBy = delimiter.toSet()
    }

    // Overload for Groovy.
    // It's impossible to call a method with char argument without type cast in groovy, because it only has string literals
    fun withNormalizationBy(delimiter: String) {
        withNormalizationBy = delimiter.toSet()
    }

    fun withoutNormalization() {
        withNormalizationBy = emptySet()
    }
}

// Without Serializable GradleRunner tests fail
// TODO add more options
data class CsvOptionsDsl(var delimiter: Char = ',') : Serializable

data class JsonOptionsDsl(
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
data class JdbcOptionsDsl(
    var user: String = "",
    var password: String = "",
    var tableName: String = "",
    var sqlQuery: String = "",
) : Serializable
